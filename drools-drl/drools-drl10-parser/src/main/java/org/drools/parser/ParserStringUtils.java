package org.drools.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

/**
 * will be merged in drools-util
 */
public class ParserStringUtils {

    private ParserStringUtils() {
    }

    public static String safeStripStringDelimiters(String value) {
        if (value != null) {
            value = value.trim();
            if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

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

    public static String getTokenTextPreservingWhitespace(ParserRuleContext ctx, TokenStream tokenStream) {
        // tokenStream is required to get hidden channel token (e.g. whitespace). Unlike getTextPreservingWhitespace, this method reflects Lexer normalizeString
        return tokenStream.getText(ctx.start, ctx.stop);
    }
}
