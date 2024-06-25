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
package org.drools.model.codegen.execmodel.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.nodeTypes.NodeWithTraversableScope;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.Index;
import org.drools.model.codegen.execmodel.errors.IncompatibleGetterOverloadError;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.consequence.DroolsImpl;
import org.drools.mvel.parser.DrlxParser;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvel.parser.printer.PrintUtil;
import org.drools.mvelcompiler.ConstraintCompiler;
import org.drools.mvelcompiler.MvelCompiler;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.util.ClassUtils;
import org.drools.util.IncompatibleGetterOverloadException;
import org.drools.util.MethodUtils;
import org.drools.util.StringUtils;
import org.drools.util.TypeResolver;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.isDslTopLevelNamespace;
import static org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper.findLeftLeafOfNameExprTraversingParent;
import static org.drools.util.ClassUtils.actualTypeFromGenerics;
import static org.drools.util.ClassUtils.toRawClass;
import static org.drools.util.MethodUtils.findMethod;

public class DrlxParseUtil {

    public static final String THIS_PLACEHOLDER = "_this";

    private static final ConcurrentMap<String, Method> ACCESSOR_CACHE = new ConcurrentHashMap<>();

    public static boolean isThisExpression( Node expr ) {
        return expr instanceof ThisExpr || (expr instanceof NameExpr && ((NameExpr)expr).getName().getIdentifier().equals(THIS_PLACEHOLDER));
    }

    public static Index.ConstraintType toConstraintType( Operator operator) {
        switch (operator) {
            case EQUALS:
                return Index.ConstraintType.EQUAL;
            case NOT_EQUALS:
                return Index.ConstraintType.NOT_EQUAL;
            case GREATER:
                return Index.ConstraintType.GREATER_THAN;
            case GREATER_EQUALS:
                return Index.ConstraintType.GREATER_OR_EQUAL;
            case LESS:
                return Index.ConstraintType.LESS_THAN;
            case LESS_EQUALS:
                return Index.ConstraintType.LESS_OR_EQUAL;
            default:
                return Index.ConstraintType.UNKNOWN;
        }
    }

    private static Operator toBinaryExprOperator(HalfBinaryExpr.Operator operator) {
        return Operator.valueOf(operator.name());
    }

    public static TypedExpression nameExprToMethodCallExpr(String name, java.lang.reflect.Type type, Expression scope, RuleContext context) {
        if (type == null) {
            return null;
        }
        Class<?> clazz = toRawClass( type );
        Method accessor = getAccessor(clazz, name, context);
        if (accessor != null) {
            MethodCallExpr body = new MethodCallExpr( scope, accessor.getName() );
            return new TypedExpression( body, actualTypeFromGenerics( type, accessor.getGenericReturnType() ) );
        } else {
            // try parse it as inner class
            for (Class<?> declaredClass : clazz.getClasses()) {
                // An internal class has always a dot on the canonical name path
                if (declaredClass.getCanonicalName().endsWith("." + name)) {
                    FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(scope, name);
                    return new TypedExpression(fieldAccessExpr, declaredClass);
                }
            }
        }
        if (clazz.isArray() && name.equals( "length" )) {
            FieldAccessExpr expr = new FieldAccessExpr( scope != null ? scope : new NameExpr( THIS_PLACEHOLDER ), name );
            return new TypedExpression( expr, int.class );
        }
        try {
            Field field = clazz.getField( name );
            if (scope == null) {
                scope = new NameExpr(Modifier.isStatic( field.getModifiers() ) ? clazz.getCanonicalName() : THIS_PLACEHOLDER);
            }
            FieldAccessExpr expr = new FieldAccessExpr( scope, name );
            return new TypedExpression( expr, field.getType() );
        } catch (NoSuchFieldException e) {
            // There's no field with the given name, return null and manage the problem on the caller
        }
        if (Map.class.isAssignableFrom(clazz)) {
            MethodCallExpr body = new MethodCallExpr( scope, "get", new NodeList<>(new StringLiteralExpr(name)) );
            return new TypedExpression( body, Object.class );
        }
        return null;
    }

