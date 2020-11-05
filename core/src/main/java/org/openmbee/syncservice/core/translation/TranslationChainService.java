package org.openmbee.syncservice.core.translation;

import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Syntax;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TranslationChainService {
    private static Logger LOG = LoggerFactory.getLogger(TranslationChainService.class);

    private Graph<Syntax, Translator> translators;

    @Autowired
    public void setTranslators(List<Translator> translators) {

        GraphBuilder<Syntax, Translator, Graph<Syntax, Translator>> builder =
                GraphTypeBuilder.<Syntax, Translator> directed()
                        .allowingMultipleEdges(true)
                        .allowingSelfLoops(true)
                        .edgeClass(Translator.class)
                        .buildGraphBuilder();

        translators.forEach(v -> builder.addEdge(v.getSourceSyntax(), v.getSinkSyntax(), v));
        this.translators = builder.buildAsUnmodifiable();
    }

    public TranslationChain getTranslationChain(Source source, Sink sink) {

        if(source.getSyntax().equals(sink.getSyntax())) {
            //No Translation required
            return new TranslationChain(source, sink, Collections.emptyList());
        }

        DijkstraShortestPath<Syntax, Translator> dijkstraShortestPath = new DijkstraShortestPath<>(translators);
        try {
            GraphPath<Syntax, Translator> path = dijkstraShortestPath.getPath(source.getSyntax(), sink.getSyntax());
            return new TranslationChain(source, sink, path.getEdgeList());
        } catch(Exception ex) {
            LOG.error("Could not find translation path", ex);
            throw ex;
        }
    }


}
