package org.drools.drl.parser;

import org.drools.drl10.parser.Drl10ExprParser;
import org.kie.internal.builder.conf.LanguageLevelOption;

public class DrlExprParserFactory {

    public static DrlExprParser getDrlExrParser(LanguageLevelOption languageLevel) {
        switch (languageLevel) {
            case DRL5:
            case DRL6:
            case DRL6_STRICT:
                return new Drl6ExprParser(languageLevel);
            case DRL10:
                return new Drl10ExprParser(languageLevel);
            default:
                throw new RuntimeException("Unsupported language level: " + languageLevel);
        }
    }
}
