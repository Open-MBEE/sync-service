package org.openmbee.syncservice.translation.twc19_3tomms4.valuebuilders;

import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.openmbee.syncservice.translation.twc19_3tomms4.TranslationContext;
import org.openmbee.syncservice.translation.twc19_3tomms4.Twc19_3ToMms4Translator;

public class LiteralUnlimitedNaturalValueBuilder extends ValueBuilder {
    public LiteralUnlimitedNaturalValueBuilder(Twc19_3ToMms4Translator translator, TranslationContext context) {
        super(translator, context);

        addDetail(SysMLv1X.VALUE, SysMLv1X.VALUE, this::parseLong, String.class, Long.class);
    }

    protected Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch(NumberFormatException ex) {
            return null;
        }
    }
}
