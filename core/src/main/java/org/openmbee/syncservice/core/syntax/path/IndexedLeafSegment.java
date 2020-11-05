package org.openmbee.syncservice.core.syntax.path;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IndexedLeafSegment<V> extends IndexedSegment<V,V> implements LeafSegment {
    private static final Logger logger = LoggerFactory.getLogger(IndexedLeafSegment.class);

    public IndexedLeafSegment(Integer index, Class<V> vClass) {
        super(index, vClass, null);
    }

    @Override
    public void put(V value, JSONArray parentObj) {
        if(parentObj == null) {
            return;
        }
        parentObj.put(getIndex(), value);
    }

    @Override
    public V get(JSONArray array) {
        if(array.length() > getIndex()) {
            Object obj = array.get(getIndex());
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
