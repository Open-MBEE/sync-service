package org.openmbee.syncservice.core.data.sourcesink;

import org.openmbee.syncservice.core.data.services.CommitReciprocityService;

public interface ISourceSinkFactory {
    Sink getSink(ProjectEndpoint sinkEndpoint);
    Source getSource(ProjectEndpoint sourceEndpoint);
    CommitReciprocityService getCommitReciprocityService(Source source, Sink sink);
}
