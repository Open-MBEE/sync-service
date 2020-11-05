package org.openmbee.syncservice.core.syntax.path;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KeyedSegment<T, V> extends PathSegment<JSONObject, T, V> {
    private static final Logger logger = LoggerFactory.getLogger(KeyedSegment.class);

    private String key;

    public KeyedSegment(String key, Class<T> tClass, PathSegment<T,?,V> downstream) {
        super(tClass);
        this.key = key;
        this.setDownstream(downstream);
    }

    public String getKey() {
        return key;
    }

    @Override
    public void put(V value, JSONObject parentObj) {
        if(parentObj == null) {
            return;
        }

        PathSegment<T,?,V> nextSegment = getDownstream();
        if(nextSegment == null) {
            logger.error("Incomplete path detected, stopped at " + getKey());
            return;
        }

        T obj =  null;
        if(parentObj.isNull(getKey())) {
            obj = newT();
            parentObj.put(getKey(), obj);
        } else {
            Object rawObj = parentObj.get(getKey());
            if (!getTClass().isInstance(rawObj)) {
                logger.error("Expected " + getTClass().getName() + " but found " + rawObj.getClass());
                return;
            }
            obj = getTClass().cast(rawObj);
        }

        nextSegment.put(value, obj);
    }


    @Override
    public V get(JSONObject parentObj) {
        if(parentObj == null) {
            return null;
        }

        PathSegment<T,?,V> nextSegment = getDownstream();
        if(nextSegment == null) {
            logger.error("Incomplete path detected");
            return null;
        }

        if(! parentObj.has(getKey()) ||  parentObj.isNull(getKey())) {
            return null;
        }

        Object obj = parentObj.get(getKey());
        if(! getTClass().isInstance(obj)) {
            //Sometimes 'empty' values come across as empty strings
            boolean isEmpty = obj instanceof String && ((String)obj).isEmpty();
            if(!isEmpty) {
                logger.error("Expected " + getTClass().getName() + " but found " + (obj == null ? "null" : obj.getClass()));
            }
            return null;
        }

        return nextSegment.get(getTClass().cast(obj));
    }
}
