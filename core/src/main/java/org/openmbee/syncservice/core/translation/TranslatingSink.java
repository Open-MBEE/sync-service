package org.openmbee.syncservice.core.translation;

import org.openmbee.syncservice.core.data.common.Branch;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.SinkDecorator;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Syntax;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;

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
    public void receiveBranch(String projectId, Branch branch) {
        Branch translatedBranch = branch;
        if(translationChain != null) {
            for(Translator translator : translationChain) {
                translatedBranch = translator.translateBranch(translatedBranch);
            }
        }
        sink.receiveBranch(projectId, translatedBranch);
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
    public void commitChanges(Source source, Branch branch, CommitChanges commitChanges) {
        CommitChanges translatedCommitChanges = commitChanges;
        if(translationChain != null) {
            for(Translator translator : translationChain) {
                translatedCommitChanges = translator.translateCommitChanges(source, branch, translatedCommitChanges);
            }
        }
        sink.commitChanges(source, branch, translatedCommitChanges);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        translationChain.forEach(v -> sb.append(" -> ").append(v.toString()).append("\n"));
        sb.append(" -> ").append(sink.toString());
        return sb.toString();
    }
}
