package org.openmbee.syncservice.twc.syntax.fields;

import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.syntax.fields.FieldBuilder;
import org.openmbee.syncservice.core.syntax.path.IndexedLeafSegment;
import org.openmbee.syncservice.core.syntax.path.IndexedSegment;
import org.openmbee.syncservice.core.syntax.path.KeyedLeafSegment;
import org.openmbee.syncservice.core.syntax.path.KeyedSegment;
import org.json.JSONArray;
import org.json.JSONObject;

public class TwcFieldBuilder<F, S, T, V> extends FieldBuilder<F, S, T, V> {
    public static <F,V> TwcFieldBuilder<F, JSONObject,V,V> get(F field, String key, Class<V> valueClass) {
        TwcFieldBuilder<F,JSONObject,V,V> fb = new TwcFieldBuilder<>();
        fb.field = field;
        fb.valueClass = valueClass;
        fb.path = new KeyedLeafSegment<>(key, valueClass);
        fb.pathClass = JSONObject.class;
        return fb;
    }

    public static <F,V> TwcFieldBuilder<F, JSONArray,V, V> get(F field, int index, Class<V> valueClass) {
        TwcFieldBuilder<F,JSONArray,V,V> fb = new TwcFieldBuilder<>();
        fb.field = field;
        fb.valueClass = valueClass;
        fb.path = new IndexedLeafSegment<>(index, valueClass);
        fb.pathClass = JSONArray.class;
        return fb;
    }

    public TwcFieldBuilder<F,JSONObject,S,V> atPath(String key) {
        TwcFieldBuilder<F,JSONObject,S,V> fb = new TwcFieldBuilder<>();
        fb.field = this.field;
        fb.valueClass = this.valueClass;
        fb.path = new KeyedSegment<S,V>(key, pathClass, path);
        fb.pathClass = JSONObject.class;
        return fb;
    }

    public TwcFieldBuilder<F,JSONArray,S,V> atIndex(int index) {
        TwcFieldBuilder<F,JSONArray,S,V> fb = new TwcFieldBuilder<>();
        fb.field = this.field;
        fb.valueClass = this.valueClass;
        fb.path = new IndexedSegment<>(index, pathClass, path);
        fb.pathClass = JSONArray.class;
        return fb;
    }

    public Field<F, V> atData(int index) {
        return atIndex(index).atPath("data").build();
    }

    public Field<F,V> atEsiData() {
        return atPath("kerml:esiData")
                .atIndex(1)
                .atPath("data")
                .build();
    }
}
