package org.openmbee.syncservice.controllers;

import org.json.JSONObject;
import org.openmbee.syncservice.core.data.sourcesink.Flow;
import org.openmbee.syncservice.core.data.sourcesink.SourceSinkFactory;
import org.openmbee.syncservice.core.queue.dto.QueueDetailsResponseTO;
import org.openmbee.syncservice.core.queue.service.QueuingService;
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
    public void syncProject(@RequestBody ProjectSyncRequest projectSyncRequest,
                              HttpServletResponse response) {
        logger.debug(METHOD_START_LOG, "syncProject()");
        getFlow(projectSyncRequest, response);
        logger.debug(METHOD_END_LOG, "syncProject()");
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
        Flow flow = getFlow(projectSyncRequest, response);
        logger.debug(METHOD_END_LOG, "checkSyncEnabled()");
        return flow != null;
    }

    private Flow getFlow(ProjectSyncRequest projectSyncRequest, HttpServletResponse response) {
        try {
            Flow flow = sourceSinkFactory.getFlow(projectSyncRequest.getSource(), projectSyncRequest.getSink()).orElse(null);
            if(flow != null) {
                queuingService.queueRequest(new JSONObject(projectSyncRequest).toString());
            } else {
                response.setStatus( HttpServletResponse.SC_BAD_REQUEST);
            }
            return flow;
        } catch (Exception e) {
            logger.error(ERROR_LOG, e.getMessage());
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}
