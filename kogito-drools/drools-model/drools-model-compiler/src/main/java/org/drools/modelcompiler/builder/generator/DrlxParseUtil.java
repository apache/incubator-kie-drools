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

package org.drools.modelcompiler.builder.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.drools.core.util.ClassUtils;
import org.drools.core.util.index.IndexUtil;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.drools.drlx.DrlxParser;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.drlx.expr.DrlxExpression;
import org.drools.javaparser.ast.drlx.expr.HalfBinaryExpr;
import org.drools.javaparser.ast.drlx.expr.HalfPointFreeExpr;
import org.drools.javaparser.ast.drlx.expr.InlineCastExpr;
import org.drools.javaparser.ast.drlx.expr.NullSafeFieldAccessExpr;
import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.expr.ArrayAccessExpr;
import org.drools.javaparser.ast.expr.ArrayCreationExpr;
import org.drools.javaparser.ast.expr.AssignExpr;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.BinaryExpr.Operator;
import org.drools.javaparser.ast.expr.BooleanLiteralExpr;
import org.drools.javaparser.ast.expr.CastExpr;
import org.drools.javaparser.ast.expr.CharLiteralExpr;
import org.drools.javaparser.ast.expr.DoubleLiteralExpr;
import org.drools.javaparser.ast.expr.EnclosedExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.InstanceOfExpr;
import org.drools.javaparser.ast.expr.IntegerLiteralExpr;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.LiteralExpr;
import org.drools.javaparser.ast.expr.LongLiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.NullLiteralExpr;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.expr.SimpleName;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.ThisExpr;
import org.drools.javaparser.ast.expr.UnaryExpr;
import org.drools.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import org.drools.javaparser.ast.nodeTypes.NodeWithSimpleName;
import org.drools.javaparser.ast.nodeTypes.NodeWithTraversableScope;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.PrimitiveType;
import org.drools.javaparser.ast.type.ReferenceType;
import org.drools.javaparser.ast.type.Type;
import org.drools.javaparser.ast.type.UnknownType;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.ParseExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.operatorspec.CustomOperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.OperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.TemporalOperatorSpec;
import org.drools.modelcompiler.util.ClassUtil;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static java.util.Optional.of;

import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.javaparser.printer.PrintUtil.toDrlx;
import static org.drools.modelcompiler.util.ClassUtil.findMethod;

public class DrlxParseUtil {

    public static final NameExpr _THIS_EXPR = new NameExpr("_this");


    public static IndexUtil.ConstraintType toConstraintType(Operator operator) {
        switch (operator) {
            case EQUALS:
                return ConstraintType.EQUAL;
            case NOT_EQUALS:
                return ConstraintType.NOT_EQUAL;
            case GREATER:
                return ConstraintType.GREATER_THAN;
            case GREATER_EQUALS:
                return ConstraintType.GREATER_OR_EQUAL;
            case LESS:
                return ConstraintType.LESS_THAN;
            case LESS_EQUALS:
                return ConstraintType.LESS_OR_EQUAL;
            default:
                return ConstraintType.UNKNOWN;
        }
    }

    public static Optional<TypedExpression> toTypedExpression(RuleContext context, PackageModel packageModel, Class<?> patternType, Expression drlxExpr, String bindingId,
                                                    List<String> usedDeclarations, Set<String> reactOnProperties, Expression parentExpression, boolean isPositional, List<Expression> prefixExpresssions) {

        Class<?> typeCursor = patternType;

        if(drlxExpr instanceof EnclosedExpr) {
            drlxExpr = ((EnclosedExpr) drlxExpr).getInner();
        }

        if (drlxExpr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr) drlxExpr;
            Optional<TypedExpression> optTypedExpr = toTypedExpression(context, packageModel, patternType, unaryExpr.getExpression(), bindingId, usedDeclarations, reactOnProperties, unaryExpr, isPositional, prefixExpresssions);
            return optTypedExpr.map(typedExpr -> new TypedExpression( new UnaryExpr( typedExpr.getExpression(), unaryExpr.getOperator() ), typedExpr.getType() ));

        } else if (drlxExpr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) drlxExpr;

            Operator operator = binaryExpr.getOperator();

