package org.openmbee.syncservice.twc.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.utils.JSONUtils;
import org.openmbee.syncservice.core.utils.RestInterface;
import org.openmbee.syncservice.twc.TeamworkCloudEndpoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TeamworkService class is used to make GET requests to Teamwork Cloud REST APIs   
 *
 */


@Service
public class TeamworkService {
	
	private static final Logger logger = LoggerFactory.getLogger(TeamworkService.class);

	private Integer maxElementBatchSize;
	private RestInterface restInterface;
	private JSONUtils jsonUtils;

	@Autowired
	public void setMaxElementBatchSize(@Value("${twc.max_element_batch_size}") Integer maxElementBatchSize) {
		this.maxElementBatchSize = maxElementBatchSize;
	}

	@Autowired
	public void setRestInterface(RestInterface restInterface) {
		this.restInterface = restInterface;
	}

	@Autowired
	public void setJSONUtils(JSONUtils jsonUtils) { this.jsonUtils = jsonUtils; }

	public String getVersion(String host, String token) {
		String versionInfo = restInterface.get(TeamworkCloudEndpoints.GET_VERSION_INFO.buildUrl(host),
				token, String.class);
		return versionInfo;
	}

	public JSONObject getRevisionDiff(ProjectEndpoint endpoint, String toRevision, String fromRevision) {
		String revisionDiff = restInterface.get(TeamworkCloudEndpoints.GET_DIFF_REVISIONS.buildUrl(
				endpoint.getHost(), endpoint.getCollection(), endpoint.getProject(), toRevision, fromRevision),
				endpoint.getToken(), String.class);
		return new JSONObject(revisionDiff);
	}

	public Map<String, JSONObject> getElementsAtRevision(ProjectEndpoint endpoint, String revision, List<String> elements) {
		Map<String, JSONObject> result = new HashMap<>();

		//TODO: Parallelize to make more efficient
		for(int index = 0; index < elements.size(); index += maxElementBatchSize) {
			List<String> sublist = elements.subList(index, Math.min(index + maxElementBatchSize, elements.size()));
			JSONObject raw = getElementsAtRevisionBatch(endpoint, revision, sublist);
			sublist.forEach(e -> result.put(e, raw.getJSONObject(e)));
		}

		return result;
	}

	private JSONObject getElementsAtRevisionBatch(ProjectEndpoint endpoint, String revision, List<String> elements) {
		String revisionDiff = restInterface.post(TeamworkCloudEndpoints.POST_FETCH_ELEMENTS_AT_REVISION.buildUrl(
				endpoint.getHost(), endpoint.getCollection(), endpoint.getProject(), revision), endpoint.getToken(),
				MediaType.TEXT_PLAIN, elements.stream().collect(Collectors.joining(",")), String.class);
		return new JSONObject(revisionDiff);
	}

	public JSONArray getProjectRevisions(ProjectEndpoint endpoint) {
		//TODO need to add paging somewhere (page, items) to handle long commit histories
		String json = restInterface.get(TeamworkCloudEndpoints.GET_PROJECT_REVISIONS.buildUrl(endpoint.getHost(),
				endpoint.getCollection(), endpoint.getProject(), "true"), endpoint.getToken(), String.class);
		return new JSONArray(json);
	}

	public JSONArray getBranches(ProjectEndpoint endpoint) {
		String json =  restInterface.get(TeamworkCloudEndpoints.GET_BRANCHES.buildUrl(endpoint.getHost(),
				endpoint.getCollection(), endpoint.getProject(), "true"), endpoint.getToken(), String.class);
		if(json == null || json.isEmpty()) {
			return null;
		}
		JSONObject returnedObject = new JSONObject(json);
		if(returnedObject.has("ldp:contains") && ! returnedObject.isNull("ldp:contains")) {
			return returnedObject.getJSONArray("ldp:contains");
		}
		return null;
	}

	public JSONArray getBranchById(ProjectEndpoint endpoint, String branchId) {
		String json =  restInterface.get(TeamworkCloudEndpoints.GET_BRANCH.buildUrl(endpoint.getHost(),
				endpoint.getCollection(), endpoint.getProject(), branchId), endpoint.getToken(), String.class);
		return new JSONArray(json);
	}

	public JSONObject getRevision(ProjectEndpoint endpoint, String revision) {
		String json = restInterface.get(TeamworkCloudEndpoints.GET_PROJECT_REVISION.buildUrl(endpoint.getHost(),
				endpoint.getCollection(), endpoint.getProject(), revision), endpoint.getToken(),
				String.class);

		if(json == null || json.isEmpty()) {
			return null;
		}
		JSONArray jsonArray = new JSONArray(json);
		if(json != null && jsonArray.length() > 0) {
			return jsonArray.getJSONObject(0);
		}
		return null;
	}
}
