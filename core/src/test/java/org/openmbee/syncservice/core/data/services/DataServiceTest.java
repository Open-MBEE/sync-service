package org.openmbee.syncservice.core.data.services;

import org.openmbee.syncservice.core.data.jobs.Job;
import org.openmbee.syncservice.core.data.jobs.JobFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class DataServiceTest {

    @Mock
    private JobFactory jobFactory;

    @Spy
    @InjectMocks
    private DataService dataService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void receiveMessageTestNormal() throws Exception {
        Job mockJob = mock(Job.class);
        String message = "Message1";
        when(jobFactory.getJob(message)).thenReturn(mockJob);

        dataService.receiveMessage(message);
        verify(mockJob).execute();
    }

    @Test
    public void receiveMessageNoJob() {
        String message = "Message1";

        try {
            dataService.receiveMessage(message);
        } catch(Exception ex) {
            fail("Should not throw");
        }
    }

    @Test
    public void receiveMessageJobThrows() throws Exception {
        Job mockJob = mock(Job.class);
        String message = "Message1";
        when(jobFactory.getJob(message)).thenReturn(mockJob);
        doThrow(new Exception()).when(mockJob).execute();

        try {
            dataService.receiveMessage(message);
            fail("Should throw");
        } catch(Exception ex) {

        }
    }
}