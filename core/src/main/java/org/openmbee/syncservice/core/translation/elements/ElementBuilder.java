package org.openmbee.syncservice.core.translation.elements;

import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.syntax.fields.FieldId;
import org.openmbee.syncservice.core.translation.Translator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ElementBuilder<T extends Translator<?,?>> {
    private static final Logger logger = LoggerFactory.getLogger(ElementBuilder.class);

    protected final T translator;
    protected final List<Detail> details = new ArrayList<>();

    public ElementBuilder(T translator) {
        this.translator = translator;
    }

    public T getTranslator() {
        return translator;
    }

    protected <F extends FieldId,V> void addDetail(F field, Class<V> valueClass) {
        addDetail(field, v -> v, valueClass);
    }

    protected <F extends FieldId,V> void addDetail(F field, DetailTranslation<V,V> translation,
                                 Class<V> valueClass) {
        details.add(new SymmetricDetail<F,V,V>(translator, field, translation, valueClass, valueClass));
    }

    protected <F extends FieldId,S,T> void addDetail(F field, DetailTranslation<S,T> translation,
                                   Class<S> sourceClass, Class<T> targetClass) {
        details.add(new SymmetricDetail<F,S,T>(translator, field, translation, sourceClass, targetClass));
    }

    protected <FS extends FieldId, FT extends FieldId,S,T> void addDetail(FS sourceFieldId, FT targetFieldId, DetailTranslation<S,T> translation,
                                                     Class<S> sourceClass, Class<T> targetClass) {
        details.add(new AsymmetricDetail<FS,FT,S,T>(translator, sourceFieldId, targetFieldId, translation, sourceClass, targetClass));
    }

    protected void addDetail(Detail detail) {
        details.add(detail);
    }

    public JSONObject buildElementFrom(JSONObject original) {
        JSONObject out = new JSONObject();
        transferDetails(original, out);
        return out;
    }

    protected <T> void writeField(Field<?,T> field, T value, JSONObject out) {
        field.put(value, out);
    }

    protected void transferDetails(JSONObject original, JSONObject out) {
        details.stream().forEach(v -> {
            try {
                v.transfer(original, out);
            } catch(Exception ex) {
                logger.error("Failed to transfer detail: " + v.toString());
            }
        });
    }
}
