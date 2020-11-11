package org.openmbee.syncservice.jobs;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.controllers.ProjectSyncRequest;
import org.openmbee.syncservice.core.data.commits.ReciprocatedCommit;
import org.openmbee.syncservice.core.data.jobs.Job;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.data.sourcesink.ReciprocatedFlow;
import org.openmbee.syncservice.core.data.sourcesink.SourceSinkFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SyncJobFactoryTest {

    @Spy
    @InjectMocks
    private SyncJobFactory syncJobFactory;
    
    @Mock
    private SourceSinkFactory sourceSinkFactory;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private AutowireCapableBeanFactory autowireCapableBeanFactory;
        
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(autowireCapableBeanFactory);
    }
    
    @Test
    public void getJob_Normal() {
        ProjectSyncRequest syncRequest = new ProjectSyncRequest();
        ProjectEndpoint sourceEndpoint = new ProjectEndpoint();
        sourceEndpoint.setHost("sourceHost");
        sourceEndpoint.setCollection("sourceCollection");
        sourceEndpoint.setProject("sourceProject");
        sourceEndpoint.setToken("sourceToken");
        ProjectEndpoint sinkEndpoint = new ProjectEndpoint();
        sinkEndpoint.setHost("sinkHost");
        sinkEndpoint.setCollection("sinkCollection");
        sinkEndpoint.setProject("sinkProject");
        sinkEndpoint.setToken("sinkToken");
        syncRequest.setSource(sourceEndpoint);
        syncRequest.setSink(sinkEndpoint);

        String json = new JSONObject(syncRequest).toString();
        ReciprocatedFlow flow = mock(ReciprocatedFlow.class);
        when(sourceSinkFactory.getReciprocatedFlow(any(), any())).thenReturn(Optional.of(flow));

        Job job = syncJobFactory.getJob(json);

        assertNotNull(job);
        verify(autowireCapableBeanFactory).autowireBean(any());
    }

    @Test
    public void getJob_NoFlow() {
        ProjectSyncRequest syncRequest = new ProjectSyncRequest();
        ProjectEndpoint sourceEndpoint = new ProjectEndpoint();
        sourceEndpoint.setHost("sourceHost");
        sourceEndpoint.setCollection("sourceCollection");
        sourceEndpoint.setProject("sourceProject");
        sourceEndpoint.setToken("sourceToken");
        ProjectEndpoint sinkEndpoint = new ProjectEndpoint();
        sinkEndpoint.setHost("sinkHost");
        sinkEndpoint.setCollection("sinkCollection");
        sinkEndpoint.setProject("sinkProject");
        sinkEndpoint.setToken("sinkToken");
        syncRequest.setSource(sourceEndpoint);
        syncRequest.setSink(sinkEndpoint);

        String json = new JSONObject(syncRequest).toString();

        Job job = syncJobFactory.getJob(json);

        assertNull(job);
    }

    @Test
    public void getJob_NotSyncJob() {
        String json = "{'name':'not a sync job'}";

        Job job = syncJobFactory.getJob(json);

        assertNull(job);
    }

    @Test
    public void getJob_NullRequest() {
        Job job = syncJobFactory.getJob(null);

        assertNull(job);
    }
}