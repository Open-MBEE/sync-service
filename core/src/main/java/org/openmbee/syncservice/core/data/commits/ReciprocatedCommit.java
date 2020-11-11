package org.openmbee.syncservice.core.data.commits;

public class ReciprocatedCommit {
    private String sinkCommitId;
    private String sourceCommitId;

    public String getSinkCommitId() {
        return sinkCommitId;
    }

    public void setSinkCommitId(String sinkCommitId) {
        this.sinkCommitId = sinkCommitId;
    }

    public String getSourceCommitId() {
        return sourceCommitId;
    }

    public void setSourceCommitId(String sourceCommitId) {
        this.sourceCommitId = sourceCommitId;
    }
}
