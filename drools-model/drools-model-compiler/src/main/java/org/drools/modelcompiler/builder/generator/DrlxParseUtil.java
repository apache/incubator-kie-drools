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

import org.drools.core.util.ClassUtils;
import org.drools.core.util.index.IndexUtil;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.drlx.expr.InlineCastExpr;
import org.drools.javaparser.ast.drlx.expr.NullSafeFieldAccessExpr;
import org.drools.javaparser.ast.expr.*;
import org.drools.javaparser.ast.expr.BinaryExpr.Operator;
import org.drools.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import org.drools.javaparser.ast.nodeTypes.NodeWithSimpleName;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.type.PrimitiveType;
import org.drools.javaparser.ast.type.ReferenceType;
import org.drools.javaparser.ast.type.Type;
import org.drools.javaparser.ast.type.UnknownType;
import org.drools.modelcompiler.builder.PackageModel;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static org.drools.core.util.ClassUtils.getter2property;

public class DrlxParseUtil {

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
        }
        throw new UnsupportedOperationException("Unknown operator " + operator);
    }

    public static TypedExpression toTypedExpression(RuleContext context, PackageModel packageModel, Class<?> patternType, Expression drlxExpr,
                                                    Set<String> usedDeclarations, Set<String> reactOnProperties) {

        Class<?> typeCursor = patternType;

        if (drlxExpr instanceof LiteralExpr) {
            return new TypedExpression(drlxExpr, Optional.empty());
        } else if (drlxExpr instanceof ThisExpr) {
            return new TypedExpression(new NameExpr("_this"), Optional.empty());
        } else if (drlxExpr instanceof NameExpr) {
            String name = drlxExpr.toString();
            if (context.existsDeclaration(name)) {
                // then drlxExpr is a single NameExpr referring to a binding, e.g.: "$p1".
                usedDeclarations.add(name);
                return new TypedExpression(drlxExpr, Optional.empty());
            } if (context.queryParameters.stream().anyMatch(qp -> qp.name.equals(name))) {
                // then drlxExpr is a single NameExpr referring to a query parameter, e.g.: "$p1".
                usedDeclarations.add(name);
                return new TypedExpression(drlxExpr, Optional.empty());
            } else if(packageModel.getGlobals().containsKey(name)){
                Expression plusThis = new NameExpr(name);
                usedDeclarations.add(name);
                return new TypedExpression(plusThis, Optional.of(packageModel.getGlobals().get(name)));
            } else {
                reactOnProperties.add(name);
                TypedExpression expression = nameExprToMethodCallExpr(name, typeCursor);
                Expression plusThis = prepend(new NameExpr("_this"), (MethodCallExpr) expression.getExpression());
                return new TypedExpression(plusThis, expression.getType());
            }
        } else if (drlxExpr instanceof FieldAccessExpr || drlxExpr instanceof MethodCallExpr) {
            List<Node> childNodes = drlxExpr.getChildNodes();
            Node firstNode = childNodes.get(0);

            boolean isInLineCast = firstNode instanceof InlineCastExpr;
            if (isInLineCast) {
                InlineCastExpr inlineCast = (InlineCastExpr) firstNode;
                try {
                    typeCursor = context.getPkg().getTypeResolver().resolveType(inlineCast.getType().toString());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                firstNode = inlineCast.getExpression();
            }

            Expression previous;

            if (firstNode instanceof NameExpr) {
                String firstName = ((NameExpr) firstNode).getName().getIdentifier();
                Optional<DeclarationSpec> declarationById = context.getDeclarationById(firstName);
                if (declarationById.isPresent()) {
                    // do NOT append any reactOnProperties.
                    // because reactOnProperties is referring only to the properties of the type of the pattern, not other declarations properites.
                    usedDeclarations.add(firstName);
                    if (!isInLineCast) {
                        typeCursor = declarationById.get().declarationClass;
                    }
                    previous = new NameExpr(firstName);
                } else {
                    Method firstAccessor = ClassUtils.getAccessor(typeCursor, firstName);
                    if (firstAccessor != null) {
                        reactOnProperties.add(firstName);
                        typeCursor = firstAccessor.getReturnType();
                        previous = new MethodCallExpr(new NameExpr("_this"), firstAccessor.getName());
                    } else {
                        throw new UnsupportedOperationException("firstNode I don't know about");
                        // TODO would it be fine to assume is a global if it's not in the declarations and not the first accesssor in a chain?
                    }
                }
            } else if (firstNode instanceof ThisExpr) {
                previous = new NameExpr("_this");
                if (childNodes.size() > 1 && !isInLineCast) {
                    SimpleName fieldName = null;
                    if (childNodes.get(1) instanceof NameExpr) {
                        fieldName = (( NameExpr ) childNodes.get( 1 )).getName();
                    } else if (childNodes.get(1) instanceof SimpleName) {
                        fieldName = ( SimpleName ) childNodes.get( 1 );
                    }
                    if (fieldName != null) {
                        if (drlxExpr instanceof MethodCallExpr) {
                            reactOnProperties.add( getter2property( fieldName.getIdentifier() ) );
                        } else {
                            reactOnProperties.add( fieldName.getIdentifier() );
                        }
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
                String name = drlxExpr instanceof MethodCallExpr ? getter2property( fieldName.getIdentifier() ) : fieldName.getIdentifier();
                reactOnProperties.add( name );
                TypedExpression expression = nameExprToMethodCallExpr(name, typeCursor);
                Expression plusThis = prepend(new NameExpr("_this"), (MethodCallExpr) expression.getExpression());
                return new TypedExpression(plusThis, expression.getType());
            } else {
                throw new UnsupportedOperationException("Unknown node: " + firstNode);
            }

            childNodes = drlxExpr.getChildNodes().subList(1, drlxExpr.getChildNodes().size());

            TypedExpression typedExpression = new TypedExpression();
            if (isInLineCast) {
                ReferenceType castType = JavaParser.parseClassOrInterfaceType(typeCursor.getName());
                typedExpression.setPrefixExpression(new InstanceOfExpr(previous, castType));
                previous = new EnclosedExpr(new CastExpr(castType, previous));
            }
            if (drlxExpr instanceof NullSafeFieldAccessExpr) {
                typedExpression.setPrefixExpression(new BinaryExpr(previous, new NullLiteralExpr(), Operator.NOT_EQUALS));
            }

            for (Node part : childNodes) {
                String field = part.toString();
                Method accessor = ClassUtils.getAccessor(typeCursor, field);
                if (accessor == null) {
                    throw new IllegalStateException("Unknown field '" + field + "' on type " + typeCursor);
                }
                typeCursor = accessor.getReturnType();
                previous = new MethodCallExpr(previous, accessor.getName());
            }

            return typedExpression.setExpression(previous).setType(Optional.of(typeCursor));
        } else {
            // TODO the below should not be needed anymore...
            drlxExpr.getChildNodes();
            String expression = drlxExpr.toString();
            String[] parts = expression.split("\\.");
            StringBuilder telescoping = new StringBuilder();
            boolean implicitThis = true;

            for (int idx = 0; idx < parts.length; idx++) {
                String part = parts[idx];
                boolean isGlobal = false;
                if (isGlobal) {
                    implicitThis = false;
                    telescoping.append(part);
                } else if (idx == 0 && context.existsDeclaration(part)) {
                    implicitThis = false;
                    usedDeclarations.add(part);
                    telescoping.append(part);
                } else {
                    if ((idx == 0 && implicitThis) || (idx == 1 && implicitThis == false)) {
                        reactOnProperties.add(part);
                    }
                    Method accessor = ClassUtils.getAccessor(typeCursor, part);
                    typeCursor = accessor.getReturnType();
                    telescoping.append("." + accessor.getName() + "()");
                }
            }
            return new TypedExpression(implicitThis ? "_this" + telescoping.toString() : telescoping.toString(), Optional.of(typeCursor));
        }
    }

    public static TypedExpression nameExprToMethodCallExpr(String name, Class<?> clazz) {
        Method accessor = ClassUtils.getAccessor(clazz, name);
        if (accessor == null) {
            throw new UnsupportedOperationException("Accessor missing");
        }
        Class<?> accessorReturnType = accessor.getReturnType();

        MethodCallExpr body = new MethodCallExpr(null, accessor.getName());
        return new TypedExpression(body, Optional.of(accessorReturnType));
    }

    public static Class<?> returnTypeOfMethodCallExpr(MethodCallExpr methodCallExpr, Class<?> clazz) {
        // This currently works only if the arguments are strings
        final Class[] argsType = methodCallExpr.getArguments().stream()
                .map((Expression e) -> ((StringLiteralExpr)e).getValue())
                .map(String::getClass).toArray(Class[]::new);
        Method accessor;
        try {
            String methodName = methodCallExpr.getName().asString();
            accessor = clazz.getMethod(methodName, argsType);
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Method missing");
        }
        return accessor.getReturnType();
    }

    public static Expression prepend(Expression scope, Expression expr) {
        final Optional<Expression> rootNode = findRootNode(expr);

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

    private static Optional<Expression> findRootNode(Expression expr) {

        if (expr instanceof NodeWithOptionalScope) {
            final NodeWithOptionalScope<?> exprWithScope = (NodeWithOptionalScope) expr;

            return exprWithScope.getScope().map(DrlxParseUtil::findRootNode).orElse(Optional.of(expr));
        }

        return Optional.empty();
    }

    public static String toVar(String key) {
        return "var_" + key;
    }

    public static BlockStmt parseBlock(String ruleConsequenceAsBlock) {
        return JavaParser.parseBlock(String.format("{%s}", ruleConsequenceAsBlock));
    }

    public static Expression generateLambdaWithoutParameters(Set<String> usedDeclarations, Expression expr) {
        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters( true );
        lambdaExpr.addParameter( new Parameter(new UnknownType(), "_this" ) );
        usedDeclarations.stream().map( s -> new Parameter( new UnknownType(), s ) ).forEach( lambdaExpr::addParameter );
        lambdaExpr.setBody( new ExpressionStmt(expr ) );
        return lambdaExpr;
    }


    public static MethodCallExpr toMethodCallWithClassCheck(Expression expr, Class<?> clazz) {

        final Deque<ParsedMethod> callStackLeftToRight = new LinkedList<>();

        createExpressionCall(expr, callStackLeftToRight);

        List<Expression> methodCall = new ArrayList<>();
        Class<?> previousClass = clazz;
        for (ParsedMethod e : callStackLeftToRight) {
            if (e.expression instanceof NameExpr || e.expression instanceof FieldAccessExpr) {
                TypedExpression te = nameExprToMethodCallExpr(e.fieldToResolve, previousClass);
                Class<?> returnType = te.getType().orElseThrow(() -> new UnsupportedOperationException("Need return type here"));
                methodCall.add(te.getExpression());
                previousClass = returnType;
            } else if (e.expression instanceof MethodCallExpr) {
                Class<?> returnType = returnTypeOfMethodCallExpr((MethodCallExpr) e.expression, previousClass);
                MethodCallExpr cloned = ((MethodCallExpr) e.expression.clone()).removeScope();
                methodCall.add(cloned);
                previousClass = returnType;
            }
        }

        return (MethodCallExpr) methodCall.stream()
                .reduce((Expression a, Expression b) -> {
                    ((MethodCallExpr) b).setScope(a);
                    return b;
                }).orElseThrow(() -> new UnsupportedOperationException("No Expression converted"));
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

    public static String findBindingIdFromDotExpression(String expression) {
        int dot = expression.indexOf( '.' );
        if ( dot < 0 ) {
            throw new UnsupportedOperationException( "unable to parse expression: " + expression );
        }
        return expression.substring(0, dot);
    }

    public static Class extractGenericType(Class<?> clazz, final String methodName) {
        Method method = null;
        try {
            method = clazz.getMethod(methodName, null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException();
        }

        java.lang.reflect.Type returnType = method.getGenericReturnType();

        if(returnType instanceof ParameterizedType){
            ParameterizedType type = (ParameterizedType) returnType;
            java.lang.reflect.Type[] typeArguments = type.getActualTypeArguments();
            for(java.lang.reflect.Type typeArgument : typeArguments){
                return (Class) typeArgument;
            }
        }
        throw new RuntimeException("No generic type");
    }
}
