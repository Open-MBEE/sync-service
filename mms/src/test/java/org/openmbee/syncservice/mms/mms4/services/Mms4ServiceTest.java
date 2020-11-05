package org.openmbee.syncservice.mms.mms4.services;

import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.utils.RestInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class Mms4ServiceTest {

    private String collection = "collection";
    private String host = "host";
    private String project = "project";
    private String token = "token";
    private String ref = "ref";

    private ProjectEndpoint projectEndpoint;

    @Mock
    private RestInterface restInterface;

    @Spy
    @InjectMocks
    private Mms4Service mms4Service;

    private CommitChanges commitChanges;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        projectEndpoint = new ProjectEndpoint();
        projectEndpoint.setCollection(collection);
        projectEndpoint.setHost(host);
        projectEndpoint.setProject(project);
        projectEndpoint.setToken(token);
    }

    @Test
    public void getRefsTest() {
        String url = Mms4Endpoints.GET_REFS.buildUrl(host, project);
        doReturn("{}").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getRefs(projectEndpoint);
        assertNotNull(retObj);
    }

    @Test
    public void getCommitsTest() {
        String url = Mms4Endpoints.GET_REF_COMMITS.buildUrl(host, project, ref);
        doReturn("{}").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getCommits(projectEndpoint, ref);
        assertNotNull(retObj);
    }

    @Test
    public void getProjectTest() {
        String url = Mms4Endpoints.GET_PROJECT.buildUrl(host, project);
        doReturn("{}").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getProject(projectEndpoint);
        assertNotNull(retObj);
    }

    @Test
    public void getLatestReciprocatedCommitTestNormal() {
        String url = Mms4Endpoints.GET_TWC_REVISIONS.buildUrl(host, project,"master","true","1");
        doReturn("[{}]").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getLatestReciprocatedCommit(projectEndpoint);
        assertNotNull(retObj);
    }

    @Test
    public void getLatestReciprocatedCommitTestEmpty() {
        String url = Mms4Endpoints.GET_TWC_REVISIONS.buildUrl(host, project,"master","true","1");
        doReturn("[]").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getLatestReciprocatedCommit(projectEndpoint);
        assertNull(retObj);
    }

}