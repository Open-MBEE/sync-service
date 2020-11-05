package org.openmbee.syncservice.twc.sourcesink;

import org.openmbee.syncservice.core.data.common.Branch;
import org.openmbee.syncservice.core.utils.JSONUtils;
import org.openmbee.syncservice.core.data.commits.Commit;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.twc.service.TeamworkService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class TeamworkCloud19_3SourceTest {

    @Mock
    private TeamworkService teamworkService;

    private ProjectEndpoint endpoint;

    @Spy
    private JSONUtils jsonUtils;

    @InjectMocks
    private TeamworkCloud19_3Source teamworkCloud19_3Source;

    @Before
    public void setup() {
        endpoint = new ProjectEndpoint();
        teamworkCloud19_3Source = new TeamworkCloud19_3Source(endpoint);
        MockitoAnnotations.initMocks(this);


    }

    @Test
    public void getCommitHistoryTestNullBranchNames() {
        when(teamworkService.getProjectRevisions(endpoint)).thenReturn(new JSONArray(getRevisionHistoryExample()));
        List<Commit> commits = teamworkCloud19_3Source.getCommitHistory();
        assertEquals(4, commits.size());

        Commit commit;
        commit = commits.get(0);
        assertEquals("3",commit.getParentCommit());
        assertEquals("184bdb32-3959-4e23-b14f-346fbd26c0eb",commit.getBranchId());
        assertNull(commit.getBranchName());
        assertEquals(new Date(1601477216*1000l),commit.getCommitDate());
        assertEquals("4",commit.getCommitId());

        commit = commits.get(1);
        assertEquals("2",commit.getParentCommit());
        assertEquals("5f31ea7b-144f-45b2-8899-0de92d99698a",commit.getBranchId());
        assertNull(commit.getBranchName());
        assertEquals(new Date(1600259459*1000l),commit.getCommitDate());
        assertEquals("3",commit.getCommitId());

        commit = commits.get(2);
        assertEquals("1",commit.getParentCommit());
        assertEquals("5f31ea7b-144f-45b2-8899-0de92d99698a",commit.getBranchId());
        assertNull(commit.getBranchName());
        assertEquals(new Date(1600258618*1000l),commit.getCommitDate());
        assertEquals("2",commit.getCommitId());

        commit = commits.get(3);
        assertEquals("-1",commit.getParentCommit());
        assertEquals("5f31ea7b-144f-45b2-8899-0de92d99698a",commit.getBranchId());
        assertNull(commit.getBranchName());
        assertEquals(new Date(1600257292*1000l),commit.getCommitDate());
        assertEquals("1",commit.getCommitId());
    }

    @Test
    public void getCommitHistoryTestWithBranchNames() {
        when(teamworkService.getProjectRevisions(endpoint)).thenReturn(new JSONArray(getRevisionHistoryExample()));
        when(teamworkService.getBranchById(endpoint,"184bdb32-3959-4e23-b14f-346fbd26c0eb"))
                .thenReturn(new JSONArray(getBranch184b()));
        when(teamworkService.getBranchById(endpoint,"5f31ea7b-144f-45b2-8899-0de92d99698a"))
                .thenReturn(new JSONArray(getBranch5F31()))
                .thenReturn(new JSONArray(getBranch5F31()))
                .thenReturn(new JSONArray(getBranch5F31()));

        List<Commit> commits = teamworkCloud19_3Source.getCommitHistory();
        assertEquals(4, commits.size());

        assertEquals("TestBranchRev3", commits.get(0).getBranchName());
        assertEquals("trunk", commits.get(1).getBranchName());
        assertEquals("trunk", commits.get(2).getBranchName());
        assertEquals("trunk", commits.get(3).getBranchName());
    }

    @Test
    public void getBranchTestNotTrunkNoParentBranchId() {
        when(teamworkService.getBranchById(endpoint,"184bdb32-3959-4e23-b14f-346fbd26c0eb"))
                .thenReturn(new JSONArray(getBranch184b()));

        Branch branch = teamworkCloud19_3Source.getBranch("184bdb32-3959-4e23-b14f-346fbd26c0eb");
        assertNotNull(branch);
        assertEquals("184bdb32-3959-4e23-b14f-346fbd26c0eb", branch.getId());
        assertEquals("TestBranchRev3", branch.getName());
        assertEquals("3",branch.getOriginCommit());
        assertNull(branch.getParentBranchId());
    }

    @Test
    public void getBranchTestNotTrunkWithParentBranchId() {
        when(teamworkService.getBranchById(endpoint,"184bdb32-3959-4e23-b14f-346fbd26c0eb"))
                .thenReturn(new JSONArray(getBranch184b()));
        when(teamworkService.getRevision(endpoint, "3")).thenReturn(new JSONObject(getRev3()));

        Branch branch = teamworkCloud19_3Source.getBranch("184bdb32-3959-4e23-b14f-346fbd26c0eb");
        assertNotNull(branch);
        assertEquals("184bdb32-3959-4e23-b14f-346fbd26c0eb", branch.getId());
        assertEquals("TestBranchRev3", branch.getName());
        assertEquals("3",branch.getOriginCommit());
        assertEquals("5f31ea7b-144f-45b2-8899-0de92d99698a",branch.getParentBranchId());
    }

    @Test
    public void getBranchTestTrunk() {
        when(teamworkService.getBranchById(endpoint,"5f31ea7b-144f-45b2-8899-0de92d99698a"))
                .thenReturn(new JSONArray(getBranch5F31()));

        Branch branch = teamworkCloud19_3Source.getBranch("5f31ea7b-144f-45b2-8899-0de92d99698a");
        assertNotNull(branch);
        assertEquals("5f31ea7b-144f-45b2-8899-0de92d99698a", branch.getId());
        assertEquals("trunk", branch.getName());
        assertNull(branch.getOriginCommit());
        assertNull(branch.getParentBranchId());
    }

    @Test
    public void getCommitChangesTestRootRevisionRealNonRecursive() {
        when(teamworkService.getRevision(endpoint, "1")).thenReturn(new JSONObject(getRev1()));
        when(teamworkService.getElementsAtRevision(any(), any(), any()))
                .thenReturn(mapChildJsonObjects(new JSONObject(getRev1RootElements())))
                .thenReturn(new HashMap<>());

        Commit commit = new Commit();
        commit.setBranchName("trunk");
        commit.setBranchId("5f31ea7b-144f-45b2-8899-0de92d99698a");
        commit.setParentCommit("-1");
        commit.setCommitDate(new Date(1600257292L*1000));
        commit.setCommitId("1");

        CommitChanges commitChanges = teamworkCloud19_3Source.getCommitChanges(commit);

        assertNotNull(commitChanges);
        assertNotNull(commitChanges.getAddedElements());
        assertEquals(1, commitChanges.getAddedElements().size());
        assertTrue(commitChanges.getUpdatedElements().isEmpty());
        assertTrue(commitChanges.getDeletedElementIds().isEmpty());
    }

    @Test
    public void getCommitChangesTestRootRevisionMockRecursive() {

        JSONObject parent = new JSONObject("{'data':[{'kerml:ownedElement':[{'@id':'child'},{'@id':'child2'}]}]}");
        JSONObject child = new JSONObject("{'data':[{'kerml:ownedElement':[]}]}");
        JSONObject child2 = new JSONObject("{}");

        when(teamworkService.getRevision(endpoint, "1")).thenReturn(new JSONObject("{'rootObjectIDs':['parent']}"));
        when(teamworkService.getElementsAtRevision(any(), any(), any()))
                .thenAnswer(i -> {
                    //First layer
                    List<String> elements = (List)i.getArgument(2);
                    assertEquals("parent", elements.get(0));
                    HashMap<String, JSONObject> result = new HashMap<>();
                    result.put("parent", parent);
                    return result;
                })
                .thenAnswer(i -> {
                    //Second layer
                    List<String> elements = (List)i.getArgument(2);
                    assertTrue(elements.contains("child"));
                    assertTrue(elements.contains("child2"));
                    HashMap<String, JSONObject> result = new HashMap<>();
                    result.put("child", child);
                    result.put("child2", child2);
                    return result;
                })
                .thenReturn(new HashMap<>());

        Commit commit = new Commit();
        commit.setBranchName("trunk");
        commit.setBranchId("5f31ea7b-144f-45b2-8899-0de92d99698a");
        commit.setParentCommit("-1");
        commit.setCommitDate(new Date(1600257292L*1000));
        commit.setCommitId("1");

        CommitChanges commitChanges = teamworkCloud19_3Source.getCommitChanges(commit);

        assertEquals(3, commitChanges.getAddedElements().size());
        assertTrue(commitChanges.getAddedElements().contains(parent));
        assertTrue(commitChanges.getAddedElements().contains(child));
        assertTrue(commitChanges.getAddedElements().contains(child2));
    }

    @Test
    public void getCommitChangesTestRootRevisionError1() {
        when(teamworkService.getRevision(endpoint, "1")).thenReturn(new JSONObject("{}"));

        Commit commit = new Commit();
        commit.setBranchName("trunk");
        commit.setBranchId("5f31ea7b-144f-45b2-8899-0de92d99698a");
        commit.setParentCommit("-1");
        commit.setCommitDate(new Date(1600257292L*1000));
        commit.setCommitId("1");

        CommitChanges commitChanges = teamworkCloud19_3Source.getCommitChanges(commit);

        assertNull(commitChanges);
    }



    @Test
    public void getCommitChangesNormalRevision() {
        when(teamworkService.getRevisionDiff(endpoint, "2", "1"))
                .thenReturn(new JSONObject(getRev2_1Diff()));

        Map<String, JSONObject> added = new HashMap<>();
        JSONObject addedOne = new JSONObject();
        added.put("one", addedOne);
        Map<String, JSONObject> updated = new HashMap<>();
        JSONObject updatedOne = new JSONObject();
        updated.put("one", updatedOne);

        when(teamworkService.getElementsAtRevision(any(), eq("2"), any()))
                .thenAnswer(i -> {
                    List<String> elements = (List)i.getArgument(2);
                    //Added
                    if("89fffbf5-773e-473c-bbdc-a768e92554b7".equals(elements.get(0))){
                        return added;
                    } else if("ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604".equals(elements.get(0))){
                        return updated;
                    }
                    fail("Unexpected call to getElementsAtRevision");
                    return null;
                });

        Commit commit = new Commit();
        commit.setBranchName("trunk");
        commit.setBranchId("5f31ea7b-144f-45b2-8899-0de92d99698a");
        commit.setParentCommit("1");
        commit.setCommitDate(new Date(1600257292L*1000));
        commit.setCommitId("2");

        CommitChanges commitChanges = teamworkCloud19_3Source.getCommitChanges(commit);

        assertNotNull(commitChanges);
        assertSame(addedOne, commitChanges.getAddedElements().iterator().next());
        assertSame(updatedOne, commitChanges.getUpdatedElements().iterator().next());
        assertEquals(1, commitChanges.getDeletedElementIds().size());
        assertEquals("5d98719c-c720-487f-bb93-fa0a2496f08f", commitChanges.getDeletedElementIds().iterator().next());
    }

    private String getRevisionHistoryExample() {
        return "[\n" +
                "  {\n" +
                "    \"commitType\": \"NORMAL\",\n" +
                "    \"branchID\": \"184bdb32-3959-4e23-b14f-346fbd26c0eb\",\n" +
                "    \"resourceID\": \"a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5\",\n" +
                "    \"createdDate\": 1601477216,\n" +
                "    \"author\": \"userid\",\n" +
                "    \"authorInfo\": {\n" +
                "      \"deleted\": false,\n" +
                "      \"name\": \"userid\"\n" +
                "    },\n" +
                "    \"pickedRevision\": -1,\n" +
                "    \"description\": \"Branch \\\"TestBranchRev3\\\" created\",\n" +
                "    \"ID\": 4,\n" +
                "    \"directParent\": 3,\n" +
                "    \"dependencies\": [],\n" +
                "    \"rootObjectIDs\": [\n" +
                "      \"1a1dbf67-bddb-4321-96fe-a01be9b76347\",\n" +
                "      \"0f2cb3fd-e92a-44b2-bb71-51b8c5026d87\",\n" +
                "      \"40e813d8-072d-46c7-ab62-3f9823878867\",\n" +
                "      \"2f500f3b-1df1-40eb-9a58-e78842df9ce7\",\n" +
                "      \"2e03a3ca-b2cd-4842-a814-bfacb58ba349\",\n" +
                "      \"ba1c5a89-adc5-4eae-a524-374622ba0646\",\n" +
                "      \"905331ea-fad9-4e16-8e1f-0ce7d0312054\",\n" +
                "      \"204cfe39-b4be-4bc7-91ab-5f2d3c48ae4f\",\n" +
                "      \"d176d011-bc40-4cb6-832d-5de7fecbd302\",\n" +
                "      \"1474dfd9-16a4-4390-871f-8e801378d010\",\n" +
                "      \"d42ba5cc-822c-426f-9f17-245d5e768548\",\n" +
                "      \"699c2b31-3847-4ffc-8e9f-f09150162d92\",\n" +
                "      \"a0c5c471-dc8f-4cfa-b594-365ca8e4e552\",\n" +
                "      \"ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\",\n" +
                "      \"0c951bbe-7430-484b-b701-dc000eebc634\",\n" +
                "      \"9b739036-aa1c-4c7b-89e7-8a1196dd3e16\",\n" +
                "      \"b9cc47f1-ba40-40fb-909e-6030299aa4da\",\n" +
                "      \"51ab26e7-adf5-42c1-a052-746710897d43\",\n" +
                "      \"36833272-6393-43b6-92c1-af160165aa09\",\n" +
                "      \"6c68c5a3-3da1-4082-a618-e7ec042611a0\",\n" +
                "      \"7600b803-6d71-4f13-9ad4-858c167809b9\",\n" +
                "      \"636a8d01-fee3-44f8-9b66-5c81e08fabb9\",\n" +
                "      \"7ab8beca-6415-4650-8293-7ed1a00a3d4f\",\n" +
                "      \"bd9e967a-8c27-4731-9c36-a282974b217b\",\n" +
                "      \"fcfccfa6-433d-4e54-b16c-ac9f78181ab1\",\n" +
                "      \"e5a55d07-1c8a-4b66-ad79-3dff09ec28b9\",\n" +
                "      \"7c9134fb-0d04-45da-ad69-e9847d180d56\",\n" +
                "      \"9ad318e8-0337-45c1-bd39-43e425ac9e3c\",\n" +
                "      \"0f4807ca-324c-4d7c-97aa-2d8af3063d35\",\n" +
                "      \"d9545791-52d9-4d98-bb5b-afaf62032a29\",\n" +
                "      \"38f8ba3e-21a0-49b6-866f-51adafdebbb6\",\n" +
                "      \"1b67b5bf-731b-4143-8113-cf1d24634b5a\",\n" +
                "      \"c0ef0464-6b42-401b-895b-fcca645b269e\",\n" +
                "      \"b8d55430-9cd0-4f9f-af7b-52d28400846b\",\n" +
                "      \"b44a02ac-e2e5-4906-8703-7f6566c1f83f\",\n" +
                "      \"b9c49894-f309-44ed-afd1-6a1aecbce68d\",\n" +
                "      \"dfa2720a-b329-45a1-a824-3ff2e28cd5aa\",\n" +
                "      \"e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\",\n" +
                "      \"7e2e67fd-b82b-44ad-af0c-31032ee85f3a\",\n" +
                "      \"2bcc26d2-03c5-4f12-b281-3283afbfbf55\",\n" +
                "      \"a88b891d-a5a2-4797-a932-6466131b332f\",\n" +
                "      \"c49e7ea3-46f7-4f9b-9c0a-3ed358e99ce2\",\n" +
                "      \"ad8d6387-ff88-4224-bb80-980b250d27ed\",\n" +
                "      \"40cde331-ba67-4a41-a019-70db6635c789\",\n" +
                "      \"62150c45-7e2d-45c8-898f-65b68a24daf8\",\n" +
                "      \"398bfcc5-36dc-459a-84a8-886aa7648bd6\",\n" +
                "      \"26545754-a19f-46b4-948a-ab51042c9aff\",\n" +
                "      \"a60b8466-0a9e-4f07-b245-d081b29f9fc8\",\n" +
                "      \"ebd9db0b-ffd1-4702-ae2a-d568bca33bd1\",\n" +
                "      \"24d7aa31-b5b8-4cf3-b564-9ade1a6e530b\",\n" +
                "      \"b632b904-6107-43fd-92d0-0b486f15f462\",\n" +
                "      \"94aee07c-9661-4fdb-bed3-5408f1cb089f\",\n" +
                "      \"bd2eee22-3440-4533-8809-9f4144b5c71d\",\n" +
                "      \"92567f7a-4fde-437c-9663-3a4c4a09ad96\",\n" +
                "      \"9481650f-c5e1-4d35-824a-d18893e47eb8\"\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"commitType\": \"NORMAL\",\n" +
                "    \"branchID\": \"5f31ea7b-144f-45b2-8899-0de92d99698a\",\n" +
                "    \"resourceID\": \"a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5\",\n" +
                "    \"createdDate\": 1600259459,\n" +
                "    \"author\": \"userid\",\n" +
                "    \"authorInfo\": {\n" +
                "      \"deleted\": false,\n" +
                "      \"name\": \"userid\"\n" +
                "    },\n" +
                "    \"pickedRevision\": -1,\n" +
                "    \"description\": \"Deleting first package and block\",\n" +
                "    \"ID\": 3,\n" +
                "    \"directParent\": 2,\n" +
                "    \"dependencies\": [],\n" +
                "    \"rootObjectIDs\": [\n" +
                "      \"1a1dbf67-bddb-4321-96fe-a01be9b76347\",\n" +
                "      \"0f2cb3fd-e92a-44b2-bb71-51b8c5026d87\",\n" +
                "      \"40e813d8-072d-46c7-ab62-3f9823878867\",\n" +
                "      \"2f500f3b-1df1-40eb-9a58-e78842df9ce7\",\n" +
                "      \"2e03a3ca-b2cd-4842-a814-bfacb58ba349\",\n" +
                "      \"ba1c5a89-adc5-4eae-a524-374622ba0646\",\n" +
                "      \"905331ea-fad9-4e16-8e1f-0ce7d0312054\",\n" +
                "      \"204cfe39-b4be-4bc7-91ab-5f2d3c48ae4f\",\n" +
                "      \"d176d011-bc40-4cb6-832d-5de7fecbd302\",\n" +
                "      \"1474dfd9-16a4-4390-871f-8e801378d010\",\n" +
                "      \"d42ba5cc-822c-426f-9f17-245d5e768548\",\n" +
                "      \"699c2b31-3847-4ffc-8e9f-f09150162d92\",\n" +
                "      \"a0c5c471-dc8f-4cfa-b594-365ca8e4e552\",\n" +
                "      \"ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\",\n" +
                "      \"0c951bbe-7430-484b-b701-dc000eebc634\",\n" +
                "      \"9b739036-aa1c-4c7b-89e7-8a1196dd3e16\",\n" +
                "      \"b9cc47f1-ba40-40fb-909e-6030299aa4da\",\n" +
                "      \"51ab26e7-adf5-42c1-a052-746710897d43\",\n" +
                "      \"36833272-6393-43b6-92c1-af160165aa09\",\n" +
                "      \"6c68c5a3-3da1-4082-a618-e7ec042611a0\",\n" +
                "      \"7600b803-6d71-4f13-9ad4-858c167809b9\",\n" +
                "      \"636a8d01-fee3-44f8-9b66-5c81e08fabb9\",\n" +
                "      \"7ab8beca-6415-4650-8293-7ed1a00a3d4f\",\n" +
                "      \"bd9e967a-8c27-4731-9c36-a282974b217b\",\n" +
                "      \"fcfccfa6-433d-4e54-b16c-ac9f78181ab1\",\n" +
                "      \"e5a55d07-1c8a-4b66-ad79-3dff09ec28b9\",\n" +
                "      \"7c9134fb-0d04-45da-ad69-e9847d180d56\",\n" +
                "      \"9ad318e8-0337-45c1-bd39-43e425ac9e3c\",\n" +
                "      \"0f4807ca-324c-4d7c-97aa-2d8af3063d35\",\n" +
                "      \"d9545791-52d9-4d98-bb5b-afaf62032a29\",\n" +
                "      \"38f8ba3e-21a0-49b6-866f-51adafdebbb6\",\n" +
                "      \"1b67b5bf-731b-4143-8113-cf1d24634b5a\",\n" +
                "      \"c0ef0464-6b42-401b-895b-fcca645b269e\",\n" +
                "      \"b8d55430-9cd0-4f9f-af7b-52d28400846b\",\n" +
                "      \"b44a02ac-e2e5-4906-8703-7f6566c1f83f\",\n" +
                "      \"b9c49894-f309-44ed-afd1-6a1aecbce68d\",\n" +
                "      \"dfa2720a-b329-45a1-a824-3ff2e28cd5aa\",\n" +
                "      \"e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\",\n" +
                "      \"7e2e67fd-b82b-44ad-af0c-31032ee85f3a\",\n" +
                "      \"2bcc26d2-03c5-4f12-b281-3283afbfbf55\",\n" +
                "      \"a88b891d-a5a2-4797-a932-6466131b332f\",\n" +
                "      \"c49e7ea3-46f7-4f9b-9c0a-3ed358e99ce2\",\n" +
                "      \"ad8d6387-ff88-4224-bb80-980b250d27ed\",\n" +
                "      \"40cde331-ba67-4a41-a019-70db6635c789\",\n" +
                "      \"62150c45-7e2d-45c8-898f-65b68a24daf8\",\n" +
                "      \"398bfcc5-36dc-459a-84a8-886aa7648bd6\",\n" +
                "      \"26545754-a19f-46b4-948a-ab51042c9aff\",\n" +
                "      \"a60b8466-0a9e-4f07-b245-d081b29f9fc8\",\n" +
                "      \"ebd9db0b-ffd1-4702-ae2a-d568bca33bd1\",\n" +
                "      \"24d7aa31-b5b8-4cf3-b564-9ade1a6e530b\",\n" +
                "      \"b632b904-6107-43fd-92d0-0b486f15f462\",\n" +
                "      \"94aee07c-9661-4fdb-bed3-5408f1cb089f\",\n" +
                "      \"bd2eee22-3440-4533-8809-9f4144b5c71d\",\n" +
                "      \"92567f7a-4fde-437c-9663-3a4c4a09ad96\",\n" +
                "      \"9481650f-c5e1-4d35-824a-d18893e47eb8\"\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"commitType\": \"NORMAL\",\n" +
                "    \"branchID\": \"5f31ea7b-144f-45b2-8899-0de92d99698a\",\n" +
                "    \"resourceID\": \"a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5\",\n" +
                "    \"createdDate\": 1600258618,\n" +
                "    \"author\": \"userid\",\n" +
                "    \"authorInfo\": {\n" +
                "      \"deleted\": false,\n" +
                "      \"name\": \"userid\"\n" +
                "    },\n" +
                "    \"pickedRevision\": -1,\n" +
                "    \"description\": \"Test commit (adding one package and one block)\",\n" +
                "    \"ID\": 2,\n" +
                "    \"directParent\": 1,\n" +
                "    \"dependencies\": [],\n" +
                "    \"rootObjectIDs\": [\n" +
                "      \"1a1dbf67-bddb-4321-96fe-a01be9b76347\",\n" +
                "      \"0f2cb3fd-e92a-44b2-bb71-51b8c5026d87\",\n" +
                "      \"40e813d8-072d-46c7-ab62-3f9823878867\",\n" +
                "      \"2f500f3b-1df1-40eb-9a58-e78842df9ce7\",\n" +
                "      \"2e03a3ca-b2cd-4842-a814-bfacb58ba349\",\n" +
                "      \"ba1c5a89-adc5-4eae-a524-374622ba0646\",\n" +
                "      \"905331ea-fad9-4e16-8e1f-0ce7d0312054\",\n" +
                "      \"204cfe39-b4be-4bc7-91ab-5f2d3c48ae4f\",\n" +
                "      \"d176d011-bc40-4cb6-832d-5de7fecbd302\",\n" +
                "      \"1474dfd9-16a4-4390-871f-8e801378d010\",\n" +
                "      \"d42ba5cc-822c-426f-9f17-245d5e768548\",\n" +
                "      \"699c2b31-3847-4ffc-8e9f-f09150162d92\",\n" +
                "      \"a0c5c471-dc8f-4cfa-b594-365ca8e4e552\",\n" +
                "      \"ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\",\n" +
                "      \"0c951bbe-7430-484b-b701-dc000eebc634\",\n" +
                "      \"9b739036-aa1c-4c7b-89e7-8a1196dd3e16\",\n" +
                "      \"b9cc47f1-ba40-40fb-909e-6030299aa4da\",\n" +
                "      \"51ab26e7-adf5-42c1-a052-746710897d43\",\n" +
                "      \"36833272-6393-43b6-92c1-af160165aa09\",\n" +
                "      \"6c68c5a3-3da1-4082-a618-e7ec042611a0\",\n" +
                "      \"7600b803-6d71-4f13-9ad4-858c167809b9\",\n" +
                "      \"636a8d01-fee3-44f8-9b66-5c81e08fabb9\",\n" +
                "      \"7ab8beca-6415-4650-8293-7ed1a00a3d4f\",\n" +
                "      \"bd9e967a-8c27-4731-9c36-a282974b217b\",\n" +
                "      \"fcfccfa6-433d-4e54-b16c-ac9f78181ab1\",\n" +
                "      \"e5a55d07-1c8a-4b66-ad79-3dff09ec28b9\",\n" +
                "      \"7c9134fb-0d04-45da-ad69-e9847d180d56\",\n" +
                "      \"9ad318e8-0337-45c1-bd39-43e425ac9e3c\",\n" +
                "      \"0f4807ca-324c-4d7c-97aa-2d8af3063d35\",\n" +
                "      \"d9545791-52d9-4d98-bb5b-afaf62032a29\",\n" +
                "      \"38f8ba3e-21a0-49b6-866f-51adafdebbb6\",\n" +
                "      \"1b67b5bf-731b-4143-8113-cf1d24634b5a\",\n" +
                "      \"c0ef0464-6b42-401b-895b-fcca645b269e\",\n" +
                "      \"b8d55430-9cd0-4f9f-af7b-52d28400846b\",\n" +
                "      \"b44a02ac-e2e5-4906-8703-7f6566c1f83f\",\n" +
                "      \"b9c49894-f309-44ed-afd1-6a1aecbce68d\",\n" +
                "      \"dfa2720a-b329-45a1-a824-3ff2e28cd5aa\",\n" +
                "      \"e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\",\n" +
                "      \"7e2e67fd-b82b-44ad-af0c-31032ee85f3a\",\n" +
                "      \"2bcc26d2-03c5-4f12-b281-3283afbfbf55\",\n" +
                "      \"a88b891d-a5a2-4797-a932-6466131b332f\",\n" +
                "      \"c49e7ea3-46f7-4f9b-9c0a-3ed358e99ce2\",\n" +
                "      \"ad8d6387-ff88-4224-bb80-980b250d27ed\",\n" +
                "      \"40cde331-ba67-4a41-a019-70db6635c789\",\n" +
                "      \"62150c45-7e2d-45c8-898f-65b68a24daf8\",\n" +
                "      \"398bfcc5-36dc-459a-84a8-886aa7648bd6\",\n" +
                "      \"26545754-a19f-46b4-948a-ab51042c9aff\",\n" +
                "      \"a60b8466-0a9e-4f07-b245-d081b29f9fc8\",\n" +
                "      \"ebd9db0b-ffd1-4702-ae2a-d568bca33bd1\",\n" +
                "      \"24d7aa31-b5b8-4cf3-b564-9ade1a6e530b\",\n" +
                "      \"b632b904-6107-43fd-92d0-0b486f15f462\",\n" +
                "      \"94aee07c-9661-4fdb-bed3-5408f1cb089f\",\n" +
                "      \"bd2eee22-3440-4533-8809-9f4144b5c71d\",\n" +
                "      \"92567f7a-4fde-437c-9663-3a4c4a09ad96\",\n" +
                "      \"9481650f-c5e1-4d35-824a-d18893e47eb8\"\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"commitType\": \"NORMAL\",\n" +
                "    \"branchID\": \"5f31ea7b-144f-45b2-8899-0de92d99698a\",\n" +
                "    \"resourceID\": \"a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5\",\n" +
                "    \"createdDate\": 1600257292,\n" +
                "    \"author\": \"userid\",\n" +
                "    \"authorInfo\": {\n" +
                "      \"deleted\": false,\n" +
                "      \"name\": \"userid\"\n" +
                "    },\n" +
                "    \"pickedRevision\": -1,\n" +
                "    \"description\": \"** no message **\",\n" +
                "    \"ID\": 1,\n" +
                "    \"directParent\": -1,\n" +
                "    \"dependencies\": [],\n" +
                "    \"rootObjectIDs\": [\n" +
                "      \"1a1dbf67-bddb-4321-96fe-a01be9b76347\",\n" +
                "      \"0f2cb3fd-e92a-44b2-bb71-51b8c5026d87\",\n" +
                "      \"40e813d8-072d-46c7-ab62-3f9823878867\",\n" +
                "      \"2f500f3b-1df1-40eb-9a58-e78842df9ce7\",\n" +
                "      \"2e03a3ca-b2cd-4842-a814-bfacb58ba349\",\n" +
                "      \"ba1c5a89-adc5-4eae-a524-374622ba0646\",\n" +
                "      \"905331ea-fad9-4e16-8e1f-0ce7d0312054\",\n" +
                "      \"204cfe39-b4be-4bc7-91ab-5f2d3c48ae4f\",\n" +
                "      \"d176d011-bc40-4cb6-832d-5de7fecbd302\",\n" +
                "      \"1474dfd9-16a4-4390-871f-8e801378d010\",\n" +
                "      \"d42ba5cc-822c-426f-9f17-245d5e768548\",\n" +
                "      \"699c2b31-3847-4ffc-8e9f-f09150162d92\",\n" +
                "      \"a0c5c471-dc8f-4cfa-b594-365ca8e4e552\",\n" +
                "      \"ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\",\n" +
                "      \"0c951bbe-7430-484b-b701-dc000eebc634\",\n" +
                "      \"9b739036-aa1c-4c7b-89e7-8a1196dd3e16\",\n" +
                "      \"b9cc47f1-ba40-40fb-909e-6030299aa4da\",\n" +
                "      \"51ab26e7-adf5-42c1-a052-746710897d43\",\n" +
                "      \"36833272-6393-43b6-92c1-af160165aa09\",\n" +
                "      \"6c68c5a3-3da1-4082-a618-e7ec042611a0\",\n" +
                "      \"7600b803-6d71-4f13-9ad4-858c167809b9\",\n" +
                "      \"636a8d01-fee3-44f8-9b66-5c81e08fabb9\",\n" +
                "      \"7ab8beca-6415-4650-8293-7ed1a00a3d4f\",\n" +
                "      \"bd9e967a-8c27-4731-9c36-a282974b217b\",\n" +
                "      \"fcfccfa6-433d-4e54-b16c-ac9f78181ab1\",\n" +
                "      \"e5a55d07-1c8a-4b66-ad79-3dff09ec28b9\",\n" +
                "      \"7c9134fb-0d04-45da-ad69-e9847d180d56\",\n" +
                "      \"9ad318e8-0337-45c1-bd39-43e425ac9e3c\",\n" +
                "      \"0f4807ca-324c-4d7c-97aa-2d8af3063d35\",\n" +
                "      \"d9545791-52d9-4d98-bb5b-afaf62032a29\",\n" +
                "      \"38f8ba3e-21a0-49b6-866f-51adafdebbb6\",\n" +
                "      \"1b67b5bf-731b-4143-8113-cf1d24634b5a\",\n" +
                "      \"c0ef0464-6b42-401b-895b-fcca645b269e\",\n" +
                "      \"b8d55430-9cd0-4f9f-af7b-52d28400846b\",\n" +
                "      \"b44a02ac-e2e5-4906-8703-7f6566c1f83f\",\n" +
                "      \"b9c49894-f309-44ed-afd1-6a1aecbce68d\",\n" +
                "      \"dfa2720a-b329-45a1-a824-3ff2e28cd5aa\",\n" +
                "      \"e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\",\n" +
                "      \"7e2e67fd-b82b-44ad-af0c-31032ee85f3a\",\n" +
                "      \"2bcc26d2-03c5-4f12-b281-3283afbfbf55\",\n" +
                "      \"a88b891d-a5a2-4797-a932-6466131b332f\",\n" +
                "      \"c49e7ea3-46f7-4f9b-9c0a-3ed358e99ce2\",\n" +
                "      \"ad8d6387-ff88-4224-bb80-980b250d27ed\",\n" +
                "      \"40cde331-ba67-4a41-a019-70db6635c789\",\n" +
                "      \"62150c45-7e2d-45c8-898f-65b68a24daf8\",\n" +
                "      \"398bfcc5-36dc-459a-84a8-886aa7648bd6\",\n" +
                "      \"26545754-a19f-46b4-948a-ab51042c9aff\",\n" +
                "      \"a60b8466-0a9e-4f07-b245-d081b29f9fc8\",\n" +
                "      \"ebd9db0b-ffd1-4702-ae2a-d568bca33bd1\",\n" +
                "      \"24d7aa31-b5b8-4cf3-b564-9ade1a6e530b\",\n" +
                "      \"b632b904-6107-43fd-92d0-0b486f15f462\",\n" +
                "      \"94aee07c-9661-4fdb-bed3-5408f1cb089f\",\n" +
                "      \"bd2eee22-3440-4533-8809-9f4144b5c71d\",\n" +
                "      \"92567f7a-4fde-437c-9663-3a4c4a09ad96\",\n" +
                "      \"9481650f-c5e1-4d35-824a-d18893e47eb8\"\n" +
                "    ]\n" +
                "  }\n" +
                "]";
    }

    private String getBranch5F31() {
        return "  [{\n" +
                "    \"ldp:membershipResource\": {\n" +
                "      \"@id\": \"#it\"\n" +
                "    },\n" +
                "    \"@type\": [\n" +
                "      \"ldp:DirectContainer\",\n" +
                "      \"kerml:Branch\"\n" +
                "    ],\n" +
                "    \"ldp:contains\": [\n" +
                "      3,\n" +
                "      2,\n" +
                "      1\n" +
                "    ],\n" +
                "    \"ldp:hasMemberRelation\": \"kerml:revisions\",\n" +
                "    \"@id\": \"\",\n" +
                "    \"@context\": \"https://server.com:8111/osmc/schemas/branchContainer\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"resourceID\": \"a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5\",\n" +
                "    \"createdDate\": 1600257292,\n" +
                "    \"startRevision\": 1,\n" +
                "    \"author\": \"lm392c\",\n" +
                "    \"@type\": [\n" +
                "      \"kerml:Branch\"\n" +
                "    ],\n" +
                "    \"kerml:revisions\": [\n" +
                "      3,\n" +
                "      2,\n" +
                "      1\n" +
                "    ],\n" +
                "    \"authorInfo\": {\n" +
                "      \"deleted\": false,\n" +
                "      \"name\": \"lm392c\"\n" +
                "    },\n" +
                "    \"dcterms:title\": \"trunk\",\n" +
                "    \"ID\": \"5f31ea7b-144f-45b2-8899-0de92d99698a\",\n" +
                "    \"@id\": {\n" +
                "      \"@id\": \"#it\"\n" +
                "    },\n" +
                "    \"latestRevision\": 3,\n" +
                "    \"@context\": \"https://server.com:8111/osmc/schemas/branch\"\n" +
                "  }]";
    }

    private String getBranch184b() {
        return "[  {\n" +
                "    \"ldp:membershipResource\": {\n" +
                "      \"@id\": \"#it\"\n" +
                "    },\n" +
                "    \"@type\": [\n" +
                "      \"ldp:DirectContainer\",\n" +
                "      \"kerml:Branch\"\n" +
                "    ],\n" +
                "    \"ldp:contains\": [\n" +
                "      4,\n" +
                "      3,\n" +
                "      2,\n" +
                "      1\n" +
                "    ],\n" +
                "    \"ldp:hasMemberRelation\": \"kerml:revisions\",\n" +
                "    \"@id\": \"\",\n" +
                "    \"@context\": \"https://server.com:8111/osmc/schemas/branchContainer\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"resourceID\": \"a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5\",\n" +
                "    \"author\": \"lm392c\",\n" +
                "    \"@type\": [\n" +
                "      \"kerml:Branch\"\n" +
                "    ],\n" +
                "    \"kerml:revisions\": [\n" +
                "      4,\n" +
                "      3,\n" +
                "      2,\n" +
                "      1\n" +
                "    ],\n" +
                "    \"authorInfo\": {\n" +
                "      \"deleted\": false,\n" +
                "      \"name\": \"lm392c\"\n" +
                "    },\n" +
                "    \"dcterms:title\": \"TestBranchRev3\",\n" +
                "    \"@context\": \"https://server.com:8111/osmc/schemas/branch\",\n" +
                "    \"createdDate\": 1601477216,\n" +
                "    \"startRevision\": 3,\n" +
                "    \"dcterms:description\": \"Branch \\\"TestBranchRev3\\\" created\",\n" +
                "    \"ID\": \"184bdb32-3959-4e23-b14f-346fbd26c0eb\",\n" +
                "    \"@id\": {\n" +
                "      \"@id\": \"#it\"\n" +
                "    },\n" +
                "    \"latestRevision\": 4\n" +
                "  }]";
    }

    private String getRev1() {
        return "  {\n" +
                "    \"commitType\": \"NORMAL\",\n" +
                "    \"branchID\": \"../../../branches/5f31ea7b-144f-45b2-8899-0de92d99698a\",\n" +
                "    \"resourceID\": \"../../..\",\n" +
                "    \"@base\": \"https://server.com:8111/osmc/workspaces/e5b17282-4b76-43fd-a077-e838e2bbf0e5/resources/a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5/revisions/1/elements\",\n" +
                "    \"author\": \"lm392c\",\n" +
                "    \"@type\": [\n" +
                "      \"RDFSource\",\n" +
                "      \"kerml:Revision\"\n" +
                "    ],\n" +
                "    \"authorInfo\": {\n" +
                "      \"deleted\": false,\n" +
                "      \"name\": \"lm392c\"\n" +
                "    },\n" +
                "    \"pickedRevision\": -1,\n" +
                "    \"description\": \"** no message **\",\n" +
                "    \"@context\": \"https://server.com:8111/osmc/schemas/revision\",\n" +
                "    \"directParent\": -1,\n" +
                "    \"dependencies\": [],\n" +
                "    \"rootObjectIDs\": [\n" +
                "      \"1a1dbf67-bddb-4321-96fe-a01be9b76347\",\n" +
                "      \"0f2cb3fd-e92a-44b2-bb71-51b8c5026d87\",\n" +
                "      \"40e813d8-072d-46c7-ab62-3f9823878867\",\n" +
                "      \"2f500f3b-1df1-40eb-9a58-e78842df9ce7\",\n" +
                "      \"2e03a3ca-b2cd-4842-a814-bfacb58ba349\",\n" +
                "      \"ba1c5a89-adc5-4eae-a524-374622ba0646\",\n" +
                "      \"905331ea-fad9-4e16-8e1f-0ce7d0312054\",\n" +
                "      \"204cfe39-b4be-4bc7-91ab-5f2d3c48ae4f\",\n" +
                "      \"d176d011-bc40-4cb6-832d-5de7fecbd302\",\n" +
                "      \"1474dfd9-16a4-4390-871f-8e801378d010\",\n" +
                "      \"d42ba5cc-822c-426f-9f17-245d5e768548\",\n" +
                "      \"699c2b31-3847-4ffc-8e9f-f09150162d92\",\n" +
                "      \"a0c5c471-dc8f-4cfa-b594-365ca8e4e552\",\n" +
                "      \"ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\",\n" +
                "      \"0c951bbe-7430-484b-b701-dc000eebc634\",\n" +
                "      \"9b739036-aa1c-4c7b-89e7-8a1196dd3e16\",\n" +
                "      \"b9cc47f1-ba40-40fb-909e-6030299aa4da\",\n" +
                "      \"51ab26e7-adf5-42c1-a052-746710897d43\",\n" +
                "      \"36833272-6393-43b6-92c1-af160165aa09\",\n" +
                "      \"6c68c5a3-3da1-4082-a618-e7ec042611a0\",\n" +
                "      \"7600b803-6d71-4f13-9ad4-858c167809b9\",\n" +
                "      \"636a8d01-fee3-44f8-9b66-5c81e08fabb9\",\n" +
                "      \"7ab8beca-6415-4650-8293-7ed1a00a3d4f\",\n" +
                "      \"bd9e967a-8c27-4731-9c36-a282974b217b\",\n" +
                "      \"fcfccfa6-433d-4e54-b16c-ac9f78181ab1\",\n" +
                "      \"e5a55d07-1c8a-4b66-ad79-3dff09ec28b9\",\n" +
                "      \"7c9134fb-0d04-45da-ad69-e9847d180d56\",\n" +
                "      \"9ad318e8-0337-45c1-bd39-43e425ac9e3c\",\n" +
                "      \"0f4807ca-324c-4d7c-97aa-2d8af3063d35\",\n" +
                "      \"d9545791-52d9-4d98-bb5b-afaf62032a29\",\n" +
                "      \"38f8ba3e-21a0-49b6-866f-51adafdebbb6\",\n" +
                "      \"1b67b5bf-731b-4143-8113-cf1d24634b5a\",\n" +
                "      \"c0ef0464-6b42-401b-895b-fcca645b269e\",\n" +
                "      \"b8d55430-9cd0-4f9f-af7b-52d28400846b\",\n" +
                "      \"b44a02ac-e2e5-4906-8703-7f6566c1f83f\",\n" +
                "      \"b9c49894-f309-44ed-afd1-6a1aecbce68d\",\n" +
                "      \"dfa2720a-b329-45a1-a824-3ff2e28cd5aa\",\n" +
                "      \"e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\",\n" +
                "      \"7e2e67fd-b82b-44ad-af0c-31032ee85f3a\",\n" +
                "      \"2bcc26d2-03c5-4f12-b281-3283afbfbf55\",\n" +
                "      \"a88b891d-a5a2-4797-a932-6466131b332f\",\n" +
                "      \"c49e7ea3-46f7-4f9b-9c0a-3ed358e99ce2\",\n" +
                "      \"ad8d6387-ff88-4224-bb80-980b250d27ed\",\n" +
                "      \"40cde331-ba67-4a41-a019-70db6635c789\",\n" +
                "      \"62150c45-7e2d-45c8-898f-65b68a24daf8\",\n" +
                "      \"398bfcc5-36dc-459a-84a8-886aa7648bd6\",\n" +
                "      \"26545754-a19f-46b4-948a-ab51042c9aff\",\n" +
                "      \"a60b8466-0a9e-4f07-b245-d081b29f9fc8\",\n" +
                "      \"ebd9db0b-ffd1-4702-ae2a-d568bca33bd1\",\n" +
                "      \"24d7aa31-b5b8-4cf3-b564-9ade1a6e530b\",\n" +
                "      \"b632b904-6107-43fd-92d0-0b486f15f462\",\n" +
                "      \"94aee07c-9661-4fdb-bed3-5408f1cb089f\",\n" +
                "      \"bd2eee22-3440-4533-8809-9f4144b5c71d\",\n" +
                "      \"92567f7a-4fde-437c-9663-3a4c4a09ad96\",\n" +
                "      \"9481650f-c5e1-4d35-824a-d18893e47eb8\"\n" +
                "    ],\n" +
                "    \"createdDate\": 1600257292,\n" +
                "    \"ID\": \"\",\n" +
                "    \"artifacts\": \"artifacts\"\n" +
                "  }";
    }

    private String getRev3(){
        return "{\n" +
                "    \"commitType\": \"NORMAL\",\n" +
                "    \"branchID\": \"../../../branches/5f31ea7b-144f-45b2-8899-0de92d99698a\",\n" +
                "    \"resourceID\": \"../../..\",\n" +
                "    \"@base\": \"https://server.com:8111/osmc/workspaces/e5b17282-4b76-43fd-a077-e838e2bbf0e5/resources/a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5/revisions/3/elements\",\n" +
                "    \"author\": \"lm392c\",\n" +
                "    \"@type\": [\n" +
                "      \"RDFSource\",\n" +
                "      \"kerml:Revision\"\n" +
                "    ],\n" +
                "    \"authorInfo\": {\n" +
                "      \"deleted\": false,\n" +
                "      \"name\": \"lm392c\"\n" +
                "    },\n" +
                "    \"pickedRevision\": -1,\n" +
                "    \"description\": \"Deleting first package and block\",\n" +
                "    \"@context\": \"https://server.com:8111/osmc/schemas/revision\",\n" +
                "    \"directParent\": 2,\n" +
                "    \"dependencies\": [],\n" +
                "    \"rootObjectIDs\": [\n" +
                "      \"1a1dbf67-bddb-4321-96fe-a01be9b76347\",\n" +
                "      \"0f2cb3fd-e92a-44b2-bb71-51b8c5026d87\",\n" +
                "      \"40e813d8-072d-46c7-ab62-3f9823878867\",\n" +
                "      \"2f500f3b-1df1-40eb-9a58-e78842df9ce7\",\n" +
                "      \"2e03a3ca-b2cd-4842-a814-bfacb58ba349\",\n" +
                "      \"ba1c5a89-adc5-4eae-a524-374622ba0646\",\n" +
                "      \"905331ea-fad9-4e16-8e1f-0ce7d0312054\",\n" +
                "      \"204cfe39-b4be-4bc7-91ab-5f2d3c48ae4f\",\n" +
                "      \"d176d011-bc40-4cb6-832d-5de7fecbd302\",\n" +
                "      \"1474dfd9-16a4-4390-871f-8e801378d010\",\n" +
                "      \"d42ba5cc-822c-426f-9f17-245d5e768548\",\n" +
                "      \"699c2b31-3847-4ffc-8e9f-f09150162d92\",\n" +
                "      \"a0c5c471-dc8f-4cfa-b594-365ca8e4e552\",\n" +
                "      \"ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\",\n" +
                "      \"0c951bbe-7430-484b-b701-dc000eebc634\",\n" +
                "      \"9b739036-aa1c-4c7b-89e7-8a1196dd3e16\",\n" +
                "      \"b9cc47f1-ba40-40fb-909e-6030299aa4da\",\n" +
                "      \"51ab26e7-adf5-42c1-a052-746710897d43\",\n" +
                "      \"36833272-6393-43b6-92c1-af160165aa09\",\n" +
                "      \"6c68c5a3-3da1-4082-a618-e7ec042611a0\",\n" +
                "      \"7600b803-6d71-4f13-9ad4-858c167809b9\",\n" +
                "      \"636a8d01-fee3-44f8-9b66-5c81e08fabb9\",\n" +
                "      \"7ab8beca-6415-4650-8293-7ed1a00a3d4f\",\n" +
                "      \"bd9e967a-8c27-4731-9c36-a282974b217b\",\n" +
                "      \"fcfccfa6-433d-4e54-b16c-ac9f78181ab1\",\n" +
                "      \"e5a55d07-1c8a-4b66-ad79-3dff09ec28b9\",\n" +
                "      \"7c9134fb-0d04-45da-ad69-e9847d180d56\",\n" +
                "      \"9ad318e8-0337-45c1-bd39-43e425ac9e3c\",\n" +
                "      \"0f4807ca-324c-4d7c-97aa-2d8af3063d35\",\n" +
                "      \"d9545791-52d9-4d98-bb5b-afaf62032a29\",\n" +
                "      \"38f8ba3e-21a0-49b6-866f-51adafdebbb6\",\n" +
                "      \"1b67b5bf-731b-4143-8113-cf1d24634b5a\",\n" +
                "      \"c0ef0464-6b42-401b-895b-fcca645b269e\",\n" +
                "      \"b8d55430-9cd0-4f9f-af7b-52d28400846b\",\n" +
                "      \"b44a02ac-e2e5-4906-8703-7f6566c1f83f\",\n" +
                "      \"b9c49894-f309-44ed-afd1-6a1aecbce68d\",\n" +
                "      \"dfa2720a-b329-45a1-a824-3ff2e28cd5aa\",\n" +
                "      \"e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\",\n" +
                "      \"7e2e67fd-b82b-44ad-af0c-31032ee85f3a\",\n" +
                "      \"2bcc26d2-03c5-4f12-b281-3283afbfbf55\",\n" +
                "      \"a88b891d-a5a2-4797-a932-6466131b332f\",\n" +
                "      \"c49e7ea3-46f7-4f9b-9c0a-3ed358e99ce2\",\n" +
                "      \"ad8d6387-ff88-4224-bb80-980b250d27ed\",\n" +
                "      \"40cde331-ba67-4a41-a019-70db6635c789\",\n" +
                "      \"62150c45-7e2d-45c8-898f-65b68a24daf8\",\n" +
                "      \"398bfcc5-36dc-459a-84a8-886aa7648bd6\",\n" +
                "      \"26545754-a19f-46b4-948a-ab51042c9aff\",\n" +
                "      \"a60b8466-0a9e-4f07-b245-d081b29f9fc8\",\n" +
                "      \"ebd9db0b-ffd1-4702-ae2a-d568bca33bd1\",\n" +
                "      \"24d7aa31-b5b8-4cf3-b564-9ade1a6e530b\",\n" +
                "      \"b632b904-6107-43fd-92d0-0b486f15f462\",\n" +
                "      \"94aee07c-9661-4fdb-bed3-5408f1cb089f\",\n" +
                "      \"bd2eee22-3440-4533-8809-9f4144b5c71d\",\n" +
                "      \"92567f7a-4fde-437c-9663-3a4c4a09ad96\",\n" +
                "      \"9481650f-c5e1-4d35-824a-d18893e47eb8\"\n" +
                "    ],\n" +
                "    \"createdDate\": 1600259459,\n" +
                "    \"ID\": \"\",\n" +
                "    \"artifacts\": \"artifacts\"\n" +
                "  }";
    }
    
    private String getRev1RootElements() {
        return "{\n" +
                "  \"ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\": {\n" +
                "    \"data\": [\n" +
                "      {\n" +
                "        \"ldp:membershipResource\": {\n" +
                "          \"@id\": \"#ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\"\n" +
                "        },\n" +
                "        \"@type\": [\n" +
                "          \"ldp:DirectContainer\",\n" +
                "          \"project.options:CommonProjectOptions\"\n" +
                "        ],\n" +
                "        \"ldp:contains\": [],\n" +
                "        \"ldp:hasMemberRelation\": \"kerml:ownedElement\",\n" +
                "        \"@id\": \"\",\n" +
                "        \"@context\": \"https://server.com:8111/osmc/schemas/umlElementContainer\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"@base\": \"https://server.com:8111/osmc/workspaces/e5b17282-4b76-43fd-a077-e838e2bbf0e5/resources/a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5/branches/5f31ea7b-144f-45b2-8899-0de92d99698a\",\n" +
                "        \"kerml:nsURI\": \"http://www.nomagic.com/ns/magicdraw/core/project/options/1.0\",\n" +
                "        \"@type\": \"project.options:CommonProjectOptions\",\n" +
                "        \"kerml:esiData\": {\n" +
                "          \"symbolStylesString\": \"AA\",\n" +
                "          \"optionsString\": \"UA=\",\n" +
                "          \"modelElementStyleString\": \"UEsA=\"\n" +
                "        },\n" +
                "        \"kerml:owner\": \"\",\n" +
                "        \"kerml:resource\": \"https://server.com:8111/osmc/resources/a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5\",\n" +
                "        \"kerml:esiID\": \"ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\",\n" +
                "        \"@id\": \"#ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\",\n" +
                "        \"kerml:revision\": \"https://server.com:8111/osmc/resources/a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5/revisions/3\",\n" +
                "        \"@context\": {\n" +
                "          \"kerml\": \"https://server.com:8111/osmc/schema/kerml/20140325\",\n" +
                "          \"project.options:CommonProjectOptions\": \"https://server.com:8111/osmc/schema/project.options/2014345/CommonProjectOptions\"\n" +
                "        },\n" +
                "        \"kerml:ownedElement\": [],\n" +
                "        \"kerml:modifiedTime\": \"20200916053059PDT\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"status\": 200\n" +
                "  },\n" +
                "  \"e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\": {\n" +
                "    \"data\": [\n" +
                "      {\n" +
                "        \"ldp:membershipResource\": {\n" +
                "          \"@id\": \"#e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\"\n" +
                "        },\n" +
                "        \"@type\": [\n" +
                "          \"ldp:DirectContainer\",\n" +
                "          \"uml:Model\"\n" +
                "        ],\n" +
                "        \"ldp:contains\": [\n" +
                "          {\n" +
                "            \"@id\": \"01f1a71b-61cc-424b-8571-f656109a0b07\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"@id\": \"11c3255a-89f3-443f-842c-31ea88be78fa\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"ldp:hasMemberRelation\": \"kerml:ownedElement\",\n" +
                "        \"@id\": \"\",\n" +
                "        \"@context\": \"https://server.com:8111/osmc/schemas/umlElementContainer\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"kerml:name\": \"Model\",\n" +
                "        \"@base\": \"https://server.com:8111/osmc/workspaces/e5b17282-4b76-43fd-a077-e838e2bbf0e5/resources/a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5/branches/5f31ea7b-144f-45b2-8899-0de92d99698a\",\n" +
                "        \"kerml:nsURI\": \"http://www.nomagic.com/magicdraw/UML/2.5.1\",\n" +
                "        \"@type\": \"uml:Model\",\n" +
                "        \"kerml:owner\": \"\",\n" +
                "        \"kerml:revision\": \"https://server.com:8111/osmc/resources/a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5/revisions/3\",\n" +
                "        \"@context\": {\n" +
                "          \"uml:Model\": \"https://server.com:8111/osmc/schema/uml/2014345/Model\",\n" +
                "          \"kerml\": \"https://server.com:8111/osmc/schema/kerml/20140325\"\n" +
                "        },\n" +
                "        \"kerml:ownedElement\": [\n" +
                "          {\n" +
                "            \"@id\": \"01f1a71b-61cc-424b-8571-f656109a0b07\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"@id\": \"11c3255a-89f3-443f-842c-31ea88be78fa\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"kerml:modifiedTime\": \"20200916053059PDT\",\n" +
                "        \"kerml:esiData\": {\n" +
                "          \"_classifierOfInheritedMember\": [],\n" +
                "          \"_directedRelationshipOfSource\": [],\n" +
                "          \"_representationText\": null,\n" +
                "          \"ownedRule\": [],\n" +
                "          \"packagedElement\": [\n" +
                "            {\n" +
                "              \"@id\": \"01f1a71b-61cc-424b-8571-f656109a0b07\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"packageMerge\": [],\n" +
                "          \"ownedTemplateSignature\": null,\n" +
                "          \"_considerIgnoreFragmentOfMessage\": [],\n" +
                "          \"_elementOfSyncElement\": [],\n" +
                "          \"_packageImportOfImportedPackage\": [],\n" +
                "          \"_namespaceOfMember\": [],\n" +
                "          \"ID\": \"PROJECT-72379b04-6dc6-43b6-8e2b-e79a5a6de142eee_1045467100313_135436_1\",\n" +
                "          \"_relationshipOfRelatedElement\": [],\n" +
                "          \"nameExpression\": null,\n" +
                "          \"owningTemplateParameter\": null,\n" +
                "          \"_manifestationOfUtilizedElement\": [],\n" +
                "          \"viewpoint\": \"\",\n" +
                "          \"_durationObservationOfEvent\": [],\n" +
                "          \"visibility\": \"public\",\n" +
                "          \"_messageOfSignature\": [],\n" +
                "          \"_templateParameterSubstitutionOfOwnedActual\": null,\n" +
                "          \"_componentOfPackagedElement\": null,\n" +
                "          \"_constraintOfConstrainedElement\": [],\n" +
                "          \"_namespaceOfImportedMember\": [],\n" +
                "          \"clientDependency\": [],\n" +
                "          \"_diagramOfContext\": [],\n" +
                "          \"nestedPackage\": [],\n" +
                "          \"ownedType\": [],\n" +
                "          \"name\": \"Model\",\n" +
                "          \"_directedRelationshipOfTarget\": [],\n" +
                "          \"elementImport\": [],\n" +
                "          \"_commentOfAnnotatedElement\": [],\n" +
                "          \"ownedMember\": [\n" +
                "            {\n" +
                "              \"@id\": \"01f1a71b-61cc-424b-8571-f656109a0b07\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"supplierDependency\": [],\n" +
                "          \"_informationFlowOfInformationTarget\": [],\n" +
                "          \"ownedComment\": [],\n" +
                "          \"_timeObservationOfEvent\": [],\n" +
                "          \"_packageMergeOfMergedPackage\": [],\n" +
                "          \"_informationFlowOfInformationSource\": [],\n" +
                "          \"URI\": \"\",\n" +
                "          \"_templateParameterOfOwnedDefault\": null,\n" +
                "          \"templateParameter\": null,\n" +
                "          \"member\": [\n" +
                "            {\n" +
                "              \"@id\": \"01f1a71b-61cc-424b-8571-f656109a0b07\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"ownedDiagram\": [],\n" +
                "          \"_templateParameterOfDefault\": [],\n" +
                "          \"owner\": [],\n" +
                "          \"profileApplication\": [],\n" +
                "          \"ownedElement\": [\n" +
                "            {\n" +
                "              \"@id\": \"01f1a71b-61cc-424b-8571-f656109a0b07\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"@id\": \"11c3255a-89f3-443f-842c-31ea88be78fa\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"visibility__from_PackageableElement\": \"public\",\n" +
                "          \"packageImport\": [],\n" +
                "          \"syncElement\": null,\n" +
                "          \"_activityPartitionOfRepresents\": [],\n" +
                "          \"_elementValueOfElement\": [],\n" +
                "          \"owningPackage\": null,\n" +
                "          \"nestingPackage\": [],\n" +
                "          \"templateBinding\": [],\n" +
                "          \"namespace\": [],\n" +
                "          \"ownedStereotype\": [],\n" +
                "          \"importedMember\": [],\n" +
                "          \"mdExtensions\": [],\n" +
                "          \"appliedStereotypeInstance\": {\n" +
                "            \"@id\": \"11c3255a-89f3-443f-842c-31ea88be78fa\"\n" +
                "          },\n" +
                "          \"_templateParameterSubstitutionOfActual\": [],\n" +
                "          \"_elementImportOfImportedElement\": []\n" +
                "        },\n" +
                "        \"kerml:resource\": \"https://server.com:8111/osmc/resources/a3e235d0-8dff-4351-8bb4-c96f9ea1e8f5\",\n" +
                "        \"kerml:esiID\": \"e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\",\n" +
                "        \"@id\": \"#e9fbfdc6-fce3-4918-9ddd-2a3a029403f2\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"status\": 200\n" +
                "  }}\n";
        //Real content truncated
    }

    private String getRev2_1Diff() {
        return "{\n" +
                "  \"removed\": [\n" +
                "    \"5d98719c-c720-487f-bb93-fa0a2496f08f\"\n" +
                "  ],\n" +
                "  \"added\": [\n" +
                "    \"89fffbf5-773e-473c-bbdc-a768e92554b7\",\n" +
                "    \"9fe2c8f7-bf0e-4629-88cc-82401ce920b9\",\n" +
                "    \"5392936d-f5b1-48fb-9644-d07fa23c80b9\",\n" +
                "    \"10b97541-e5c2-4f86-9579-39486f2d3894\",\n" +
                "    \"27ef3024-1f8a-4b81-a0a8-fc5284964898\"\n" +
                "  ],\n" +
                "  \"changed\": [\n" +
                "    \"ec2cef7b-7ef8-4250-b6e3-ebdb57ef7604\",\n" +
                "    \"699c2b31-3847-4ffc-8e9f-f09150162d92\",\n" +
                "    \"a0c5c471-dc8f-4cfa-b594-365ca8e4e552\",\n" +
                "    \"92567f7a-4fde-437c-9663-3a4c4a09ad96\",\n" +
                "    \"eb9877b2-5542-4a86-abd2-842e62627ad9\",\n" +
                "    \"973fbef0-828e-4511-b1cd-ffd5e74aaec2\",\n" +
                "    \"1a1dbf67-bddb-4321-96fe-a01be9b76347\"\n" +
                "  ],\n" +
                "  \"empty\": false\n" +
                "}";
    }

    private Map<String, JSONObject> mapChildJsonObjects(JSONObject parent){
        Map<String, JSONObject> result = new HashMap<>();
        Set<String> keyset = parent.keySet();
        for (String key : keyset) {
            try {
                JSONObject child = parent.getJSONObject(key);
                result.put(key, child);
            } catch(Exception ex) {}
        }
        return result;
    }
}