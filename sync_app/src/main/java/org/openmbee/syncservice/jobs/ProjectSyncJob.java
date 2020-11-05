package org.openmbee.syncservice.jobs;

import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.commits.CommitDateComparator;
import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;
import org.openmbee.syncservice.core.data.common.Branch;
import org.openmbee.syncservice.core.data.jobs.Job;
import org.openmbee.syncservice.core.data.sourcesink.ReciprocatedFlow;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.exceptions.ProjectSyncOverlapException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProjectSyncJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(ProjectSyncJob.class);

    private ReciprocatedFlow reciprocatedFlow;

    public ProjectSyncJob(ReciprocatedFlow reciprocatedFlow) {
        this.reciprocatedFlow = reciprocatedFlow;
    }

    public void execute() throws ProjectSyncOverlapException {
        logger.info("Synchronizing " + reciprocatedFlow.getSource().toString()
                + " to " + reciprocatedFlow.getSink().toString());

        Source source = reciprocatedFlow.getSource();
        Sink sink = reciprocatedFlow.getSink();

        UnreciprocatedCommits unreciprocatedCommits = reciprocatedFlow.getUnreciprocatedCommits();

        //Warn about unreciprocated commits (present in sink, but does not correspond to any in source)
        if(! unreciprocatedCommits.getSinkCommits().isEmpty()) {
            logger.warn(String.format("One way flow is ignoring %d unreciprocated commits in destination endpoint",
                    unreciprocatedCommits.getSinkCommits().size()));
        }

        //Loop through missing commits
        List<Commit> commits = new ArrayList<>(unreciprocatedCommits.getSourceCommits());
        commits.sort(new CommitDateComparator());
        for(Commit commit : commits) {
            Branch sinkBranch = sink.getBranchByName(commit.getBranchName());

            if(sinkBranch == null) {
                Branch sourceBranch = source.getBranch(commit.getBranchId());
                //TODO create new branch instead of skipping commit
                continue;
            }

            CommitChanges commitChanges = source.getCommitChanges(commit);
            sink.commitChanges(source, sinkBranch, commitChanges);
        }
    }

}
