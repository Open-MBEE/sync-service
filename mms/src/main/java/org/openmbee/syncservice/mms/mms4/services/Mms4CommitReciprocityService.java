package org.openmbee.syncservice.mms.mms4.services;

import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.data.commits.ReciprocatedCommit;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.UnreciprocatedCommits;
import org.openmbee.syncservice.core.data.services.CommitReciprocityService;
import org.openmbee.syncservice.mms.mms4.sourcesink.Mms4Sink;

import java.util.List;

public class Mms4CommitReciprocityService implements CommitReciprocityService {

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

        ReciprocatedCommit latestReciprocatedCommit = sink.getLatestReciprocatedCommit();

        if(latestReciprocatedCommit != null) {
            List<Commit> unreciprocatedSourceCommits = trimAtCommit(sourceCommits, latestReciprocatedCommit.getForeignCommitId());
            List<Commit> unreciprocatedSinkCommits = trimAtCommit(sinkCommits, latestReciprocatedCommit.getLocalCommitId());
            return new UnreciprocatedCommits(unreciprocatedSourceCommits, unreciprocatedSinkCommits);
        } else {
            //If sink project is empty this is still ok
            if(sinkCommits.isEmpty() || (sinkCommits.size() == 1 && "cameo".equals(sink.getProjectSchema()))){
                return new UnreciprocatedCommits(sourceCommits, sinkCommits);
            }
            throw new RuntimeException("Endpoints have no reciprocated commits");
        }
    }

    private List<Commit> trimAtCommit(List<Commit> list, String commitId) {
        for (int i = 0; i < list.size(); ++i) {
            if (commitId.equals(list.get(i).getCommitId())) {
                return list.subList(0, i);
            }
        }
        throw new RuntimeException("Reciprocated commit id not found in commit history");
    }
}
