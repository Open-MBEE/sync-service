package org.openmbee.syncservice.core.syntax;

import org.json.JSONObject;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.syntax.fields.FieldId;

public interface Parser {

    Fields getFields();

    default <F extends FieldId,V> V getFieldFromElement(F field, JSONObject jsonObject, Class<V> valueClass) {
        Field<F,V> f = getFields().getField(field, valueClass);
        if(f != null) {
            return getFieldFromElement(f, jsonObject);
        }
        return null;
    }

    default <V> V getFieldFromElement(Field<?,V> field, JSONObject jsonObject) {
        return field.get(jsonObject);
    }

}
