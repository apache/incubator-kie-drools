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
import org.drools.javaparser.ast.drlx.expr.InlineCastExpr;
import org.drools.javaparser.ast.drlx.expr.NullSafeFieldAccessExpr;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.BinaryExpr.Operator;
import org.drools.javaparser.ast.expr.CastExpr;
import org.drools.javaparser.ast.expr.EnclosedExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.InstanceOfExpr;
import org.drools.javaparser.ast.expr.LiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.NullLiteralExpr;
import org.drools.javaparser.ast.expr.ThisExpr;
import org.drools.javaparser.ast.type.ReferenceType;
import org.drools.modelcompiler.builder.generator.ModelGenerator.RuleContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DrlxParseUtil {

    public static IndexUtil.ConstraintType toConstraintType( Operator operator ) {
        switch ( operator ) {
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
        throw new UnsupportedOperationException( "Unknown operator " + operator );
    }

    public static TypedExpression toTypedExpression( RuleContext context, Class<?> patternType, Expression drlxExpr,
                                                     Set<String> usedDeclarations, Set<String> reactOnProperties ) {
        
        Class<?> typeCursor = patternType;
        
        if ( drlxExpr instanceof LiteralExpr ) {
            return new TypedExpression( drlxExpr , Optional.empty());
        } else if ( drlxExpr instanceof ThisExpr ) {
            return new TypedExpression( new NameExpr( "_this") , Optional.empty());
        }
        else if ( drlxExpr instanceof NameExpr ) {
            String name = drlxExpr.toString();
            if (context.declarations.containsKey(name)) {
                // then drlxExpr is a single NameExpr referring to a binding, e.g.: "$p1".
                usedDeclarations.add( name );
                return new TypedExpression( drlxExpr, Optional.empty());
            } else {
                TypedExpression expression = nameExprToMethodCallExpr(name, typeCursor);
                Expression plusThis = preprendNameExprToMethodCallExpr(new NameExpr("_this"), (MethodCallExpr)expression.getExpression());
                return new TypedExpression(plusThis, expression.getType());
            }
        } else if ( drlxExpr instanceof FieldAccessExpr ) {
            List<Node> childNodes = drlxExpr.getChildNodes();
            Node firstNode = childNodes.get(0);

            boolean isInLineCast = firstNode instanceof InlineCastExpr;
            if (isInLineCast) {
                InlineCastExpr inlineCast = (InlineCastExpr) firstNode;
                try {
                    typeCursor = context.getPkg().getTypeResolver().resolveType( inlineCast.getType().toString() );
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException( e );
                }
                firstNode = inlineCast.getExpression();
            }

            Expression previous;

            if (firstNode instanceof NameExpr) {
                String firstName = ( (NameExpr) firstNode ).getName().getIdentifier();
                if ( context.declarations.containsKey( firstName ) ) {
                    // do NOT append any reactOnProperties.
                    // because reactOnProperties is referring only to the properties of the type of the pattern, not other declarations properites.
                    usedDeclarations.add( firstName );
                    if (!isInLineCast) {
                        typeCursor = context.declarations.get( firstName ).declarationClass;
                    }
                    previous = new NameExpr( firstName );
                } else {
                    Method firstAccessor = ClassUtils.getAccessor( typeCursor, firstName );
                    if (firstAccessor != null) {
                        reactOnProperties.add( firstName );
                        typeCursor = firstAccessor.getReturnType();
                        previous = new MethodCallExpr( new NameExpr( "_this" ), firstAccessor.getName() );
                    } else {
                        throw new UnsupportedOperationException("firstNode I don't know about");
                        // TODO would it be fine to assume is a global if it's not in the declarations and not the first accesssor in a chain?
                    }
                }

            } else if (firstNode instanceof ThisExpr) {
                previous = new NameExpr( "_this" );
                if ( childNodes.size() > 1 && childNodes.get(1) instanceof NameExpr ) {
                    NameExpr child0 = (NameExpr) childNodes.get(1);
                    reactOnProperties.add( child0.getName().getIdentifier() );
                }

            } else if (firstNode instanceof FieldAccessExpr && ( (FieldAccessExpr) firstNode ).getScope() instanceof ThisExpr) {
                String firstName = ( (FieldAccessExpr) firstNode ).getName().getIdentifier();
                Method firstAccessor = ClassUtils.getAccessor( typeCursor, firstName );
                if (firstAccessor != null) {
                    reactOnProperties.add( firstName );
                    typeCursor = firstAccessor.getReturnType();
                    previous = new MethodCallExpr( new NameExpr( "_this" ), firstAccessor.getName() );
                } else {
                    throw new UnsupportedOperationException("firstNode I don't know about");
                    // TODO would it be fine to assume is a global if it's not in the declarations and not the first accesssor in a chain?
                }
            } else {
                throw new UnsupportedOperationException( "Unknown node: " + firstNode );
            }

            childNodes = drlxExpr.getChildNodes().subList( 1, drlxExpr.getChildNodes().size() );

            TypedExpression typedExpression = new TypedExpression();
            if (isInLineCast) {
                ReferenceType castType = JavaParser.parseClassOrInterfaceType( typeCursor.getName() );
                typedExpression.setPrefixExpression( new InstanceOfExpr( previous, castType ) );
                previous = new EnclosedExpr( new CastExpr( castType, previous ) );
            }
            if ( drlxExpr instanceof NullSafeFieldAccessExpr ) {
                typedExpression.setPrefixExpression( new BinaryExpr( previous, new NullLiteralExpr(), Operator.NOT_EQUALS ) );
            }

            for ( Node part : childNodes ) {
                String field = part.toString();
                Method accessor = ClassUtils.getAccessor( typeCursor, field );
                if (accessor == null) {
                    throw new IllegalStateException( "Unknown field '" + field + "' on type " + typeCursor );
                }
                typeCursor = accessor.getReturnType();
                previous = new MethodCallExpr( previous, accessor.getName() );
            }

            return typedExpression.setExpression( previous ).setType( Optional.of( typeCursor ) );
        } else {
            // TODO the below should not be needed anymore...
            drlxExpr.getChildNodes();
            String expression = drlxExpr.toString();
            String[] parts = expression.split("\\.");
            StringBuilder telescoping = new StringBuilder();
            boolean implicitThis = true;
            
            for ( int idx = 0; idx < parts.length ; idx++ ) {
                String part = parts[idx];
                boolean isGlobal = false;
                if ( isGlobal ) {
                    implicitThis = false;
                    telescoping.append( part );
                } else if ( idx == 0 && context.declarations.containsKey(part) ) {
                    implicitThis = false;
                    usedDeclarations.add( part );
                    telescoping.append( part );
                } else {
                    if ( ( idx == 0 && implicitThis ) || ( idx == 1 && implicitThis == false ) ) {
                        reactOnProperties.add(part);
                    }
                    Method accessor = ClassUtils.getAccessor( typeCursor, part );
                    typeCursor = accessor.getReturnType();
                    telescoping.append( "." + accessor.getName() + "()" );
                }
            }
            return new TypedExpression( implicitThis ? "_this" + telescoping.toString() : telescoping.toString(), Optional.of( typeCursor ));
        }
    }

    public static TypedExpression nameExprToMethodCallExpr(String name, Class<?> clazz) {
        Class<?> typeCursor = clazz;
        Method accessor = ClassUtils.getAccessor(typeCursor, name );
        Class<?> accessorReturnType = accessor.getReturnType();

        MethodCallExpr body = new MethodCallExpr( null, accessor.getName() );
        return new TypedExpression( body, Optional.of( accessorReturnType ));
    }

    public static MethodCallExpr preprendNameExprToMethodCallExpr(NameExpr nameExpr, MethodCallExpr methodCallExpr) {

        final Optional<Expression> rootNode = findRootNote(methodCallExpr);

        rootNode.map(f -> {
            if(f instanceof MethodCallExpr) {
                ((MethodCallExpr)f).setScope(nameExpr);
            }
            return f;
        });

        return methodCallExpr;
    }

    private static Optional<Expression> findRootNote(Expression methodCallExpr) {

        if(methodCallExpr instanceof MethodCallExpr) {
            final MethodCallExpr methodCall = (MethodCallExpr)methodCallExpr;

            if(methodCall.getScope().isPresent()) {
                return findRootNote(((MethodCallExpr) methodCallExpr).getScope().get());
            } else {
                return Optional.of(methodCall);
            }
        }

        return Optional.empty();

    }
}
