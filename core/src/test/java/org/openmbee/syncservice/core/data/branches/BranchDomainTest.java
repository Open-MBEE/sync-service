package org.openmbee.syncservice.core.data.branches;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.ReciprocatedCommit;
import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BranchDomainTest {

    @Mock
    private Source source;

    @Mock
    private Sink sink;

    @Spy
    private BranchDomain branchDomain;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkForAndCreateHistoricBranches_NoReciprocatedCommits() {
        UnreciprocatedCommits unreciprocatedCommits = new UnreciprocatedCommits(null,
                Collections.emptyList(), Collections.emptyList());
        //This should just do nothing, not throw
        branchDomain.checkForAndCreateHistoricBranches(unreciprocatedCommits, Collections.emptyMap(), sink);

        verify(branchDomain, times(0)).createBranchAtHead(any(), any(), any(), any());
    }

    @Test
    public void checkForAndCreateHistoricBranches_NoBranches() {
        ReciprocatedCommit lastReciprocatedCommit = new ReciprocatedCommit();
        lastReciprocatedCommit.setSourceCommitId("source0");
        lastReciprocatedCommit.setSinkCommitId("sink0");
        UnreciprocatedCommits unreciprocatedCommits = new UnreciprocatedCommits(lastReciprocatedCommit,
                Collections.emptyList(), Collections.emptyList());
        //This should just do nothing, not throw
        branchDomain.checkForAndCreateHistoricBranches(unreciprocatedCommits, Collections.emptyMap(), sink);

        verify(branchDomain, times(0)).createBranchAtHead(any(), any(), any(), any());
    }

    @Test
    public void checkForAndCreateHistoricBranches_Historic_A() {
        ReciprocatedCommit lastReciprocatedCommit = new ReciprocatedCommit();
        lastReciprocatedCommit.setSourceCommitId("source0");
        lastReciprocatedCommit.setSinkCommitId("sink0");
        UnreciprocatedCommits unreciprocatedCommits = new UnreciprocatedCommits(lastReciprocatedCommit,
                Collections.emptyList(), Collections.emptyList());
        Map<String, Collection<Branch>> branches = new HashMap<>();
        Branch historicBranch = new Branch();
        historicBranch.setId("hb1");
        historicBranch.setName("historic branch 1");
        historicBranch.setOriginCommit("source-1");
        historicBranch.setParentBranchId("master");
        branches.computeIfAbsent(historicBranch.getOriginCommit(), k -> new ArrayList<>()).add(historicBranch);

        //Variant A: sink cannot create historic branches
        when(sink.canCreateHistoricBranches()).thenReturn(false);

        //Run test
        branchDomain.checkForAndCreateHistoricBranches(unreciprocatedCommits, branches, sink);

        verify(sink).canCreateHistoricBranches();
        verify(branchDomain, times(0)).createBranchAtHead(any(), any(), any(), any());
    }

    @Test
    public void checkForAndCreateHistoricBranches_Historic_B() {
        ReciprocatedCommit lastReciprocatedCommit = new ReciprocatedCommit();
        lastReciprocatedCommit.setSourceCommitId("source0");
        lastReciprocatedCommit.setSinkCommitId("sink0");
        UnreciprocatedCommits unreciprocatedCommits = new UnreciprocatedCommits(lastReciprocatedCommit,
                Collections.emptyList(), Collections.emptyList());
        Map<String, Collection<Branch>> branches = new HashMap<>();
        Branch historicBranch = new Branch();
        historicBranch.setId("hb1");
        historicBranch.setName("historic branch 1");
        historicBranch.setOriginCommit("source-1");
        historicBranch.setParentBranchId("master");
        branches.computeIfAbsent(historicBranch.getOriginCommit(), k -> new ArrayList<>()).add(historicBranch);

        //Variant B: sink can create historic branches
        when(sink.canCreateHistoricBranches()).thenReturn(true);
        //Run test
        try {
            branchDomain.checkForAndCreateHistoricBranches(unreciprocatedCommits, branches, sink);
            fail("Expected to throw"); //TODO update when implemented
        } catch(RuntimeException ex) {}

        verify(sink).canCreateHistoricBranches();
        verify(branchDomain, times(0)).createBranchAtHead(any(), any(), any(), any());
    }

    @Test
    public void checkForAndCreateHistoricBranches_AtCurrentCommit_A() {
        //Variant A: sink cannot create historic branches, unreciprocated sink commits is not empty
        when(sink.canCreateHistoricBranches()).thenReturn(false);
        List<Commit> unreciprocatedSinkCommits = List.of(new Commit());

        //Setup
        ReciprocatedCommit lastReciprocatedCommit = new ReciprocatedCommit();
        lastReciprocatedCommit.setSourceCommitId("source0");
        lastReciprocatedCommit.setSinkCommitId("sink0");
        UnreciprocatedCommits unreciprocatedCommits = new UnreciprocatedCommits(lastReciprocatedCommit,
                Collections.emptyList(), unreciprocatedSinkCommits);
        Map<String, Collection<Branch>> branches = new HashMap<>();
        Branch historicBranch = new Branch();
        historicBranch.setId("hb1");
        historicBranch.setName("historic branch 1");
        historicBranch.setOriginCommit("source-0");
        historicBranch.setParentBranchId("master");
        branches.computeIfAbsent(historicBranch.getOriginCommit(), k -> new ArrayList<>()).add(historicBranch);

        //Run test
        branchDomain.checkForAndCreateHistoricBranches(unreciprocatedCommits, branches, sink);

        verify(sink).canCreateHistoricBranches();
        verify(branchDomain, times(0)).createBranchAtHead(any(), any(), any(), any());
    }

    @Test
    public void checkForAndCreateHistoricBranches_AtCurrentCommit_B() {
        //Variant B: sink can create historic branches, unreciprocated sink commits is not empty
        when(sink.canCreateHistoricBranches()).thenReturn(true);
        List<Commit> unreciprocatedSinkCommits = List.of(new Commit());

        //Setup
        ReciprocatedCommit lastReciprocatedCommit = new ReciprocatedCommit();
        lastReciprocatedCommit.setSourceCommitId("source0");
        lastReciprocatedCommit.setSinkCommitId("sink0");
        UnreciprocatedCommits unreciprocatedCommits = new UnreciprocatedCommits(lastReciprocatedCommit,
                Collections.emptyList(), unreciprocatedSinkCommits);
        Map<String, Collection<Branch>> branches = new HashMap<>();
        Branch historicBranch = new Branch();
        historicBranch.setId("hb1");
        historicBranch.setName("historic branch 1");
        historicBranch.setOriginCommit("source-0");
        historicBranch.setParentBranchId("master");
        branches.computeIfAbsent(historicBranch.getOriginCommit(), k -> new ArrayList<>()).add(historicBranch);

        //Run test
        try {
            branchDomain.checkForAndCreateHistoricBranches(unreciprocatedCommits, branches, sink);
            fail("Expected to throw"); //TODO update when implemented
        } catch(RuntimeException ex) {}

        verify(sink).canCreateHistoricBranches();
        verify(branchDomain, times(0)).createBranchAtHead(any(), any(), any(), any());
    }

    @Test
    public void checkForAndCreateHistoricBranches_AtCurrentCommit_C() {
        //Variant C: sink cannot create historic branches, unreciprocated sink commits is empty
        when(sink.canCreateHistoricBranches()).thenReturn(false);
        List<Commit> unreciprocatedSinkCommits = Collections.emptyList();

        //Setup
        ReciprocatedCommit lastReciprocatedCommit = new ReciprocatedCommit();
        lastReciprocatedCommit.setSourceCommitId("source0");
        lastReciprocatedCommit.setSinkCommitId("sink0");
        UnreciprocatedCommits unreciprocatedCommits = new UnreciprocatedCommits(lastReciprocatedCommit,
                Collections.emptyList(), unreciprocatedSinkCommits);
        Map<String, Collection<Branch>> branches = new HashMap<>();
        Branch branch = new Branch();
        branch.setId("b1");
        branch.setName("branch 1");
        branch.setOriginCommit("source0");
        branch.setParentBranchId("master");
        branches.computeIfAbsent(branch.getOriginCommit(), k -> new ArrayList<>()).add(branch);

        Commit commit = new Commit();
        commit.setBranchName("master");
        commit.setBranchId("master");
        when(sink.getCommitById("sink0")).thenReturn(commit);

        //Run test
        branchDomain.checkForAndCreateHistoricBranches(unreciprocatedCommits, branches, sink);

        verify(sink, times(0)).canCreateHistoricBranches();
        verify(branchDomain).createBranchAtHead(sink, "master", "master", branch);
    }

    @Test
    public void getNewBranches_Empty() {
        when(source.getBranches()).thenReturn(Collections.emptyList());

        //Run test
        Map<String, Collection<Branch>> branches = branchDomain.getNewBranches(source, sink);

        assertNotNull(branches);
        assertTrue(branches.isEmpty());
    }

    @Test
    public void getNewBranches_New_Normal() {
        Branch branch1 = new Branch();
        branch1.setName("branch 1");
        branch1.setId("id1");
        branch1.setOriginCommit("commit1");
        when(source.getBranches()).thenReturn(List.of(branch1));

        //Run test
        Map<String, Collection<Branch>> branches = branchDomain.getNewBranches(source, sink);

        assertNotNull(branches);
        assertEquals(1, branches.size());
        assertTrue(branches.containsKey(branch1.getOriginCommit()));
    }

    @Test
    public void getNewBranches_New_Error() {
        Branch branch1 = new Branch();
        branch1.setName("branch 1");
        branch1.setId("id1");
        //No origin commit
        when(source.getBranches()).thenReturn(List.of(branch1));

        //Run test
        Map<String, Collection<Branch>> branches = branchDomain.getNewBranches(source, sink);

        assertNotNull(branches);
        assertTrue(branches.isEmpty());
    }

    @Test
    public void getNewBranches_NoNew() {
        Branch branch1 = new Branch();
        branch1.setName("branch 1");
        branch1.setId("id1");

        when(source.getBranches()).thenReturn(List.of(branch1));
        when(sink.getBranchByName("branch 1")).thenReturn(branch1);

        //Run test
        Map<String, Collection<Branch>> branches = branchDomain.getNewBranches(source, sink);

        assertNotNull(branches);
        assertTrue(branches.isEmpty());
    }

    @Test
    public void createBranchAtHead_Cascade() {
        Branch branch0 = new Branch();
        branch0.setName("branch 0");
        branch0.setId("id0");

        Branch branch1 = new Branch();
        branch1.setName("branch 1");
        branch1.setId("id1");

        //Run test
        branchDomain.createBranchAtHead(sink, branch0, branch1);

        verify(branchDomain).createBranchAtHead(sink, "branch 0", "id0", branch1);
    }

    @Test
    public void createBranchAtHead_Normal() {
        Branch branch0 = new Branch();
        branch0.setName("branch 0");
        branch0.setId("id0");

        Branch branch1 = new Branch();
        branch1.setName("branch 1");
        branch1.setId("id1");

        when(sink.createBranch(any())).thenReturn(branch1);

        //Run test
        branchDomain.createBranchAtHead(sink, branch0.getName(), branch0.getId(), branch1);

        verify(sink).createBranch(any());
    }

    @Test
    public void createBranchAtHead_Error() {
        Branch branch0 = new Branch();
        branch0.setName("branch 0");
        branch0.setId("id0");

        Branch branch1 = new Branch();
        branch1.setName("branch 1");
        branch1.setId("id1");

        when(sink.createBranch(any())).thenReturn(null);

        //Run test
        branchDomain.createBranchAtHead(sink, branch0.getName(), branch0.getId(), branch1);

        verify(sink).createBranch(any());
    }
}