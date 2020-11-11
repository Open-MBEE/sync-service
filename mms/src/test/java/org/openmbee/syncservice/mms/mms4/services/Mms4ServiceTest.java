package org.openmbee.syncservice.mms.mms4.services;

import org.openmbee.syncservice.core.data.branches.Branch;
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
import java.util.List;

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
    public void getRefByIdTest() {
        String url = Mms4Endpoints.GET_REF.buildUrl(host, project, "id");
        doReturn("{}").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getRefById(projectEndpoint, "id");
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
        doReturn("{\"projects\":[{}]}").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getProject(projectEndpoint);
        assertNotNull(retObj);
    }

    @Test
    public void getProjectTest_Fail1() {
        String url = Mms4Endpoints.GET_PROJECT.buildUrl(host, project);
        doReturn("{\"projects\":null}").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getProject(projectEndpoint);
        assertNull(retObj);
    }

    @Test
    public void getProjectTest_Fail2() {
        String url = Mms4Endpoints.GET_PROJECT.buildUrl(host, project);
        doReturn("{}").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getProject(projectEndpoint);
        assertNull(retObj);
    }

    @Test
    public void getProjectTest_Fail3() {
        String url = Mms4Endpoints.GET_PROJECT.buildUrl(host, project);
        doReturn("{\"projects\":[]}").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getProject(projectEndpoint);
        assertNull(retObj);
    }

    @Test
    public void getLatestReciprocatedCommitTestNormal() {
        Branch branch = new Branch();
        branch.setId("master");

        String url = Mms4Endpoints.GET_TWC_REVISIONS.buildUrl(host, project,"master","true","1");
        doReturn("[{}]").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getLatestReciprocatedCommit(projectEndpoint, branch);
        assertNotNull(retObj);
    }

    @Test
    public void getLatestReciprocatedCommitTestEmpty() {
        Branch branch = new Branch();
        branch.setId("master");

        String url = Mms4Endpoints.GET_TWC_REVISIONS.buildUrl(host, project,"master","true","1");
        doReturn("[]").when(restInterface).get(url, token, String.class);

        JSONObject retObj = mms4Service.getLatestReciprocatedCommit(projectEndpoint, branch);
        assertNull(retObj);
    }

    @Test
    public void postAddedOrUpdatedElementsTest() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(List.of(new JSONObject("{'id':'1'}")));
        commitChanges.setUpdatedElements(List.of(new JSONObject("{'id':'2'}")));
        commitChanges.setDeletedElementIds(List.of("3"));
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);
        Branch branch1 = new Branch();
        branch1.setName("branch");
        branch1.setId("bid");

        String url = Mms4Endpoints.POST_ELEMENTS.buildUrl(host, project,"bid");
        doReturn("{}").when(restInterface).post(url, token, MediaType.APPLICATION_JSON,
                "{\"elements\":[{\"id\":\"1\"},{\"id\":\"2\"}]}", String.class);

        JSONObject retObj = mms4Service.postAddedOrUpdatedElements(projectEndpoint, branch1, commitChanges);
        assertNotNull(retObj);
    }

    @Test
    public void deleteElementsTest() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(List.of(new JSONObject("{'id':'1'}")));
        commitChanges.setUpdatedElements(List.of(new JSONObject("{'id':'2'}")));
        commitChanges.setDeletedElementIds(List.of("3"));
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);
        Branch branch1 = new Branch();
        branch1.setName("branch");
        branch1.setId("bid");

        String url = Mms4Endpoints.DELETE_ELEMENTS.buildUrl(host, project,"bid");
        doReturn("{}").when(restInterface).delete(url, token, MediaType.APPLICATION_JSON,
                "{\"elements\":[{\"id\":\"3\"}]}", String.class);

        JSONObject retObj = mms4Service.deleteElements(projectEndpoint, branch1, commitChanges);
        assertNotNull(retObj);
    }

    @Test
    public void createBranchTest() {
        String url = Mms4Endpoints.CREATE_REFS.buildUrl(host, project);
        String req = "{\"refs\":[{\"parentRefId\":\"pid\",\"name\":\"bname\",\"id\":\"bid\",\"type\":\"Branch\"}]}";
        doReturn("{}").when(restInterface).post(url, token, MediaType.APPLICATION_JSON, req, String.class);
        JSONObject branch = mms4Service.createBranch(projectEndpoint, "pid", "bname", "bid");
        assertNotNull(branch);
    }

    @Test
    public void updateCommitWithTwcRevision() {
        String url = Mms4Endpoints.UPDATE_TWC_REVISION.buildUrl(host, project, "mmsId", "twcId");
        doReturn("yup").when(restInterface).put(url, token, String.class);
        String retVal = mms4Service.updateCommitWithTwcRevision(projectEndpoint, "twcId", "mmsId");
        assertEquals("yup", retVal);
    }

    @Test
    public void getCommitTest_Normal() {
        String url = Mms4Endpoints.GET_COMMIT.buildUrl(host, project, "commitId");
        doReturn("{'commits':[{}]}").when(restInterface).get(url, token, String.class);

        JSONObject obj = mms4Service.getCommit(projectEndpoint, "commitId");
        assertNotNull(obj);
    }

    @Test
    public void getCommitTest_Fail1() {
        String url = Mms4Endpoints.GET_COMMIT.buildUrl(host, project, "commitId");
        doReturn("{'commits':[]}").when(restInterface).get(url, token, String.class);

        JSONObject obj = mms4Service.getCommit(projectEndpoint, "commitId");
        assertNull(obj);
    }

    @Test
    public void getCommitTest_Fail2() {
        String url = Mms4Endpoints.GET_COMMIT.buildUrl(host, project, "commitId");
        doReturn("{'commits':null}").when(restInterface).get(url, token, String.class);

        JSONObject obj = mms4Service.getCommit(projectEndpoint, "commitId");
        assertNull(obj);
    }

    @Test
    public void getCommitTest_Fail3() {
        String url = Mms4Endpoints.GET_COMMIT.buildUrl(host, project, "commitId");
        doReturn("{}").when(restInterface).get(url, token, String.class);

        JSONObject obj = mms4Service.getCommit(projectEndpoint, "commitId");
        assertNull(obj);
    }
}