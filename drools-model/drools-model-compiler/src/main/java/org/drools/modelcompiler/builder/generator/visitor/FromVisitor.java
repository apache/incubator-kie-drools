package org.drools.modelcompiler.builder.generator.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.printer.PrintUtil;
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

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static java.util.Optional.of;
import static org.drools.core.rule.Pattern.isCompatibleWithFromReturnType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findViaScopeWithPredicate;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.sanitizeDrlNameExpr;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.FROM_CALL;

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

            boolean isEnumeratedList = expression.startsWith( "[" ) && expression.endsWith( "]" );
            return isEnumeratedList ?
                    createEnumeratedFrom( expression.substring( 1, expression.length()-1 ) ) :
                    createSingleFrom( expression );
        } else {
            return Optional.empty();
        }
    }

    private Optional<Expression> createSingleFrom( String expression ) {
        final Expression parsedExpression = DrlxParseUtil.parseExpression(expression).getExpr();

        if (parsedExpression instanceof FieldAccessExpr || parsedExpression instanceof NameExpr || parsedExpression instanceof DrlNameExpr) {
            return fromFieldOrName(expression);
        }

        if (parsedExpression instanceof MethodCallExpr ) {
            MethodCallExpr sanitized = sanitizeDrlNameExpr((MethodCallExpr) parsedExpression);
            return fromMethodExpr(expression, sanitized);
        }

        if (parsedExpression instanceof ObjectCreationExpr ) {
            return fromConstructorExpr(expression, (ObjectCreationExpr) parsedExpression);
        }

        if (parsedExpression instanceof LiteralExpr ) {
            MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
            fromCall.addArgument( parsedExpression );
            return of(fromCall);
        }

        return Optional.empty();
    }

    private Optional<Expression> createEnumeratedFrom( String expressions ) {
        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        MethodCallExpr asListCall = new MethodCallExpr(null, "java.util.Arrays.asList");
        Collection<String> usedDeclarations = new ArrayList<>();

        for (String expr : expressions.split( "," )) {
            Optional<DeclarationSpec> optContainsBinding = context.getDeclarationById( expr );
            if (optContainsBinding.isPresent()) {
                String bindingId = optContainsBinding.get().getBindingId();
                fromCall.addArgument( context.getVarExpr(bindingId));
                usedDeclarations.add( expr );
            }
            asListCall.addArgument(expr);
        }

        fromCall.addArgument( generateLambdaWithoutParameters( usedDeclarations, asListCall, true ) );
        return of(fromCall);
    }

    private Optional<Expression> fromMethodExpr(String expression, MethodCallExpr parsedExpression) {
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

        fromCall.addArgument( generateLambdaWithoutParameters( bindingIds, parsedExpression, true ) );
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
        if (contextHasDeclaration(bindingId)) {
            return of( createFromCall(expression, bindingId, optContainsBinding.isPresent()) );
        }
        return of(createUnitDataCall(bindingId));
    }

    private Expression createSupplier(Expression parsedExpression) {
        final LambdaExpr lambdaExpr = new LambdaExpr(NodeList.nodeList(), new ExpressionStmt(parsedExpression), true);

        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        fromCall.addArgument(lambdaExpr);
        return fromCall;
    }

    private Optional<Expression> fromExpressionUsingArguments(String expression, NodeWithArguments<?> methodCallExpr) {
        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        String bindingId = null;

        for (Expression argument : methodCallExpr.getArguments()) {
            final String argumentName = PrintUtil.printConstraint(argument);
            if (contextHasDeclaration(argumentName)) {
                if (bindingId == null) {
                    bindingId = argumentName;
                }
                fromCall.addArgument( context.getVarExpr(argumentName));
            }
        }

        return bindingId != null ?
                of(addLambdaToFromExpression( expression, bindingId, fromCall )) :
                of(addNoArgLambdaToFromExpression( expression, fromCall ));
    }

    private Optional<Expression> fromExpressionViaScope(String expression, Expression methodCallExpr) {
        final Expression sanitizedMethodCallExpr = (Expression) DrlxParseUtil.transformDrlNameExprToNameExpr(methodCallExpr);
        return findViaScopeWithPredicate(sanitizedMethodCallExpr, e -> {
            if (e instanceof NameExpr) {
                return contextHasDeclaration(((NameExpr) e).getName().toString());
            }
            return false;
        })
        .filter( Expression::isNameExpr )
        .map( e -> createFromCall(expression, e.asNameExpr().toString(), true) );
    }

    private boolean contextHasDeclaration(String name) {
        return context.hasDeclaration(name) || packageModel.hasDeclaration(name);
    }

    private Expression createFromCall( String expression, String bindingId, boolean hasBinding ) {
        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        fromCall.addArgument( context.getVarExpr(bindingId));
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
        fromCall.addArgument( generateLambdaWithoutParameters( Collections.emptyList(), DrlxParseUtil.parseExpression( expression ).getExpr(), true ) );
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
                return generateLambdaWithoutParameters( singleResult.getUsedDeclarations(), parsedExpression );
            } );
        }
        return null;
    }

    private Expression createUnitDataCall( String bindingId ) {
        return parseExpression(DrlxParseUtil.toVar(bindingId));
    }
}
