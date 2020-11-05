package org.openmbee.syncservice.core.translation.elements;

import org.openmbee.syncservice.core.syntax.fields.FieldId;
import org.openmbee.syncservice.core.translation.Translator;

public class SymmetricDetail<F extends FieldId, S,T> extends AsymmetricDetail<F, F, S, T> {

    public SymmetricDetail(Translator translator, F field, DetailTranslation<S,T> translation,
                           Class<S> sClass, Class<T> tClass) {
        super(translator, field, field, translation, sClass, tClass);
    }

}
