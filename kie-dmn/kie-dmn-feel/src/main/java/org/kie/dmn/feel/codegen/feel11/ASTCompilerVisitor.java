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
package org.kie.dmn.feel.codegen.feel11;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.BetweenNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.CTypeNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextNode;
import org.kie.dmn.feel.lang.ast.ContextTypeNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.FilterExpressionNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.FormalParameterNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.FunctionTypeNode;
import org.kie.dmn.feel.lang.ast.IfExpressionNode;
import org.kie.dmn.feel.lang.ast.InNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.ast.InstanceOfNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.ListTypeNode;
import org.kie.dmn.feel.lang.ast.NameDefNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NamedParameterNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.PathExpressionNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.SignedUnaryNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.Visitor;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.StringEvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.ARRAYS_N;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.AS_LIST;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.BIG_DECIMAL_N;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.BIG_DECIMAL_T;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.BOOLEAN_T;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.FEELCTX;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.INFIXOPERATOR_T;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.LIST_T;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.OBJECT_T;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.PARAM_T;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.STATIC_EVALUATION;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.STRING_T;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.TYPE_T;
import static org.kie.dmn.feel.codegen.feel11.FeelCtx.VALUE_OF;
import static org.kie.dmn.feel.lang.ast.FormalParameterNode.FORMALPARAMETERNODE_N;
import static org.kie.dmn.feel.lang.ast.FunctionDefNode.FUNCTIONDEFNODE_N;
import static org.kie.dmn.feel.lang.ast.InNode.INNODE_N;
import static org.kie.dmn.feel.lang.ast.InfixOpNode.INFIXOPNODE_N;
import static org.kie.dmn.feel.lang.ast.NameRefNode.NAMEREFNODE_N;

public class ASTCompilerVisitor implements Visitor<BlockStmt> {

    //ScopeHelper<Type> scopeHelper = new ScopeHelper<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ASTCompilerVisitor.class);
    private static final String VAR_BASE = "var";
    private final BlockStmt toPopulate;
    private final AtomicInteger variableCounter;
    private AtomicReference<String> lastVariableName = new AtomicReference<>();

    public ASTCompilerVisitor() {
        toPopulate = new BlockStmt();
        variableCounter = new AtomicInteger(0);
    }

    @Override
    public BlockStmt visit(ASTNode n) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public BlockStmt visit(AtLiteralNode n) {
        return n.accept(this);
//        BlockStmt stringLiteral = n.getStringLiteral().accept(this);
//        String value = ((StringLiteralExpr) stringLiteral.getExpression()).asString();
//        TypeAndFn typeAndFn = AtLiteralNode.fromAtValue(value);
//        String functionName = typeAndFn.fnName;
//        Type resultType = typeAndFn.type;
//        if (resultType == BuiltInType.UNKNOWN) {
//            return BlockStmt.of(CompiledFEELSupport.compiledErrorExpression(Msg.createMessage(Msg.MALFORMED_AT_LITERAL, n.getText())),
//                                           BuiltInType.UNKNOWN);
//        }
//        return BlockStmt.of(Expressions.invoke(FeelCtx.getValue(functionName),
//                                                          stringLiteral.getExpression()),
//                                       resultType)
//                                   .withFD(stringLiteral);
    }

    @Override
    public BlockStmt visit(BetweenNode n) {
        return n.accept(this);
//        BlockStmt value = n.getValue().accept(this);
//        BlockStmt start = n.getStart().accept(this);
//        BlockStmt end = n.getEnd().accept(this);
//
//        return BlockStmt.of(
//                Expressions.between(
//                        value.getExpression(),
//                        start.getExpression(),
//                        end.getExpression()),
//                BuiltInType.BOOLEAN)
//                .withFD(value)
//                .withFD(start)
//                .withFD(end);
    }

    @Override
    public BlockStmt visit(BooleanNode n) {
        return n.accept(this);
    }

