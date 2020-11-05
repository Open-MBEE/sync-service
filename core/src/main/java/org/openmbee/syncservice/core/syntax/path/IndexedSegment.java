package org.openmbee.syncservice.core.syntax.path;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IndexedSegment<T,V> extends PathSegment<JSONArray,T,V> {
    private static final Logger logger = LoggerFactory.getLogger(IndexedSegment.class);

    private Integer index;

    public IndexedSegment(Integer index, Class<T> tClass, PathSegment<T,?,V> downstream) {
        super(tClass);
        this.index = index;
        this.setDownstream(downstream);
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public void put(V value, JSONArray parentObj) {
        if(parentObj == null) {
            return;
        }

        PathSegment<T,?,V> nextSegment = getDownstream();
        if(nextSegment == null) {
            logger.error("Incomplete path detected");
            return;
        }

        T obj =  null;
        if(parentObj.isNull(index)) {
            obj = newT();
            parentObj.put(getIndex(), obj);
        } else {
            Object rawObj = parentObj.get(index);
            if (!getTClass().isInstance(rawObj)) {
                logger.error("Expected " + getTClass().getName() + " but found " + rawObj.getClass());
                return;
            }
            obj = getTClass().cast(rawObj);
        }

        nextSegment.put(value, obj);
    }

    @Override
    public V get(JSONArray parentObj) {
        if(parentObj == null || parentObj.length() <= getIndex()) {
            return null;
        }

        PathSegment<T,?,V> nextSegment = getDownstream();
        if(nextSegment == null) {
            logger.error("Incomplete path detected");
            return null;
        }

        Object obj = parentObj.get(index);
        if(! getTClass().isInstance(obj)) {
            logger.error("Expected " + getTClass().getName() + " but found " + (obj == null ? "null" : obj.getClass()));
            return null;
        }

        return nextSegment.get(getTClass().cast(obj));
    }
}
