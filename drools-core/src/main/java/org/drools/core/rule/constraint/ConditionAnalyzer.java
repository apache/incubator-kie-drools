package org.drools.core.rule.constraint;

import org.drools.core.rule.Declaration;
import org.mvel2.Operator;
import org.mvel2.ParserContext;
import org.mvel2.ast.ASTNode;
import org.mvel2.ast.And;
import org.mvel2.ast.BinaryOperation;
import org.mvel2.ast.BooleanNode;
import org.mvel2.ast.Contains;
import org.mvel2.ast.Instance;
import org.mvel2.ast.LiteralNode;
import org.mvel2.ast.Negation;
import org.mvel2.ast.NewObjectNode;
import org.mvel2.ast.Or;
import org.mvel2.ast.RegExMatch;
import org.mvel2.ast.Sign;
import org.mvel2.ast.Soundslike;
import org.mvel2.ast.Substatement;
import org.mvel2.ast.TypeCast;
import org.mvel2.ast.Union;
import org.mvel2.compiler.Accessor;
import org.mvel2.compiler.AccessorNode;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.compiler.ExecutableLiteral;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.optimizers.dynamic.DynamicGetAccessor;
import org.mvel2.optimizers.impl.refl.collection.ArrayCreator;
import org.mvel2.optimizers.impl.refl.collection.ExprValueAccessor;
import org.mvel2.optimizers.impl.refl.collection.ListCreator;
import org.mvel2.optimizers.impl.refl.nodes.ArrayAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ArrayAccessorNest;
import org.mvel2.optimizers.impl.refl.nodes.ArrayLength;
import org.mvel2.optimizers.impl.refl.nodes.ConstructorAccessor;
import org.mvel2.optimizers.impl.refl.nodes.FieldAccessor;
import org.mvel2.optimizers.impl.refl.nodes.GetterAccessor;
import org.mvel2.optimizers.impl.refl.nodes.IndexedVariableAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ListAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ListAccessorNest;
import org.mvel2.optimizers.impl.refl.nodes.MapAccessor;
import org.mvel2.optimizers.impl.refl.nodes.MapAccessorNest;
import org.mvel2.optimizers.impl.refl.nodes.MethodAccessor;
import org.mvel2.optimizers.impl.refl.nodes.StaticReferenceAccessor;
import org.mvel2.optimizers.impl.refl.nodes.StaticVarAccessor;
import org.mvel2.optimizers.impl.refl.nodes.ThisValueAccessor;
import org.mvel2.optimizers.impl.refl.nodes.VariableAccessor;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static org.drools.core.util.ClassUtils.convertToPrimitiveType;

public class ConditionAnalyzer {

    private ASTNode node;
    private ExecutableLiteral executableLiteral;
    private final Declaration[] declarations;
    private final String conditionClass;

    public ConditionAnalyzer(ExecutableStatement stmt, Declaration[] declarations, String conditionClass) {
        this.declarations = declarations;
        this.conditionClass = conditionClass;
        if (stmt instanceof ExecutableLiteral) {
            executableLiteral = (ExecutableLiteral)stmt;
        } else if (stmt instanceof CompiledExpression) {
            node = ((CompiledExpression)stmt).getFirstNode();
        } else {
            node = ((ExecutableAccessor)stmt).getNode();
        }
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
            ExecutableStatement statement = ((Negation)node).getStatement();
            if (statement instanceof ExecutableLiteral) {
                return new FixedValueCondition(!(Boolean)((ExecutableLiteral)statement).getLiteral());
            }
            node = ((ExecutableAccessor)statement).getNode();
        }
        node = analyzeSubstatement(node);

        if (node instanceof LiteralNode && node.getEgressType() == Boolean.class) {
            boolean literalValue = (Boolean)node.getLiteralValue();
            return new FixedValueCondition(isNegated ? !literalValue : literalValue);
        }

