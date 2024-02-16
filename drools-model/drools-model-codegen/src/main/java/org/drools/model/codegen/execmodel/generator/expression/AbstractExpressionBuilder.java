/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
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
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.model.Index;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.BoxedParameters;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.drools.model.codegen.execmodel.generator.drlxparse.CoercedExpression;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.MultipleDrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.kie.api.io.Resource;

import static java.util.Optional.ofNullable;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.isThisExpression;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toJavaParserType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toStringLiteral;
import static org.drools.model.codegen.execmodel.generator.drlxparse.ConstraintParser.toBigDecimalExpression;
import static org.drools.modelcompiler.util.ClassUtil.isAccessiblePropertiesIncludingNonGetterValueMethod;
import static org.drools.mvel.parser.printer.PrintUtil.printNode;
import static org.drools.util.ClassUtils.toRawClass;

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

    public void processExpression(MultipleDrlxParseSuccess drlxParseResult) {
        if ( drlxParseResult.isPredicate() ) {
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

    protected Expression getBindingExpression(SingleDrlxParseSuccess drlxParseResult) {
        if (drlxParseResult.getExpr() instanceof EnclosedExpr && !drlxParseResult.isCombined() && !drlxParseResult.isPredicate()) {
            return buildConstraintExpression(drlxParseResult, ((EnclosedExpr) drlxParseResult.getExpr()).getInner());
        } else {
            final TypedExpression boundExpr = drlxParseResult.getBoundExpr();
            // Can we unify it? Sometimes expression is in the left sometimes in expression
            final Expression expression;
            if (boundExpr != null) {
                if (boundExpr.getExpression() instanceof EnclosedExpr) {
                    expression = boundExpr.getExpression();
                } else {
                    expression = findLeftmostExpression(boundExpr.getExpression());
                }
            } else {
                expression = drlxParseResult.getExpr();
            }
            return buildConstraintExpression(drlxParseResult, drlxParseResult.getUsedDeclarationsOnLeft(), expression);
        }
    }

    private Expression findLeftmostExpression(Expression expression) {
        if (expression instanceof EnclosedExpr) {
            return findLeftmostExpression( (( EnclosedExpr ) expression).getInner() );
        }
        if (expression instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr) expression;
            return findLeftmostExpression(be.getLeft());
        }
        if (expression instanceof CastExpr) {
            CastExpr ce = (CastExpr) expression;
            return findLeftmostExpression(ce.getExpression());
        }
        if (expression instanceof MethodCallExpr || expression instanceof FieldAccessExpr) {
            return expression;
        }

        context.addCompilationError(new InvalidExpressionErrorResult("Unable to Analyse Expression" + printNode(expression)));
        return expression;
    }

    protected Expression buildConstraintExpression(SingleDrlxParseSuccess drlxParseResult, Expression expr ) {
        return buildConstraintExpression(drlxParseResult, drlxParseResult.getUsedDeclarations(), expr );
    }

    protected Expression buildConstraintExpression(SingleDrlxParseSuccess drlxParseResult, Collection<String> usedDeclarations, Expression expr ) {
        return drlxParseResult.isStatic() ?
                expr :
                generateLambdaWithoutParameters(usedDeclarations, expr, drlxParseResult.isSkipThisAsParam(), ofNullable(drlxParseResult.getPatternType()), context);
    }

    boolean shouldCreateIndex(SingleDrlxParseSuccess drlxParseResult ) {
        if ( drlxParseResult.getDecodeConstraintType() == Index.ConstraintType.FORALL_SELF_JOIN ) {
            return true;
        }

        if (!shouldIndexConstraintWithRightScopePatternBinding(drlxParseResult)) {
            return false;
        }

        TypedExpression left = drlxParseResult.getLeft();
        TypedExpression right = drlxParseResult.getRight();
        Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();

        return left != null && (left.getFieldName() != null || isThisExpression(left.getExpression())) &&
                drlxParseResult.getDecodeConstraintType() != null &&
                drlxParseResult.getPatternType() != null &&
                isLeftIndexableExpression(left.getExpression()) &&
                areIndexableDeclaration(usedDeclarations) &&
                right != null && !right.getExpression().isArrayAccessExpr() && !right.getExpression().isNullLiteralExpr();
    }

    private boolean isLeftIndexableExpression(Expression expr) {
        if (expr instanceof MethodCallExpr) {
            Optional<Expression> methodChainScope = DrlxParseUtil.findRootNodeViaScope(expr);
            return methodChainScope.map(DrlxParseUtil::isThisExpression).orElse(false);
        }
        return true;
    }

    private boolean areIndexableDeclaration( Collection<String> usedDeclarations ) {
        if (usedDeclarations.size() > 4) {
            return false;
        }
        return usedDeclarations.stream()
                .map( context::getTypedDeclarationById)
                .noneMatch(optDecl -> optDecl.isPresent() && optDecl.get().isGlobal() );
    }

    // See PatternBuilder:1198 (buildConstraintForPattern) Pattern are indexed only when the root of the right part
    // (i.e. $p in address.street == $p.name) is a Pattern binding.
    // See also IndexingTest and ExistentialTest
    protected boolean shouldIndexConstraintWithRightScopePatternBinding(SingleDrlxParseSuccess result) {
        TypedExpression right = result.getRight();

        if (right != null && right.getExpression() != null && right.getExpression() instanceof NodeWithOptionalScope) {
            if (isStringToDateExpression(right.getExpression()) || isNumberToStringExpression(right.getExpression())) {
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
        return expression instanceof NameExpr &&
                ((NameExpr) expression).getNameAsString().startsWith( CoercedExpression.STRING_TO_DATE_FIELD_START );
    }

    protected boolean isNumberToStringExpression(Expression expression) {
        return expression instanceof MethodCallExpr &&
                ((MethodCallExpr) expression).getNameAsString().equals("valueOf") &&
                ((MethodCallExpr) expression).getScope().map(s -> s.toString().equals("String")).orElse(false);
    }

    public static AbstractExpressionBuilder getExpressionBuilder(RuleContext context) {
        return new PatternExpressionBuilder( context );
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
                return toNewExpr(BigDecimal.class, toStringLiteral(((BigDecimalLiteralExpr) expression).asBigDecimal().toString()));
            }
            if (expression instanceof BigIntegerLiteralExpr) {
                return toNewExpr(toRawClass(leftType), toStringLiteral(((BigIntegerLiteralExpr) expression).asBigInteger().toString()));
            }
            if (leftType.equals(BigDecimal.class)) {
                String expressionString = stringValue(expression);
                final BigDecimal bigDecimal = new BigDecimal( expressionString );
                return toNewExpr(BigDecimal.class, toStringLiteral( bigDecimal.toString() ) );
            }
            if (leftType.equals(BigInteger.class)) {
                String expressionString = stringValue(expression);
                final BigInteger bigInteger = new BigDecimal(expressionString).toBigInteger();
                return toNewExpr(BigInteger.class, toStringLiteral(bigInteger.toString()));
            }

            if (leftType.equals(float.class)) {
                return new DoubleLiteralExpr(expression + "f");
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

        return expression;
    }

    private String stringValue(Expression expression) {
        if(expression.isStringLiteralExpr()) {
            return expression.asStringLiteralExpr().getValue();
        } else {
            return expression.toString();
        }
    }

    private static Expression toNewExpr(Class<?> clazz, Expression initExpression) {
        return new ObjectCreationExpr(null, toClassOrInterfaceType(clazz), NodeList.nodeList(initExpression));
    }

    protected void addIndexedByDeclaration(TypedExpression left,
                                           TypedExpression right,
                                           boolean leftContainsThis,
                                           MethodCallExpr indexedByDSL,
                                           Collection<String> usedDeclarations) {
        LambdaExpr indexedByRightOperandExtractor = new LambdaExpr();

        BlockStmt lambdaBlock = new BlockStmt();

        NodeList<Parameter> parameters = new BoxedParameters(context).getBoxedParametersWithUnboxedAssignment(usedDeclarations, lambdaBlock);
        parameters.forEach(indexedByRightOperandExtractor::addParameter);

        TypedExpression expression = leftContainsThis ? right : left;
        indexedByRightOperandExtractor.setEnclosingParameters(true);

        Expression extractorExpression = expression.getExpression();
        extractorExpression = DrlxParseUtil.stripEnclosedExpr(extractorExpression);
        if (extractorExpression instanceof BinaryExpr && expression.getType() == BigDecimal.class) {
            extractorExpression = toBigDecimalExpression(expression, context);
        }
        lambdaBlock.addStatement(new ReturnStmt(extractorExpression));

        indexedByRightOperandExtractor.setBody(lambdaBlock);
        indexedByDSL.addArgument(indexedByRightOperandExtractor);
        indexedByDSL.addArgument(new ClassExpr(toJavaParserType(expression.getRawClass())));
    }

    String getIndexIdArgument(SingleDrlxParseSuccess drlxParseResult, TypedExpression left) {
        return isAccessiblePropertiesIncludingNonGetterValueMethod( drlxParseResult.getPatternType(), left.getFieldName() ) ?
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
            context.getPackageModel().registerLambdaReturnType((LambdaExpr) generatedExpr, typedExpression.getType());
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

        drlxParseResult.getUsedDeclarations().stream()
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
                return name.equals(new NameExpr(THIS_PLACEHOLDER));
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
        return opt.map(s -> s.equals(THIS_PLACEHOLDER)).orElse(false);
    }

    protected String createExprId(SingleDrlxParseSuccess drlxParseResult) {
        String exprId = drlxParseResult.getExprId(context.getPackageModel().getExprIdGenerator());

        String stringConstraint = drlxParseResult.getOriginalDrlConstraint();
        String ruleName = context.getRuleName();
        String ruleFileName = Optional.ofNullable(context.getRuleDescr())
                                      .map(RuleDescr::getResource)
                                      .map(Resource::getSourcePath)
                                      .orElse("");

        context.getPackageModel().indexConstraint(exprId, stringConstraint, ruleName, ruleFileName);

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