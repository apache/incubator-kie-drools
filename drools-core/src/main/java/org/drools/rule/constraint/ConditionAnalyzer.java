package org.drools.rule.constraint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

import org.mvel2.Operator;
import org.mvel2.ParserContext;
import org.mvel2.ast.*;
import org.mvel2.compiler.Accessor;
import org.mvel2.compiler.AccessorNode;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.compiler.ExecutableLiteral;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.optimizers.dynamic.DynamicGetAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ArrayAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ArrayAccessorNest;
import org.mvel2.optimizers.impl.refl.nodes.ArrayLength;
import org.mvel2.optimizers.impl.refl.nodes.ConstructorAccessor;
import org.mvel2.optimizers.impl.refl.nodes.FieldAccessor;
import org.mvel2.optimizers.impl.refl.nodes.GetterAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ListAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ListAccessorNest;
import org.mvel2.optimizers.impl.refl.nodes.MapAccessor;
import org.mvel2.optimizers.impl.refl.nodes.MapAccessorNest;
import org.mvel2.optimizers.impl.refl.nodes.MethodAccessor;
import org.mvel2.optimizers.impl.refl.nodes.StaticReferenceAccessor;
import org.mvel2.optimizers.impl.refl.nodes.StaticVarAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ThisValueAccessor;
import org.mvel2.optimizers.impl.refl.nodes.VariableAccessor;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class ConditionAnalyzer {

    private ASTNode node;
    private ExecutableLiteral executableLiteral;
    private ParserContext parserContext;
    private Map<String, Object> vars;

    public ConditionAnalyzer(ExecutableStatement stmt) {
        if (stmt instanceof ExecutableLiteral) {
            executableLiteral = (ExecutableLiteral)stmt;
        } else if (stmt instanceof CompiledExpression) {
            parserContext = ((CompiledExpression)stmt).getParserContext();
            node = ((CompiledExpression)stmt).getFirstNode();
        } else {
            node = ((ExecutableAccessor)stmt).getNode();
        }
    }

    public ConditionAnalyzer(ExecutableStatement stmt, Map<String, Object> vars) {
        this(stmt);
        this.vars = vars;
    }

    public Condition analyzeCondition() {
        if (executableLiteral != null) {
            return new FixedValueCondition((Boolean)executableLiteral.getLiteral());
        }
        while (node.nextASTNode != null) node = node.nextASTNode;
        return analyzeCondition(node);
    }

    private Condition analyzeCondition(ASTNode node) {
        boolean isNegated = false;
        if (node instanceof Negation) {
            isNegated = true;
            node = ((ExecutableAccessor)((Negation)node).getStatement()).getNode();
        }
        node = analyzeSubstatement(node);

        if (node instanceof LiteralNode && node.getEgressType() == Boolean.class) {
            boolean literalValue = (Boolean)((LiteralNode)node).getLiteralValue();
            return new FixedValueCondition(isNegated ? !literalValue : literalValue);
        }

        if (node instanceof And || node instanceof Or) {
            return analyzeCombinedCondition((BooleanNode)node, isNegated);
        }
        if (node instanceof Negation) {
            isNegated = !isNegated;
            node = ((ExecutableAccessor)((Negation)node).getStatement()).getNode();
            node = analyzeSubstatement(node);
        }

        return analyzeSingleCondition(node, isNegated);
    }

    private SingleCondition analyzeSingleCondition(ASTNode node, boolean isNegated) {
        SingleCondition condition = new SingleCondition(isNegated);
        if (node instanceof BinaryOperation) {
            BinaryOperation binaryOperation = (BinaryOperation)node;
            condition.left = analyzeNode(binaryOperation.getLeft());
            condition.operation = BooleanOperator.fromMvelOpCode(binaryOperation.getOperation());
            condition.right = analyzeNode(binaryOperation.getRight());
        } else if (node instanceof RegExMatch) {
            condition.left = analyzeNode(node);
            condition.operation = BooleanOperator.MATCHES;
            Pattern pattern = ((RegExMatch)node).getPattern();
            condition.right = new FixedExpression(String.class, pattern.pattern());
        } else if (node instanceof Contains) {
            condition.left = analyzeNode(((Contains)node).getFirstStatement());
            condition.operation = BooleanOperator.CONTAINS;
            condition.right = analyzeNode(((Contains)node).getSecondStatement());
        } else if (node instanceof Soundslike) {
            condition.left = analyzeNode((ASTNode)getFieldValue(Soundslike.class, "stmt", (Soundslike)node));
            condition.operation = BooleanOperator.SOUNDSLIKE;
            condition.right = analyzeNode((ASTNode)getFieldValue(Soundslike.class, "soundslike", (Soundslike)node));
        } else {
            condition.left = analyzeNode(node);
        }
        return condition;
    }

    private CombinedCondition analyzeCombinedCondition(BooleanNode booleanNode, boolean isNegated) {
        CombinedCondition condition = new CombinedCondition(booleanNode instanceof And, isNegated);
        condition.addCondition(analyzeCondition(booleanNode.getLeft()));
        condition.addCondition(analyzeCondition(booleanNode.getRight()));
        return condition;
    }

    private ASTNode analyzeSubstatement(ASTNode node) {
        if (node instanceof Substatement) {
            return ((ExecutableAccessor)((Substatement)node).getStatement()).getNode();
        }
        return node;
    }

    private ASTNode analyzeRegEx(ASTNode node) {
        if (node instanceof RegExMatch) {
            return ((ExecutableAccessor)((RegExMatch)node).getStatement()).getNode();
        }
        return node;
    }

    private Expression analyzeNode(ASTNode node) {
        node = analyzeRegEx(analyzeSubstatement(node));

        if (node instanceof LiteralNode) {
            LiteralNode literalNode = (LiteralNode)node;
            return new FixedExpression(literalNode.getEgressType(), literalNode.getLiteralValue());
        }

        if (node instanceof BinaryOperation) {
            BinaryOperation op = (BinaryOperation)node;
            if (node.getClass() == BinaryOperation.class) {
                return new AritmeticExpression(analyzeNode(op.getLeft()), AritmeticOperator.fromMvelOpCode(op.getOperation()), analyzeNode(op.getRight()));
            } else {
                return new FixedExpression(op.getEgressType(), op.getReducedValue(parserContext, null, new ImmutableDefaultFactory()));
            }
        }

        if (node instanceof Union) {
            ASTNode main = getFieldValue(Union.class, "main", (Union)node);
            Accessor accessor = getFieldValue(Union.class, "accessor", (Union)node);

            EvaluatedExpression expression = new EvaluatedExpression();
            expression.firstExpression = analyzeNode(main);
            if (accessor instanceof DynamicGetAccessor) {
                AccessorNode accessorNode = (AccessorNode)((DynamicGetAccessor)accessor).getAccessor();
                expression.addInvocation(analyzeAccessor(accessorNode, null));
            } else if (accessor instanceof AccessorNode) {
                expression.addInvocation(analyzeAccessor((AccessorNode)accessor, null));
            } else {
                throw new RuntimeException("Unexpected accessor: " + accessor);
            }
            return expression;
        }

        Accessor accessor = node.getAccessor();

        if (accessor == null && node instanceof NewObjectNode) {
            accessor = ((NewObjectNode)node).getNewObjectOptimizer();
        }

        return analyzeAccessor(accessor);
    }

    private Expression analyzeAccessor(Accessor accessor) {
        AccessorNode accessorNode;

        if (accessor instanceof VariableAccessor) {
            VariableAccessor variableAccessor = (VariableAccessor)accessor;
            accessorNode = variableAccessor.getNextNode();
            if (accessorNode == null || !isStaticAccessor(accessorNode)) {
                String variableName = (String)(variableAccessor.getProperty());
                return new VariableExpression(variableName, analyzeExpressionNode(accessorNode), getVariableType(variableName));
            }
        }

        if (accessor instanceof DynamicGetAccessor) {
            accessorNode = (AccessorNode)((DynamicGetAccessor)accessor).getAccessor();
        } else if (accessor instanceof AccessorNode) {
            accessorNode = (AccessorNode)accessor;
        } else if (accessor instanceof CompiledExpression) {
            return analyzeNode(((CompiledExpression)accessor).getFirstNode());
        } else {
            throw new RuntimeException("Unknown accessor type: " + accessor);
        }

        if (accessorNode != null && accessorNode instanceof VariableAccessor) {
            if (isStaticAccessor(accessorNode)) {
                while (accessorNode != null && accessorNode instanceof VariableAccessor) {
                    accessorNode = accessorNode.getNextNode();
                }
            } else {
                return analyzeAccessor(accessorNode);
            }
        }

        while (accessorNode instanceof StaticReferenceAccessor) {
            StaticReferenceAccessor staticReferenceAccessor = ((StaticReferenceAccessor)accessorNode);
            Object literal = staticReferenceAccessor.getLiteral();
            accessorNode = accessorNode.getNextNode();
            if (accessorNode == null) {
                return new FixedExpression(literal.getClass(), literal);
            }
        }

        return analyzeExpressionNode(accessorNode);
    }

    private boolean isStaticAccessor(AccessorNode accessorNode) {
        while (accessorNode != null) {
            if (accessorNode instanceof StaticVarAccessor || accessorNode instanceof StaticReferenceAccessor) {
                return true;
            }
            if (accessorNode instanceof MethodAccessor) {
                Method method = ((MethodAccessor)accessorNode).getMethod();
                return (method.getModifiers() & Modifier.STATIC) > 0;
            }
            accessorNode = accessorNode.getNextNode();
        }
        return false;
    }

    private EvaluatedExpression analyzeExpressionNode(AccessorNode accessorNode) {
        if (accessorNode == null) return null;
        EvaluatedExpression expression = new EvaluatedExpression();
        Invocation invocation = null;
        while (accessorNode != null) {
            invocation = analyzeAccessor(accessorNode, invocation);
            if (invocation != null) {
                expression.addInvocation(invocation);
            }
            accessorNode = accessorNode.getNextNode();
        }
        return expression;
    }

    private Invocation analyzeAccessor(AccessorNode accessorNode, Invocation formerInvocation) {
        if (accessorNode instanceof GetterAccessor) {
            return new MethodInvocation(((GetterAccessor)accessorNode).getMethod());
        }

        if (accessorNode instanceof MethodAccessor) {
            MethodAccessor methodAccessor = (MethodAccessor)accessorNode;
            MethodInvocation invocation = new MethodInvocation(methodAccessor.getMethod());
            readInvocationParams(invocation, methodAccessor.getParms(), methodAccessor.getParameterTypes());
            return invocation;
        }

        if (accessorNode instanceof ConstructorAccessor) {
            ConstructorAccessor constructorAccessor = (ConstructorAccessor)accessorNode;
            Constructor constructor = constructorAccessor.getConstructor();
            ConstructorInvocation invocation = new ConstructorInvocation(constructor);
            readInvocationParams(invocation, constructorAccessor.getParameters(), constructorAccessor.getParameterTypes());
            return invocation;
        }

        if (accessorNode instanceof ArrayAccessor) {
            ArrayAccessor arrayAccessor = (ArrayAccessor)accessorNode;
            return new ArrayAccessInvocation(new FixedExpression(int.class, arrayAccessor.getIndex()));
        }

        if (accessorNode instanceof ArrayAccessorNest) {
            ArrayAccessorNest arrayAccessorNest = (ArrayAccessorNest)accessorNode;
            ExecutableAccessor index = (ExecutableAccessor)arrayAccessorNest.getIndex();
            return new ArrayAccessInvocation(analyzeNode(index.getNode()));
        }

        if (accessorNode instanceof ArrayLength) {
            return new ArrayLengthInvocation();
        }

        if (accessorNode instanceof ListAccessor) {
            Class<?> listType = getListType(formerInvocation);
            ListAccessor listAccessor = (ListAccessor)accessorNode;
            return new ListAccessInvocation(listType, new FixedExpression(int.class, listAccessor.getIndex()));
        }

        if (accessorNode instanceof ListAccessorNest) {
            Class<?> listType = getListType(formerInvocation);
            ListAccessorNest listAccessorNest = (ListAccessorNest)accessorNode;
            ExecutableAccessor index = (ExecutableAccessor)listAccessorNest.getIndex();
            return new ListAccessInvocation(listType, analyzeNode(index.getNode()));
        }

        if (accessorNode instanceof MapAccessor) {
            MapAccessor mapAccessor = (MapAccessor)accessorNode;
            return new MapAccessInvocation(Object.class, Object.class, new FixedExpression(Object.class, mapAccessor.getProperty()));
        }

        if (accessorNode instanceof MapAccessorNest) {
            Class<?> keyType = Object.class;
            Class<?> valueType = Object.class;
            Type[] generics = getGenerics(formerInvocation);
            if (generics != null && generics.length == 2 && generics[0] instanceof Class) {
                if (generics[0] instanceof Class) keyType = (Class<?>)generics[0];
                if (generics[1] instanceof Class) valueType = (Class<?>)generics[1];
            }
            MapAccessorNest mapAccessor = (MapAccessorNest)accessorNode;
            ExecutableStatement statement = mapAccessor.getProperty();
            if (statement instanceof ExecutableLiteral) {
                return new MapAccessInvocation(keyType, valueType, new FixedExpression(keyType, ((ExecutableLiteral)statement).getLiteral()));
            } else {
                return new MapAccessInvocation(keyType, valueType, analyzeNode(((ExecutableAccessor)statement).getNode()));
            }
        }

        if (accessorNode instanceof FieldAccessor) {
            return new FieldAccessInvocation(((FieldAccessor)accessorNode).getField());
        }

        if (accessorNode instanceof StaticVarAccessor) {
            Field field = ((StaticVarAccessor)accessorNode).getField();
            return new FieldAccessInvocation(field);
        }

        if (accessorNode instanceof ThisValueAccessor) {
            return new MethodInvocation(null);
        }

        throw new RuntimeException("Unknown AccessorNode type: " + accessorNode.getClass().getName());
    }

    private Class<?> getListType(Invocation formerInvocation) {
        Class<?> listType = Object.class;
        Type[] generics = getGenerics(formerInvocation);
        if (generics != null && generics.length == 1 && generics[0] instanceof Class) {
            listType = (Class<?>)generics[0];
        }
        return listType;
    }

    private Type[] getGenerics(Invocation invocation) {
        if (invocation != null && invocation instanceof MethodInvocation && ((MethodInvocation) invocation).getMethod() != null) {
            Type returnType = ((MethodInvocation) invocation).getMethod().getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                return ((ParameterizedType)returnType).getActualTypeArguments();
            }
        }
        return null;
    }

    private void readInvocationParams(Invocation invocation, ExecutableStatement[] params, Class[] paramTypes) {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ExecutableStatement param = params[i];
                if (param instanceof ExecutableLiteral) {
                    invocation.addArgument(new FixedExpression(paramTypes[i], ((ExecutableLiteral)param).getLiteral()));
                } else if (param instanceof ExecutableAccessor) {
                    invocation.addArgument(analyzeNode(((ExecutableAccessor)param).getNode()));
                }
            }
        }
    }

    private Class<?> getVariableType(String name) {
        if (vars == null) return Object.class;
        Object value = vars.get(name);
        return value == null ? Object.class : value.getClass();
    }

    private <T, V> V getFieldValue(Class<T> clazz, String fieldName, T object) {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return (V)f.get(object);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static abstract class Condition {
        private boolean negated;

        protected Condition(boolean negated) {
            this.negated = negated;
        }

        public boolean isNegated() {
            return negated;
        }

        public void toggleNegation() {
            negated = !negated;
        }
    }

    public static class FixedValueCondition extends Condition {

        private final boolean fixedValue;

        protected FixedValueCondition(boolean fixedValue) {
            super(false);
            this.fixedValue = fixedValue;
        }

        public boolean getFixedValue() {
            return fixedValue;
        }

        public void toggleNegation() {
            throw new UnsupportedOperationException();
        }
    }

    public static class SingleCondition extends Condition {
        private Expression left;
        private BooleanOperator operation;
        private Expression right;

        protected SingleCondition(boolean negated) {
            super(negated);
        }

        public Expression getLeft() {
            return left;
        }

        public Expression getRight() {
            return right;
        }

        public boolean isBinary() {
            return operation != null;
        }

        public BooleanOperator getOperation() {
            return operation;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(isNegated() ? "not " : "");
            sb.append(left);
            if (isBinary()) {
                sb.append(" ").append(operation);
                sb.append(" ").append(right);
            }
            return sb.toString();
        }
    }

    public static class CombinedCondition extends Condition {
        private final boolean isAnd;
        private List<Condition> conditions = new ArrayList<Condition>();

        protected CombinedCondition(boolean isAnd, boolean negated) {
            super(negated);
            this.isAnd = isAnd;
        }

        private void addCondition(Condition condition) {
            if (condition instanceof CombinedCondition && isAnd() == ((CombinedCondition)condition).isAnd()) {
                conditions.addAll(((CombinedCondition)condition).getConditions());
            } else {
                conditions.add(condition);
            }
        }

        public boolean isAnd() {
            return isAnd;
        }

        public List<Condition> getConditions() {
            return conditions;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(isNegated() ? "not (" : "(");
            Iterator<Condition> i = conditions.iterator();
            while (i.hasNext()) {
                sb.append(i.next());
                if (i.hasNext()) {
                    sb.append(isAnd ? " and " : " or ");
                }
            }
            return sb.append(")").toString();
        }
    }

    public interface Expression {
        boolean isFixed();
        boolean canBeNull();
        Class<?> getType();
    }

    public static class FixedExpression implements Expression {
        final TypedValue typedValue;

        FixedExpression(TypedValue value) {
            this.typedValue = value;
        }

        FixedExpression(Class<?> type, Object value) {
            this(new TypedValue(type, value));
        }

        public String toString() {
            return typedValue.toString();
        }

        public boolean isFixed() {
            return true;
        }

        public boolean canBeNull() {
            return typedValue.value == null;
        }

        public Class<?> getType() {
            return typedValue.type;
        }
    }

    public static class VariableExpression implements Expression {
        final String variableName;
        final EvaluatedExpression subsequentInvocations;
        final Class<?> type;

        VariableExpression(String variableName, EvaluatedExpression subsequentInvocations, Class<?> type) {
            this.variableName = variableName;
            this.subsequentInvocations = subsequentInvocations;
            this.type = type;
        }

        public boolean isFixed() {
            return false;
        }

        public boolean canBeNull() {
            return true;
        }

        public Class<?> getType() {
            return subsequentInvocations != null ? subsequentInvocations.getType() : Object.class;
        }

        public Class<?> getVariableType() {
            return subsequentInvocations != null ? subsequentInvocations.getType() : type;
        }

        public String toString() {
            return variableName + (subsequentInvocations == null ? "" : " . " + subsequentInvocations);
        }
    }

    public static class EvaluatedExpression implements Expression {
        Expression firstExpression;
        final List<Invocation> invocations = new ArrayList<Invocation>();

        void addInvocation(Invocation invocation) {
            invocations.add(invocation);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(firstExpression != null ? firstExpression.toString() : "");
            Iterator<Invocation> i = invocations.iterator();
            while (i.hasNext()) {
                sb.append(i.next());
                if (i.hasNext()) sb.append(" . ");
            }
            return sb.toString();
        }

        public boolean isFixed() {
            return false;
        }

        public boolean canBeNull() {
            return true;
        }

        public Class<?> getType() {
            return invocations.get(invocations.size()-1).getReturnType();
        }
    }

    public static class AritmeticExpression implements Expression {
        private final Class<?> type;
        final Expression left;
        final AritmeticOperator operator;
        final Expression right;

        public AritmeticExpression(Expression left, AritmeticOperator operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
            this.type = inferType();
        }

        public boolean isFixed() {
            return false;
        }

        public boolean canBeNull() {
            return true;
        }

        public Class<?> getType() {
            return type;
        }

        public String toString() {
            return left + " " + operator + " " + right;
        }

        public boolean isStringConcat() {
            return operator == AritmeticOperator.ADD && (left.getType() == String.class || right.getType() == String.class);
        }

        private Class<?> inferType() {
            if (isStringConcat()) {
                return String.class;
            }

            if (operator.isBitwiseOperation()) {
                Class<?> type = left instanceof VariableExpression ? ((VariableExpression)left).getVariableType() : left.getType();
                return type == Long.class ? long.class : int.class;
            }

            if (left.getType().isPrimitive() && left.getType() == right.getType()) {
                return left.getType();
            }

            return double.class;
        }
    }

    public static abstract class Invocation {
        private final List<Expression> arguments = new ArrayList<Expression>();

        public List<Expression> getArguments() {
            return arguments;
        }

        public void addArgument(Expression argument) {
            arguments.add(argument);
        }

        public abstract Class<?> getReturnType();
    }

    public static class MethodInvocation extends Invocation {
        private final Method method;

        public MethodInvocation(Method method) {
            this.method = getMethodFromSuperclass(method);
        }

        private Method getMethodFromSuperclass(Method method) {
            if (method == null) {
                return method;
            }
            Class<?> declaringClass = method.getDeclaringClass();
            Class<?> declaringSuperclass = declaringClass.getSuperclass();
            if (declaringSuperclass != null) {
                try {
                    return getMethodFromSuperclass(declaringSuperclass.getMethod(method.getName(), method.getParameterTypes()));
                } catch (Exception e) { }
            }
            for (Class<?> interfaze : declaringClass.getInterfaces()) {
                try {
                    return interfaze.getMethod(method.getName(), method.getParameterTypes());
                } catch (Exception e) { }
            }
            return method;
        }

        public Method getMethod() {
            return method;
        }

        public String toString() {
            if (method == null) return "this";
            return method + (!getArguments().isEmpty() ? " with " + getArguments() : "");
        }

        public Class<?> getReturnType() {
            return method != null ? method.getReturnType() : Object.class;
        }
    }

    public static class ConstructorInvocation extends Invocation {
        private final Constructor constructor;

        public ConstructorInvocation(Constructor constructor) {
            this.constructor = constructor;
        }

        public Constructor getConstructor() {
            return constructor;
        }

        public String toString() {
            return "new " + getReturnType() + (!getArguments().isEmpty() ? " with " + getArguments() : "");
        }

        public Class<?> getReturnType() {
            return constructor.getDeclaringClass();
        }
    }

    public static class ListAccessInvocation extends Invocation {
        private final Class<?> listType;
        private final Expression index;

        public ListAccessInvocation(Class<?> listType, Expression index) {
            this.listType = listType;
            this.index = index;
        }

        public Expression getIndex() {
            return index;
        }

        public String toString() {
            return ".get(" + index + ")";
        }

        public Class<?> getReturnType() {
            return listType;
        }
    }

    public static class ArrayAccessInvocation extends Invocation {
        private final Expression index;

        public ArrayAccessInvocation(Expression index) {
            this.index = index;
        }

        public Expression getIndex() {
            return index;
        }

        public String toString() {
            return "[" + index + "]";
        }

        public Class<?> getReturnType() {
            return Object.class;
        }
    }

    public static class ArrayLengthInvocation extends Invocation {
        public Class<?> getReturnType() {
            return int.class;
        }
    }

    public static class MapAccessInvocation extends Invocation {
        private final Class<?> keyType;
        private final Class<?> valueType;
        private final Expression key;

        public MapAccessInvocation(Class<?> keyType, Class<?> valueType, Expression key) {
            this.keyType = keyType;
            this.valueType = valueType;
            this.key = key;
        }

        public Expression getKey() {
            return key;
        }

        public String toString() {
            return "[\"" + key + "\"]";
        }

        public Class<?> getKeyType() {
            return keyType;
        }

        public Class<?> getReturnType() {
            return valueType;
        }
    }

    public static class FieldAccessInvocation extends Invocation {
        private final Field field;

        public FieldAccessInvocation(Field field) {
            this.field = field;
        }

        public Field getField() {
            return field;
        }

        public String toString() {
            return field.getName();
        }

        public Class<?> getReturnType() {
            return field.getType();
        }
    }

    public static class TypedValue {
        Class<?> type;
        Object value;

        TypedValue(Class<?> type, Object value) {
            this.type = type;
            this.value = value;
        }

        public String toString() {
            return type.getSimpleName() + " " + value;
        }
    }

    public enum BooleanOperator {
        EQ("=="), NE("!="), GT(">"), GE(">="), LT("<"), LE("<="), MATCHES("~="), CONTAINS("in"), SOUNDSLIKE("like");

        private String symbol;

        BooleanOperator(String symbol) {
            this.symbol = symbol;
        }

        public String toString() {
            return symbol;
        }

        public boolean isEquality() {
            return this == EQ || this == NE;
        }

        public boolean needsSameType() {
            return this != CONTAINS;
        }

        public boolean isComparison() {
            return this == GT || this == GE || this == LT || this == LE;
        }

        public static BooleanOperator fromMvelOpCode(int opCode) {
            switch (opCode) {
                case Operator.EQUAL: return EQ;
                case Operator.NEQUAL: return NE;
                case Operator.GTHAN: return GT;
                case Operator.GETHAN: return GE;
                case Operator.LTHAN: return LT;
                case Operator.LETHAN: return LE;
            }
            throw new RuntimeException("Unknown boolean operator");
        }
    }

    public enum AritmeticOperator {
        ADD("+"), SUB("-"), MUL("*"), DIV("/"), MOD("%"), POW("^"),
        BW_AND("&"), BW_OR("|"), BW_XOR("^"), BW_SHIFT_RIGHT(">>"), BW_SHIFT_LEFT("<<"), BW_USHIFT_RIGHT(">>>"), BW_USHIFT_LEFT("<<<");

        private String symbol;

        AritmeticOperator(String symbol) {
            this.symbol = symbol;
        }

        public String toString() {
            return symbol;
        }

        public boolean isBitwiseOperation() {
            return this == BW_AND || this == BW_OR || this == BW_SHIFT_RIGHT || this == BW_SHIFT_LEFT || this == BW_USHIFT_RIGHT || this == BW_USHIFT_LEFT;
        }

        public static AritmeticOperator fromMvelOpCode(int opCode) {
            switch (opCode) {
                case Operator.ADD: return ADD;
                case Operator.SUB: return SUB;
                case Operator.MULT: return MUL;
                case Operator.DIV: return DIV;
                case Operator.MOD: return MOD;
                case Operator.BW_AND: return BW_AND;
                case Operator.BW_OR: return BW_OR;
                case Operator.BW_SHIFT_RIGHT: return BW_SHIFT_RIGHT;
                case Operator.BW_SHIFT_LEFT: return BW_SHIFT_LEFT;
                case Operator.BW_USHIFT_RIGHT: return BW_USHIFT_RIGHT;
                case Operator.BW_USHIFT_LEFT: return BW_USHIFT_LEFT;
            }
            throw new RuntimeException("Unknown boolean operator");
        }
    }
}
