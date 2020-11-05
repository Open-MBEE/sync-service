package org.openmbee.syncservice.core.data.jobs;

public interface JobFactory {
    Job getJob(String jobRequest);
}
