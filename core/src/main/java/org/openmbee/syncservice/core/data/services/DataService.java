package org.openmbee.syncservice.core.data.services;

import org.openmbee.syncservice.core.data.jobs.Job;
import org.openmbee.syncservice.core.data.jobs.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openmbee.syncservice.core.constants.SyncServiceConstants;


@Service
public class DataService{

    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    private JobFactory jobFactory;

    @Autowired
    public void setJobFactory(JobFactory jobFactory) {
        this.jobFactory = jobFactory;
    }

    public void receiveMessage(String message) throws Exception {

        logger.debug(SyncServiceConstants.LoggerStatements.METHOD_START_LOG, "receiveMessage()");

        Job job = jobFactory.getJob(message);
        if(job != null) {
            job.execute();
        } else {
            logger.error("Could not create job for message: " + message);
        }

        logger.debug(SyncServiceConstants.LoggerStatements.METHOD_END_LOG, "receiveMessage()");
    }
}

