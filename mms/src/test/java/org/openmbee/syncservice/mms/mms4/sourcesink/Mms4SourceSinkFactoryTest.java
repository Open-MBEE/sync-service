package org.openmbee.syncservice.mms.mms4.sourcesink;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.core.data.services.CommitReciprocityService;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.SinkDecorator;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Mms4SourceSinkFactoryTest {

    @Mock
    private ApplicationContext context;
    @Mock
    private AutowireCapableBeanFactory autowireCapableBeanFactory;
    @Mock
    private Source source;
    @Mock
    private Sink sink;
    @Mock
    private Mms4Sink mms4Sink;

    private interface DecoratingSink extends SinkDecorator, Sink {}
    @Mock
    private DecoratingSink decoratingSink;
    @Mock
    private ProjectEndpoint endpoint;

    @Spy
    @InjectMocks
    private Mms4SourceSinkFactory factory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(context.getAutowireCapableBeanFactory()).thenReturn(autowireCapableBeanFactory);
    }

    @Test
    public void getSinkTest() {
        Sink s = factory.getSink(endpoint);
        assertNotNull(s);
        verify(autowireCapableBeanFactory).autowireBean(any());
    }

    @Test
    public void getSourceTest() {
        Source s = factory.getSource(endpoint);
        assertNull(s);
    }

    @Test
    public void getCommitReciprocityService_Mms4Sink() {
        CommitReciprocityService s = factory.getCommitReciprocityService(source, mms4Sink);
        assertNotNull(s);
        verify(autowireCapableBeanFactory).autowireBean(any());
    }

    @Test
    public void getCommitReciprocityService_NormalSink() {
        CommitReciprocityService s = factory.getCommitReciprocityService(source, sink);
        assertNull(s);
    }

    @Test
    public void getCommitReciprocityService_DecoratedMms4Sink() {
        when(decoratingSink.getSink()).thenReturn(mms4Sink).thenReturn(mms4Sink);
        CommitReciprocityService s = factory.getCommitReciprocityService(source, decoratingSink);
        assertNotNull(s);
        verify(autowireCapableBeanFactory).autowireBean(any());
    }

    @Test
    public void getCommitReciprocityService_DecoratedNormalSink() {
        when(decoratingSink.getSink()).thenReturn(sink);
        CommitReciprocityService s = factory.getCommitReciprocityService(source, decoratingSink);
        assertNull(s);
    }
}