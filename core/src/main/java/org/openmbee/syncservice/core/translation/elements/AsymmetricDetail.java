package org.openmbee.syncservice.core.translation.elements;

import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.syntax.fields.FieldId;
import org.openmbee.syncservice.core.translation.Translator;
import org.openmbee.syncservice.core.utils.JSONUtils;
import org.json.JSONObject;


public class AsymmetricDetail<FS extends FieldId, FT extends FieldId, S,T> extends JSONUtils implements Detail {
    private Translator<?,?> translator;
    private FS sourceFieldId;
    private FT targetFieldId;
    private DetailTranslation<S,T> translation;
    private Class<S> sClass;
    private Class<T> tClass;

    public AsymmetricDetail(Translator translator, FS sourceFieldId, FT targetFieldId, DetailTranslation<S,T> translation,
                            Class<S> sClass, Class<T> tClass) {
        this.translator = translator;
        this.sourceFieldId = sourceFieldId;
        this.targetFieldId = targetFieldId;
        this.translation = translation;
        this.sClass = sClass;
        this.tClass = tClass;
    }

    public FS getSourceFieldId() {
        return sourceFieldId;
    }

    public FT getTargetFieldId() {
        return targetFieldId;
    }

    public Class<S> getSourceClass() {
        return sClass;
    }

    public Class<T> getTargetClass() {
        return tClass;
    }

    @Override
    public void transfer(JSONObject original, JSONObject out) {
        Field<FS, S> sourceField = translator.getSourceSyntax().getParser().getFields().getField(sourceFieldId, sClass);
        S sourceValue = translator.getSourceSyntax().getParser().getFieldFromElement(sourceField, original);
        Field<FT, T> targetField = translator.getSinkSyntax().getParser().getFields().getField(targetFieldId, tClass);
        T targetValue = translation.translate(sourceValue);
        targetField.put(targetValue, out);
    }

    @Override
    public String toString() {
        return sourceFieldId.toString() + " -> " + targetFieldId.toString();
    }
}
