package org.openmbee.syncservice.twc.filter;

import org.json.JSONObject;

import java.util.Set;

public interface ElementFilter {
    void add(JSONObject element);
    Set<String> getIgnoredIds();
}
