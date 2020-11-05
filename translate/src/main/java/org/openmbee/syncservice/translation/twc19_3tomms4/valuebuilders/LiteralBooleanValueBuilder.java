package org.openmbee.syncservice.translation.twc19_3tomms4.valuebuilders;

import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.openmbee.syncservice.translation.twc19_3tomms4.TranslationContext;
import org.openmbee.syncservice.translation.twc19_3tomms4.Twc19_3ToMms4Translator;

public class LiteralBooleanValueBuilder extends ValueBuilder {
    public LiteralBooleanValueBuilder(Twc19_3ToMms4Translator translator, TranslationContext context) {
        super(translator, context);

        addDetail(SysMLv1X.VALUE, SysMLv1X.VALUE, this::parseBoolean, String.class, Boolean.class);
    }

    protected Boolean parseBoolean(String value) {
        try {
            return Boolean.parseBoolean(value);
        } catch(NumberFormatException ex) {
            return null;
        }
    }
}