    @Override
    public BlockStmt visit(ContextEntryNode n) {
        return n.accept(this);
//        BlockStmt key = n.getName().accept(this);
//        BlockStmt value = n.getValue().accept(this);
//        if (key.resultType != BuiltInType.STRING) {
//            throw new IllegalArgumentException(
//                    "a Context Entry Key must be a valid FEEL String type");
//        }
//        String keyText = key.getExpression().asStringLiteralExpr().getValue();
//
//        // .setEntry(key, value)
//        MethodCallExpr setEntryContextCall =
//                FeelCtx.setEntry(keyText, value.getExpression());
//
//        return BlockStmt.of(
//                setEntryContextCall,
//                value.resultType,
//                value.getFieldDeclarations());
    }

    @Override
    public BlockStmt visit(ContextNode n) {
        return n.accept(this);
//        if (n.getEntries().isEmpty()) {
//            return BlockStmt.of(
//                    FeelCtx.emptyContext(), BuiltInType.CONTEXT);
//        }
//
//        scopeHelper.pushScope();
//
//        // openContext(feelCtx)
//        MapBackedType resultType = new MapBackedType();
//        BlockStmt openContext =
//                BlockStmt.of(FeelCtx.openContext(), resultType);
//
//        //   .setEntry( k,v )
//        //   .setEntry( k,v )
//        //   ...
//        BlockStmt entries = n.getEntries()
//                .stream()
//                .map(e -> {
//                    BlockStmt r = e.accept(this);
//                    scopeHelper.addInScope(e.getName().getText(), r.resultType);
//                    return r;
//                })
//                .reduce(openContext,
//                        (l, r) -> BlockStmt.of(
//                                r.getExpression().asMethodCallExpr().setScope(l.getExpression()),
//                                r.resultType,
//                                BlockStmt.mergeFDs(l, r)));
//
//        scopeHelper.popScope();
//
//        // .closeContext()
//        return BlockStmt.of(
//                FeelCtx.closeContext(entries),
//                resultType,
//                entries.getFieldDeclarations());
    }

    @Override
    public BlockStmt visit(ContextTypeNode n) {
        return n.accept(this);
//        Map<String, BlockStmt> fields = new HashMap<>();
//        for (Entry<String, TypeNode> kv : n.getGen().entrySet()) {
//            fields.put(kv.getKey(), kv.getValue().accept(this));
//        }
//        return BlockStmt.of(Expressions.genContextType(fields.entrySet().stream().collect(Collectors.toMap(Map
//        .Entry::getKey, e -> e.getValue().getExpression()))),
//                                       BuiltInType.UNKNOWN,
//                                       mergeFDs(fields.values().stream().collect(Collectors.toList())));
    }

    @Override
    public BlockStmt visit(CTypeNode n) {
        if (!(n.getType() instanceof BuiltInType)) {
            throw new UnsupportedOperationException();
        }
        BuiltInType feelCType = (BuiltInType) n.getType();
        String variableName = getNextVariableName();
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(TYPE_T, variableName);
        final VariableDeclarationExpr toAdd = new VariableDeclarationExpr(variableDeclarator);
        String fullEnumString = String.format("%s.%s", BuiltInType.class.getName(), feelCType.name());
        variableDeclarator.setInitializer(new NameExpr(fullEnumString));
        return addStatement(toAdd, variableName);
    }

    @Override
    public BlockStmt visit(DashNode n) {
        return n.accept(this);
    }

    @Override
    public BlockStmt visit(ForExpressionNode n) {
        return n.accept(this);
//        BlockStmt expr = n.getExpression().accept(this);
//        HashSet<FieldDeclaration> fds = new HashSet<>();
//
//        Expressions.NamedLambda namedLambda =
//                Expressions.namedLambda(
//                        expr.getExpression(),
//                        n.getExpression().getText());
//
//        fds.add(namedLambda.field());
//        fds.addAll(expr.getFieldDeclarations());
//
//        List<Expression> expressions = n.getIterationContexts()
//                .stream()
//                .map(iter -> iter.accept(this))
//                .peek(r -> fds.addAll(r.getFieldDeclarations()))
//                .map(BlockStmt::getExpression)
//                .collect(Collectors.toList());
//
//        // .satisfies(expr)
//        return BlockStmt.of(
//                Expressions.ffor(expressions, namedLambda.name()),
//                expr.resultType,
//                fds);
    }