            Optional<TypedExpression> optLeft = DrlxParseUtil.toTypedExpression(context, packageModel, patternType, binaryExpr.getLeft(), bindingId, usedDeclarations, reactOnProperties, binaryExpr, isPositional, prefixExpresssions);
            Optional<TypedExpression> optRight = DrlxParseUtil.toTypedExpression(context, packageModel, patternType, binaryExpr.getRight(), bindingId, usedDeclarations, reactOnProperties, binaryExpr, isPositional, prefixExpresssions);

            return optLeft.flatMap(left -> optRight.flatMap(right -> {
                final BinaryExpr combo = new BinaryExpr(left.getExpression(), right.getExpression(), operator);
                return of(new TypedExpression(combo, left.getType()));
            }));

        } else if (drlxExpr instanceof HalfBinaryExpr) {
            final Expression binaryExpr = trasformHalfBinaryToBinary(drlxExpr);
            return toTypedExpression(context, packageModel, patternType, binaryExpr, bindingId, usedDeclarations, reactOnProperties, parentExpression, isPositional, prefixExpresssions);

        } else if (drlxExpr instanceof LiteralExpr) {
            return of(new TypedExpression(drlxExpr, getLiteralExpressionType( ( LiteralExpr ) drlxExpr )));

        } else if (drlxExpr instanceof ThisExpr) {
            return of(new TypedExpression(new NameExpr("_this"), patternType));

        } else if (drlxExpr instanceof CastExpr) {
            CastExpr castExpr = (CastExpr)drlxExpr;
            toTypedExpression( context, packageModel, patternType, castExpr.getExpression(), bindingId, usedDeclarations, reactOnProperties, castExpr, isPositional, prefixExpresssions);
            return of(new TypedExpression(castExpr, getClassFromContext(context.getPkg().getTypeResolver(), castExpr.getType().asString())));

        } else if (drlxExpr instanceof NameExpr) {
            String name = drlxExpr.toString();
            Optional<DeclarationSpec> decl = context.getDeclarationById(name);
            if (decl.isPresent()) {
                // then drlxExpr is a single NameExpr referring to a binding, e.g.: "$p1".
                usedDeclarations.add(name);
                return of(new TypedExpression(drlxExpr, decl.get().getDeclarationClass()));
            } if (context.getQueryParameters().stream().anyMatch(qp -> qp.name.equals(name))) {
                // then drlxExpr is a single NameExpr referring to a query parameter, e.g.: "$p1".
                usedDeclarations.add(name);
                return of(new TypedExpression(drlxExpr));
            } else if(packageModel.getGlobals().containsKey(name)){
                Expression plusThis = new NameExpr(name);
                usedDeclarations.add(name);
                return of(new TypedExpression(plusThis, packageModel.getGlobals().get(name)));
            } else {
                TypedExpression expression;
                try {
                    expression = nameExprToMethodCallExpr(name, typeCursor, null);
                } catch (IllegalArgumentException e) {
                    if (isPositional || context.getQueryName().isPresent()) {
                        String unificationVariable = context.getOrCreateUnificationId(name);
                        expression = new TypedExpression(unificationVariable, typeCursor, name);
                        return of(expression);
                    }
                    return Optional.empty();
                }
                reactOnProperties.add(name);
                Expression plusThis = prepend(new NameExpr("_this"), expression.getExpression());
                return of(new TypedExpression(plusThis, expression.getType(), name));
            }
        } else if (drlxExpr instanceof FieldAccessExpr || drlxExpr instanceof MethodCallExpr) {
            return toTypedExpressionFromMethodCallOrField(context, patternType, drlxExpr, bindingId, usedDeclarations, reactOnProperties, context.getPkg().getTypeResolver(), prefixExpresssions);
        } else if (drlxExpr instanceof PointFreeExpr) {

            final PointFreeExpr pointFreeExpr = (PointFreeExpr)drlxExpr;

            Optional<TypedExpression> optLeft = DrlxParseUtil.toTypedExpression(context, packageModel, patternType, pointFreeExpr.getLeft(), bindingId, usedDeclarations, reactOnProperties, pointFreeExpr, isPositional, prefixExpresssions);
            OperatorSpec opSpec = getOperatorSpec(context, packageModel, patternType, drlxExpr, bindingId, usedDeclarations, reactOnProperties, isPositional, pointFreeExpr, pointFreeExpr.getRight(), pointFreeExpr.getOperator());

            return optLeft.map(left -> {
                return new TypedExpression(opSpec.getExpression( pointFreeExpr, left ), left.getType())
                        .setStatic(opSpec.isStatic())
                        .setLeft(left);
            });

        } else if (drlxExpr instanceof HalfPointFreeExpr) {

            final HalfPointFreeExpr halfPointFreeExpr = (HalfPointFreeExpr)drlxExpr;
            Expression parentLeft = findLeftLeafOfNameExpr(parentExpression);

            Optional<TypedExpression> optLeft = DrlxParseUtil.toTypedExpression(context, packageModel, patternType, parentLeft, bindingId, usedDeclarations, reactOnProperties, halfPointFreeExpr, isPositional, prefixExpresssions);
            OperatorSpec opSpec = getOperatorSpec(context, packageModel, patternType, drlxExpr, bindingId, usedDeclarations, reactOnProperties, isPositional, halfPointFreeExpr, halfPointFreeExpr.getRight(), halfPointFreeExpr.getOperator());

            final PointFreeExpr transformedToPointFree =
                    new PointFreeExpr(halfPointFreeExpr.getTokenRange().get(),
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
                                       new TypedExpression(opSpec.getExpression(transformedToPointFree, left ), left.getType())
                    .setStatic(opSpec.isStatic())
                    .setLeft(left));
        }

        throw new UnsupportedOperationException();
    }

    private static OperatorSpec getOperatorSpec(RuleContext context, PackageModel packageModel, Class<?> patternType, Expression drlxExpr, String bindingId, List<String> usedDeclarations, Set<String> reactOnProperties, boolean isPositional, Expression pointFreeExpr, NodeList<Expression> rightExpressions, SimpleName expressionOperator) {
        for (Expression rightExpr : rightExpressions) {
            DrlxParseUtil.toTypedExpression(context, packageModel, patternType, rightExpr, bindingId, usedDeclarations, reactOnProperties, pointFreeExpr, isPositional, new ArrayList<>());
        }

        String operator = expressionOperator.asString();
        OperatorSpec opSpec = null;
        if (ModelGenerator.temporalOperators.contains(operator )) {
            opSpec = TemporalOperatorSpec.INSTANCE;
        } else if ( org.drools.model.functions.Operator.Register.hasOperator( operator ) ) {
            opSpec = CustomOperatorSpec.INSTANCE;
        }
        if (opSpec == null) {
            throw new UnsupportedOperationException("Unknown operator '" + operator + "' in expression: " + toDrlx(drlxExpr));
        }
        return opSpec;
    }

    public static Optional<TypedExpression> toTypedExpressionFromMethodCallOrField(RuleContext context, Class<?> patternType, Expression drlxExpr, String bindingId, Collection<String> usedDeclarations, Set<String> reactOnProperties, TypeResolver typeResolver, List<Expression> prefixExpresssions) {
        Class<?> typeCursor = patternType;

        List<Node> childNodes = flattenScope(drlxExpr);
        Node firstNode = childNodes.get(0);

        boolean isInLineCast = firstNode instanceof InlineCastExpr;
        if (isInLineCast) {
            InlineCastExpr inlineCast = (InlineCastExpr) firstNode;
            try {
                typeCursor = typeResolver.resolveType(inlineCast.getType().toString());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            firstNode = inlineCast.getExpression();
        }

        Expression previous;

        if (firstNode instanceof ThisExpr || (firstNode instanceof NameExpr && firstNode.toString().equals( bindingId ))) {
            previous = new NameExpr("_this");
            if (childNodes.size() > 1 && !isInLineCast) {
                SimpleName fieldName = null;
                if (childNodes.get(1) instanceof NameExpr) {
                    fieldName = (( NameExpr ) childNodes.get( 1 )).getName();
                } else if (childNodes.get(1) instanceof SimpleName) {
                    fieldName = ( SimpleName ) childNodes.get( 1 );
                }
                if (fieldName != null) {
                    reactOnProperties.add( getFieldName( drlxExpr, fieldName ) );
                }
            }
        } else if (firstNode instanceof NameExpr) {
            NameExpr firstNodeName = (NameExpr) firstNode;
            String firstName = firstNodeName.getName().getIdentifier();
            Optional<DeclarationSpec> declarationById = context.getDeclarationById(firstName);
            if (declarationById.isPresent()) {
                // do NOT append any reactOnProperties.
                // because reactOnProperties is referring only to the properties of the type of the pattern, not other declarations properites.
                usedDeclarations.add(firstName);
                if (!isInLineCast) {
                    typeCursor = declarationById.get().getDeclarationClass();
                }
                previous = new NameExpr(firstName);
            } else {

                // In OOPath a declaration is based on a position rather than a name.
                // Only an OOPath chunk can have a backreference expression
                Optional<DeclarationSpec> backReference = Optional.empty();
                if(firstNodeName.getBackReferencesCount()  > 0) {
                    List<DeclarationSpec> ooPathDeclarations = context.getOOPathDeclarations();
                    DeclarationSpec backReferenceDeclaration = ooPathDeclarations.get(ooPathDeclarations.size() - 1 - firstNodeName.getBackReferencesCount());
                    typeCursor = backReferenceDeclaration.getDeclarationClass();
                    backReference = of(backReferenceDeclaration);
                    usedDeclarations.add(backReferenceDeclaration.getBindingId());
                }

                Method firstAccessor = ClassUtils.getAccessor((!isInLineCast) ? typeCursor : patternType, firstName);
                if (firstAccessor != null) {
                    // Hack to review - if a property is upper case it's probably not a react on property
                    if(!"".equals(firstName) && Character.isLowerCase(firstName.charAt(0))) {
                        reactOnProperties.add(firstName);
                    }
                    if (!isInLineCast) {
                        typeCursor = firstAccessor.getReturnType();
                    }
                    NameExpr thisAccessor = new NameExpr("_this");
                    final NameExpr scope = backReference.map(d -> new NameExpr(d.getBindingId())).orElse(thisAccessor);
                    previous = new MethodCallExpr(scope, firstAccessor.getName());
                } else {
                    final Optional<Node> rootNode = findRootNodeViaParent(drlxExpr);
                    rootNode.ifPresent(n -> {
                        // In the error messages HalfBinary are transformed to Binary
                        Node withHalfBinaryReplaced = replaceAllHalfBinaryChildren(n);
                        context.addCompilationError(new ParseExpressionErrorResult((Expression) withHalfBinaryReplaced));
                    });
                    return Optional.empty();
                }
            }
        } else if (firstNode instanceof FieldAccessExpr && ((FieldAccessExpr) firstNode).getScope() instanceof ThisExpr) {
            String firstName = ((FieldAccessExpr) firstNode).getName().getIdentifier();
            Method firstAccessor = ClassUtils.getAccessor(typeCursor, firstName);
            if (firstAccessor != null) {
                reactOnProperties.add(firstName);
                typeCursor = firstAccessor.getReturnType();
                previous = new MethodCallExpr(new NameExpr("_this"), firstAccessor.getName());
            } else {
                throw new UnsupportedOperationException("firstNode I don't know about");
                // TODO would it be fine to assume is a global if it's not in the declarations and not the first accesssor in a chain?
            }
        } else if (firstNode instanceof SimpleName) {
            previous = new NameExpr("_this");
            SimpleName fieldName = ( SimpleName ) firstNode;
            String name = getFieldName( drlxExpr, fieldName );
            reactOnProperties.add( name );
            TypedExpression expression = nameExprToMethodCallExpr(name, typeCursor, null);
            Expression plusThis = prepend(new NameExpr("_this"), expression.getExpression());
            if (childNodes.size() != 1) {
                throw new UnsupportedOperationException("then the below should not be a return");
            }
            return of(new TypedExpression(plusThis, expression.getType()));
        } else if (firstNode instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) firstNode;
            previous = new NameExpr("_this");
            methodCallExpr.setScope(previous);
            typeCursor = returnTypeOfMethodCallExpr(context, typeResolver, methodCallExpr, typeCursor, usedDeclarations);
            previous = methodCallExpr;
        } else if (firstNode instanceof StringLiteralExpr) {
            typeCursor = String.class;
            previous = (( StringLiteralExpr ) firstNode);
        } else {
            throw new UnsupportedOperationException("Unknown node: " + firstNode);
        }

        childNodes = childNodes.subList(1, childNodes.size());

        TypedExpression typedExpression = new TypedExpression();
        if (isInLineCast) {
            ReferenceType castType = JavaParser.parseClassOrInterfaceType(typeCursor.getName());
            prefixExpresssions.add(new InstanceOfExpr(previous, castType));
            previous = new EnclosedExpr(new CastExpr(castType, previous));
        }
        if (drlxExpr instanceof NullSafeFieldAccessExpr) {
            final BinaryExpr prefixExpression = new BinaryExpr(previous, new NullLiteralExpr(), Operator.NOT_EQUALS);
            prefixExpresssions.add(prefixExpression);

            final Expression scope = ((NullSafeFieldAccessExpr) drlxExpr).getScope();
            if(scope != null) {
                final Optional<TypedExpression> typedExpression1 = DrlxParseUtil.toTypedExpression(context, context.getPackageModel(), patternType, scope, bindingId, new ArrayList<>(), reactOnProperties, drlxExpr, false, prefixExpresssions);
                typedExpression1.ifPresent(te -> {
                    final Expression expression = te.getExpression();
                    final BinaryExpr notNullScope = new BinaryExpr(expression, new NullLiteralExpr(), Operator.NOT_EQUALS);
                    prefixExpresssions.add(0, notNullScope);
                });
            }
        }

        for (Node part : childNodes) {
            if(typeCursor.isEnum()) {
                previous = drlxExpr;
            } else if (part instanceof SimpleName) {
                String field = part.toString();
                TypedExpression expression = nameExprToMethodCallExpr(field, typeCursor, previous);
                typeCursor = expression.getType();
                previous = expression.getExpression();
            } else if (part instanceof MethodCallExpr) {
                MethodCallExpr methodCallExprPart = (MethodCallExpr) part;
                typeCursor = returnTypeOfMethodCallExpr(context, typeResolver, (MethodCallExpr) part, typeCursor, usedDeclarations);
                methodCallExprPart.setScope(previous);
                previous = methodCallExprPart;
            } else {
                throw new UnsupportedOperationException();
            }
        }

        return of(typedExpression.setExpression(previous).setType(typeCursor));
    }

    private static Expression findLeftLeafOfNameExpr(Expression expression) {
        if(expression instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr)expression;
            return findLeftLeafOfNameExpr(be.getLeft());
        } else if(expression instanceof NameExpr) {
            return expression;
        } else if(expression instanceof ThisExpr) {
            return expression;
        } else if(expression instanceof PointFreeExpr) {
            return findLeftLeafOfNameExpr(((PointFreeExpr) expression).getLeft());
        } else {
            throw new UnsupportedOperationException("Unknown expression: " + expression);
        }
    }

    public static Expression findLeftLeafOfMethodCall(Expression expression) {
        if(expression instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr)expression;
            return findLeftLeafOfMethodCall(be.getLeft());
        } else if(expression instanceof MethodCallExpr) {
            return expression;
        } else {
            throw new UnsupportedOperationException("Unknown expression: " + expression);
        }
    }

    private static Operator toBinaryExprOperator(HalfBinaryExpr.Operator operator) {
        return Operator.valueOf(operator.name());
    }
    private static List<Node> flattenScope(Expression expressionWithScope) {
        List<Node> res = new ArrayList<>();
        if (expressionWithScope instanceof FieldAccessExpr) {
            FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) expressionWithScope;
            res.addAll(flattenScope(fieldAccessExpr.getScope()));
            res.add(fieldAccessExpr.getName());
        } else if (expressionWithScope instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionWithScope;
            if (methodCallExpr.getScope().isPresent()) {
                res.addAll(flattenScope(methodCallExpr.getScope().get()));
            }
            res.add(methodCallExpr.setScope(null));
        } else {
            res.add(expressionWithScope);
        }
        return res;
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

    public static TypedExpression nameExprToMethodCallExpr(String name, Class<?> clazz, Expression scope) {
        Method accessor = ClassUtils.getAccessor(clazz, name);
        if (accessor != null) {
            MethodCallExpr body = new MethodCallExpr( scope, accessor.getName() );
            return new TypedExpression( body, accessor.getReturnType() );
        }
        try {
            Field field = clazz.getField( name );
            FieldAccessExpr expr = new FieldAccessExpr( scope, name );
            return new TypedExpression( expr, field.getType() );
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException( "Unknown field " + name + " on " + clazz );
        }
    }

    public static Class<?> returnTypeOfMethodCallExpr(RuleContext context, TypeResolver typeResolver, MethodCallExpr methodCallExpr, Class<?> clazz, Collection<String> usedDeclarations) {
        final Class[] argsType = methodCallExpr.getArguments().stream()
                .map((Expression e) -> getExpressionType(context, typeResolver, e, usedDeclarations))
                .toArray(Class[]::new);
        return findMethod(clazz, methodCallExpr.getNameAsString(), argsType).getReturnType();
    }

    public static Class<?> getExpressionType(RuleContext context, TypeResolver typeResolver, Expression expr, Collection<String> usedDeclarations) {
        if (expr instanceof LiteralExpr) {
            return getLiteralExpressionType( ( LiteralExpr ) expr );
        }

        if(expr instanceof ArrayAccessExpr) {
            return getClassFromContext(typeResolver, ((ArrayCreationExpr)((ArrayAccessExpr) expr).getName()).getElementType().asString());
        } else if(expr instanceof ArrayCreationExpr) {
            return getClassFromContext(typeResolver, ((ArrayCreationExpr) expr).getElementType().asString());
        } else if(expr instanceof NameExpr) {
            String name = (( NameExpr ) expr).getNameAsString();
            if (usedDeclarations != null) {
                usedDeclarations.add(name);
            }
            return context.getDeclarationById( name ).map( DeclarationSpec::getDeclarationClass ).get();
        } else if(expr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = ( MethodCallExpr ) expr;
            Class<?> scopeType = getExpressionType(context, typeResolver, methodCallExpr.getScope().get(), usedDeclarations);
            return returnTypeOfMethodCallExpr(context, typeResolver, methodCallExpr, scopeType, usedDeclarations);
        } else if(expr instanceof ObjectCreationExpr){
            final ClassOrInterfaceType type = ((ObjectCreationExpr) expr).getType();
            return getClassFromContext (typeResolver, type.asString());
        }else {
            throw new RuntimeException("Unknown expression type: " + expr);
        }
    }

    public static Class<?> getLiteralExpressionType( LiteralExpr expr ) {
        if (expr instanceof BooleanLiteralExpr) {
            return boolean.class;
        } else if (expr instanceof CharLiteralExpr) {
            return char.class;
        } else if (expr instanceof DoubleLiteralExpr) {
            return double.class;
        } else if (expr instanceof IntegerLiteralExpr) {
            return int.class;
        } else if (expr instanceof LongLiteralExpr) {
            return long.class;
        } else if (expr instanceof NullLiteralExpr) {
            return ClassUtil.NullType.class;
        } else if (expr instanceof StringLiteralExpr) {
            return String.class;
        }
        throw new RuntimeException("Unknown literal: " + expr);
    }

    public static Expression prepend(Expression scope, Expression expr) {
        final Optional<Expression> rootNode = findRootNodeViaScope(expr);

        if (!rootNode.isPresent()) {
            throw new UnsupportedOperationException("No root found");
        }

        rootNode.map(f -> {
            if (f instanceof NodeWithOptionalScope<?>) {
                ((NodeWithOptionalScope) f).setScope(scope);
            }
            return f;
        });

        return expr;
    }

    public static Optional<Node> findRootNodeViaParent(Node expr) {
        final Optional<Node> parentNode = expr.getParentNode();
        if(parentNode.isPresent()) {
            return findRootNodeViaParent(parentNode.get());
        } else {
            return Optional.of(expr);
        }
    }

    public static Node replaceAllHalfBinaryChildren(Node parent) {
        parent.getChildNodesByType(HalfBinaryExpr.class)
                .forEach(n -> n.replace(trasformHalfBinaryToBinary(n)));
        return parent;
    }

    public static Expression trasformHalfBinaryToBinary(Expression drlxExpr) {
        final Optional<Node> parent = drlxExpr.getParentNode();
        if(drlxExpr instanceof HalfBinaryExpr && parent.isPresent()) {

            HalfBinaryExpr halfBinaryExpr = (HalfBinaryExpr) drlxExpr;

            Expression parentLeft = findLeftLeafOfNameExpr((Expression) parent.get());
            Operator operator = toBinaryExprOperator(halfBinaryExpr.getOperator());
            return new BinaryExpr(parentLeft, halfBinaryExpr.getRight(), operator);
        }
        return drlxExpr;
    }

    public static Optional<Expression> findRootNodeViaScope(Expression expr) {

        if (expr instanceof NodeWithTraversableScope) {
            final NodeWithTraversableScope exprWithScope = (NodeWithTraversableScope) expr;

            return exprWithScope.traverseScope().map(DrlxParseUtil::findRootNodeViaScope).orElse(of(expr));
        } else if(expr instanceof NameExpr) {
            return of(expr);
        }

        return Optional.empty();
    }

    public static RemoveRootNodeResult removeRootNode(Expression expr) {
        Optional<Expression> rootNode = findRootNodeViaScope(expr);

        if(rootNode.isPresent()) {
            Expression root = rootNode.get();
            Optional<Node> parent = root.getParentNode();

            parent.ifPresent(p -> p.remove(root));

            return new RemoveRootNodeResult( rootNode, (Expression) parent.orElse(expr));
        }
        return new RemoveRootNodeResult(rootNode, expr);
    }

    public static class RemoveRootNodeResult {
        private Optional<Expression> rootNode;
        private Expression withoutRootNode;

        public RemoveRootNodeResult(Optional<Expression> rootNode, Expression withoutRootNode) {
            this.rootNode = rootNode;
            this.withoutRootNode = withoutRootNode;
        }

        public Optional<Expression> getRootNode() {
            return rootNode;
        }

        public Expression getWithoutRootNode() {
            return withoutRootNode;
        }
    }

    public static String toVar(String key) {
        return "var_" + key;
    }

    public static BlockStmt parseBlock(String ruleConsequenceAsBlock) {
        return JavaParser.parseBlock(String.format("{\n%s\n}", ruleConsequenceAsBlock)); // if the RHS is composed only of a line of comment like `//do nothing.` then JavaParser would fail to recognize the ending }
    }

    public static Expression generateLambdaWithoutParameters(Collection<String> usedDeclarations, Expression expr) {
        return generateLambdaWithoutParameters(usedDeclarations, expr, false);
    }

    public static Expression generateLambdaWithoutParameters(Collection<String> usedDeclarations, Expression expr, boolean skipFirstParamAsThis) {
        if (skipFirstParamAsThis && usedDeclarations.isEmpty()) {
            return expr;
        }
        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters( true );
        if (!skipFirstParamAsThis) {
            lambdaExpr.addParameter(new Parameter(new UnknownType(), "_this"));
        }
        usedDeclarations.stream().map( s -> new Parameter( new UnknownType(), s ) ).forEach( lambdaExpr::addParameter );
        lambdaExpr.setBody( new ExpressionStmt(expr ) );
        return lambdaExpr;
    }

    public static TypedExpression toMethodCallWithClassCheck(RuleContext context, Expression expr, String bindingId, Class<?> clazz, TypeResolver typeResolver) {

        final Deque<ParsedMethod> callStackLeftToRight = new LinkedList<>();

        createExpressionCall(expr, callStackLeftToRight);

        List<Expression> methodCall = new ArrayList<>();
        Class<?> previousClass = clazz;
        for (ParsedMethod e : callStackLeftToRight) {
            if (e.expression instanceof NameExpr || e.expression instanceof FieldAccessExpr) {
                if (e.fieldToResolve.equals( bindingId )) {
                    continue;
                }
                TypedExpression te = nameExprToMethodCallExpr(e.fieldToResolve, previousClass, null);
                Class<?> returnType = te.getType();
                methodCall.add(te.getExpression());
                previousClass = returnType;
            } else if (e.expression instanceof MethodCallExpr) {
                Class<?> returnType = returnTypeOfMethodCallExpr(context, typeResolver, (MethodCallExpr) e.expression, previousClass, null);
                MethodCallExpr cloned = ((MethodCallExpr) e.expression.clone()).removeScope();
                methodCall.add(cloned);
                previousClass = returnType;
            }
        }

        Expression call = methodCall.stream()
                .reduce((a, b) -> {
                    ((NodeWithOptionalScope) b).setScope(a);
                    return b;
                }).orElseThrow(() -> new UnsupportedOperationException("No Expression converted"));

        return new TypedExpression(call, previousClass);
    }

    private static Expression createExpressionCall(Expression expr, Deque<ParsedMethod> expressions) {

        if(expr instanceof NodeWithSimpleName) {
            NodeWithSimpleName fae = (NodeWithSimpleName)expr;
            expressions.push(new ParsedMethod(expr, fae.getName().asString()));
        }

        if (expr instanceof NodeWithOptionalScope) {
            final NodeWithOptionalScope<?> exprWithScope = (NodeWithOptionalScope) expr;

            exprWithScope.getScope().map((Expression scope) -> createExpressionCall(scope, expressions));
        } else if (expr instanceof FieldAccessExpr) {
            // Cannot recurse over getScope() as FieldAccessExpr doesn't support the NodeWithOptionalScope,
            // it will support a new interface to traverse among scopes called NodeWithTraversableScope so
            // we can merge this and the previous branch
            createExpressionCall(((FieldAccessExpr) expr).getScope(), expressions);
        }

        return expr;
    }

    static class ParsedMethod {
        final Expression expression;

        final String fieldToResolve;

        public ParsedMethod(Expression expression, String fieldToResolve) {
            this.expression = expression;
            this.fieldToResolve = fieldToResolve;
        }
        @Override
        public String toString() {
            return "{" +
                    "expression=" + expression +
                    ", fieldToResolve='" + fieldToResolve + '\'' +
                    '}';
        }
    }

    public static Type classToReferenceType(Class<?> declClass) {
        Type parsedType = JavaParser.parseType(declClass.getCanonicalName());
        return parsedType instanceof PrimitiveType ?
                ((PrimitiveType) parsedType).toBoxedType() :
                parsedType.getElementType();
    }

    public static Type toType(Class<?> declClass) {
        return JavaParser.parseType(declClass.getCanonicalName());
    }

    public static Optional<String> findBindingIdFromDotExpression(String expression) {
        int dot = expression.indexOf( '.' );
        if ( dot < 0 ) {
            return Optional.empty();
        }
        return of(expression.substring(0, dot));
    }

    public static DrlxExpression parseExpression(String expression) {
        return DrlxParser.parseExpression(DrlxParser.buildDrlxParserWithArguments(OperatorsHolder.operators), expression);
    }

    public static Class<?> getClassFromContext(TypeResolver typeResolver, String className) {
        Class<?> patternType;
        try {
            patternType = typeResolver.resolveType(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
        return patternType;
    }

    public static boolean isPrimitiveExpression(Expression expr) {
        if (!(expr instanceof LiteralExpr)) {
            return false;
        }
        return expr instanceof NullLiteralExpr || expr instanceof IntegerLiteralExpr || expr instanceof DoubleLiteralExpr ||
                expr instanceof BooleanLiteralExpr || expr instanceof LongLiteralExpr;
    }

    /**
     * Mutates expression
     * such that, if it contains a <pre>nameRef</pre>, it is replaced and forcibly casted with <pre>(type) nameRef</pre>.
     * 
     * @param expression a mutated expression
     */
    public static void forceCastForName(String nameRef, Type type, Expression expression) {
        List<NameExpr> allNameExprForName = expression.findAll(NameExpr.class, n -> n.getNameAsString().equals(nameRef));
        for (NameExpr n : allNameExprForName) {
            expression.replace(n, new CastExpr(type, n));
        }
    }

    /**
     * Mutates expression
     * such that, if it contains a NameExpr for any of the <code>names</code>,
     * it is replaced with a FieldAccessExpr having <code>newScope</code> as the scope.
     */
    public static void rescopeNamesToNewScope(Expression newScope, List<String> names, Expression e) {
        if (e instanceof AssignExpr) {
            AssignExpr assignExpr = (AssignExpr) e;
            rescopeNamesToNewScope(newScope, names, assignExpr.getTarget());
            rescopeNamesToNewScope(newScope, names, assignExpr.getValue());
        } else {
            Optional<Expression> rootNode = DrlxParseUtil.findRootNodeViaScope(e);
            if (rootNode.isPresent() && rootNode.get() instanceof NameExpr) {
                NameExpr nameExpr = (NameExpr) rootNode.get();
                if (names.contains(nameExpr.getNameAsString())) {
                    Expression prepend = new FieldAccessExpr(newScope, nameExpr.getNameAsString());
                    if (e instanceof NameExpr) {
                        e.getParentNode().get().replace(nameExpr, prepend); // actually `e` was not composite, it was already the NameExpr node I was looking to replace.
                    } else {
                        e.replace(nameExpr, prepend);
                    }
                }
            }
        }
    }

    static class OperatorsHolder {
        static final Collection<String> operators = getOperators();

        private static Collection<String> getOperators() {
            Collection<String> operators = new ArrayList<>();
            operators.addAll(org.drools.model.functions.Operator.Register.getOperators());
            operators.addAll(ModelGenerator.temporalOperators);
            return operators;
        }
    }
}
