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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.PatternDescr;

/**
 * Helper class for Descr manipulation.
 */
public class DescrHelper {

    private DescrHelper() {
        // Private constructor to prevent instantiation.
    }

    public static <T extends BaseDescr> T populateCommonProperties(T descr, ParserRuleContext ctx) {
        Token startToken = ctx.getStart(); // Start token is never null.
        // If the stop token is null, use the start token as both the start end the end.
        Token stopToken = ctx.getStop() != null ? ctx.getStop() : startToken;

        if (descr instanceof ExprConstraintDescr) {
            // Backward Compatibility Notes:
            //   Old DRL6Parser.constraint() has slightly different behavior for ExprConstraintDescr. Keep it for backward compatibility
            //   When we will update LanguageLevel, we can align this with other Descr.
            descr.setStartCharacter(startToken.getStartIndex());
            descr.setEndCharacter(stopToken.getStopIndex());
            descr.setLocation(startToken.getLine(), startToken.getCharPositionInLine());
            descr.setEndLocation(stopToken.getLine(), stopToken.getCharPositionInLine());
        } else {
            descr.setStartCharacter(startToken.getStartIndex());
            // Backward Compatibility Notes:
            //   Old DRL6Parser adds +1 for EndCharacter (except ExprConstraintDescr). This new parser follows the same to keep the backward compatibility.
            //   However, it doesn't look reasonable. When we will update LanguageLevel, we can remove this +1.
            descr.setEndCharacter(stopToken.getStopIndex() + 1);
            descr.setLocation(startToken.getLine(), startToken.getCharPositionInLine());
            descr.setEndLocation(stopToken.getLine(), stopToken.getCharPositionInLine() + stopTokenLength(stopToken) - 1); // last column of the end token
        }
        return descr;
    }

    private static int stopTokenLength(Token token) {
        return token.getType() == Token.EOF ? 0 : token.getText().length();
    }

    public static <T extends BaseDescr> T populateCommonProperties(T descr, List<? extends ParserRuleContext> ctxList) {
        if (ctxList.isEmpty()) {
            return descr;
        }
        ParserRuleContext firstCtx = ctxList.get(0);
        ParserRuleContext lastCtx = ctxList.get(ctxList.size() - 1);

        descr.setStartCharacter(firstCtx.getStart().getStartIndex());
        descr.setEndCharacter(lastCtx.getStop().getStopIndex() + 1);
        descr.setLocation(firstCtx.getStart().getLine(), firstCtx.getStart().getCharPositionInLine());
        descr.setEndLocation(lastCtx.getStop().getLine(), lastCtx.getStop().getCharPositionInLine() + lastCtx.getStop().getText().length() - 1); // last column of the end token
        return descr;
    }

    /**
     * PatternDescr requires special handling for properties, because it should be updated with PatternBindContext. e.g. label
     */
    public static PatternDescr refreshPatternDescrProperties(PatternDescr descr, DRLParser.LhsPatternBindContext ctx) {
        return populateCommonProperties(descr, ctx);
    }
}
