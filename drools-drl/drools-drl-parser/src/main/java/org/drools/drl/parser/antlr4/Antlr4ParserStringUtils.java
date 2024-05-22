/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

/**
 * Collection of String utilities used by antlr4 DRLParser.
 */
public class Antlr4ParserStringUtils {

    private Antlr4ParserStringUtils() {
        // Private constructor to prevent instantiation.
    }

    /**
     * Get text from ParserRuleContext's CharStream without trimming whitespace
     */
    public static String getTextPreservingWhitespace(ParserRuleContext ctx) {
        if (ctx == null) {
            return "";
        }
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
     * Get text from List of ParserRuleContext's CharStream without trimming whitespace
     */
    public static String getTextPreservingWhitespace(List<? extends ParserRuleContext> ctx) {
        if (ctx == null) {
            return "";
        }
        return ctx.stream().map(Antlr4ParserStringUtils::getTextPreservingWhitespace).collect(Collectors.joining());
    }

    /**
     * Get text from ParserRuleContext's CharStream without trimming whitespace
     * tokenStream is required to get hidden channel token (e.g. whitespace).
     * Unlike getTextPreservingWhitespace, this method reflects Lexer normalizeString
     */
    public static String getTokenTextPreservingWhitespace(ParserRuleContext ctx, TokenStream tokenStream) {
        if (ctx == null) {
            return "";
        }
        return tokenStream.getText(ctx.start, ctx.stop);
    }

    /**
     * Extract name from "then[name]" of RHS_NAMED_CONSEQUENCE_THEN
     */
    public static String extractNamedConsequenceName(String namedConsequenceThen) {
        if (namedConsequenceThen.toLowerCase().startsWith("then[") && namedConsequenceThen.endsWith("]")) {
            return namedConsequenceThen.substring("then[".length(), namedConsequenceThen.length() - 1);
        } else {
            throw new DRLParserException("namedConsequenceThen has to be surrounded by 'then[', ']' : " + namedConsequenceThen);
        }
    }

}
