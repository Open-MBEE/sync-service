package org.openmbee.syncservice.core.data.sourcesink;

import org.openmbee.syncservice.core.data.services.CommitReciprocityService;
import org.openmbee.syncservice.core.translation.TranslationChain;
import org.openmbee.syncservice.core.translation.TranslationChainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class SourceSinkFactory { //Does not inherit from ISourceSinkFactory on purpose
    private static final Logger logger = LoggerFactory.getLogger(SourceSinkFactory.class);

    private List<ISourceSinkFactory> sourceSinkFactories;
    private TranslationChainService translationChainService;

    @Autowired
    public void setSourceSinkFactories(List<ISourceSinkFactory> sourceSinkFactories) {
        this.sourceSinkFactories = sourceSinkFactories;
    }

    @Autowired
    public void setTranslationChainService(TranslationChainService translationChainService) {
        this.translationChainService = translationChainService;
    }

    public Optional<Sink> getSink(ProjectEndpoint sinkEndpoint){
        return sourceSinkFactories.stream().map(f -> f.getSink(sinkEndpoint)).filter(Objects::nonNull).findFirst();
    }

    public Optional<Source> getSource(ProjectEndpoint sourceEndpoint){
        return sourceSinkFactories.stream().map(f -> f.getSource(sourceEndpoint)).filter(Objects::nonNull).findFirst();
    }

    public Optional<CommitReciprocityService> getCommitReciprocityService(Source source, Sink sink){
        return sourceSinkFactories.stream().map(f -> f.getCommitReciprocityService(source, sink))
                .filter(Objects::nonNull).findFirst();
    }

    public Optional<ReciprocatedFlow> getReciprocatedFlow(ProjectEndpoint sourceEndpoint, ProjectEndpoint sinkEndpoint) {
        Flow flow = getFlow(sourceEndpoint, sinkEndpoint).orElse(null);

        if(flow == null) {
            return Optional.empty();
        }

        CommitReciprocityService commitReciprocityService = getCommitReciprocityService(flow.getSource(), flow.getSink())
                .orElse(null);
        if(commitReciprocityService == null) {
            logger.error("Could not create flow due to missing commit reciprocity service");
            return Optional.empty();
        }

        return Optional.of(new ReciprocatedFlow(flow.getSource(), flow.getSink(),
                commitReciprocityService));
    }

    public Optional<Flow> getFlow(ProjectEndpoint sourceEndpoint, ProjectEndpoint sinkEndpoint) {
        Source source = getSource(sourceEndpoint).orElse(null);
        Sink sink = getSink(sinkEndpoint).orElse(null);

        if(source == null || sink == null) {
            logger.error("Could not create flow due to missing sink or source");
            return Optional.empty();
        }

        TranslationChain translationChain = translationChainService.getTranslationChain(source, sink);
        return Optional.of(new Flow(translationChain.getSource(), translationChain.getTranslatingSink()));
    }
}
