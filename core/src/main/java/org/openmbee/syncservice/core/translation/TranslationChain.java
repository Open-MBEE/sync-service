package org.openmbee.syncservice.core.translation;



import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;

import java.util.List;

public class TranslationChain {

    private Source source;
    private TranslatingSink translatingSink;

    public TranslationChain(Source source, Sink sink, List<Translator> translationChain) {
        this.source = source;
        this.translatingSink = new TranslatingSink(translationChain, sink);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(source.toString()).append("\n").append(translatingSink.toString());
        return s.toString();
    }

    public Source getSource() {
        return source;
    }

    public TranslatingSink getTranslatingSink() {
        return translatingSink;
    }


}
