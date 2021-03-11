/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.model.Index;
import org.drools.model.functions.PredicateInformation;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.MultipleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.kie.api.io.Resource;

import static java.util.Optional.ofNullable;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isThisExpression;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.INPUT_CALL;
import static org.drools.modelcompiler.builder.generator.PrimitiveTypeConsequenceRewrite.rewriteNode;
import static org.drools.modelcompiler.util.ClassUtil.isAccessibleProperties;
import static org.drools.modelcompiler.util.ClassUtil.isAssignableFrom;
import static org.drools.modelcompiler.util.ClassUtil.toNonPrimitiveType;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

public abstract class AbstractExpressionBuilder {

    protected RuleContext context;

    protected AbstractExpressionBuilder( RuleContext context ) {
        this.context = context;
    }

    public void processExpression(DrlxParseSuccess drlxParseResult) {
        if (drlxParseResult instanceof SingleDrlxParseSuccess) {
            processExpression( (SingleDrlxParseSuccess) drlxParseResult );
        } else if (drlxParseResult instanceof MultipleDrlxParseSuccess) {
            processExpression( (MultipleDrlxParseSuccess) drlxParseResult );
        } else {
            throw new UnsupportedOperationException( "Unknown expression type: " + drlxParseResult.getClass().getName() );
        }
    }

    public abstract void processExpression(SingleDrlxParseSuccess drlxParseResult);

    protected MethodCallExpr createInputExpression(String identifier) {
        MethodCallExpr exprDSL = new MethodCallExpr(null, INPUT_CALL);
        exprDSL.addArgument( context.getVarExpr(identifier) );

        return exprDSL;
    }

    public void processExpression(MultipleDrlxParseSuccess drlxParseResult) {
        if ( drlxParseResult.isValidExpression() ) {
            Expression dslExpr = buildExpressionWithIndexing(drlxParseResult);
            context.addExpression(dslExpr);
        }
    }

    protected Expression buildUnificationExpression(SingleDrlxParseSuccess drlxParseResult) {
        MethodCallExpr exprDSL = buildBinding(drlxParseResult);
        context.addDeclaration(drlxParseResult.getUnificationVariable(), drlxParseResult.getUnificationVariableType(), drlxParseResult.getUnificationName());
        return exprDSL;
    }

    public abstract MethodCallExpr buildExpressionWithIndexing(DrlxParseSuccess drlxParseResult);

    public abstract MethodCallExpr buildBinding(SingleDrlxParseSuccess drlxParseResult);

    protected Expression getConstraintExpression(SingleDrlxParseSuccess drlxParseResult) {
        if (drlxParseResult.getExpr() instanceof EnclosedExpr) {
            return buildConstraintExpression(drlxParseResult, ((EnclosedExpr) drlxParseResult.getExpr()).getInner());
        } else {
            final TypedExpression left = drlxParseResult.getLeft();
            // Can we unify it? Sometimes expression is in the left sometimes in expression
            final Expression e;
            if(left != null) {
                e = findLeftmostExpression(left.getExpression());
            } else {
                e = drlxParseResult.getExpr();
            }
            return buildConstraintExpression(drlxParseResult, drlxParseResult.getUsedDeclarationsOnLeft(), e);
        }
    }

