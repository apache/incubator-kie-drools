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
package org.kie.dmn.feel.parser.feel11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTBuilderFactory;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.BetweenNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextTypeNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.FunctionTypeNode;
import org.kie.dmn.feel.lang.ast.InNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InstanceOfNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.ListTypeNode;
import org.kie.dmn.feel.lang.ast.NameDefNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.TypeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode.UnaryOperator;
import org.kie.dmn.feel.lang.ast.visitor.ASTTemporalConstantVisitor;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.DefaultBuiltinFEELTypeRegistry;
import org.kie.dmn.feel.lang.types.FEELTypeRegistry;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.RelExpressionValueContext;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser.TypeContext;
import org.kie.dmn.feel.util.StringEvalHelper;

public class ASTBuilderVisitor
        extends FEEL_1_1BaseVisitor<BaseNode> {
    
    private ScopeHelper<Type> scopeHelper;
    private FEELTypeRegistry typeRegistry;
    private boolean visitedTemporalCandidate = false;

    public ASTBuilderVisitor(Map<String, Type> inputTypes, FEELTypeRegistry typeRegistry) {
        this.scopeHelper = new ScopeHelper<>();
        this.scopeHelper.addInScope(inputTypes);
        this.typeRegistry = typeRegistry != null ? typeRegistry : DefaultBuiltinFEELTypeRegistry.INSTANCE;
    }

    public boolean isVisitedTemporalCandidate() {
        return visitedTemporalCandidate;
    }

    @Override
    public BaseNode visitNumberLiteral(FEEL_1_1Parser.NumberLiteralContext ctx) {
        return ASTBuilderFactory.newNumberNode( ctx );
    }

    @Override
    public BaseNode visitBoolLiteral(FEEL_1_1Parser.BoolLiteralContext ctx) {
        return ASTBuilderFactory.newBooleanNode( ctx );
    }

    @Override
    public BaseNode visitAtLiteral(FEEL_1_1Parser.AtLiteralContext ctx) {
        StringNode stringLiteral = ASTBuilderFactory.newStringNode(ctx.atLiteralValue());
        return ASTBuilderFactory.newAtLiteralNode(ctx, stringLiteral);
    }

    @Override
    public BaseNode visitSignedUnaryExpressionPlus(FEEL_1_1Parser.SignedUnaryExpressionPlusContext ctx) {
        BaseNode node = visit( ctx.unaryExpressionNotPlusMinus() );
        return ASTBuilderFactory.newSignedUnaryNode( ctx, node );
    }

    @Override
    public BaseNode visitSignedUnaryExpressionMinus(FEEL_1_1Parser.SignedUnaryExpressionMinusContext ctx) {
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
    public BaseNode visitPositiveUnaryTestIneqInterval(FEEL_1_1Parser.PositiveUnaryTestIneqIntervalContext ctx) {
        BaseNode value = visit(ctx.endpoint());
        String op = ctx.op.getText();
        switch (UnaryOperator.determineOperator(op)) {
            case GT:
                return ASTBuilderFactory.newIntervalNode(ctx, RangeNode.IntervalBoundary.OPEN, value, ASTBuilderFactory.newNullNode(ctx), RangeNode.IntervalBoundary.OPEN);
            case GTE:
                return ASTBuilderFactory.newIntervalNode(ctx, RangeNode.IntervalBoundary.CLOSED, value, ASTBuilderFactory.newNullNode(ctx), RangeNode.IntervalBoundary.OPEN);
            case LT:
                return ASTBuilderFactory.newIntervalNode(ctx, RangeNode.IntervalBoundary.OPEN, ASTBuilderFactory.newNullNode(ctx), value, RangeNode.IntervalBoundary.OPEN);
            case LTE:
                return ASTBuilderFactory.newIntervalNode(ctx, RangeNode.IntervalBoundary.OPEN, ASTBuilderFactory.newNullNode(ctx), value, RangeNode.IntervalBoundary.CLOSED);
            default:
                throw new UnsupportedOperationException("by the parser rule FEEL grammar rule 7.a for range syntax should not have determined the operator " + op);
        }
    }

    @Override
    public BaseNode visitPositiveUnaryTests(FEEL_1_1Parser.PositiveUnaryTestsContext ctx) {
        List<BaseNode> tests = new ArrayList<>();
        for ( int i = 0; i < ctx.getChildCount(); i++ ) {
            if ( ctx.getChild( i ) instanceof FEEL_1_1Parser.PositiveUnaryTestContext ||
                    ctx.getChild( i ) instanceof FEEL_1_1Parser.PrimaryContext) {
                tests.add( visit( ctx.getChild( i ) ) );
            }
        }
        return ASTBuilderFactory.newListNode( ctx, tests );
    }

    @Override
    public BaseNode visitRelExpressionTestList(FEEL_1_1Parser.RelExpressionTestListContext ctx) {
        BaseNode value = visit( ctx.val );
        BaseNode list = visit( ctx.positiveUnaryTests() );
        return ASTBuilderFactory.newInNode( ctx, value, list );
    }
    
    @Override
    public BaseNode visitRelExpressionValue(RelExpressionValueContext ctx) {
        BaseNode value = visit( ctx.val );
        BaseNode test = visit( ctx.expression() );
        return ASTBuilderFactory.newInNode( ctx, value, test );
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
    public BaseNode visitIterationNameDefinition(FEEL_1_1Parser.IterationNameDefinitionContext ctx) {
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
        return ASTBuilderFactory.newStringNode(ctx);
    }

    @Override
    public BaseNode visitKeyName(FEEL_1_1Parser.KeyNameContext ctx) {
        return visit(ctx.nameDefinition());
    }

    @Override
    public BaseNode visitContextEntry(FEEL_1_1Parser.ContextEntryContext ctx) {
        FEEL_1_1Parser.KeyContext key = ctx.key();
        FEEL_1_1Parser.ExpressionContext expression = ctx.expression();
        if (key == null || expression == null) {
            return null;
        }
        BaseNode name = visit(key);
        BaseNode value = visit(expression);
        if (value == null) return null;
        return ASTBuilderFactory.newContextEntry( ctx, name, value );
    }

    @Override
    public BaseNode visitContextEntries(FEEL_1_1Parser.ContextEntriesContext ctx) {
        List<BaseNode> nodes = new ArrayList<>();
        scopeHelper.pushScope();
        for ( FEEL_1_1Parser.ContextEntryContext c : ctx.contextEntry() ) {
            ContextEntryNode visited = (ContextEntryNode) visit( c ); // forced cast similarly to visitFunctionDefinition() method
            if (visited != null) {
                nodes.add( visited );
                scopeHelper.addInScope(visited.getName().getText(), visited.getResultType());
            }
        }
        scopeHelper.popScope();
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
    public BaseNode visitFormalParameter(FEEL_1_1Parser.FormalParameterContext ctx) {
        NameDefNode name = (NameDefNode) visit(ctx.nameDefinition());
        TypeNode type = ctx.type() != null ? (TypeNode) visit(ctx.type()) : null;
        return ASTBuilderFactory.newFormalParameter(ctx, name, type);
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
        NameDefNode name = (NameDefNode) visit( ctx.iterationNameDefinition() );
        BaseNode expr = visit(ctx.expression().get(0));
        if (ctx.expression().size() == 1) {
            return ASTBuilderFactory.newIterationContextNode(ctx, name, expr);
        } else {
            BaseNode rangeEndExpr = visit(ctx.expression().get(1));
            return ASTBuilderFactory.newIterationContextNode(ctx, name, expr, rangeEndExpr);
        }
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
        Type typeCursor = null;
        for ( FEEL_1_1Parser.NameRefContext t : ctx.nameRef() ) {
            String originalText = ParserHelper.getOriginalText(t);
            if ( typeCursor == null ) {
                typeCursor = scopeHelper.resolve(originalText).orElse(BuiltInType.UNKNOWN);
            } else if ( typeCursor instanceof CompositeType ) {
                typeCursor = ((CompositeType) typeCursor).getFields().get(originalText);
            } else {
                // TODO throw error here?
                typeCursor = BuiltInType.UNKNOWN;
            }
            parts.add( ASTBuilderFactory.newNameRefNode( t, typeCursor ) );
        }
        return parts.size() > 1 ? ASTBuilderFactory.newQualifiedNameNode( ctx, parts, typeCursor ) : parts.get( 0 );
    }

    @Override
    public BaseNode visitIfExpression(FEEL_1_1Parser.IfExpressionContext ctx) {
        BaseNode c = visit( ctx.c );
        BaseNode t = ctx.t != null ? visit(ctx.t) : null;
        BaseNode e = ctx.e != null ? visit(ctx.e) : null;
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

    // TODO verify, this is never covered in test, possibly as qualifiedName visitor "ingest" it directly.
    @Override
    public BaseNode visitNameRef(FEEL_1_1Parser.NameRefContext ctx) {
        return ASTBuilderFactory.newNameRefNode( ctx, BuiltInType.UNKNOWN );
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
    public BaseNode visitFnInvocation(FEEL_1_1Parser.FnInvocationContext ctx) {
        BaseNode name = visit(ctx.unaryExpression());
        ListNode params = (ListNode) visit(ctx.parameters());
        if (ASTTemporalConstantVisitor.TEMPORAL_FNS_NAMES.contains(name.getText())) {
            visitedTemporalCandidate = true;
        }
        return buildFunctionCall(ctx, name, params);
    }

    @Override
    public BaseNode visitPrimaryName(FEEL_1_1Parser.PrimaryNameContext ctx) {
        return visit(ctx.qualifiedName());
    }

    private String getFunctionName(BaseNode name) {
        String functionName = null;
        if ( name instanceof NameRefNode ) {
            // simple name
            functionName = name.getText();
        } else if (name instanceof QualifiedNameNode) {
            QualifiedNameNode qn = (QualifiedNameNode) name;
            functionName = qn.getParts().stream().map( p -> p.getText() ).collect( Collectors.joining( " ") );
        }
        return functionName;
    }

    private BaseNode buildFunctionCall(ParserRuleContext ctx, BaseNode name, ListNode params) {
        String functionName = getFunctionName( name );
        return ASTBuilderFactory.newFunctionInvocationNode( ctx, name, params );
    }

    @Override
    public BaseNode visitUnaryTestsRoot(FEEL_1_1Parser.UnaryTestsRootContext ctx) {
        return visit(ctx.unaryTests());
    }

    @Override
    public BaseNode visitUnaryTests_empty(FEEL_1_1Parser.UnaryTests_emptyContext ctx) {
        return ASTBuilderFactory.newUnaryTestListNode(ctx, Collections.singletonList(ASTBuilderFactory.newDashNode(ctx)), UnaryTestListNode.State.Positive);
    }

    @Override
    public BaseNode visitUnaryTests_positive(FEEL_1_1Parser.UnaryTests_positiveContext ctx) {
        ListNode list = (ListNode) visit(ctx.positiveUnaryTests());
        return ASTBuilderFactory.newUnaryTestListNode(ctx, list.getElements(), UnaryTestListNode.State.Positive);
    }

    @Override
    public BaseNode visitUnaryTests_negated(FEEL_1_1Parser.UnaryTests_negatedContext ctx) {
        BaseNode name = ASTBuilderFactory.newNameRefNode( ctx, "not", BuiltInType.BOOLEAN ); // negating a unary tests: BOOLEAN-type anyway
        ListNode value = (ListNode) visit( ctx.positiveUnaryTests() );
        return ASTBuilderFactory.newUnaryTestListNode(ctx, value.getElements(), UnaryTestListNode.State.Negated);
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
            } else if (param instanceof FunctionInvocationNode) {
                return ASTBuilderFactory.newFunctionInvocationNode(ctx, name, params);
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
    public TypeNode visitQnType(FEEL_1_1Parser.QnTypeContext ctx) {
        List<String> qns = new ArrayList<>();
        if (ctx.qualifiedName() != null) {
            ctx.qualifiedName().nameRef().forEach(nr -> qns.add(StringEvalHelper.normalizeVariableName(ParserHelper.getOriginalText(nr))));
        } else if (ctx.FUNCTION() != null) {
            qns.add("function");
        } else {
            throw new IllegalStateException("grammar rule changed.");
        }
        return ASTBuilderFactory.newCTypeNode(ctx, typeRegistry.resolveFEELType(qns));
    }

    @Override
    public BaseNode visitListType(FEEL_1_1Parser.ListTypeContext ctx) {
        TypeNode type = (TypeNode) visit(ctx.type());
        return new ListTypeNode(ctx, type);
    }

    @Override
    public BaseNode visitContextType(FEEL_1_1Parser.ContextTypeContext ctx) {
        List<String> pNames = new ArrayList<>();
        for (TerminalNode id : ctx.Identifier()) {
            pNames.add(id.getText());
        }
        if (!pNames.get(0).equals("context")) {
            throw new IllegalStateException("grammar rule changed.");
        } else {
            pNames.remove(0);
        }
        List<TypeNode> pTypes = new ArrayList<>();
        for (TypeContext t : ctx.type()) {
            pTypes.add((TypeNode) visit(t));
        }
        Map<String, TypeNode> gens = new HashMap<>();
        for (int i = 0; i < pNames.size(); i++) {
            gens.put(pNames.get(i), pTypes.get(i));
        }
        return new ContextTypeNode(ctx, gens);
    }

    @Override
    public BaseNode visitFunctionType(FEEL_1_1Parser.FunctionTypeContext ctx) {
        List<TypeNode> argTypes = new ArrayList<>();
        for (TypeContext t : ctx.type()) {
            argTypes.add((TypeNode) visit(t));
        }
        TypeNode type = argTypes.remove(argTypes.size() - 1);
        return new FunctionTypeNode(ctx, argTypes, type);
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
        if (ctx.parameters() != null) {
            ListNode params = (ListNode) visit(ctx.parameters());
            return buildFunctionCall(ctx, expr, params);
        }
        return expr;
    }

    @Override
    public BaseNode visitCompilation_unit(FEEL_1_1Parser.Compilation_unitContext ctx) {
        return visit( ctx.expression() );
    }

}
