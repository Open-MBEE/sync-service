package org.openmbee.syncservice.core.syntax.path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PathSegment<S,T,V> {

    private static final Logger logger = LoggerFactory.getLogger(PathSegment.class);

    private Class<T> tClass;
    private PathSegment<T,?,V> downstream;

    public PathSegment(Class<T> tClass) {
        this.tClass = tClass;
    }

    public Class<T> getTClass() {
        return tClass;
    }

    public void setDownstream(PathSegment<T, ?, V> downstream) {
        this.downstream = downstream;
    }

    public PathSegment<T, ?, V> getDownstream() {
        return downstream;
    }

    public LeafSegment getLeaf() {
        if(downstream == null) {
            logger.error("Incomplete path detected");
            return null;
        }
        return downstream.getLeaf();
    }

    protected T newT() {
        try {
            return getTClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("Could not construct a " + " with the default constructor");
            return null;
        }
    }

    public abstract void put(V value, S parentObj);
    public abstract V get(S parentObj);
}
