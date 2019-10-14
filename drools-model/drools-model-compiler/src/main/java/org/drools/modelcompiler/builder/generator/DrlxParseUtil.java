package org.drools.modelcompiler.builder.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
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
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithTraversableScope;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.StringUtils;
import org.drools.model.Index;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.util.ClassUtil;
import org.drools.mvel.parser.DrlxParser;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.mvel.parser.printer.PrintUtil;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

import static com.github.javaparser.StaticJavaParser.parseType;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.PATTERN_CALL;
import static org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper.findLeftLeafOfNameExpr;
import static org.drools.modelcompiler.util.ClassUtil.findMethod;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class DrlxParseUtil {

    public static final String THIS_PLACEHOLDER = "_this";

    private static final Map<String, Method> accessorsCache = new HashMap<>();

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

    public static TypedExpression nameExprToMethodCallExpr(String name, java.lang.reflect.Type type, Expression scope) {
        if (type == null) {
            return null;
        }
        Class<?> clazz = toRawClass( type );
        Method accessor = getAccessor( clazz, name );
        if (accessor != null) {
            MethodCallExpr body = new MethodCallExpr( scope, accessor.getName() );
            return new TypedExpression( body, accessor.getGenericReturnType() );
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
        return null;
    }

    public static java.lang.reflect.Type returnTypeOfMethodCallExpr(RuleContext context, TypeResolver typeResolver, MethodCallExpr methodCallExpr, java.lang.reflect.Type clazz, Collection<String> usedDeclarations) {
        final Class[] argsType = methodCallExpr.getArguments().stream()
                .map((Expression e) -> getExpressionType(context, typeResolver, e, usedDeclarations))
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

        throw new RuntimeException("Unknown expression type: " + PrintUtil.printConstraint(expr));
    }

    private static java.lang.reflect.Type expressionTypeNameExpr(RuleContext context, Collection<String> usedDeclarations, String nameAsString) {
        String name = nameAsString;
        if (usedDeclarations != null) {
            usedDeclarations.add(name);
        }
        Optional<java.lang.reflect.Type> type = context.getDeclarationById( name ).map(DeclarationSpec::getDeclarationClass );
        return type.orElseThrow(() -> new NoSuchElementException("Cannot get expression type by name " + name + "!"));
    }

    public static boolean canCoerceLiteralNumberExpr(Class<?> type) {
        final List<? extends Class<?>> classes = Arrays.asList(int.class, long.class, double.class);
        return classes.contains(type);
    }

    public static Expression coerceLiteralNumberExprToType(LiteralStringValueExpr expr, Class<?> type ) {
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
            if (rootNode.get() instanceof NodeWithOptionalScope<?>) {
                ((NodeWithOptionalScope) rootNode.get()).setScope(scope);
            }
            return expr;
        } else {
            throw new IllegalStateException("No root node was found!");
        }
    }

    public static Optional<Node> findRootNodeViaParent(Node expr) {
        final Optional<Node> parentNode = expr.getParentNode();
        if (parentNode.isPresent()) {
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
        } else if (expr instanceof NodeWithTraversableScope) {
            final NodeWithTraversableScope exprWithScope = (NodeWithTraversableScope) expr;

            return exprWithScope.traverseScope().map((Expression scope) -> {
                Expression sanitizedExpr = DrlxParseUtil.transformDrlNameExprToNameExpr(expr);
                acc.addLast(sanitizedExpr.clone());
                return findRootNodeViaScopeRec(scope, acc);
            }).orElse(new RemoveRootNodeResult(Optional.of(expr), expr, acc.isEmpty() ? expr : acc.getLast()));
        } else if (expr instanceof NameExpr) {
            if(!acc.isEmpty() && acc.getLast() instanceof NodeWithOptionalScope) {
                ((NodeWithOptionalScope) acc.getLast()).setScope(null);

                for (ListIterator<Expression> iterator = acc.listIterator(); iterator.hasNext(); ) {
                    Expression e = iterator.next();
                    NodeWithOptionalScope node = (NodeWithOptionalScope)e;
                    if(iterator.hasNext()) {
                        node.setScope(acc.get(iterator.nextIndex()));
                    }
                }

                return new RemoveRootNodeResult(Optional.of(expr), acc.getFirst(), acc.getLast());
            } else {
                return new RemoveRootNodeResult(Optional.of(expr), expr, expr);
            }

        }

        return new RemoveRootNodeResult(Optional.empty(), expr, expr);
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
                    "rootNode=" + rootNode.map(PrintUtil::printConstraint) +
                    ", withoutRootNode=" + PrintUtil.printConstraint(withoutRootNode) +
                    ", firstChild=" + PrintUtil.printConstraint(firstChild) +
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
            return Objects.equals(rootNode.map(PrintUtil::printConstraint), that.rootNode.map(PrintUtil::printConstraint)) &&
                    Objects.equals(PrintUtil.printConstraint(withoutRootNode), PrintUtil.printConstraint(that.withoutRootNode)) &&
                    Objects.equals(PrintUtil.printConstraint(firstChild), PrintUtil.printConstraint(that.firstChild));
        }

        @Override
        public int hashCode() {
            return Objects.hash(rootNode, withoutRootNode, firstChild);
        }
    }

    public static String fromVar(String key) {
        return key.substring( "var_".length() );
    }

    public static BlockStmt parseBlock(String ruleConsequenceAsBlock) {
        return StaticJavaParser.parseBlock(String.format("{%n%s%n}", ruleConsequenceAsBlock)); // if the RHS is composed only of a line of comment like `//do nothing.` then JavaParser would fail to recognize the ending
    }

    public static Expression generateLambdaWithoutParameters(Collection<String> usedDeclarations, Expression expr) {
        return generateLambdaWithoutParameters(usedDeclarations, expr, false);
    }

    public static Expression generateLambdaWithoutParameters(Expression expr) {
        Collection<String> usedDeclarations = expr.findAll( NameExpr.class ).stream().map( NameExpr::getName ).map( SimpleName::getIdentifier ).collect( toList() );
        return generateLambdaWithoutParameters(usedDeclarations, expr, true);
    }

    public static Expression generateLambdaWithoutParameters(Collection<String> usedDeclarations, Expression expr, boolean skipFirstParamAsThis) {
        DrlxParseUtil.transformDrlNameExprToNameExpr(expr);
        if (skipFirstParamAsThis && usedDeclarations.isEmpty()) {
            return expr;
        }
        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters( true );
        if (!skipFirstParamAsThis) {
            lambdaExpr.addParameter(new Parameter(new UnknownType(), THIS_PLACEHOLDER));
        }
        usedDeclarations.stream().map( s -> new Parameter( new UnknownType(), s ) ).forEach( lambdaExpr::addParameter );
        lambdaExpr.setBody( new ExpressionStmt(expr) );
        return lambdaExpr;
    }

    public static TypedExpression toMethodCallWithClassCheck(RuleContext context, Expression expr, String bindingId, Class<?> clazz, TypeResolver typeResolver) {

        final Deque<ParsedMethod> callStackLeftToRight = new LinkedList<>();

        createExpressionCall(expr, callStackLeftToRight);

        java.lang.reflect.Type previousClass = clazz;
        Expression previousScope = null;

        for (ParsedMethod e : callStackLeftToRight) {
            if (e.expression instanceof NameExpr || e.expression instanceof FieldAccessExpr || e.expression instanceof NullSafeFieldAccessExpr) {
                if (e.fieldToResolve.equals( bindingId )) {
                    continue;
                }
                if (previousClass == null) {
                    try {
                        previousClass = typeResolver.resolveType( e.fieldToResolve );
                        previousScope = new NameExpr( e.fieldToResolve );
                    } catch (ClassNotFoundException e1) {
                        // ignore
                    }
                    if (previousClass == null) {
                        previousClass = context.getDeclarationById( e.fieldToResolve )
                                .map( DeclarationSpec::getDeclarationClass )
                                .orElseThrow( () -> new RuntimeException( "Unknown field: " + e.fieldToResolve ) );
                        previousScope = e.expression;
                    }
                } else {
                    TypedExpression te = nameExprToMethodCallExpr( e.fieldToResolve, previousClass, previousScope );
                    if (te == null) {
                        context.addCompilationError( new InvalidExpressionErrorResult( "Unknown field " + e.fieldToResolve + " on " + previousClass ) );
                        return null;
                    }
                    java.lang.reflect.Type returnType = te.getType();
                    previousScope = te.getExpression();
                    previousClass = returnType;
                }
            } else if (e.expression instanceof MethodCallExpr) {
                java.lang.reflect.Type returnType = returnTypeOfMethodCallExpr(context, typeResolver, (MethodCallExpr) e.expression, previousClass, null);
                MethodCallExpr cloned = ((MethodCallExpr) e.expression.clone()).setScope(previousScope);
                previousScope = cloned;
                previousClass = returnType;
            }
        }

        return new TypedExpression(previousScope, previousClass);
    }

    private static Expression createExpressionCall(Expression expr, Deque<ParsedMethod> expressions) {

        if (expr instanceof NodeWithSimpleName) {
            NodeWithSimpleName fae = (NodeWithSimpleName)expr;
            expressions.push(new ParsedMethod(expr, fae.getName().asString()));
        }

        if (expr instanceof NodeWithOptionalScope) {
            final NodeWithOptionalScope<?> exprWithScope = (NodeWithOptionalScope) expr;
            exprWithScope.getScope().ifPresent(expression -> createExpressionCall(expression, expressions));
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
        Type parsedType = parseType(declClass.getCanonicalName());
        return parsedType instanceof PrimitiveType ?
                ((PrimitiveType) parsedType).toBoxedType() :
                parsedType;
    }

    public static Type toType(Class<?> declClass) {
        return parseType(declClass.getCanonicalName());
    }

    public static ClassOrInterfaceType toClassOrInterfaceType( Class<?> declClass ) {
        return toClassOrInterfaceType(declClass.getCanonicalName());
    }

    public static ClassOrInterfaceType toClassOrInterfaceType( String className ) {
        return StaticJavaParser.parseClassOrInterfaceType(className);
    }

    public static Optional<String> findBindingIdFromDotExpression(String expression) {
        int dot = expression.indexOf( '.' );
        if ( dot < 0 ) {
            return Optional.empty();
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

        return Optional.empty();
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
    public static void forceCastForName(String nameRef, Type type, Expression expression) {
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
    public static void rescopeNamesToNewScope(Expression newScope, List<String> names, Expression e) {

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
        } else {
            Optional<Expression> rootNode = DrlxParseUtil.findRootNodeViaScope(e);
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
                        .map( StringUtils::lcFirst )
                        .collect( toList() );
    }

    public static Optional<MethodCallExpr> findPatternWithBinding(RuleContext context, Collection<String> patternBindings, List<Expression> expressions) {
        return expressions.stream().flatMap((Expression e) -> {
            final Optional<MethodCallExpr> pattern = e.findFirst(MethodCallExpr.class, expr -> {
                boolean isPatternExpr = expr.getName().asString().equals(PATTERN_CALL);
                List<Expression> bindingExprsVars = patternBindings.stream().map(context::getVarExpr).collect(Collectors.toList());
                boolean hasBindingHasArgument = !Collections.disjoint(bindingExprsVars, expr.getArguments());
                return isPatternExpr && hasBindingHasArgument;
            });
            return pattern.map(Stream::of).orElse(Stream.empty());
        }).findFirst();
    }

    public static Optional<MethodCallExpr> findLastPattern(List<Expression> expressions) {
        final Stream<MethodCallExpr> patterns = expressions.stream().flatMap((Expression e) -> {
            final List<MethodCallExpr> pattern = e.findAll(MethodCallExpr.class, expr -> expr.getName().asString().equals(PATTERN_CALL));
            return pattern.stream();
        });
        final List<MethodCallExpr> collect = patterns.collect(toList());
        if (collect.isEmpty()) {
            return Optional.empty();
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
        return Optional.empty();
    }

    public static Method getAccessor( Class<?> clazz, String name ) {
        return accessorsCache.computeIfAbsent( clazz.getCanonicalName() + "." + name, k -> ClassUtils.getAccessor(clazz, name) );
    }

    public static void clearAccessorCache() {
        accessorsCache.clear();
    }

    public static Field getField( Class<?> clazz, String name ) {
        try {
            return clazz.getField( name );
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static <T extends Node> T transformDrlNameExprToNameExpr(T e) {
        e.findAll(DrlNameExpr.class)
                .forEach(n -> n.replace(new NameExpr(n.getName())));
        if(e instanceof DrlNameExpr) {
            return (T) new NameExpr(((DrlNameExpr) e).getName());
        } else {
            return e;
        }
    }

    public static MethodCallExpr sanitizeDrlNameExpr(MethodCallExpr me) {
        NodeList<Expression> arguments = NodeList.nodeList();
        for(Expression e : me.getArguments()) {
            arguments.add(sanitizeExpr(e, me));
        }
        me.getScope().map((Expression e) -> sanitizeExpr(e, me)).ifPresent(me::setScope);

        me.setArguments(arguments);
        return me;
    }

    private static Expression sanitizeExpr(Expression e, Expression parent) {
        Expression sanitized;
        if (e instanceof DrlNameExpr) {
            sanitized = new NameExpr(PrintUtil.printConstraint(e));
            sanitized.setParentNode(parent);
        } else {
            sanitized = e;
        }
        return sanitized;
    }

    public static String addCurlyBracesToBlock(String blockString) {
        return String.format("{%s}", blockString);
    }

    public static String addSemicolon(String block) {
        return block.endsWith(";") ? block : block + ";";
    }

    private DrlxParseUtil() {
        // It is not allowed to create instances of util classes.
    }
}
