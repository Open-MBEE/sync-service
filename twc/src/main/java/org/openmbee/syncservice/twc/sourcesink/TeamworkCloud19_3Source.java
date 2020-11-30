package org.openmbee.syncservice.twc.sourcesink;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpointInterface;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Fields;
import org.openmbee.syncservice.core.syntax.Parser;
import org.openmbee.syncservice.core.syntax.Syntax;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.utils.JSONUtils;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.openmbee.syncservice.twc.filter.ElementFilter;
import org.openmbee.syncservice.twc.filter.OnlyMainModelElementFilter;
import org.openmbee.syncservice.twc.service.TeamworkService;
import org.openmbee.syncservice.twc.syntax.TwcSyntax;
import org.openmbee.syncservice.twc.syntax.fields.TwcFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TeamworkCloud19_3Source implements Source, ProjectEndpointInterface, TeamworkCloudSource {

    private static final Logger logger = LoggerFactory.getLogger(TeamworkCloud19_3Source.class);

    private ProjectEndpoint sourceEndpoint;
    private TeamworkService teamworkService;
    private JSONUtils jsonUtils;

    @Value("${twc.enforce_project_associations:true}")
    private boolean enforceProjectAssociations = true;

    @Autowired
    public void setTeamworkService(TeamworkService teamworkService) {
        this.teamworkService = teamworkService;
    }

    @Autowired
    public void setJsonUtils(JSONUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    public void setEnforceProjectAssociations(boolean enforceProjectAssociations) {
        this.enforceProjectAssociations = enforceProjectAssociations;
    }

    public boolean isEnforceProjectAssociations() {
        return enforceProjectAssociations;
    }

    public TeamworkCloud19_3Source(ProjectEndpoint sourceEndpoint) {
        this.sourceEndpoint = sourceEndpoint;
    }

    public boolean isValid() {
        JSONObject projectInfo = teamworkService.getProjectInfo(getEndpoint());
        return projectInfo != null && projectInfo.has("removed") && !projectInfo.getBoolean("removed");
    }

    @Override
    public Syntax getSyntax() {
        return TwcSyntax.TWC_19_0_3;
    }

    @Override
    public List<Commit> getCommitHistory() {
        JSONArray commits = teamworkService.getProjectRevisions(sourceEndpoint);
        if(commits == null) {
            return null;
        }

        List<Commit> result = new ArrayList<>(commits.length());
        for (int i = 0; i < commits.length(); i++) {
            JSONObject commitJson = commits.getJSONObject(i);
            Commit commit = new Commit();
            commit.setCommitId(String.valueOf(commitJson.get("ID")));
            Instant instant = Instant.ofEpochMilli(commitJson.getLong("createdDate")*1000);
            commit.setCommitDate(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
            commit.setBranchId(commitJson.getString("branchID"));
            commit.setParentCommit(String.valueOf(commitJson.get("directParent")));

            String branchName = null;
            JSONArray branch = teamworkService.getBranchById(sourceEndpoint, commit.getBranchId());
            if(branch != null) {
                branchName = jsonUtils.getStringFromArrayOfJSONObjects(branch, "dcterms:title");
            }
            if(branchName != null) {
                commit.setBranchName(branchName);
            } else {
                logger.error("Could not find branch in Teamwork Cloud: " + commit.getBranchId());
            }
            result.add(commit);
        }
        return result;
    }

    @Override
    public List<Branch> getBranches() {
        JSONArray branchesJson = teamworkService.getBranches(sourceEndpoint);
        if(branchesJson == null) {
            return Collections.emptyList();
        }
        //Branches should be an array of arrays
        List<Branch> branches = new ArrayList<>(branchesJson.length());
        for (int i = 0; i < branchesJson.length(); i++) {
            branches.add(parseBranch(branchesJson.getJSONArray(i)));
        }
        return branches;
    }


    @Override
    public Branch getBranch(String branchId) {
        JSONArray branchJson = teamworkService.getBranchById(sourceEndpoint, branchId);
        if(branchJson == null) {
            return null;
        }
        return parseBranch(branchJson);
    }

    private Branch parseBranch(JSONArray branchJson) {
        Branch branch = new Branch();
        branch.setJson(branchJson);
        branch.setId(trimBranchId(jsonUtils.getStringFromArrayOfJSONObjects(branchJson, "ID")));
        branch.setName(jsonUtils.getStringFromArrayOfJSONObjects(branchJson, "dcterms:title"));

        if(! "trunk".equals(branch.getName())) {
            String startRevision = String.valueOf(jsonUtils.getIntFromArrayOfJSONObjects(branchJson,"startRevision"));
            branch.setOriginCommit(String.valueOf(startRevision));
            JSONObject revision = teamworkService.getRevision(sourceEndpoint, startRevision);
            if(revision == null) {
                logger.error("Could not find revision " + startRevision);
            } else {
                branch.setParentBranchId(trimBranchId(revision.getString("branchID")));
            }
        }

        return branch;
    }

    private String trimBranchId(String id) {
        if(id == null) {
            return null;
        }
        return id.replaceAll("\\.\\./","").replaceAll("branches/", "");
    }

    @Override
    public CommitChanges getCommitChanges(Commit commit) {
        Map<String, JSONObject> addedElements;
        Map<String, JSONObject> updatedElements;
        List<String> deletedElementIds;

        JSONObject revision = teamworkService.getRevision(sourceEndpoint, commit.getCommitId());

        if(commit.getParentCommit() == null ||  "-1".equals(commit.getParentCommit())) {
            //Root revision, treat everything as an addition
            addedElements = getElementsFromRevision(revision, commit.getCommitId(), true);
            updatedElements = new HashMap<>();
            deletedElementIds = new ArrayList<>();

        } else {
            //Normal revision, do a diff
            JSONObject diff = teamworkService.getRevisionDiff(sourceEndpoint, commit.getCommitId(), commit.getParentCommit());
            List<String> added = jsonUtils.convertJsonArrayToStringList(diff.getJSONArray("added"));
            List<String> updated = jsonUtils.convertJsonArrayToStringList(diff.getJSONArray("changed"));

            deletedElementIds = jsonUtils.convertJsonArrayToStringList(diff.getJSONArray("removed"));
            addedElements = teamworkService.getElementsAtRevision(sourceEndpoint, commit.getCommitId(), added);
            updatedElements = teamworkService.getElementsAtRevision(sourceEndpoint, commit.getCommitId(), updated);
        }

        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setCommit(commit);
        commitChanges.setAddedElements(addedElements != null ? addedElements.values() : Collections.emptyList());
        commitChanges.setUpdatedElements(updatedElements != null ? updatedElements.values() : Collections.emptyList());
        commitChanges.setDeletedElementIds(deletedElementIds != null ? deletedElementIds : Collections.emptyList());
        commitChanges.setCommitJson(revision);
        return commitChanges;
    }

    protected Map<String, JSONObject> getElementsFromRevision(JSONObject revision, String revisionId, boolean recursive) {
        if(!revision.has("rootObjectIDs")) {
            logger.error("Could not get root revision for project " + sourceEndpoint.getProject());
            return null;
        }
        JSONArray rootObjects = revision.getJSONArray("rootObjectIDs");
        List<String> elementIds = new ArrayList<>(rootObjects.length());
        for (int i = 0; i < rootObjects.length(); i++) {
            elementIds.add(rootObjects.getString(i));
        }
        if(recursive) {
            return getElementsRecursively(revisionId, elementIds);
        } else {
            return getElements(revisionId, elementIds);
        }
    }

    public Map<String, JSONObject> getElements(String revision, Collection<String> elementIds) {
        return getElements(revision, elementIds, new OnlyMainModelElementFilter(this));
    }

    public Map<String, JSONObject> getElements(String revision, Collection<String> elementIds, ElementFilter filter) {
        Map<String, JSONObject> elements = teamworkService.getElementsAtRevision(sourceEndpoint, revision, new ArrayList<>(elementIds));
        if(filter != null) {
            elements.values().forEach(filter::add);
            filter.getIgnoredIds().forEach(elements::remove);
        }
        return elements;
    }

    public Map<String, JSONObject> getElementsRecursively(String revision, Collection<String> elementIds) {
        return getElementsRecursively(revision, elementIds, new OnlyMainModelElementFilter(this));
    }

    public Map<String, JSONObject> getElementsRecursively(String revision, Collection<String> elementIds, ElementFilter filter) {
        if(elementIds == null || elementIds.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, JSONObject> elements = getElements(revision, elementIds, filter);
        if(elements == null || elements.isEmpty()) {
            return new HashMap<>();
        }

        Set<String> nextElements = elements.values()
                .parallelStream().map(this::getOwnedElements)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        if(filter != null) {
            nextElements.removeAll(filter.getIgnoredIds());
        }
        elements.putAll(getElementsRecursively(revision, nextElements, filter));
        return elements;
    }

    private Set<String> getOwnedElements(JSONObject element) {
        Set<String> ownedElements = new HashSet<>();
        if(! element.has("data")) {
            return ownedElements;
        }
        JSONArray dataArray = element.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);
            if(jsonObject.has("kerml:ownedElement")) {
                JSONArray ownedElementArray = jsonObject.getJSONArray("kerml:ownedElement");
                for (int j = 0; j < ownedElementArray.length(); j++) {
                    JSONObject ownedElement = ownedElementArray.getJSONObject(j);
                    ownedElements.add(ownedElement.getString("@id"));
                }
                break;
            }
        }
        return ownedElements;
    }

    @Override
    public ProjectEndpoint getEndpoint() {
        return sourceEndpoint;
    }

    @Override
    public boolean canSendTo(Sink sink) {
        if(!enforceProjectAssociations) {
            return true;
        }
        if(sink instanceof ProjectEndpointInterface) {
            ProjectEndpoint sinkEndpoint = ((ProjectEndpointInterface)sink).getEndpoint();
            JSONObject project = teamworkService.getProjectInfo(getEndpoint());
            String projectId = getSyntax().getParser().getFieldFromElement(TwcFields.PROJECT_ID, project, String.class);

            if(projectId == null || !projectId.equals(sinkEndpoint.getProject())) {
                logger.error("Project IDs do not match.");
                return false;
            }

            Set<String> associatedHosts = getAssociatedHosts();
            //TODO need something more sophisticated?
            if(associatedHosts == null || !associatedHosts.contains(sinkEndpoint.getHost())) {
                logger.error("Embedded hosts do not match.");
                return false;
            }

            return true;
        }
        return false;
    }

    protected Set<String> getAssociatedHosts() {
        //Find the main model in the latest revision
        JSONObject revision = teamworkService.getLatestRevision(sourceEndpoint);
        Integer revisionIdInt = jsonUtils.getInt(revision, "ID");
        if(revisionIdInt == null) {
            logger.error("Could not find revision id in latest revision. " + getEndpoint().toString());
            return null;
        }
        String revisionId = String.valueOf(revisionIdInt);
        Parser parser = getSyntax().getParser();
        Fields fields = parser.getFields();
        Map<String, JSONObject> rootElements = getElementsFromRevision(revision, revisionId, false);
        Field<SysMLv1X, String> typeField = fields.getField(SysMLv1X.ELEMENT_TYPE, String.class);
        JSONObject mainModel = rootElements.values().stream().filter(v -> "uml:Model".equals(typeField.get(v))).findAny().orElse(null);
        if(mainModel == null) {
            logger.error("Could not find main model. " + getEndpoint().toString());
            return null;
        }
        //Get the applied stereotype instance from the main model and everything under it
        Field<SysMLv1X, String> asiIdField = fields.getField(SysMLv1X.APPLIED_STEREOTYPE_INSTANCE, String.class);
        String asiId = asiIdField.get(mainModel);
        if(asiId == null) {
            logger.debug("Main model has no Applied Stereotype Instance - ignoring. " + getEndpoint().toString());
            return null;
        }
        Map<String, JSONObject> asiElements = getElementsRecursively(revisionId, List.of(asiId), null);

        //Figure out which defining features are associated with MMS URL values
        Field<SysMLv1X, String> definingFeatureIdField = fields.getField(SysMLv1X.DEFINING_FEATURE, String.class);
        Set<String> definingFeatureIds = asiElements.values().stream().map(v -> definingFeatureIdField.get(v))
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, JSONObject> definingFeatures = getElements(revisionId, definingFeatureIds, null);
        Field<SysMLv1X, String> nameField = fields.getField(SysMLv1X.NAME, String.class);
        Field<TwcFields, String> esiIdField = fields.getField(TwcFields.ESI_ID, String.class);
        Set<String> mmsUrlDefiningFeatureIds = definingFeatures.values().stream()
                .filter(v -> "MMS URL".equals(nameField.get(v)))
                .map(esiIdField::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if(mmsUrlDefiningFeatureIds.isEmpty()) {
            logger.debug("No MMS URL defining feature found");
            return null;
        }

        //Get the MMS URL values
        Field<SysMLv1X, JSONArray> valuesArrayField = fields.getField(SysMLv1X.VALUE, JSONArray.class);
        Set<String> valueIds = asiElements.values().parallelStream().filter(v -> {
                    String definingFeatureId = definingFeatureIdField.get(v);
                    return definingFeatureId != null && mmsUrlDefiningFeatureIds.contains(definingFeatureId);
                })
                .map(v -> jsonUtils.<String>flattenObjectArray(valuesArrayField.get(v), "@id"))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Field<SysMLv1X, String> valueField = fields.getField(SysMLv1X.VALUE, String.class);
        return valueIds.stream().map(asiElements::get)
                .filter(Objects::nonNull)
                .filter(v -> "uml:LiteralString".equals(typeField.get(v)))
                .map(valueField::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return String.format("TeamworkCloud19_3Source (%s/%s/%s)", sourceEndpoint.getHost(),
                sourceEndpoint.getCollection(), sourceEndpoint.getProject());
    }
}
