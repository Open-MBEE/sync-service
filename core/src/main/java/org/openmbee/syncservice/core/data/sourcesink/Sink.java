package org.openmbee.syncservice.core.data.sourcesink;

import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.common.Branch;
import org.openmbee.syncservice.core.syntax.Syntax;

import java.util.List;

public interface Sink {

    boolean canReceiveFrom(Source source);
    Syntax getSyntax();
    List<Commit> getCommitHistory();    //Latest commits first
    Branch getBranchByName(String branchName);
    void receiveBranch(String projectId, Branch branch);
    void commitChanges(Source source, Branch branch, CommitChanges commitChanges);
}
