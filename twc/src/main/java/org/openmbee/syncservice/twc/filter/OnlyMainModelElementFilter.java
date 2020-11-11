package org.openmbee.syncservice.twc.filter;

import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Fields;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.utils.JSONUtils;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.openmbee.syncservice.twc.syntax.fields.TwcFields;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

//TODO this works for new projects only
//TODO need to filter out changes to project usages in diffs
/**
 * Filters out elements that are not within or directly part of the main model
 * (Filters out project usages, package imports, and element imports
 */

public class OnlyMainModelElementFilter extends JSONUtils implements ElementFilter {
    private static final Logger logger = LoggerFactory.getLogger(OnlyMainModelElementFilter.class);

    private Set<String> ignoredIds = new HashSet<>();
    private Map<String, Collection<String>> modelPurgatory = new HashMap<>();
    private Field<SysMLv1X, String> typeField;
    private Field<SysMLv1X, JSONArray> ownedElementsField;
    private Field<TwcFields, String> esiIdField;
    private Field<TwcFields, String> defaultWorkingPackageField;
    private String mainModelEsiId = null;

    public OnlyMainModelElementFilter(Source source) {
        Fields fields = source.getSyntax().getParser().getFields();
        typeField = fields.getField(SysMLv1X.ELEMENT_TYPE, String.class);
        ownedElementsField = fields.getField(SysMLv1X.OWNED_ELEMENTS, JSONArray.class);
        esiIdField = fields.getField(TwcFields.ESI_ID, String.class);
        defaultWorkingPackageField = fields.getField(TwcFields.DEFAULT_WORKING_PACKAGE, String.class);
    }

    @Override
    public void add(JSONObject element) {
        List<String> ownedIds;
        String esiId;

        String type = typeField.get(element);
        if(type == null) {
            //Do nothing
            return;
        }

        switch(type) {
            case "md.ce.rt.options:Options":
                setMainModel(defaultWorkingPackageField.get(element));
                //Fall through
            case "esiproject:EsiProject":
            case "md.ce.csh.rt.options:CshLanguageOptions":
            case "project.options:UserProjectOptions":
            case "project.options:CommonProjectOptions":
            case "md.ce.corbaidl.rt.options:CORBAIDLLanguageOptions":
            case "MDFoundation:MDExtension":
            case "md.ce.java.rt.options:JavaLanguageOptions":
            case "uuidmap:UUIDMap":
            case "esiproject:ProxyInfoHolder":
            case "md.ce.msil.rt.options:MsilLanguageOptions":
            case "security:ProjectSecurity":
            case "md.ce.cppAnsi.rt.options:CppLanguageOptions":
            case "uml:PackageImport":
            case "uml:ElementImport":
                ownedIds = getOwnedIds(element);
                if(ownedIds != null) {
                    ignoredIds.addAll(ownedIds);
                }
                esiId = esiIdField.get(element);
                if(esiId != null) {
                    ignoredIds.add(esiId);
                }
                break;
            case "uml:Model":
                //Need to filter out project usages
                ownedIds = getOwnedIds(element);
                esiId = esiIdField.get(element);
                addModel(esiId, ownedIds);
                break;
            default:
                //Do nothing
        }
    }

    private void setMainModel(String esiId) {
        if(esiId == null) {
            return;
        } else if(mainModelEsiId != null) {
            logger.error("Cannot set main model more than once!");
            return;
        }
        mainModelEsiId = esiId;
        modelPurgatory.forEach(this::addModel);
        modelPurgatory.clear();
    }

    private void addModel(String esiId, Collection<String> ownedIds) {
        if(mainModelEsiId != null) {
            if(mainModelEsiId.equals(esiId)) {
                //Don't ignore the main model
                return;
            } else {
                ignoredIds.add(esiId);
                ignoredIds.addAll(ownedIds);
            }
        } else {
            modelPurgatory.put(esiId, ownedIds);
        }
    }

    private List<String> getOwnedIds(JSONObject element) {
        JSONArray owned = ownedElementsField.get(element);
        return flattenObjectArray(owned, "@id");
    }

    @Override
    public Set<String> getIgnoredIds() {
        return ignoredIds;
    }
}
