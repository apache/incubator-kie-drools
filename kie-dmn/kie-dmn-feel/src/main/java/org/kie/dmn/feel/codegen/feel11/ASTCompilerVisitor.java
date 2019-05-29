/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package org.kie.dmn.feel.codegen.feel11;

import java.time.Duration;
import java.time.chrono.ChronoPeriod;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.BetweenNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.FilterExpressionNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.IfExpressionNode;
import org.kie.dmn.feel.lang.ast.InNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InstanceOfNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.ListNode;
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
import org.kie.dmn.feel.lang.ast.TypeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.Visitor;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.EvalHelper;

import static org.kie.dmn.feel.codegen.feel11.DirectCompilerResult.mergeFDs;

public class ASTCompilerVisitor implements Visitor<DirectCompilerResult> {

    private static class ScopeHelper {
        Deque<Map<String, Type>> stack;

        public ScopeHelper() {
            this.stack = new ArrayDeque<>();
            this.stack.push(new HashMap<>());
        }

        public void addType(String name, Type type) {
            stack.peek().put(name,
                             type);
        }

        public void pushScope() {
            stack.push(new HashMap<>());
        }

        public void popScope() {
            stack.pop();
        }

        public Optional<Type> resolveType(String name) {
            return stack.stream()
                    .map(scope -> Optional.ofNullable(scope.get(name)))
                    .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
                    .findFirst();
        }
    }

    ScopeHelper scopeHelper = new ScopeHelper();

    @Override
    public DirectCompilerResult visit(ASTNode n) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public DirectCompilerResult visit(DashNode n) {
        return DirectCompilerResult.of(Expressions.dash(), BuiltInType.UNARY_TEST);
    }

    @Override
    public DirectCompilerResult visit(BooleanNode n) {
        return DirectCompilerResult.of(new BooleanLiteralExpr(n.getValue()), BuiltInType.BOOLEAN);
    }

    @Override
    public DirectCompilerResult visit(NumberNode n) {
        String originalText = n.getText();
        String constantName = Constants.numericName(originalText);
        FieldDeclaration constant = Constants.numeric(constantName, originalText);
        return DirectCompilerResult.of(
                new NameExpr(constantName),
                BuiltInType.NUMBER,
                constant);
    }

    @Override
    public DirectCompilerResult visit(StringNode n) {
        return DirectCompilerResult.of(
                Expressions.stringLiteral(n.getText()), // setString escapes the contents Java-style
                BuiltInType.STRING);
    }

    @Override
    public DirectCompilerResult visit(UnaryTestListNode n) {
        MethodCallExpr expr = Expressions.list();
        HashSet<FieldDeclaration> fds = new HashSet<>();
        for (BaseNode e : n.getElements()) {
            DirectCompilerResult r = e.accept(this);
            fds.addAll(r.getFieldDeclarations());
            expr.addArgument(r.getExpression());
        }

        if (n.isNegated()) {
            Expressions.NamedLambda negated =
                    Expressions.namedUnaryLambda(
                            Expressions.notExists(expr), n.getText());

            fds.add(negated.field());
            return DirectCompilerResult.of(
                    Expressions.list(negated.name()),
                    BuiltInType.LIST, fds);
        } else {
            return DirectCompilerResult.of(
                    expr, BuiltInType.LIST, fds);
        }
    }

    @Override
    public DirectCompilerResult visit(NullNode n) {
        return DirectCompilerResult.of(new NullLiteralExpr(), BuiltInType.UNKNOWN);
    }

    @Override
    public DirectCompilerResult visit(NameDefNode n) {
        StringLiteralExpr expr = Expressions.stringLiteral(EvalHelper.normalizeVariableName(n.getText()));
        return DirectCompilerResult.of(expr, BuiltInType.STRING);
    }

    @Override
    public DirectCompilerResult visit(NameRefNode n) {
        String nameRef = EvalHelper.normalizeVariableName(n.getText());
        Type type = scopeHelper.resolveType(nameRef).orElse(BuiltInType.UNKNOWN);
        return DirectCompilerResult.of(FeelCtx.getValue(nameRef), type);
    }

