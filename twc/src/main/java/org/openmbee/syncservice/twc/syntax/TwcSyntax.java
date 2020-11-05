package org.openmbee.syncservice.twc.syntax;

import org.openmbee.syncservice.core.syntax.Parser;
import org.openmbee.syncservice.core.syntax.Syntax;
import org.openmbee.syncservice.twc.syntax.fields.TwcFields19_0_3;

public enum TwcSyntax implements Syntax {
    TWC_19_0_3(new TwcParser19_0_3(new TwcFields19_0_3()));

    private Parser parser;

    TwcSyntax(Parser parser) {
        this.parser = parser;
    }

    @Override
    public Parser getParser() {
        return parser;
    }
}
