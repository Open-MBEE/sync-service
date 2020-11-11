package org.openmbee.syncservice.core.data.branches;

import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BranchDomain {
    private static final Logger logger = LoggerFactory.getLogger(BranchDomain.class);

    public void checkForAndCreateHistoricBranches(UnreciprocatedCommits unreciprocatedCommits,
                                                  Map<String, Collection<Branch>> newBranches, Sink sink) {

        if(unreciprocatedCommits.getLastReciprocatedCommit() == null) {
            return;
        }

        //Check for branches created at a point before current commit
        Map<String, Commit> recentCommits = unreciprocatedCommits.getSourceCommits().stream()
                .collect(Collectors.toMap(Commit::getCommitId, v -> v));
        List<Branch> newHistoricBranches = newBranches.entrySet().stream()
                .filter(v -> !recentCommits.containsKey(v.getKey()))
                .filter(v -> !unreciprocatedCommits.getLastReciprocatedCommit().getSourceCommitId().equals(v.getKey()))
                .flatMap(v -> v.getValue().stream()).collect(Collectors.toList());

        if(!newHistoricBranches.isEmpty()) {
            if (sink.canCreateHistoricBranches()) {
                //TODO: implement this once it's applicable
                throw new RuntimeException("Historic branch creation is not implemented yet.");
            } else {
                newHistoricBranches.forEach(v -> {
                    logger.warn("Ignoring missing branch related to past commit: " + v.getName());
                });
            }
        }

        //Check for new branches created at current commit
        if (newBranches.containsKey(unreciprocatedCommits.getLastReciprocatedCommit().getSourceCommitId())) {

            Collection<Branch> branches = newBranches.get(unreciprocatedCommits.getLastReciprocatedCommit()
                    .getSourceCommitId());
            //Check if there have been commits in the sink, if so these branches are historic branches now
            if (!unreciprocatedCommits.getSinkCommits().isEmpty()) {

                if (sink.canCreateHistoricBranches()) {
                    //TODO: implement this once it's applicable
                    throw new RuntimeException("Historic branch creation is not implemented yet.");
                } else {
                    logger.warn("Branch(es) were created in the source since the last sync, " +
                            "but there have been commits on the sink side that make automatic branch " +
                            "creation not possible: " +
                            branches.stream().map(Branch::getName).collect(Collectors.joining(", ")));
                }
            } else {
                //New branches can be created @ current commit
                branches.forEach(v -> {
                    Commit latestSinkCommit = sink.getCommitById(unreciprocatedCommits.getLastReciprocatedCommit()
                            .getSinkCommitId());
                    createBranchAtHead(sink, latestSinkCommit.getBranchName(), latestSinkCommit.getBranchId(), v);
                });
            }
        }
    }

    public Map<String, Collection<Branch>> getNewBranches(Source source, Sink sink) {
        List<Branch> branches = source.getBranches();
        Map<String, Collection<Branch>> newBranches = new HashMap<>();
        for(Branch sourceBranch : branches) {
            Branch sinkBranch = sink.getBranchByName(sourceBranch.getName());
            if(sinkBranch == null) {
                if(sourceBranch.getOriginCommit() == null) {
                    logger.error("Encountered a branch that does not already exist in sink " +
                            "and source branch does not have an origin commit: " + sourceBranch.getName());
                    continue;
                }
                Collection<Branch> branchesAtCommit = newBranches.computeIfAbsent(sourceBranch.getOriginCommit(),
                        k -> new ArrayList<>());
                branchesAtCommit.add(sourceBranch);
            }
        }
        return newBranches;
    }

    public void createBranchAtHead(Sink sink, Branch sinkBranch, Branch newBranch) {
        createBranchAtHead(sink, sinkBranch.getName(), sinkBranch.getId(), newBranch);
    }

    public void createBranchAtHead(Sink sink, String parentSinkBranchName, String parentSinkBranchId, Branch branch) {
        BranchCreateRequest createRequest = new BranchCreateRequest(parentSinkBranchName, parentSinkBranchId,
                branch.getName(), branch.getId());
        Branch createdBranch = sink.createBranch(createRequest);
        if(createdBranch == null) {
            logger.error("Failed to create branch " + branch.getName());
        }
    }
}
