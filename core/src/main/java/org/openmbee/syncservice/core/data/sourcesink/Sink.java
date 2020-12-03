package org.openmbee.syncservice.core.data.sourcesink;

import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.branches.BranchCreateRequest;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.syntax.Syntax;

import java.util.Collection;
import java.util.List;

public interface Sink {

    boolean canReceiveFrom(Source source);
    Syntax getSyntax();
    List<Commit> getCommitHistory();    //Latest commits first
    Commit getCommitById(String sinkCommitId);
    List<Commit> getBranchCommitHistory(String branchId, int limit);    //Latest commits first

    Branch getBranchById(String branchId);
    Collection<Branch> getBranches();
    Branch getBranchByName(String branchName);
    Branch createBranch(BranchCreateRequest branchCreateRequest);
    boolean canCreateHistoricBranches();

    List<String> commitChanges(Source source, Branch sinkBranch, CommitChanges commitChanges);
}
