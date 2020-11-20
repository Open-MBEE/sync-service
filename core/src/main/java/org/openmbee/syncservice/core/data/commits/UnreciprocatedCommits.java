package org.openmbee.syncservice.core.data.commits;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class UnreciprocatedCommits {
    private SortedSet<ReciprocatedCommit> lastReciprocatedCommitSet;
    private SortedSet<Commit> sourceCommits;
    private SortedSet<Commit> sinkCommits;

    public UnreciprocatedCommits() {
        this.lastReciprocatedCommitSet = new TreeSet<>();
        this.sourceCommits = new TreeSet<>();
        this.sinkCommits = new TreeSet<>();
    }

    @Override
    public final boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(!(o instanceof UnreciprocatedCommits)) {
            return false;
        }
        UnreciprocatedCommits unreciprocatedCommits = (UnreciprocatedCommits) o;
        return this.getLastReciprocatedCommitSet().equals(unreciprocatedCommits.getLastReciprocatedCommitSet());
    }

    @Override
    public final int hashCode() {
        return this.getLastReciprocatedCommitSet() != null ? this.getLastReciprocatedCommitSet().hashCode() : -1;
    }

    public SortedSet<ReciprocatedCommit> getLastReciprocatedCommitSet() {
        return lastReciprocatedCommitSet;
    }

    /**
     * Assumes the developer has checked to ensure this is the last reciprocated commit of a particular branch.
     *
     * @param lastReciprocatedCommit
     */
    public void addLastReciprocatedCommit(ReciprocatedCommit lastReciprocatedCommit) {
        this.lastReciprocatedCommitSet.add(lastReciprocatedCommit);
    }

    public SortedSet<Commit> getSourceCommits() {
        return sourceCommits;
    }

    public void addSourceCommits(Collection<Commit> sourceCommits) {
        this.sourceCommits.addAll(sourceCommits);
    }

    public SortedSet<Commit> getSinkCommits() {
        return sinkCommits;
    }

    public void addSinkCommits(Collection<Commit> sinkCommits) {
        this.sinkCommits.addAll(sinkCommits);
    }
}