    public static java.lang.reflect.Type returnTypeOfMethodCallExpr(RuleContext context, TypeResolver typeResolver, MethodCallExpr methodCallExpr, java.lang.reflect.Type clazz, Collection<String> usedDeclarations) {
        final Class[] argsType = methodCallExpr.getArguments().stream()
                .map((Expression e) -> toRawClass(getExpressionType(context, typeResolver, e, usedDeclarations)))
                .toArray(Class[]::new);
        return findMethod(toRawClass( clazz ), methodCallExpr.getNameAsString(), argsType).getGenericReturnType();
    }

    public static java.lang.reflect.Type getExpressionType(RuleContext context, TypeResolver typeResolver, Expression expr, Collection<String> usedDeclarations) {
        if (expr instanceof LiteralExpr) {
            return getLiteralExpressionType( ( LiteralExpr ) expr );
        }

        if (expr instanceof UnaryExpr) {
            return getExpressionType(context, typeResolver, expr.asUnaryExpr().getExpression(), usedDeclarations);
        }

        if (expr instanceof ArrayAccessExpr) {
            return getClassFromContext(typeResolver, ((ArrayCreationExpr)((ArrayAccessExpr) expr).getName()).getElementType().asString());
        }

        if (expr instanceof ArrayCreationExpr) {
            return getClassFromContext(typeResolver, ((ArrayCreationExpr) expr).getElementType().asString());
        }

        if (expr instanceof MapCreationLiteralExpression) {
            return Map.class;
        }

        if (expr instanceof ListCreationLiteralExpression) {
            return List.class;
        }

        if (expr instanceof NameExpr) {
            return expressionTypeNameExpr(context, usedDeclarations, ((NameExpr) expr).getNameAsString());
        }
        if (expr instanceof DrlNameExpr) {
            return expressionTypeNameExpr(context, usedDeclarations, ((DrlNameExpr) expr).getNameAsString());
        }

        if (expr instanceof BinaryExpr) {
            return boolean.class;
        }

        if (expr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = ( MethodCallExpr ) expr;
            Optional<Expression> scopeExpression = methodCallExpr.getScope();
            if (scopeExpression.isPresent()) {
                java.lang.reflect.Type scopeType = getExpressionType(context, typeResolver, scopeExpression.get(), usedDeclarations);
                return returnTypeOfMethodCallExpr(context, typeResolver, methodCallExpr, scopeType, usedDeclarations);
            } else {
                throw new IllegalStateException("Scope expression is not present for " + ((MethodCallExpr) expr).getNameAsString() + "!");
            }
        }

        if (expr instanceof ObjectCreationExpr) {
            final ClassOrInterfaceType type = ((ObjectCreationExpr) expr).getType();
            return getClassFromContext (typeResolver, type.asString());
        }

        if (expr.isCastExpr()) {
            String typeName = expr.asCastExpr().getType().toString();
            try {
                return typeResolver.resolveType( expr.asCastExpr().getType().toString() );
            } catch (ClassNotFoundException e) {
                context.addCompilationError( new InvalidExpressionErrorResult( "Unknown type in cast expression: " + typeName ) );
                throw new RuntimeException( "Unknown type in cast expression: " + typeName );
            }
        }

        if (expr instanceof ConditionalExpr) {
            ConditionalExpr ternaryExpr = (( ConditionalExpr ) expr);
            java.lang.reflect.Type conditionType = getExpressionType( context, typeResolver, ternaryExpr.getCondition(), usedDeclarations );
            if (conditionType != Boolean.class && conditionType != boolean.class) {
                context.addCompilationError( new InvalidExpressionErrorResult( "Condtion used in ternary expression '" + expr + "' isn't boolean" ) );
                return Object.class;
            }

            java.lang.reflect.Type leftType = getExpressionType( context, typeResolver, ternaryExpr.getThenExpr(), usedDeclarations );
            java.lang.reflect.Type rightType = getExpressionType( context, typeResolver, ternaryExpr.getElseExpr(), usedDeclarations );
            Class<?> leftClass = toRawClass( leftType );
            Class<?> rightClass = toRawClass( rightType );
            if (leftClass.isAssignableFrom( rightClass )) {
                return leftType;
            }
            if (rightClass.isAssignableFrom( leftClass )) {
                return rightType;
            }
            return Object.class;
        }

        if (expr.isClassExpr()) {
            return Class.class;
        }

        throw new RuntimeException("Unknown expression type: " + PrintUtil.printNode(expr));
    }

