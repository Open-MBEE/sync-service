package org.openmbee.syncservice.core.syntax.fields;

import org.openmbee.syncservice.core.syntax.path.KeyedSegment;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Field <F,V> {

    private static final Logger logger = LoggerFactory.getLogger(Field.class);

    private final F field;
    private final KeyedSegment<?,V> path;

    Field(F field, KeyedSegment<?,V> path) {
        this.field = field;
        this.path = path;
    }

    public F getField() {
        return field;
    }

    public void put(V value, JSONObject out) {
        if(! path.getLeaf().isValidValue(value)) {
            logger.error("Invalid value attempted to put in field " + getField().toString());
            return;
        }
        path.put(value, out);
    }

    public V get(JSONObject jsonObject) {
        return path.get(jsonObject);
    }
}
