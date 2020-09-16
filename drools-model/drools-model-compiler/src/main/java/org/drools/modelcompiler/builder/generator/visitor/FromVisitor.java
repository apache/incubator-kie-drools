/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.printer.PrintUtil;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.drools.core.rule.Pattern.isCompatibleWithFromReturnType;
import static org.drools.core.util.StringUtils.splitArgumentsList;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findViaScopeWithPredicate;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ENTRY_POINT_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.FROM_CALL;
import static org.kie.internal.ruleunit.RuleUnitUtil.isLegacyRuleUnit;

public class FromVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;
    private final Class<?> patternType;

    public FromVisitor(RuleContext context, PackageModel packageModel, Class<?> patternType) {
        this.context = context;
        this.packageModel = packageModel;
        this.patternType = patternType;
    }

    public Optional<Expression> visit(PatternSourceDescr sourceDescr) {
        if (sourceDescr instanceof FromDescr) {
            String expression = ((FromDescr) sourceDescr).getDataSource().toString();

            return isEnumeratedList( expression ) ?
                    createEnumeratedFrom( expression.substring( 1, expression.length()-1 ) ) :
                    createSingleFrom( expression );
        } else {
            return Optional.empty();
        }
    }

    private boolean isEnumeratedList( String expression ) {
        return expression.startsWith( "[" ) && expression.endsWith( "]" );
    }

    private Optional<Expression> createSingleFrom( String expression ) {
        final Expression parsedExpression = DrlxParseUtil.parseExpression(expression).getExpr();

        if (parsedExpression instanceof FieldAccessExpr || parsedExpression instanceof NameExpr || parsedExpression instanceof DrlNameExpr) {
            return fromFieldOrName(expression);
        }

        if (parsedExpression instanceof ObjectCreationExpr ) {
            return fromConstructorExpr(expression, (ObjectCreationExpr) parsedExpression);
        }

        if (parsedExpression instanceof LiteralExpr ) {
            MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
            fromCall.addArgument( parsedExpression );
            return of(fromCall);
        }

        return fromExpression(expression, parsedExpression);
    }

    private Optional<Expression> createEnumeratedFrom( String expressions ) {
        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        Collection<String> usedDeclarations = new ArrayList<>();
        MethodCallExpr asListCall = createListForLiteralFrom( expressions, fromCall, usedDeclarations );
        fromCall.addArgument( generateLambdaWithoutParameters( usedDeclarations, asListCall, true , Optional.empty()) );
        return of(fromCall);
    }

    private MethodCallExpr createListForLiteralFrom( String expressions, MethodCallExpr fromCall, Collection<String> usedDeclarations ) {
        MethodCallExpr asListCall = new MethodCallExpr(null, "java.util.Arrays.asList");
        for (String expr : splitArgumentsList(expressions)) {
            if (isEnumeratedList( expr )) {
                asListCall.addArgument( createListForLiteralFrom( expr.substring( 1, expr.length()-1 ), fromCall, usedDeclarations ) );
            } else {
                Optional<DeclarationSpec> optContainsBinding = context.getDeclarationById( expr );
                if ( optContainsBinding.isPresent() ) {
                    String bindingId = optContainsBinding.get().getBindingId();
                    fromCall.addArgument( context.getVarExpr( bindingId ) );
                    usedDeclarations.add( expr );
                }
                asListCall.addArgument( expr );
            }
        }
        return asListCall;
    }

    private Optional<Expression> fromExpression(String expression, Expression parsedExpression) {
        return fromExpressionViaScope(expression, parsedExpression).map(Optional::of)
                .orElseGet(() -> fromExpressionUsingArguments(expression, parsedExpression));
    }

    private Optional<Expression> fromConstructorExpr(String expression, ObjectCreationExpr parsedExpression) {
        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        List<String> bindingIds = new ArrayList<>();

        for (Expression argument : parsedExpression.getArguments()) {
            final String argumentName = PrintUtil.printConstraint(argument);
            if (contextHasDeclaration(argumentName)) {
                bindingIds.add(argumentName);
                fromCall.addArgument( context.getVarExpr(argumentName));
            }
        }

        Expression newExpr = generateLambdaWithoutParameters( bindingIds, parsedExpression, true, Optional.empty(), context );
        if (newExpr instanceof LambdaExpr) {
            context.getPackageModel().getLambdaReturnTypes().put((LambdaExpr)newExpr, DrlxParseUtil.getClassFromType(context.getTypeResolver(), parsedExpression.getType()));
        }
        fromCall.addArgument(newExpr);
        return of( fromCall );
    }

    private Optional<Expression> fromFieldOrName(String expression) {
        Optional<String> optContainsBinding = DrlxParseUtil.findBindingIdFromDotExpression(expression);
        final String bindingId = optContainsBinding.orElse(expression);

        final DrlxExpression drlxExpression = DrlxParseUtil.parseExpression(expression);

        final Expression parsedExpression = drlxExpression.getExpr();
        Optional<TypedExpression> staticField = parsedExpression instanceof FieldAccessExpr ?
                ExpressionTyper.tryParseAsConstantField(context.getTypeResolver(), ((FieldAccessExpr) parsedExpression).getScope(), ((FieldAccessExpr) parsedExpression).getNameAsString()) :
                Optional.empty();

        if (staticField.isPresent()) {
            return of( createSupplier(parsedExpression) );
        }
        if ( !isLegacyRuleUnit() && packageModel.hasEntryPoint( bindingId ) ) {
            return of( createEntryPointCall(bindingId) );
        }
        if ( contextHasDeclaration( bindingId ) ) {
            return of( createFromCall(expression, bindingId, optContainsBinding.isPresent(), null) );
        }
        return of(createUnitDataCall(bindingId));
    }

    private Expression createSupplier(Expression parsedExpression) {
        final LambdaExpr lambdaExpr = new LambdaExpr(NodeList.nodeList(), new ExpressionStmt(parsedExpression), true);

        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        fromCall.addArgument(lambdaExpr);
        return fromCall;
    }

    private Optional<Expression> fromExpressionUsingArguments(String expression, Expression methodCallExpr) {
        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        String bindingId = addFromArgument( methodCallExpr, fromCall );

        return bindingId != null ?
                of(addLambdaToFromExpression( expression, bindingId, fromCall )) :
                of(addNoArgLambdaToFromExpression( expression, fromCall ));
    }

    private String addFromArgument( Expression methodCallExpr, MethodCallExpr expr ) {
        Collection<String> args = methodCallExpr
                .findAll(NameExpr.class)
                .stream()
                .map(Object::toString)
                .filter(context::hasDeclaration)
                .distinct()
                .collect(Collectors.toList());

        args.stream()
                // Avoid re-add preexisting arguments
                .filter(a -> expr.findAll(NameExpr.class, fa -> fa.toString().equals(toVar(a))).isEmpty())
                .map(context::getVarExpr)
                .forEach(expr::addArgument);

        return args
                .stream()
                .findFirst()
                .orElse(null);
    }

    private Optional<Expression> fromExpressionViaScope(String expression, Expression expr) {
        final Expression sanitizedMethodCallExpr = (Expression) DrlxParseUtil.transformDrlNameExprToNameExpr(expr);
        return findViaScopeWithPredicate(sanitizedMethodCallExpr, e -> {
            if (e instanceof NameExpr) {
                return contextHasDeclaration(((NameExpr) e).getName().toString());
            }
            return false;
        })
        .filter( Expression::isNameExpr )
        .map( e -> createFromCall(expression, e.asNameExpr().toString(), true, expr) );
    }

    private boolean contextHasDeclaration(String name) {
        return context.hasDeclaration(name) || packageModel.hasDeclaration(name);
    }

    private Expression createEntryPointCall( String bindingId ) {
        MethodCallExpr entryPointCall = new MethodCallExpr(null, ENTRY_POINT_CALL);
        entryPointCall.addArgument( new StringLiteralExpr( bindingId ) );
        return entryPointCall;
    }

    private Expression createFromCall( String expression, String bindingId, boolean hasBinding, Expression expr ) {
        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        fromCall.addArgument( context.getVarExpr(bindingId));
        if (expr != null) {
            addFromArgument( expr, fromCall );
        }
        return hasBinding ? addLambdaToFromExpression( expression, bindingId, fromCall ) : fromCall;
    }

    private Expression addLambdaToFromExpression( String expression, String bindingId, MethodCallExpr fromCall ) {
        Expression exprArg = createArg( expression, bindingId );
        if (exprArg != null) {
            fromCall.addArgument( exprArg );
        }
        return fromCall;
    }

    private Expression addNoArgLambdaToFromExpression( String expression, MethodCallExpr fromCall ) {
        fromCall.addArgument( generateLambdaWithoutParameters( Collections.emptyList(), DrlxParseUtil.parseExpression( expression ).getExpr(), true, Optional.empty()) );
        return fromCall;
    }

    private Expression createArg( String expression, String bindingId ) {
        if (bindingId != null) {
            DeclarationSpec declarationSpec = context.getDeclarationById( bindingId ).orElseThrow( RuntimeException::new );
            Class<?> clazz = declarationSpec.getDeclarationClass();

            DrlxParseResult drlxParseResult = new ConstraintParser( context, packageModel ).drlxParse( clazz, bindingId, expression );

            return drlxParseResult.acceptWithReturnValue( drlxParseSuccess -> {
                SingleDrlxParseSuccess singleResult = ( SingleDrlxParseSuccess ) drlxParseResult;
                TypedExpression left = singleResult.getLeft();
                if ( left != null && !isCompatibleWithFromReturnType( patternType, left.getRawClass() ) ) {
                    context.addCompilationError( new InvalidExpressionErrorResult(
                            "Pattern of type: '" + patternType.getCanonicalName() + "' on rule '" + context.getRuleName() +
                                    "' is not compatible with type " + left.getRawClass().getCanonicalName() + " returned by source" ) );
                }
                Expression parsedExpression = drlxParseSuccess.getExpr();
                Expression newExpr = generateLambdaWithoutParameters( singleResult.getUsedDeclarations(), parsedExpression, singleResult.isSkipThisAsParam(), ofNullable(singleResult.getPatternType()), context );
                if (newExpr instanceof LambdaExpr) {
                    context.getPackageModel().getLambdaReturnTypes().put((LambdaExpr)newExpr, singleResult.getExprType());
                }
                return newExpr;
            } );
        }
        return null;
    }

    private Expression createUnitDataCall( String bindingId ) {
        if (isLegacyRuleUnit()) {
            return parseExpression( DrlxParseUtil.toVar( bindingId ) );
        }
        MethodCallExpr entryPointCall = new MethodCallExpr(null, ENTRY_POINT_CALL);
        return entryPointCall.addArgument( new StringLiteralExpr(bindingId) );
    }
}