    private static java.lang.reflect.Type expressionTypeNameExpr(RuleContext context, Collection<String> usedDeclarations, String nameAsString) {
        String name = nameAsString;
        if (usedDeclarations != null) {
            usedDeclarations.add(name);
        }
        Optional<java.lang.reflect.Type> type = context.getTypedDeclarationById(name ).map(TypedDeclarationSpec::getDeclarationClass);
        return type.orElseThrow(() -> new NoSuchElementException("Cannot get expression type by name " + name + "!"));
    }

    public static boolean canCoerceLiteralNumberExpr(Class<?> type) {
        final List<? extends Class<?>> classes = Arrays.asList(int.class, long.class, double.class);
        return classes.contains(type);
    }

    public static Class<?> getLiteralExpressionType( LiteralExpr expr ) {
        if (expr instanceof BooleanLiteralExpr) {
            return boolean.class;
        }
        if (expr instanceof CharLiteralExpr) {
            return char.class;
        }
        if (expr instanceof DoubleLiteralExpr) {
            return double.class;
        }
        if (expr instanceof IntegerLiteralExpr) {
            return int.class;
        }
        if (expr instanceof LongLiteralExpr) {
            return long.class;
        }
        if (expr instanceof NullLiteralExpr) {
            return MethodUtils.NullType.class;
        }
        if (expr instanceof StringLiteralExpr) {
            return String.class;
        }
        if (expr instanceof BigDecimalLiteralExpr) {
            return BigDecimal.class;
        }
        if (expr instanceof BigIntegerLiteralExpr) {
            return BigInteger.class;
        }
        throw new RuntimeException("Unknown literal: " + expr);
    }

    public static Expression prepend(Expression scope, Expression expr) {
        final Optional<Expression> rootNode = findRootNodeViaScope(expr);
        if (rootNode.isPresent()) {
            if(rootNode.get() instanceof ThisExpr) {
                rootNode.get().replace(scope);
            } else if (rootNode.get() instanceof NodeWithOptionalScope<?>) {
                ((NodeWithOptionalScope) rootNode.get()).setScope(scope);
            }
            return expr;
        } else {
            throw new IllegalStateException("No root node was found!");
        }
    }

    public static Optional<Node> findRootNodeViaParent(Node expr) {
        final Optional<Node> parentNode = expr.getParentNode();
        if(expr instanceof Statement) { // we never use this method to navigate up to the statement
            return Optional.empty();
        } else if (parentNode.isPresent()) {
            return findRootNodeViaParent(parentNode.get());
        } else {
            return Optional.of(expr);
        }
    }

    public static Node replaceAllHalfBinaryChildren(Node parent) {
        parent.findAll(HalfBinaryExpr.class)
                .forEach(n -> n.replace(trasformHalfBinaryToBinary(n)));
        return parent;
    }

    public static Expression trasformHalfBinaryToBinary(Expression drlxExpr) {
        final Optional<Node> parent = drlxExpr.getParentNode();
        if(drlxExpr instanceof HalfBinaryExpr && parent.isPresent()) {
            HalfBinaryExpr halfBinaryExpr = (HalfBinaryExpr) drlxExpr;
            Expression parentLeft = findLeftLeafOfNameExprTraversingParent( halfBinaryExpr );
            Operator operator = toBinaryExprOperator(halfBinaryExpr.getOperator());
            return new BinaryExpr(parentLeft, halfBinaryExpr.getRight(), operator);
        }
        return drlxExpr;
    }

    public static MethodCallExpr findLastMethodInChain(MethodCallExpr expr) {
        return expr.getScope().filter( MethodCallExpr.class::isInstance ).map( MethodCallExpr.class::cast ).map( DrlxParseUtil::findLastMethodInChain ).orElse( expr );
    }

    public static RemoveRootNodeResult findRemoveRootNodeViaScope(Expression expr) {
        return findRootNodeViaScopeRec(expr, new LinkedList<>());
    }

    public static Optional<Expression> findRootNodeViaScope(Expression expr) {
        return findRemoveRootNodeViaScope(expr).rootNode;
    }

    public static RemoveRootNodeResult removeRootNode(Expression expr) {
        return findRemoveRootNodeViaScope(expr);
    }

