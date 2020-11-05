package org.openmbee.syncservice.controllers;

import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;

public class ProjectSyncRequest {
    private ProjectEndpoint source;
    private ProjectEndpoint sink;

    public ProjectEndpoint getSource() {
        return source;
    }

    public void setSource(ProjectEndpoint source) {
        this.source = source;
    }

    public ProjectEndpoint getSink() {
        return sink;
    }

    public void setSink(ProjectEndpoint sink) {
        this.sink = sink;
    }
}
