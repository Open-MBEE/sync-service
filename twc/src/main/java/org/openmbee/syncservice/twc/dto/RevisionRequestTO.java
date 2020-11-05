package org.openmbee.syncservice.twc.dto;

import java.util.List;

public class RevisionRequestTO {

	private String resourceId;
	private String branchId;
	private List<String> revisionsToSync;
	private String twcUrl;

	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getTwcUrl() {
		return twcUrl;
	}
	public void setTwcUrl(String twcUrl) {
		this.twcUrl = twcUrl;
	}

    public void setTwcRevisionsToSync(List<String> revisionsToSync) {
    }
}
