package org.drools.rule.constraint;

import org.mvel2.Operator;
import org.mvel2.ParserContext;
import org.mvel2.ast.ASTNode;
import org.mvel2.ast.BinaryOperation;
import org.mvel2.ast.Contains;
import org.mvel2.ast.LiteralNode;
import org.mvel2.ast.Negation;
import org.mvel2.ast.NewObjectNode;
import org.mvel2.ast.RegExMatch;
import org.mvel2.ast.Substatement;
import org.mvel2.compiler.Accessor;
import org.mvel2.compiler.AccessorNode;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.compiler.ExecutableLiteral;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.impl.ImmutableDefaultFactory;
import org.mvel2.optimizers.dynamic.DynamicGetAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ConstructorAccessor;
import org.mvel2.optimizers.impl.refl.nodes.FieldAccessor;
import org.mvel2.optimizers.impl.refl.nodes.GetterAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ListAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ListAccessorNest;
import org.mvel2.optimizers.impl.refl.nodes.MapAccessorNest;
import org.mvel2.optimizers.impl.refl.nodes.MethodAccessor;
import org.mvel2.optimizers.impl.refl.nodes.StaticReferenceAccessor;
import org.mvel2.optimizers.impl.refl.nodes.StaticVarAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ThisValueAccessor;
import org.mvel2.optimizers.impl.refl.nodes.VariableAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.regex.Pattern;

public class AnalyzedCondition {

    private boolean negated;
    private Expression left;
    private BooleanOperator operation;
    private Expression right;
    private ParserContext parserContext;

