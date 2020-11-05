package org.openmbee.syncservice.core.queue.service;

import static org.openmbee.syncservice.core.constants.SyncServiceConstants.LoggerStatements.METHOD_END_LOG;
import static org.openmbee.syncservice.core.constants.SyncServiceConstants.LoggerStatements.METHOD_START_LOG;

import java.io.IOException;

import org.openmbee.syncservice.core.queue.dto.QueueDetailsResponseTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class QueuingService  {

    private static final Logger logger = LoggerFactory.getLogger(QueuingService.class);

    private Sender sender;

    @Autowired
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public String queueRequest(String request) throws IOException {
        logger.debug(METHOD_START_LOG, "queueSyncProject()");

        String pubMsgStatus = sender.send(request);

        logger.debug(METHOD_END_LOG, "queueSyncProject()");
        return pubMsgStatus;
    }

    public abstract QueueDetailsResponseTO getStatus();
}
