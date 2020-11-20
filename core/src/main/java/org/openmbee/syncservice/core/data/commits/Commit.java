package org.openmbee.syncservice.core.data.commits;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Commit implements Comparable<Commit> {
    private String commitId;
    private ZonedDateTime commitDate;
    private String branchId;
    private String branchName;
    private String parentCommit;

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public ZonedDateTime getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(ZonedDateTime commitDate) {
        this.commitDate = commitDate;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getParentCommit() {
        return parentCommit;
    }

    public void setParentCommit(String parentCommit) {
        this.parentCommit = parentCommit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commit commit = (Commit) o;
        return commitId.equals(commit.commitId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commitId);
    }

    @Override
    public int compareTo(Commit o) {
        if(this.equals(o)) {
            return 0;
        } else {
            int result = this.getCommitDate().compareTo(o.getCommitDate());

            // handle edge case of dates being the same
            if(result == 0) {
                result = this.getCommitId().compareTo(o.getCommitId());
            }

            if(result == 0) {
                result = this.getBranchId().compareTo(o.getBranchId());
            }

            if(result == 0) {
                result = this.getBranchName().compareTo(o.getBranchName());
            }

            if(result == 0) {
                result = this.getParentCommit().compareTo(o.getParentCommit());
            }
            return result;
        }
    }
}
