package org.openmbee.syncservice.mms.mms4;

import org.openmbee.syncservice.core.syntax.Parser;
import org.openmbee.syncservice.core.syntax.Syntax;

public enum MmsSyntax implements Syntax {
    MMS4 (new MMS4Parser(new Mms4Fields()));

    private Parser parser;

    MmsSyntax(Parser parser) {
        this.parser = parser;
    }

    @Override
    public Parser getParser() {
        return parser;
    }
}
