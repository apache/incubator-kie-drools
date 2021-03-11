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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.kie.dmn.feel.lang.types.GenFnType;
import org.kie.dmn.feel.lang.types.GenListType;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.util.EvalHelper;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.kie.dmn.feel.codegen.feel11.Constants.BigDecimalT;
import static org.kie.dmn.feel.codegen.feel11.Constants.BuiltInTypeT;

public class Expressions {

    public static final ClassOrInterfaceType NamedParamterT = parseClassOrInterfaceType(NamedParameter.class.getCanonicalName());
    public static final ClassOrInterfaceType FormalParamterT = parseClassOrInterfaceType(BaseFEELFunction.Param.class.getCanonicalName());
    public static final ClassOrInterfaceType GenListTypeT = parseClassOrInterfaceType(GenListType.class.getCanonicalName());
    public static final ClassOrInterfaceType MapBackedTypeT = parseClassOrInterfaceType(MapBackedType.class.getCanonicalName());
    public static final ClassOrInterfaceType GenFnTypeT = parseClassOrInterfaceType(GenFnType.class.getCanonicalName());
    private static final Expression DASH_UNARY_TEST = parseExpression(org.kie.dmn.feel.lang.ast.DashNode.DashUnaryTest.class.getCanonicalName() + ".INSTANCE");

    public static class NamedLambda {

        private final NameExpr name;

        private final LambdaExpr expr;
        private final FieldDeclaration field;

        private NamedLambda(NameExpr name, LambdaExpr expr, FieldDeclaration field) {
            this.name = name;
            this.expr = expr;
            this.field = field;
        }

        public NameExpr name() {
            return name;
        }

        public LambdaExpr expr() {
            return expr;
        }

        public FieldDeclaration field() {
            return field;
        }
    }

    private static final Expression QUANTIFIER_SOME = parseExpression("org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode.Quantifier.SOME");
    private static final Expression QUANTIFIER_EVERY = parseExpression("org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode.Quantifier.EVERY");

    public static final String LEFT = "left";
    public static final NameExpr LEFT_EXPR = new NameExpr(LEFT);
    public static final UnknownType UNKNOWN_TYPE = new UnknownType();
    public static final NameExpr STDLIB = new NameExpr(CompiledFEELSupport.class.getSimpleName());

    public static Expression dash() {
        return DASH_UNARY_TEST;
    }

    public static Expression negate(Expression expression) {
        EnclosedExpr e = castTo(BigDecimalT, expression);
        return new MethodCallExpr(e, "negate");
    }

    public static MethodCallExpr binary(
            InfixOpNode.InfixOperator operator,
            Expression l,
            Expression r) {
        switch (operator) {
            case ADD:
                return arithmetic("add", l, r);
            case SUB:
                return arithmetic("sub", l, r);
            case MULT:
                return arithmetic("mult", l, r);
            case DIV:
                return arithmetic("div", l, r);
            case POW:
                return arithmetic("pow", l, r);

            case LTE:
                return comparison("lte", l, r);
            case LT:
                return comparison("lt", l, r);
            case GT:
                return comparison("gt", l, r);
            case GTE:
                return comparison("gte", l, r);
            case EQ:
                return equality("eq", l, r);
            case NE:
                return equality("ne", l, r);
            case AND:
                return booleans("and", l, r);
            case OR:
                return booleans("or", l, r);
            default:
                throw new UnsupportedOperationException(operator.toString());
        }
    }

    private static MethodCallExpr arithmetic(String op, Expression left, Expression right) {
        return new MethodCallExpr(null, op, new NodeList<>(left, right));
    }

    private static MethodCallExpr equality(String op, Expression left, Expression right) {
        return new MethodCallExpr(null, op, new NodeList<>(left, right));
    }

    private static MethodCallExpr comparison(String op, Expression left, Expression right) {
        return new MethodCallExpr(null, op, new NodeList<>(left, right));
    }

    private static MethodCallExpr booleans(String op, Expression left, Expression right) {
        Expression l = coerceToBoolean(left);
        Expression r = supplierLambda(coerceToBoolean(right));

        return new MethodCallExpr(null, op, new NodeList<>(l, r));
    }