        if (node instanceof Negation) {
            isNegated = !isNegated;
            node = ((ExecutableAccessor)((Negation)node).getStatement()).getNode();
            node = analyzeSubstatement(node);
        }
        if (node instanceof And || node instanceof Or) {
            return analyzeCombinedCondition((BooleanNode)node, isNegated);
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
            RegExMatch regExNode = (RegExMatch)node;
            Pattern pattern = regExNode.getPattern();
            if (pattern != null) {
                condition.right = new FixedExpression(String.class, pattern.pattern());
            } else {
                condition.right = analyzeNode(((ExecutableAccessor)regExNode.getPatternStatement()).getNode());
            }
        } else if (node instanceof Contains) {
            condition.left = analyzeNode(((Contains)node).getFirstStatement());
            condition.operation = BooleanOperator.CONTAINS;
            condition.right = analyzeNode(((Contains)node).getSecondStatement());
        } else if (node instanceof Soundslike) {
            condition.left = analyzeNode(((Soundslike) node).getStatement());
            condition.operation = BooleanOperator.SOUNDSLIKE;
            condition.right = analyzeNode(((Soundslike)node).getSoundslike());
        } else if (node instanceof Instance) {
            condition.left = analyzeNode(((Instance) node).getStatement());
            condition.operation = BooleanOperator.INSTANCEOF;
            condition.right = analyzeNode(((Instance) node).getClassStatement());
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
            return new AritmeticExpression(analyzeNode(op.getLeft()), AritmeticOperator.fromMvelOpCode(op.getOperation()), analyzeNode(op.getRight()));
        }

        if (node instanceof TypeCast) {
            ExecutableStatement statement = ((TypeCast)node).getStatement();
            if (statement instanceof ExecutableAccessor) {
                ExecutableAccessor accessor = (ExecutableAccessor) statement;
                return new CastExpression(node.getEgressType(), analyzeNode(accessor.getNode()));
            } else {
                ExecutableLiteral literal = (ExecutableLiteral) statement;
                return new CastExpression(node.getEgressType(), new FixedExpression(literal.getLiteral()));
            }
        }

        if (node instanceof Union) {
            ASTNode main = ((Union)node).getMain();
            Accessor accessor = node.getAccessor();

            EvaluatedExpression expression = new EvaluatedExpression();
            expression.firstExpression = analyzeNode(main);
            if (accessor instanceof DynamicGetAccessor) {
                AccessorNode accessorNode = (AccessorNode)((DynamicGetAccessor)accessor).getSafeAccessor();
                expression.addInvocation(analyzeAccessorInvocation(accessorNode, node, null, null));
            } else if (accessor instanceof AccessorNode) {
                AccessorNode accessorNode = (AccessorNode)accessor;
                while (accessorNode != null) {
                    expression.addInvocation(analyzeAccessorInvocation(accessorNode, node, null, null));
                    accessorNode = accessorNode.getNextNode();
                }
            } else {
                throw new RuntimeException("Unexpected accessor: " + accessor);
            }
            return expression;
        }

        if (node instanceof Sign) {
            ExecutableStatement statement = getFieldValue(Sign.class, "stmt", (Sign) node);
            if (statement instanceof ExecutableAccessor) {
                ExecutableAccessor accessor = (ExecutableAccessor) statement;
                return new AritmeticExpression(new FixedExpression(0), AritmeticOperator.SUB, analyzeNode(accessor.getNode()));
            } else {
                ExecutableLiteral literal = (ExecutableLiteral) statement;
                return new AritmeticExpression(new FixedExpression(0), AritmeticOperator.SUB, new FixedExpression(literal.getLiteral()));
            }
        }

        Accessor accessor = node.getAccessor();

        if (accessor instanceof IndexedVariableAccessor) {
            String variableName = node.getName();
            int dot = variableName.indexOf('.');
            if (dot > 0) {
                variableName = variableName.substring(0, dot);
            }
            Class<?> variableType = getVariableType(variableName);
            return new VariableExpression(variableName,
                                          analyzeExpressionNode(((AccessorNode) accessor).getNextNode(), node, variableType),
                                          variableType != null ? variableType : node.getEgressType());
        }

