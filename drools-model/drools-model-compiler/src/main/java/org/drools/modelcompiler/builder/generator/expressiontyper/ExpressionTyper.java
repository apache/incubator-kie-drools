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

package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import org.drools.core.addon.TypeResolver;
import org.drools.core.util.MethodUtils;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.errors.ParseExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.UnificationTypedExpression;
import org.drools.modelcompiler.builder.generator.operatorspec.CustomOperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.NativeOperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.OperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.TemporalOperatorSpec;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.FullyQualifiedInlineCastExpr;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
import org.drools.mvel.parser.ast.expr.HalfPointFreeExpr;
import org.drools.mvel.parser.ast.expr.InlineCastExpr;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpressionElement;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpressionKeyValuePair;
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.mvel.parser.ast.expr.NullSafeMethodCallExpr;
import org.drools.mvel.parser.ast.expr.OOPathChunk;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.printer.PrintUtil;
import org.drools.mvelcompiler.util.BigDecimalArgumentCoercion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.ast.NodeList.nodeList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.drools.core.util.ClassUtils.extractGenericType;
import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findRootNodeViaParent;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isThisExpression;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.nameExprToMethodCallExpr;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.prepend;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.replaceAllHalfBinaryChildren;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.safeResolveType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.transformDrlNameExprToNameExpr;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.trasformHalfBinaryToBinary;
import static org.drools.modelcompiler.builder.generator.expressiontyper.FlattenScope.flattenScope;
import static org.drools.modelcompiler.builder.generator.expressiontyper.FlattenScope.transformFullyQualifiedInlineCastExpr;
import static org.drools.modelcompiler.util.ClassUtil.getTypeArgument;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;
import static org.drools.mvel.parser.MvelParser.parseType;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;
import static org.kie.internal.ruleunit.RuleUnitUtil.isDataSource;

public class ExpressionTyper {

    private final RuleContext ruleContext;
    private final PackageModel packageModel;
    private Class<?> patternType;
    private String bindingId;
    private boolean isPositional;
    private final ExpressionTyperContext context;

    private static final Logger logger          = LoggerFactory.getLogger(ExpressionTyper.class);

    public ExpressionTyper(RuleContext ruleContext, Class<?> patternType, String bindingId, boolean isPositional) {
        this(ruleContext, patternType, bindingId, isPositional, new ExpressionTyperContext());
    }

    // When using the ExpressionTyper outside of a pattern
    public ExpressionTyper(RuleContext ruleContext) {
        this(ruleContext, Object.class, null, false, new ExpressionTyperContext());
    }

    public ExpressionTyper(RuleContext ruleContext, Class<?> patternType, String bindingId, boolean isPositional, ExpressionTyperContext context) {
        this.ruleContext = ruleContext;
        packageModel = ruleContext.getPackageModel();
        this.patternType = patternType;
        this.bindingId = bindingId;
        this.isPositional = isPositional;
        this.context = context;
    }

    public TypedExpressionResult toTypedExpression(Expression drlxExpr) {
        context.setOriginalExpression(drlxExpr);
        if (logger.isDebugEnabled()) {
            logger.debug( "Typed expression Input: drlxExpr = {} , patternType = {} ,declarations = {}", printConstraint(drlxExpr), patternType, context.getUsedDeclarations() );
        }
        final Optional<TypedExpression> typedExpression = toTypedExpressionRec(drlxExpr);
        typedExpression.ifPresent(t -> t.setOriginalPatternType(patternType));
        final TypedExpressionResult typedExpressionResult = new TypedExpressionResult(typedExpression, context);
        if (logger.isDebugEnabled()) {
            logger.debug( "Typed expression Output: {}", typedExpressionResult );
        }
        return typedExpressionResult;
    }

    private Optional<TypedExpression> toTypedExpressionRec(Expression drlxExpr) {

        Class<?> typeCursor = patternType;

        if (drlxExpr instanceof FullyQualifiedInlineCastExpr ) {
            return toTypedExpressionRec( transformFullyQualifiedInlineCastExpr( ruleContext.getTypeResolver(), (FullyQualifiedInlineCastExpr) drlxExpr ) );
        }

        if (drlxExpr instanceof EnclosedExpr) {
            Expression inner = ((EnclosedExpr) drlxExpr).getInner();
            Optional<TypedExpression> typedExpression = toTypedExpressionRec(inner);
            return typedExpression.map(t -> t.cloneWithNewExpression(new EnclosedExpr(t.getExpression())));
        }

        if (drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodExpr = (MethodCallExpr) drlxExpr;
            Expression expr = methodExpr;
            if (isEval(methodExpr.getNameAsString(), methodExpr.getScope(), methodExpr.getArguments())) {
                expr = methodExpr.getArgument(0);
            }
            drlxExpr = expr;
        }

        if (drlxExpr instanceof NullSafeMethodCallExpr) {
            NullSafeMethodCallExpr methodExpr = (NullSafeMethodCallExpr) drlxExpr;
            Expression expr = methodExpr;
            if (isEval(methodExpr.getNameAsString(), methodExpr.getScope(), methodExpr.getArguments())) {
                expr = methodExpr.getArgument(0);
            }
            drlxExpr = expr;
        }

        if (drlxExpr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr) drlxExpr;
            Optional<TypedExpression> optTypedExpr = toTypedExpressionRec(unaryExpr.getExpression());
            return optTypedExpr.map(typedExpr -> new TypedExpression( new UnaryExpr( typedExpr.getExpression(), unaryExpr.getOperator() ), typedExpr.getType() ));
        }

        if (drlxExpr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) drlxExpr;

            BinaryExpr.Operator operator = binaryExpr.getOperator();

            Optional<TypedExpression> optLeft = toTypedExpressionRec(binaryExpr.getLeft());
            Optional<TypedExpression> optRight = toTypedExpressionRec(binaryExpr.getRight());

