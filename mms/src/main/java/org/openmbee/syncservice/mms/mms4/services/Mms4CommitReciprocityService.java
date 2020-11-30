package org.openmbee.syncservice.mms.mms4.services;

import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.ReciprocatedCommit;
import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;
import org.openmbee.syncservice.core.data.services.CommitReciprocityService;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.utils.RestInterface;
import org.openmbee.syncservice.mms.mms4.sourcesink.Mms4Sink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Mms4CommitReciprocityService implements CommitReciprocityService {
    private static final Logger logger = LoggerFactory.getLogger(Mms4CommitReciprocityService.class);

    private Source source;
    private Mms4Sink sink;

    public Mms4CommitReciprocityService(Source source, Mms4Sink sink) {
        this.source = source;
        this.sink = sink;
    }

    @Override
    public UnreciprocatedCommits getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit() {
        //TODO probably want to rework this so we aren't always pulling the full histories, maybe step back through until we find the latest reciprocated commits?
        List<Commit> sourceCommits = source.getCommitHistory();
        List<Commit> sinkCommits = sink.getCommitHistory();

        Map<Branch, ReciprocatedCommit> reciprocatedCommitMapByBranch = sink.getLatestReciprocatedCommitMapByBranch();

        UnreciprocatedCommits unreciprocatedCommits = new UnreciprocatedCommits();
        if(reciprocatedCommitMapByBranch == null || reciprocatedCommitMapByBranch.isEmpty()) {
            //This is ok if this is a new project
            if(sinkCommits.isEmpty() || (sinkCommits.size() == 1 && "cameo".equals(sink.getProjectSchema())) ) {
                unreciprocatedCommits.addSourceCommits(sourceCommits);
                unreciprocatedCommits.addSinkCommits(sinkCommits);
            } else {
                throw new RuntimeException("Endpoints have no reciprocated commits");
            }
        }

        // UnreciprocatedCommits has SortedSet variables inside to prevent duplicates and allow ordering
        reciprocatedCommitMapByBranch.forEach((b, r) -> {
            // only use commits belonging to the current branch, should prevent re-used commits
            List<Commit> branchSourceCommits = trimByBranchName(sourceCommits, getSourceBranchName(b.getName()));
            List<Commit> branchSinkCommits = trimByBranchId(sinkCommits, b.getId());
            if(r != null) {
                unreciprocatedCommits.addSourceCommits(trimAtCommit(branchSourceCommits, r.getSourceCommitId()));
                unreciprocatedCommits.addSinkCommits(trimAtCommit(branchSinkCommits, r.getSinkCommitId()));
                unreciprocatedCommits.addLastReciprocatedCommit(r);
            } else { // if the sink project is empty this is still ok, but there's no reciprocated commit to add
                if(branchSinkCommits.isEmpty() || (branchSinkCommits.size() == 1 && "cameo".equals(sink.getProjectSchema())) ) {
                    unreciprocatedCommits.addSourceCommits(branchSourceCommits);
                    unreciprocatedCommits.addSinkCommits(branchSinkCommits);
                } else {
                    logger.warn("Ignoring branch " + b.getName() + " because it lacks unreciprocated commits.");
                }
            }
        });

        return unreciprocatedCommits;
    }

    private String getSourceBranchName(String sinkBranchName) {
        //TODO: make this generic - this is just a hack to get it to work for now
        if("master".equals(sinkBranchName)) {
            return "trunk";
        }
        return sinkBranchName;
    }

    @Override
    public void registerReciprocatedCommit(String sourceCommitId, String sinkCommitId) {
        sink.registerReciprocatedCommit(sourceCommitId, sinkCommitId);
    }

    private List<Commit> trimByBranchId(List<Commit> list, String branchId) {
        List<Commit> trimmed = new ArrayList<>();
        for(Commit c : list) {
            if(c.getBranchId().equals(branchId)) {
                trimmed.add(c);
            }
        }
        return trimmed;
    }

    private List<Commit> trimByBranchName(List<Commit> list, String branchName) {
        List<Commit> trimmed = new ArrayList<>();
        for(Commit c : list) {
            if(c.getBranchName().equals(branchName)) {
                trimmed.add(c);
            }
        }
        return trimmed;
    }

    private List<Commit> trimAtCommit(List<Commit> list, String commitId) {
        for (int i = 0; i < list.size(); ++i) {
            if (commitId.equals(list.get(i).getCommitId())) {
                return list.subList(0, i);
            }
        }
        return new ArrayList<>(list);
    }
}
