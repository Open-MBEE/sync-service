package org.openmbee.syncservice.core.data.services;

import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;

public interface CommitReciprocityService {

    UnreciprocatedCommits getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();

    void registerReciprocatedCommit(String sourceCommitId, String sinkCommitId);
}
