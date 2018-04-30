package org.drools.modelcompiler.builder.generator.visitor;

import java.util.Optional;

import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.drlx.expr.DrlxExpression;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findViaScopeWithPredicate;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.FROM_CALL;

public class FromVisitor {

    final RuleContext ruleContext;
    final PackageModel packageModel;

    public FromVisitor(RuleContext context, PackageModel packageModel) {
        this.ruleContext = context;
        this.packageModel = packageModel;
    }

    public Optional<Expression> visit(PatternSourceDescr sourceDescr) {
        if (sourceDescr instanceof FromDescr) {
            final String expression = ((FromDescr) sourceDescr).getDataSource().toString();

            final DrlxExpression drlxExpression = DrlxParseUtil.parseExpression(expression);
            final Expression parsedExpression = drlxExpression.getExpr();

            if (parsedExpression instanceof FieldAccessExpr || parsedExpression instanceof NameExpr) {

                Optional<String> optContainsBinding = DrlxParseUtil.findBindingIdFromDotExpression(expression);
                final String bindingId = optContainsBinding.orElse(expression);

                Expression fromCall;
                if (ruleContext.hasDeclaration(bindingId) || packageModel.hasDeclaration(bindingId)) {
                    fromCall = createFromCall(expression, optContainsBinding, bindingId);
                } else {
                    fromCall = createUnitDataCall(optContainsBinding, bindingId);
                }

                return Optional.of(fromCall);
            }

            if (parsedExpression instanceof MethodCallExpr) {
                MethodCallExpr methodCallExpr = (MethodCallExpr) parsedExpression;

                final Optional<Expression> bindingIdViaScope = findViaScopeWithPredicate(methodCallExpr, e -> {
                    if(e instanceof NameExpr) {
                        final String name = ((NameExpr) e).getName().toString();
                        return ruleContext.hasDeclaration(name) || packageModel.hasDeclaration(name);
                    }
                    return false;
                });

                if(bindingIdViaScope.isPresent()) {
                    final String binding = bindingIdViaScope.get().asNameExpr().toString();
                    return Optional.of(createFromCall(expression, Optional.of(binding), binding));
                }


                for(Expression argument : methodCallExpr.getArguments()) {
                    final String argumentName = argument.toString();
                    if (ruleContext.hasDeclaration(argumentName) || packageModel.hasDeclaration(argumentName)) {
                        Expression fromCall = createFromCall(expression, Optional.of(argumentName), argumentName);
                        return Optional.of(fromCall);
                    }
                }

                return Optional.empty();
            }

            return Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    private Expression createFromCall( String expression, Optional<String> optContainsBinding, String bindingId ) {
        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        fromCall.addArgument(new NameExpr(toVar(bindingId)));

        if (optContainsBinding.isPresent()) {
            DeclarationSpec declarationSpec = ruleContext.getDeclarationById(bindingId).orElseThrow(RuntimeException::new);
            Class<?> clazz = declarationSpec.getDeclarationClass();

            DrlxParseResult drlxParseResult = new ConstraintParser(ruleContext, packageModel).drlxParse(clazz, bindingId, expression);

            drlxParseResult.accept(drlxParseSuccess -> {
                Expression parsedExpression = drlxParseSuccess.getExpr();
                Expression exprArg = generateLambdaWithoutParameters(drlxParseSuccess.getUsedDeclarations(), parsedExpression);

                fromCall.addArgument(exprArg);
            });
        }
        return fromCall;
    }

    private Expression createUnitDataCall( Optional<String> optContainsBinding, String bindingId ) {
        return JavaParser.parseExpression(toVar(bindingId));
    }
}
