package org.drools.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

/**
 * Collection of String utilities used by DRLParser.
 * This may be merged in drools-util
 */
public class ParserStringUtils {

    private ParserStringUtils() {
    }

    /**
     * Strip string delimiters (e.g. "foo" -> foo)
     */
    public static String safeStripStringDelimiters(String value) {
        if (value != null) {
            value = value.trim();
            if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    /**
     * Get text from ParserRuleContext's CharStream without trimming whitespace
     */
    public static String getTextPreservingWhitespace(ParserRuleContext ctx) {
        // Using raw CharStream
        int startIndex = ctx.start.getStartIndex();
        int stopIndex = ctx.stop.getStopIndex();
        if (startIndex > stopIndex) {
            // no text
            return "";
        }
        Interval interval = new Interval(startIndex, stopIndex);
        return ctx.start.getTokenSource().getInputStream().getText(interval);
    }

    /**
     * Get text from ParserRuleContext's CharStream without trimming whitespace
     * tokenStream is required to get hidden channel token (e.g. whitespace).
     * Unlike getTextPreservingWhitespace, this method reflects Lexer normalizeString
     */
    public static String getTokenTextPreservingWhitespace(ParserRuleContext ctx, TokenStream tokenStream) {
        return tokenStream.getText(ctx.start, ctx.stop);
    }

    /**
     * Just remove leading "then"
     */
    public static String trimThen(String rhs) {
        if (rhs.startsWith("then")) {
            return rhs.substring("then".length());
        } else {
            throw new DRLParserException("rhs has to start with 'then' : rhs = " + rhs);
        }
    }
}
