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

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
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

    public static RangeNode newIntervalNode(ParserRuleContext ctx, RangeNode.IntervalBoundary low, BaseNode start, BaseNode end, RangeNode.IntervalBoundary up) {
        return new RangeNode( ctx, low, start, end, up );
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

    public static FunctionDefNode newFunctionDefinition(ParserRuleContext ctx, ListNode parameters, boolean external, BaseNode body) {
        return new FunctionDefNode( ctx, parameters, external, body );
    }

    public static IterationContextNode newIterationContextNode(ParserRuleContext ctx, NameDefNode name, BaseNode expr) {
        return new IterationContextNode( ctx, name, expr );
    }

    public static ForExpressionNode newForExpression(ParserRuleContext ctx, ListNode list, BaseNode expr) {
        return new ForExpressionNode( ctx, list, expr );
    }

    public static NameRefNode newNameRefNode( ParserRuleContext ctx ) {
        return new NameRefNode( ctx );
    }

    public static QualifiedNameNode newQualifiedNameNode(ParserRuleContext ctx, ArrayList<NameRefNode> parts) {
        return new QualifiedNameNode( ctx, parts );
    }

    public static IfExpressionNode newIfExpression(ParserRuleContext ctx, BaseNode c, BaseNode t, BaseNode e) {
        return new IfExpressionNode( ctx, c, t, e );
    }

    public static QuantifiedExpressionNode newQuantifiedExpression(ParserRuleContext ctx, QuantifiedExpressionNode.Quantifier quant, ListNode list, BaseNode expr) {
        return new QuantifiedExpressionNode( ctx, quant, list, expr );
    }

    public static InstanceOfNode newInstanceOfNode(ParserRuleContext ctx, BaseNode expr, TypeNode type) {
        return new InstanceOfNode( ctx, expr, type );
    }

    public static PathExpressionNode newPathExpressionNode(ParserRuleContext ctx, BaseNode expr, BaseNode name) {
        return new PathExpressionNode( ctx, expr, name );
    }

    public static FilterExpressionNode newFilterExpressionNode(ParserRuleContext ctx, BaseNode expr, BaseNode filter) {
        return new FilterExpressionNode( ctx, expr, filter );
    }

    public static NamedParameterNode newNamedParameterNode(ParserRuleContext ctx, NameDefNode name, BaseNode value) {
        return new NamedParameterNode( ctx, name, value );
    }

    public static FunctionInvocationNode newFunctionInvocationNode(ParserRuleContext ctx, BaseNode name, ListNode params) {
        return new FunctionInvocationNode( ctx, name, params );
    }

    public static DashNode newDashNode(ParserRuleContext ctx) {
        return new DashNode( ctx );
    }

    public static TypeNode newTypeNode(ParserRuleContext ctx) {
        return new TypeNode( ctx );
    }
}
