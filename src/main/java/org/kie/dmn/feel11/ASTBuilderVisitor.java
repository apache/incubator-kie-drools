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

package org.kie.dmn.feel11;

import org.antlr.v4.runtime.Token;
import org.kie.dmn.lang.ast.*;
import org.kie.dmn.lang.builder.ASTBuilder;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilderVisitor
        extends FEEL_1_1BaseVisitor<BaseNode> {

    @Override
    public BaseNode visitNumberLiteral(FEEL_1_1Parser.NumberLiteralContext ctx) {
        return ASTBuilderFactory.newNumberNode( ctx );
    }

    @Override
    public BaseNode visitBooleanLiteral(FEEL_1_1Parser.BooleanLiteralContext ctx) {
        return ASTBuilderFactory.newBooleanNode( ctx );
    }

    @Override
    public BaseNode visitSignedUnaryExpression(FEEL_1_1Parser.SignedUnaryExpressionContext ctx) {
        BaseNode node = visit( ctx.unaryExpression() );
        return ASTBuilderFactory.newSignedUnaryNode( ctx, node );
    }

    @Override
    public BaseNode visitNullLiteral(FEEL_1_1Parser.NullLiteralContext ctx) {
        return ASTBuilderFactory.newNullNode( ctx );
    }

    @Override
    public BaseNode visitStringLiteral(FEEL_1_1Parser.StringLiteralContext ctx) {
        return ASTBuilderFactory.newStringNode( ctx );
    }

    @Override
    public BaseNode visitPrimaryName(FEEL_1_1Parser.PrimaryNameContext ctx) {
        return ASTBuilderFactory.newNameNode( ctx );
    }

    @Override
    public BaseNode visitPrimaryParens(FEEL_1_1Parser.PrimaryParensContext ctx) {
        return visit( ctx.expression() );
    }

    @Override
    public BaseNode visitLogicalNegation(FEEL_1_1Parser.LogicalNegationContext ctx) {
        BaseNode node = visit( ctx.unaryExpression() );
        return ASTBuilderFactory.newNotNode( ctx, node );
    }

    @Override
    public BaseNode visitPowExpression(FEEL_1_1Parser.PowExpressionContext ctx) {
        BaseNode left = visit( ctx.powerExpression() );
        BaseNode right = visit( ctx.unaryExpression() );
        String op = ctx.op.getText();
        return ASTBuilderFactory.newInfixOpNode( ctx, left, op, right );
    }

    @Override
    public BaseNode visitMultExpression(FEEL_1_1Parser.MultExpressionContext ctx) {
        BaseNode left = visit( ctx.multiplicativeExpression() );
        BaseNode right = visit( ctx.powerExpression() );
        String op = ctx.op.getText();
        return ASTBuilderFactory.newInfixOpNode( ctx, left, op, right );
    }

    @Override
    public BaseNode visitAddExpression(FEEL_1_1Parser.AddExpressionContext ctx) {
        BaseNode left = visit( ctx.additiveExpression() );
        BaseNode right = visit( ctx.multiplicativeExpression() );
        String op = ctx.op.getText();
        return ASTBuilderFactory.newInfixOpNode( ctx, left, op, right );
    }

    @Override
    public BaseNode visitRelExpressionBetween(FEEL_1_1Parser.RelExpressionBetweenContext ctx) {
        BaseNode value = visit( ctx.val );
        BaseNode start = visit( ctx.start );
        BaseNode end = visit( ctx.end );
        return ASTBuilderFactory.newBetweenNode( ctx, value, start, end );
    }

    @Override
    public BaseNode visitExpressionList(FEEL_1_1Parser.ExpressionListContext ctx) {
        List<BaseNode> exprs = new ArrayList<>();
        for ( int i = 0; i < ctx.getChildCount(); i++ ) {
            if ( ctx.getChild( i ) instanceof FEEL_1_1Parser.ExpressionContext ) {
                exprs.add( visit( ctx.getChild( i ) ) );
            }
        }
        return ASTBuilderFactory.newListNode( ctx, exprs );
    }

    @Override
    public BaseNode visitRelExpressionValueList(FEEL_1_1Parser.RelExpressionValueListContext ctx) {
        BaseNode value = visit( ctx.val );
        BaseNode list = visit( ctx.expressionList() );
        return ASTBuilderFactory.newInNode( ctx, value, list );
    }

    @Override
    public BaseNode visitInterval(FEEL_1_1Parser.IntervalContext ctx) {
        BaseNode start = visit( ctx.start );
        BaseNode end = visit( ctx.end );
        IntervalNode.IntervalBoundary low = ctx.low.getText().equals( "[" ) ? IntervalNode.IntervalBoundary.CLOSED : IntervalNode.IntervalBoundary.OPEN;
        IntervalNode.IntervalBoundary up = ctx.low.getText().equals( "]" ) ? IntervalNode.IntervalBoundary.CLOSED : IntervalNode.IntervalBoundary.OPEN;
        return ASTBuilderFactory.newIntervalNode( ctx, low, start, end, up );
    }

    @Override
    public BaseNode visitPositiveUnaryTestIneq(FEEL_1_1Parser.PositiveUnaryTestIneqContext ctx) {
        BaseNode value = visit( ctx.endpoint() );
        String op = ctx.op.getText();
        return ASTBuilderFactory.newUnaryTestNode( ctx, op, value );
    }

    @Override
    public BaseNode visitSimplePositiveUnaryTests(FEEL_1_1Parser.SimplePositiveUnaryTestsContext ctx) {
        List<BaseNode> tests = new ArrayList<>();
        for ( int i = 0; i < ctx.getChildCount(); i++ ) {
            if ( ctx.getChild( i ) instanceof FEEL_1_1Parser.SimplePositiveUnaryTestContext ) {
                tests.add( visit( ctx.getChild( i ) ) );
            }
        }
        return ASTBuilderFactory.newListNode( ctx, tests );
    }

    @Override
    public BaseNode visitRelExpressionTestList(FEEL_1_1Parser.RelExpressionTestListContext ctx) {
        BaseNode value = visit( ctx.val );
        BaseNode list = visit( ctx.simplePositiveUnaryTests() );
        return ASTBuilderFactory.newInNode( ctx, value, list );
    }

    @Override
    public BaseNode visitRelExpressionTest(FEEL_1_1Parser.RelExpressionTestContext ctx) {
        BaseNode value = visit( ctx.val );
        BaseNode test = visit( ctx.simplePositiveUnaryTest() );
        return ASTBuilderFactory.newInNode( ctx, value, test );
    }

    @Override
    public BaseNode visitPositiveUnaryTestNull(FEEL_1_1Parser.PositiveUnaryTestNullContext ctx) {
        return ASTBuilderFactory.newNullNode( ctx );
    }

    @Override
    public BaseNode visitCompExpression(FEEL_1_1Parser.CompExpressionContext ctx) {
        BaseNode left = visit( ctx.left );
        BaseNode right = visit( ctx.right );
        return ASTBuilderFactory.newInfixOpNode( ctx, left, ctx.op.getText(), right );
    }

    @Override
    public BaseNode visitCondOr(FEEL_1_1Parser.CondOrContext ctx) {
        BaseNode left = visit( ctx.left );
        BaseNode right = visit( ctx.right );
        return ASTBuilderFactory.newInfixOpNode( ctx, left, ctx.op.getText(), right );
    }

    @Override
    public BaseNode visitCondAnd(FEEL_1_1Parser.CondAndContext ctx) {
        BaseNode left = visit( ctx.left );
        BaseNode right = visit( ctx.right );
        return ASTBuilderFactory.newInfixOpNode( ctx, left, ctx.op.getText(), right );
    }

    @Override
    public BaseNode visitList(FEEL_1_1Parser.ListContext ctx) {
        if ( ctx.expressionList() == null ) {
            // empty list -> children are [ ]
            return ASTBuilderFactory.newListNode( ctx, new ArrayList<>() );
        } else {
            // returns actual list
            return visit( ctx.expressionList() );
        }
    }

    @Override
    public BaseNode visitNameDefinition(FEEL_1_1Parser.NameDefinitionContext ctx) {
        List<String> tokens = new ArrayList<>();
        for( int i = 0; i < ctx.getChildCount(); i++ ) {
            tokens.add( ctx.getChild( i ).getText() );
        }
        return ASTBuilderFactory.newNameDefNode( ctx, tokens );
    }

    @Override
    public BaseNode visitKeyString(FEEL_1_1Parser.KeyStringContext ctx) {
        return ASTBuilderFactory.newNameDefNode( ctx, ctx.getText() );
    }

    @Override
    public BaseNode visitContextEntry(FEEL_1_1Parser.ContextEntryContext ctx) {
        BaseNode name = visit( ctx.key() );
        BaseNode value = visit( ctx.expression() );
        return ASTBuilderFactory.newContextEntry( ctx, name, value );
    }

    @Override
    public BaseNode visitContextEntries(FEEL_1_1Parser.ContextEntriesContext ctx) {
        List<BaseNode> nodes = new ArrayList<>(  );
        for( FEEL_1_1Parser.ContextEntryContext c : ctx.contextEntry() ) {
            nodes.add( visit( c ) );
        }
        return ASTBuilderFactory.newListNode( ctx, nodes );
    }

    @Override
    public BaseNode visitContext(FEEL_1_1Parser.ContextContext ctx) {
        ListNode list = ctx.contextEntries() != null ? (ListNode) visit( ctx.contextEntries() ) : ASTBuilderFactory.newListNode( ctx, new ArrayList<>(  ) );
        return ASTBuilderFactory.newContextNode( ctx, list );
    }
}
