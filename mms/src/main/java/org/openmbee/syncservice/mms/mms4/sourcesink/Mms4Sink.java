package org.openmbee.syncservice.mms.mms4.sourcesink;

import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.commits.ReciprocatedCommit;
import org.openmbee.syncservice.core.data.common.Branch;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpointInterface;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Syntax;
import org.openmbee.syncservice.mms.mms4.MmsSyntax;
import org.openmbee.syncservice.mms.mms4.services.Mms4Service;
import org.openmbee.syncservice.mms.mms4.util.Mms4DateFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Mms4Sink implements ProjectEndpointInterface, Sink {

    private static final Logger logger = LoggerFactory.getLogger(Mms4Sink.class);

    private ProjectEndpoint endpointConfig;
    private Mms4Service mms4Service;
    private Mms4DateFormat mms4DateFormat;

    @Autowired
    public void setMms4Service(Mms4Service mms4Service) {
        this.mms4Service = mms4Service;
    }

    @Autowired
    public void setMms4DateFormat(Mms4DateFormat mms4DateFormat) {
        this.mms4DateFormat = mms4DateFormat;
    }

    public Mms4Sink(ProjectEndpoint endpointConfig) {
        this.endpointConfig = endpointConfig;
    }

    @Override
    public ProjectEndpoint getEndpoint() {
        return endpointConfig;
    }

    @Override
    public boolean canReceiveFrom(Source source) {
        //TODO: get MMS project and check that it can sync with the TWC project provided
        return true;
    }

    @Override
    public Syntax getSyntax() {
        return MmsSyntax.MMS4;
    }

    @Override
    public void receiveBranch(String projectId, Branch branch) {
        //TODO
    }

    @Override
    public void commitChanges(Source source, Branch branch, CommitChanges commitChanges) {
        JSONObject addedOrUpdatedResponse = mms4Service.postAddedOrUpdatedElements(endpointConfig, commitChanges);
        JSONObject deletedResponse = mms4Service.deleteElements(endpointConfig, commitChanges);
        // why does this keep sending messages if it gets rejected objects back?
    }

    @Override
    public String toString() {
        return String.format("Mms4Sink (%s/%s/%s)", endpointConfig.getHost(), endpointConfig.getCollection(),
                endpointConfig.getProject());
    }

    @Override
    public List<Commit> getCommitHistory() {
        //TODO: expand to all branches
        String branchId = "master";
        String branchName = "master";
        JSONObject jsonObject = mms4Service.getCommits(endpointConfig, branchId);
        if(jsonObject == null) {
            return null;
        }
        JSONArray commitsArray = jsonObject.getJSONArray("commits");
        List<Commit> result = new ArrayList<>(commitsArray.length());
        for (int i = 0; i < commitsArray.length(); ++i) {
            JSONObject commitJson = commitsArray.getJSONObject(i);
            Commit commit = new Commit();
            commit.setBranchId(branchId);
            commit.setBranchName(branchName);
            commit.setCommitDate(parseDate(commitJson.getString("_created")));
            commit.setCommitId(commitJson.getString("id"));
            result.add(commit);
        }
        return result;
    }

    public ReciprocatedCommit getLatestReciprocatedCommit() {
        JSONObject commit = mms4Service.getLatestReciprocatedCommit(endpointConfig);
        if(commit == null ||  !commit.has("twc-revisionId")) {
            return null;
        }

        ReciprocatedCommit reciprocatedCommit = new ReciprocatedCommit();
        reciprocatedCommit.setForeignCommitId(commit.getString("twc-revisionId"));
        reciprocatedCommit.setLocalCommitId(commit.getString("id"));
        return reciprocatedCommit;
    }

    @Override
    public Branch getBranchByName(String branchName) {
        JSONObject refs = mms4Service.getRefs(endpointConfig);
        if(refs == null) {
            return null;
        }

        JSONArray refsArray = refs.getJSONArray("refs");
        if(refsArray == null) {
            return null;
        }

        for(int i = 0; i < refsArray.length(); ++i) {
            JSONObject ref = refsArray.getJSONObject(i);
            if(ref == null) {
                continue;
            }
            String name = ref.getString("name");
            if(branchName.equals(name)) {
                Branch result = new Branch();
                result.setId(ref.getString("id"));
                result.setName(name);
                if(ref.has("parentRefId") && ! ref.isNull("parentRefId")) {
                    result.setParentBranchId(ref.getString("parentRefId"));
                    result.setOriginCommit(ref.getString("parentCommitId"));
                }
                result.setJson(ref);
                return result;
            }
        }
        return null;
    }

    public String getProjectSchema() {
        JSONObject project = mms4Service.getProject(getEndpoint());
        return project.getString("schema");
    }

    private Date parseDate(String date) {
        try {
            return mms4DateFormat.parse(date);
        } catch (ParseException e) {
            logger.error("Could not parse date in MMS 4: " + date, e);
            return null;
        }
    }
}

