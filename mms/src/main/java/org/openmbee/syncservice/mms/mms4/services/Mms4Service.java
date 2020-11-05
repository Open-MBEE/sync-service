package org.openmbee.syncservice.mms.mms4.services;


import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.utils.RestInterface;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class Mms4Service {
    private static final Logger logger = LoggerFactory.getLogger(Mms4Service.class);

    private RestInterface restInterface;

    @Autowired
    public void setRestInterface(RestInterface restInterface) {
        this.restInterface = restInterface;
    }

    public JSONObject getRefs(ProjectEndpoint endpoint) {
        String response = restInterface.get(Mms4Endpoints.GET_REFS.buildUrl(endpoint.getHost(), endpoint.getProject()),
                endpoint.getToken(), String.class);
        return new JSONObject(response);
    }

    public JSONObject getCommits(ProjectEndpoint endpoint, String ref) {
        String response = restInterface.get(Mms4Endpoints.GET_REF_COMMITS.buildUrl(endpoint.getHost(), endpoint.getProject(), ref),
                endpoint.getToken(), String.class);
        return new JSONObject(response);
    }

    public JSONObject getProject(ProjectEndpoint endpoint) {
        String response = restInterface.get(Mms4Endpoints.GET_PROJECT.buildUrl(endpoint.getHost(), endpoint.getProject()),
                endpoint.getToken(), String.class);
        return new JSONObject(response);
    }

    public JSONObject getLatestReciprocatedCommit(ProjectEndpoint endpoint) {
        //TODO expand to all branches
        String response = restInterface.get(Mms4Endpoints.GET_TWC_REVISIONS.buildUrl(endpoint.getHost(),
                endpoint.getProject(), "master", "true", "1"), endpoint.getToken(), String.class);
        JSONArray array = new JSONArray(response);
        if(array == null || array.length() == 0){
            return null;
        }
        return array.getJSONObject(0);
    }

    public JSONObject postAddedOrUpdatedElements(ProjectEndpoint endpoint, CommitChanges commitChanges) {
        String response = restInterface.post(Mms4Endpoints.POST_ELEMENTS.buildUrl(endpoint.getHost(), endpoint.getProject(),
                commitChanges.getCommit().getBranchId()), endpoint.getToken(), MediaType.APPLICATION_JSON,
                wrapAddedOrUpdatedElements(commitChanges).toString(), String.class);
        return new JSONObject(response);
    }

    protected JSONObject wrapAddedOrUpdatedElements(CommitChanges commitChanges) {
        JSONArray addedAndUpdated = new JSONArray();
        commitChanges.getAddedElements().forEach(addedAndUpdated::put);
        commitChanges.getUpdatedElements().forEach(addedAndUpdated::put);
        JSONObject wrappedElements = new JSONObject();
        wrappedElements.put("elements", addedAndUpdated);
        return wrappedElements;
    }

    public JSONObject deleteElements(ProjectEndpoint endpoint, CommitChanges commitChanges) {
        String response = restInterface.delete(Mms4Endpoints.DELETE_ELEMENTS.buildUrl(endpoint.getHost(), endpoint.getProject(),
                commitChanges.getCommit().getBranchId()), endpoint.getToken(), MediaType.APPLICATION_JSON,
                wrapElementIdsForDeletion(commitChanges.getDeletedElementIds()).toString(), String.class);
        return new JSONObject(response);
    }

    protected JSONObject wrapElementIdsForDeletion(Collection<String> elementIds) {
        JSONObject wrappedArray = new JSONObject();
        JSONArray wrappedIds = new JSONArray();
        for(String id : elementIds) {
            JSONObject wrappedId = new JSONObject();
            wrappedId.put("id", id);
            wrappedIds.put(wrappedId);
        }
        wrappedArray.put("elements", wrappedIds);

        return wrappedArray;
    }
}