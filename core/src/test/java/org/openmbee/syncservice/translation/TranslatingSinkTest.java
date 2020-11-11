package org.openmbee.syncservice.translation;

import org.openmbee.syncservice.core.data.branches.Branch;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.commits.CommitChanges;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.translation.TranslatingSink;
import org.openmbee.syncservice.core.translation.Translator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TranslatingSinkTest {

    @Mock
    private Translator translator1;

    @Mock
    private Translator translator2;

    @Mock
    private Sink sink;

    @Mock
    private Source source;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getBranchByNameTest() {
        String name = "name";
        String name1 = "name1";
        String name2 = "name2";
        Branch branch = mock(Branch.class);
        when(translator1.translateBranchName(name)).thenReturn(name1);
        when(translator2.translateBranchName(name1)).thenReturn(name2);
        when(sink.getBranchByName(name2)).thenReturn(branch);
        TranslatingSink t = new TranslatingSink(List.of(translator1, translator2), sink);

        Branch b = t.getBranchByName(name);
        assertSame(branch, b);
    }

    @Test
    public void getBranchByNameNoChain() {
        String name = "name";
        Branch branch = mock(Branch.class);
        when(sink.getBranchByName(name)).thenReturn(branch);
        TranslatingSink t = new TranslatingSink(null, sink);

        Branch b = t.getBranchByName(name);
        assertSame(branch, b);
    }

    @Test
    public void commitChangesTest() {
        Branch branch = mock(Branch.class);
        CommitChanges changes = mock(CommitChanges.class);
        CommitChanges changes1 = mock(CommitChanges.class);
        CommitChanges changes2 = mock(CommitChanges.class);

        when(translator1.translateCommitChanges(source, changes)).thenReturn(changes1);
        when(translator2.translateCommitChanges(source,  changes1)).thenReturn(changes2);
        TranslatingSink t = new TranslatingSink(List.of(translator1, translator2), sink);

        t.commitChanges(source, branch, changes);

        verify(sink).commitChanges(source, branch, changes2);
    }

    @Test
    public void commitChangesNoChain() {
        Branch branch = mock(Branch.class);
        CommitChanges changes = mock(CommitChanges.class);

        TranslatingSink t = new TranslatingSink(null, sink);

        t.commitChanges(source, branch, changes);

        verify(sink).commitChanges(source, branch, changes);
    }

    @Test
    public void getSyntaxTest() {
        TranslatingSink t = new TranslatingSink(null, sink);
        t.getSyntax();
        verify(sink).getSyntax();
    }

    @Test
    public void getCommitHistoryTest() {
        TranslatingSink t = new TranslatingSink(null, sink);
        t.getCommitHistory();
        verify(sink).getCommitHistory();
    }

    @Test
    public void toStringTest() {
        TranslatingSink t = new TranslatingSink(List.of(translator1, translator2), sink);
        String s = t.toString();
        assertNotNull(s);
    }
}