package org.drools.modelcompiler.builder.generator;

import java.util.Optional;

import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.ModelGenerator.DrlxParseResult;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.drlxParse;

public class FromVisitor {

    final RuleContext ruleContext;
    final PackageModel packageModel;

    private final String FROM_CALL = "from";

    public FromVisitor(RuleContext context, PackageModel packageModel) {
        this.ruleContext = context;
        this.packageModel = packageModel;
    }

    public Optional<Expression> visit(PatternSourceDescr sourceDescr) {
        if (sourceDescr instanceof FromDescr) {
            final String expression = ((FromDescr) sourceDescr).getDataSource().toString();
            Optional<String> optContainsBinding = DrlxParseUtil.findBindingIdFromDotExpression(expression);
            final String bindingId = optContainsBinding.orElse(expression);

            final MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
            fromCall.addArgument(new NameExpr(toVar(bindingId)));

            if (optContainsBinding.isPresent()) {
                final DeclarationSpec declarationSpec = ruleContext.getDeclarationById(bindingId).orElseThrow(RuntimeException::new);
                final Class<?> clazz = declarationSpec.declarationClass;

                final DrlxParseResult drlxParseResult = drlxParse(ruleContext, packageModel, clazz, bindingId, expression);

                final Expression parsedExpression = drlxParseResult.expr;
                final Expression exprArg = generateLambdaWithoutParameters(drlxParseResult.usedDeclarations, parsedExpression);

                fromCall.addArgument(exprArg);
            } else {
                fromCall.addArgument(JavaParser.parseExpression("x -> x")); // cannot use Function.identity() here because target type is ?.
                fromCall.setLineComment(" cannot use Function.identity() here because target type is ?.");
            }

            return Optional.of(fromCall);
        } else {
            return Optional.empty();
        }
    }
}