            return optLeft.flatMap(left -> optRight.flatMap(right -> {
                final BinaryExpr combo = new BinaryExpr(left.getExpression(), right.getExpression(), operator);
                return of(new TypedExpression(combo, left.getType()));
            }));
        }

        if (drlxExpr instanceof HalfBinaryExpr) {
            final Expression binaryExpr = trasformHalfBinaryToBinary(drlxExpr);
            return toTypedExpressionRec(binaryExpr);
        }

        if (drlxExpr instanceof LiteralExpr) {
            drlxExpr = normalizeDigit(drlxExpr);
            return of(new TypedExpression(drlxExpr, getLiteralExpressionType( ( LiteralExpr ) drlxExpr )));
        }

        if (drlxExpr instanceof ThisExpr || (drlxExpr instanceof NameExpr && THIS_PLACEHOLDER.equals(printConstraint(drlxExpr)))) {
            return of(new TypedExpression(new NameExpr(THIS_PLACEHOLDER), patternType));

        }

        if (drlxExpr instanceof CastExpr) {
            CastExpr castExpr = (CastExpr)drlxExpr;
            Optional<TypedExpression> optTypedExpr = toTypedExpressionRec(castExpr.getExpression());
            return optTypedExpr.map(typedExpr -> new TypedExpression(new CastExpr(castExpr.getType(), typedExpr.getExpression()), getClassFromContext(ruleContext.getTypeResolver(), castExpr.getType().asString())));
        }

        if (drlxExpr instanceof NameExpr) {
            return nameExpr(((NameExpr)drlxExpr).getNameAsString(), typeCursor);
        }

        if (drlxExpr instanceof FieldAccessExpr || drlxExpr instanceof MethodCallExpr || drlxExpr instanceof ObjectCreationExpr
                || drlxExpr instanceof NullSafeFieldAccessExpr || drlxExpr instanceof  NullSafeMethodCallExpr || drlxExpr instanceof MapCreationLiteralExpression || drlxExpr instanceof ListCreationLiteralExpression) {

            return toTypedExpressionFromMethodCallOrField(drlxExpr).getTypedExpression();
        }

        if (drlxExpr instanceof PointFreeExpr) {

            final PointFreeExpr pointFreeExpr = (PointFreeExpr)drlxExpr;

            Optional<TypedExpression> optLeft = toTypedExpressionRec(pointFreeExpr.getLeft());
            Optional<TypedExpression> optRight = pointFreeExpr.getRight().size() == 1 ? toTypedExpressionRec(pointFreeExpr.getRight().get( 0 )) : Optional.empty();
            OperatorSpec opSpec = getOperatorSpec(pointFreeExpr.getRight(), pointFreeExpr.getOperator());

            return optLeft.map(left -> new TypedExpression(opSpec.getExpression( ruleContext, pointFreeExpr, left, this), left.getType())
                    .setStatic(opSpec.isStatic())
                    .setLeft(left)
                    .setRight( optRight.orElse( null ) ) );
        }

        if (drlxExpr instanceof HalfPointFreeExpr) {

            final HalfPointFreeExpr halfPointFreeExpr = (HalfPointFreeExpr)drlxExpr;
            Expression parentLeft = findLeftLeafOfNameExpr(halfPointFreeExpr.getParentNode().orElseThrow(UnsupportedOperationException::new));

            Optional<TypedExpression> optLeft = toTypedExpressionRec(parentLeft);
            OperatorSpec opSpec = getOperatorSpec(halfPointFreeExpr.getRight(), halfPointFreeExpr.getOperator());

            final PointFreeExpr transformedToPointFree =
                    new PointFreeExpr(halfPointFreeExpr.getTokenRange().orElseThrow(() -> new IllegalStateException("Token range is not present!")),
                                      parentLeft,
                                      halfPointFreeExpr.getRight(),
                                      halfPointFreeExpr.getOperator(),
                                      halfPointFreeExpr.isNegated(),
                                      halfPointFreeExpr.getArg1(),
                                      halfPointFreeExpr.getArg2(),
                                      halfPointFreeExpr.getArg3(),
                                      halfPointFreeExpr.getArg4()
                    );

            return optLeft.map(left ->
                                new TypedExpression(opSpec.getExpression(ruleContext, transformedToPointFree, left, this), left.getType())
                                        .setStatic(opSpec.isStatic())
                                        .setLeft(left));

        }

        if (drlxExpr instanceof ArrayAccessExpr) {
            final ArrayAccessExpr arrayAccessExpr = (ArrayAccessExpr)drlxExpr;
            if (Map.class.isAssignableFrom( typeCursor )) {
                return createMapAccessExpression(arrayAccessExpr.getIndex(), arrayAccessExpr.getName() instanceof ThisExpr ? new NameExpr(THIS_PLACEHOLDER) : arrayAccessExpr.getName(), Map.class);
            } else if (arrayAccessExpr.getName() instanceof FieldAccessExpr ) {
                Optional<TypedExpression> typedExpression = toTypedExpressionFromMethodCallOrField(drlxExpr).getTypedExpression();
                typedExpression.ifPresent(te -> {
                    final Expression originalExpression = te.getExpression();
                    DrlxParseUtil.removeRootNode(originalExpression);
                });
                return typedExpression;
            } else {
                String name = printConstraint(drlxExpr.asArrayAccessExpr().getName());
                final Optional<TypedExpression> nameExpr = nameExpr(name, typeCursor);
                Expression indexExpr = toTypedExpressionFromMethodCallOrField( arrayAccessExpr.getIndex() )
                        .getTypedExpression()
                        .orElseThrow(() -> new NoSuchElementException("TypedExpressionResult doesn't contain TypedExpression!"))
                        .getExpression();
                return nameExpr.flatMap( te -> transformToArrayOrMapExpressionWithType(indexExpr, te));
            }
        }

        if (drlxExpr instanceof InstanceOfExpr) {
            InstanceOfExpr instanceOfExpr = (InstanceOfExpr)drlxExpr;
            ruleContext.addInlineCastType(printConstraint(instanceOfExpr.getExpression()), instanceOfExpr.getType());
            return toTypedExpressionRec(instanceOfExpr.getExpression())
                    .map( e -> new TypedExpression(new InstanceOfExpr(e.getExpression(), instanceOfExpr.getType()), boolean.class) );

        }

        if (drlxExpr instanceof ClassExpr) {
            return of(new TypedExpression(drlxExpr, Class.class));
        }

        if (drlxExpr instanceof InlineCastExpr) {
            return toTypedExpressionFromMethodCallOrField(drlxExpr).getTypedExpression();
        }

        if (drlxExpr instanceof OOPathExpr) {
            Class<?> type = patternType;
            for (OOPathChunk chunk : ((OOPathExpr) drlxExpr).getChunks()) {
                final String fieldName = chunk.getField().toString();

                final TypedExpression callExpr = DrlxParseUtil.nameExprToMethodCallExpr(fieldName, type, null);
                if (callExpr == null) {
                    return empty();
                }
                Class<?> fieldType = (chunk.getInlineCast() != null)
                        ? DrlxParseUtil.getClassFromContext(ruleContext.getTypeResolver(), chunk.getInlineCast().toString())
                        : callExpr.getRawClass();

                if ( !chunk.isSingleValue() && Iterable.class.isAssignableFrom(fieldType) || isDataSource(fieldType) ) {
                    type = extractGenericType(type, ((MethodCallExpr) callExpr.getExpression()).getName().toString());
                } else {
                    type = fieldType;
                }
            }
            return of(new TypedExpression(drlxExpr, type));
        }

        if (drlxExpr.isAssignExpr()) {
            AssignExpr assignExpr = drlxExpr.asAssignExpr();

            final Expression rightSide = assignExpr.getValue();

            return toTypedExpressionRec(rightSide)
                    .map(e -> {
                        final AssignExpr newExpression = new AssignExpr(assignExpr.getTarget(), e.getExpression(), assignExpr.getOperator());
                        return new TypedExpression(newExpression, e.getType());
                    });

        }

        throw new UnsupportedOperationException();
    }

    private Expression normalizeDigit(Expression expr) {
        if (expr instanceof DoubleLiteralExpr) {
            return new DoubleLiteralExpr(((DoubleLiteralExpr) expr).asDouble());
        }
        return expr;
    }

    private Optional<TypedExpression> transformToArrayOrMapExpressionWithType(Expression indexExpr, TypedExpression te) {
        if (te.isArray()) {
            return createArrayAccessExpression(indexExpr, te.getExpression());
        }

        java.lang.reflect.Type type;
        if (te.isList()) {
            type = getTypeArgument(te.getType(), 0);
        } else if(te.isMap()) {
            type = getTypeArgument(te.getType(), 1);
        } else {
            type = Object.class;
        }
        return createMapAccessExpression(indexExpr, te.getExpression(), type);
    }

    private boolean isEval(String nameAsString, Optional<Expression> scope, NodeList<Expression> arguments) {
        return nameAsString.equals("eval") && !scope.isPresent() && arguments.size() == 1;
    }

    private Optional<TypedExpression> createArrayAccessExpression(Expression index, Expression scope) {
        ArrayAccessExpr arrayAccessExpr = new ArrayAccessExpr(scope, index);
        TypedExpression typedExpression = new TypedExpression(arrayAccessExpr, Object.class);
        return of(typedExpression);
    }

    private Optional<TypedExpression> createMapAccessExpression(Expression index, Expression scope, java.lang.reflect.Type type) {
        MethodCallExpr mapAccessExpr = new MethodCallExpr(scope, "get" );
        mapAccessExpr.addArgument(index);
        if(index.isNameExpr()) {
            context.addUsedDeclarations(printConstraint(index));
        }
        TypedExpression typedExpression = new TypedExpression(mapAccessExpr, type);
        return of(typedExpression);
    }

    private Optional<TypedExpression> nameExpr(String name, Class<?> typeCursor) {
        TypedExpression expression = nameExprToMethodCallExpr(name, typeCursor, null);
        if (expression != null) {
            context.addReactOnProperties(name);
            Expression plusThis = prepend(new NameExpr(THIS_PLACEHOLDER), expression.getExpression());
            return of(new TypedExpression(plusThis, expression.getType(), name));
        }

        Optional<DeclarationSpec> decl = ruleContext.getDeclarationById(name);
        if (decl.isPresent()) {
            // then drlxExpr is a single NameExpr referring to a binding, e.g.: "$p1".
            context.addUsedDeclarations(name);
            decl.get().getBoundVariable().ifPresent( context::addReactOnProperties );
            return of(new TypedExpression(new NameExpr(name), decl.get().getDeclarationClass()));
        }

        if (ruleContext.getQueryParameters().stream().anyMatch(qp -> qp.getName().equals(name))) {
            // then drlxExpr is a single NameExpr referring to a query parameter, e.g.: "$p1".
            context.addUsedDeclarations(name);
            return of(new TypedExpression(new NameExpr(name)));

        } else if(packageModel.getGlobals().containsKey(name)){
            Expression plusThis = new NameExpr(name);
            context.addUsedDeclarations(name);
            return of(new TypedExpression(plusThis, packageModel.getGlobals().get(name)));

        } else if (isPositional || ruleContext.isQuery()) {
            String unificationVariable = ruleContext.getOrCreateUnificationId(name);
            expression = new UnificationTypedExpression(unificationVariable, typeCursor, name);
            return of(expression);
        }

        return empty();
    }

    private OperatorSpec getOperatorSpec(NodeList<Expression> rightExpressions, SimpleName expressionOperator) {
        for (Expression rightExpr : rightExpressions) {
            toTypedExpressionRec(rightExpr);
        }

        String operator = expressionOperator.asString();
        if (ModelGenerator.temporalOperators.contains(operator )) {
            return TemporalOperatorSpec.INSTANCE;
        }
        if ( org.drools.model.functions.Operator.Register.hasOperator( operator ) ) {
            return NativeOperatorSpec.INSTANCE;
        }
        return CustomOperatorSpec.INSTANCE;
    }

    private TypedExpressionResult toTypedExpressionFromMethodCallOrField(Expression drlxExpr) {
        if (drlxExpr instanceof FieldAccessExpr) {
            // try to see if it's a constant
            final Optional<TypedExpression> typedExpression = tryParseAsConstantField(ruleContext.getTypeResolver(), ((FieldAccessExpr) drlxExpr).getScope(), ((FieldAccessExpr) drlxExpr).getNameAsString());
            if(typedExpression.isPresent()) {
                return new TypedExpressionResult(typedExpression, context);
            }
        }

        if (patternType == null && drlxExpr instanceof NullSafeFieldAccessExpr) {
            // try to see if it's a constant
            final Optional<TypedExpression> typedExpression = tryParseAsConstantField(ruleContext.getTypeResolver(), ((NullSafeFieldAccessExpr) drlxExpr).getScope(), ((NullSafeFieldAccessExpr) drlxExpr).getNameAsString());
            if(typedExpression.isPresent()) {
                return new TypedExpressionResult(typedExpression, context);
            }
        }

        final List<Node> childrenNodes = flattenScope(ruleContext.getTypeResolver(), drlxExpr);
        final Node firstChild = childrenNodes.get(0);

        boolean isInLineCast = firstChild instanceof InlineCastExpr;
        java.lang.reflect.Type originalTypeCursor;
        final Node firstNode;
        if (isInLineCast) {
            InlineCastExpr inlineCast = (InlineCastExpr) firstChild;
            originalTypeCursor = originalTypeCursorFromInlineCast(inlineCast);
            firstNode = inlineCast.getExpression();

            if (inlineCast.getExpression().isThisExpr()) {
                context.setInlineCastExpression(
                        Optional.of(new InstanceOfExpr(new NameExpr(THIS_PLACEHOLDER), (ReferenceType) inlineCast.getType())) );
            } else {
                context.setInlineCastExpression(
                    toTypedExpression(inlineCast.getExpression()).getTypedExpression().map( TypedExpression::getExpression )
                            .map( expr ->  new InstanceOfExpr(expr, (ReferenceType) inlineCast.getType())) );
            }
        } else {
            originalTypeCursor = patternType;
            firstNode = firstChild;
        }

        if (originalTypeCursor != null && originalTypeCursor.equals(Object.class)) {
            // try infer type  from the declarations
            final Optional<DeclarationSpec> declarationById = ruleContext.getDeclarationById(printConstraint(firstChild));
            originalTypeCursor = declarationById.map(d -> (java.lang.reflect.Type)d.getDeclarationClass()).orElse(originalTypeCursor);
        }

        final Optional<TypedExpressionCursor> teCursor = processFirstNode(drlxExpr, childrenNodes, firstNode, isInLineCast, originalTypeCursor);

        if (firstNode instanceof MethodCallExpr) {
            MethodCallExpr me = (MethodCallExpr) firstNode;
            addReactOnProperty(me.getNameAsString(), me.getArguments());
        }
        if (firstNode instanceof NullSafeMethodCallExpr) {
            NullSafeMethodCallExpr me = (NullSafeMethodCallExpr) firstNode;
            addReactOnProperty(me.getNameAsString(), me.getArguments());
        }

        if(!teCursor.isPresent()) {
            return new TypedExpressionResult(empty(), context);
        }

        Expression previous = teCursor.get().expressionCursor;
        java.lang.reflect.Type typeCursor = teCursor.get().typeCursor;

        List<Node> childrenWithoutFirst = childrenNodes.subList(1, childrenNodes.size());
        for (Node part : childrenWithoutFirst) {
            if (part instanceof SimpleName) {
                String field = part.toString();
                TypedExpression expression = nameExprToMethodCallExpr(field, typeCursor, previous);
                if (expression == null) {
                    ruleContext.addCompilationError( new InvalidExpressionErrorResult( "Unknown field " + field + " on " + typeCursor ) );
                    break;
                }
                typeCursor = expression.getType();
                previous = expression.getExpression();

            } else if (part instanceof MethodCallExpr) {
                TypedExpressionCursor typedExpr = methodCallExpr((MethodCallExpr) part, typeCursor, previous);
                typeCursor = typedExpr.typeCursor;
                previous = typedExpr.expressionCursor;

            } else if (part instanceof NullSafeMethodCallExpr) {
                TypedExpressionCursor typedExpr = nullSafeMethodCallExpr((NullSafeMethodCallExpr) part, typeCursor, previous);
                typeCursor = typedExpr.typeCursor;
                previous = typedExpr.expressionCursor;

            } else if (part instanceof InlineCastExpr && ((InlineCastExpr) part).getExpression() instanceof FieldAccessExpr) {
                InlineCastExpr inlineCastExprPart = (InlineCastExpr) part;
                final FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) inlineCastExprPart.getExpression();
                final TypedExpression toMethodCallExpr = nameExprToMethodCallExpr(fieldAccessExpr.getNameAsString(), typeCursor, previous);
                if (toMethodCallExpr == null) {
                    ruleContext.addCompilationError( new InvalidExpressionErrorResult( "Unknown field " + fieldAccessExpr.getNameAsString() + " on " + typeCursor ) );
                    break;
                }
                final Class<?> castClass = getClassFromType(ruleContext.getTypeResolver(), inlineCastExprPart.getType());
                previous = addCastToExpression(castClass, toMethodCallExpr.getExpression(), false);
                typeCursor = castClass;

            } else if (part instanceof ArrayAccessExpr) {
                final ArrayAccessExpr inlineCastExprPart = (ArrayAccessExpr) part;
                TypedExpressionCursor typedExpr =
                        arrayAccessExpr(inlineCastExprPart, typeCursor, previous)
                                .orElseThrow(() -> new NoSuchElementException("ArrayAccessExpr doesn't contain TypedExpressionCursor!"));
                typeCursor = typedExpr.typeCursor;
                previous = typedExpr.expressionCursor;


            } else {
                throw new UnsupportedOperationException();
            }
        }

        return new TypedExpressionResult(of(new TypedExpression(previous, typeCursor, accessorToFieldName(drlxExpr))), context);
    }

    private String accessorToFieldName(Expression drlxExpr) {
        if (drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodCall = ( MethodCallExpr ) drlxExpr;
            return methodCall.getArguments().isEmpty() ? getter2property( methodCall.getNameAsString() ) : null;
        }
        return printConstraint(drlxExpr);
    }

    private void addReactOnProperty(String methodName, NodeList<Expression> methodArguments) {
        if (methodArguments.isEmpty()) {
            String firstProp = getter2property(methodName);
            if (firstProp != null) {
                context.addReactOnProperties( firstProp );
            }
        } else {
            for (Expression arg : methodArguments) {
                addReactOnPropertyForArgument( arg );
            }
        }
    }

    private void addReactOnPropertyForArgument( Node arg ) {
        if ( arg instanceof MethodCallExpr) {
            MethodCallExpr methodArg = ( MethodCallExpr ) arg;
            if ( methodArg.getArguments().isEmpty() && isThisExpression( methodArg.getScope().orElse( null ) ) ) {
                String firstProp = getter2property(methodArg.getNameAsString());
                if (firstProp != null) {
                    context.addReactOnProperties( firstProp );
                    return;
                }
            }
        }

        for (Node child : arg.getChildNodes()) {
            addReactOnPropertyForArgument( child );
        }
    }

    public static Optional<TypedExpression> tryParseAsConstantField(TypeResolver typeResolver, Expression scope, String name) {
        Class<?> clazz;
        try {
            clazz = DrlxParseUtil.getClassFromContext(typeResolver, PrintUtil.printConstraint(scope));
        } catch(RuntimeException e) {
            return empty();
        }
        String field = name;

        final Object staticValue;
        try {
            staticValue = clazz.getDeclaredField(field).get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return empty();
        }

        if(staticValue != null) {
            final Expression sanitizedScope = transformDrlNameExprToNameExpr(scope);
            return of(new TypedExpression(new FieldAccessExpr(sanitizedScope, name), clazz)
                    .setType(staticValue.getClass()));
        } else {
            return empty();
        }
    }

    private Optional<TypedExpressionCursor> processFirstNode(Expression drlxExpr, List<Node> childNodes, Node firstNode, boolean isInLineCast, java.lang.reflect.Type originalTypeCursor) {
        Optional<TypedExpressionCursor> result;
        if (isThisExpression(firstNode) || (firstNode instanceof DrlNameExpr && printConstraint(firstNode).equals(bindingId))) {
            result = of(thisExpr(drlxExpr, childNodes, isInLineCast, originalTypeCursor));

        } else if (firstNode instanceof DrlNameExpr) {
            result = drlNameExpr(drlxExpr, (DrlNameExpr) firstNode, isInLineCast, originalTypeCursor);

        } else if (firstNode instanceof NameExpr) {
            result = drlNameExpr(drlxExpr, new DrlNameExpr( (( NameExpr ) firstNode).getName() ), isInLineCast, originalTypeCursor);

        } else if (firstNode instanceof FieldAccessExpr) {
            if (((FieldAccessExpr) firstNode).getScope() instanceof ThisExpr) {
                result = of( fieldAccessExpr( originalTypeCursor, (( FieldAccessExpr ) firstNode).getName() ) );
            } else {
                try {
                    Class<?> resolvedType = ruleContext.getTypeResolver().resolveType( firstNode.toString() );
                    result = of( new TypedExpressionCursor( new NameExpr( firstNode.toString() ), resolvedType ) );
                } catch (ClassNotFoundException e) {
                    result = empty();
                }
            }

        } else if (firstNode instanceof NullSafeFieldAccessExpr && ((NullSafeFieldAccessExpr) firstNode).getScope() instanceof ThisExpr) {
            result = of(fieldAccessExpr(originalTypeCursor, ((NullSafeFieldAccessExpr) firstNode).getName()));

        } else if (firstNode instanceof MethodCallExpr) {
            Optional<Expression> scopeExpr = ((MethodCallExpr) firstNode).getScope();
            Optional<DeclarationSpec> scopeDecl = scopeExpr.flatMap( scope -> ruleContext.getDeclarationById(PrintUtil.printConstraint(scope) ) );

            Expression scope;
            java.lang.reflect.Type type;
            if (scopeDecl.isPresent() && !scopeDecl.get().getBindingId().equals( bindingId )) {
                type = scopeDecl.get().getDeclarationClass();
                scope = new NameExpr( scopeDecl.get().getBindingId() );
                context.addUsedDeclarations( scopeDecl.get().getBindingId() );
            } else if (scopeExpr.isPresent()) {
                TypedExpressionCursor parsedScope = processFirstNode(drlxExpr, childNodes, scopeExpr.get(), isInLineCast, originalTypeCursor).get();
                type = parsedScope.typeCursor;
                scope = parsedScope.expressionCursor;
            } else {
                type = originalTypeCursor;
                scope = new NameExpr( THIS_PLACEHOLDER );
            }

            result = of(methodCallExpr((MethodCallExpr) firstNode, type, scope));

        } else if (firstNode instanceof ObjectCreationExpr) {
            result = of(objectCreationExpr((ObjectCreationExpr) firstNode));

        } else if (firstNode instanceof StringLiteralExpr) {
            result = of(stringLiteralExpr((StringLiteralExpr) firstNode));

        } else if (firstNode instanceof EnclosedExpr) {
            result = processFirstNode( drlxExpr, childNodes, (( EnclosedExpr ) firstNode).getInner(), isInLineCast, originalTypeCursor);

        } else if (firstNode instanceof CastExpr) {
            result = castExpr( ( CastExpr ) firstNode, drlxExpr, childNodes, isInLineCast, originalTypeCursor );

        } else if (firstNode instanceof ArrayCreationExpr) {
            result = of(arrayCreationExpr( (( ArrayCreationExpr ) firstNode) ));

        } else if (firstNode instanceof BinaryExpr) {
            result = of( binaryExpr( ( BinaryExpr ) firstNode ));

        } else if (firstNode instanceof ArrayAccessExpr) {
            Optional<DeclarationSpec> scopeDecl = ruleContext.getDeclarationById( ((ArrayAccessExpr) firstNode).getName().toString() );

            Expression scope;
            java.lang.reflect.Type type;
            if (scopeDecl.isPresent() && !scopeDecl.get().getBindingId().equals( bindingId )) {
                type = scopeDecl.get().getDeclarationClass();
                scope = new NameExpr( scopeDecl.get().getBindingId() );
                context.addUsedDeclarations( scopeDecl.get().getBindingId() );
            } else {
                type = originalTypeCursor;
                scope = new NameExpr( THIS_PLACEHOLDER );
            }

            result = arrayAccessExpr((ArrayAccessExpr) firstNode, type, scope);

        } else if (firstNode instanceof MapCreationLiteralExpression) {
            result = mapCreationLiteral((MapCreationLiteralExpression) firstNode, originalTypeCursor);
        } else if (firstNode instanceof ListCreationLiteralExpression) {
            result = listCreationLiteral((ListCreationLiteralExpression) firstNode, originalTypeCursor);
        } else {
            result = of(new TypedExpressionCursor( (Expression)firstNode, getExpressionType( ruleContext, ruleContext.getTypeResolver(), (Expression)firstNode, context.getUsedDeclarations() ) ));
        }

        if (result.isPresent()) {
            processNullSafeDereferencing( drlxExpr );
        }

        return result.map(te -> {
            if (isInLineCast) {
                Expression exprWithInlineCast = addCastToExpression(toRawClass( te.typeCursor ), te.expressionCursor, isInLineCast);
                return new TypedExpressionCursor(exprWithInlineCast, te.typeCursor);
            } else {
                return te;
            }
        });
    }

    private void processNullSafeDereferencing( Expression drlxExpr ) {
        if (drlxExpr instanceof NullSafeFieldAccessExpr) {
            addNullSafeExpression( (( NullSafeFieldAccessExpr ) drlxExpr).getScope() );
        } else if (drlxExpr instanceof NullSafeMethodCallExpr) {
            ((NullSafeMethodCallExpr) drlxExpr).getScope().ifPresent( this::addNullSafeExpression );
        } else if (drlxExpr instanceof FieldAccessExpr) {
            processNullSafeDereferencing( (( FieldAccessExpr ) drlxExpr).getScope() );
        } else if (drlxExpr instanceof MethodCallExpr && (( MethodCallExpr ) drlxExpr).getScope().isPresent()) {
            processNullSafeDereferencing( (( MethodCallExpr ) drlxExpr).getScope().orElseThrow(() -> new IllegalStateException("Scope expression is not present!")) );
        }
    }

    private void addNullSafeExpression( Expression scope ) {
        toTypedExpressionRec(scope).ifPresent(te -> context.addPrefixExpression(0, new BinaryExpr(te.getExpression(), new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS)));
    }

    private TypedExpressionCursor binaryExpr( BinaryExpr binaryExpr ) {
        TypedExpressionResult left = toTypedExpression( binaryExpr.getLeft() );
        binaryExpr.setLeft( left.getTypedExpression()
                                    .orElseThrow(() -> new NoSuchElementException("TypedExpressionResult doesn't contain TypedExpression!"))
                                    .getExpression() );
        TypedExpressionResult right = toTypedExpression( binaryExpr.getRight() );
        binaryExpr.setRight( right.getTypedExpression()
                                     .orElseThrow(() -> new NoSuchElementException("TypedExpressionResult doesn't contain TypedExpression!"))
                                     .getExpression() );
        return new TypedExpressionCursor( binaryExpr,
                                          left.getTypedExpression()
                                                  .orElseThrow(() -> new NoSuchElementException("TypedExpressionResult doesn't contain TypedExpression!"))
                                                  .getType() );
    }

    private Optional<TypedExpressionCursor> castExpr( CastExpr firstNode, Expression drlxExpr, List<Node> childNodes, boolean isInLineCast, java.lang.reflect.Type originalTypeCursor ) {
        try {
            Type type = firstNode.getType();
            Class<?> typeClass = ruleContext.getTypeResolver().resolveType( type.toString() );
            Optional<TypedExpressionCursor> result = processFirstNode( drlxExpr, childNodes, firstNode.getExpression(), isInLineCast, originalTypeCursor);
            return result.map(te -> {
                Expression exprWithInlineCast = addCastToExpression(type, te.expressionCursor, isInLineCast);
                return new TypedExpressionCursor(exprWithInlineCast, typeClass);
            });
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
    }

    private Class<?> originalTypeCursorFromInlineCast(InlineCastExpr inlineCast) {
        Class<?> originalTypeCursor;
        try {
            originalTypeCursor = ruleContext.getTypeResolver().resolveType(inlineCast.getType().toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return originalTypeCursor;
    }

    private TypedExpressionCursor stringLiteralExpr(StringLiteralExpr firstNode) {
        final Class<?> typeCursor = String.class;
        return new TypedExpressionCursor(firstNode, typeCursor);
    }

    private TypedExpressionCursor methodCallExpr(MethodCallExpr methodCallExpr, java.lang.reflect.Type originalTypeCursor, Expression scope) {
        methodCallExpr.setScope( scope );
        return parseMethodCallExpr(methodCallExpr, originalTypeCursor);
    }

    private TypedExpressionCursor nullSafeMethodCallExpr(NullSafeMethodCallExpr nullSafeMethodCallExpr, java.lang.reflect.Type originalTypeCursor, Expression scope) {
        MethodCallExpr methodCallExpr = new MethodCallExpr( scope, nullSafeMethodCallExpr.getName(), nullSafeMethodCallExpr.getArguments() );

        return parseMethodCallExpr(methodCallExpr, originalTypeCursor);
    }

    private TypedExpressionCursor parseMethodCallExpr(MethodCallExpr methodCallExpr, java.lang.reflect.Type originalTypeCursor) {
        Class<?> rawClassCursor = toRawClass(originalTypeCursor);
        String methodName = methodCallExpr.getNameAsString();
        Class[] argsType = parseNodeArguments(methodCallExpr);

        Optional<TypedExpressionCursor> startsWithMvel = checkStartsWithMVEL(methodCallExpr, originalTypeCursor, argsType);
        if(startsWithMvel.isPresent()) {
            return startsWithMvel.get();
        }

        Method m = rawClassCursor != null ? MethodUtils.findMethod(rawClassCursor, methodName, argsType) : null;
        if (m == null) {
            Optional<RuleContext.FunctionType> functionType = ruleContext.getFunctionType(methodName);
            if (functionType.isPresent()) {
                RuleContext.FunctionType typedDeclaredFunction = functionType.get();
                methodCallExpr.setScope(null);

                promoteBigDecimalParameters(methodCallExpr, argsType, typedDeclaredFunction.getArgumentsType().toArray(new Class[0]));

                return new TypedExpressionCursor(methodCallExpr, typedDeclaredFunction.getReturnType());
            }

            ruleContext.addCompilationError(new InvalidExpressionErrorResult(
                    String.format("Method %s on %s with arguments %s is missing", methodName, originalTypeCursor, Arrays.toString(argsType))));
            return new TypedExpressionCursor(methodCallExpr, Object.class);
        }

        Class<?>[] actualArgumentTypes = m.getParameterTypes();
        promoteBigDecimalParameters(methodCallExpr, argsType, actualArgumentTypes);

        if (methodName.equals("get") && List.class.isAssignableFrom(rawClassCursor) && originalTypeCursor instanceof ParameterizedType) {
            return new TypedExpressionCursor(methodCallExpr, ((ParameterizedType) originalTypeCursor).getActualTypeArguments()[0]);
        }

        java.lang.reflect.Type genericReturnType = m.getGenericReturnType();
        if (genericReturnType instanceof TypeVariable) {
            if (originalTypeCursor instanceof ParameterizedType) {
                return new TypedExpressionCursor( methodCallExpr, getActualType( rawClassCursor, ( ParameterizedType ) originalTypeCursor, ( TypeVariable ) genericReturnType ) );
            } else {
                return new TypedExpressionCursor(methodCallExpr, Object.class);
            }
        } else {
            return new TypedExpressionCursor(methodCallExpr, genericReturnType);
        }
    }

    private void promoteBigDecimalParameters(MethodCallExpr methodCallExpr, Class[] argsType, Class<?>[] actualArgumentTypes) {
        if (actualArgumentTypes.length == argsType.length && actualArgumentTypes.length == methodCallExpr.getArguments().size()) {
            for (int i = 0; i < argsType.length; i++) {
                Class<?> argumentType = argsType[i];
                Class<?> actualArgumentType = actualArgumentTypes[i];

                Expression argumentExpression = methodCallExpr.getArgument(i);

                if (argumentType != actualArgumentType) {
                    Expression coercedExpression = new BigDecimalArgumentCoercion().coercedArgument(argumentType, actualArgumentType, argumentExpression);
                    methodCallExpr.setArgument(i, coercedExpression);
                }
            }
        }
    }

    // MVEL allows startsWith with a single char instead of a String
    private Optional<TypedExpressionCursor> checkStartsWithMVEL(MethodCallExpr methodCallExpr, java.lang.reflect.Type originalTypeCursor, Class<?>[] argsType) {
        if (("startsWith".equals(methodCallExpr.getNameAsString()) || "endsWith".equals(methodCallExpr.getNameAsString()))
                && originalTypeCursor.equals(java.lang.String.class)
                && Arrays.equals(argsType, new Class[]{char.class})) {

            MethodCallExpr methodCallExprWithString = methodCallExpr.clone();
            methodCallExprWithString.findAll(CharLiteralExpr.class).forEach(c ->  c.replace(new StringLiteralExpr(c.getValue())));
            return Optional.of(new TypedExpressionCursor(methodCallExprWithString, boolean.class));
        } else {
            return Optional.empty();
        }
    }

    private java.lang.reflect.Type getActualType(Class<?> rawClassCursor, ParameterizedType originalTypeCursor, TypeVariable genericReturnType) {
        int genericPos = 0;
        for (TypeVariable typeVar : rawClassCursor.getTypeParameters()) {
            if (typeVar.equals( genericReturnType )) {
                return (( ParameterizedType ) originalTypeCursor).getActualTypeArguments()[genericPos];
            }
            genericPos++;
        }
        throw new RuntimeException( "Unknonw generic type " + genericReturnType + " for type " + originalTypeCursor );
    }

    private TypedExpressionCursor objectCreationExpr(ObjectCreationExpr objectCreationExpr) {
        parseNodeArguments( objectCreationExpr );
        return new TypedExpressionCursor(objectCreationExpr, getClassFromType(ruleContext.getTypeResolver(), objectCreationExpr.getType()));
    }

    private Class[] parseNodeArguments( NodeWithArguments<?> methodCallExpr ) {
        Class[] argsType = new Class[methodCallExpr.getArguments().size()];
        context.setRegisterPropertyReactivity( false );
        try {
            for (int i = 0; i < methodCallExpr.getArguments().size(); i++) {
                Expression arg = methodCallExpr.getArgument( i );
                TypedExpressionResult typedArgumentResult = toTypedExpressionFromMethodCallOrField( arg );
                Optional<TypedExpression> optTypedArgumentExpression = typedArgumentResult.getTypedExpression();
                if(optTypedArgumentExpression.isPresent()) {
                    TypedExpression typedArgumentExpression = optTypedArgumentExpression.get();
                    argsType[i] = toRawClass(typedArgumentExpression.getType() );
                    methodCallExpr.setArgument(i, typedArgumentExpression.getExpression() );
                } else {
                    argsType[i] = Object.class;
                    methodCallExpr.setArgument( i, arg );
                }
            }
        } finally {
            context.setRegisterPropertyReactivity( true );
        }
        return argsType;
    }

    private Optional<TypedExpressionCursor> mapCreationLiteral(MapCreationLiteralExpression mapCreationLiteralExpression, java.lang.reflect.Type originalTypeCursor) {
        ClassOrInterfaceType hashMapType = (ClassOrInterfaceType) parseType(HashMap.class.getCanonicalName());

        BlockStmt initializationStmt = new BlockStmt();

        InitializerDeclaration body = new InitializerDeclaration(false, initializationStmt);
        ObjectCreationExpr newHashMapExpr = new ObjectCreationExpr(null, hashMapType, nodeList(), nodeList(), nodeList(body));

        for(Expression e : mapCreationLiteralExpression.getExpressions()) {
            MapCreationLiteralExpressionKeyValuePair expr = (MapCreationLiteralExpressionKeyValuePair)e;

            Expression key = resolveCreationLiteralNameExpr(originalTypeCursor, expr.getKey());
            Expression value = resolveCreationLiteralNameExpr(originalTypeCursor, expr.getValue());

            initializationStmt.addStatement(new MethodCallExpr(null, "put", nodeList(key, value)));
        }

        return of(new TypedExpressionCursor(newHashMapExpr, HashMap.class));
    }

    private Expression resolveCreationLiteralNameExpr(java.lang.reflect.Type originalTypeCursor, Expression expression) {
        Expression result = expression;
        if (result instanceof DrlNameExpr) {
            TypedExpressionCursor typedExpressionCursor = drlNameExpr(null, (DrlNameExpr) result, false, originalTypeCursor)
                    .orElseThrow(() -> new RuntimeException("Cannot find field: " + expression));
            result = typedExpressionCursor.expressionCursor;
        }
        return result;
    }

    private Optional<TypedExpressionCursor> listCreationLiteral(ListCreationLiteralExpression listCreationLiteralExpression, java.lang.reflect.Type originalTypeCursor) {
        ClassOrInterfaceType arrayListType = (ClassOrInterfaceType) parseType(ArrayList.class.getCanonicalName());

        BlockStmt initializationStmt = new BlockStmt();

        InitializerDeclaration body = new InitializerDeclaration(false, initializationStmt);
        ObjectCreationExpr newArrayListExpr = new ObjectCreationExpr(null, arrayListType, nodeList(), nodeList(), nodeList(body));

        for(Expression e : listCreationLiteralExpression.getExpressions()) {
            ListCreationLiteralExpressionElement expr = (ListCreationLiteralExpressionElement)e;

            Expression value = resolveCreationLiteralNameExpr(originalTypeCursor, expr.getValue());

            initializationStmt.addStatement(new MethodCallExpr(null, "add", nodeList(value)));
        }

        return of(new TypedExpressionCursor(newArrayListExpr, ArrayList.class));
    }

    private Optional<TypedExpressionCursor> arrayAccessExpr(ArrayAccessExpr arrayAccessExpr, java.lang.reflect.Type originalTypeCursor, Expression scope) {
        final Expression expression = arrayAccessExpr.getName();
        final Optional<TypedExpressionCursor> expressionCursor;
        if (expression.isNameExpr() || expression.isFieldAccessExpr()) {
            expressionCursor = Optional.of(new TypedExpressionCursor(scope, originalTypeCursor));
        } else {
            expressionCursor = Optional.of(new TypedExpressionCursor(expression, originalTypeCursor));
        }

        TypedExpressionCursor nameExpr = expressionCursor.get();
        java.lang.reflect.Type arrayType = nameExpr.typeCursor;
        Class<?> rawClass = toRawClass( arrayType );
        TypedExpression indexExpr = toTypedExpressionFromMethodCallOrField( arrayAccessExpr.getIndex() ).getTypedExpression()
                .orElseThrow(() -> new NoSuchElementException("TypedExpressionResult doesn't contain TypedExpression!"));

        if (rawClass.isArray()) {
            ArrayAccessExpr result = new ArrayAccessExpr( nameExpr.expressionCursor, indexExpr.getExpression() );
            return of(new TypedExpressionCursor( result, rawClass.getComponentType() ));
        } else if (List.class.isAssignableFrom( rawClass ) || Map.class.isAssignableFrom( rawClass )) {
            MethodCallExpr result = new MethodCallExpr( nameExpr.expressionCursor, "get" );
            result.addArgument( indexExpr.getExpression() );
            return of(new TypedExpressionCursor( result, getTypeArgument( arrayType, List.class.isAssignableFrom( rawClass ) ? 0 : 1) ));
        }

        return empty();
    }

    private TypedExpressionCursor arrayCreationExpr(ArrayCreationExpr arrayCreationExpr) {
        Optional<ArrayInitializerExpr> optInit = arrayCreationExpr.getInitializer();
        if (optInit.isPresent()) {
            NodeList<Expression> values = optInit.get().getValues();
            for (int i = 0; i < values.size(); i++) {
                values.set( i,
                            toTypedExpressionFromMethodCallOrField( values.get(i) )
                                    .getTypedExpression()
                                    .orElseThrow(() -> new NoSuchElementException("TypedExpressionResult doesn't contain TypedExpression!"))
                                    .getExpression() );
            }
        }

        Class<?> type = getClassFromContext(ruleContext.getTypeResolver(), arrayCreationExpr.getElementType().asString() + "[]");
        return new TypedExpressionCursor(arrayCreationExpr, type);
    }

    private TypedExpressionCursor fieldAccessExpr(java.lang.reflect.Type originalTypeCursor, SimpleName firstNodeName) {
        TypedExpressionCursor teCursor;
        final java.lang.reflect.Type tc4 = originalTypeCursor;
        String firstName = firstNodeName.getIdentifier();
        Method firstAccessor = DrlxParseUtil.getAccessor(toRawClass(tc4), firstName);
        if (firstAccessor != null) {
            context.addReactOnProperties(firstName);
            teCursor = new TypedExpressionCursor(new MethodCallExpr(new NameExpr(THIS_PLACEHOLDER), firstAccessor.getName()), firstAccessor.getGenericReturnType());
        } else {
            throw new UnsupportedOperationException("firstNode I don't know about");
            // TODO would it be fine to assume is a global if it's not in the declarations and not the first accesssor in a chain?
        }
        return teCursor;
    }

    private Optional<TypedExpressionCursor> drlNameExpr(Expression drlxExpr, DrlNameExpr firstNode, boolean isInLineCast, java.lang.reflect.Type originalTypeCursor) {
        String firstName = firstNode.getName().getIdentifier();
        java.lang.reflect.Type typeCursor;

        // In OOPath a declaration is based on a position rather than a name.
        // Only an OOPath chunk can have a backreference expression
        Optional<DeclarationSpec> backReference = empty();
        if ( firstNode.getBackReferencesCount() > 0) {
            List<DeclarationSpec> ooPathDeclarations = ruleContext.getOOPathDeclarations();
            DeclarationSpec backReferenceDeclaration = ooPathDeclarations.get(ooPathDeclarations.size() - 1 - firstNode.getBackReferencesCount());
            typeCursor = backReferenceDeclaration.getDeclarationClass();
            backReference = of(backReferenceDeclaration);
            context.addUsedDeclarations(backReferenceDeclaration.getBindingId());
        } else {
            typeCursor = originalTypeCursor;
        }

        try {
            Class<?> resolvedType = ruleContext.getTypeResolver().resolveType( firstName );
            return of( new TypedExpressionCursor( new NameExpr(firstName), resolvedType ));
        } catch (ClassNotFoundException e) {
            // ignore
        }

        Class<?> classCursor = toRawClass(typeCursor);
        if ( classCursor != null ) {
            Method firstAccessor = DrlxParseUtil.getAccessor( !isInLineCast ? classCursor : patternType, firstName );
            if ( firstAccessor != null ) {
                if ( !"".equals( firstName ) ) {
                    context.addReactOnProperties( firstName );
                }

                NameExpr thisAccessor = new NameExpr( THIS_PLACEHOLDER );
                NameExpr scope = backReference.map( d -> new NameExpr( d.getBindingId() ) ).orElse( thisAccessor );

                Expression fieldAccessor = new MethodCallExpr(scope, firstAccessor.getName());

                if (isInLineCast) {
                    return of(new TypedExpressionCursor(fieldAccessor, typeCursor ) );
                }

                Optional<java.lang.reflect.Type> castType = ruleContext.explicitCastType(firstName)
                        .flatMap(t -> safeResolveType(ruleContext.getTypeResolver(), t.asString()));

                if (castType.isPresent()) {
                    java.lang.reflect.Type typeOfFirstAccessor = castType.get();
                    ClassOrInterfaceType typeWithoutDollar = toClassOrInterfaceType(typeOfFirstAccessor.getTypeName());
                    return of(new TypedExpressionCursor(addCastToExpression(typeWithoutDollar, fieldAccessor, false), typeOfFirstAccessor ) );
                }

                return of(new TypedExpressionCursor(fieldAccessor, firstAccessor.getGenericReturnType() ) );
            }

            Field field = DrlxParseUtil.getField( classCursor, firstName );
            if ( field != null ) {
                NameExpr scope = new NameExpr( Modifier.isStatic( field.getModifiers() ) ? classCursor.getCanonicalName() : THIS_PLACEHOLDER );
                return of( new TypedExpressionCursor( new FieldAccessExpr( scope, field.getName() ), field.getType() ) );
            }
        }

        Optional<DeclarationSpec> declarationById = ruleContext.getDeclarationById(firstName);
        if (declarationById.isPresent()) {
            // do NOT append any reactOnProperties.
            // because reactOnProperties is referring only to the properties of the type of the pattern, not other declarations properites.
            context.addUsedDeclarations(firstName);
            typeCursor = isInLineCast ? originalTypeCursor : declarationById.get().getDeclarationClass();
            return of(new TypedExpressionCursor(new NameExpr(firstName), typeCursor));
        }

        if (packageModel.getGlobals().containsKey(firstName)) {
            context.addUsedDeclarations(firstName);
            return of(new TypedExpressionCursor(new NameExpr(firstName), packageModel.getGlobals().get(firstName)));
        }

        final Optional<Node> rootNode = findRootNodeViaParent(drlxExpr);
        rootNode.ifPresent(n -> {
            // In the error messages HalfBinary are transformed to Binary
            Node withHalfBinaryReplaced = replaceAllHalfBinaryChildren(n);
            ruleContext.addCompilationError(new ParseExpressionErrorResult((Expression) withHalfBinaryReplaced, ruleContext.getCurrentConstraintDescr()));
        });
        return empty();
    }

    private TypedExpressionCursor thisExpr(Expression drlxExpr, List<Node> childNodes, boolean isInLineCast, java.lang.reflect.Type originalTypeCursor) {
        TypedExpressionCursor teCursor;
        if (childNodes.size() > 1 && !isInLineCast) {
            SimpleName fieldName = null;
            if (childNodes.get(1) instanceof NameExpr) {
                fieldName = (( NameExpr ) childNodes.get(1 )).getName();
            } else if (childNodes.get(1) instanceof SimpleName) {
                fieldName = ( SimpleName ) childNodes.get(1 );
            }
            if (fieldName != null) {
                context.addReactOnProperties( getFieldName(drlxExpr, fieldName ) );
            }
        }
        teCursor = new TypedExpressionCursor(new NameExpr(THIS_PLACEHOLDER), originalTypeCursor);
        return teCursor;
    }

    private Expression addCastToExpression(Class<?> typeCursor, Expression previous, boolean isInLineCast) {
        ReferenceType castType = toClassOrInterfaceType(typeCursor.getName());
        return addCastToExpression( castType, previous, isInLineCast );
    }

    private Expression addCastToExpression( Type castType, Expression previous, boolean isInLineCast ) {
        if (isInLineCast) {
            context.addPrefixExpression( new InstanceOfExpr( previous, ( ReferenceType ) castType ) );
        }
        previous = new EnclosedExpr(new CastExpr(castType, previous));
        return previous;
    }

    private static String getFieldName( Expression drlxExpr, SimpleName fieldName ) {
        if ( drlxExpr instanceof MethodCallExpr ) {
            String name = getter2property( fieldName.getIdentifier() );
            if ( name != null ) {
                return name;
            }
        }
        return fieldName.getIdentifier();
    }


    public static Expression findLeftLeafOfNameExpr(Node expression) {
        if (expression instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr) expression;
            return findLeftLeafOfNameExpr(be.getLeft());
        } else if (expression instanceof PointFreeExpr) {
            return findLeftLeafOfNameExpr(((PointFreeExpr) expression).getLeft());
        } else {
            return (Expression) expression;
        }
    }

    public static class TypedExpressionCursor {
        public final Expression expressionCursor;
        public final java.lang.reflect.Type typeCursor;

        public TypedExpressionCursor(Expression expressionCursor, java.lang.reflect.Type typeCursor) {
            this.expressionCursor = expressionCursor;
            this.typeCursor = typeCursor;
        }

        @Override
        public String toString() {
            return "TypedExpressionCursor{" +
                    "expressionCursor=" + expressionCursor +
                    ", typeCursor=" + typeCursor +
                    '}';
        }
    }
}
