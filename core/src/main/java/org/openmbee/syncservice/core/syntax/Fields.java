package org.openmbee.syncservice.core.syntax;

import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.syntax.fields.FieldId;

public interface Fields {

    <F extends FieldId, T> Field<F, T> getField(F field, Class<T> valueClass);
}
