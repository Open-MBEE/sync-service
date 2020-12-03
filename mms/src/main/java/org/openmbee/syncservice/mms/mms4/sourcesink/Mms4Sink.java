package org.openmbee.syncservice.mms.mms4.sourcesink;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.branches.BranchCreateRequest;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.commits.ReciprocatedCommit;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpointInterface;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Syntax;
import org.openmbee.syncservice.core.utils.JSONUtils;
import org.openmbee.syncservice.mms.mms4.MmsSyntax;
import org.openmbee.syncservice.mms.mms4.services.Mms4Service;
import org.openmbee.syncservice.mms.mms4.util.Mms4DateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Mms4Sink implements ProjectEndpointInterface, Sink {

    private static final Logger logger = LoggerFactory.getLogger(Mms4Sink.class);

    private ProjectEndpoint endpointConfig;
    private Mms4Service mms4Service;
    private Mms4DateFormat mms4DateFormat;
    private DateTimeFormatter mms4DateTimeFormatter;
    private JSONUtils jsonUtils;

    @Value("${mms.enforce_project_associations:true}")
    private boolean enforceProjectAssociations = true;

    @Autowired
    public void setMms4Service(Mms4Service mms4Service) {
        this.mms4Service = mms4Service;
    }

    @Autowired
    public void setMms4DateFormat(Mms4DateFormat mms4DateFormat) {
        this.mms4DateFormat = mms4DateFormat;
        mms4DateTimeFormatter = DateTimeFormatter.ofPattern(this.mms4DateFormat.toPattern());
    }

    @Autowired
    public void setJsonUtils(JSONUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    public Mms4Sink(ProjectEndpoint endpointConfig) {
        this.endpointConfig = endpointConfig;
    }

    public boolean isEnforceProjectAssociations() {
        return enforceProjectAssociations;
    }

    public void setEnforceProjectAssociations(boolean enforceProjectAssociations) {
        this.enforceProjectAssociations = enforceProjectAssociations;
    }

    @Override
    public ProjectEndpoint getEndpoint() {
        return endpointConfig;
    }

    public boolean isValid() {
        JSONObject project = mms4Service.getProject(getEndpoint());
        return  project != null && project.has("id");
    }


    @Override
    public boolean canReceiveFrom(Source source) {
        if(!enforceProjectAssociations) {
            return true;
        }

        if(source instanceof ProjectEndpointInterface) {
            ProjectEndpoint otherEndpoint = ((ProjectEndpointInterface)source).getEndpoint();
            return mms4Service.checkValidSyncTarget(getEndpoint(), otherEndpoint);
        }
        return false;
    }

    @Override
    public Syntax getSyntax() {
        return MmsSyntax.MMS4;
    }

    @Override
    public List<String> commitChanges(Source source, Branch sinkBranch, CommitChanges commitChanges) {
        List<String> commitIds = new ArrayList<>();
        if(!commitChanges.getAddedElements().isEmpty() || !commitChanges.getUpdatedElements().isEmpty()) {
            JSONObject addedOrUpdatedResponse = mms4Service.postAddedOrUpdatedElements(getEndpoint(), sinkBranch, commitChanges);
            String commitId = getCommitIdFromElementsResponse(addedOrUpdatedResponse);
            if(commitId != null) {
                commitIds.add(commitId);
                logger.info("Added/Updated Elements from " + commitChanges.getCommit().getCommitId() + " in " + commitId);
            } else {
                logger.info("Could not add or update elements from " + commitChanges.getCommit().getCommitId());
                //TODO should we throw here?
            }
            warnRejectedElements(addedOrUpdatedResponse);
        }
        if(!commitChanges.getDeletedElementIds().isEmpty()) {
            JSONObject deletedResponse = mms4Service.deleteElements(getEndpoint(), sinkBranch, commitChanges);
            String commitId = getCommitIdFromElementsResponse(deletedResponse);
            if(commitId != null) {
                commitIds.add(commitId);
                logger.info("Deleted Elements from " + commitChanges.getCommit().getCommitId() + " in " + commitId);
            } else {
                logger.info("Could not delete elements from " + commitChanges.getCommit().getCommitId());
                //TODO should we throw here?
            }
            warnRejectedElements(deletedResponse);
        }
        return commitIds;
    }

    private void warnRejectedElements(JSONObject response) {
        if(response == null){
            return;
        }
        if(response.has("rejected") && !response.isNull("rejected")){
            JSONArray rejected = response.getJSONArray("rejected");
            if(rejected.length() > 0) {
                logger.warn(rejected.length() + " elements were rejected by MMS");
                //TODO is this ok or should we throw?
                for (int i = 0; i < rejected.length(); i++) {
                    logger.debug("Rejected: " + rejected.get(i).toString());
                }
            }
        }
    }

    private String getCommitIdFromElementsResponse(JSONObject response) {
        if(response == null){
            return null;
        }
        if(response.has("commitId") && !response.isNull("commitId")){
            return response.getString("commitId");
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Mms4Sink (%s/%s/%s)", getEndpoint().getHost(), getEndpoint().getCollection(),
                getEndpoint().getProject());
    }

    @Override
    public List<Commit> getCommitHistory() {
        Collection<Branch> branches = getBranches();
        List<Commit> result = new ArrayList<>();
        for (Branch branch : branches) {
            JSONObject jsonObject = mms4Service.getCommits(getEndpoint(), branch.getId());
            if(jsonObject == null) {
                continue;
            }
            JSONArray commitsArray = jsonObject.getJSONArray("commits");
            for (int i = 0; i < commitsArray.length(); ++i) {
                JSONObject commitJson = commitsArray.getJSONObject(i);
                Commit commit = new Commit();
                commit.setBranchId(branch.getId());
                commit.setBranchName(branch.getName());
                commit.setCommitDate(parseDate(commitJson.getString("_created")));
                commit.setCommitId(commitJson.getString("id"));
                result.add(commit);
            }
        }
        return result;
    }

    @Override
    public List<Commit> getBranchCommitHistory(String branchId, int limit) {
        Branch branch = getBranchById(branchId);
        List<Commit> result = new ArrayList<>();
        JSONObject jsonObject = mms4Service.getCommits(getEndpoint(), branchId);
        if(jsonObject == null) {
            return null;
        }
        JSONArray commitsArray = jsonObject.getJSONArray("commits");
        for (int i = 0; i < commitsArray.length(); ++i) {
            if(result.size() >= limit) {
                break;
            }
            JSONObject commitJson = commitsArray.getJSONObject(i);
            Commit commit = new Commit();
            commit.setBranchId(branchId);
            commit.setBranchName(branch != null ? branch.getName() : null);
            commit.setCommitDate(parseDate(commitJson.getString("_created")));
            commit.setCommitId(commitJson.getString("id"));
            result.add(commit);
        }

        return result;
    }


    @Override
    public Commit getCommitById(String commitId) {
        JSONObject commitJson = mms4Service.getCommit(getEndpoint(), commitId);
        if(commitJson == null) {
            return null;
        }
        String branchId = commitJson.getString("_refId");
        Branch branch = getBranchById(branchId);

        Commit commit = new Commit();
        commit.setBranchId(branchId);
        commit.setBranchName(branch != null ? branch.getName() : null);
        commit.setCommitDate(parseDate(commitJson.getString("_created")));
        commit.setCommitId(commitId);
        return commit;
    }

    public Map<Branch, ReciprocatedCommit> getLatestReciprocatedCommitMapByBranch() {
        Collection<Branch> branches = getBranches();
        Map<Branch, ReciprocatedCommit> reciprocatedCommitMapByBranch = new HashMap<>();
        branches.forEach(b -> {
            ReciprocatedCommit r = getLatestReciprocatedCommit(b);
            if(r != null) {
                reciprocatedCommitMapByBranch.put(b, r);
            }
        });

        return reciprocatedCommitMapByBranch;
    }

    protected ReciprocatedCommit getLatestReciprocatedCommit(Branch branch) {
        //TODO should add new endpoint in MMS to make this less clunky
        JSONObject commit = mms4Service.getLatestReciprocatedCommit(getEndpoint(), branch);
        if(commit == null ||  !commit.has("twc-revisionId")) {
            return null;
        }
        ZonedDateTime commitDate = parseDate(commit.getString("_created"));
        if(commitDate == null) {
            return null;
        }

        ReciprocatedCommit reciprocatedCommit = new ReciprocatedCommit();
        reciprocatedCommit.setSourceCommitId(commit.getString("twc-revisionId"));
        reciprocatedCommit.setSinkCommitId(commit.getString("id"));
        reciprocatedCommit.setCommitDate(commitDate);
        return reciprocatedCommit;
    }

    public void registerReciprocatedCommit(String foreignCommitId, String localCommitId) {
        String response = mms4Service.updateCommitWithTwcRevision(getEndpoint(), foreignCommitId, localCommitId);
        //TODO should this throw if not successful?
    }

    @Override
    public Collection<Branch> getBranches() {
        JSONObject refs = mms4Service.getRefs(getEndpoint());
        if(refs == null || !refs.has("refs")) {
            return Collections.emptyList();
        }

        JSONArray refsArray = refs.getJSONArray("refs");
        List<Branch> branches = new ArrayList<>(refsArray.length());
        for(int i = 0; i < refsArray.length(); ++i) {
            JSONObject ref = refsArray.getJSONObject(i);
            branches.add(parseBranch(ref));
        }
        return branches;
    }

    @Override
    public Branch getBranchByName(String branchName) {
        JSONObject refs = mms4Service.getRefs(getEndpoint());
        if(refs == null || !refs.has("refs")) {
            return null;
        }

        JSONArray refsArray = refs.getJSONArray("refs");
        for(int i = 0; i < refsArray.length(); ++i) {
            JSONObject ref = refsArray.getJSONObject(i);
            String name = ref.getString("name");
            if(branchName.equals(name)) {
                return parseBranch(ref);
            }
        }
        return null;
    }

    @Override
    public Branch getBranchById(String branchId) {
        JSONObject refs = mms4Service.getRefById(getEndpoint(), branchId);
        if(refs == null || !refs.has("refs")) {
            return null;
        }

        JSONArray refsArray = refs.getJSONArray("refs");
        if(refsArray.length() > 0) {
            return parseBranch(refsArray.getJSONObject(0));
        }
        return null;
    }

    private Branch parseBranch(JSONObject ref) {
        Branch result = new Branch();
        result.setId(ref.getString("id"));
        result.setName(ref.getString("name"));
        if(ref.has("parentRefId") && ! ref.isNull("parentRefId")) {
            result.setParentBranchId(ref.getString("parentRefId"));
            result.setOriginCommit(ref.getString("parentCommitId"));
        }
        result.setJson(ref);
        return result;
    }

    @Override
    public Branch createBranch(BranchCreateRequest branchCreateRequest) {
        JSONObject refs = mms4Service.createBranch(getEndpoint(), branchCreateRequest.getParentBranchId(),
                branchCreateRequest.getBranchName(), branchCreateRequest.getBranchId());
        if(refs == null || !refs.has("refs")) {
            return null;
        }

        JSONArray refsArray = refs.getJSONArray("refs");
        //There should only be one (or zero if rejected)
        for(int i = 0; i < refsArray.length(); ++i) {
            JSONObject ref = refsArray.getJSONObject(i);
            return parseBranch(ref);
        }
        return null;
    }

    @Override
    public boolean canCreateHistoricBranches() {
        return false;
    }

    public String getProjectSchema() {
        JSONObject project = mms4Service.getProject(getEndpoint());
        if(project == null || !project.has("schema"))
            return null;

        return project.getString("schema");
    }

    private ZonedDateTime parseDate(String date) {
        try {
            return ZonedDateTime.parse(date, mms4DateTimeFormatter);
        } catch (DateTimeParseException e) {
            logger.error("Could not parse date in MMS 4: " + date, e);
            return null;
        }
    }


}

