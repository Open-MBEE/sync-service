package org.openmbee.syncservice.core.data.sourcesink;

public class ProjectEndpoint {

    private String host;
    private String collection;
    private String project;
    private String token;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return String.join("/", getHost(), getCollection(), getProject());
    }
}