    @Override
    public DirectCompilerResult visit(QualifiedNameNode n) {
        List<NameRefNode> parts = n.getParts();
        DirectCompilerResult nameRef0 = parts.get(0).accept(this);
        Type typeCursor = nameRef0.resultType;
        Expression currentContext = nameRef0.getExpression();
        for (int i = 1; i < parts.size(); i++) {
            NameRefNode acc = parts.get(i);
            String key = acc.getText();
            if (typeCursor instanceof CompositeType) {
                CompositeType currentContextType = (CompositeType) typeCursor;
                currentContext = Contexts.getKey(currentContext, currentContextType, key);
                typeCursor = currentContextType.getFields().get(key);
            } else {
                //  degraded mode, or accessing fields of DATE etc.
                currentContext = Expressions.path(currentContext, new StringLiteralExpr(key));
                typeCursor = BuiltInType.UNKNOWN;
            }
        }
        // If it was a NameRef expression, the number coercion is directly performed by the EvaluationContext for the simple variable.
        // Otherwise in case of QualifiedName expression, for a structured type like this case, it need to be coerced on the last accessor:
        return DirectCompilerResult.of(
                Expressions.coerceNumber(currentContext),
                typeCursor);
    }

    @Override
    public DirectCompilerResult visit(InfixOpNode n) {
        DirectCompilerResult left = n.getLeft().accept(this);
        DirectCompilerResult right = n.getRight().accept(this);
        MethodCallExpr expr = Expressions.binary(
                n.getOperator(),
                left.getExpression(),
                right.getExpression());
        return DirectCompilerResult.of(expr, BuiltInType.UNKNOWN).withFD(left).withFD(right);
    }

    @Override
    public DirectCompilerResult visit(InstanceOfNode n) {
        DirectCompilerResult expr = n.getExpression().accept(this);
        DirectCompilerResult type = n.getType().accept(this);
        switch (n.getType().getText()) {
            case SimpleType.YEARS_AND_MONTHS_DURATION:
                return DirectCompilerResult.of(Expressions.nativeInstanceOf(StaticJavaParser.parseClassOrInterfaceType(ChronoPeriod.class.getCanonicalName()),
                                                                            expr.getExpression()),
                                               BuiltInType.BOOLEAN,
                                               mergeFDs(expr, type));
            case SimpleType.DAYS_AND_TIME_DURATION:
                return DirectCompilerResult.of(Expressions.nativeInstanceOf(StaticJavaParser.parseClassOrInterfaceType(Duration.class.getCanonicalName()),
                                                                            expr.getExpression()),
                                               BuiltInType.BOOLEAN,
                                               mergeFDs(expr, type));
            default:
                return DirectCompilerResult.of(Expressions.isInstanceOf(expr.getExpression(), type.getExpression()),
                                               BuiltInType.BOOLEAN,
                                               mergeFDs(expr, type));
        }

    }

    @Override
    public DirectCompilerResult visit(TypeNode n) {
        return DirectCompilerResult.of(
                Expressions.determineTypeFromName(n.getText()),
                BuiltInType.UNKNOWN);
    }

    @Override
    public DirectCompilerResult visit(IfExpressionNode n) {
        DirectCompilerResult condition = n.getCondition().accept(this);
        DirectCompilerResult thenExpr = n.getThenExpression().accept(this);
        DirectCompilerResult elseExpr = n.getElseExpression().accept(this);

        return DirectCompilerResult.of(
                new ConditionalExpr(
                        new BinaryExpr(
                                Expressions.nativeInstanceOf(
                                        Constants.BooleanT, condition.getExpression()),
                                Expressions.reflectiveCastTo(
                                        Constants.BooleanT, condition.getExpression()),
                                BinaryExpr.Operator.AND),
                        new EnclosedExpr(thenExpr.getExpression()),
                        new EnclosedExpr(elseExpr.getExpression())),
                thenExpr.resultType // should find common type between then/else
        ).withFD(condition).withFD(thenExpr).withFD(elseExpr);
    }

