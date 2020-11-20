package org.openmbee.syncservice.mms.mms4.services;


import org.json.JSONArray;
import org.json.JSONObject;
import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.utils.RestInterface;
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

    public JSONObject getRefById(ProjectEndpoint endpoint, String refId) {
        String response = restInterface.get(Mms4Endpoints.GET_REF.buildUrl(endpoint.getHost(), endpoint.getProject(), refId),
                endpoint.getToken(), String.class);
        return new JSONObject(response);
    }

    public JSONObject getCommits(ProjectEndpoint endpoint, String refId) {
        String response = restInterface.get(Mms4Endpoints.GET_REF_COMMITS.buildUrl(endpoint.getHost(), endpoint.getProject(), refId),
                endpoint.getToken(), String.class);
        return new JSONObject(response);
    }

    public JSONObject getProject(ProjectEndpoint endpoint) {
        String response = restInterface.get(Mms4Endpoints.GET_PROJECT.buildUrl(endpoint.getHost(), endpoint.getProject()),
                endpoint.getToken(), String.class);
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.has("projects") && !responseObj.isNull("projects")) {
            JSONArray projects = responseObj.getJSONArray("projects");
            if(projects.length() > 0) {
                return projects.getJSONObject(0);
            }
        }
        return null;
    }

    public JSONObject getLatestReciprocatedCommit(ProjectEndpoint endpoint,  Branch branch) {
        String response = restInterface.get(Mms4Endpoints.GET_TWC_REVISIONS.buildUrl(endpoint.getHost(),
                endpoint.getProject(), branch.getId(), "true", "1"), endpoint.getToken(), String.class);
        JSONArray array = new JSONArray(response);
        if(array.length() == 0){
            return null;
        }
        return array.getJSONObject(0);
    }

    public JSONObject postAddedOrUpdatedElements(ProjectEndpoint endpoint, Branch branch, CommitChanges commitChanges) {
        String response = restInterface.post(Mms4Endpoints.POST_ELEMENTS.buildUrl(endpoint.getHost(), endpoint.getProject(),
                branch.getId()), endpoint.getToken(), MediaType.APPLICATION_JSON,
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

    public JSONObject deleteElements(ProjectEndpoint endpoint, Branch branch, CommitChanges commitChanges) {
        String response = restInterface.delete(Mms4Endpoints.DELETE_ELEMENTS.buildUrl(endpoint.getHost(), endpoint.getProject(),
                branch.getId()), endpoint.getToken(), MediaType.APPLICATION_JSON,
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

    public JSONObject createBranch(ProjectEndpoint endpoint, String parentBranchId, String branchName, String branchId) {
        JSONObject requestObj = createBranchRequest(parentBranchId, branchName, branchId);
        String response = restInterface.post(Mms4Endpoints.CREATE_REFS.buildUrl(endpoint.getHost(),
                endpoint.getProject()), endpoint.getToken(), MediaType.APPLICATION_JSON, requestObj.toString(),
                String.class);
        return new JSONObject(response);
    }

    private JSONObject createBranchRequest(String parentBranchId, String branchName, String branchId) {
        JSONObject ref = new JSONObject();
        ref.put("id", branchId);
        ref.put("name", branchName);
        ref.put("type", "Branch");
        ref.put("parentRefId", parentBranchId);
        JSONArray refs = new JSONArray();
        refs.put(ref);
        JSONObject pkg = new JSONObject();
        pkg.put("refs", refs);
        return pkg;
    }

    public String updateCommitWithTwcRevision(ProjectEndpoint endpoint, String twcCommitId, String mmsCommitId) {
        return restInterface.put(Mms4Endpoints.UPDATE_TWC_REVISION.buildUrl(endpoint.getHost(), endpoint.getProject(),
                mmsCommitId, twcCommitId), endpoint.getToken(), String.class);
    }

    public JSONObject getCommit(ProjectEndpoint endpoint, String commitId) {
        String response = restInterface.get(Mms4Endpoints.GET_COMMIT.buildUrl(endpoint.getHost(), endpoint.getProject(), commitId),
                endpoint.getToken(), String.class);
        JSONObject responseObj = new JSONObject(response);
        if(responseObj.has("commits") && !responseObj.isNull("commits")) {
            JSONArray projects = responseObj.getJSONArray("commits");
            if(projects.length() > 0) {
                return projects.getJSONObject(0);
            }
        }
        return null;
    }

}