    public static Expression unary(
            UnaryTestNode.UnaryOperator operator,
            Expression right) {
        switch (operator) {
            case LTE:
                return unaryComparison("lte", right);
            case LT:
                return unaryComparison("lt", right);
            case GT:
                return unaryComparison("gt", right);
            case GTE:
                return unaryComparison("gte", right);
            case EQ:
                return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "gracefulEq", new NodeList<>(FeelCtx.FEELCTX, right, LEFT_EXPR));
            case NE:
                return unaryComparison("ne", right);
            case IN:
                // only used in decision tables: refactor? how?
                return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "includes", new NodeList<>(FeelCtx.FEELCTX, right, LEFT_EXPR));
            case NOT:
                return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "notExists", new NodeList<>(FeelCtx.FEELCTX, right, LEFT_EXPR));
            case TEST:
                return coerceToBoolean(right);
            default:
                throw new UnsupportedOperationException(operator.toString());
        }
    }

    public static MethodCallExpr unaryComparison(String operator, Expression right) {
        return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), operator, new NodeList<>(LEFT_EXPR, right));
    }

    public static MethodCallExpr lt(Expression left, Expression right) {
        return new MethodCallExpr(null, "lt")
                .addArgument(left)
                .addArgument(right);
    }

    public static MethodCallExpr gt(Expression left, Expression right) {
        return new MethodCallExpr(null, "gt")
                .addArgument(left)
                .addArgument(right);
    }

    public static MethodCallExpr between(Expression value, Expression start, Expression end) {
        return new MethodCallExpr(null, "between")
                .addArgument(FeelCtx.FEELCTX)
                .addArgument(value)
                .addArgument(start)
                .addArgument(end);
    }

    public static EnclosedExpr castTo(Type type, Expression expr) {
        return new EnclosedExpr(new CastExpr(type, new EnclosedExpr(expr)));
    }

    public static MethodCallExpr reflectiveCastTo(Type type, Expression expr) {
        return new MethodCallExpr(new ClassExpr(type), "cast")
                .addArgument(new EnclosedExpr(expr));
    }

    public static Expression quantifier(
            QuantifiedExpressionNode.Quantifier quantifier,
            Expression condition,
            List<Expression> iterationContexts) {

        // quant({SOME,EVERY}, FEELCTX)
        MethodCallExpr quant =
                new MethodCallExpr(Expressions.STDLIB, "quant")
                        .addArgument(quantifier == QuantifiedExpressionNode.Quantifier.SOME ?
                                             QUANTIFIER_SOME :
                                             QUANTIFIER_EVERY)
                        .addArgument(FeelCtx.FEELCTX);

        // .with(expr)
        // .with(expr)
        Expression chainedCalls = iterationContexts.stream()
                .reduce(quant, (l, r) -> r.asMethodCallExpr().setScope(l));

        return new MethodCallExpr(chainedCalls, "satisfies")
                .addArgument(condition);
    }

    public static MethodCallExpr ffor(
            List<Expression> iterationContexts,
            Expression returnExpr) {
        MethodCallExpr ffor =
                new MethodCallExpr(Expressions.STDLIB, "ffor")
                        .addArgument(FeelCtx.FEELCTX);

        // .with(expr)
        // .with(expr)
        Expression chainedCalls = iterationContexts.stream()
                .reduce(ffor, (l, r) -> r.asMethodCallExpr().setScope(l));

        return new MethodCallExpr(chainedCalls, "rreturn")
                .addArgument(returnExpr);
    }

    public static NameExpr compiledFeelSemanticMappingsFQN() {
        return new NameExpr("org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings");
    }

    public static MethodCallExpr list(Expression... exprs) {
        return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "list", NodeList.nodeList(exprs));
    }

    public static MethodCallExpr range(RangeNode.IntervalBoundary lowBoundary,
                                       Expression lowEndPoint,
                                       Expression highEndPoint,
                                       RangeNode.IntervalBoundary highBoundary) {

        return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "range")
                .addArgument(FeelCtx.FEELCTX)
                .addArgument(Constants.rangeBoundary(lowBoundary))
                .addArgument(lowEndPoint)
                .addArgument(highEndPoint)
                .addArgument(Constants.rangeBoundary(highBoundary));
    }

    public static MethodCallExpr includes(Expression range, Expression target) {
        return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "includes")
                .addArgument(FeelCtx.FEELCTX)
                .addArgument(range)
                .addArgument(target);
    }

    public static MethodCallExpr exists(Expression tests, Expression target) {
        return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "exists")
                .addArgument(FeelCtx.FEELCTX)
                .addArgument(tests)
                .addArgument(target);
    }

    public static MethodCallExpr notExists(Expression expr) {
        return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "notExists")
                .addArgument(FeelCtx.FEELCTX)
                .addArgument(expr)
                .addArgument(LEFT_EXPR);
    }

    public static NamedLambda namedLambda(Expression expr, String text) {
        LambdaExpr lambda = Expressions.lambda(expr);
        String name = Constants.functionName(text);
        FieldDeclaration field = Constants.function(name, lambda);
        return new NamedLambda(new NameExpr(name), lambda, field);
    }

    public static LambdaExpr lambda(Expression expr) {
        return new LambdaExpr(
                new NodeList<>(
                        new Parameter(UNKNOWN_TYPE, FeelCtx.FEELCTX_N)),
                new ExpressionStmt(expr),
                true);
    }

    public static LambdaExpr supplierLambda(Expression expr) {
        return new LambdaExpr(new NodeList<>(),
                              new ExpressionStmt(expr),
                              true);
    }

    public static NamedLambda namedUnaryLambda(Expression expr, String text) {
        LambdaExpr lambda = Expressions.unaryLambda(expr);
        String name = Constants.unaryTestName(text);
        FieldDeclaration field = Constants.unaryTest(name, lambda);
        return new NamedLambda(new NameExpr(name), lambda, field);
    }


    public static LambdaExpr unaryLambda(Expression expr) {
        return new LambdaExpr(
                new NodeList<>(
                        new Parameter(UNKNOWN_TYPE, FeelCtx.FEELCTX_N),
                        new Parameter(UNKNOWN_TYPE, "left")),
                new ExpressionStmt(expr),
                true);
    }

    public static ObjectCreationExpr namedParameter(Expression name, Expression value) {
        return new ObjectCreationExpr(null, NamedParamterT, new NodeList<>(name, value));
    }

    public static ObjectCreationExpr formalParameter(Expression name, Expression type) {
        return new ObjectCreationExpr(null, FormalParamterT, new NodeList<>(name, type));
    }

    public static MethodCallExpr invoke(Expression functionName, Expression params) {
        return new MethodCallExpr(STDLIB, "invoke")
                .addArgument(FeelCtx.FEELCTX)
                .addArgument(functionName)
                .addArgument(params);
    }

    public static MethodCallExpr filter(Expression expr, Expression filter) {
        return new MethodCallExpr(new MethodCallExpr(STDLIB, "filter")
                                          .addArgument(FeelCtx.FEELCTX)
                                          .addArgument(expr),
                                  "with")
                .addArgument(filter);
    }

    public static MethodCallExpr path(Expression expr, Expression filter) {
        return new MethodCallExpr(new MethodCallExpr(STDLIB, "path")
                                          .addArgument(FeelCtx.FEELCTX)
                                          .addArgument(expr),
                                  "with")
                .addArgument(filter);
    }

    public static MethodCallExpr path(Expression expr, List<Expression> filters) {
        MethodCallExpr methodCallExpr = new MethodCallExpr(new MethodCallExpr(STDLIB, "path")
                                                                   .addArgument(FeelCtx.FEELCTX)
                                                                   .addArgument(expr),
                                                           "with");
        filters.forEach(methodCallExpr::addArgument);
        return methodCallExpr;
    }

    public static MethodCallExpr isInstanceOf(Expression expr, Expression type) {
        return new MethodCallExpr(type, "isInstanceOf")
                .addArgument(expr);
    }

    public static MethodCallExpr nativeInstanceOf(Type type, Expression condition) {
        return new MethodCallExpr(
                new ClassExpr(type),
                "isInstance")
                .addArgument(new EnclosedExpr(condition));
    }

    public static MethodCallExpr determineTypeFromName(String typeAsText) {
        return new MethodCallExpr(BuiltInTypeT, "determineTypeFromName")
                .addArgument(new StringLiteralExpr(typeAsText));
    }

    public static ObjectCreationExpr genListType(Expression gen) {
        return new ObjectCreationExpr(null, GenListTypeT, new NodeList<>(gen));
    }

    public static Expression genContextType(Map<String, Expression> fields) {
        final ClassOrInterfaceType sie = parseClassOrInterfaceType(java.util.AbstractMap.SimpleImmutableEntry.class.getCanonicalName());
        sie.setTypeArguments(parseClassOrInterfaceType(String.class.getCanonicalName()),
                             parseClassOrInterfaceType(org.kie.dmn.feel.lang.Type.class.getCanonicalName()));
        List<Expression> entryParams = fields.entrySet().stream().map(e -> new ObjectCreationExpr(null,
                                                                                                  sie,
                                                                                                  new NodeList<>(stringLiteral(e.getKey()),
                                                                                                                 e.getValue())))
                                             .collect(Collectors.toList());
        MethodCallExpr mOf = new MethodCallExpr(new NameExpr(java.util.stream.Stream.class.getCanonicalName()), "of");
        entryParams.forEach(mOf::addArgument);
        MethodCallExpr mCollect = new MethodCallExpr(mOf, "collect");
        mCollect.addArgument(new MethodCallExpr(new NameExpr(java.util.stream.Collectors.class.getCanonicalName()),
                                                "toMap").addArgument(new MethodReferenceExpr(new NameExpr(java.util.Map.Entry.class.getCanonicalName()), new NodeList<>(), "getKey"))
                                                        .addArgument(new MethodReferenceExpr(new NameExpr(java.util.Map.Entry.class.getCanonicalName()), new NodeList<>(), "getValue")));
        return new ObjectCreationExpr(null, MapBackedTypeT, new NodeList<>(stringLiteral("[anonymous]"), mCollect));
    }

    public static ObjectCreationExpr genFnType(List<Expression> args, Expression ret) {
        return new ObjectCreationExpr(null,
                                      GenFnTypeT,
                                      new NodeList<>(new MethodCallExpr(new NameExpr(java.util.Arrays.class.getCanonicalName()),
                                                                        "asList",
                                                                        new NodeList<>(args)),
                                                     ret));
    }

    public static Expression contains(Expression expr, Expression value) {
        return new MethodCallExpr(expr, "contains")
                .addArgument(value);
    }

    public static StringLiteralExpr stringLiteral(String text) {
        if (text.startsWith("\"") && text.endsWith("\"")) {
            String actualStringContent = text.substring(1, text.length() - 1); // remove start/end " from the FEEL text expression.
            String unescaped = EvalHelper.unescapeString(actualStringContent); // unescapes String, FEEL-style
            return new StringLiteralExpr().setString(unescaped); // setString escapes the contents Java-style
        } else {
            return new StringLiteralExpr().setString(text);
        }
    }

    public static Expression coerceToBoolean(Expression expression) {
        return new MethodCallExpr(compiledFeelSemanticMappingsFQN(), "coerceToBoolean")
                .addArgument(FeelCtx.FEELCTX)
                .addArgument(expression);
    }

    public static MethodCallExpr coerceNumber(Expression exprCursor) {
        MethodCallExpr coerceNumberMethodCallExpr = new MethodCallExpr(new NameExpr(CompiledFEELSupport.class.getSimpleName()), "coerceNumber");
        coerceNumberMethodCallExpr.addArgument(exprCursor);
        return coerceNumberMethodCallExpr;
    }

    public static ObjectCreationExpr newIllegalState() {
        return new ObjectCreationExpr(null,
                                      parseClassOrInterfaceType(IllegalStateException.class.getCanonicalName()),
                                      new NodeList<>());
    }
}


