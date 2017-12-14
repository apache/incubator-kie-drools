package org.drools.modelcompiler.builder.generator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.rule.builder.util.AccumulateUtil;
import org.drools.drlx.DrlxParser;
import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.type.UnknownType;
import org.drools.modelcompiler.builder.PackageModel;
import org.kie.api.runtime.rule.AccumulateFunction;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.BIND_CALL;

public class AccumulateVisitor {

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

        final MethodCallExpr functionDSL = new MethodCallExpr(null, "accFunction");

        final String expression = function.getParams()[0];
        final Expression expr = DrlxParser.parseExpression(expression).getExpr();
        final String bindingId = Optional.ofNullable(function.getBind()).orElse(context.getExprId(Number.class, function.getFunction()));

        if(expr instanceof BinaryExpr) {

            final ModelGenerator.DrlxParseResult drlxParseResult = ModelGenerator.drlxParse(context, packageModel, Object.class, bindingId, expression);

            final AccumulateFunction accumulateFunction = getAccumulateFunction(function, drlxParseResult.exprType);
            System.out.println("drlxParseResult = " + drlxParseResult);

            final String bindExpressionVariable = context.getExprId(accumulateFunction.getResultType(), drlxParseResult.left.toString());

            drlxParseResult.setExprBinding(bindExpressionVariable);

            context.addDeclaration(new DeclarationSpec(drlxParseResult.getPatternBinding(), drlxParseResult.exprType));

            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
            context.addExpression(buildBinding(bindExpressionVariable, drlxParseResult.usedDeclarations, drlxParseResult.expr));
            context.addDeclaration(new DeclarationSpec(bindExpressionVariable, drlxParseResult.exprType));
            functionDSL.addArgument(new NameExpr(toVar(bindExpressionVariable)));

        } else if (expr instanceof MethodCallExpr) {

            final DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode = DrlxParseUtil.removeRootNode(expr);

            final String rootNodeName = getRootNodeName(methodCallWithoutRootNode);

            final TypedExpression typedExpression = parseMethodCallType(context, rootNodeName, methodCallWithoutRootNode.withoutRootNode);
            final Class<?> methodCallExprType = typedExpression.getType();

            final AccumulateFunction accumulateFunction = getAccumulateFunction(function, methodCallExprType);

            final Class accumulateFunctionResultType = accumulateFunction.getResultType();
            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));

            // Every expression in an accumulate function gets transformed in a bind expression with a generated id
            // Then the accumulate function will have that binding expression as a source
            final String bindExpressionVariable = context.getExprId(accumulateFunctionResultType, typedExpression.toString());
            Expression withThis = DrlxParseUtil.prepend(DrlxParseUtil._THIS_EXPR, typedExpression.getExpression());
            ModelGenerator.DrlxParseResult result = new ModelGenerator.DrlxParseResult(accumulateFunctionResultType, "", rootNodeName, withThis, accumulateFunctionResultType)
                    .setLeft(typedExpression)
                    .setExprBinding(bindExpressionVariable);
            context.addExpression(ModelGenerator.buildBinding(result));
            context.addDeclaration(new DeclarationSpec(bindExpressionVariable, methodCallExprType));
            functionDSL.addArgument(new NameExpr(toVar(bindExpressionVariable)));

            context.addDeclaration(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        } else if (expr instanceof NameExpr) {
            final Class<?> declarationClass = context
                    .getDeclarationById(expr.toString())
                    .orElseThrow(RuntimeException::new)
                    .declarationClass;
            final AccumulateFunction accumulateFunction = getAccumulateFunction(function, declarationClass);
            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
            functionDSL.addArgument(new NameExpr(toVar(((NameExpr) expr).getName().asString())));

            final Class accumulateFunctionResultType = accumulateFunction.getResultType();
            context.addDeclaration(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        } else {
            throw new UnsupportedOperationException("Unsupported expression " + expr);
        }

        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, "as");
        asDSL.addArgument(new NameExpr(toVar(bindingId)));
        accumulateDSL.addArgument(asDSL);

        context.popExprPointer();
    }

    private AccumulateFunction getAccumulateFunction(AccumulateDescr.AccumulateFunctionCallDescr function, Class<?> methodCallExprType) {
        final String accumulateFunctionName = AccumulateUtil.getFunctionName(() -> methodCallExprType, function.getFunction());
        final Optional<AccumulateFunction> bundledAccumulateFunction = Optional.ofNullable(packageModel.getConfiguration().getAccumulateFunction(accumulateFunctionName));
        final Optional<AccumulateFunction> importedAccumulateFunction = Optional.ofNullable(packageModel.getAccumulateFunctions().get(accumulateFunctionName));

        return bundledAccumulateFunction
                .map(Optional::of)
                .orElse(importedAccumulateFunction)
                .orElseThrow(RuntimeException::new);
    }

    private String getRootNodeName(DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode) {
        final Expression rootNode = methodCallWithoutRootNode
                .rootNode.orElseThrow(UnsupportedOperationException::new);

        final String rootNodeName;
        if(rootNode instanceof NameExpr) {
            rootNodeName = ((NameExpr)rootNode).getName().asString();
        } else {
            throw new RuntimeException("Root node of expression should be a declaration");
        }
        return rootNodeName;
    }

    private TypedExpression parseMethodCallType(RuleContext context, String variableName, Expression methodCallWithoutRoot) {
        final Class clazz = context.getDeclarationById(variableName)
                .map(DeclarationSpec::getDeclarationClass)
                .orElseThrow(RuntimeException::new);

        return DrlxParseUtil.toMethodCallWithClassCheck(methodCallWithoutRoot, clazz, context.getPkg().getTypeResolver());
    }

    public MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        bindDSL.addArgument(toVar(bindingName));
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        usedDeclaration.stream().map(d -> new NameExpr(toVar(d))).forEach(bindAsDSL::addArgument);
        bindAsDSL.addArgument( buildConstraintExpression(expression, usedDeclaration) );
        return bindAsDSL;
    }

    private Expression buildConstraintExpression(Expression expr, Collection<String> usedDeclarations) {
        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters( true );
        usedDeclarations.stream().map(s -> new Parameter(new UnknownType(), s ) ).forEach(lambdaExpr::addParameter );
        lambdaExpr.setBody( new ExpressionStmt(expr) );
        return lambdaExpr;
    }
}
