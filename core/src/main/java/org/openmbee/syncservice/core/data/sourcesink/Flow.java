package org.openmbee.syncservice.core.data.sourcesink;

public class Flow {
    private Source source;
    private Sink sink;

    public Flow(Source source, Sink sink) {
        this.source = source;
        this.sink = sink;
    }

    public Source getSource() {
        return source;
    }

    public Sink getSink() {
        return sink;
    }
}
