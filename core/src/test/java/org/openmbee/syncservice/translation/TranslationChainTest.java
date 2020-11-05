package org.openmbee.syncservice.translation;

import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.translation.TranslationChain;
import org.openmbee.syncservice.core.translation.Translator;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TranslationChainTest {

    @Test
    public void constructTest() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);
        Translator translator = mock(Translator.class);

        TranslationChain chain = new TranslationChain(source, sink, List.of(translator));
        assertSame(source, chain.getSource());
        assertNotSame(sink, chain.getTranslatingSink());
        assertSame(sink, chain.getTranslatingSink().getSink());
        assertSame(translator, chain.getTranslatingSink().getTranslationChain().get(0));

    }

    @Test
    public void toStringTest() {
        Source source = mock(Source.class);
        Sink sink = mock(Sink.class);
        Translator translator = mock(Translator.class);

        TranslationChain chain = new TranslationChain(source, sink, List.of(translator));
        String s = chain.toString();
        assertNotNull(s);
    }

}