package org.openmbee.syncservice.core.data.branches;

public class Branch {
    private String id;
    private String name;
    private String parentBranchId;
    private String originCommit;
    private Object json;

    public String getParentBranchId() {
        return parentBranchId;
    }

    public void setParentBranchId(String parentBranchId) {
        this.parentBranchId = parentBranchId;
    }

    public String getOriginCommit() {
        return originCommit;
    }

    public void setOriginCommit(String originCommit) {
        this.originCommit = originCommit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getJson() {
        return json;
    }

    public void setJson(Object json) {
        this.json = json;
    }
}
