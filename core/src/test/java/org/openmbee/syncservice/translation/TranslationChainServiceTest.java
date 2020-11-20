package org.openmbee.syncservice.translation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Syntax;
import org.openmbee.syncservice.core.translation.TranslationChain;
import org.openmbee.syncservice.core.translation.TranslationChainService;
import org.openmbee.syncservice.core.translation.Translator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TranslationChainServiceTest {

    @Mock
    private Syntax syntax1;

    @Mock
    private Syntax syntax2;

    @Mock
    private Syntax syntax3;

    @Mock
    private Syntax syntax4;

    @Mock
    private Translator translator12;

    @Mock
    private Translator translator23;


    @Spy
    private TranslationChainService translationChainService;

    @Mock
    Source source1;

    @Mock
    Source source2;

    @Mock
    Sink sink1;

    @Mock
    Sink sink2;

    @Mock
    Sink sink3;

    @Mock
    Sink sink4;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(translator12.getSourceSyntax()).thenReturn(syntax1);
        when(translator12.getSinkSyntax()).thenReturn(syntax2);
        when(translator23.getSourceSyntax()).thenReturn(syntax2);
        when(translator23.getSinkSyntax()).thenReturn(syntax3);

        when(source1.getSyntax()).thenReturn(syntax1);
        when(source2.getSyntax()).thenReturn(syntax2);
        when(sink1.getSyntax()).thenReturn(syntax1);
        when(sink2.getSyntax()).thenReturn(syntax2);
        when(sink3.getSyntax()).thenReturn(syntax3);
        when(sink4.getSyntax()).thenReturn(syntax4);

        translationChainService.setTranslators(List.of(translator12, translator23));
    }

    @Test
    public void test1to1() {
        TranslationChain chain = translationChainService.getTranslationChain(source1, sink1);
        assertNotNull(chain);
        assertTrue(chain.getTranslatingSink().getTranslationChain().isEmpty());
    }

    @Test
    public void test1to2() {
        TranslationChain chain = translationChainService.getTranslationChain(source1, sink2);
        assertNotNull(chain);
        assertEquals(1, chain.getTranslatingSink().getTranslationChain().size());
        assertSame(translator12, chain.getTranslatingSink().getTranslationChain().get(0));
    }

    @Test
    public void test2to3() {
        TranslationChain chain = translationChainService.getTranslationChain(source2, sink3);
        assertNotNull(chain);
        assertEquals(1, chain.getTranslatingSink().getTranslationChain().size());
        assertSame(translator23, chain.getTranslatingSink().getTranslationChain().get(0));
    }

    @Test
    public void test1to3() {
        TranslationChain chain = translationChainService.getTranslationChain(source1, sink3);
        assertNotNull(chain);
        assertEquals(2, chain.getTranslatingSink().getTranslationChain().size());
        assertSame(translator12, chain.getTranslatingSink().getTranslationChain().get(0));
        assertSame(translator23, chain.getTranslatingSink().getTranslationChain().get(1));
    }

    @Test
    public void testNoPath() {
        try {
            TranslationChain chain = translationChainService.getTranslationChain(source1, sink4);
            fail("Should throw an exception");
        } catch(Exception ex) {}

    }
}