    @Override
    public BlockStmt visit(FilterExpressionNode n) {
        return n.accept(this);
//        BlockStmt expr = n.getExpression().accept(this);
//        BlockStmt filter = n.getFilter().accept(this);
//
//        Expressions.NamedLambda lambda = Expressions.namedLambda(filter.getExpression(), n.getFilter().getText());
//        BlockStmt r = BlockStmt.of(
//                Expressions.filter(expr.getExpression(), lambda.name()),
//                // here we could still try to infer the result type, but presently use ANY
//                BuiltInType.UNKNOWN).withFD(expr).withFD(filter);
//        r.addFieldDeclaration(lambda.field());
//        return r;
    }

    @Override
    public BlockStmt visit(FormalParameterNode n) {
        n.getName().accept(this);
        String nameVariableName = lastVariableName.get();
        n.getType().accept(this);
        String typeVariableName = lastVariableName.get();
        // static evaluation invocation
        String variableName = getNextVariableName();
        VariableDeclarator variableDeclarator =
                new VariableDeclarator(PARAM_T, variableName);
        MethodCallExpr assignMethod = new MethodCallExpr(FORMALPARAMETERNODE_N, STATIC_EVALUATION);
        assignMethod.addArgument(new NameExpr(nameVariableName));
        assignMethod.addArgument(new NameExpr(typeVariableName));

        variableDeclarator.setInitializer(assignMethod);
        VariableDeclarationExpr toAdd = new VariableDeclarationExpr(variableDeclarator);
        return addStatement(toAdd, variableName);
    }

    @Override
    public BlockStmt visit(FunctionDefNode n) {
        List<String> paramNames = new ArrayList<>();
        List<NameExpr> paramVariables = new ArrayList<>();
        n.getFormalParameters().forEach(formalParameter -> {
            formalParameter.accept(this);
            paramNames.add(formalParameter.getName().getText());
            paramVariables.add(new NameExpr(lastVariableName.get()));
        });
        // Add parameter list
        String parameterListVariableName = getNextVariableName();
        VariableDeclarator parameterListVariableDeclarator = new VariableDeclarator(LIST_T, parameterListVariableName);
        MethodCallExpr parameterListVariableAssignMethod = new MethodCallExpr(ARRAYS_N, AS_LIST);
        paramVariables.forEach(parameterListVariableAssignMethod::addArgument);
        parameterListVariableDeclarator.setInitializer(parameterListVariableAssignMethod);
        VariableDeclarationExpr toAdd = new VariableDeclarationExpr(parameterListVariableDeclarator);
        addStatement(toAdd, parameterListVariableName);

        if (n.isExternal()) {
            // Add body evaluation
            n.getBody().accept(this);
            String bodyVariableName = lastVariableName.get();
            // static evaluation invocation
            String variableName = getNextVariableName();
            VariableDeclarator variableDeclarator =
                    new VariableDeclarator(OBJECT_T, variableName);
            MethodCallExpr assignMethod = new MethodCallExpr(FUNCTIONDEFNODE_N, STATIC_EVALUATION);
            assignMethod.addArgument(FEELCTX);
            assignMethod.addArgument(new NameExpr(parameterListVariableName));
            assignMethod.addArgument(new NameExpr(bodyVariableName));

            variableDeclarator.setInitializer(assignMethod);
            toAdd = new VariableDeclarationExpr(variableDeclarator);
            return addStatement(toAdd, variableName);
        } else {
            BlockStmt newInstance = n.getBody().newInstance(this);
            LambdaExpr lambda = Expressions.supplierLambda(new NameExpr(bodyVariableName));
            // static evaluation invocation
            String variableName = getNextVariableName();
            VariableDeclarator variableDeclarator =
                    new VariableDeclarator(OBJECT_T, variableName);
            MethodCallExpr assignMethod = new MethodCallExpr(FUNCTIONDEFNODE_N, STATIC_EVALUATION);
            assignMethod.addArgument(FEELCTX);
            assignMethod.addArgument(new NameExpr(parameterListVariableName));
            assignMethod.addArgument(lambda);

            variableDeclarator.setInitializer(assignMethod);
            toAdd = new VariableDeclarationExpr(variableDeclarator);
            return addStatement(toAdd, variableName);
        }
    }

