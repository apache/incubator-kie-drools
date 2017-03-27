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

package org.kie.dmn.feel.parser.feel11;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.kie.dmn.feel.lang.ast.*;
import org.kie.dmn.feel.runtime.UnaryTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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
    public BaseNode visitPrimaryParens(FEEL_1_1Parser.PrimaryParensContext ctx) {
        return visit( ctx.expression() );
    }

    @Override
    public BaseNode visitLogicalNegation(FEEL_1_1Parser.LogicalNegationContext ctx) {
        BaseNode name = ASTBuilderFactory.newNameRefNode( ctx.not_key() );
        BaseNode node = visit( ctx.unaryExpression() );
        ListNode params = ASTBuilderFactory.newListNode( ctx.unaryExpression(), Arrays.asList( node ) );

        return buildFunctionCall( ctx, name, params );
    }

    @Override
    public BaseNode visitPowExpression(FEEL_1_1Parser.PowExpressionContext ctx) {
        BaseNode left = visit( ctx.powerExpression() );
        BaseNode right = visit( ctx.filterPathExpression() );
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
        RangeNode.IntervalBoundary low = ctx.low.getText().equals( "[" ) ? RangeNode.IntervalBoundary.CLOSED : RangeNode.IntervalBoundary.OPEN;
        RangeNode.IntervalBoundary up = ctx.up.getText().equals( "]" ) ? RangeNode.IntervalBoundary.CLOSED : RangeNode.IntervalBoundary.OPEN;
        return ASTBuilderFactory.newIntervalNode( ctx, low, start, end, up );
    }

    @Override
    public BaseNode visitPositiveUnaryTestIneq(FEEL_1_1Parser.PositiveUnaryTestIneqContext ctx) {
        BaseNode value = visit( ctx.endpoint() );
        String op = ctx.op.getText();
        return ASTBuilderFactory.newUnaryTestNode( ctx, op, value );
    }

    @Override
    public BaseNode visitSimpleUnaryTests(FEEL_1_1Parser.SimpleUnaryTestsContext ctx) {
        List<BaseNode> tests = new ArrayList<>();
        for ( int i = 0; i < ctx.getChildCount(); i++ ) {
            if ( ctx.getChild( i ) instanceof FEEL_1_1Parser.SimpleUnaryTestContext ||
                    ctx.getChild( i ) instanceof FEEL_1_1Parser.PrimaryContext) {
                tests.add( visit( ctx.getChild( i ) ) );
            }
        }
        return ASTBuilderFactory.newListNode( ctx, tests );
    }

    @Override
    public BaseNode visitRelExpressionTestList(FEEL_1_1Parser.RelExpressionTestListContext ctx) {
        BaseNode value = visit( ctx.val );
        BaseNode list = visit( ctx.simpleUnaryTests() );
        return ASTBuilderFactory.newInNode( ctx, value, list );
    }

    @Override
    public BaseNode visitRelExpressionTest(FEEL_1_1Parser.RelExpressionTestContext ctx) {
        BaseNode value = visit( ctx.val );
        BaseNode test = visit( ctx.simpleUnaryTest() );
        return ASTBuilderFactory.newInNode( ctx, value, test );
    }

    @Override
    public BaseNode visitPositiveUnaryTestNull(FEEL_1_1Parser.PositiveUnaryTestNullContext ctx) {
        return ASTBuilderFactory.newNullNode( ctx );
    }

    @Override
    public BaseNode visitPositiveUnaryTestDash(FEEL_1_1Parser.PositiveUnaryTestDashContext ctx) {
        return ASTBuilderFactory.newDashNode( ctx );
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
        List<String> tokenStrs = new ArrayList<>();
        List<Token> tokens = new ArrayList<>(  );
        for ( int i = 0; i < ctx.getChildCount(); i++ ) {
            visit( ctx.getChild( i ) );
        }
        ParserHelper.getAllTokens( ctx, tokens );
        for( Token t : tokens ) {
            tokenStrs.add( t.getText() );
        }
        return ASTBuilderFactory.newNameDefNode( ctx, tokenStrs );
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
        List<BaseNode> nodes = new ArrayList<>();
        for ( FEEL_1_1Parser.ContextEntryContext c : ctx.contextEntry() ) {
            nodes.add( visit( c ) );
        }
        return ASTBuilderFactory.newListNode( ctx, nodes );
    }

    @Override
    public BaseNode visitContext(FEEL_1_1Parser.ContextContext ctx) {
        ListNode list = ctx.contextEntries() != null ? (ListNode) visit( ctx.contextEntries() ) : ASTBuilderFactory.newListNode( ctx, new ArrayList<>() );
        return ASTBuilderFactory.newContextNode( ctx, list );
    }

    @Override
    public BaseNode visitFormalParameters(FEEL_1_1Parser.FormalParametersContext ctx) {
        List<BaseNode> list = new ArrayList<>();
        for ( FEEL_1_1Parser.FormalParameterContext fpc : ctx.formalParameter() ) {
            list.add( visit( fpc ) );
        }
        return ASTBuilderFactory.newListNode( ctx, list );
    }

    @Override
    public BaseNode visitFunctionDefinition(FEEL_1_1Parser.FunctionDefinitionContext ctx) {
        ListNode parameters = null;
        if ( ctx.formalParameters() != null ) {
            parameters = (ListNode) visit( ctx.formalParameters() );
        }
        boolean external = ctx.external != null;
        BaseNode body = visit( ctx.body );
        return ASTBuilderFactory.newFunctionDefinition( ctx, parameters, external, body );
    }

    @Override
    public BaseNode visitIterationContext(FEEL_1_1Parser.IterationContextContext ctx) {
        NameDefNode name = (NameDefNode) visit( ctx.nameDefinition() );
        BaseNode expr = visit( ctx.expression() );
        return ASTBuilderFactory.newIterationContextNode( ctx, name, expr );
    }

    @Override
    public BaseNode visitIterationContexts(FEEL_1_1Parser.IterationContextsContext ctx) {
        ArrayList<BaseNode> ctxs = new ArrayList<>();
        for ( FEEL_1_1Parser.IterationContextContext ic : ctx.iterationContext() ) {
            ctxs.add( visit( ic ) );
        }
        return ASTBuilderFactory.newListNode( ctx, ctxs );
    }

    @Override
    public BaseNode visitForExpression(FEEL_1_1Parser.ForExpressionContext ctx) {
        ListNode list = (ListNode) visit( ctx.iterationContexts() );
        BaseNode expr = visit( ctx.expression() );
        return ASTBuilderFactory.newForExpression( ctx, list, expr );
    }

    @Override
    public BaseNode visitQualifiedName(FEEL_1_1Parser.QualifiedNameContext ctx) {
        ArrayList<NameRefNode> parts = new ArrayList<>();
        for ( FEEL_1_1Parser.NameRefContext t : ctx.nameRef() ) {
            parts.add( ASTBuilderFactory.newNameRefNode( t ) );
        }
        return parts.size() > 1 ? ASTBuilderFactory.newQualifiedNameNode( ctx, parts ) : parts.get( 0 );
    }

    @Override
    public BaseNode visitIfExpression(FEEL_1_1Parser.IfExpressionContext ctx) {
        BaseNode c = visit( ctx.c );
        BaseNode t = visit( ctx.t );
        BaseNode e = visit( ctx.e );
        return ASTBuilderFactory.newIfExpression( ctx, c, t, e );
    }

    @Override
    public BaseNode visitQuantExprSome(FEEL_1_1Parser.QuantExprSomeContext ctx) {
        ListNode list = (ListNode) visit( ctx.iterationContexts() );
        BaseNode expr = visit( ctx.expression() );
        return ASTBuilderFactory.newQuantifiedExpression( ctx, QuantifiedExpressionNode.Quantifier.SOME, list, expr );
    }

    @Override
    public BaseNode visitQuantExprEvery(FEEL_1_1Parser.QuantExprEveryContext ctx) {
        ListNode list = (ListNode) visit( ctx.iterationContexts() );
        BaseNode expr = visit( ctx.expression() );
        return ASTBuilderFactory.newQuantifiedExpression( ctx, QuantifiedExpressionNode.Quantifier.EVERY, list, expr );
    }

    @Override
    public BaseNode visitNameRef(FEEL_1_1Parser.NameRefContext ctx) {
        return ASTBuilderFactory.newNameRefNode( ctx );
    }

    @Override
    public BaseNode visitPositionalParameters(FEEL_1_1Parser.PositionalParametersContext ctx) {
        List<BaseNode> params = new ArrayList<>();
        for ( FEEL_1_1Parser.ExpressionContext ec : ctx.expression() ) {
            params.add( visit( ec ) );
        }
        return ASTBuilderFactory.newListNode( ctx, params );
    }

    @Override
    public BaseNode visitNamedParameter(FEEL_1_1Parser.NamedParameterContext ctx) {
        NameDefNode name = (NameDefNode) visit( ctx.name );
        BaseNode value = visit( ctx.value );
        return ASTBuilderFactory.newNamedParameterNode( ctx, name, value );
    }

    @Override
    public BaseNode visitNamedParameters(FEEL_1_1Parser.NamedParametersContext ctx) {
        List<BaseNode> params = new ArrayList<>();
        for ( FEEL_1_1Parser.NamedParameterContext npc : ctx.namedParameter() ) {
            params.add( visit( npc ) );
        }
        return ASTBuilderFactory.newListNode( ctx, params );
    }

    @Override
    public BaseNode visitParametersEmpty(FEEL_1_1Parser.ParametersEmptyContext ctx) {
        return ASTBuilderFactory.newListNode( ctx, new ArrayList<>() );
    }

    @Override
    public BaseNode visitParametersNamed(FEEL_1_1Parser.ParametersNamedContext ctx) {
        return visit( ctx.namedParameters() );
    }

    @Override
    public BaseNode visitParametersPositional(FEEL_1_1Parser.ParametersPositionalContext ctx) {
        return visit( ctx.positionalParameters() );
    }

    @Override
    public BaseNode visitPrimaryName(FEEL_1_1Parser.PrimaryNameContext ctx) {
        BaseNode name = visit( ctx.qualifiedName() );
        if( ctx.parameters() != null ) {
            ListNode params = (ListNode) visit( ctx.parameters() );
            return buildFunctionCall( ctx, name, params );
        } else {
            return name;
        }
    }

    private String getFunctionName(BaseNode name) {
        String functionName = null;
        if ( name instanceof NameRefNode ) {
            // simple name
            functionName = name.getText();
        } else {
            QualifiedNameNode qn = (QualifiedNameNode) name;
            functionName = qn.getParts().stream().map( p -> p.getText() ).collect( Collectors.joining( " ") );
        }
        return functionName;
    }

    private BaseNode buildFunctionCall(ParserRuleContext ctx, BaseNode name, ListNode params) {
        String functionName = getFunctionName( name );
        if( "not".equals( functionName ) ) {
            return buildNotCall( ctx, name, params );
        } else {
            return ASTBuilderFactory.newFunctionInvocationNode( ctx, name, params );
        }
    }

    private BaseNode buildNotCall(ParserRuleContext ctx, BaseNode name, ListNode params) {
        // if a not() call is found, we have to differentiate between the boolean function
        // and the unary tests operator
        if( params.getElements().size() == 1 ) {
            // if it is a single parameter, we need to check if the type is boolean
            BaseNode param = params.getElements().get( 0 );
            if( param instanceof UnaryTestNode ) {
                return ASTBuilderFactory.newUnaryTestNode( ctx, "not", params );
            } else if( param instanceof BooleanNode ) {
                return ASTBuilderFactory.newFunctionInvocationNode( ctx, name, params );
            } else if( param instanceof NameRefNode ) {
                return ASTBuilderFactory.newFunctionInvocationNode( ctx, name, params );
            } else if( param instanceof QuantifiedExpressionNode ) {
                return ASTBuilderFactory.newFunctionInvocationNode( ctx, name, params );
            } else if( param instanceof InstanceOfNode ) {
                return ASTBuilderFactory.newFunctionInvocationNode( ctx, name, params );
            } else if( param instanceof BetweenNode ) {
                return ASTBuilderFactory.newFunctionInvocationNode( ctx, name, params );
            } else if( param instanceof InNode ) {
                return ASTBuilderFactory.newFunctionInvocationNode( ctx, name, params );
            } else if( param instanceof InfixOpNode && ((InfixOpNode)param).isBoolean() ) {
                return ASTBuilderFactory.newFunctionInvocationNode( ctx, name, params );
            } else if( param instanceof RangeNode ) {
                return ASTBuilderFactory.newUnaryTestNode( ctx, "not", params );
            } else if( param instanceof DashNode ) {
                return ASTBuilderFactory.newUnaryTestNode( ctx, "not", params );
            } else {
                return ASTBuilderFactory.newUnaryTestNode( ctx, "not", params );
            }
        } else {
            return ASTBuilderFactory.newUnaryTestNode( ctx, "not", params );
        }
    }

    @Override
    public TypeNode visitType(FEEL_1_1Parser.TypeContext ctx) {
        return ASTBuilderFactory.newTypeNode( ctx );
    }

    @Override
    public BaseNode visitRelExpressionInstanceOf(FEEL_1_1Parser.RelExpressionInstanceOfContext ctx) {
        BaseNode expr = visit( ctx.val );
        TypeNode type = (TypeNode) visit( ctx.type() );
        return ASTBuilderFactory.newInstanceOfNode( ctx, expr, type );
    }

    @Override
    public BaseNode visitFilterPathExpression(FEEL_1_1Parser.FilterPathExpressionContext ctx) {
        if( ctx.filter != null ) {
            BaseNode expr = visit( ctx.filterPathExpression() );
            BaseNode filter = visit( ctx.filter );
            expr = ASTBuilderFactory.newFilterExpressionNode( ctx, expr, filter );
            return expr;
        } else if( ctx.qualifiedName() != null ) {
            BaseNode expr = visit( ctx.filterPathExpression() );
            BaseNode path = visit( ctx.qualifiedName() );
            return ASTBuilderFactory.newPathExpressionNode( ctx, expr, path );
        } else {
            return visit( ctx.unaryExpression() );
        }
    }

    @Override
    public BaseNode visitExpressionTextual(FEEL_1_1Parser.ExpressionTextualContext ctx) {
        BaseNode expr = visit( ctx.expr );
        return expr;
    }

    @Override
    public BaseNode visitUenpmPrimary(FEEL_1_1Parser.UenpmPrimaryContext ctx) {
        BaseNode expr = visit( ctx.primary() );
        if( ctx.qualifiedName() != null ) {
            BaseNode path = visit( ctx.qualifiedName() );
            expr = ASTBuilderFactory.newPathExpressionNode( ctx, expr, path );
        }
        return expr;
    }

    @Override
    public BaseNode visitCompilation_unit(FEEL_1_1Parser.Compilation_unitContext ctx) {
        return visit( ctx.expression() );
    }

    @Override
    public BaseNode visitNegatedUnaryTests(FEEL_1_1Parser.NegatedUnaryTestsContext ctx) {
        BaseNode name = ASTBuilderFactory.newNameRefNode( ctx.not_key() );
        ListNode value = (ListNode) visit( ctx.simpleUnaryTests() );
        return buildFunctionCall( ctx, name, value );
    }
}
