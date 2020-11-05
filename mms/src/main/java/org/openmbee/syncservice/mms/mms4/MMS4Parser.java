package org.openmbee.syncservice.mms.mms4;

import org.openmbee.syncservice.core.syntax.Fields;
import org.openmbee.syncservice.core.syntax.Parser;
public class MMS4Parser implements Parser {
    private Fields fields;

    public MMS4Parser(Fields fields) {
        this.fields = fields;
    }

    @Override
    public Fields getFields() {
        return fields;
    }


}
