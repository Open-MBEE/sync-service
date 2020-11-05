package org.openmbee.syncservice.core.syntax.path;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyedLeafSegment<V> extends KeyedSegment<V,V> implements LeafSegment {
    private static final Logger logger = LoggerFactory.getLogger(KeyedLeafSegment.class);

    public KeyedLeafSegment(String key, Class<V> vClass) {
        super(key, vClass, null);
    }

    @Override
    public void put(V value, JSONObject parentObj) {
        if(parentObj == null) {
            return;
        }
        parentObj.put(getKey(), value);
    }

    @Override
    public V get(JSONObject parentObj) {
        if(parentObj != null && parentObj.has(getKey()) && !parentObj.isNull(getKey())) {
            Object obj = parentObj.get(getKey());
            try {
                return getTClass().cast(obj);
            } catch(ClassCastException ex) {
                logger.error("Could not convert " + obj.getClass() + " to " + getTClass().getName());
            }
        }
        return null;
    }

    public LeafSegment getLeaf() {
        return this;
    }

    @Override
    public boolean isValidValue(Object value) {
        try {
            getTClass().cast(value);
            return true;
        } catch(ClassCastException ex) {
            return false;
        }
    }
}
