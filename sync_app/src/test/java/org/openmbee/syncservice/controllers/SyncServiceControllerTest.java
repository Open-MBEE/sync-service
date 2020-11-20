package org.openmbee.syncservice.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.core.data.sourcesink.Flow;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.data.sourcesink.SourceSinkFactory;
import org.openmbee.syncservice.core.queue.service.QueuingService;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SyncServiceControllerTest {

    @Mock
    private QueuingService queuingService;
    @Mock
    private SourceSinkFactory sourceSinkFactory;
    @Spy
    @InjectMocks
    private SyncServiceController syncServiceController;

    @Mock
    private Flow flow;
    @Mock
    private HttpServletResponse response;

    private ProjectSyncRequest syncRequest;
    private ProjectEndpoint sourceEndpoint;
    private ProjectEndpoint sinkEndpoint;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        syncRequest = new ProjectSyncRequest();
        sourceEndpoint = new ProjectEndpoint();
        sourceEndpoint.setHost("sourceHost");
        sourceEndpoint.setCollection("sourceCollection");
        sourceEndpoint.setProject("sourceProject");
        sourceEndpoint.setToken("sourceToken");
        sinkEndpoint = new ProjectEndpoint();
        sinkEndpoint.setHost("sinkHost");
        sinkEndpoint.setCollection("sinkCollection");
        sinkEndpoint.setProject("sinkProject");
        sinkEndpoint.setToken("sinkToken");
        syncRequest.setSource(sourceEndpoint);
        syncRequest.setSink(sinkEndpoint);
    }

    @Test
    public void syncProject_Normal() {
        when(sourceSinkFactory.getFlow(sourceEndpoint, sinkEndpoint)).thenReturn(Optional.of(flow));

        try {
            syncServiceController.syncProject(syncRequest, response);

            verify(queuingService).queueRequest(any());
            verify(response, times(0)).setStatus(anyInt());
        } catch(Exception ex){
            fail("Should not throw");
        }
    }

    @Test
    public void syncProject_NoFlow() {
        when(sourceSinkFactory.getFlow(sourceEndpoint, sinkEndpoint)).thenReturn(Optional.empty());

        try {
            syncServiceController.syncProject(syncRequest, response);

            verify(queuingService, times(0)).queueRequest(any());
            verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch(Exception ex){
            fail("Should not throw");
        }
    }

    @Test
    public void syncProject_Exception() {
        when(sourceSinkFactory.getFlow(sourceEndpoint, sinkEndpoint)).thenThrow(new RuntimeException("Mock exception"));

        try {
            syncServiceController.syncProject(syncRequest, response);

            verify(queuingService, times(0)).queueRequest(any());
            verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch(Exception ex){
            fail("Should not throw");
        }
    }

    @Test
    public void checkSyncEnabled_Normal() {
        when(sourceSinkFactory.getFlow(sourceEndpoint, sinkEndpoint)).thenReturn(Optional.of(flow));

        try {
            Boolean enabled = syncServiceController.checkSyncEnabled(syncRequest, response);

            assertTrue(enabled);
            verify(response, times(0)).setStatus(anyInt());
        } catch(Exception ex){
            fail("Should not throw");
        }
    }

    @Test
    public void checkSyncEnabled_NoFlow() {
        when(sourceSinkFactory.getFlow(sourceEndpoint, sinkEndpoint)).thenReturn(Optional.empty());

        try {
            Boolean enabled = syncServiceController.checkSyncEnabled(syncRequest, response);

            assertFalse(enabled);
            verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch(Exception ex){
            fail("Should not throw");
        }
    }

    @Test
    public void checkSyncEnabled_Exception() {
        when(sourceSinkFactory.getFlow(sourceEndpoint, sinkEndpoint)).thenThrow(new RuntimeException("Mock exception"));

        try {
            Boolean enabled = syncServiceController.checkSyncEnabled(syncRequest, response);

            assertFalse(enabled);
            verify(response, times(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch(Exception ex){
            fail("Should not throw");
        }
    }

}