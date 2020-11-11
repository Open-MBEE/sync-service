package org.openmbee.syncservice.twc.service;

import org.openmbee.syncservice.core.utils.JSONUtils;
import org.openmbee.syncservice.core.utils.RestInterface;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.twc.TeamworkCloudEndpoints;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static reactor.core.publisher.Mono.when;

public class TeamworkServiceTest {

    @Mock
    private RestInterface restInterface;

    @Spy
    private JSONUtils jsonUtils;

    @Spy
    @InjectMocks
    private TeamworkService teamworkService;

    private ProjectEndpoint endpoint;

    @Before
    public void setup() {
        endpoint = new ProjectEndpoint();
        endpoint.setHost("host");
        endpoint.setCollection("collection");
        endpoint.setProject("project");
        endpoint.setToken("token");

        MockitoAnnotations.initMocks(this);
        teamworkService.setMaxElementBatchSize(5);
    }

    @Test
    public void getVersionTest() {
        String versionInfo = "version info here";
        String url = TeamworkCloudEndpoints.GET_VERSION_INFO.buildUrl("host");
        doReturn(versionInfo).when(restInterface).get(eq(url), eq("token"), eq(String.class));

        String versionObj = teamworkService.getVersion("host", "token");
        assertEquals(versionInfo, versionObj);
    }

    @Test
    public void getRevisionDiffTest() {
        String revisionDiff = "{'name':'value'}";
        String toRev = "2";
        String fromRev = "1";
        String url = TeamworkCloudEndpoints.GET_DIFF_REVISIONS.buildUrl("host","collection","project", toRev, fromRev);
        doReturn(revisionDiff).when(restInterface).get(eq(url), eq("token"), eq(String.class));

        JSONObject obj = teamworkService.getRevisionDiff(endpoint, toRev, fromRev);
        assertNotNull(obj);
        assertTrue(obj.has("name"));
    }

    @Test
    public void getElementsAtRevisionTestSingleBatch() {
        List<String> elements = List.of("one", "two");
        String json = "{'one':{},'two':{}}";
        String rev = "22";

        String url = TeamworkCloudEndpoints.POST_FETCH_ELEMENTS_AT_REVISION
                .buildUrl("host", "collection", "project", "22");

        doReturn(json).when(restInterface).post(eq(url), eq("token"), eq(MediaType.TEXT_PLAIN),
                eq("one,two"), eq(String.class));

        Map<String, JSONObject> elementMap = teamworkService.getElementsAtRevision(endpoint, rev, elements);
        assertNotNull(elementMap);
        assertEquals(2, elementMap.size());
    }

    @Test
    public void getElementsAtRevisionTestMultiBatch() {
        List<String> elements = List.of("one", "two", "three", "four", "five", "six");
        String json1 = "{'one':{},'two':{},'three':{},'four':{},'five':{}}";
        String json2 = "{'six':{}}";
        String rev = "22";

        String url = TeamworkCloudEndpoints.POST_FETCH_ELEMENTS_AT_REVISION
                .buildUrl("host", "collection", "project", rev);

        doReturn(json1).when(restInterface).post(eq(url), eq("token"), eq(MediaType.TEXT_PLAIN),
                eq("one,two,three,four,five"), eq(String.class));
        doReturn(json2).when(restInterface).post(eq(url), eq("token"), eq(MediaType.TEXT_PLAIN),
                eq("six"), eq(String.class));

        Map<String, JSONObject> elementMap = teamworkService.getElementsAtRevision(endpoint, rev, elements);
        assertNotNull(elementMap);
        assertEquals(6, elementMap.size());
    }

    @Test
    public void getProjectRevisionsTest() {
        String json = "[{'one':'onev'},{'two':'twov'}]";
        String url = TeamworkCloudEndpoints.GET_PROJECT_REVISIONS.buildUrl("host","collection","project","true");
        doReturn(json).when(restInterface).get(url, "token", String.class);

        JSONArray array = teamworkService.getProjectRevisions(endpoint);
        assertNotNull(array);
        assertEquals(2, array.length());
    }

    @Test
    public void getBranches_Null() {
        String json = null;
        String url = TeamworkCloudEndpoints.GET_BRANCHES.buildUrl("host", "collection", "project", "true");
        doReturn(json).when(restInterface).get(url, "token", String.class);

        JSONArray array = teamworkService.getBranches(endpoint);
        assertNull(array);
    }

    @Test
    public void getBranches_Empty() {
        String json = "";
        String url = TeamworkCloudEndpoints.GET_BRANCHES.buildUrl("host", "collection", "project", "true");
        doReturn(json).when(restInterface).get(url, "token", String.class);

        JSONArray array = teamworkService.getBranches(endpoint);
        assertNull(array);
    }

    @Test
    public void getBranches_Missing() {
        String json = "{}";
        String url = TeamworkCloudEndpoints.GET_BRANCHES.buildUrl("host", "collection", "project", "true");
        doReturn(json).when(restInterface).get(url, "token", String.class);

        JSONArray array = teamworkService.getBranches(endpoint);
        assertNull(array);
    }

    @Test
    public void getBranches_InternalNull() {
        String json = "{\"ldp:contains\":null}";
        String url = TeamworkCloudEndpoints.GET_BRANCHES.buildUrl("host", "collection", "project", "true");
        doReturn(json).when(restInterface).get(url, "token", String.class);

        JSONArray array = teamworkService.getBranches(endpoint);
        assertNull(array);
    }

    @Test
    public void getBranches_Normal() {
        String json = "{\"ldp:contains\":[{}]}";
        String url = TeamworkCloudEndpoints.GET_BRANCHES.buildUrl("host", "collection", "project", "true");
        doReturn(json).when(restInterface).get(url, "token", String.class);

        JSONArray array = teamworkService.getBranches(endpoint);
        assertNotNull(array);
        assertEquals(1, array.length());
    }

    @Test
    public void getBranchByIdTest() {
        String branchId = "thebranch";
        String json = "[{'part1':{}},{'part2':{}}]";
        String url = TeamworkCloudEndpoints.GET_BRANCH.buildUrl("host","collection", "project", branchId);
        doReturn(json).when(restInterface).get(url, "token",String.class);

        JSONArray branch = teamworkService.getBranchById(endpoint, branchId);
        assertNotNull(branch);
        assertEquals(2, branch.length());
    }

    @Test
    public void getRevisionTest() {
        String revision = "123";
        String json = "[{'part1':{}},{'part2':{}}]";
        String url = TeamworkCloudEndpoints.GET_PROJECT_REVISION.buildUrl("host","collection", "project", revision);
        doReturn(json).when(restInterface).get(url, "token",String.class);

        JSONObject rev = teamworkService.getRevision(endpoint, revision);
        assertNotNull(rev);
        assertTrue(rev.has("part1"));
    }

    @Test
    public void getRevisionTestError1() {
        String revision = "123";
        String json = "[]";
        String url = TeamworkCloudEndpoints.GET_PROJECT_REVISION.buildUrl("host","collection", "project", revision);
        doReturn(json).when(restInterface).get(url, "token",String.class);

        JSONObject rev = teamworkService.getRevision(endpoint, revision);
        assertNull(rev);
    }

    @Test
    public void getRevisionTestError2() {
        String revision = "123";
        String url = TeamworkCloudEndpoints.GET_PROJECT_REVISION.buildUrl("host","collection", "project", revision);
        doReturn(null).when(restInterface).get(url, "token",String.class);

        JSONObject rev = teamworkService.getRevision(endpoint, revision);
        assertNull(rev);
    }

}