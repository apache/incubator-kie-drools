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
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.PatternDescr;

/**
 * Helper class for Descr manipulation.
 */
public class DescrHelper {

    private DescrHelper() {
        // Private constructor to prevent instantiation.
    }

    public static <T extends BaseDescr> T populateCommonProperties(T descr, ParserRuleContext ctx) {
        descr.setStartCharacter(ctx.getStart().getStartIndex());
        // TODO: Current DRL6Parser adds +1 for EndCharacter but it doesn't look reasonable. At the moment, I don't add. Instead, I fix unit tests.
        //       I will revisit if this is the right approach.
        descr.setEndCharacter(ctx.getStop().getStopIndex());
        descr.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        descr.setEndLocation(ctx.getStop().getLine(), ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() - 1); // last column of the end token
        return descr;
    }

    /**
     * LHS rootDescr requires special handling for properties, because it rearranges its children.
     */
    public static AndDescr refreshRootProperties(AndDescr descr) {
        List<BaseDescr> childDescrs = descr.getDescrs();
        if (childDescrs.isEmpty()) {
            return descr;
        }
        BaseDescr firstChild = childDescrs.get(0);
        BaseDescr lastChild = childDescrs.get(childDescrs.size() - 1);
        descr.setStartCharacter(firstChild.getStartCharacter());
        descr.setEndCharacter(lastChild.getEndCharacter());
        descr.setLocation(firstChild.getLine(), firstChild.getColumn());
        descr.setEndLocation(lastChild.getEndLine(), lastChild.getEndColumn());
        return descr;
    }

    /**
     * PatternDescr requires special handling for properties, because it should be updated with PatternBindContext. e.g. label
     */
    public static PatternDescr refreshPatternDescrProperties(PatternDescr descr, DRLParser.LhsPatternBindContext ctx) {
        return populateCommonProperties(descr, ctx);
    }
}
