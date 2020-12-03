package org.openmbee.syncservice.twc.sourcesink;

import org.openmbee.syncservice.core.data.services.CommitReciprocityService;
import org.openmbee.syncservice.core.data.sourcesink.ISourceSinkFactory;
import org.openmbee.syncservice.core.data.sourcesink.ProjectEndpoint;
import org.openmbee.syncservice.core.data.sourcesink.Sink;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TeamworkCloudSourceSinkFactory implements ISourceSinkFactory {

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Sink getSink(ProjectEndpoint sinkEndpoint) {
        //TODO will need for reverse sync back from MMS
        return null;
    }

    @Override
    public Source getSource(ProjectEndpoint sourceEndpoint) {
        TeamworkCloud19_3Source source = autowire(new TeamworkCloud19_3Source(sourceEndpoint));
        if(source.isValid()) {
            return source;
        }
        return null;
    }

    @Override
    public CommitReciprocityService getCommitReciprocityService(Source source, Sink sink) {
        //TWC does not have built in commit reciprocity
        return null;
    }

    private <T> T autowire(T object) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
        return object;
    }
}