    private static RemoveRootNodeResult findRootNodeViaScopeRec(Expression expr, LinkedList<Expression> acc) {

        if (expr.isArrayAccessExpr()) {
            throw new RuntimeException("This doesn't work on arrayAccessExpr convert them to a method call");
        }

        if (expr instanceof EnclosedExpr) {
            return findRootNodeViaScopeRec(expr.asEnclosedExpr().getInner(), acc);
        } else if (expr instanceof CastExpr) {
            return findRootNodeViaScopeRec(expr.asCastExpr().getExpression(), acc);
        } else if (expr instanceof ThisExpr) {
            return new RemoveRootNodeResult(Optional.of(expr), expr, expr);
        } else if (expr instanceof NodeWithTraversableScope) {
            final NodeWithTraversableScope exprWithScope = (NodeWithTraversableScope) expr;

            return exprWithScope.traverseScope().flatMap((Expression scope) -> {
                if (isDslTopLevelNamespace(scope)) {
                    return empty();
                }
                Expression sanitizedExpr = DrlxParseUtil.transformDrlNameExprToNameExpr(expr);
                acc.addLast(sanitizedExpr.clone());
                return of(findRootNodeViaScopeRec(scope, acc));
            }).orElse(new RemoveRootNodeResult(Optional.of(expr), expr, acc.isEmpty() ? expr : acc.getLast()));
        } else if (expr instanceof NameExpr) {
            if(!acc.isEmpty() && acc.getLast() instanceof NodeWithOptionalScope<?>) {
                ((NodeWithOptionalScope<?>) acc.getLast()).setScope(null);

                for (ListIterator<Expression> iterator = acc.listIterator(); iterator.hasNext(); ) {
                    Expression e = iterator.next();
                    if(e instanceof NodeWithOptionalScope) {
                        NodeWithOptionalScope<?> node = (NodeWithOptionalScope<?>)e;
                        if(iterator.hasNext()) {
                            node.setScope(acc.get(iterator.nextIndex()));
                        }
                    }
                }

                return new RemoveRootNodeResult(Optional.of(expr), acc.getFirst(), acc.getLast());
            } else {
                return new RemoveRootNodeResult(Optional.of(expr), expr, expr);
            }

        }

        return new RemoveRootNodeResult(empty(), expr, expr);
    }

    public static class RemoveRootNodeResult {
        private Optional<Expression> rootNode;
        private Expression withoutRootNode;
        private Expression firstChild;

        public RemoveRootNodeResult(Optional<Expression> rootNode, Expression withoutRootNode, Expression firstChild) {
            this.rootNode = rootNode;
            this.withoutRootNode = withoutRootNode;
            this.firstChild = firstChild;
        }

        public Optional<Expression> getRootNode() {
            return rootNode;
        }

        public Expression getWithoutRootNode() {
            return withoutRootNode;
        }

        public Expression getFirstChild() {
            return firstChild;
        }

        @Override
        public String toString() {
            return "RemoveRootNodeResult{" +
                    "rootNode=" + rootNode.map(PrintUtil::printNode) +
                    ", withoutRootNode=" + PrintUtil.printNode(withoutRootNode) +
                    ", firstChild=" + PrintUtil.printNode(firstChild) +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RemoveRootNodeResult that = (RemoveRootNodeResult) o;
            return Objects.equals(rootNode.map(PrintUtil::printNode), that.rootNode.map(PrintUtil::printNode)) &&
                    Objects.equals(PrintUtil.printNode(withoutRootNode), PrintUtil.printNode(that.withoutRootNode)) &&
                    Objects.equals(PrintUtil.printNode(firstChild), PrintUtil.printNode(that.firstChild));
        }

        @Override
        public int hashCode() {
            return Objects.hash(rootNode, withoutRootNode, firstChild);
        }
    }

    public static BlockStmt parseBlock(String ruleConsequenceAsBlock) {
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_15);
        com.github.javaparser.JavaParser javaParser = new com.github.javaparser.JavaParser(parserConfiguration);

        ParseResult<BlockStmt> blockStmtParseResult = javaParser.parseBlock(String.format("{%n%s%n}", ruleConsequenceAsBlock));

