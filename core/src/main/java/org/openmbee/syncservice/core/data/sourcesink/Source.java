package org.openmbee.syncservice.core.data.sourcesink;

import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.syntax.Syntax;

import java.util.*;

public interface Source {

    boolean canSendTo(Sink sink);
    Syntax getSyntax();
    List<Commit> getCommitHistory();//Latest commits first
    List<Branch> getBranches();
    Branch getBranch(String branchId);
    CommitChanges getCommitChanges(Commit commit);
}
