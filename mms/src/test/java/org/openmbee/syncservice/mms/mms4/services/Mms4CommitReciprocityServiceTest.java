package org.openmbee.syncservice.mms.mms4.services;

import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.ReciprocatedCommit;
import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.mms.mms4.sourcesink.Mms4Sink;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Date;
import java.util.List;

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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestHasUnreciprocated() {
        Commit commita1 = createCommit("a1", new Date(), "a", "a", "-1");
        Commit commita2 = createCommit("a2", new Date(), "a", "a", "a1");
        Commit commita3 = createCommit("a3", new Date(), "a", "a", "a2");
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1));

        Commit commitb1 = createCommit("b1", new Date(), "b", "b", "-1");
        Commit commitb2 = createCommit("b2", new Date(), "b", "b", "b1");
        Commit commitb3 = createCommit("b3", new Date(), "b", "b", "b2");
        Commit commitb4 = createCommit("b4", new Date(), "b", "b", "b3");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb4, commitb3, commitb2, commitb1));

        ReciprocatedCommit rc = new ReciprocatedCommit();
        rc.setForeignCommitId("a2");
        rc.setLocalCommitId("b2");
        when(sink.getLatestReciprocatedCommit()).thenReturn(rc);

        UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();

        assertNotNull(unreciprocatedCommits);
        assertEquals(1, unreciprocatedCommits.getSourceCommits().size());
        assertEquals(2, unreciprocatedCommits.getSinkCommits().size());
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestSinkEmpty() {
        Commit commita1 = createCommit("a1", new Date(), "a", "a", "-1");
        Commit commita2 = createCommit("a2", new Date(), "a", "a", "a1");
        Commit commita3 = createCommit("a3", new Date(), "a", "a", "a2");
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1));
        when(sink.getCommitHistory()).thenReturn(List.of());
        when(sink.getLatestReciprocatedCommit()).thenReturn(null);

        UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();

        assertNotNull(unreciprocatedCommits);
        assertEquals(3, unreciprocatedCommits.getSourceCommits().size());
        assertEquals(0, unreciprocatedCommits.getSinkCommits().size());
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestSinkCameoEmpty() {
        Commit commita1 = createCommit("a1", new Date(), "a", "a", "-1");
        Commit commita2 = createCommit("a2", new Date(), "a", "a", "a1");
        Commit commita3 = createCommit("a3", new Date(), "a", "a", "a2");
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1));

        Commit commitb1 = createCommit("b1", new Date(), "b", "b", "-1");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb1));
        when(sink.getLatestReciprocatedCommit()).thenReturn(null);
        when(sink.getProjectSchema()).thenReturn("cameo");

        UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();

        assertNotNull(unreciprocatedCommits);
        assertEquals(3, unreciprocatedCommits.getSourceCommits().size());
        assertEquals(1, unreciprocatedCommits.getSinkCommits().size());
    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestSinkNotEmpty2() {
        Commit commita1 = createCommit("a1", new Date(), "a", "a", "-1");
        Commit commita2 = createCommit("a2", new Date(), "a", "a", "a1");
        Commit commita3 = createCommit("a3", new Date(), "a", "a", "a2");
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1));

        Commit commitb1 = createCommit("b1", new Date(), "b", "b", "-1");
        Commit commitb2 = createCommit("b2", new Date(), "b", "b", "b1");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb2, commitb1));
        when(sink.getLatestReciprocatedCommit()).thenReturn(null);
        when(sink.getProjectSchema()).thenReturn("default");

        try {
            UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                    .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();
            fail("Should have thrown");
        } catch(Exception ex) {}

    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestSinkNotEmpty() {
        Commit commita1 = createCommit("a1", new Date(), "a", "a", "-1");
        Commit commita2 = createCommit("a2", new Date(), "a", "a", "a1");
        Commit commita3 = createCommit("a3", new Date(), "a", "a", "a2");
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1));

        Commit commitb1 = createCommit("b1", new Date(), "b", "b", "-1");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb1));
        when(sink.getLatestReciprocatedCommit()).thenReturn(null);
        when(sink.getProjectSchema()).thenReturn("default");

        try {
            UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                    .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();
            fail("Should have thrown");
        } catch(Exception ex) {}

    }

    @Test
    public void getUnreciprocatedSourceCommitsSinceLastReciprocatedCommitTestBadState() {
        Commit commita1 = createCommit("a1", new Date(), "a", "a", "-1");
        Commit commita2 = createCommit("a2", new Date(), "a", "a", "a1");
        Commit commita3 = createCommit("a3", new Date(), "a", "a", "a2");
        when(source.getCommitHistory()).thenReturn(List.of(commita3, commita2, commita1));

        Commit commitb1 = createCommit("b1", new Date(), "b", "b", "-1");
        Commit commitb2 = createCommit("b2", new Date(), "b", "b", "b1");
        Commit commitb3 = createCommit("b3", new Date(), "b", "b", "b2");
        Commit commitb4 = createCommit("b4", new Date(), "b", "b", "b3");
        when(sink.getCommitHistory()).thenReturn(List.of(commitb4, commitb3, commitb2, commitb1));

        ReciprocatedCommit rc = new ReciprocatedCommit();
        rc.setForeignCommitId("a2");
        rc.setLocalCommitId("doesn't exist");
        when(sink.getLatestReciprocatedCommit()).thenReturn(rc);

        try {
            UnreciprocatedCommits unreciprocatedCommits = mms4CommitReciprocityService
                    .getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();
            fail("Should have thrown");
        } catch(Exception ex) {}

    }

    private Commit createCommit(String id, Date date, String branchId, String branchName, String parentId) {
        Commit commit = new Commit();
        commit.setBranchId(branchId);
        commit.setBranchName(branchName);
        commit.setCommitDate(date);
        commit.setCommitId(id);
        commit.setParentCommit(parentId);
        return commit;
    }
}