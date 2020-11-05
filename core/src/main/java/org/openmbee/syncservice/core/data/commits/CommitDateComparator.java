package org.openmbee.syncservice.core.data.commits;

import java.util.Comparator;

public class CommitDateComparator implements Comparator<Commit> {
    @Override
    public int compare(Commit lhs, Commit rhs) {
        if(lhs == rhs){
            return 0;
        }
        if(lhs == null){
            return -1;
        }
        if(rhs == null){
            return 1;
        }
        return lhs.getCommitDate().compareTo(rhs.getCommitDate());
    }
}
