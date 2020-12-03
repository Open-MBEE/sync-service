package org.openmbee.syncservice.core.data.branches;

public class BranchCreateRequest {
    private String parentBranchName;
    private String parentBranchId;
    private String branchName;
    private String branchId;

    public BranchCreateRequest() {
    }

    public BranchCreateRequest(String parentBranchName, String parentBranchId, String branchName, String branchId) {
        this.parentBranchName = parentBranchName;
        this.parentBranchId = parentBranchId;
        this.branchName = branchName;
        this.branchId = branchId;
    }

    public String getParentBranchName() {
        return parentBranchName;
    }

    public void setParentBranchName(String parentBranchName) {
        this.parentBranchName = parentBranchName;
    }

    public String getParentBranchId() {
        return parentBranchId;
    }

    public void setParentBranchId(String parentBranchId) {
        this.parentBranchId = parentBranchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
}
