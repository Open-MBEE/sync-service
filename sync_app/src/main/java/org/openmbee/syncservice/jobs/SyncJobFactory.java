package org.openmbee.syncservice.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmbee.syncservice.controllers.ProjectSyncRequest;
import org.openmbee.syncservice.core.data.jobs.Job;
import org.openmbee.syncservice.core.data.jobs.JobFactory;
import org.openmbee.syncservice.core.data.sourcesink.ReciprocatedFlow;
import org.openmbee.syncservice.core.data.sourcesink.SourceSinkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SyncJobFactory implements JobFactory {
    private static final Logger logger = LoggerFactory.getLogger(SyncJobFactory.class);

    private SourceSinkFactory sourceSinkFactory;
    private ApplicationContext applicationContext;

    @Autowired
    public void setSourceSinkFactory(SourceSinkFactory sourceSinkFactory) {
        this.sourceSinkFactory = sourceSinkFactory;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Job getJob(String jobRequest) {
        if(jobRequest == null) {
            return null;
        }

        ObjectMapper m = new ObjectMapper();
        try {
            ProjectSyncRequest projectSyncRequest = m.readValue(jobRequest, ProjectSyncRequest.class);
            Optional<ReciprocatedFlow> flow = sourceSinkFactory.getReciprocatedFlow(projectSyncRequest.getSource(),
                    projectSyncRequest.getSink());

            if(flow.isPresent()) {
                return autowire(new ProjectSyncJob(flow.get()));
            }
        } catch (Exception ex) {
            logger.debug("Job is not a ProjectSyncJob");
        }
        return null;
    }

    private <T> T autowire(T object) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
        return object;
    }
}
