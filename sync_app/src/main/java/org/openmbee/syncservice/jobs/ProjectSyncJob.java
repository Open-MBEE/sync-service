package org.openmbee.syncservice.jobs;

import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.branches.BranchDomain;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.commits.CommitDateComparator;
import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;
import org.openmbee.syncservice.core.data.jobs.Job;
import org.openmbee.syncservice.core.data.sourcesink.ReciprocatedFlow;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.exceptions.ProjectSyncOverlapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProjectSyncJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ProjectSyncJob.class);

    private ReciprocatedFlow reciprocatedFlow;
    private BranchDomain branchDomain;

    @Autowired
    public void setBranchDomain(BranchDomain branchDomain) {
        this.branchDomain = branchDomain;
    }

    public ProjectSyncJob(ReciprocatedFlow reciprocatedFlow) {
        this.reciprocatedFlow = reciprocatedFlow;
    }

    public void execute() throws ProjectSyncOverlapException {
        logger.info("Synchronizing " + reciprocatedFlow.getSource().toString()
                + " to " + reciprocatedFlow.getSink().toString());

        Source source = reciprocatedFlow.getSource();
        Sink sink = reciprocatedFlow.getSink();

        // obtains UnreciprocatedCommits, it contains a sorted set of the last reciprocated commits by branch and
        // a sorted set of the source and sink commits that are unreciprocated
        UnreciprocatedCommits unreciprocatedCommits = reciprocatedFlow.getUnreciprocatedCommits();

        if(unreciprocatedCommits != null) {
            //Warn about unreciprocated commits (present in sink, but does not correspond to any in source)
            if(!unreciprocatedCommits.getSinkCommits().isEmpty()) {
                logger.warn(String.format("One way flow is ignoring %d unreciprocated commits in destination endpoint",
                        unreciprocatedCommits.getSinkCommits().size()));
            }

            //Collect new branches
            Map<String, Collection<Branch>> newBranches = branchDomain.getNewBranches(source, sink);
            //Check for new branches associated with past commits
            branchDomain.checkForAndCreateHistoricBranches(unreciprocatedCommits, newBranches, sink);

            //Loop through new commits
            List<Commit> commits = new ArrayList<>(unreciprocatedCommits.getSourceCommits());
            commits.sort(new CommitDateComparator());

            for(Commit commit : commits) {
                Branch sinkBranch = sink.getBranchByName(commit.getBranchName());

                if(sinkBranch == null) {
                    logger.error("Encountered a commit for a branch (" +
                            commit.getBranchName() +
                            ") which does not exist in sink.  Commit id: " + commit.getCommitId());
                    continue;
                }

                CommitChanges commitChanges = source.getCommitChanges(commit);
                List<String> commitIds = sink.commitChanges(source, sinkBranch, commitChanges);
                if(commitIds == null || commitIds.isEmpty()) {
                    logger.warn("Source commit " + commit.getCommitId() + " did not generate a sink commit");
                } else {
                    commitIds.forEach(v -> reciprocatedFlow.registerReciprocatedCommit(commit.getCommitId(), v));
                }

                //Create new branches at current commit if applicable
                if(newBranches.containsKey(commit.getCommitId())) {
                    newBranches.get(commit.getCommitId()).forEach(v -> branchDomain.createBranchAtHead(sink, sinkBranch, v));
                }
            }
        }
    }
}
