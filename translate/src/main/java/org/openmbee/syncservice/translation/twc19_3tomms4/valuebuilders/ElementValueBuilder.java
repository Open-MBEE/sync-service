package org.openmbee.syncservice.translation.twc19_3tomms4.valuebuilders;

import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.openmbee.syncservice.translation.twc19_3tomms4.TranslationContext;
import org.openmbee.syncservice.translation.twc19_3tomms4.Twc19_3ToMms4Translator;

public class ElementValueBuilder extends ValueBuilder {
    public ElementValueBuilder(Twc19_3ToMms4Translator translator, TranslationContext context) {
        super(translator, context);

        addDetail(SysMLv1X.ELEMENT_VALUE, this::translateId, String.class);
    }
}
