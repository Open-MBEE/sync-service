package org.openmbee.syncservice.translation.twc19_3tomms4;

import org.json.JSONObject;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.syntax.Parser;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.translation.Translator;
import org.openmbee.syncservice.twc.sourcesink.TeamworkCloudSource;
import org.openmbee.syncservice.twc.syntax.fields.TwcFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TranslationContext {
    private static final Logger logger = LoggerFactory.getLogger(TranslationContext.class);

    private Translator<?,?> translator;
    private final TeamworkCloudSource source;
    private final Commit commit;
    private final HashSet<String> originalContextElements = new HashSet<>();
    private final Map<String, JSONObject> elementCache = new HashMap<>(); //TODO, is map the right thing to use? Might get big...

    public TranslationContext(Translator<?,?> translator, TeamworkCloudSource source, CommitChanges commitChanges) {
        this.translator = translator;
        this.source = source;
        this.commit = commitChanges.getCommit();
        primeOriginalContext(commitChanges.getAddedElements());
        primeOriginalContext(commitChanges.getUpdatedElements());
    }

    public Commit getCommit() {
        return commit;
    }

    /**
     * @param forceParentCommit Force using the parent commit if you know the elements were deleted
     * @param esiIds
     * @return
     */
    public Map<String, JSONObject> getElements(boolean forceParentCommit, Collection<String> esiIds) {
        Map<String, JSONObject> result = new HashMap<>();
        Set<String> esiIdsToRequest = new HashSet<>();

        esiIds.forEach(esiId -> {
            JSONObject element = elementCache.get(esiId);
            if(element != null) {
                result.put(esiId, element);
            } else {
                esiIdsToRequest.add(esiId);
            }
        });

        if(source == null) {
            return result;
        }
        if(!forceParentCommit && !esiIdsToRequest.isEmpty()) {
            //Try finding as of current commit
            Map<String, JSONObject> requestedElements = source.getElements(commit.getCommitId(), new ArrayList<>(esiIdsToRequest));
            if (requestedElements != null) {
                elementCache.putAll(requestedElements);
                result.putAll(requestedElements);
                esiIdsToRequest.removeAll(requestedElements.keySet());
            }
        }
        if(!esiIdsToRequest.isEmpty()) {
            //Try as of parent commit (deleted items show up here)
            Map<String, JSONObject> requestedElements = source.getElements(commit.getParentCommit(), new ArrayList<>(esiIdsToRequest));
            if (requestedElements != null) {
                elementCache.putAll(requestedElements);
                result.putAll(requestedElements);
                esiIdsToRequest.removeAll(requestedElements.keySet());
            }
        }

        return result;
    }

    public Map<String, JSONObject> getElements(Collection<String> ids) {
        return getElements(false, ids);
    }

    public JSONObject getElement(String id) {
        return getElements(List.of(id)).values().stream().findFirst().orElse(null);
    }

    public boolean isInOriginalContext(String esiId) {
        return originalContextElements.contains(esiId);
    }

    private TranslationContext primeOriginalContext(Collection<JSONObject> elements) {
        if(elements != null && !elements.isEmpty()) {
            Parser parser = translator.getSourceSyntax().getParser();
            Field<TwcFields, String> esiIdField = parser.getFields().getField(TwcFields.ESI_ID, String.class);
            elements.forEach(e -> {
                String esiId = parser.getFieldFromElement(esiIdField, e);
                if(esiId != null) {
                    elementCache.put(esiId, e);
                    originalContextElements.add(esiId);
                } else {
                    logger.error("Could not find esi id in a TWC element: " + commit.getBranchId() + "/" + commit.getCommitId());
                }
            });
        }
        return this;
    }
}
