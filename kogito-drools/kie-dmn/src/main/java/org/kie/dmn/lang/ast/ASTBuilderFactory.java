/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel11.FEEL_1_1Parser;

import java.util.List;

public class ASTBuilderFactory {

    public static BaseNode newNumberNode(ParserRuleContext ctx) {
        return new NumberNode( ctx );
    }

    public static BaseNode newBooleanNode(ParserRuleContext ctx) {
        return new BooleanNode( ctx );
    }

    public static BaseNode newSignedUnaryNode(ParserRuleContext ctx, BaseNode expr) {
        return new SignedUnaryNode( ctx, expr );
    }

    public static BaseNode newNullNode(ParserRuleContext ctx) {
        return new NullNode( ctx );
    }

    public static BaseNode newStringNode(ParserRuleContext ctx) {
        return new StringNode( ctx );
    }

    public static BaseNode newNameNode(ParserRuleContext ctx) {
        return new VariableNode( ctx );
    }

    public static BaseNode newNotNode(ParserRuleContext ctx, BaseNode expr) {
        return new NotNode( ctx, expr );
    }

    public static BaseNode newInfixOpNode(ParserRuleContext ctx, BaseNode left, String op, BaseNode right) {
        return new InfixOpNode( ctx, left, op, right );
    }

    public static BaseNode newBetweenNode(ParserRuleContext ctx, BaseNode value, BaseNode start, BaseNode end) {
        return new BetweenNode( ctx, value, start, end );
    }

    public static BaseNode newListNode(ParserRuleContext ctx, List<BaseNode> exprs) {
        return new ListNode( ctx, exprs );
    }

    public static BaseNode newInNode(ParserRuleContext ctx, BaseNode value, BaseNode list) {
        return new InNode( ctx, value, list );
    }

    public static BaseNode newIntervalNode(ParserRuleContext ctx, IntervalNode.IntervalBoundary low, BaseNode start, BaseNode end, IntervalNode.IntervalBoundary up) {
        return new IntervalNode( ctx, low, start, end, up );
    }

    public static BaseNode newUnaryTestNode(ParserRuleContext ctx, String op, BaseNode value) {
        return new UnaryTestNode( ctx, op, value );
    }
}