    @Override
    public BlockStmt visit(FunctionInvocationNode n) {
        return n.accept(this);
//        TemporalConstantNode tcFolded = n.getTcFolded();
//        if (tcFolded != null) {
//            return replaceWithTemporalConstant(n, tcFolded);
//        }
//        BlockStmt functionName = n.getName().accept(this);
//        BlockStmt params = n.getParams().accept(this);
//        return BlockStmt.of(
//                Expressions.invoke(functionName.getExpression(), params.getExpression()),
//                functionName.resultType)
//                .withFD(functionName)
//                .withFD(params);
    }

    @Override
    public BlockStmt visit(FunctionTypeNode n) {
        return n.accept(this);
//        List<BlockStmt> args = new ArrayList<>();
//        for (TypeNode arg : n.getArgTypes()) {
//            args.add(arg.accept(this));
//        }
//        BlockStmt ret = n.getRetType().accept(this);
//        return BlockStmt.of(Expressions.genFnType(args.stream().map(BlockStmt::getExpression).collect(Collectors
//        .toList()),
//                                                             ret.getExpression()),
//                                       BuiltInType.UNKNOWN,
//                                       mergeFDs(args))
//                                   .withFD(ret);
    }

    @Override
    public BlockStmt visit(IfExpressionNode n) {
        return n.accept(this);
//        BlockStmt condition = n.getCondition().accept(this);
//        BlockStmt thenExpr = n.getThenExpression().accept(this);
//        BlockStmt elseExpr = n.getElseExpression().accept(this);
//
//        return BlockStmt.of(
//                new ConditionalExpr(
//                        new BinaryExpr(
//                                Expressions.nativeInstanceOf(
//                                        Constants.BooleanT, condition.getExpression()),
//                                Expressions.reflectiveCastTo(
//                                        Constants.BooleanT, condition.getExpression()),
//                                BinaryExpr.Operator.AND),
//                        new EnclosedExpr(thenExpr.getExpression()),
//                        new EnclosedExpr(elseExpr.getExpression())),
//                thenExpr.resultType // should find common type between then/else
//        ).withFD(condition).withFD(thenExpr).withFD(elseExpr);
    }

    @Override
    public BlockStmt visit(InfixOpNode n) {
        String leftVariableName;
        VariableDeclarationExpr toAdd;
        if (n.getLeft() != null) {
            n.getLeft().accept(this);
            leftVariableName = lastVariableName.get();
        } else {
            leftVariableName = getNextVariableName();
            VariableDeclarator leftVariableDeclarator =
                    new VariableDeclarator(OBJECT_T, leftVariableName);
            leftVariableDeclarator.setInitializer(new NullLiteralExpr());
            toAdd = new VariableDeclarationExpr(leftVariableDeclarator);
            addStatement(toAdd, leftVariableName);
        }
        String rightVariableName;
        if (n.getRight() != null) {
            n.getRight().accept(this);
            rightVariableName = lastVariableName.get();
        } else {
            rightVariableName = getNextVariableName();
            VariableDeclarator rightVariableDeclarator =
                    new VariableDeclarator(OBJECT_T, leftVariableName);
            rightVariableDeclarator.setInitializer(new NullLiteralExpr());
            toAdd = new VariableDeclarationExpr(rightVariableDeclarator);
            addStatement(toAdd, rightVariableName);
        }
        // evaluate operator
        String operatorVariableName = getNextVariableName();
        final VariableDeclarator operatorVariableDeclarator =
                new VariableDeclarator(INFIXOPERATOR_T, operatorVariableName);
        String fullEnumString = String.format("%s.%s", InfixOperator.class.getName(), n.getOperator().name());
        operatorVariableDeclarator.setInitializer(new NameExpr(fullEnumString));
        toAdd = new VariableDeclarationExpr(operatorVariableDeclarator);
        addStatement(toAdd, rightVariableName);

        // static evaluation invocation
        String variableName = getNextVariableName();
        VariableDeclarator variableDeclarator =
                new VariableDeclarator(OBJECT_T, variableName);
        MethodCallExpr assignMethod = new MethodCallExpr(INFIXOPNODE_N, STATIC_EVALUATION);
        assignMethod.addArgument(FEELCTX);
        assignMethod.addArgument(new NameExpr(operatorVariableName));
        assignMethod.addArgument(new NameExpr(leftVariableName));
        assignMethod.addArgument(new NameExpr(rightVariableName));
        variableDeclarator.setInitializer(assignMethod);
        toAdd = new VariableDeclarationExpr(variableDeclarator);
        return addStatement(toAdd, variableName);
    }

