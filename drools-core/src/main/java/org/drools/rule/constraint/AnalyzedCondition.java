package org.drools.rule.constraint;

import org.mvel2.Operator;
import org.mvel2.ast.ASTNode;
import org.mvel2.ast.BinaryOperation;
import org.mvel2.ast.LiteralNode;
import org.mvel2.ast.Negation;
import org.mvel2.ast.RegExMatch;
import org.mvel2.ast.Substatement;
import org.mvel2.compiler.Accessor;
import org.mvel2.compiler.AccessorNode;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.compiler.ExecutableLiteral;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.optimizers.dynamic.DynamicGetAccessor;
import org.mvel2.optimizers.impl.refl.nodes.GetterAccessor;
import org.mvel2.optimizers.impl.refl.nodes.MethodAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.regex.Pattern;

public class AnalyzedCondition {

    private boolean negated;
    private Expression left;
    private BooleanOperator operation;
    private Expression right;

    public AnalyzedCondition(CompiledExpression compiledExpression) {
        analyzeExpression(compiledExpression);
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

    private void analyzeExpression(CompiledExpression compiledExpression) {
        ASTNode node = compiledExpression.getFirstNode();
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
            right = new FixedExpression(new TypedValue(String.class, pattern.pattern()));
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
            return new FixedExpression(new TypedValue(literalNode.getEgressType(), literalNode.getLiteralValue()));
        }

        Accessor accessor = node.getAccessor();
        EvaluatedExpression expression = new EvaluatedExpression();
        AccessorNode accessorNode = null;

        if (accessor instanceof DynamicGetAccessor) {
            accessorNode = getFieldValue(DynamicGetAccessor.class, "_accessor", (DynamicGetAccessor)accessor);
        } else if (accessor instanceof AccessorNode) {
            accessorNode = (AccessorNode)accessor;
        } else {
            throw new RuntimeException("Unknown expression type: " + node);
        }

        while (accessorNode != null) {
            expression.addInvocation(analyzeAccessor(accessorNode));
            accessorNode = accessorNode.getNextNode();
        }

        return expression;
    }

    private Invocation analyzeAccessor(AccessorNode accessorNode) {
        Invocation invocation = new Invocation();
        if (accessorNode instanceof GetterAccessor) {
            invocation.method = ((GetterAccessor)accessorNode).getMethod();
        } else if (accessorNode instanceof MethodAccessor) {
            MethodAccessor methodAccessor = (MethodAccessor)accessorNode;
            invocation.method = methodAccessor.getMethod();
            ExecutableStatement[] params = methodAccessor.getParms();
            if (params != null) {
                int i = 0;
                Class[] paramTypes = getFieldValue(MethodAccessor.class, "parameterTypes", methodAccessor);
                for (ExecutableStatement param : params) {
                    if (param instanceof ExecutableLiteral) {
                        Object literal = ((ExecutableLiteral)param).getLiteral();
                        invocation.addArgument(new FixedExpression(new TypedValue(paramTypes[i++], literal)));
                    }
                }
            }
        }
        return invocation;
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

    public static class Invocation {
        Method method;
        List<Expression> arguments;

        void addArgument(Expression argument) {
            if (arguments == null) arguments = new ArrayList<Expression>();
            arguments.add(argument);
        }

        public String toString() {
            if (method == null) return "this";
            return method + (arguments != null ? " with " + arguments : "");
        }

        public Class<?> getReturnType() {
            return method != null ? method.getReturnType() : Object.class;
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
        EQ("=="), NE("!="), GT(">"), GE(">="), LT("<"), LE("<="), MATCHES("~=");

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
