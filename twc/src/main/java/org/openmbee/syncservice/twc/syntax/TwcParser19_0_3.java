package org.openmbee.syncservice.twc.syntax;

import org.openmbee.syncservice.core.syntax.Fields;
import org.openmbee.syncservice.core.syntax.Parser;
import org.openmbee.syncservice.core.utils.JSONUtils;

public class TwcParser19_0_3 extends JSONUtils implements Parser {

    private Fields fields;

    public TwcParser19_0_3(Fields fields) {
        super();
        this.fields = fields;
    }

    @Override
    public Fields getFields() {
        return fields;
    }

}
