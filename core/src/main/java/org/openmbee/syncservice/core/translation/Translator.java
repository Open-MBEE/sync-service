package org.openmbee.syncservice.core.translation;

import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.branches.BranchCreateRequest;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Syntax;
import org.openmbee.syncservice.core.data.commits.CommitChanges;

public interface Translator<S extends Syntax, T extends Syntax> {
    S getSourceSyntax();
    T getSinkSyntax();

    Branch translateBranch(Branch refs);
    String translateBranchName(String branchName);

    String translateBranchId(String branchName, String branchId);

    BranchCreateRequest translateBranchCreateRequest(BranchCreateRequest translatedBranchCreateRequest);

    CommitChanges translateCommitChanges(Source source, CommitChanges translatedCommitChanges);
}
