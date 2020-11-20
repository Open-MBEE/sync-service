package org.openmbee.syncservice.core.translation;

import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.branches.BranchCreateRequest;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.SinkDecorator;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Syntax;

import java.util.Collection;
import java.util.List;

/**
 * Sink that runs inputs sequentially through the translationChain and finally to the sink
 */
public class TranslatingSink implements Sink, SinkDecorator {
    private List<Translator> translationChain;
    private Sink sink;

    public TranslatingSink(List<Translator> translationChain, Sink sink) {
        this.translationChain = translationChain;
        this.sink = sink;
    }

    public List<Translator> getTranslationChain() {
        return translationChain;
    }

    public Sink getSink() {
        return sink;
    }

    @Override
    public Branch createBranch(BranchCreateRequest branchCreateRequest) {
        BranchCreateRequest translatedBranchCreateRequest = branchCreateRequest;
        if(translationChain != null) {
            for(Translator translator : translationChain) {
                translatedBranchCreateRequest = translator.translateBranchCreateRequest(translatedBranchCreateRequest);
            }
        }
        return sink.createBranch(translatedBranchCreateRequest);
    }

    @Override
    public boolean canCreateHistoricBranches() {
        return getSink().canCreateHistoricBranches();
    }

    @Override
    public boolean canReceiveFrom(Source source) {
        //TODO: check translation chain can do the translation?
        return sink.canReceiveFrom(source);
    }

    @Override
    public Syntax getSyntax() {
        return getSink().getSyntax();
    }

    @Override
    public List<Commit> getCommitHistory() {
        return getSink().getCommitHistory();
    }

    @Override
    public List<Commit> getBranchCommitHistory(String branchId, int limit) {
        return getSink().getBranchCommitHistory(branchId, limit);
    }

    @Override
    public Commit getCommitById(String commitId) {
        return getSink().getCommitById(commitId);
    }

    @Override
    public Branch getBranchById(String branchId) {
        return getSink().getBranchById(branchId);
    }

    @Override
    public Collection<Branch> getBranches() {
        return getSink().getBranches();
    }

    @Override
    public Branch getBranchByName(String branchName) {
        String translatedBranchName = branchName;
        if(translationChain != null) {
            for(Translator translator : translationChain) {
                translatedBranchName = translator.translateBranchName(translatedBranchName);
            }
        }
        return sink.getBranchByName(translatedBranchName);
    }

    @Override
    public List<String> commitChanges(Source source, Branch sinkBranch, CommitChanges commitChanges) {
        CommitChanges translatedCommitChanges = commitChanges;
        if(translationChain != null) {
            for(Translator translator : translationChain) {
                translatedCommitChanges = translator.translateCommitChanges(source,  translatedCommitChanges);
            }
        }
        return sink.commitChanges(source, sinkBranch, translatedCommitChanges);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        translationChain.forEach(v -> sb.append(" -> ").append(v.toString()).append("\n"));
        sb.append(" -> ").append(sink.toString());
        return sb.toString();
    }
}
