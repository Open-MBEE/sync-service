package org.openmbee.syncservice.core.data.commits;

import java.util.List;

public class UnreciprocatedCommits {
    private ReciprocatedCommit lastReciprocatedCommit;
    private List<Commit> sourceCommits;
    private List<Commit> sinkCommits;

    public UnreciprocatedCommits(ReciprocatedCommit lastReciprocatedCommit, List<Commit> sourceCommits, List<Commit> sinkCommits) {
        this.lastReciprocatedCommit = lastReciprocatedCommit;
        this.sourceCommits = sourceCommits;
        this.sinkCommits = sinkCommits;
    }

    public ReciprocatedCommit getLastReciprocatedCommit() {
        return lastReciprocatedCommit;
    }

    public void setLastReciprocatedCommit(ReciprocatedCommit lastReciprocatedCommit) {
        this.lastReciprocatedCommit = lastReciprocatedCommit;
    }

    public List<Commit> getSourceCommits() {
        return sourceCommits;
    }

    public void setSourceCommits(List<Commit> sourceCommits) {
        this.sourceCommits = sourceCommits;
    }

    public List<Commit> getSinkCommits() {
        return sinkCommits;
    }

    public void setSinkCommits(List<Commit> sinkCommits) {
        this.sinkCommits = sinkCommits;
    }
}
