package org.openmbee.syncservice.core.queue.service;

import org.openmbee.syncservice.core.queue.dto.QueueDetailsResponseTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.verify;

public class QueuingServiceTest {

    @Mock
    private Sender sender;

    @InjectMocks
    private QueuingService queuingService;

    @Before
    public void setUp() throws Exception {
        queuingService = new QueuingService() {
            @Override
            public QueueDetailsResponseTO getStatus() {
                return null;
            }
        };
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testQueuingMessage() throws IOException {
        String message = "This is the message";
        queuingService.queueRequest(message);
        verify(sender).send(message);
    }
}