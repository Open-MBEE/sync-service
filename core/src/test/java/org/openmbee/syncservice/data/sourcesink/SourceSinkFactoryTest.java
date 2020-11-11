package org.openmbee.syncservice.data.sourcesink;

import org.openmbee.syncservice.core.data.services.CommitReciprocityService;
import org.openmbee.syncservice.core.data.sourcesink.*;
import org.openmbee.syncservice.core.translation.TranslatingSink;
import org.openmbee.syncservice.core.translation.TranslationChain;
import org.openmbee.syncservice.core.translation.TranslationChainService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SourceSinkFactoryTest {

    private ProjectEndpoint endpoint1;
    private ProjectEndpoint endpoint2;

    @Mock
    private ISourceSinkFactory factory1;

    @Mock
    private ISourceSinkFactory factory2;

    @Mock
    private TranslationChainService translationChainService;

    @Spy
    @InjectMocks
    private SourceSinkFactory sourceSinkFactory;

    @Before
    public void setup() {
        endpoint1 = new ProjectEndpoint();
        endpoint2 = new ProjectEndpoint();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getSinkTest1() {
        Sink sink1 = mock(Sink.class);
        Sink sink2 = mock(Sink.class);
        when(factory1.getSink(endpoint1)).thenReturn(sink1);
        when(factory2.getSink(endpoint1)).thenReturn(sink2);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));
        Sink rsink = sourceSinkFactory.getSink(endpoint1).orElse(null);

        assertSame(sink1, rsink);
        verify(factory1, times(1)).getSink(endpoint1);
        verify(factory2, times(0)).getSink(endpoint1);
    }

    @Test
    public void getSinkTest2() {
        Sink sink1 = null;
        Sink sink2 = mock(Sink.class);
        when(factory1.getSink(endpoint1)).thenReturn(sink1);
        when(factory2.getSink(endpoint1)).thenReturn(sink2);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));
        Sink rsink = sourceSinkFactory.getSink(endpoint1).orElse(null);

        assertSame(sink2, rsink);
        verify(factory1, times(1)).getSink(endpoint1);
        verify(factory2, times(1)).getSink(endpoint1);
    }

    @Test
    public void getSourceTest1() {
        Source source1 = mock(Source.class);
        Source source2 = mock(Source.class);
        when(factory1.getSource(endpoint1)).thenReturn(source1);
        when(factory2.getSource(endpoint1)).thenReturn(source2);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));
        Source rsource = sourceSinkFactory.getSource(endpoint1).orElse(null);

        assertSame(source1, rsource);
        verify(factory1, times(1)).getSource(endpoint1);
        verify(factory2, times(0)).getSource(endpoint1);
    }

    @Test
    public void getSourceTest2() {
        Source source1 = null;
        Source source2 = mock(Source.class);
        when(factory1.getSource(endpoint1)).thenReturn(source1);
        when(factory2.getSource(endpoint1)).thenReturn(source2);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));
        Source rsource = sourceSinkFactory.getSource(endpoint1).orElse(null);

        assertSame(source2, rsource);
        verify(factory1, times(1)).getSource(endpoint1);
        verify(factory2, times(1)).getSource(endpoint1);
    }

    @Test
    public void getCommitReciprocityServiceTest1() {
        Sink sink = mock(Sink.class);
        Source source = mock(Source.class);

        CommitReciprocityService svc1 = mock(CommitReciprocityService.class);
        CommitReciprocityService svc2 = mock(CommitReciprocityService.class);
        when(factory1.getCommitReciprocityService(source, sink)).thenReturn(svc1);
        when(factory2.getCommitReciprocityService(source, sink)).thenReturn(svc2);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));
        CommitReciprocityService rsvc = sourceSinkFactory.getCommitReciprocityService(source, sink).orElse(null);

        assertSame(svc1, rsvc);
        verify(factory1, times(1)).getCommitReciprocityService(source, sink);
        verify(factory2, times(0)).getCommitReciprocityService(source, sink);
    }

    @Test
    public void getCommitReciprocityServiceTest2() {
        Sink sink = mock(Sink.class);
        Source source = mock(Source.class);

        CommitReciprocityService svc1 = null;
        CommitReciprocityService svc2 = mock(CommitReciprocityService.class);
        when(factory1.getCommitReciprocityService(source, sink)).thenReturn(svc1);
        when(factory2.getCommitReciprocityService(source, sink)).thenReturn(svc2);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));
        CommitReciprocityService rsvc = sourceSinkFactory.getCommitReciprocityService(source, sink).orElse(null);

        assertSame(svc2, rsvc);
        verify(factory1, times(1)).getCommitReciprocityService(source, sink);
        verify(factory2, times(1)).getCommitReciprocityService(source, sink);
    }

    @Test
    public void getFlowTest1() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);
        Source tsource = mock(Source.class);
        TranslatingSink tsink = mock(TranslatingSink.class);
        TranslationChain translationChain = mock(TranslationChain.class);

        when(factory1.getSource(endpoint1)).thenReturn(source);
        when(factory2.getSink(endpoint2)).thenReturn(sink);
        when(source.canSendTo(sink)).thenReturn(true);
        when(sink.canReceiveFrom(source)).thenReturn(true);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));

        when(translationChainService.getTranslationChain(source, sink)).thenReturn(translationChain);
        when(translationChain.getSource()).thenReturn(tsource);
        when(translationChain.getTranslatingSink()).thenReturn(tsink);

        Flow flow = sourceSinkFactory.getFlow(endpoint1, endpoint2).orElse(null);

        assertNotNull(flow);
        assertSame(tsource, flow.getSource());
        assertSame(tsink, flow.getSink());
    }

    @Test
    public void getFlowTest2() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);
        Source tsource = mock(Source.class);
        TranslatingSink tsink = mock(TranslatingSink.class);
        TranslationChain translationChain = mock(TranslationChain.class);

        when(factory1.getSource(endpoint1)).thenReturn(source);
        when(factory2.getSink(endpoint2)).thenReturn(null);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));

        when(translationChainService.getTranslationChain(source, sink)).thenReturn(translationChain);
        when(translationChain.getSource()).thenReturn(tsource);
        when(translationChain.getTranslatingSink()).thenReturn(tsink);

        Flow flow = sourceSinkFactory.getFlow(endpoint1, endpoint2).orElse(null);

        assertNull(flow);
    }

    @Test
    public void getFlowTest3() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);
        Source tsource = mock(Source.class);
        TranslatingSink tsink = mock(TranslatingSink.class);
        TranslationChain translationChain = mock(TranslationChain.class);

        when(factory1.getSource(endpoint1)).thenReturn(null);
        when(factory2.getSink(endpoint2)).thenReturn(sink);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));

        when(translationChainService.getTranslationChain(source, sink)).thenReturn(translationChain);
        when(translationChain.getSource()).thenReturn(tsource);
        when(translationChain.getTranslatingSink()).thenReturn(tsink);

        Flow flow = sourceSinkFactory.getFlow(endpoint1, endpoint2).orElse(null);

        assertNull(flow);
    }

    @Test
    public void getRFlowTest_Normal() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);
        Source tsource = mock(Source.class);
        TranslatingSink tsink = mock(TranslatingSink.class);
        TranslationChain translationChain = mock(TranslationChain.class);
        CommitReciprocityService commitReciprocityService = mock(CommitReciprocityService.class);

        when(factory1.getSource(endpoint1)).thenReturn(source);
        when(factory2.getSink(endpoint2)).thenReturn(sink);
        when(source.canSendTo(sink)).thenReturn(true);
        when(sink.canReceiveFrom(source)).thenReturn(true);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));

        when(translationChainService.getTranslationChain(source, sink)).thenReturn(translationChain);
        when(translationChain.getSource()).thenReturn(tsource);
        when(translationChain.getTranslatingSink()).thenReturn(tsink);

        when(factory1.getCommitReciprocityService(tsource, tsink)).thenReturn(commitReciprocityService);
        when(commitReciprocityService.getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit()).thenReturn(null);

        ReciprocatedFlow flow = sourceSinkFactory.getReciprocatedFlow(endpoint1, endpoint2).orElse(null);
        assertNotNull(flow);
        assertSame(tsource, flow.getSource());
        assertSame(tsink, flow.getSink());

        flow.getUnreciprocatedCommits();
        verify(commitReciprocityService).getUnreciprocatedSourceCommitsSinceLastReciprocatedCommit();
    }

    @Test
    public void getRFlowTest_TranslationChainThrows() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);

        when(factory1.getSource(endpoint1)).thenReturn(source);
        when(factory2.getSink(endpoint2)).thenReturn(sink);
        when(source.canSendTo(sink)).thenReturn(true);
        when(sink.canReceiveFrom(source)).thenReturn(true);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));

        when(translationChainService.getTranslationChain(source, sink)).thenThrow(new RuntimeException("Mock exception"));

        ReciprocatedFlow flow = sourceSinkFactory.getReciprocatedFlow(endpoint1, endpoint2).orElse(null);
        assertNull(flow);
    }

    @Test
    public void getRFlowTest_NoReciprocityService() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);
        Source tsource = mock(Source.class);
        TranslatingSink tsink = mock(TranslatingSink.class);
        TranslationChain translationChain = mock(TranslationChain.class);

        when(factory1.getSource(endpoint1)).thenReturn(source);
        when(factory2.getSink(endpoint2)).thenReturn(sink);
        when(source.canSendTo(sink)).thenReturn(true);
        when(sink.canReceiveFrom(source)).thenReturn(true);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));

        when(translationChainService.getTranslationChain(source, sink)).thenReturn(translationChain);
        when(translationChain.getSource()).thenReturn(tsource);
        when(translationChain.getTranslatingSink()).thenReturn(tsink);

        ReciprocatedFlow flow = sourceSinkFactory.getReciprocatedFlow(endpoint1, endpoint2).orElse(null);

        assertNull(flow);
    }

    @Test
    public void getRFlowTest_SourceCantSend() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);
        Source tsource = mock(Source.class);
        TranslatingSink tsink = mock(TranslatingSink.class);
        TranslationChain translationChain = mock(TranslationChain.class);

        when(factory1.getSource(endpoint1)).thenReturn(source);
        when(factory2.getSink(endpoint2)).thenReturn(sink);
        when(source.canSendTo(sink)).thenReturn(false);
        when(sink.canReceiveFrom(source)).thenReturn(true);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));

        when(translationChainService.getTranslationChain(source, sink)).thenReturn(translationChain);
        when(translationChain.getSource()).thenReturn(tsource);
        when(translationChain.getTranslatingSink()).thenReturn(tsink);

        ReciprocatedFlow flow = sourceSinkFactory.getReciprocatedFlow(endpoint1, endpoint2).orElse(null);

        assertNull(flow);
    }

    @Test
    public void getRFlowTest_SinkCantReceive() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);
        Source tsource = mock(Source.class);
        TranslatingSink tsink = mock(TranslatingSink.class);
        TranslationChain translationChain = mock(TranslationChain.class);

        when(factory1.getSource(endpoint1)).thenReturn(source);
        when(factory2.getSink(endpoint2)).thenReturn(sink);
        when(source.canSendTo(sink)).thenReturn(true);
        when(sink.canReceiveFrom(source)).thenReturn(false);

        sourceSinkFactory.setSourceSinkFactories(List.of(factory1, factory2));

        when(translationChainService.getTranslationChain(source, sink)).thenReturn(translationChain);
        when(translationChain.getSource()).thenReturn(tsource);
        when(translationChain.getTranslatingSink()).thenReturn(tsink);

        ReciprocatedFlow flow = sourceSinkFactory.getReciprocatedFlow(endpoint1, endpoint2).orElse(null);

        assertNull(flow);
    }

    @Test
    public void getRFlowTest_NoFactories() {

        sourceSinkFactory.setSourceSinkFactories(List.of());

        ReciprocatedFlow flow = sourceSinkFactory.getReciprocatedFlow(endpoint1, endpoint2).orElse(null);

        assertNull(flow);
    }
}