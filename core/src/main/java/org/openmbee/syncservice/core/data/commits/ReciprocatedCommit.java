package org.openmbee.syncservice.core.data.commits;

import java.time.ZonedDateTime;
import java.util.Objects;

public class ReciprocatedCommit implements Comparable<ReciprocatedCommit> {
    private String sinkCommitId;
    private String sourceCommitId;
    private ZonedDateTime commitDate;

    @Override
    public final boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(!(o instanceof ReciprocatedCommit)) {
            return false;
        }
        ReciprocatedCommit reciprocatedCommit = (ReciprocatedCommit) o;
        return this.getSinkCommitId() != null && this.getSourceCommitId() != null && this.getCommitDate() != null &&
                this.getSinkCommitId().equals(reciprocatedCommit.getSinkCommitId()) &&
                this.getSourceCommitId().equals(reciprocatedCommit.getSourceCommitId()) &&
                this.getCommitDate().equals(reciprocatedCommit.getCommitDate());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.getSinkCommitId(), this.getSourceCommitId(), this.getCommitDate());
    }

    @Override
    public int compareTo(ReciprocatedCommit o) {
        if(this.equals(o)) {
            return 0;
        } else {
            int result = this.getCommitDate().compareTo(o.getCommitDate());

            // handle edge case of dates being the same
            if(result == 0) {
                result = this.getSinkCommitId().compareTo(o.getSinkCommitId());
            }

            if(result == 0) {
                result = this.getSourceCommitId().compareTo(o.getSourceCommitId());
            }
            return result;
        }
    }

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

    public ZonedDateTime getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(ZonedDateTime commitDate) {
        this.commitDate = commitDate;
    }
}
