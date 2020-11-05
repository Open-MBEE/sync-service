package org.openmbee.syncservice.core.data.commits;

public class ReciprocatedCommit {
    private String localCommitId;
    private String foreignCommitId;

    public String getLocalCommitId() {
        return localCommitId;
    }

    public void setLocalCommitId(String localCommitId) {
        this.localCommitId = localCommitId;
    }

    public String getForeignCommitId() {
        return foreignCommitId;
    }

    public void setForeignCommitId(String foreignCommitId) {
        this.foreignCommitId = foreignCommitId;
    }
}