    @Override
    public BlockStmt visit(InNode n) {
        n.getValue().accept(this);
        String valueVariableName = lastVariableName.get();
        n.getExprs().accept(this);
        String exprVariableName = lastVariableName.get();
        String variableName = getNextVariableName();

        final VariableDeclarator variableDeclarator = new VariableDeclarator(BOOLEAN_T, variableName);
        MethodCallExpr assignMethod = new MethodCallExpr(INNODE_N, STATIC_EVALUATION);
        assignMethod.addArgument(FEELCTX);
        assignMethod.addArgument(new NameExpr(valueVariableName));
        assignMethod.addArgument(new NameExpr(exprVariableName));
        assignMethod.addArgument(new NullLiteralExpr());
        variableDeclarator.setInitializer(assignMethod);
        final VariableDeclarationExpr toAdd = new VariableDeclarationExpr(variableDeclarator);
        return addStatement(toAdd, variableName);
    }

    @Override
    public BlockStmt visit(InstanceOfNode n) {
        return n.accept(this);
//        BlockStmt expr = n.getExpression().accept(this);
//        BlockStmt type = n.getType().accept(this);
//        switch (n.getType().getText()) {
//            case SimpleType.YEARS_AND_MONTHS_DURATION:
//                return BlockStmt.of(Expressions.nativeInstanceOf(StaticJavaParser.parseClassOrInterfaceType
//                (ChronoPeriod.class.getCanonicalName()),
//                                                                            expr.getExpression()),
//                                               BuiltInType.BOOLEAN,
//                                               mergeFDs(expr, type));
//            case SimpleType.DAYS_AND_TIME_DURATION:
//                return BlockStmt.of(Expressions.nativeInstanceOf(StaticJavaParser.parseClassOrInterfaceType
//                (Duration.class.getCanonicalName()),
//                                                                            expr.getExpression()),
//                                               BuiltInType.BOOLEAN,
//                                               mergeFDs(expr, type));
//            default:
//                return BlockStmt.of(Expressions.isInstanceOf(expr.getExpression(), type.getExpression()),
//                                               BuiltInType.BOOLEAN,
//                                               mergeFDs(expr, type));
//        }

    }

    @Override
    public BlockStmt visit(IterationContextNode n) {
        return n.accept(this);
//        BlockStmt iterName = n.getName().accept(this);
//        BlockStmt iterExpr = n.getExpression().accept(this);
//
//        Expressions.NamedLambda nameLambda =
//                Expressions.namedLambda(
//                        iterName.getExpression(),
//                        n.getName().getText());
//        Expressions.NamedLambda exprLambda =
//                Expressions.namedLambda(
//                        iterExpr.getExpression(),
//                        n.getExpression().getText());
//
//        MethodCallExpr with =
//                new MethodCallExpr(null, "with")
//                        .addArgument(nameLambda.name())
//                        .addArgument(exprLambda.name());
//
//        BlockStmt r =
//                BlockStmt.of(with, BuiltInType.UNKNOWN);
//        r.addFieldDeclaration(nameLambda.field());
//        r.addFieldDeclaration(exprLambda.field());
//        r.withFD(iterName);
//        r.withFD(iterExpr);
//
//        BaseNode rangeEndExpr = n.getRangeEndExpr();
//        if (rangeEndExpr != null) {
//            BlockStmt rangeEnd = rangeEndExpr.accept(this);
//            Expressions.NamedLambda rangeLambda =
//                    Expressions.namedLambda(
//                            rangeEnd.getExpression(),
//                            rangeEndExpr.getText());
//            with.addArgument(rangeLambda.name());
//            r.addFieldDeclaration(rangeLambda.field());
//            r.withFD(rangeEnd);
//        }
//
//        return r;
    }

