package org.drools.modelcompiler.builder.generator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.drlx.DrlxParser;
import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.type.TypeParameter;
import org.drools.modelcompiler.builder.PackageModel;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;

public class AccumulateVisitor {

    public static final String AVERAGE = "average";
    public static final String COUNT = "count";
    public static final String MIN = "min";
    public static final String MAX = "max";

    final RuleContext context;
    final PackageModel packageModel;

    public AccumulateVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void visit(AccumulateDescr descr) {
        final MethodCallExpr accumulateDSL = new MethodCallExpr(null, "accumulate");
        context.addExpression(accumulateDSL);
        context.pushExprPointer(accumulateDSL::addArgument);
        ModelGenerator.visit(context, packageModel, descr.getInputPattern() == null ? descr.getInput() : descr.getInputPattern());
        for (AccumulateDescr.AccumulateFunctionCallDescr function : descr.getFunctions()) {
            visit(context, function, accumulateDSL);
        }

        // Remove eventual binding expression created in pattern
        // Re-add them as base expressions
        final List<Node> bindExprs = accumulateDSL
                .getChildNodes()
                .stream()
                .filter(a -> a.toString().startsWith("bind"))
                .collect(Collectors.toList());

        for (Node bindExpr : bindExprs) {
            accumulateDSL.remove(bindExpr);
        }

        context.popExprPointer();

        for (Node bindExpr : bindExprs) {
            context.expressions.add(0, (MethodCallExpr) bindExpr);
        }
    }

    private void visit(RuleContext context, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr accumulateDSL) {

        context.pushExprPointer(accumulateDSL::addArgument);

        final MethodCallExpr functionDSL = new MethodCallExpr(null, function.getFunction());

        final String expression = function.getParams()[0];
        final Expression expr = DrlxParser.parseExpression(expression).getExpr();
        final String bindingId = Optional.ofNullable(function.getBind()).orElse(context.getExprId(Number.class, function.getFunction()));
        if (expr instanceof MethodCallExpr) {

            final MethodCallExpr methodCallExpr = (MethodCallExpr) expr;

            final NameExpr scope = (NameExpr) methodCallExpr.getScope().orElseThrow(UnsupportedOperationException::new);
            final String variableName = scope.getName().asString();
            final Class clazz = context.getDeclarationById(variableName)
                    .map(DeclarationSpec::getDeclarationClass)
                    .orElseThrow(RuntimeException::new);


            final Expression e = DrlxParseUtil.removeRootNode(expr);
            final TypedExpression typedExpression = DrlxParseUtil.toMethodCallWithClassCheck(e, clazz, context.getPkg().getTypeResolver());

            NameExpr _this = new NameExpr("_this");
            Expression withThis = DrlxParseUtil.prepend(_this, typedExpression.getExpression());

            Class<?> aggregateFunctionType = getReturnTypeForAggregateFunction(functionDSL.getName().asString(), clazz, methodCallExpr);
            final String newBindVariable = context.getExprId(aggregateFunctionType, typedExpression.toString());
            ModelGenerator.DrlxParseResult result = new ModelGenerator.DrlxParseResult(clazz, "", variableName, withThis, typedExpression.getType())
                    .setLeft(typedExpression)
                    .setExprBinding(newBindVariable);
            final MethodCallExpr bind = ModelGenerator.buildBinding(result);
            context.addExpression(bind);

            final DeclarationSpec declaration = new DeclarationSpec(newBindVariable, typedExpression.getType());
            context.addDeclaration(declaration);
            functionDSL.addArgument(new NameExpr(toVar(newBindVariable)));

            context.addDeclaration(new DeclarationSpec(bindingId, aggregateFunctionType));
        } else if (expr instanceof NameExpr) {
            functionDSL.addArgument(new NameExpr(toVar(((NameExpr) expr).getName().asString())));
            final Optional<DeclarationSpec> declarationById = context.getDeclarationById(expr.toString());
            final Class bindClass = getReturnTypeForAggregateFunction(functionDSL.getName().asString(), declarationById.map(a -> a.declarationClass));
            context.addDeclaration(new DeclarationSpec(bindingId, bindClass));
        }

        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, "as");
        asDSL.addArgument(new NameExpr(toVar(bindingId)));

        accumulateDSL.addArgument(asDSL);

        context.popExprPointer();
    }

    private Class<?> getReturnTypeForAggregateFunction(String functionName, Class<?> clazz, MethodCallExpr field) {
        if (AVERAGE.equals(functionName)) {
            return Double.class;
        } else {
            try {
                return clazz.getMethod(field.getName().asString()).getReturnType();
            } catch (NoSuchMethodException e) {
                throw new UnsupportedOperationException("Aggregate function result type", e);
            }
        }
    }

    private Class<?> getReturnTypeForAggregateFunction(String functionName, Optional<Class> orElse) {
        if (AVERAGE.equals(functionName)) {
            return Double.class;
        } else if (COUNT.equals(functionName)) {
            return Integer.class;
        } else if (MIN.equals(functionName)) {
            return Double.class;
        } else if (MAX.equals(functionName)) {
            return Double.class;
        } else {
            return orElse.orElse(Number.class);
        }
    }
}