    @Override
    public DirectCompilerResult visit(ForExpressionNode n) {
        DirectCompilerResult expr = n.getExpression().accept(this);
        HashSet<FieldDeclaration> fds = new HashSet<>();

        Expressions.NamedLambda namedLambda =
                Expressions.namedLambda(
                        expr.getExpression(),
                        n.getExpression().getText());

        fds.add(namedLambda.field());
        fds.addAll(expr.getFieldDeclarations());

        List<Expression> expressions = n.getIterationContexts()
                .stream()
                .map(iter -> iter.accept(this))
                .peek(r -> fds.addAll(r.getFieldDeclarations()))
                .map(DirectCompilerResult::getExpression)
                .collect(Collectors.toList());

        // .satisfies(expr)
        return DirectCompilerResult.of(
                Expressions.ffor(expressions, namedLambda.name()),
                expr.resultType,
                fds);
    }

    @Override
    public DirectCompilerResult visit(BetweenNode n) {
        DirectCompilerResult value = n.getValue().accept(this);
        DirectCompilerResult start = n.getStart().accept(this);
        DirectCompilerResult end = n.getEnd().accept(this);

        return DirectCompilerResult.of(
                Expressions.between(
                        value.getExpression(),
                        start.getExpression(),
                        end.getExpression()),
                BuiltInType.BOOLEAN)
                .withFD(value)
                .withFD(start)
                .withFD(end);
    }

    @Override
    public DirectCompilerResult visit(ContextNode n) {
        if (n.getEntries().isEmpty()) {
            return DirectCompilerResult.of(
                    FeelCtx.emptyContext(), BuiltInType.CONTEXT);
        }

        scopeHelper.pushScope();

        // openContext(feelCtx)
        MapBackedType resultType = new MapBackedType();
        DirectCompilerResult openContext =
                DirectCompilerResult.of(FeelCtx.openContext(), resultType);

        //   .setEntry( k,v )
        //   .setEntry( k,v )
        //   ...
        DirectCompilerResult entries = n.getEntries()
                .stream()
                .map(e -> {
                    DirectCompilerResult r = e.accept(this);
                    scopeHelper.addType(e.getName().getText(), r.resultType);
                    return r;
                })
                .reduce(openContext,
                        (l, r) -> DirectCompilerResult.of(
                                r.getExpression().asMethodCallExpr().setScope(l.getExpression()),
                                r.resultType,
                                DirectCompilerResult.mergeFDs(l, r)));

        scopeHelper.popScope();

        // .closeContext()
        return DirectCompilerResult.of(
                FeelCtx.closeContext(entries),
                resultType,
                entries.getFieldDeclarations());
    }

    @Override
    public DirectCompilerResult visit(ContextEntryNode n) {
        DirectCompilerResult key = n.getName().accept(this);
        DirectCompilerResult value = n.getValue().accept(this);
        if (key.resultType != BuiltInType.STRING) {
            throw new IllegalArgumentException(
                    "a Context Entry Key must be a valid FEEL String type");
        }
        String keyText = key.getExpression().asStringLiteralExpr().getValue();

        // .setEntry(key, value)
        MethodCallExpr setEntryContextCall =
                FeelCtx.setEntry(keyText, value.getExpression());

        return DirectCompilerResult.of(
                setEntryContextCall,
                value.resultType,
                value.getFieldDeclarations());
    }

    @Override
    public DirectCompilerResult visit(FilterExpressionNode n) {
        DirectCompilerResult expr = n.getExpression().accept(this);
        DirectCompilerResult filter = n.getFilter().accept(this);

        Expressions.NamedLambda lambda = Expressions.namedLambda(filter.getExpression(), n.getFilter().getText());
        DirectCompilerResult r = DirectCompilerResult.of(
                Expressions.filter(expr.getExpression(), lambda.name()),
                // here we could still try to infer the result type, but presently use ANY
                BuiltInType.UNKNOWN).withFD(expr).withFD(filter);
        r.addFieldDesclaration(lambda.field());
        return r;
    }

