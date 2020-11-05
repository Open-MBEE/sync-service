package org.openmbee.syncservice.core.syntax.fields;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openmbee.syncservice.core.syntax.path.*;


public class FieldBuilder<F,S,T,V> {

    protected F field;
    protected Class<V> valueClass;
    protected PathSegment<S,T,V> path;
    protected Class<S> pathClass;

    public static <F,V> FieldBuilder<F,JSONObject,V,V> get(F field, String key, Class<V> valueClass) {
        FieldBuilder<F,JSONObject,V,V> fb = new FieldBuilder<>();
        fb.field = field;
        fb.valueClass = valueClass;
        fb.path = new KeyedLeafSegment<>(key, valueClass);
        fb.pathClass = JSONObject.class;
        return fb;
    }

    public static <F,V> FieldBuilder<F,JSONArray,V, V> get(F field, int index, Class<V> valueClass) {
        FieldBuilder<F,JSONArray,V,V> fb = new FieldBuilder<>();
        fb.field = field;
        fb.valueClass = valueClass;
        fb.path = new IndexedLeafSegment<>(index, valueClass);
        fb.pathClass = JSONArray.class;
        return fb;
    }

    public FieldBuilder<F,JSONObject,S,V> atPath(String key) {
        FieldBuilder<F,JSONObject,S,V> fb = new FieldBuilder<>();
        fb.field = this.field;
        fb.valueClass = this.valueClass;
        fb.path = new KeyedSegment<S,V>(key, pathClass, path);
        fb.pathClass = JSONObject.class;
        return fb;
    }

    public FieldBuilder<F,JSONArray,S,V> atIndex(int index) {
        FieldBuilder<F,JSONArray,S,V> fb = new FieldBuilder<>();
        fb.field = this.field;
        fb.valueClass = this.valueClass;
        fb.path = new IndexedSegment<>(index, pathClass, path);
        fb.pathClass = JSONArray.class;
        return fb;
    }

    public Field<F,V> build() {
        if(pathClass != JSONObject.class || (! (path instanceof KeyedSegment))) {
            throw new RuntimeException("Fields only allow beginning of path to expect objects, not arrays");
        }
        return new Field<F,V>(field, (KeyedSegment<T,V>)path);
    }


}
