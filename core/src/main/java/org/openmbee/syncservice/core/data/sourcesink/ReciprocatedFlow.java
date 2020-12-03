package org.openmbee.syncservice.core.data.sourcesink;

import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;
import org.openmbee.syncservice.core.data.services.CommitReciprocityService;

public class ReciprocatedFlow extends Flow {

    private CommitReciprocityService commitReciprocityService;

    public ReciprocatedFlow(Source source, Sink sink, CommitReciprocityService commitReciprocityService) {
        super(source, sink);
        this.commitReciprocityService = commitReciprocityService;
    }

    public UnreciprocatedCommits getUnreciprocatedCommits() {
        return commitReciprocityService.getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();
    }

    public void registerReciprocatedCommit(String sourceCommitId, String sinkCommitId) {
        commitReciprocityService.registerReciprocatedCommit(sourceCommitId, sinkCommitId);
    }
}
