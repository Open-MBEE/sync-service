package org.openmbee.syncservice.mms.mms4.sourcesink;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.branches.BranchCreateRequest;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.commits.ReciprocatedCommit;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.utils.JSONUtils;
import org.openmbee.syncservice.mms.mms4.services.Mms4Service;
import org.openmbee.syncservice.mms.mms4.util.Mms4DateFormat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class Mms4SinkTest {

    @Mock
    private Mms4Service mms4Service;
    @Spy
    private Mms4DateFormat mms4DateFormat;
    @Spy
    private JSONUtils jsonUtils;

    @Mock
    private ProjectEndpoint endpoint;
    private Mms4Sink mms4Sink;

    @Mock
    private Source source;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mms4Sink = spy(new Mms4Sink(endpoint));
        mms4Sink.setMms4Service(mms4Service);
        mms4Sink.setMms4DateFormat(mms4DateFormat);
        mms4Sink.setJsonUtils(jsonUtils);
    }

    @Test
    public void commitChanges_OnlyAdded_Normal() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(List.of(new JSONObject()));
        commitChanges.setUpdatedElements(Collections.emptyList());
        commitChanges.setDeletedElementIds(Collections.emptyList());
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);

        Branch branch = new Branch();

        JSONObject response = new JSONObject("{'commitId':'abcd-1234'}");

        when(mms4Service.postAddedOrUpdatedElements(any(), any(), any())).thenReturn(response);

        List<String> commitIds = mms4Sink.commitChanges(source, branch, commitChanges);
        assertEquals(1, commitIds.size());
        assertEquals("abcd-1234", commitIds.get(0));
    }

    @Test
    public void commitChanges_OnlyAdded_Fail() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(List.of(new JSONObject()));
        commitChanges.setUpdatedElements(Collections.emptyList());
        commitChanges.setDeletedElementIds(Collections.emptyList());
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);

        Branch branch = new Branch();

        List<String> commitIds = mms4Sink.commitChanges(source, branch, commitChanges);
        assertEquals(0, commitIds.size());
    }

    @Test
    public void commitChanges_OnlyAdded_Rejected() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(List.of(new JSONObject()));
        commitChanges.setUpdatedElements(Collections.emptyList());
        commitChanges.setDeletedElementIds(Collections.emptyList());
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);

        Branch branch = new Branch();

        JSONObject response = new JSONObject("{'commitId':'abcd-1234','rejected':[{},{}]}");

        when(mms4Service.postAddedOrUpdatedElements(any(), any(), any())).thenReturn(response);

        List<String> commitIds = mms4Sink.commitChanges(source, branch, commitChanges);
        assertEquals(1, commitIds.size());
        assertEquals("abcd-1234", commitIds.get(0));
    }

    @Test
    public void commitChanges_OnlyUpdated_Normal() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(Collections.emptyList());
        commitChanges.setUpdatedElements(List.of(new JSONObject()));
        commitChanges.setDeletedElementIds(Collections.emptyList());
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);

        Branch branch = new Branch();

        JSONObject response = new JSONObject("{'commitId':'abcd-1234','elements':[{'_commitId':'commit1'}]}");

        when(mms4Service.postAddedOrUpdatedElements(any(), any(), any())).thenReturn(response);

        List<String> commitIds = mms4Sink.commitChanges(source, branch, commitChanges);
        assertEquals(1, commitIds.size());
        assertEquals("abcd-1234", commitIds.get(0));
    }

    @Test
    public void commitChanges_OnlyUpdated_Fail() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(Collections.emptyList());
        commitChanges.setUpdatedElements(List.of(new JSONObject()));
        commitChanges.setDeletedElementIds(Collections.emptyList());
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);

        Branch branch = new Branch();

        List<String> commitIds = mms4Sink.commitChanges(source, branch, commitChanges);
        assertEquals(0, commitIds.size());
    }

    @Test
    public void commitChanges_AddedAndUpdated_Normal() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(List.of(new JSONObject()));
        commitChanges.setUpdatedElements(List.of(new JSONObject()));
        commitChanges.setDeletedElementIds(Collections.emptyList());
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);

        Branch branch = new Branch();

        JSONObject response = new JSONObject("{'commitId':'abcd-1234','elements':[{'_commitId':'commit1'}]}");

        when(mms4Service.postAddedOrUpdatedElements(any(), any(), any())).thenReturn(response);

        List<String> commitIds = mms4Sink.commitChanges(source, branch, commitChanges);
        assertEquals(1, commitIds.size());
        assertEquals("abcd-1234", commitIds.get(0));
    }

    @Test
    public void commitChanges_OnlyDeleted_Normal() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(Collections.emptyList());
        commitChanges.setUpdatedElements(Collections.emptyList());
        commitChanges.setDeletedElementIds(List.of("id1"));
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);

        Branch branch = new Branch();

        JSONObject response = new JSONObject("{'commitId':'abcd-1234','elements':[{'_commitId':'commit1'}]}");

        when(mms4Service.deleteElements(any(), any(), any())).thenReturn(response);

        List<String> commitIds = mms4Sink.commitChanges(source, branch, commitChanges);
        assertEquals(1, commitIds.size());
        assertEquals("abcd-1234", commitIds.get(0));
    }

    @Test
    public void commitChanges_OnlyDeleted_Fail() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(Collections.emptyList());
        commitChanges.setUpdatedElements(Collections.emptyList());
        commitChanges.setDeletedElementIds(List.of("id1"));
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);

        Branch branch = new Branch();

        when(mms4Service.deleteElements(any(), any(), any())).thenReturn(null);

        List<String> commitIds = mms4Sink.commitChanges(source, branch, commitChanges);
        assertEquals(0, commitIds.size());
    }

    @Test
    public void commitChanges_AddedAndUpdatedAndDeleted_Normal() {
        CommitChanges commitChanges = new CommitChanges();
        commitChanges.setAddedElements(List.of(new JSONObject()));
        commitChanges.setUpdatedElements(List.of(new JSONObject()));
        commitChanges.setDeletedElementIds(List.of("id1"));
        Commit commit = new Commit();
        commit.setCommitId("sourceCommitId");
        commitChanges.setCommit(commit);

        Branch branch = new Branch();

        JSONObject response1 = new JSONObject("{'commitId':'abcd-1234','elements':[{'_commitId':'commit1'}]}");
        JSONObject response2 = new JSONObject("{'commitId':'abcd-2345','elements':[{'_commitId':'commit2'}]}");

        when(mms4Service.postAddedOrUpdatedElements(any(), any(), any())).thenReturn(response1);
        when(mms4Service.deleteElements(any(), any(), any())).thenReturn(response2);

        List<String> commitIds = mms4Sink.commitChanges(source, branch, commitChanges);
        assertEquals(2, commitIds.size());
        assertEquals("abcd-1234", commitIds.get(0));
        assertEquals("abcd-2345", commitIds.get(1));
    }

    @Test
    public void getCommitHistory() {
        Branch branch1 = new Branch();
        branch1.setName("branch1");
        branch1.setId("b1id");
        Branch branch2 = new Branch();
        branch2.setName("branch2");
        branch2.setId("b2id");
        doReturn(List.of(branch1, branch2)).when(mms4Sink).getBranches();

        JSONObject branch1Commits = new JSONObject("{'commits':[{'_created':'2020-01-01T01:01:01.000-0600','id':'commitb1-1'}]}");
        when(mms4Service.getCommits(endpoint, branch1.getId())).thenReturn(branch1Commits);

        List<Commit> commits = mms4Sink.getCommitHistory();
        assertEquals(1, commits.size());
        assertEquals("commitb1-1", commits.get(0).getCommitId());
    }


    @Test
    public void getBranchCommitHistory_Normal() {
        Branch branch1 = new Branch();
        branch1.setName("branch1");
        branch1.setId("b1id");
        doReturn(branch1).when(mms4Sink).getBranchById("b1id");

        JSONObject branch1Commits = new JSONObject("{'commits':[{'_created':'2020-01-01T01:01:01.000-0600','id':'commitb1-1'},{}]}");
        when(mms4Service.getCommits(endpoint, "b1id")).thenReturn(branch1Commits);

        List<Commit> commits = mms4Sink.getBranchCommitHistory("b1id",1);
        assertEquals(1, commits.size());
        assertEquals("commitb1-1", commits.get(0).getCommitId());
        assertEquals("branch1", commits.get(0).getBranchName());
    }


    @Test
    public void getBranchCommitHistory_Fail1() {
        Branch branch1 = new Branch();
        branch1.setName("branch1");
        branch1.setId("b1id");
        doReturn(branch1).when(mms4Sink).getBranchById("b1id");

        when(mms4Service.getCommits(endpoint, "b1id")).thenReturn(null);

        List<Commit> commits = mms4Sink.getBranchCommitHistory("b1id",1);
        assertNull(commits);
    }

    @Test
    public void getBranchCommitHistory_BranchFail() {

        JSONObject branch1Commits = new JSONObject("{'commits':[{'_created':'2020-01-01T01:01:01.000-0600','id':'commitb1-1'},{}]}");
        when(mms4Service.getCommits(endpoint, "b1id")).thenReturn(branch1Commits);

        List<Commit> commits = mms4Sink.getBranchCommitHistory("b1id",1);
        assertEquals(1, commits.size());
        assertEquals("commitb1-1", commits.get(0).getCommitId());
        assertNull(commits.get(0).getBranchName());
    }

    @Test
    public void getCommitById_Normal() {
        Branch branch1 = new Branch();
        branch1.setName("branch1");
        branch1.setId("b1id");
        doReturn(branch1).when(mms4Sink).getBranchById("b1id");

        JSONObject commitObj = new JSONObject("{'_refId':'b1id','_created':'2020-01-01T01:01:01.000-0600'}");
        when(mms4Service.getCommit(endpoint, "commit1")).thenReturn(commitObj);

        Commit commit = mms4Sink.getCommitById("commit1");
        assertNotNull(commit);
        assertEquals("commit1", commit.getCommitId());
        assertEquals("branch1", commit.getBranchName());
    }

    @Test
    public void getCommitById_Fail() {
        when(mms4Service.getCommit(endpoint, "commit1")).thenReturn(null);

        Commit commit = mms4Sink.getCommitById("commit1");
        assertNull(commit);
    }

    @Test
    public void getCommitById_BranchFail() {
        doReturn(null).when(mms4Sink).getBranchById("b1id");

        JSONObject commitObj = new JSONObject("{'_refId':'b1id','_created':'2020-01-01T01:01:01.000-0600'}");
        when(mms4Service.getCommit(endpoint, "commit1")).thenReturn(commitObj);

        Commit commit = mms4Sink.getCommitById("commit1");
        assertNotNull(commit);
        assertEquals("commit1", commit.getCommitId());
        assertNull(commit.getBranchName());
    }

    @Test
    public void getLatestReciprocatedCommit_Normal() {
        Branch branch1 = new Branch();
        branch1.setName("branch1");
        branch1.setId("b1id");
        Branch branch2 = new Branch();
        branch2.setName("branch2");
        branch2.setId("b2id");
        doReturn(List.of(branch1, branch2)).when(mms4Sink).getBranches();

        JSONObject commit1 = new JSONObject("{'twc-revisionId':'123','id':'commitId1','_created':'2020-01-01T01:01:01.000-0600'}");
        when(mms4Service.getLatestReciprocatedCommit(endpoint, branch1)).thenReturn(commit1);
        JSONObject commit2 = new JSONObject("{'twc-revisionId':'124','id':'commitId2','_created':'2020-01-01T01:01:01.001-0600'}");
        when(mms4Service.getLatestReciprocatedCommit(endpoint, branch2)).thenReturn(commit2);

        ReciprocatedCommit rc = mms4Sink.getLatestReciprocatedCommit();
        assertNotNull(rc);
        assertEquals("124", rc.getSourceCommitId());
        assertEquals("commitId2", rc.getSinkCommitId());
    }

    @Test
    public void getLatestReciprocatedCommit_Fail1() {
        Branch branch1 = new Branch();
        branch1.setName("branch1");
        branch1.setId("b1id");
        Branch branch2 = new Branch();
        branch2.setName("branch2");
        branch2.setId("b2id");
        doReturn(List.of(branch1, branch2)).when(mms4Sink).getBranches();

        JSONObject commit = new JSONObject("{'id':'commitId'}");
        when(mms4Service.getLatestReciprocatedCommit(endpoint, branch1)).thenReturn(commit);

        ReciprocatedCommit rc = mms4Sink.getLatestReciprocatedCommit();
        assertNull(rc);
    }

    @Test
    public void getLatestReciprocatedCommit_Fail2() {
        Branch branch1 = new Branch();
        branch1.setName("branch1");
        branch1.setId("b1id");
        Branch branch2 = new Branch();
        branch2.setName("branch2");
        branch2.setId("b2id");
        doReturn(List.of(branch1, branch2)).when(mms4Sink).getBranches();

        when(mms4Service.getLatestReciprocatedCommit(endpoint, branch1)).thenReturn(null);

        ReciprocatedCommit rc = mms4Sink.getLatestReciprocatedCommit();
        assertNull(rc);
    }


    @Test
    public void registerReciprocatedCommit() {
        mms4Sink.registerReciprocatedCommit("234", "456");
        verify(mms4Service).updateCommitWithTwcRevision(endpoint, "234", "456");
    }

    @Test
    public void getBranches_Normal_NoParent() {
        JSONObject refs = new JSONObject("{'refs':[{'id':'bid','name':'bname'}]}");
        when(mms4Service.getRefs(endpoint)).thenReturn(refs);

        Collection<Branch> branches = mms4Sink.getBranches();
        assertEquals(1, branches.size());
        assertNull(branches.iterator().next().getOriginCommit());
    }

    @Test
    public void getBranches_Normal_Parent() {
        JSONObject refs = new JSONObject("{'refs':[{'id':'bid','name':'bname','parentRefId':'pid','parentCommitId':'pcommit'}]}");
        when(mms4Service.getRefs(endpoint)).thenReturn(refs);

        Collection<Branch> branches = mms4Sink.getBranches();
        assertEquals(1, branches.size());
        assertEquals("pcommit", branches.iterator().next().getOriginCommit());
    }

    @Test
    public void getBranches_Normal_Fail1() {
        JSONObject refs = new JSONObject("{}");
        when(mms4Service.getRefs(endpoint)).thenReturn(refs);

        Collection<Branch> branches = mms4Sink.getBranches();
        assertEquals(0, branches.size());
    }

    @Test
    public void getBranches_Normal_Fail2() {
        when(mms4Service.getRefs(endpoint)).thenReturn(null);

        Collection<Branch> branches = mms4Sink.getBranches();
        assertEquals(0, branches.size());
    }

    @Test
    public void getBranchByName_Normal() {
        JSONObject refs = new JSONObject("{'refs':[{'id':'bid','name':'bname'}]}");
        when(mms4Service.getRefs(endpoint)).thenReturn(refs);

        Branch branch = mms4Sink.getBranchByName("bname");
        assertNotNull(branch);
    }

    @Test
    public void getBranchByName_Fail1() {
        JSONObject refs = new JSONObject("{'refs':[{'id':'bid','name':'bname'}]}");
        when(mms4Service.getRefs(endpoint)).thenReturn(refs);

        Branch branch = mms4Sink.getBranchByName("notfound");
        assertNull(branch);
    }

    @Test
    public void getBranchByName_Fail2() {
        JSONObject refs = new JSONObject("{}");
        when(mms4Service.getRefs(endpoint)).thenReturn(refs);

        Branch branch = mms4Sink.getBranchByName("notfound");
        assertNull(branch);
    }

    @Test
    public void getBranchByName_Fail3() {
        when(mms4Service.getRefs(endpoint)).thenReturn(null);

        Branch branch = mms4Sink.getBranchByName("notfound");
        assertNull(branch);
    }

    @Test
    public void getBranchById_Normal() {
        JSONObject refs = new JSONObject("{'refs':[{'id':'bid','name':'bname'}]}");
        when(mms4Service.getRefById(endpoint,"bid")).thenReturn(refs);

        Branch branch = mms4Sink.getBranchById("bid");
        assertNotNull(branch);
    }

    @Test
    public void getBranchById_Fail1() {
        JSONObject refs = new JSONObject("{'refs':[]}");
        when(mms4Service.getRefById(endpoint,"bid")).thenReturn(refs);

        Branch branch = mms4Sink.getBranchById("bid");
        assertNull(branch);
    }

    @Test
    public void getBranchById_Fail2() {
        JSONObject refs = new JSONObject("{}");
        when(mms4Service.getRefById(endpoint,"bid")).thenReturn(refs);

        Branch branch = mms4Sink.getBranchById("bid");
        assertNull(branch);
    }

    @Test
    public void getBranchById_Fail3() {
        when(mms4Service.getRefById(endpoint,"bid")).thenReturn(null);

        Branch branch = mms4Sink.getBranchById("bid");
        assertNull(branch);
    }

    @Test
    public void createBranch_Normal() {
        BranchCreateRequest request = new BranchCreateRequest();
        request.setBranchId("bid");
        request.setBranchName("bname");
        request.setParentBranchId("pid");
        request.setParentBranchName("pname");

        JSONObject refs = new JSONObject("{'refs':[{'id':'bid','name':'bname'}]}");
        when(mms4Service.createBranch(endpoint,"pid", "bname", "bid")).thenReturn(refs);

        Branch branch = mms4Sink.createBranch(request);

        assertNotNull(branch);
    }

    @Test
    public void createBranch_Fail1() {
        BranchCreateRequest request = new BranchCreateRequest();
        request.setBranchId("bid");
        request.setBranchName("bname");
        request.setParentBranchId("pid");
        request.setParentBranchName("pname");

        JSONObject refs = new JSONObject("{'refs':[]}");
        when(mms4Service.createBranch(endpoint,"pid", "bname", "bid")).thenReturn(refs);

        Branch branch = mms4Sink.createBranch(request);

        assertNull(branch);
    }

    @Test
    public void createBranch_Fail2() {
        BranchCreateRequest request = new BranchCreateRequest();
        request.setBranchId("bid");
        request.setBranchName("bname");
        request.setParentBranchId("pid");
        request.setParentBranchName("pname");

        JSONObject refs = new JSONObject("{}");
        when(mms4Service.createBranch(endpoint,"pid", "bname", "bid")).thenReturn(refs);

        Branch branch = mms4Sink.createBranch(request);

        assertNull(branch);
    }

    @Test
    public void createBranch_Fail3() {
        BranchCreateRequest request = new BranchCreateRequest();
        request.setBranchId("bid");
        request.setBranchName("bname");
        request.setParentBranchId("pid");
        request.setParentBranchName("pname");

        when(mms4Service.createBranch(endpoint,"pid", "bname", "bid")).thenReturn(null);

        Branch branch = mms4Sink.createBranch(request);

        assertNull(branch);
    }

    @Test
    public void getProjectSchema_Normal() {
        JSONObject project = new JSONObject("{'schema':'default'}");
        when(mms4Service.getProject(endpoint)).thenReturn(project);

        String schema = mms4Sink.getProjectSchema();
        assertEquals("default", schema);
    }

    @Test
    public void getProjectSchema_Fail1() {
        JSONObject project = new JSONObject("{}");
        when(mms4Service.getProject(endpoint)).thenReturn(project);

        String schema = mms4Sink.getProjectSchema();
        assertNull(schema);
    }

    @Test
    public void getProjectSchema_Fail2() {
        when(mms4Service.getProject(endpoint)).thenReturn(null);

        String schema = mms4Sink.getProjectSchema();
        assertNull(schema);
    }
}