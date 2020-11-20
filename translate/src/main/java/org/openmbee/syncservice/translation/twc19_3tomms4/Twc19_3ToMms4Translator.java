package org.openmbee.syncservice.translation.twc19_3tomms4;

import org.json.JSONObject;
import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.branches.BranchCreateRequest;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Parser;
import org.openmbee.syncservice.core.syntax.fields.CommonFields;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.translation.Translator;
import org.openmbee.syncservice.core.utils.JSONUtils;
import org.openmbee.syncservice.mms.mms4.MmsSyntax;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.openmbee.syncservice.twc.sourcesink.TeamworkCloudSource;
import org.openmbee.syncservice.twc.syntax.TwcSyntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Twc19_3ToMms4Translator implements Translator<TwcSyntax, MmsSyntax> {

    private static final Logger logger = LoggerFactory.getLogger(Twc19_3ToMms4Translator.class);

    private JSONUtils jsonUtils;

    public JSONUtils getJsonUtils() {
        return jsonUtils;
    }

    @Autowired
    public void setJsonUtils(JSONUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    @Override
    public TwcSyntax getSourceSyntax() {
        return TwcSyntax.TWC_19_0_3;
    }

    @Override
    public MmsSyntax getSinkSyntax() {
        return MmsSyntax.MMS4;
    }

    @Override
    public Branch translateBranch(Branch refs) {
        //TODO
        return null;
    }

    @Override
    public String translateBranchName(String branchName) {
        if("trunk".equals(branchName)) {
            return "master";
        }
        return branchName;
    }

    @Override
    public String translateBranchId(String branchName, String branchId) {
        if("trunk".equals(branchName)) {
            return "master";
        }
        return sanitizeBranchId(branchId);
    }

    private String sanitizeBranchId(String branchId) {
        //TODO handle collisions?
        return branchId.toLowerCase().replaceAll("[^\\w-]", "_");
    }

    @Override
    public BranchCreateRequest translateBranchCreateRequest(BranchCreateRequest request) {
        BranchCreateRequest translatedRequest = new BranchCreateRequest();
        translatedRequest.setBranchName(translateBranchName(request.getBranchName()));
        translatedRequest.setBranchId(translateBranchId(request.getBranchName(), request.getBranchId()));
        translatedRequest.setParentBranchName(request.getParentBranchName());
        translatedRequest.setParentBranchId(request.getParentBranchId());
        return translatedRequest;
    }

    @Override
    public CommitChanges translateCommitChanges(Source source, CommitChanges commitChanges) {
        TranslationContext context = new TranslationContext(this, source instanceof TeamworkCloudSource
                ? (TeamworkCloudSource)source : null, commitChanges);
        CommitChanges translatedCommitChanges = new CommitChanges();
        translatedCommitChanges.setCommit(translateCommit(commitChanges.getCommit()));
        translatedCommitChanges.setCommitJson(commitChanges.getCommitJson());
        translatedCommitChanges.setAddedElements(translateElements(context, commitChanges.getAddedElements()));
        translatedCommitChanges.setUpdatedElements(translateElementUpdates(context, commitChanges.getUpdatedElements()));
        translatedCommitChanges.setDeletedElementIds(translateElementIds(context, commitChanges.getDeletedElementIds(), true));

        return translatedCommitChanges;
    }

    private Commit translateCommit(Commit commit) {
        Commit translatedCommit = new Commit();
        translatedCommit.setCommitDate(commit.getCommitDate());
        translatedCommit.setBranchName(translateBranchName(commit.getBranchName()));
        translatedCommit.setBranchId(translatedCommit.getBranchName());
        return translatedCommit;
    }

    private Collection<String> translateElementIds(TranslationContext context, Collection<String> esiIds, boolean forDelete) {
        Map<String, JSONObject> elements = context.getElements(forDelete, esiIds);
        Parser sourceParser = getSourceSyntax().getParser();
        Field<CommonFields, String> idField = sourceParser.getFields().getField(CommonFields.ID, String.class);

        return elements.values().parallelStream().map(v -> sourceParser.getFieldFromElement(idField, v)).collect(Collectors.toList());
    }

    private Collection<JSONObject> translateElementUpdates(TranslationContext context, Collection<JSONObject> updatedElements) {
        List<JSONObject> translatedElements = new ArrayList<>(translateElements(context, updatedElements));
        //Need to detect updates to slot values where the slot itself wasn't affected (add and delete should affect the original slot)
        Set<String> extraSlotIds = new HashSet<>();
        Set<String> valueTypes = SlotBuilder.getSlotValueTypes();
        Field<SysMLv1X, String> typeField = getSourceSyntax().getParser().getFields().getField(SysMLv1X.ELEMENT_TYPE, String.class);
        Field<SysMLv1X, String> ownerField = getSourceSyntax().getParser().getFields().getField(SysMLv1X.OWNER_ID, String.class);
        for (JSONObject element : updatedElements) {
            String type = typeField.get(element);
            if(valueTypes.contains(type)) {
                String slotId = ownerField.get(element);
                if(! context.isInOriginalContext(slotId)) {
                    //This slot changed, but it wasn't listed in the commit, so we need to go ahead and add it to the commit
                    extraSlotIds.add(slotId);
                }
            }
        }
        //process the extra slots before returning
        if(!extraSlotIds.isEmpty()) {
            logger.debug("Adding updates to " + extraSlotIds.size() + " slots");
            translatedElements.addAll(translateElements(context, context.getElements(extraSlotIds).values()));
        }
        return translatedElements;
    }

    private Collection<JSONObject> translateElements(TranslationContext context, Collection<JSONObject> elements) {
        if(elements == null) {
            return Collections.emptyList();
        }
        Parser sourceParser = getSourceSyntax().getParser();

        return elements.parallelStream().map(e -> {
            try {
                Mms4ElementBuilder elementBuilder = getElementBuilder(context, sourceParser, e);
                if(elementBuilder == null) {
                    return null;
                }
                return elementBuilder.buildElementFrom(e);
            } catch(Exception ex) {
                logger.warn("Could not translate object: " + ex.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Mms4ElementBuilder getElementBuilder(TranslationContext context, Parser parser, JSONObject e) {
        String type = parser.getFieldFromElement(SysMLv1X.ELEMENT_TYPE,e, String.class);
        switch(type) {
            case "uml:Package": return new PackageBuilder(this, context);
            case "uml:Class": return new ClassBuilder(this, context);
            case "uml:InstanceSpecification": return new InstanceSpecificationBuilder(this, context);
            case "uml:Slot": return new SlotBuilder(this, context);
            default:
                //Ignore slot value elements - these will be incorporated into the Slot elements themselves
                if(SlotBuilder.getSlotValueTypes().contains(type)) {
                    logger.debug("Ignoring slot value type element: " + type);
                    return null;
                }
                if(type.startsWith("uml:")) {
                    //TODO: need to do a side-by-side comparision between MDK elements and SS elements to look for missing values which can be added to the base builder or need specialized builders
                    logger.debug("Using default Mms4 Element Builder for type " + type);
                    return new Mms4ElementBuilder(this, context);
                }
                logger.warn("Could not find builder for element type: " + type);
                return null;
        }
    }

    @Override
    public String toString() {
        return "Twc19_3ToMms4Translator";
    }

    public String translateEsiId(TranslationContext context, String esiId) {
        if(esiId == null) {
            return null;
        }
        //TODO handle the special cases of InstanceSpecification and Slots (MMS/MDK doesn't use the real id, but something like the owner id + "_asi")
        JSONObject element = context.getElement(esiId);
        if(element == null) {
            logger.error("Could not translate id of element " + esiId);
            return esiId;
        } else {
            return getSourceSyntax().getParser().getFieldFromElement(CommonFields.ID, element, String.class);
        }
    }
}