    public AnalyzedCondition(ExecutableStatement stmt) {
        ASTNode node;
        if (stmt instanceof CompiledExpression) {
            parserContext = ((CompiledExpression)stmt).getParserContext();
            node = ((CompiledExpression)stmt).getFirstNode();
        } else {
            node = ((ExecutableAccessor)stmt).getNode();
        }
        analyzeExpression(node);
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

    public boolean isNegated() {
        return negated;
    }

    public void toggleNegation() {
        negated = !negated;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (negated) sb.append("not ");
        sb.append(left);
        if (isBinary()) {
            sb.append(" ").append(operation);
            sb.append(" ").append(right);
        }
        return sb.toString();
    }

    private void analyzeExpression(ASTNode node) {
        while (node.nextASTNode != null) node = node.nextASTNode;
        node = analyzeNegation(node);
        node = analyzeSubstatement(node);

        if (node instanceof BinaryOperation) {
            BinaryOperation binaryOperation = (BinaryOperation)node;
            left = analyzeNode(binaryOperation.getLeft());
            operation = BooleanOperator.fromMvelOpCode(binaryOperation.getOperation());
            right = analyzeNode(binaryOperation.getRight());
        } else if (node instanceof RegExMatch) {
            left = analyzeNode(node);
            operation = BooleanOperator.MATCHES;
            Pattern pattern = getFieldValue(RegExMatch.class, "p", (RegExMatch)node);
            right = new FixedExpression(String.class, pattern.pattern());
        } else if (node instanceof Contains) {
            left = analyzeNode((ASTNode)getFieldValue(Contains.class, "stmt", (Contains)node));
            operation = BooleanOperator.CONTAINS;
            right = analyzeNode((ASTNode)getFieldValue(Contains.class, "stmt2", (Contains)node));
        } else {
            left = analyzeNode(node);
        }
    }

    private ASTNode analyzeNegation(ASTNode node) {
        if (node instanceof Negation) {
            negated = true;
            ExecutableAccessor executableAccessor = getFieldValue(Negation.class, "stmt", (Negation)node);
            node = executableAccessor.getNode();
        }
        return node;
    }

    private ASTNode analyzeSubstatement(ASTNode node) {
        if (node instanceof Substatement) {
            ExecutableAccessor executableAccessor = (ExecutableAccessor)((Substatement)node).getStatement();
            node = executableAccessor.getNode();
        }
        return node;
    }

    private Expression analyzeNode(ASTNode node) {
        if (node instanceof RegExMatch) {
            ExecutableAccessor executableAccessor = getFieldValue(RegExMatch.class, "stmt", (RegExMatch)node);
            node = executableAccessor.getNode();
        }

        if (node instanceof LiteralNode) {
            LiteralNode literalNode = (LiteralNode)node;
            return new FixedExpression(literalNode.getEgressType(), literalNode.getLiteralValue());
        }

        if (node instanceof BinaryOperation) {
            BinaryOperation binaryOperation = (BinaryOperation)node;
            Object value = binaryOperation.getReducedValue(parserContext, null, new ImmutableDefaultFactory());
            return new FixedExpression(binaryOperation.getEgressType(), value);
        }

        Accessor accessor = node.getAccessor();
        if (accessor == null && node instanceof NewObjectNode) {
            accessor = getFieldValue(NewObjectNode.class, "newObjectOptimizer", (NewObjectNode)node);
        }

        AccessorNode accessorNode = null;

        if (accessor instanceof DynamicGetAccessor) {
            accessorNode = getFieldValue(DynamicGetAccessor.class, "_accessor", (DynamicGetAccessor)accessor);
        } else if (accessor instanceof AccessorNode) {
            accessorNode = (AccessorNode)accessor;
        } else if (accessor instanceof CompiledExpression) {
            return analyzeNode(((CompiledExpression)accessor).getFirstNode());
        } else {
            throw new RuntimeException("Unknown expression type: " + node);
        }

        while (accessorNode != null && accessorNode instanceof VariableAccessor) {
            accessorNode = accessorNode.getNextNode();
        }

        while (accessorNode instanceof StaticReferenceAccessor) {
            StaticReferenceAccessor staticReferenceAccessor = ((StaticReferenceAccessor)accessorNode);
            Object literal = staticReferenceAccessor.getLiteral();
            accessorNode = accessorNode.getNextNode();
            if (accessorNode == null) return new FixedExpression(literal.getClass(), literal);
        }

        EvaluatedExpression expression = new EvaluatedExpression();
        Invocation invocation = null;
        while (accessorNode != null) {
            invocation = analyzeAccessor(accessorNode, invocation);
            if (invocation != null) expression.addInvocation(invocation);
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
            ExecutableStatement[] params = methodAccessor.getParms();
            Class[] paramTypes = getFieldValue(MethodAccessor.class, "parameterTypes", methodAccessor);
            readInvocationParams(invocation, params, paramTypes);
            return invocation;
        }

        if (accessorNode instanceof ConstructorAccessor) {
            ConstructorAccessor constructorAccessor = (ConstructorAccessor)accessorNode;
            Constructor constructor = getFieldValue(ConstructorAccessor.class, "constructor", constructorAccessor);
            ConstructorInvocation invocation = new ConstructorInvocation(constructor);
            ExecutableStatement[] params = getFieldValue(ConstructorAccessor.class, "parms", constructorAccessor);
            Class[] paramTypes = getFieldValue(ConstructorAccessor.class, "parmTypes", constructorAccessor);
            readInvocationParams(invocation, params, paramTypes);
            return invocation;
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
            StaticVarAccessor staticVarAccessor = ((StaticVarAccessor)accessorNode);
            Field field = getFieldValue(StaticVarAccessor.class, "field", staticVarAccessor);
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
        if (invocation != null && invocation instanceof MethodInvocation) {
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

    public interface Expression {
        boolean isFixed();
        boolean canBeNull();
        Class<?> getType();
    }

    public static class FixedExpression implements Expression {
        TypedValue typedValue;

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

    public static class EvaluatedExpression implements Expression {
        List<Invocation> invocations = new ArrayList<Invocation>();

        void addInvocation(Invocation invocation) {
            invocations.add(invocation);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
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
            this.method = method;
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
            return "[" + index + "]";
        }

        public Class<?> getReturnType() {
            return listType;
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
        EQ("=="), NE("!="), GT(">"), GE(">="), LT("<"), LE("<="), MATCHES("~="), CONTAINS("in");

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

        public static BooleanOperator fromMvelOpCode(int opCode) {
            switch (opCode) {
                case Operator.EQUAL: return EQ;
                case Operator.NEQUAL: return NE;
                case Operator.GTHAN: return GT;
                case Operator.GETHAN: return GE;
                case Operator.LTHAN: return LT;
                case Operator.LETHAN: return LE;
            }
            throw new RuntimeException("Unknown opeation");
        }
    }
}
