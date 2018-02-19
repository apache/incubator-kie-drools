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
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.index.IndexUtil;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.drools.drlx.DrlxParser;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.drlx.expr.DrlxExpression;
import org.drools.javaparser.ast.drlx.expr.HalfBinaryExpr;
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
import org.drools.javaparser.ast.expr.IntegerLiteralExpr;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.LiteralExpr;
import org.drools.javaparser.ast.expr.LiteralStringValueExpr;
import org.drools.javaparser.ast.expr.LongLiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.NullLiteralExpr;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.UnaryExpr;
import org.drools.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import org.drools.javaparser.ast.nodeTypes.NodeWithSimpleName;
import org.drools.javaparser.ast.nodeTypes.NodeWithTraversableScope;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.PrimitiveType;
import org.drools.javaparser.ast.type.Type;
import org.drools.javaparser.ast.type.UnknownType;
import org.drools.modelcompiler.util.ClassUtil;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static java.util.Optional.of;

import static org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper.findLeftLeafOfNameExpr;
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

    public static Expression coerceLiteralExprToType( LiteralStringValueExpr expr, Class<?> type ) {
        if (type == int.class) {
            return new IntegerLiteralExpr( expr.getValue() );
        }
        if (type == long.class) {
            return new LongLiteralExpr( expr.getValue().endsWith( "l" ) ? expr.getValue() : expr.getValue() + "l" );
        }
        if (type == double.class) {
            return new DoubleLiteralExpr( expr.getValue().endsWith( "d" ) ? expr.getValue() : expr.getValue() + "d" );
        }
        throw new RuntimeException("Unknown literal: " + expr);
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
            return ClassUtil.NullType.class;
        }
        if (expr instanceof StringLiteralExpr) {
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
        parent.findAll(HalfBinaryExpr.class)
                .forEach(n -> n.replace(trasformHalfBinaryToBinary(n)));
        return parent;
    }

    public static Expression trasformHalfBinaryToBinary(Expression drlxExpr) {
        final Optional<Node> parent = drlxExpr.getParentNode();
        if(drlxExpr instanceof HalfBinaryExpr && parent.isPresent()) {

            HalfBinaryExpr halfBinaryExpr = (HalfBinaryExpr) drlxExpr;

            Expression parentLeft = findLeftLeafOfNameExpr( parent.get() );
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
                if (previousClass == null) {
                    previousClass = context.getDeclarationById( e.fieldToResolve )
                            .map( DeclarationSpec::getDeclarationClass )
                            .orElseThrow( () -> new RuntimeException( "Unknown field: " + e.fieldToResolve ) );
                    methodCall.add(e.expression);
                } else {
                    TypedExpression te = nameExprToMethodCallExpr( e.fieldToResolve, previousClass, null );
                    Class<?> returnType = te.getType();
                    methodCall.add( te.getExpression() );
                    previousClass = returnType;
                }
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

    public static Class<?> getClassFromType(TypeResolver typeResolver, Type type) {
        return getClassFromContext(typeResolver, type.asString());
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
            n.getParentNode().get().replace(n, new CastExpr(type, n));
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
        } else if (e instanceof BinaryExpr) {
            rescopeNamesToNewScope(newScope, names, (( BinaryExpr ) e).getLeft());
            rescopeNamesToNewScope(newScope, names, (( BinaryExpr ) e).getRight());
        } else if (e instanceof UnaryExpr) {
            rescopeNamesToNewScope(newScope, names, (( UnaryExpr ) e).getExpression());
        } else if (e instanceof EnclosedExpr) {
            rescopeNamesToNewScope(newScope, names, (( EnclosedExpr ) e).getInner());
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

    public static List<String> getPatternListenedProperties( PatternDescr pattern) {
        AnnotationDescr watchAnn = pattern != null ? pattern.getAnnotation("watch") : null;
        return watchAnn == null ? Collections.emptyList() : Stream.of(watchAnn.getValue().toString().split(",")).map(String::trim).collect( Collectors.toList() );
    }
}