    @Override
    public BlockStmt visit(ListNode n) {
        List<String> variableNames = n.getElements().stream().map(element -> {
                    element.accept(this);
                    return lastVariableName.get();
                })
                .toList();
        String variableName = getNextVariableName();
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(LIST_T, variableName);
        MethodCallExpr assignMethod = new MethodCallExpr(ARRAYS_N, AS_LIST);
        variableNames.forEach(s -> assignMethod.addArgument(new NameExpr(s)));
        variableDeclarator.setInitializer(assignMethod);
        final VariableDeclarationExpr toAdd = new VariableDeclarationExpr(variableDeclarator);
        return addStatement(toAdd, variableName);
    }

    @Override
    public BlockStmt visit(ListTypeNode n) {
        return n.accept(this);
//        BlockStmt expr = n.getGenTypeNode().accept(this);
//        return BlockStmt.of(Expressions.genListType(expr.getExpression()),
//                                       BuiltInType.UNKNOWN,
//                                       mergeFDs(expr));
    }

    @Override
    public BlockStmt visit(NameDefNode n) {
        String variableName = getNextVariableName();
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(STRING_T, variableName);
        final VariableDeclarationExpr toAdd = new VariableDeclarationExpr(variableDeclarator);
        variableDeclarator.setInitializer(new StringLiteralExpr(n.getText()));
        return addStatement(toAdd, variableName);
    }

    @Override
    public BlockStmt visit(NamedParameterNode n) {
        return n.accept(this);
//        BlockStmt name = n.getName().accept(this);
//        BlockStmt expr = n.getExpression().accept(this);
//        return BlockStmt.of(
//                Expressions.namedParameter(name.getExpression(), expr.getExpression()),
//                BuiltInType.UNKNOWN).withFD(name).withFD(expr);
    }

    @Override
    public BlockStmt visit(NameRefNode n) {
        String nameRef = StringEvalHelper.normalizeVariableName(n.getText());

        // static evaluation invocation
        String variableName = getNextVariableName();
        VariableDeclarator variableDeclarator =
                new VariableDeclarator(OBJECT_T, variableName);
        MethodCallExpr assignMethod = new MethodCallExpr(NAMEREFNODE_N, STATIC_EVALUATION);
        assignMethod.addArgument(FEELCTX);
        assignMethod.addArgument(new StringLiteralExpr(nameRef));
        assignMethod.addArgument(new NullLiteralExpr());

        variableDeclarator.setInitializer(assignMethod);
        VariableDeclarationExpr toAdd = new VariableDeclarationExpr(variableDeclarator);
        return addStatement(toAdd, variableName);
    }

    @Override
    public BlockStmt visit(NullNode n) {
        return n.accept(this);
//        return BlockStmt.of(new NullLiteralExpr(), BuiltInType.UNKNOWN);
    }

    @Override
    public BlockStmt visit(NumberNode n) {
        String variableName = getNextVariableName();
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(BIG_DECIMAL_T, variableName);
        MethodCallExpr assignMethod = new MethodCallExpr(BIG_DECIMAL_N, VALUE_OF);
        assignMethod.addArgument(n.getValue().toEngineeringString());
        variableDeclarator.setInitializer(assignMethod);
        final VariableDeclarationExpr toAdd = new VariableDeclarationExpr(variableDeclarator);
        return addStatement(toAdd, variableName);
    }

