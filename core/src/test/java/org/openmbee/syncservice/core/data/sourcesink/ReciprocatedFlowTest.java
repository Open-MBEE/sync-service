package org.openmbee.syncservice.core.data.sourcesink;

import org.openmbee.syncservice.core.data.services.CommitReciprocityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class ReciprocatedFlowTest {

    @Mock
    private Source source;
    @Mock
    private Sink sink;
    @Mock
    private CommitReciprocityService commitReciprocityService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void constructTest() {
        ReciprocatedFlow reciprocatedFlow = new ReciprocatedFlow(source, sink, commitReciprocityService);
        assertSame(source, reciprocatedFlow.getSource());
        assertSame(sink, reciprocatedFlow.getSink());
    }

    @Test
    public void getUnreciprocatedCommitsTest() {
        ReciprocatedFlow reciprocatedFlow = new ReciprocatedFlow(source, sink, commitReciprocityService);
        reciprocatedFlow.getUnreciprocatedCommits();
        verify(commitReciprocityService).getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();
    }
}