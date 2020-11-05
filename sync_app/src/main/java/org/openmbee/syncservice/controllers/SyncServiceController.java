package org.openmbee.syncservice.controllers;

import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.queue.dto.QueueDetailsResponseTO;
import org.openmbee.syncservice.core.queue.service.QueuingService;
import org.openmbee.syncservice.core.data.sourcesink.SourceSinkFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

import static org.openmbee.syncservice.core.constants.SyncServiceConstants.LoggerStatements.*;


@RestController
@RequestMapping("api/v1/")
public class SyncServiceController {

    private static final Logger logger = LoggerFactory.getLogger(SyncServiceController.class);

    private QueuingService queuingService;
    private SourceSinkFactory sourceSinkFactory;

    @Autowired
    public void setQueuingService(QueuingService queuingService) {
        this.queuingService = queuingService;
    }

    @Autowired
    public void setSourceSinkFactory(SourceSinkFactory sourceSinkFactory) {
        this.sourceSinkFactory = sourceSinkFactory;
    }

    @PostMapping("syncProject")
    public String syncProject(@RequestBody ProjectSyncRequest projectSyncRequest,
                              HttpServletResponse response) throws Exception {
        logger.debug(METHOD_START_LOG, "syncProject()");
        String syncStatus = null;

        try {
            Source source = sourceSinkFactory.getSource(projectSyncRequest.getSource()).orElse(null);
            Sink sink = sourceSinkFactory.getSink(projectSyncRequest.getSink()).orElse(null);

            if(isValidFlow(source, sink)) {
                queuingService.queueRequest(new JSONObject(projectSyncRequest).toString());
            } else {
                syncStatus = "Invalid sync request.";
                response.setStatus( HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.error(ERROR_LOG, e.getMessage());
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        logger.debug(METHOD_END_LOG, "syncProject()");
        return syncStatus;
    }

    @GetMapping(value = "status", produces = MediaType.APPLICATION_JSON_VALUE)
    public QueueDetailsResponseTO statusQueue() throws Exception {
        QueueDetailsResponseTO queueStatusTO = null;
        queueStatusTO = queuingService.getStatus();
        return queueStatusTO;
    }

    @PostMapping("checkSyncEnabled")
    public Boolean checkSyncEnabled(@RequestBody ProjectSyncRequest projectSyncRequest,
                                           HttpServletResponse response) {
        logger.debug(METHOD_START_LOG, "checkSyncEnabled()");
        String syncStatus = null;

        try {
            Source source = sourceSinkFactory.getSource(projectSyncRequest.getSource()).orElse(null);
            Sink sink = sourceSinkFactory.getSink(projectSyncRequest.getSink()).orElse(null);

            if(isValidFlow(source, sink)) {
                return true;
            }

        } catch (Exception e) {
            logger.error(ERROR_LOG, e.getMessage());
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            logger.debug(METHOD_END_LOG, "checkSyncEnabled()");
        }
        return false;
    }

    private boolean isValidFlow(Source source, Sink sink) {
        return source != null && sink != null && sink.canReceiveFrom(source) && source.canSendTo(sink);
    }
}