    @Override
    public BlockStmt visit(PathExpressionNode n) {
        return n.accept(this);
//        BlockStmt expr = n.getExpression().accept(this);
//        BaseNode nameNode = n.getName();
//        if (nameNode instanceof QualifiedNameNode) {
//            QualifiedNameNode qualifiedNameNode = (QualifiedNameNode) n.getName();
//            List<Expression> exprs =
//                    qualifiedNameNode.getParts().stream()
//                            .map(name -> new StringLiteralExpr(name.getText()))
//                            .collect(Collectors.toList());
//
//            return BlockStmt.of(
//                    Expressions.path(expr.getExpression(), exprs),
//                    // here we could still try to infer the result type, but presently use ANY
//                    BuiltInType.UNKNOWN).withFD(expr);
//        } else {
//            return BlockStmt.of(
//                    Expressions.path(expr.getExpression(), new StringLiteralExpr(nameNode.getText())),
//                    // here we could still try to infer the result type, but presently use ANY
//                    BuiltInType.UNKNOWN).withFD(expr);
//        }
    }

    @Override
    public BlockStmt visit(QualifiedNameNode n) {
        return n.accept(this);
//        List<NameRefNode> parts = n.getParts();
//        BlockStmt nameRef0 = parts.get(0).accept(this);
//        Type typeCursor = nameRef0.resultType;
//        Expression currentContext = nameRef0.getExpression();
//        for (int i = 1; i < parts.size(); i++) {
//            NameRefNode acc = parts.get(i);
//            String key = acc.getText();
//            if (typeCursor instanceof CompositeType) {
//                CompositeType currentContextType = (CompositeType) typeCursor;
//                currentContext = Contexts.getKey(currentContext, currentContextType, key);
//                typeCursor = currentContextType.getFields().get(key);
//            } else {
//                //  degraded mode, or accessing fields of DATE etc.
//                currentContext = Expressions.path(currentContext, new StringLiteralExpr(key));
//                typeCursor = BuiltInType.UNKNOWN;
//            }
//        }
//        // If it was a NameRef expression, the number coercion is directly performed by the EvaluationContext for
//        the simple variable.
//        // Otherwise in case of QualifiedName expression, for a structured type like this case, it need to be
//        coerced on the last accessor:
//        return BlockStmt.of(
//                Expressions.coerceNumber(currentContext),
//                typeCursor);
    }

    @Override
    public BlockStmt visit(QuantifiedExpressionNode n) {
        return n.accept(this);
//        BlockStmt expr = n.getExpression().accept(this);
//        HashSet<FieldDeclaration> fds = new HashSet<>();
//
//        Expressions.NamedLambda namedLambda =
//                Expressions.namedLambda(
//                        expr.getExpression(),
//                        n.getExpression().getText());
//
//        fds.add(namedLambda.field());
//        fds.addAll(expr.getFieldDeclarations());
//
//        List<Expression> expressions = n.getIterationContexts()
//                .stream()
//                .map(iter -> iter.accept(this))
//                .peek(r -> fds.addAll(r.getFieldDeclarations()))
//                .map(BlockStmt::getExpression)
//                .collect(Collectors.toList());
//
//        // .satisfies(expr)
//        return BlockStmt.of(
//                Expressions.quantifier(n.getQuantifier(), namedLambda.name(), expressions),
//                expr.resultType,
//                fds);
    }

    @Override
    public BlockStmt visit(RangeNode n) {
        return n.accept(this);
//        BlockStmt start = n.getStart().accept(this);
//        BlockStmt end = n.getEnd().accept(this);
//        return BlockStmt.of(
//                Expressions.range(
//                        n.getLowerBound(),
//                        start.getExpression(),
//                        end.getExpression(),
//                        n.getUpperBound()),
//                BuiltInType.RANGE,
//                BlockStmt.mergeFDs(start, end));
    }

