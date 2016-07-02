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

    public static NumberNode newNumberNode(ParserRuleContext ctx) {
        return new NumberNode( ctx );
    }

    public static BooleanNode newBooleanNode(ParserRuleContext ctx) {
        return new BooleanNode( ctx );
    }

    public static SignedUnaryNode newSignedUnaryNode(ParserRuleContext ctx, BaseNode expr) {
        return new SignedUnaryNode( ctx, expr );
    }

    public static NullNode newNullNode(ParserRuleContext ctx) {
        return new NullNode( ctx );
    }

    public static StringNode newStringNode(ParserRuleContext ctx) {
        return new StringNode( ctx );
    }

    public static VariableNode newNameNode(ParserRuleContext ctx) {
        return new VariableNode( ctx );
    }

    public static NotNode newNotNode(ParserRuleContext ctx, BaseNode expr) {
        return new NotNode( ctx, expr );
    }

    public static InfixOpNode newInfixOpNode(ParserRuleContext ctx, BaseNode left, String op, BaseNode right) {
        return new InfixOpNode( ctx, left, op, right );
    }

    public static BetweenNode newBetweenNode(ParserRuleContext ctx, BaseNode value, BaseNode start, BaseNode end) {
        return new BetweenNode( ctx, value, start, end );
    }

    public static ListNode newListNode(ParserRuleContext ctx, List<BaseNode> exprs) {
        return new ListNode( ctx, exprs );
    }

    public static InNode newInNode(ParserRuleContext ctx, BaseNode value, BaseNode list) {
        return new InNode( ctx, value, list );
    }

    public static IntervalNode newIntervalNode(ParserRuleContext ctx, IntervalNode.IntervalBoundary low, BaseNode start, BaseNode end, IntervalNode.IntervalBoundary up) {
        return new IntervalNode( ctx, low, start, end, up );
    }

    public static UnaryTestNode newUnaryTestNode(ParserRuleContext ctx, String op, BaseNode value) {
        return new UnaryTestNode( ctx, op, value );
    }

    public static NameDefNode newNameDefNode(ParserRuleContext ctx, List<String> tokens) {
        return new NameDefNode( ctx, tokens );
    }

    public static NameDefNode newNameDefNode(ParserRuleContext ctx, String name) {
        return new NameDefNode( ctx, name );
    }

    public static ContextEntryNode newContextEntry(ParserRuleContext ctx, BaseNode name, BaseNode value) {
        return new ContextEntryNode( ctx, name, value );
    }

    public static ContextNode newContextNode(ParserRuleContext ctx, ListNode list) {
        return new ContextNode( ctx, list );
    }
}