    @Override
    public DirectCompilerResult visit(FunctionDefNode n) {
        MethodCallExpr list = Expressions.list();
        n.getFormalParameters()
                .stream()
                .map(fp -> fp.accept(this))
                .map(DirectCompilerResult::getExpression)
                .forEach(list::addArgument);

        if (n.isExternal()) {
            List<String> paramNames =
                    n.getFormalParameters().stream()
                            .map(BaseNode::getText)
                            .collect(Collectors.toList());

            return Functions.declaration(
                    n, list,
                    Functions.external(paramNames, n.getBody()));
        } else {
            DirectCompilerResult body = n.getBody().accept(this);
            return Functions.declaration(n, list,
                                         body.getExpression()).withFD(body);
        }
    }

    @Override
    public DirectCompilerResult visit(FunctionInvocationNode n) {
        DirectCompilerResult functionName = n.getName().accept(this);
        DirectCompilerResult params = n.getParams().accept(this);
        return DirectCompilerResult.of(
                Expressions.invoke(functionName.getExpression(), params.getExpression()),
                functionName.resultType)
                .withFD(functionName)
                .withFD(params);
    }

    @Override
    public DirectCompilerResult visit(NamedParameterNode n) {
        DirectCompilerResult name = n.getName().accept(this);
        DirectCompilerResult expr = n.getExpression().accept(this);
        return DirectCompilerResult.of(
                Expressions.namedParameter(name.getExpression(), expr.getExpression()),
                BuiltInType.UNKNOWN).withFD(name).withFD(expr);
    }

    @Override
    public DirectCompilerResult visit(InNode n) {
        DirectCompilerResult value = n.getValue().accept(this);
        DirectCompilerResult exprs = n.getExprs().accept(this);

        if (exprs.resultType == BuiltInType.LIST) {
            return DirectCompilerResult.of(
                    Expressions.exists(exprs.getExpression(), value.getExpression()),
                    BuiltInType.BOOLEAN).withFD(value).withFD(exprs);
        } else if (exprs.resultType == BuiltInType.RANGE) {
            return DirectCompilerResult.of(
                    Expressions.includes(exprs.getExpression(), value.getExpression()),
                    BuiltInType.BOOLEAN).withFD(value).withFD(exprs);
        } else {
            // this should be turned into a tree rewrite
            return DirectCompilerResult.of(
                    Expressions.exists(exprs.getExpression(), value.getExpression()),
                    BuiltInType.BOOLEAN).withFD(value).withFD(exprs);
        }
    }

    @Override
    public DirectCompilerResult visit(IterationContextNode n) {
        DirectCompilerResult iterName = n.getName().accept(this);
        DirectCompilerResult iterExpr = n.getExpression().accept(this);

        Expressions.NamedLambda nameLambda =
                Expressions.namedLambda(
                        iterName.getExpression(),
                        n.getName().getText());
        Expressions.NamedLambda exprLambda =
                Expressions.namedLambda(
                        iterExpr.getExpression(),
                        n.getExpression().getText());

        MethodCallExpr with =
                new MethodCallExpr(null, "with")
                        .addArgument(nameLambda.name())
                        .addArgument(exprLambda.name());

        DirectCompilerResult r =
                DirectCompilerResult.of(with, BuiltInType.UNKNOWN);
        r.addFieldDesclaration(nameLambda.field());
        r.addFieldDesclaration(exprLambda.field());
        r.withFD(iterName);
        r.withFD(iterExpr);

        BaseNode rangeEndExpr = n.getRangeEndExpr();
        if (rangeEndExpr != null) {
            DirectCompilerResult rangeEnd = rangeEndExpr.accept(this);
            Expressions.NamedLambda rangeLambda =
                    Expressions.namedLambda(
                            rangeEnd.getExpression(),
                            rangeEndExpr.getText());
            with.addArgument(rangeLambda.name());
            r.addFieldDesclaration(rangeLambda.field());
            r.withFD(rangeEnd);
        }

        return r;
    }