    private Expression findLeftmostExpression(Expression expression) {
        if (expression instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr) expression;
            return findLeftmostExpression(be.getLeft());
        }
        if (expression instanceof CastExpr) {
            CastExpr ce = (CastExpr) expression;
            return findLeftmostExpression(ce.getExpression());
        } else if (expression instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = expression.asMethodCallExpr();
            if(!methodCallExpr.getArguments().isEmpty()) {
                return findLeftmostExpression(methodCallExpr.getArguments().iterator().next());
            } else {
                return expression;
            }
        } else if (expression instanceof FieldAccessExpr) {
            return expression;
        } else {
            context.addCompilationError(new InvalidExpressionErrorResult("Unable to Analyse Expression" + printConstraint(expression)));
            return expression;
        }
    }

    protected Expression buildConstraintExpression(SingleDrlxParseSuccess drlxParseResult, Expression expr ) {
        return buildConstraintExpression(drlxParseResult, drlxParseResult.getUsedDeclarations(), expr );
    }

    protected Expression buildConstraintExpression(SingleDrlxParseSuccess drlxParseResult, Collection<String> usedDeclarations, Expression expr ) {
        return drlxParseResult.isStatic() ? expr :
                generateLambdaWithoutParameters(usedDeclarations,
                                                expr,
                                                drlxParseResult.isSkipThisAsParam(), ofNullable(drlxParseResult.getPatternType()), context);
    }

    boolean shouldCreateIndex(SingleDrlxParseSuccess drlxParseResult ) {
        if ( drlxParseResult.getDecodeConstraintType() == Index.ConstraintType.FORALL_SELF_JOIN ) {
            return true;
        }

        TypedExpression left = drlxParseResult.getLeft();

        if(!shouldIndexConstraintWithRightScopePatternBinding(drlxParseResult)) {
            return false;
        }

        Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();

        return left != null && left.getFieldName() != null &&
                drlxParseResult.getDecodeConstraintType() != null &&
                drlxParseResult.getPatternType() != null &&
                isLeftIndexableExpression( left.getExpression() ) &&
                areIndexableDeclaration( usedDeclarations );
    }

    private boolean isLeftIndexableExpression( Expression expr ) {
        if (expr instanceof MethodCallExpr) {
            if ( !getMethodChainScope(( MethodCallExpr ) expr).map( DrlxParseUtil::isThisExpression ).orElse( false ) ) {
                return false;
            }
        }
        return !isThisExpression( expr );
    }

    private Optional<Expression> getMethodChainScope(MethodCallExpr expr) {
        Optional<Expression> scope = expr.getScope();
        return scope.isPresent() && scope.get() instanceof MethodCallExpr ? getMethodChainScope( (MethodCallExpr) scope.get()) : scope;
    }

    private boolean areIndexableDeclaration( Collection<String> usedDeclarations ) {
        if (usedDeclarations.size() > 4) {
            return false;
        }
        return !usedDeclarations.stream()
                .map( context::getDeclarationById )
                .anyMatch( optDecl -> optDecl.isPresent() && optDecl.get().isGlobal() );
    }

    // See PatternBuilder:1198 (buildConstraintForPattern) Pattern are indexed only when the root of the right part
    // (i.e. $p in address.street == $p.name) is a Pattern binding.
    // See also IndexingTest and ExistentialTest
    protected boolean shouldIndexConstraintWithRightScopePatternBinding(SingleDrlxParseSuccess result) {
        TypedExpression right = result.getRight();

        if (right != null && right.getExpression() != null && right.getExpression() instanceof NodeWithOptionalScope) {
            if (isStringToDateExpression(right.getExpression())) {
                return true;
            }
            NodeWithOptionalScope<?> e = (NodeWithOptionalScope<?>) (right.getExpression());
            return e.getScope()
                    .map(Object::toString)
                    .filter(context::isPatternBinding)
                    .isPresent();
        }

        return true;
    }

    protected boolean isStringToDateExpression(Expression expression) {
        if (expression instanceof MethodCallExpr) {
            String methodName = ((MethodCallExpr) expression).getNameAsString();
            if (methodName.equals(PackageModel.STRING_TO_DATE_METHOD)
                    || methodName.equals(PackageModel.STRING_TO_LOCAL_DATE_METHOD)
                    || methodName.equals(PackageModel.STRING_TO_LOCAL_DATE_TIME_METHOD)) {
                return true;
            }
        }
        return false;
    }

    public static AbstractExpressionBuilder getExpressionBuilder(RuleContext context) {
        return context.isPatternDSL() ? new PatternExpressionBuilder( context ) : new FlowExpressionBuilder( context );
    }

    protected Expression narrowExpressionToType( TypedExpression right, java.lang.reflect.Type leftType ) {
        Expression expression = right.getExpression();

        if (expression instanceof NullLiteralExpr) {
            return expression;
        }

        if (leftType.equals(Double.class)) {
            return new CastExpr( PrimitiveType.doubleType(), expression );
        }

        if (leftType.equals(Long.class)) {
            if (right.getType().equals( Double.class ) || right.getType().equals( double.class )) {
                return new MethodCallExpr( expression, "longValue" );
            } else {
                return new CastExpr( PrimitiveType.longType(), expression );
            }
        }

        if (expression instanceof LiteralExpr) {
            if (expression instanceof BigDecimalLiteralExpr) {
                return toNewExpr(BigDecimal.class, new StringLiteralExpr(((BigDecimalLiteralExpr) expression).asBigDecimal().toString()));
            }
            if (expression instanceof BigIntegerLiteralExpr) {
                return toNewExpr(toRawClass(leftType), new StringLiteralExpr(((BigIntegerLiteralExpr) expression).asBigInteger().toString()));
            }
            if (leftType.equals(BigDecimal.class)) {
                final BigDecimal bigDecimal = new BigDecimal( expression.toString() );
                return toNewExpr(BigDecimal.class, new StringLiteralExpr( bigDecimal.toString() ) );
            }
            if (leftType.equals(BigInteger.class)) {
                final BigInteger bigInteger = new BigDecimal(expression.toString()).toBigInteger();
                return toNewExpr(BigInteger.class, new StringLiteralExpr(bigInteger.toString()));
            }

        }

        if (expression instanceof NameExpr) {
            if (leftType.equals(BigDecimal.class) && !right.getType().equals(BigDecimal.class)) {
                return toNewExpr(BigDecimal.class, expression);
            }
            if (leftType.equals(BigInteger.class) && !right.getType().equals(BigInteger.class)) {
                return toNewExpr(BigInteger.class, expression);
            }
        }

        if ( !isAssignableFrom( leftType, right.getType() ) && isAssignableFrom( right.getType(), leftType ) ) {
            return new CastExpr( toClassOrInterfaceType(toNonPrimitiveType(toRawClass(leftType))), expression );
        }

        return expression;
    }

    private static Expression toNewExpr(Class<?> clazz, Expression initExpression) {
        return new ObjectCreationExpr(null, toClassOrInterfaceType(clazz), NodeList.nodeList(initExpression));
    }

    protected void addIndexedByDeclaration(TypedExpression left,
                                           TypedExpression right,
                                           boolean leftContainsThis,
                                           MethodCallExpr indexedByDSL,
                                           Collection<String> usedDeclarations,
                                           java.lang.reflect.Type leftType,
                                           SingleDrlxParseSuccess drlxParseResult) {
        LambdaExpr indexedByRightOperandExtractor = new LambdaExpr();
        for (String declarationName : usedDeclarations) {
            DeclarationSpec declarationById = context.getDeclarationByIdWithException( declarationName );
            indexedByRightOperandExtractor.addParameter( new Parameter( declarationById.getBoxedType(), declarationName ) );
        }

        TypedExpression expression = leftContainsThis ? right : left;
        indexedByRightOperandExtractor.setEnclosingParameters(true);
        Expression narrowed = rewriteNode( context, narrowExpressionToType(expression, leftType) );
        indexedByRightOperandExtractor.setBody(new ExpressionStmt(narrowed));
        indexedByDSL.addArgument(indexedByRightOperandExtractor);
        indexedByDSL.addArgument(new ClassExpr(StaticJavaParser.parseType(expression.getRawClass().getCanonicalName())));
    }

    String getIndexIdArgument(SingleDrlxParseSuccess drlxParseResult, TypedExpression left) {
        return isAccessibleProperties( drlxParseResult.getPatternType(), left.getFieldName() ) ?
                context.getPackageModel().getDomainClassName( drlxParseResult.getPatternType() ) + ".getPropertyIndex(\"" + left.getFieldName() + "\")" :
                "-1";
    }

    boolean shouldBuildReactOn(SingleDrlxParseSuccess drlxParseResult) {
        return !drlxParseResult.isTemporal() && !drlxParseResult.getReactOnProperties().isEmpty() && context.isPropertyReactive( drlxParseResult.getPatternType() );
    }

    protected Expression generateLambdaForTemporalConstraint(TypedExpression typedExpression, Class<?> patternType) {
        Expression expr = typedExpression.getExpression();
        Collection<String> usedDeclarations = DrlxParseUtil.collectUsedDeclarationsInExpression(expr);
        boolean containsThis = usedDeclarations.contains(THIS_PLACEHOLDER);
        if (containsThis) {
            usedDeclarations.remove(THIS_PLACEHOLDER);
        }
        Expression generatedExpr = generateLambdaWithoutParameters(usedDeclarations, expr, !containsThis, Optional.ofNullable(patternType), context);
        if (generatedExpr instanceof LambdaExpr) {
            context.getPackageModel().getLambdaReturnTypes().put((LambdaExpr) generatedExpr, typedExpression.getType());
        }
        return generatedExpr;
    }

    protected MethodCallExpr buildTemporalExpression(SingleDrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL) {
        boolean thisOnRight = isThisOnRight(drlxParseResult);

        // function for "this" should be added first
        if (thisOnRight) {
            if (drlxParseResult.getRight() != null && !drlxParseResult.getRight().getExpression().isNameExpr()) {
                exprDSL.addArgument(generateLambdaForTemporalConstraint(drlxParseResult.getRight(), drlxParseResult.getPatternType()));
            }
        } else {
            if (drlxParseResult.getLeft() != null && !drlxParseResult.getLeft().getExpression().isNameExpr()) {
                exprDSL.addArgument(generateLambdaForTemporalConstraint(drlxParseResult.getLeft(), drlxParseResult.getPatternType()));
            }
        }

        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        usedDeclarationsWithUnification.addAll(drlxParseResult.getUsedDeclarations());

        usedDeclarationsWithUnification.stream()
                .filter( s -> !(drlxParseResult.isSkipThisAsParam() && s.equals( drlxParseResult.getPatternBinding() ) ) )
                .map(context::getVarExpr)
                .forEach(exprDSL::addArgument);

        if (drlxParseResult.getRightLiteral() != null) {
            exprDSL.addArgument( "" + drlxParseResult.getRightLiteral() );
        } else {
            // function for variable
            if (thisOnRight) {
                if (drlxParseResult.getLeft() != null && !drlxParseResult.getLeft().getExpression().isNameExpr()) {
                    exprDSL.addArgument(generateLambdaForTemporalConstraint(drlxParseResult.getLeft(), drlxParseResult.getPatternType()));
                }
            } else {
                if (drlxParseResult.getRight() != null && !drlxParseResult.getRight().getExpression().isNameExpr()) {
                    exprDSL.addArgument(generateLambdaForTemporalConstraint(drlxParseResult.getRight(), drlxParseResult.getPatternType()));
                }
            }
        }

        if (thisOnRight) {
            exprDSL.addArgument(buildConstraintExpression(drlxParseResult, new MethodCallExpr(drlxParseResult.getExpr(), "thisOnRight")));
        } else {
            exprDSL.addArgument(buildConstraintExpression(drlxParseResult, drlxParseResult.getExpr()));
        }
        return exprDSL;
    }

    protected boolean isThisOnRight(SingleDrlxParseSuccess drlxParseResult) {
        if (drlxParseResult.getRight() != null) {
            if (drlxParseResult.getRight().getExpression().isNameExpr()) {
                NameExpr name = drlxParseResult.getRight().getExpression().asNameExpr();
                if (name.equals(new NameExpr(THIS_PLACEHOLDER))) {
                    return true;
                }
            } else {
                return containsThis(drlxParseResult.getRight());
            }
        }
        return false;
    }

    protected boolean containsThis(TypedExpression typedExpression) {
        Expression expr = typedExpression.getExpression();
        Optional<String> opt = expr.findAll(NameExpr.class)
                .stream()
                .map(NameExpr::getName)
                .map(SimpleName::getIdentifier)
                .findFirst(); // just first one
        if (!opt.isPresent()) {
            return false;
        }
        return opt.get().equals(THIS_PLACEHOLDER);
    }

    protected String createExprId(SingleDrlxParseSuccess drlxParseResult) {
        String exprId = drlxParseResult.getExprId(context.getPackageModel().getExprIdGenerator());

        context.getPackageModel().indexConstraint(exprId, new PredicateInformation(
                drlxParseResult.getOriginalDrlConstraint(),
                context.getRuleName(),
                Optional.ofNullable(context.getRuleDescr())
                    .map(RuleDescr::getResource)
                    .map(Resource::getSourcePath)
                    .orElse("")
        ));
        return exprId;
    }

    protected void sortUsedDeclarations(SingleDrlxParseSuccess drlxParseResult) {
        // Binding parameters have to be sorted as when they're sorted lexicographically when invoked
        // See Accumulate.initInnerDeclarationCache()
        List<String> sorted = drlxParseResult.getUsedDeclarationsOnLeft()
                .stream()
                .sorted()
                .collect(Collectors.toList());
        drlxParseResult.setUsedDeclarationsOnLeft(sorted);
    }
}