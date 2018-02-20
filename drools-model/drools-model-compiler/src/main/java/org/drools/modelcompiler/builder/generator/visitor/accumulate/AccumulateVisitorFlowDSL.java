package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.kie.api.runtime.rule.AccumulateFunction;

import static java.util.stream.Collectors.toList;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder.BIND_CALL;

public class AccumulateVisitorFlowDSL extends AccumulateVisitor {

    public AccumulateVisitorFlowDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
    }

    public void visit(AccumulateDescr descr, PatternDescr basePattern) {
        final MethodCallExpr accumulateDSL = new MethodCallExpr(null, "accumulate");
        context.addExpression(accumulateDSL);
        final MethodCallExpr accumulateExprs = new MethodCallExpr(null, "and");
        accumulateDSL.addArgument( accumulateExprs );

        context.pushExprPointer(accumulateExprs::addArgument);

        BaseDescr input = descr.getInputPattern() == null ? descr.getInput() : descr.getInputPattern();
        boolean inputPatternHasConstraints = (input instanceof PatternDescr) && (!((PatternDescr)input).getConstraint().getDescrs().isEmpty());
        input.accept(modelGeneratorVisitor);

        if (accumulateExprs.getArguments().isEmpty()) {
            accumulateDSL.remove( accumulateExprs );
        } else if (accumulateExprs.getArguments().size() == 1) {
            accumulateDSL.setArgument( 0, accumulateExprs.getArguments().get(0) );
        }

        if (!descr.getFunctions().isEmpty()) {
            for (AccumulateDescr.AccumulateFunctionCallDescr function : descr.getFunctions()) {
                visit(context, function, accumulateDSL, basePattern, inputPatternHasConstraints);
            }
        } else if (descr.getFunctions().isEmpty() && descr.getInitCode() != null) {
            // LEGACY: Accumulate with inline custom code
            if (input instanceof PatternDescr) {
                visitAccInlineCustomCode(context, descr, accumulateDSL, basePattern, (PatternDescr) input);    
            } else {
                throw new UnsupportedOperationException("I was expecting input to be of type PatternDescr. "+input);
            }
        } else {
            throw new UnsupportedOperationException("Unknown type of Accumulate.");
        }
        removeBindExpressionAndAddThemAsBaseExpr(accumulateDSL);
    }

    /*
        Since accumulate are always relative to the Pattern, it may happen that the declaration inside the accumulate
        was already set in the relative Pattern.
        Here though the type is more precise as it checks the result type Accumulate Function, so we use
        addDeclarationReplacing instead of addDeclaration to overwrite the previous declaration.
     */
    private void visit(RuleContext context, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr accumulateDSL, PatternDescr basePattern, boolean inputPatternHasConstraints) {

        context.pushExprPointer(accumulateDSL::addArgument);

        final MethodCallExpr functionDSL = new MethodCallExpr(null, "accFunction");

        final String expression = function.getParams()[0];
        final Expression expr = DrlxParseUtil.parseExpression(expression).getExpr();
        final String bindingId = Optional.ofNullable(function.getBind()).orElse(basePattern.getIdentifier());

        if(expr instanceof BinaryExpr) {

            final DrlxParseResult parseResult = new ConstraintParser(context, packageModel).drlxParse(Object.class, bindingId, expression);

            parseResult.accept(drlxParseResult -> {
                final AccumulateFunction accumulateFunction = getAccumulateFunction(function, drlxParseResult.getExprType());

                final String bindExpressionVariable = context.getExprId(accumulateFunction.getResultType(), drlxParseResult.getLeft().toString());

                drlxParseResult.setExprBinding(bindExpressionVariable);

                context.addDeclarationReplacing(new DeclarationSpec(drlxParseResult.getPatternBinding(), drlxParseResult.getExprType()));

                functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
                context.addExpression(buildBinding(bindExpressionVariable, drlxParseResult.getUsedDeclarations(), drlxParseResult.getExpr()));
                context.addDeclarationReplacing(new DeclarationSpec(bindExpressionVariable, drlxParseResult.getExprType()));
                functionDSL.addArgument(new NameExpr(toVar(bindExpressionVariable)));
            });


        } else if (expr instanceof MethodCallExpr) {

            final DrlxParseUtil.RemoveRootNodeResult methodCallWithoutRootNode = DrlxParseUtil.removeRootNode(expr);

            final String rootNodeName = getRootNodeName(methodCallWithoutRootNode);

            final TypedExpression typedExpression = parseMethodCallType(context, rootNodeName, methodCallWithoutRootNode.getWithoutRootNode());
            final Class<?> methodCallExprType = typedExpression.getType();

            final AccumulateFunction accumulateFunction = getAccumulateFunction(function, methodCallExprType);

            final Class accumulateFunctionResultType = accumulateFunction.getResultType();
            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));

            // Every expression in an accumulate function gets transformed in a bind expression with a generated id
            // Then the accumulate function will have that binding expression as a source
            final String bindExpressionVariable = context.getExprId(accumulateFunctionResultType, typedExpression.toString());
            Expression withThis = DrlxParseUtil.prepend(DrlxParseUtil._THIS_EXPR, typedExpression.getExpression());
            DrlxParseSuccess result = new DrlxParseSuccess(accumulateFunctionResultType, "", rootNodeName, withThis, accumulateFunctionResultType)
                    .setLeft(typedExpression)
                    .setExprBinding(bindExpressionVariable);
            context.addExpression(new FlowExpressionBuilder(context).buildBinding(result));
            context.addDeclarationReplacing(new DeclarationSpec(bindExpressionVariable, methodCallExprType));
            functionDSL.addArgument(new NameExpr(toVar(bindExpressionVariable)));

            context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));
        } else if (expr instanceof NameExpr) {
            final Class<?> declarationClass = context
                    .getDeclarationById(expr.toString())
                    .orElseThrow(RuntimeException::new)
                    .getDeclarationClass();

            final String nameExpr = ((NameExpr) expr).getName().asString();
            final AccumulateFunction accumulateFunction = getAccumulateFunction(function, declarationClass);
            functionDSL.addArgument(new ClassExpr(toType(accumulateFunction.getClass())));
            functionDSL.addArgument(new NameExpr(toVar(nameExpr)));

            Class accumulateFunctionResultType = accumulateFunction.getResultType();
            if ( accumulateFunctionResultType == Comparable.class && (Comparable.class.isAssignableFrom( declarationClass ) || declarationClass.isPrimitive()) ) {
                accumulateFunctionResultType = declarationClass;
            }
            context.addDeclarationReplacing(new DeclarationSpec(bindingId, accumulateFunctionResultType));

        } else {
            throw new UnsupportedOperationException("Unsupported expression " + expr);
        }

        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, "as");
        asDSL.addArgument(new NameExpr(toVar(bindingId)));
        accumulateDSL.addArgument(asDSL);

        context.popExprPointer();
    }

    private MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        bindDSL.addArgument(toVar(bindingName));
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        usedDeclaration.stream().map(d -> new NameExpr(toVar(d))).forEach(bindAsDSL::addArgument);
        bindAsDSL.addArgument( buildConstraintExpression(expression, usedDeclaration) );
        return bindAsDSL;
    }

    private void removeBindExpressionAndAddThemAsBaseExpr(MethodCallExpr accumulateDSL) {
        // Remove eventual binding expression created in pattern
        // Re-add them as base expressions
        final List<Node> bindExprs = accumulateDSL
                .getChildNodes()
                .stream()
                .filter(a -> a.toString().startsWith("bind"))
                .collect( toList());

        for (Node bindExpr : bindExprs) {
            accumulateDSL.remove(bindExpr);
        }

        context.popExprPointer();

        for (Node bindExpr : bindExprs) {
            context.getExpressions().add(0, (MethodCallExpr) bindExpr);
        }
    }

}