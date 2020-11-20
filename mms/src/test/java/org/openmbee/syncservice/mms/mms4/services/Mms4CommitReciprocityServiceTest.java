package org.openmbee.syncservice.mms.mms4.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.ReciprocatedCommit;
import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.mms.mms4.sourcesink.Mms4Sink;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class Mms4CommitReciprocityServiceTest {

    @Mock
    private Source source;
    @Mock
    private Mms4Sink sink;
    @Spy
    @InjectMocks
    private Mms4CommitReciprocityService mms4CommitReciprocityService;

    private Commit commita1, commita2, commita3, commitb1, commitb2, commitb3, commitb4, commitc1, commitc2, commitc3,
            commitd1, commitd2, commitd3, commitd4;
    private Branch brancha;
    private ReciprocatedCommit rca;
    private Map<Branch, ReciprocatedCommit> bRcMap;
    private SortedSet<ReciprocatedCommit> reciprocatedCommits;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ZonedDateTime t1 = ZonedDateTime.now();
        commita1 = createCommit("a1", t1, "a", "a", "-1");
        commita2 = createCommit("a2", t1, "a", "a", "a1");
        commita3 = createCommit("a3", t1, "a", "a", "a2");

        brancha = new Branch();
        brancha.setId("a");
        brancha.setName("a");
        brancha.setOriginCommit("a1");
        rca = new ReciprocatedCommit();
        rca.setSourceCommitId("a2");
        rca.setSinkCommitId("b2");
        rca.setCommitDate(commita2.getCommitDate());

        ZonedDateTime t2 = ZonedDateTime.now();
        commitc1 = createCommit("c1", t2, "c", "c", "-1");
        commitc2 = createCommit("c2", t2, "c", "c", "c1");
        commitc3 = createCommit("c3", t2, "c", "c", "c2");

        bRcMap = new HashMap<>();
        reciprocatedCommits = new TreeSet<>();
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestHasUnreciprocated() {
        commitb1 = createCommit("b1", ZonedDateTime.now(), "a", "a", "-1");
        commitb2 = createCommit("b2", ZonedDateTime.now(), "a", "a", "b1");
        commitb3 = createCommit("b3", ZonedDateTime.now(), "a", "a", "b2");
        commitb4 = createCommit("b4", ZonedDateTime.now(), "a", "a", "b3");

        commitd1 = createCommit("d1", ZonedDateTime.now(), "c", "c", "-1");
        commitd2 = createCommit("d2", ZonedDateTime.now(), "c", "c", "d1");
        commitd3 = createCommit("d3", ZonedDateTime.now(), "c", "c", "d2");
        commitd4 = createCommit("d4", ZonedDateTime.now(), "c", "c", "d3");

        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1, commitc3, commitc2, commitc1));
        when(sink.getCommitHistory()).thenReturn(List.of(commitb4, commitb3, commitb2, commitb1, commitd4, commitd3, commitd2, commitd1));

        Branch branchc = new Branch();
        branchc.setId("c");
        branchc.setName("c");
        branchc.setOriginCommit("c1");
        ReciprocatedCommit rcc = new ReciprocatedCommit();
        rcc.setSourceCommitId("c2");
        rcc.setSinkCommitId("d2");
        rcc.setCommitDate(commitc2.getCommitDate());

        bRcMap.put(brancha, rca);
        bRcMap.put(branchc, rcc);
        reciprocatedCommits.add(rca);
        reciprocatedCommits.add(rcc);
        when(sink.getLatestReciprocatedCommitMapByBranch()).thenReturn(bRcMap);

        UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();

        assertNotNull(unreciprocatedCommits);
        assertFalse(unreciprocatedCommits.getLastReciprocatedCommitSet().isEmpty());
        assertEquals(2, unreciprocatedCommits.getLastReciprocatedCommitSet().size());
        assertFalse(unreciprocatedCommits.getSinkCommits().isEmpty());
        assertFalse(unreciprocatedCommits.getSourceCommits().isEmpty());
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestSinkEmpty() {
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1, commitc3, commitc2, commitc1));
        when(sink.getCommitHistory()).thenReturn(List.of());

        bRcMap.put(brancha, null);
        when(sink.getLatestReciprocatedCommitMapByBranch()).thenReturn(bRcMap);

        UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();

        assertNotNull(unreciprocatedCommits);
        assertTrue(unreciprocatedCommits.getLastReciprocatedCommitSet().isEmpty());
        assertEquals(3, unreciprocatedCommits.getSourceCommits().size());
        assertEquals(0, unreciprocatedCommits.getSinkCommits().size());
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestSinkCameoEmpty() {
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1, commitc3, commitc2, commitc1));

        commitb1 = createCommit("b1", ZonedDateTime.now(), "a", "a", "-1");
        commitd1 = createCommit("d1", ZonedDateTime.now(), "c", "c", "-1");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb1, commitd1));

        bRcMap.put(brancha, null);
        when(sink.getLatestReciprocatedCommitMapByBranch()).thenReturn(bRcMap);
        when(sink.getProjectSchema()).thenReturn("cameo");

        UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();

        assertNotNull(unreciprocatedCommits);
        assertTrue(unreciprocatedCommits.getLastReciprocatedCommitSet().isEmpty());
        assertEquals(3, unreciprocatedCommits.getSourceCommits().size());
        assertEquals(1, unreciprocatedCommits.getSinkCommits().size());
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestBadState() {
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1, commitc3, commitc2, commitc1));

        commitb1 = createCommit("b1", ZonedDateTime.now(), "a", "a", "-1");
        commitb2 = createCommit("b2", ZonedDateTime.now(), "a", "a", "b1");
        commitb3 = createCommit("b3", ZonedDateTime.now(), "a", "a", "b2");
        commitb4 = createCommit("b4", ZonedDateTime.now(), "a", "a", "b3");
        commitd1 = createCommit("d1", ZonedDateTime.now(), "c", "c", "-1");
        commitd2 = createCommit("d2", ZonedDateTime.now(), "c", "c", "d1");
        commitd3 = createCommit("d3", ZonedDateTime.now(), "c", "c", "d2");
        commitd4 = createCommit("d4", ZonedDateTime.now(), "c", "c", "d3");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb4, commitb3, commitb2, commitb1, commitd4, commitd3, commitd2, commitd1));

        ReciprocatedCommit rc = new ReciprocatedCommit();
        rc.setSourceCommitId("a2");
        rc.setSinkCommitId("doesn't exist");
        rc.setCommitDate(ZonedDateTime.now());
        bRcMap.put(brancha, rc);
        reciprocatedCommits.add(rc);
        when(sink.getLatestReciprocatedCommitMapByBranch()).thenReturn(bRcMap);

        try {
            UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                    .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();
            fail("Should have thrown");
        } catch(Exception ex) {}
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestTrunkBranch() {
        // source commits can have trunk, sink can have master, they should both be accepted
        ZonedDateTime t1 = ZonedDateTime.now();
        commita1 = createCommit("a1", t1, "trunk", "a", "-1");
        commita2 = createCommit("a2", t1, "trunk", "a", "a1");
        commita3 = createCommit("a3", t1, "trunk", "a", "a2");
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1));
        commitb1 = createCommit("b1", t1, "master", "a", "-1");
        commitb2 = createCommit("b2", t1, "master", "a", "b1");
        commitb3 = createCommit("b3", t1, "master", "a", "b2");
        commitb4 = createCommit("b4", t1, "master", "a", "b3");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb4, commitb3, commitb2, commitb1));

        brancha.setId("master"); // branch should come from sink
        brancha.setName("a");
        brancha.setOriginCommit("a1");
        rca.setSourceCommitId("a2");
        rca.setSinkCommitId("b2");
        rca.setCommitDate(commita2.getCommitDate());
        bRcMap.put(brancha, rca);
        reciprocatedCommits.add(rca);
        when(sink.getLatestReciprocatedCommitMapByBranch()).thenReturn(bRcMap);

        UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();

        assertNotNull(unreciprocatedCommits);
        assertFalse(unreciprocatedCommits.getLastReciprocatedCommitSet().isEmpty());
        assertFalse(unreciprocatedCommits.getSinkCommits().isEmpty());
        assertFalse(unreciprocatedCommits.getSourceCommits().isEmpty());
        assertEquals(1, unreciprocatedCommits.getSourceCommits().size());
        assertEquals(2, unreciprocatedCommits.getSinkCommits().size());
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestNullMap() {
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1, commitc3, commitc2, commitc1));

        commitb1 = createCommit("b1", ZonedDateTime.now(), "a", "a", "-1");
        commitd1 = createCommit("d1", ZonedDateTime.now(), "c", "c", "-1");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb1, commitd1));
        when(sink.getLatestReciprocatedCommitMapByBranch()).thenReturn(null);

        try {
            UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                    .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();
            fail("Should have thrown");
        } catch(Exception ex) {}
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestEmptyMap() {
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1, commitc3, commitc2, commitc1));

        commitb1 = createCommit("b1", ZonedDateTime.now(), "a", "a", "-1");
        commitd1 = createCommit("d1", ZonedDateTime.now(), "c", "c", "-1");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb1, commitd1));
        when(sink.getLatestReciprocatedCommitMapByBranch()).thenReturn(new HashMap<>());

        try {
            UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                    .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();
            fail("Should have thrown");
        } catch(Exception ex) {}
    }

    private Commit createCommit(String id, ZonedDateTime date, String branchId, String branchName, String parentId) {
        Commit commit = new Commit();
        commit.setBranchId(branchId);
        commit.setBranchName(branchName);
        commit.setCommitDate(date);
        commit.setCommitId(id);
        commit.setParentCommit(parentId);
        return commit;
    }
}