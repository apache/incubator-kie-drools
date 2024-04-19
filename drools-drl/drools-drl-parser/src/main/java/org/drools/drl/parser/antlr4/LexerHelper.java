/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.IntStream;
import org.drools.drl.parser.lang.DroolsSoftKeywords;

/**
 * Helper class for lexer. It requires instance creation to keep track of the lookahead counter.
 */
public class LexerHelper {

    private static final List<Character> semiAndWS = List.of(';', ' ', '\t', '\n', '\r');
    private static final List<String> statementKeywordsList = List.of(ParserHelper.statementKeywords);
    private static final List<String> attributeKeywordsList = List.of(DroolsSoftKeywords.SALIENCE,
                                                                      DroolsSoftKeywords.ENABLED,
                                                                      DroolsSoftKeywords.NO + "-" + DroolsSoftKeywords.LOOP,
                                                                      DroolsSoftKeywords.AUTO + "-" + DroolsSoftKeywords.FOCUS,
                                                                      DroolsSoftKeywords.LOCK + "-" + DroolsSoftKeywords.ON + "-" + DroolsSoftKeywords.ACTIVE,
                                                                      DroolsSoftKeywords.AGENDA + "-" + DroolsSoftKeywords.GROUP,
                                                                      DroolsSoftKeywords.ACTIVATION + "-" + DroolsSoftKeywords.GROUP,
                                                                      DroolsSoftKeywords.RULEFLOW + "-" + DroolsSoftKeywords.GROUP,
                                                                      DroolsSoftKeywords.DATE + "-" + DroolsSoftKeywords.EFFECTIVE,
                                                                      DroolsSoftKeywords.DATE + "-" + DroolsSoftKeywords.EXPIRES,
                                                                      DroolsSoftKeywords.DIALECT,
                                                                      DroolsSoftKeywords.CALENDARS,
                                                                      DroolsSoftKeywords.TIMER,
                                                                      DroolsSoftKeywords.DURATION,
                                                                      DroolsSoftKeywords.REFRACT,
                                                                      DroolsSoftKeywords.DIRECT);

    private final CharStream input;
    private int lookAheadCounter;

    public LexerHelper(CharStream input) {
        this.input = input;
        this.lookAheadCounter = 1;
    }

    /**
     * Determine if the current token is the end of a RHS DRL by lookahead.
     * 1. 'end'
     * 2. skip semi, WS, and comment
     * 3. next token should be EOF or statement or attribute keyword
     *
     * TODO: This method is low-level and may be too complex in order to keep backward compatibility.
     *       This could be refactored by going back to a parser rather than the lexer island mode.
     */
    boolean isRhsDrlEnd() {
        if (!validateDrlEnd()) {
            return false;
        }
        skipSemiAndWSAndComment();

        return validateEOForNextStatement();
    }

    private boolean validateDrlEnd() {
        return captureNextToken().equals(DroolsSoftKeywords.END);
    }

    private String captureNextToken() {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int la = input.LA(lookAheadCounter);
            if (semiAndWS.contains((char) la) || la == IntStream.EOF) {
                break;
            }
            sb.append((char) la);
            lookAheadCounter++;
        }
        return sb.toString(); // never null
    }

    private void skipSemiAndWSAndComment() {
        while (true) {
            skipSemiAndWS();
            if (input.LA(lookAheadCounter) == '/') {
                boolean skipped = skipComment();
                if (!skipped) {
                    // found non-comment token
                    break;
                }
                // if comment is found and skipped, continue to skip semi and WS
            } else {
                // found non-comment token
                break;
            }
        }
    }

    private void skipSemiAndWS() {
        while (true) {
            int la = input.LA(lookAheadCounter);
            if (!semiAndWS.contains((char) la)) {
                break;
            }
            lookAheadCounter++;
        }
    }

    // if comment is found, skip it and return true
    private boolean skipComment() {
        boolean skipped = false;
        // skip single line comment
        int la1 = input.LA(lookAheadCounter);
        int la2 = input.LA(lookAheadCounter + 1);
        if (la1 == '/' && la2 == '/') {
            // skip single line comment
            skipSingleLineComment();
            skipped = true;
        } else if (la1 == '/' && la2 == '*') {
            // skip multi line comment
            skipMultiLineComment();
            skipped = true;
        }

        return skipped;
    }

    private void skipSingleLineComment() {
        while (true) {
            int la = input.LA(lookAheadCounter);
            if (la == '\n' || la == IntStream.EOF) { // this can handle `\r\n` as well
                break;
            }
            lookAheadCounter++;
        }
    }

    private void skipMultiLineComment() {
        while (true) {
            int la = input.LA(lookAheadCounter);
            if (la == IntStream.EOF) {
                break;
            }
            if (la == '*' && input.LA(lookAheadCounter + 1) == '/') {
                lookAheadCounter += 2;
                break;
            }
            lookAheadCounter++;
        }
    }

    private boolean validateEOForNextStatement() {
        if (input.LA(lookAheadCounter) == IntStream.EOF) {
            return true;
        }
        String nextToken = captureNextToken();
        return statementKeywordsList.contains(nextToken) || attributeKeywordsList.contains(nextToken);
    }
}
