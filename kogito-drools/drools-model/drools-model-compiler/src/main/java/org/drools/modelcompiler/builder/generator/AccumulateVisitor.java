package org.drools.modelcompiler.builder.generator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.rule.builder.util.AccumulateUtil;
import org.drools.drlx.DrlxParser;
import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.kie.api.runtime.rule.AccumulateFunction;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;

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

        if (expr instanceof MethodCallExpr) {

            final DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode = DrlxParseUtil.removeRootNode(expr);

            final String rootNodeName = getRootNodeName(methodCallWithoutRootNode);

            final TypedExpression typedExpression = parseMethodCallType(context, rootNodeName, methodCallWithoutRootNode.withoutRootNode);
            final Class<?> methodCallExprType = typedExpression.getType();

            final String accumulateFunctionName = AccumulateUtil.getFunctionName(() -> methodCallExprType, function.getFunction());
            final Optional<AccumulateFunction> bundledAccumulateFunction = Optional.ofNullable(packageModel.getConfiguration().getAccumulateFunction(accumulateFunctionName));
            final Optional<AccumulateFunction> importedAccumulateFunction = Optional.ofNullable(packageModel.getAccumulateFunctions().get(accumulateFunctionName));

            final AccumulateFunction accumulateFunction = bundledAccumulateFunction
                    .map(Optional::of)
                    .orElse(importedAccumulateFunction)
                    .orElseThrow(RuntimeException::new);

            final Class accumulateFunctionResultType = accumulateFunction.getResultType();
            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));

            // Every expression in an accumulate function gets transformed in a bind expression with a generated id
            // Then the accumulate function will have that binding expression as a source
            final String bindExpressionVariable = context.getExprId(accumulateFunctionResultType, typedExpression.toString());
            context.addExpression(generateBindExpression(rootNodeName, typedExpression, accumulateFunctionResultType, bindExpressionVariable));
            context.addDeclaration(new DeclarationSpec(bindExpressionVariable, methodCallExprType));
            functionDSL.addArgument(new NameExpr(toVar(bindExpressionVariable)));

            context.addDeclaration(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        } else if (expr instanceof NameExpr) {
            final Class<?> declarationClass = context
                    .getDeclarationById(expr.toString())
                    .orElseThrow(RuntimeException::new)
                    .declarationClass;
            final String accumulateFunctionName = AccumulateUtil.getFunctionName(() -> declarationClass, function.getFunction());
            final AccumulateFunction accumulateFunction = packageModel.getConfiguration().getAccumulateFunction(accumulateFunctionName);
            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
            functionDSL.addArgument(new NameExpr(toVar(((NameExpr) expr).getName().asString())));

            final Class accumulateFunctionResultType = accumulateFunction.getResultType();
            context.addDeclaration(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        }

        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, "as");
        asDSL.addArgument(new NameExpr(toVar(bindingId)));
        accumulateDSL.addArgument(asDSL);

        context.popExprPointer();
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

    private MethodCallExpr generateBindExpression(String variableName, TypedExpression typedExpression, Class<?> type, String newBindVariable) {
        Expression withThis = DrlxParseUtil.prepend(DrlxParseUtil._THIS_EXPR, typedExpression.getExpression());
        ModelGenerator.DrlxParseResult result = new ModelGenerator.DrlxParseResult(type, "", variableName, withThis, type)
                .setLeft(typedExpression)
                .setExprBinding(newBindVariable);
        return ModelGenerator.buildBinding(result);
    }

    private TypedExpression parseMethodCallType(RuleContext context, String variableName, Expression methodCallWithoutRoot) {
        final Class clazz = context.getDeclarationById(variableName)
                .map(DeclarationSpec::getDeclarationClass)
                .orElseThrow(RuntimeException::new);

        return DrlxParseUtil.toMethodCallWithClassCheck(methodCallWithoutRoot, clazz, context.getPkg().getTypeResolver());
    }
}