    @Override
    public DirectCompilerResult visit(ListNode n) {
        MethodCallExpr list = Expressions.list();
        DirectCompilerResult result = DirectCompilerResult.of(list, BuiltInType.LIST);

        for (BaseNode e : n.getElements()) {
            DirectCompilerResult r = e.accept(this);
            result.withFD(r.getFieldDeclarations());
            list.addArgument(r.getExpression());
        }

        return result;
    }

    @Override
    public DirectCompilerResult visit(PathExpressionNode n) {
        DirectCompilerResult expr = n.getExpression().accept(this);
        BaseNode nameNode = n.getName();
        if (nameNode instanceof QualifiedNameNode) {
            QualifiedNameNode qualifiedNameNode = (QualifiedNameNode) n.getName();
            List<Expression> exprs =
                    qualifiedNameNode.getParts().stream()
                            .map(name -> new StringLiteralExpr(name.getText()))
                            .collect(Collectors.toList());

            return DirectCompilerResult.of(
                    Expressions.path(expr.getExpression(), exprs),
                    // here we could still try to infer the result type, but presently use ANY
                    BuiltInType.UNKNOWN).withFD(expr);
        } else {
            return DirectCompilerResult.of(
                    Expressions.path(expr.getExpression(), new StringLiteralExpr(nameNode.getText())),
                    // here we could still try to infer the result type, but presently use ANY
                    BuiltInType.UNKNOWN).withFD(expr);
        }
    }

    @Override
    public DirectCompilerResult visit(QuantifiedExpressionNode n) {
        DirectCompilerResult expr = n.getExpression().accept(this);
        HashSet<FieldDeclaration> fds = new HashSet<>();

        Expressions.NamedLambda namedLambda =
                Expressions.namedLambda(
                        expr.getExpression(),
                        n.getExpression().getText());

        fds.add(namedLambda.field());
        fds.addAll(expr.getFieldDeclarations());

        List<Expression> expressions = n.getIterationContexts()
                .stream()
                .map(iter -> iter.accept(this))
                .peek(r -> fds.addAll(r.getFieldDeclarations()))
                .map(DirectCompilerResult::getExpression)
                .collect(Collectors.toList());

        // .satisfies(expr)
        return DirectCompilerResult.of(
                Expressions.quantifier(n.getQuantifier(), namedLambda.name(), expressions),
                expr.resultType,
                fds);
    }

    @Override
    public DirectCompilerResult visit(RangeNode n) {
        DirectCompilerResult start = n.getStart().accept(this);
        DirectCompilerResult end = n.getEnd().accept(this);
        return DirectCompilerResult.of(
                Expressions.range(
                        n.getLowerBound(),
                        start.getExpression(),
                        end.getExpression(),
                        n.getUpperBound()),
                BuiltInType.RANGE,
                DirectCompilerResult.mergeFDs(start, end));
    }

    @Override
    public DirectCompilerResult visit(SignedUnaryNode n) {
        DirectCompilerResult result = n.getExpression().accept(this);
        if (n.getSign() == SignedUnaryNode.Sign.NEGATIVE) {
            return DirectCompilerResult.of(
                    Expressions.negate(result.getExpression()),
                    result.resultType,
                    result.getFieldDeclarations());
        } else {
            return result;
        }
    }

    @Override
    public DirectCompilerResult visit(UnaryTestNode n) {
        DirectCompilerResult value = n.getValue().accept(this);
        Expression expr = Expressions.unary(n.getOperator(), value.getExpression());
        Expressions.NamedLambda namedLambda = Expressions.namedUnaryLambda(expr, n.getText());
        DirectCompilerResult r =
                DirectCompilerResult.of(namedLambda.name(), BuiltInType.UNARY_TEST)
                        .withFD(value);
        r.addFieldDesclaration(namedLambda.field());
        return r;
    }
}
