package org.openmbee.syncservice.twc;

public enum TeamworkCloudEndpoints {

    GET_BRANCH("workspaces/%s/resources/%s/branches/%s"),
    GET_BRANCHES("workspaces/%s/resources/%s/branches?includeBody=%s"),
    GET_TWC_REVISION_INFO("resources/%s/branches/%s"),
    GET_VERSION_INFO("version"),
    GET_PROJECT_REVISIONS("workspaces/%s/resources/%s/revisions?includeBody=%s"),
    GET_PROJECT_LATEST_REVISION("resources/%s/revisions?includeBody=true&page=1&items=1"),
    GET_PROJECT_REVISION("workspaces/%s/resources/%s/revisions/%s"),
    GET_DIFF_REVISIONS("workspaces/%s/resources/%s/revisiondiff?target=%s&source=%s"),
    POST_FETCH_ELEMENTS_AT_REVISION("workspaces/%s/resources/%s/revisions/%s/elements"),
    GET_PROJECT_INFO("workspaces/%s/resources/%s");

    private String path;

    public String getPath() {
        return path;
    }

    TeamworkCloudEndpoints(String path) {
        this.path = path;
    }

    public String buildUrl(String host, String... params) {
        String url = String.format("%s/osmc/%s", host, getPath());

        if (params == null) {
            return url;
        }
        return String.format(url, (Object[]) params);
    }

}
