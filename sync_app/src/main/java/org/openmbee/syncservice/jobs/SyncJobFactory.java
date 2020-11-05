package org.openmbee.syncservice.jobs;

import org.openmbee.syncservice.controllers.ProjectSyncRequest;
import org.openmbee.syncservice.core.data.jobs.Job;
import org.openmbee.syncservice.core.data.jobs.JobFactory;
import org.openmbee.syncservice.core.data.sourcesink.ReciprocatedFlow;
import org.openmbee.syncservice.core.data.sourcesink.SourceSinkFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SyncJobFactory implements JobFactory {
    private static final Logger logger = LoggerFactory.getLogger(SyncJobFactory.class);

    private SourceSinkFactory sourceSinkFactory;

    @Autowired
    public void setSourceSinkFactory(SourceSinkFactory sourceSinkFactory) {
        this.sourceSinkFactory = sourceSinkFactory;
    }

    @Override
    public Job getJob(String jobRequest) {
        ObjectMapper m = new ObjectMapper();
        try {
            ProjectSyncRequest projectSyncRequest = m.readValue(jobRequest.toString(), ProjectSyncRequest.class);
            Optional<ReciprocatedFlow> flow = sourceSinkFactory.getReciprocatedFlow(projectSyncRequest.getSource(),
                    projectSyncRequest.getSink());

            if(flow.isPresent()) {
                return new ProjectSyncJob(flow.get());
            }
        } catch (Exception ex) {
            logger.debug("Job is not a ProjectSyncJob");
        }
        return null;
    }
}
