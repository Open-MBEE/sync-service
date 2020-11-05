package org.openmbee.syncservice.mms.mms4.sourcesink;

import org.openmbee.syncservice.core.data.services.CommitReciprocityService;
import org.openmbee.syncservice.mms.mms4.services.Mms4CommitReciprocityService;
import org.openmbee.syncservice.core.data.sourcesink.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Mms4SourceSinkFactory implements ISourceSinkFactory {

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public CommitReciprocityService getCommitReciprocityService(Source source, Sink sink) {
        if(sink instanceof Mms4Sink) {
            return autowire(new Mms4CommitReciprocityService(source, (Mms4Sink) sink));
        } else if(sink instanceof SinkDecorator && ((SinkDecorator)sink).getSink() instanceof Mms4Sink)  {
            return autowire(new Mms4CommitReciprocityService(source, (Mms4Sink) ((SinkDecorator)sink).getSink()));
        }
        return null;
    }

    @Override
    public Sink getSink(ProjectEndpoint sinkEndpoint) {
        //TODO check that this is an MMS 4 endpoint
        return autowire(new Mms4Sink(sinkEndpoint));
    }

    @Override
    public Source getSource(ProjectEndpoint sourceEndpoint) {
        //TODO need this for syncing back to TWC eventually
        return null;
    }

    private <T> T autowire(T object) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
        return object;
    }
}