        if (blockStmtParseResult.isSuccessful()) {
            return blockStmtParseResult.getResult().get();  // if the RHS is composed only of a line of comment like `//do nothing.` then JavaParser would fail to recognize the ending
        }
        throw new ParseProblemException(blockStmtParseResult.getProblems());
    }

    public static Expression generateLambdaWithoutParameters(Collection<String> usedDeclarations, Expression expr) {
        return generateLambdaWithoutParameters(usedDeclarations, expr, false, empty());
    }

    public static Expression generateLambdaWithoutParameters(Expression expr) {
        Collection<String> usedDeclarations = expr.findAll( NameExpr.class ).stream().map( NameExpr::getName ).map( SimpleName::getIdentifier ).collect( toList() );
        return generateLambdaWithoutParameters(usedDeclarations, expr, true, empty());
    }

    public static Expression generateLambdaWithoutParameters(Collection<String> usedDeclarations,
                                                             Expression expr,
                                                             boolean skipFirstParamAsThis,
                                                             Optional<Class<?>> patternClass) {
        return generateLambdaWithoutParameters(usedDeclarations, expr, skipFirstParamAsThis, patternClass, null);
    }

    public static Expression generateLambdaWithoutParameters(Collection<String> usedDeclarations,
                                                             Expression expr,
                                                             boolean skipFirstParamAsThis,
                                                             Optional<Class<?>> patternClass,
                                                             RuleContext ruleContext) {
        DrlxParseUtil.transformDrlNameExprToNameExpr(expr);
        if (skipFirstParamAsThis && usedDeclarations.isEmpty()) {
            return expr;
        }
        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters(true);

        // Only when we can resolve all parameter types, do it
        boolean canResolve = canResolveAllParameterTypes(usedDeclarations, skipFirstParamAsThis, patternClass, ruleContext);
        if (!skipFirstParamAsThis) {
            Type type;
            if (canResolve) {
                type = toClassOrInterfaceType(patternClass.get());
            } else {
                type = new UnknownType();
            }
            lambdaExpr.addParameter(new Parameter(type, THIS_PLACEHOLDER));
        }
        usedDeclarations.stream()
                        .map(s -> {
                            if (canResolve) {
                                return new Parameter(getDeclarationType(ruleContext, s), s);
                            } else {
                                return new Parameter(new UnknownType(), s);
                            }
                        })
                        .forEach(lambdaExpr::addParameter);

        lambdaExpr.setBody(new ExpressionStmt(expr));
        return lambdaExpr;
    }

    private static boolean canResolveAllParameterTypes(Collection<String> usedDeclarations, boolean skipFirstParamAsThis, Optional<Class<?>> patternClass, RuleContext ruleContext) {
        if (!skipFirstParamAsThis && patternClass.isEmpty()) {
            return false;
        }
        if (usedDeclarations.isEmpty()) {
            return true;
        }
        return usedDeclarations.stream().map(decl -> getDeclarationType(ruleContext, decl)).noneMatch(type -> type instanceof UnknownType);
    }

    private static Type getDeclarationType(RuleContext ruleContext, String variableName) {
        if (ruleContext == null) {
            return new UnknownType();
        }
        return ruleContext.getDelarationType(variableName);
    }

    public static AnnotationExpr createSimpleAnnotation(Class<?> annotationClass) {
        return createSimpleAnnotation(annotationClass.getCanonicalName());
    }

    public static AnnotationExpr createSimpleAnnotation(String className) {
        return new NormalAnnotationExpr(new Name(className), new NodeList<>());
    }

    public static Type classToReferenceType(Class<?> declarationClass) {
        return classNameToReferenceTypeWithBoxing(declarationClass).parsedType;
    }

    public static Type classToReferenceType(TypedDeclarationSpec declaration) {
        if (declaration.isParametrizedType()) {
            return StaticJavaParser.parseClassOrInterfaceType(declaration.getDeclarationType().getTypeName());
        }
        Class<?> declarationClass = declaration.getDeclarationClass();
        ReferenceType parsedType = classNameToReferenceTypeWithBoxing(declarationClass);
        declaration.setBoxed(parsedType.wasBoxed);
        return parsedType.parsedType;
    }

    private static ReferenceType classNameToReferenceTypeWithBoxing(Class<?> declarationClass) {
        Type parsedType = toJavaParserType(declarationClass);
        if (parsedType instanceof PrimitiveType) {
            return new ReferenceType(((PrimitiveType) parsedType).toBoxedType(), true);
        }
        return new ReferenceType(parsedType, false);
    }

    public static Type toJavaParserType(Class<?> cls) {
        return toJavaParserType( cls, cls.isPrimitive() );
    }

    public static Type toJavaParserType(Class<?> cls, boolean primitive) {
        if (primitive) {
            if (cls == int.class || cls == Integer.class) {
                return PrimitiveType.intType();
            }
            else if (cls == char.class || cls == Character.class) {
                return PrimitiveType.charType();
            }
            else if (cls == long.class || cls == Long.class) {
                return PrimitiveType.longType();
            }
            else if (cls == short.class || cls == Short.class) {
                return PrimitiveType.shortType();
            }
            else if (cls == double.class || cls == Double.class) {
                return PrimitiveType.doubleType();
            }
            else if (cls == float.class || cls == Float.class) {
                return PrimitiveType.floatType();
            }
            else if (cls == boolean.class || cls == Boolean.class) {
                return PrimitiveType.booleanType();
            }
            else if (cls == byte.class || cls == Byte.class) {
                return PrimitiveType.byteType();
            }
        }
        return toClassOrInterfaceType(cls);
    }

    static class ReferenceType {
        Type parsedType;
        Boolean wasBoxed;

        public ReferenceType(Type parsedType, Boolean wasBoxed) {
            this.parsedType = parsedType;
            this.wasBoxed = wasBoxed;
        }
    }

    public static ClassOrInterfaceType toClassOrInterfaceType( Class<?> declClass ) {
        return new ClassOrInterfaceType(null, declClass.getCanonicalName());
    }

    public static ClassOrInterfaceType toClassOrInterfaceType( String className ) {
        String withoutDollars = className.replace("$", "."); // nested class in Java cannot be used in casts
        return withoutDollars.indexOf('<') >= 0 ? StaticJavaParser.parseClassOrInterfaceType(withoutDollars) : new ClassOrInterfaceType(null, withoutDollars);
    }

    public static StringLiteralExpr toStringLiteral(String s) {
        return new StringLiteralExpr(null, s);
    }

    public static Optional<String> findBindingIdFromDotExpression(String expression) {
        int dot = expression.indexOf( '.' );
        if ( dot < 0 ) {
            return empty();
        }
        return of(expression.substring(0, dot));
    }

    public static Optional<Expression> findViaScopeWithPredicate(Expression expr, Predicate<Expression> predicate) {

        final Boolean result = predicate.test(expr);
        if(Boolean.TRUE.equals(result)) {
            return Optional.of(expr);
        } else if (expr instanceof NodeWithTraversableScope) {
            final NodeWithTraversableScope exprWithScope = (NodeWithTraversableScope) expr;

            return exprWithScope.traverseScope().map((Expression expr1) -> findViaScopeWithPredicate(expr1, predicate)).orElse(of(expr));
        }

        return empty();
    }

    public static DrlxExpression parseExpression(String expression) {
        return DrlxParser.parseExpression(DrlxParser.buildDrlxParserWithArguments(OperatorsHolder.operators), expression);
    }

    public static Class<?> getClassFromType(TypeResolver typeResolver, Type type) {
        return getClassFromContext(typeResolver, type.asString());
    }

    public static Class<?> getClassFromContext(TypeResolver typeResolver, String className) {
        try {
            return typeResolver.resolveType(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
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
    public static void forceCastForName(String nameRef, Type type, Node expression) {
        List<NameExpr> allNameExprForName = expression.findAll(NameExpr.class, n -> n.getNameAsString().equals(nameRef));
        for (NameExpr n : allNameExprForName) {
            Optional<Node> parentNode = n.getParentNode();
            if (parentNode.isPresent()) {
                parentNode.get().replace(n, new EnclosedExpr(new CastExpr(type, n)));
            } else {
                throw new IllegalStateException("Cannot find parent node for " + n.getNameAsString() + "!");
            }
        }
    }

    /**
     * Mutates expression
     * such that, if it contains a NameExpr for any of the <code>names</code>,
     * it is replaced with a FieldAccessExpr having <code>newScope</code> as the scope.
     */
    public static void rescopeNamesToNewScope(Expression newScope, List<String> names, Node e) {

        if (e instanceof NodeWithArguments) {
            NodeWithArguments<?> arguments = (NodeWithArguments) e;
            for (Expression argument : arguments.getArguments()) {
                rescopeNamesToNewScope(newScope, names, argument);
            }
        }

        if (e instanceof AssignExpr) {
            AssignExpr assignExpr = (AssignExpr) e;
            rescopeNamesToNewScope(newScope, names, assignExpr.getTarget());
            rescopeNamesToNewScope(newScope, names, assignExpr.getValue());
        } else if (e instanceof BinaryExpr) {
            rescopeNamesToNewScope(newScope, names, (( BinaryExpr ) e).getLeft());
            rescopeNamesToNewScope(newScope, names, (( BinaryExpr ) e).getRight());
        } else if (e instanceof UnaryExpr) {
            rescopeNamesToNewScope(newScope, names, (( UnaryExpr ) e).getExpression());
        } else if (e instanceof EnclosedExpr) {
            rescopeNamesToNewScope(newScope, names, (( EnclosedExpr ) e).getInner());
        } else if (e instanceof Expression) {
            Optional<Expression> rootNode = DrlxParseUtil.findRootNodeViaScope((Expression)e);
            if (rootNode.isPresent() && rootNode.get() instanceof NameExpr) {
                NameExpr nameExpr = (NameExpr) rootNode.get();
                if (names.contains(nameExpr.getNameAsString())) {
                    Expression prepend = new FieldAccessExpr(newScope, nameExpr.getNameAsString());
                    if (e instanceof NameExpr) {
                        Optional<Node> parentNode = e.getParentNode();
                        if (parentNode.isPresent()) {
                            parentNode.get().replace(nameExpr, prepend); // actually `e` was not composite, it was already the NameExpr node I was looking to replace.
                        } else {
                            throw new IllegalStateException("Cannot find parent node for " + ((NameExpr) e).getNameAsString() + "!" );
                        }
                    } else {
                        e.replace(nameExpr, prepend);
                    }
                }
            }
        } else {
            for (Node child : e.getChildNodes()) {
                rescopeNamesToNewScope(newScope, names, child);
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

    public static List<String> getPatternListenedProperties( PatternDescr pattern) {
        AnnotationDescr watchAnn = pattern != null ? pattern.getAnnotation("watch") : null;
        return watchAnn == null ?
                Collections.emptyList() :
                Stream.of(watchAnn.getValue().toString().split(","))
                        .map( String::trim )
                        .map( StringUtils::lcFirstForBean )
                        .collect( toList() );
    }

    public static Optional<MethodCallExpr> findLastPattern(List<Expression> expressions) {
        final Stream<MethodCallExpr> patterns = expressions.stream().flatMap((Expression e) -> {
            final List<MethodCallExpr> pattern = e.findAll(MethodCallExpr.class, expr -> expr.getName().asString().equals(PATTERN_CALL));
            return pattern.stream();
        });
        final List<MethodCallExpr> collect = patterns.collect(toList());
        if (collect.isEmpty()) {
            return empty();
        } else {
            return Optional.of(collect.get(collect.size() - 1));
        }
    }

    public static boolean isNameExprWithName(Node expression, String name) {
        return expression instanceof NameExpr && (( NameExpr ) expression).getNameAsString().equals(name );
    }

    public static List<Node> findAllChildrenRecursive(Expression e) {
        final List<Node> accumulator = new ArrayList<>();
        findAllChildrenRecursiveRec(accumulator, e);
        return accumulator;
    }

    private static void findAllChildrenRecursiveRec(List<Node> accumulator, Node e) {
        for(Node child : e.getChildNodes()) {
            accumulator.add(child);
            findAllChildrenRecursiveRec(accumulator, child);
        }
    }

    public static String toVar(String key) {
        return "var_" + key;
    }

    public static Optional<InvalidExpressionErrorResult> validateDuplicateBindings(String ruleName, List<String> allBindings) {
        final Set<String> duplicates = new HashSet<>();
        for(String b : allBindings) {
            Boolean notExisting = duplicates.add(b);
            if(Boolean.FALSE.equals(notExisting)) {
                return Optional.of(new InvalidExpressionErrorResult(String.format("Duplicate declaration for variable '%s' in the rule '%s'", b, ruleName)));
            }
        }
        return empty();
    }

    public static Method getAccessor(Class<?> clazz, String name, RuleContext context) {
        String key = clazz.getCanonicalName() + "." + name;
        return ACCESSOR_CACHE.computeIfAbsent(key, k -> {
            Method accessor = null;
            try {
                accessor = ClassUtils.getAccessor(clazz, name, true);
            } catch (IncompatibleGetterOverloadException e) {
                context.addCompilationError(new IncompatibleGetterOverloadError(clazz, e.getOldName(), e.getOldType(), e.getNewName(), e.getOldType()));
            }
            return accessor;
        });
    }

    public static void clearAccessorCache() {
        ACCESSOR_CACHE.clear();
    }

    public static Field getField( Class<?> clazz, String name ) {
        try {
            return clazz.getField( name );
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static <T extends Node> T transformDrlNameExprToNameExpr(T e) {
        if (e instanceof DrlNameExpr) {
            return (T) new NameExpr(((DrlNameExpr) e).getName());
        }
        e.findAll(DrlNameExpr.class).forEach(n -> n.replace(new NameExpr(n.getName())));
        return e;
    }

    public static String addCurlyBracesToBlock(String blockString) {
        return String.format("{\n%s\n}", blockString);
    }

    public static String addSemicolon(String block) {
        return block.endsWith(";") ? block : block + ";";
    }

    public static Expression uncastExpr(Expression e) {
        if(e.isCastExpr()) {
            return e.asCastExpr().getExpression();
        } else {
            return e;
        }
    }

    public static Collection<String> collectUsedDeclarationsInExpression(Expression expr) {
        return expr.findAll(NameExpr.class)
                   .stream()
                   .map(NameExpr::getName)
                   .map(SimpleName::getIdentifier)
                   .collect(toList());
    }

    public static Optional<java.lang.reflect.Type> safeResolveType(TypeResolver typeResolver, String typeName) {
        try {
            return Optional.of(typeResolver.resolveType(typeName));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Expression unEncloseExpr(Expression expression) {
        if(expression.isEnclosedExpr()) {
            return unEncloseExpr(expression.asEnclosedExpr().getInner());
        } else {
            return expression;
        }
    }

    public static MvelCompiler createMvelCompiler(RuleContext context) {
        return createMvelCompiler(context, false);
    }

    public static MvelCompiler createMvelCompiler(RuleContext context, boolean withDrools) {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext( context.getTypeResolver(), context.getCurrentScopeSuffix() );

        for (DeclarationSpec ds : context.getAllDeclarations()) {
            mvelCompilerContext.addDeclaration(ds.getBindingId(), ds.getDeclarationClass());
        }

        for(Map.Entry<String, Method> m : context.getPackageModel().getStaticMethods().entrySet()) {
            mvelCompilerContext.addStaticMethod(m.getKey(), m.getValue());
        }

        for(MethodDeclaration m : context.getPackageModel().getFunctions()) {
            List<String> parametersType = m.getParameters().stream().map(Parameter::getType).map(Type::asString).collect(toList());
            mvelCompilerContext.addDeclaredFunction(m.getNameAsString(), m.getTypeAsString(), parametersType);
        }

        if (withDrools) {
            mvelCompilerContext.addDeclaration("drools", DroolsImpl.class);
        }

        return new MvelCompiler(mvelCompilerContext);
    }

    public static ConstraintCompiler createConstraintCompiler(RuleContext context, Optional<Class<?>> originalPatternType) {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext( context.getTypeResolver(), context.getCurrentScopeSuffix() );

        List<DeclarationSpec> allDeclarations = new ArrayList<>(context.getAllDeclarations());
        originalPatternType.ifPresent(pt -> {
            allDeclarations.add(new TypedDeclarationSpec(THIS_PLACEHOLDER, pt));
            mvelCompilerContext.setRootPatternPrefix(pt, THIS_PLACEHOLDER);
        });

        for(Map.Entry<String, Method> m : context.getPackageModel().getStaticMethods().entrySet()) {
            mvelCompilerContext.addStaticMethod(m.getKey(), m.getValue());
        }

        for (DeclarationSpec ds : allDeclarations) {
            mvelCompilerContext.addDeclaration(ds.getBindingId(), ds.getDeclarationClass());
        }

        return new ConstraintCompiler(mvelCompilerContext);
    }


    public static boolean isBooleanBoxedUnboxed(java.lang.reflect.Type exprType) {
        return exprType == Boolean.class || exprType == boolean.class;
    }

    public static boolean hasDuplicateExpr(BlockStmt ruleBlock, Expression expr) {
        return ruleBlock.findFirst(expr.getClass(), expr::equals).isPresent();
    }

    public static Expression stripEnclosedExpr(Expression expr) {
        if (!(expr instanceof EnclosedExpr)) {
            return expr;
        }
        Expression inner = ((EnclosedExpr) expr).getInner();
        return stripEnclosedExpr(inner);
    }

    private DrlxParseUtil() {
        // It is not allowed to create instances of util classes.
    }
}
