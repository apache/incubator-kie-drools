package org.drools.drl.parser;

import org.drools.drl.parser.antlr4.Drl6ExprParserAntlr4;
import org.kie.internal.builder.conf.LanguageLevelOption;

public class DrlExprParserFactory {

    public static DrlExprParser getDrlExprParser(LanguageLevelOption languageLevel) {
        switch (languageLevel) {
            case DRL5:
            case DRL6:
            case DRL6_STRICT:
                return DrlParser.ANTLR4_PARSER_ENABLED ? new Drl6ExprParserAntlr4(languageLevel) : new Drl6ExprParser(languageLevel);
            default:
                throw new RuntimeException("Unsupported language level: " + languageLevel);
        }
    }
}
