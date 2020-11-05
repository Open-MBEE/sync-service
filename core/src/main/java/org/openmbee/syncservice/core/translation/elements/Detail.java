package org.openmbee.syncservice.core.translation.elements;

import org.json.JSONObject;

public interface Detail {
    void transfer(JSONObject original, JSONObject out);
}
