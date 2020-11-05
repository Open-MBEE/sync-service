package org.openmbee.syncservice.core.data.commits;

import org.json.JSONObject;

import java.util.Collection;

public class CommitChanges {
    private Commit commit;
    private JSONObject commitJson;
    private Collection<JSONObject> addedElements;
    private Collection<JSONObject> updatedElements;
    private Collection<String> deletedElementIds;

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public JSONObject getCommitJson() {
        return commitJson;
    }

    public void setCommitJson(JSONObject commitJson) {
        this.commitJson = commitJson;
    }

    public Collection<JSONObject> getAddedElements() {
        return addedElements;
    }

    public void setAddedElements(Collection<JSONObject> addedElements) {
        this.addedElements = addedElements;
    }

    public Collection<JSONObject> getUpdatedElements() {
        return updatedElements;
    }

    public void setUpdatedElements(Collection<JSONObject> updatedElements) {
        this.updatedElements = updatedElements;
    }

    public Collection<String> getDeletedElementIds() {
        return deletedElementIds;
    }

    public void setDeletedElementIds(Collection<String> deletedElementIds) {
        this.deletedElementIds = deletedElementIds;
    }
}