        if (accessor == null && node instanceof NewObjectNode) {
            accessor = ((NewObjectNode)node).getNewObjectOptimizer();
        }

        if (accessor instanceof VariableAccessor) {
            VariableAccessor variableAccessor = (VariableAccessor)accessor;
            AccessorNode accessorNode = variableAccessor.getNextNode();
            if (accessorNode == null || !isStaticAccessor(accessorNode)) {
                String variableName = (String)(variableAccessor.getProperty());
                Class<?> variableType = getVariableType(variableName);
                if (variableType != null) {
                    return new VariableExpression(variableName, analyzeExpressionNode(accessorNode, node, variableType), variableType);
                } else {
                    if (node.getLiteralValue() instanceof ParserContext) {
                        ParserContext pCtx = (ParserContext)node.getLiteralValue();
                        // it's not a variable but a method invocation on this
                        Class<?> thisClass = pCtx.getInputs().get("this");
                        try {
                            return new EvaluatedExpression(new MethodInvocation(thisClass.getMethod(variableName)));
                        } catch (NoSuchMethodException e) {
                            if (node.getEgressType() == Class.class) {
                                // there's no method on this with the given name, check if it is a class literal
                                Class<?> classLiteral = pCtx.getParserConfiguration().getImport(variableName);
                                if (classLiteral != null) {
                                    return new FixedExpression(Class.class, classLiteral);
                                }
                            }
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        if (accessor == null) {
            throw new RuntimeException("Null accessor on node: " + node);
        }

        return analyzeNodeAccessor(accessor, node);
    }

    private Expression analyzeNodeAccessor(Accessor accessor, ASTNode node) {
        AccessorNode accessorNode;
        if (accessor instanceof DynamicGetAccessor) {
            accessorNode = (AccessorNode)((DynamicGetAccessor)accessor).getSafeAccessor();
        } else if (accessor instanceof AccessorNode) {
            accessorNode = (AccessorNode)accessor;
        } else if (accessor instanceof CompiledExpression) {
            return analyzeNode(((CompiledExpression)accessor).getFirstNode());
        } else if (accessor instanceof ListCreator) {
            return analyzeListCreation(((ListCreator) accessor));
        } else if (accessor instanceof ArrayCreator) {
            return analyzeArrayCreation(((ArrayCreator) accessor));
        } else {
            throw new RuntimeException("Unknown accessor type: " + accessor);
        }

        if (accessorNode instanceof VariableAccessor) {
            if (isStaticAccessor(accessorNode)) {
                while (accessorNode instanceof VariableAccessor) {
                    accessorNode = accessorNode.getNextNode();
                }
            } else {
                return analyzeNodeAccessor(accessorNode, node);
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

        return analyzeExpressionNode(accessorNode, node, null);
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

    private Expression analyzeArrayCreation(ArrayCreator arrayCreator) {
        Accessor[] accessors = getFieldValue(ArrayCreator.class, "template", (ArrayCreator) arrayCreator);
        Class<?> type = arrayCreator.getKnownEgressType();
        Class<?> arrayType = Array.newInstance(type, 0).getClass();
        return getArrayCreationExpression( arrayType, type, accessors );
    }

    private EvaluatedExpression analyzeListCreation(ListCreator listCreator) {
        Method listCreationMethod = null;
        try {
            listCreationMethod = Arrays.class.getMethod("asList", Object[].class);
        } catch (NoSuchMethodException e) { }

        Invocation invocation = new MethodInvocation(listCreationMethod);
        invocation.addArgument( getArrayCreationExpression( Object[].class, Object.class, listCreator.getValues() ) );
        return new EvaluatedExpression(invocation);
    }

    private ArrayCreationExpression getArrayCreationExpression(Class<?> arrayType, Class<?> type, Accessor[] accessors) {
        ArrayCreationExpression arrayExpression = new ArrayCreationExpression(arrayType);
        for (Accessor accessor : accessors) {
            ExecutableStatement statement = ((ExprValueAccessor)accessor).getStmt();
            arrayExpression.addItem(statementToExpression(statement, type));
        }
        return arrayExpression;
    }

    private EvaluatedExpression analyzeExpressionNode(AccessorNode accessorNode, ASTNode containingNode, Class<?> variableType) {
        if (accessorNode == null) return null;
        EvaluatedExpression expression = new EvaluatedExpression();
        Invocation invocation = null;
        while (accessorNode != null) {
            invocation = analyzeAccessorInvocation(accessorNode, containingNode, invocation, variableType);
            if (invocation != null) {
                expression.addInvocation(invocation);
            }
            accessorNode = accessorNode.getNextNode();
        }
        return expression;
    }

    private Invocation analyzeAccessorInvocation(AccessorNode accessorNode, ASTNode containingNode, Invocation formerInvocation, Class<?> variableType) {
        if (accessorNode instanceof GetterAccessor) {
            return new MethodInvocation(((GetterAccessor)accessorNode).getMethod(), variableType == null ? conditionClass : variableType.getName());
        }

        if (accessorNode instanceof MethodAccessor) {
            MethodAccessor methodAccessor = (MethodAccessor)accessorNode;
            Method method = methodAccessor.getMethod();
            MethodInvocation invocation = new MethodInvocation(method);
            boolean isVarArgs = method.isVarArgs();
            readInvocationParams(invocation, methodAccessor.getParms(), methodAccessor.getParameterTypes(), isVarArgs);
            return invocation;
        }

        if (accessorNode instanceof ConstructorAccessor) {
            ConstructorAccessor constructorAccessor = (ConstructorAccessor)accessorNode;
            Constructor constructor = constructorAccessor.getConstructor();
            ConstructorInvocation invocation = new ConstructorInvocation(constructor);
            readInvocationParams(invocation, constructorAccessor.getParameters(), constructorAccessor.getParameterTypes(), constructor.isVarArgs());
            return invocation;
        }

        if (accessorNode instanceof ArrayAccessor) {
            ArrayAccessor arrayAccessor = (ArrayAccessor)accessorNode;
            return new ArrayAccessInvocation( formerInvocation != null ? formerInvocation.getReturnType() : Object[].class,
                                              new FixedExpression(int.class, arrayAccessor.getIndex()) );
        }

        if (accessorNode instanceof ArrayAccessorNest) {
            ArrayAccessorNest arrayAccessorNest = (ArrayAccessorNest)accessorNode;
            ExecutableAccessor index = (ExecutableAccessor)arrayAccessorNest.getIndex();
            return new ArrayAccessInvocation( formerInvocation != null ? formerInvocation.getReturnType() : Object[].class,
                                              analyzeNode(index.getNode()) );
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
            return new ThisInvocation(accessorNode.getNextNode() == null ? containingNode.getEgressType() : Object.class);
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

    private void readInvocationParams(Invocation invocation, ExecutableStatement[] params, Class[] paramTypes, boolean isVarArgs) {
        if (params == null) {
            return;
        }

        for (int i = 0; i < (isVarArgs ? paramTypes.length-1 : paramTypes.length); i++) {
            invocation.addArgument(statementToExpression(params[i], paramTypes[i]));
        }

        if (isVarArgs) {
            Class<?> varargType = paramTypes[paramTypes.length-1];
            ArrayCreationExpression varargParam = new ArrayCreationExpression(varargType);
            for (int i = paramTypes.length-1; i < params.length; i++) {
                varargParam.addItem(statementToExpression(params[i], varargType.getComponentType()));
            }
            invocation.addArgument(varargParam);
        }
    }

    private Expression statementToExpression(ExecutableStatement param, Class paramType) {
        if (param instanceof ExecutableLiteral) {
            return new FixedExpression(paramType, ((ExecutableLiteral)param).getLiteral());
        } else if (param instanceof ExecutableAccessor) {
            return analyzeNode(((ExecutableAccessor)param).getNode());
        } else {
            throw new RuntimeException("Unknown ExecutableStatement type: " + param);
        }
    }

    private Class<?> getVariableType(String name) {
        for (Declaration declaration : declarations) {
            if (declaration.getBindingName().equals(name)) {
                if (declaration.getExtractor() != null) {
                    return declaration.getExtractor().getExtractToClass();
                } else {
                    // TODO when can declaration.getExtractor() be null? (mdp)
                    return declaration.getValueType().getClassType();
                }
            }
        }
        return null;
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
        boolean canBeNull();
        Class<?> getType();
    }

    public static boolean isFixed(Expression expression) {
        return expression instanceof FixedExpression;
    }

    public static class FixedExpression implements Expression {
        private final Class<?> type;
        private final Object value;

        FixedExpression(Object value) {
            this(value.getClass(), value);
        }

        FixedExpression(Class<?> type, Object value) {
            this.type = type == CharSequence.class ? String.class : type;
            this.value = value;
        }

        public String toString() {
            return type.getSimpleName() + " " + value;
        }

        public boolean canBeNull() {
            return value == null;
        }

        public Class<?> getType() {
            return type;
        }

        public Class<?> getTypeAsPrimitive() {
            if (type == Integer.class) return int.class;
            if (type == Long.class) return long.class;
            if (type == Float.class) return float.class;
            if (type == Double.class) return double.class;
            if (type == Short.class) return short.class;
            if (type == Byte.class) return byte.class;
            if (type == Character.class) return char.class;
            if (type == Boolean.class) return boolean.class;
            return type;
        }

        public Object getValue() {
            return value;
        }
    }

    public static class VariableExpression implements Expression {
        final String variableName;
        final EvaluatedExpression subsequentInvocations;
        final Class<?> variableType;

        VariableExpression(String variableName, EvaluatedExpression subsequentInvocations, Class<?> variableType) {
            this.variableName = variableName;
            this.subsequentInvocations = subsequentInvocations;
            this.variableType = variableType;
        }

        public boolean canBeNull() {
            return true;
        }

        public Class<?> getVariableType() {
            return variableType;
        }

        public Class<?> getType() {
            return subsequentInvocations != null ? subsequentInvocations.getType() : variableType;
        }

        public String toString() {
            return variableName + (subsequentInvocations == null ? "" : " . " + subsequentInvocations);
        }
    }

    public static class EvaluatedExpression implements Expression {
        Expression firstExpression;
        final List<Invocation> invocations = new ArrayList<Invocation>();

        EvaluatedExpression () { }

        EvaluatedExpression (Invocation invocation ) {
            addInvocation(invocation);
        }

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
                Class<?> type = left.getType();
                return type == long.class || type == Long.class ? long.class : int.class;
            }

            if (left.getType().isPrimitive() && left.getType() == right.getType()) {
                return left.getType();
            }

            if (left.getType() == BigDecimal.class || right.getType() == BigDecimal.class) {
                return BigDecimal.class;
            }

            if (left.getType() == BigInteger.class || right.getType() == BigInteger.class) {
                return BigInteger.class;
            }

            Class<?> primitiveLeft = convertToPrimitiveType(left.getType());
            Class<?> primitiveRight = convertToPrimitiveType(right.getType());
            return primitiveLeft == primitiveRight ? primitiveLeft : double.class;
        }
    }

    public static class CastExpression implements Expression {
        private final Class<?> type;
        final Expression expression;

        public CastExpression(Class<?> type, Expression expression) {
            this.type = type;
            this.expression = expression;
        }

        public boolean canBeNull() {
            return true;
        }

        public Class<?> getType() {
            return type;
        }
    }

    public static class ArrayCreationExpression implements Expression {
        private final Class<?> arrayType;
        final List<Expression> items = new ArrayList<Expression>();

        public ArrayCreationExpression(Class<?> arrayType) {
            this.arrayType = arrayType;
        }

        public boolean canBeNull() {
            return false;
        }

        public Class<?> getType() {
            return arrayType;
        }

        public Class<?> getComponentType() {
            return arrayType.getComponentType();
        }

        public void addItem(Expression argument) {
            items.add(argument);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(100);
            sb.append("[ ");
            for (Iterator<Expression> i = items.iterator(); i.hasNext();) {
                sb.append(i.next());
                if (i.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(" ]");
            return sb.toString();
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
            this(method, null);
        }

        public MethodInvocation(Method method, String conditionClassName) {
            this.method = getMethodFromSuperclass(method, conditionClassName);
        }

        private Method getMethodFromSuperclass(Method method, String conditionClassName) {
            if (method == null || Modifier.isStatic(method.getModifiers())) {
                return method;
            }
            Class<?> declaringClass = method.getDeclaringClass();
            Class<?> conditionClass = conditionClassName != null ? getConditionClass(declaringClass, conditionClassName) : null;
            if (conditionClass != null) {
                try {
                    return conditionClass.getMethod(method.getName(), method.getParameterTypes());
                } catch (Exception e) { }
            }

            return getMethodForUnknownConditionClass(method, declaringClass);
        }

        private Method getMethodForUnknownConditionClass(Method method, Class<?> declaringClass) {
            Class<?> declaringSuperclass = declaringClass.getSuperclass();
            if (declaringSuperclass != null) {
                try {
                    return getMethodForUnknownConditionClass(declaringSuperclass.getMethod(method.getName(), method.getParameterTypes()), declaringSuperclass);
                } catch (Exception e) { }
            }
            Method iMethod = getMethodFromInterface(declaringClass, method);
            return iMethod == null ? method : iMethod;
        }

        private Method getMethodFromInterface(Class<?> clazz, Method method) {
            for (Class<?> interfaze : clazz.getInterfaces()) {
                Method iMethod = getMethodFromInterface(interfaze, method);
                if (iMethod != null) {
                    return iMethod;
                }
            }
            try {
                return clazz.getMethod(method.getName(), method.getParameterTypes());
            } catch (Exception e) { }
            return null;
        }

        private Class<?> getConditionClass(Class<?> declaringClass, String conditionClassName) {
            if (declaringClass.getName().equals(conditionClassName)) {
                return declaringClass;
            }
            for (Class<?> interfaze : declaringClass.getInterfaces()) {
                if (interfaze.getName().equals(conditionClassName)) {
                    return interfaze;
                }
            }
            Class<?> declaringSuperclass = declaringClass.getSuperclass();
            return declaringSuperclass != null ? getConditionClass(declaringSuperclass, conditionClassName) : null;
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

    public static class ThisInvocation extends MethodInvocation {
        private final Class<?> thisClass;

        public ThisInvocation(Class<?> thisClass) {
            super(null);
            this.thisClass = thisClass;
        }

        @Override
        public Class<?> getReturnType() {
            return thisClass;
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
        private final Class<?> arrayType;
        private final Expression index;

        public ArrayAccessInvocation(Class arrayType, Expression index) {
            this.arrayType = arrayType;
            this.index = index;
        }

        public Expression getIndex() {
            return index;
        }

        public String toString() {
            return "[" + index + "]";
        }

        public Class<?> getArrayType() {
            return arrayType;
        }

        public Class<?> getReturnType() {
            return arrayType.getComponentType();
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
            if (keyType == Object.class && key instanceof FixedExpression) {
                Object value = ((FixedExpression)key).getValue();
                if (value != null) {
                    return value.getClass();
                }
            }
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

    public enum BooleanOperator {
        EQ("=="), NE("!="), GT(">"), GE(">="), LT("<"), LE("<="), MATCHES("~="), CONTAINS("in"), SOUNDSLIKE("like"), INSTANCEOF("isa");

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
            return this != CONTAINS && this != INSTANCEOF;
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
