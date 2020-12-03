package org.openmbee.syncservice.twc.sourcesink;

import org.json.JSONObject;
import org.openmbee.syncservice.twc.filter.ElementFilter;

import java.util.Collection;
import java.util.Map;

public interface TeamworkCloudSource {
    Map<String, JSONObject> getElements(String revision, Collection<String> elementIds);
    Map<String, JSONObject> getElements(String revision, Collection<String> elementIds, ElementFilter filter);
    Map<String, JSONObject> getElementsRecursively(String revision, Collection<String> elementIds);
    Map<String, JSONObject> getElementsRecursively(String revision, Collection<String> elementIds, ElementFilter filter);
}
