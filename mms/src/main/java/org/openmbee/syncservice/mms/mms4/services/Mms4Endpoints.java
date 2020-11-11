package org.openmbee.syncservice.mms.mms4.services;

public enum Mms4Endpoints {
    GET_TWC_REVISIONS("projects/%s/refs/%s/twc-revisions?reverseOrder=%s&limit=%s"),
    GET_REFS("projects/%s/refs"),
    GET_REF("projects/%s/refs/%s"),
    GET_COMMIT("projects/%s/commits/%s"),
    GET_REF_COMMITS("projects/%s/refs/%s/commits"),
    GET_PROJECT("projects/%s"),
    POST_ELEMENTS("projects/%s/refs/%s/elements"),
    DELETE_ELEMENTS("projects/%s/refs/%s/elements"),
    CREATE_REFS("projects/%s/refs"),
    UPDATE_TWC_REVISION("projects/%s/commits/%s/twc-revision/%s");

    private String path;

    public String getPath() {
        return path;
    }

    Mms4Endpoints(String path) {
        this.path = path;
    }


    public String buildUrl(String mmsHost, String... params) {
        String url = String.format("%s/%s", mmsHost, getPath());
        if (params == null) {
            return url;
        }
        return String.format(url, (Object[]) params);
    }
}