    @Override
    public BlockStmt visit(SignedUnaryNode n) {
        return n.accept(this);
//        BlockStmt result = n.getExpression().accept(this);
//        if (n.getSign() == SignedUnaryNode.Sign.NEGATIVE) {
//            return BlockStmt.of(
//                    Expressions.negate(result.getExpression()),
//                    result.resultType,
//                    result.getFieldDeclarations());
//        } else {
//            return BlockStmt.of(
//                    Expressions.positive(result.getExpression()),
//                    result.resultType,
//                    result.getFieldDeclarations());
//        }
    }

    @Override
    public BlockStmt visit(StringNode n) {
        String variableName = getNextVariableName();
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(STRING_T, variableName);
        final VariableDeclarationExpr toAdd = new VariableDeclarationExpr(variableDeclarator);
        variableDeclarator.setInitializer(new StringLiteralExpr(n.getValue()));
        return addStatement(toAdd, variableName);
    }

    @Override
    public BlockStmt visit(UnaryTestListNode n) {
        return n.accept(this);
//        MethodCallExpr expr = Expressions.list();
//        HashSet<FieldDeclaration> fds = new HashSet<>();
//        for (BaseNode e : n.getElements()) {
//            BlockStmt r = e.accept(this);
//            fds.addAll(r.getFieldDeclarations());
//            expr.addArgument(r.getExpression());
//        }
//
//        if (n.isNegated()) {
//            Expressions.NamedLambda negated =
//                    Expressions.namedUnaryLambda(
//                            Expressions.notExists(expr), n.getText());
//
//            fds.add(negated.field());
//            return BlockStmt.of(
//                    Expressions.list(negated.name()),
//                    BuiltInType.LIST, fds);
//        } else {
//            return BlockStmt.of(
//                    expr, BuiltInType.LIST, fds);
//        }
    }

//    public BlockStmt replaceWithTemporalConstant(FunctionInvocationNode n, TemporalConstantNode tcFolded) {
//        return null;
////        MethodCallExpr methodCallExpr = new MethodCallExpr(new FieldAccessExpr(new NameExpr(tcFolded.fn.getClass()
////        .getCanonicalName()),
////                                                                               "INSTANCE"),
////                                                           "invoke");
////        for (Object p : tcFolded.params) {
////            if (p instanceof String) {
////                methodCallExpr.addArgument(Expressions.stringLiteral((String) p));
////            } else if (p instanceof Number) {
////                methodCallExpr.addArgument(new IntegerLiteralExpr(p.toString()));
////            } else {
////                throw new IllegalStateException("Unexpected Temporal Constant parameter found.");
////            }
////        }
////        methodCallExpr = new MethodCallExpr(methodCallExpr, "getOrElseThrow"); // since this AST Node exists, the
////        Fn invocation returns result.
////        methodCallExpr.addArgument(new LambdaExpr(new Parameter(new UnknownType(), "e"),
////                                                  Expressions.newIllegalState()));
////        String constantName = Constants.dtConstantName(n.getText());
////        FieldDeclaration constant = Constants.dtConstant(constantName, methodCallExpr);
////        return BlockStmt.of(new NameExpr(constantName),
////                                       BuiltInType.UNKNOWN,
////                                       constant);
//    }

    @Override
    public BlockStmt visit(UnaryTestNode n) {
        return n.accept(this);
//        BlockStmt value = n.getValue().accept(this);
//        Expression expr = Expressions.unary(n.getOperator(), value.getExpression());
//        Expressions.NamedLambda namedLambda = Expressions.namedUnaryLambda(expr, n.getText());
//        BlockStmt r =
//                BlockStmt.of(namedLambda.name(), BuiltInType.UNARY_TEST)
//                        .withFD(value);
//        r.addFieldDeclaration(namedLambda.field());
//        return r;
    }

    public String getLastVariableName() {
        return lastVariableName.get();
    }

    private String getNextVariableName() {
        return String.format("%s_%d", VAR_BASE, variableCounter.getAndIncrement());
    }

    private BlockStmt addStatement(Expression toAdd, String variableName) {
        toPopulate.addStatement(toAdd);
        lastVariableName.set(variableName);
        LOGGER.debug(toPopulate.toString());
        return toPopulate;
    }

}
