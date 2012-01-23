package org.drools.rule.constraint;

import org.drools.rule.builder.dialect.asm.ClassGenerator;
import org.drools.rule.constraint.ConditionAnalyzer.*;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.drools.core.util.StringUtils.generateUUID;
import static org.mvel2.asm.Opcodes.*;

public class ASMConditionEvaluatorJitter {

    public static ConditionEvaluator jit(Condition condition, ClassLoader classLoader) {
        ClassGenerator generator = new ClassGenerator(getUniqueClassName(), classLoader)
                .setInterfaces(ConditionEvaluator.class)
                .addDefaultConstructor();

        generator.addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(boolean.class, Object.class, Map.class), new EvaluateMethodGenerator(condition));

        return generator.newInstance();
    }

    private static String getUniqueClassName() {
        return "ConditionEvaluator" + generateUUID();
    }

    private static class EvaluateMethodGenerator extends ClassGenerator.MethodBody {
        private static final int LEFT_OPERAND = 3;
        private static final int RIGHT_OPERAND = 5;
        
        private final Condition condition;

        public EvaluateMethodGenerator(Condition condition) {
            this.condition = condition;
        }

        public void body(MethodVisitor mv) {
            jitCondition(condition);
            mv.visitInsn(IRETURN);
        }

        private void jitCondition(Condition condition) {
            if (condition instanceof SingleCondition) {
                jitSingleCondition((SingleCondition)condition);
            } else {
                jitCombinedCondition((CombinedCondition)condition);
            }

            if (condition.isNegated()) {
                jitNegation();
            }
        }

        private void jitSingleCondition(SingleCondition singleCondition) {
            if (singleCondition.isBinary()) {
                jitBinary(singleCondition);
            } else {
                jitUnary(singleCondition);
            }
        }

        private void jitCombinedCondition(CombinedCondition combinedCondition) {
            boolean isAnd = combinedCondition.isAnd();
            Label shortcut = new Label();
            Label noShortcut = new Label();

            for (Condition condition : combinedCondition.getConditions()) {
                jitCondition(condition);
                mv.visitJumpInsn(isAnd ? IFEQ : IFNE, shortcut);
            }

            mv.visitInsn(isAnd ? ICONST_1 : ICONST_0);
            mv.visitJumpInsn(GOTO, noShortcut);
            mv.visitLabel(shortcut);
            mv.visitInsn(isAnd ? ICONST_0 : ICONST_1);
            mv.visitLabel(noShortcut);
        }

        private void jitUnary(SingleCondition singleCondition) {
            jitExpression(singleCondition.getLeft());
        }

        private void jitBinary(SingleCondition singleCondition) {
            Expression left = singleCondition.getLeft();
            Expression right = singleCondition.getRight();
            Class<?> commonType = singleCondition.getOperation().needsSameType() ?
                    findCommonClass(left.getType(), !left.canBeNull(), right.getType(), !right.canBeNull()) :
                    null;

            if (commonType != null && commonType.isPrimitive()) {
                jitPrimitiveBinary(singleCondition, left, right, commonType);
            } else {
                jitObjectBinary(singleCondition, left, right, commonType);
            }
        }

        private void jitPrimitiveBinary(SingleCondition singleCondition, Expression left, Expression right, Class<?> type) {
            if (right.isFixed() && right.canBeNull()) {
                // a primitive cannot be null
                mv.visitInsn(singleCondition.getOperation() == BooleanOperator.NE ? ICONST_1 : ICONST_0);
                return;
            }
            jitExpression(left, type);
            castPrimitiveToPrimitive(left.getType(), type);
            jitExpression(right, type);
            castPrimitiveToPrimitive(right.getType(), type);
            jitPrimitiveOperation(singleCondition.getOperation(), type);
        }

        private void jitObjectBinary(SingleCondition singleCondition, Expression left, Expression right, Class<?> type) {
            if (left.isFixed()) {
                throw new RuntimeException("Unmanaged fixed left"); // TODO
            }

            Class<?> leftType = left.getType();
            Class<?> rightType = right.getType();

            jitExpression(left, type != null ? type : leftType);
            store(LEFT_OPERAND, leftType);

            jitExpression(right, type != null ? type : rightType);
            store(RIGHT_OPERAND, rightType);

            BooleanOperator operation = singleCondition.getOperation();
            prepareLeftOperand(operation, type, leftType, rightType);
            prepareRightOperand(operation, right, type, rightType);

            load(LEFT_OPERAND);
            load(RIGHT_OPERAND);

            if (operation == BooleanOperator.CONTAINS) {
                invokeStatic(EvaluatorHelper.class, "contains", boolean.class, Object.class, rightType.isPrimitive() ? rightType : Object.class);
            } else if (operation == BooleanOperator.MATCHES) {
                invokeVirtual(type, "matches", boolean.class, String.class);
            } else if (operation.isEquality()) {
                if (type.isInterface()) {
                    invokeInterface(type, "equals", boolean.class, Object.class);
                } else {
                    invokeVirtual(type, "equals", boolean.class, Object.class);
                }
                if (operation == BooleanOperator.NE) {
                    singleCondition.toggleNegation();
                }
            } else {
                if (type.isInterface()) {
                    invokeInterface(type, "compareTo", int.class, type);
                } else {
                    invokeVirtual(type, "compareTo", int.class, type);
                }
                mv.visitInsn(ICONST_0);
                jitPrimitiveOperation(operation, int.class);
            }
        }

        private void prepareLeftOperand(BooleanOperator operation, Class<?> type, Class<?> leftType, Class<?> rightType) {
            if (leftType.isPrimitive()) {
                if (type != null) castOrCoercePrimitive(LEFT_OPERAND, leftType, type);
                return;
            }

            Label notNullLabel = jitLeftIsNull(type == null || leftType == type ?
                    jitNullSafeOperationStart() :
                    jitNullSafeCoercion(leftType, type));

            if (operation.isEquality() && !rightType.isPrimitive()) {
                // if (left == null) return right == null
                checkNullEquality(operation);
            } else {
                // if (left == null) return false
                mv.visitInsn(ICONST_0);
            }

            mv.visitInsn(IRETURN);
            mv.visitLabel(notNullLabel);
        }

        private void prepareRightOperand(BooleanOperator operation, Expression right, Class<?> type, Class<?> rightType) {
            if (rightType.isPrimitive()) {
                if (type != null) castOrCoercePrimitive(RIGHT_OPERAND, rightType, type);
                return;
            }

            Label nullLabel = new Label();
            Label notNullLabel = new Label();
            load(RIGHT_OPERAND);
            mv.visitJumpInsn(IFNULL, nullLabel);
            if (type != null && !right.isFixed() && rightType != type) {
                castOrCoerceTo(RIGHT_OPERAND, rightType, type);
            }
            mv.visitJumpInsn(GOTO, notNullLabel);
            mv.visitLabel(nullLabel);
            mv.visitInsn(operation == BooleanOperator.NE ? ICONST_1 : ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitLabel(notNullLabel);
        }

        private void checkNullEquality(BooleanOperator operation) {
            Label rightNullLabel = new Label();
            Label rightNotNullLabel = new Label();
            load(RIGHT_OPERAND);
            mv.visitJumpInsn(IFNULL, rightNullLabel);
            mv.visitInsn(operation == BooleanOperator.EQ ? ICONST_0 : ICONST_1);
            mv.visitJumpInsn(GOTO, rightNotNullLabel);
            mv.visitLabel(rightNullLabel);
            mv.visitInsn(operation == BooleanOperator.EQ ? ICONST_1 : ICONST_0);
            mv.visitLabel(rightNotNullLabel);
        }

        private Label jitNullSafeOperationStart() {
            Label nullLabel = new Label();
            load(LEFT_OPERAND);
            mv.visitJumpInsn(IFNULL, nullLabel);
            return nullLabel;
        }

        private Label jitNullSafeCoercion(Class<?> fromType, Class<?> toType) {
            Label nullLabel = new Label();
            load(LEFT_OPERAND);
            mv.visitJumpInsn(IFNULL, nullLabel);
            castOrCoerceTo(LEFT_OPERAND, fromType, toType);
            return nullLabel;
        }

        private Label jitLeftIsNull(Label nullLabel) {
            Label notNullLabel = new Label();
            mv.visitJumpInsn(GOTO, notNullLabel);
            mv.visitLabel(nullLabel);
            return notNullLabel;
        }

        private void castOrCoerceTo(int regNr, Class<?> fromType, Class<?> toType) {
            Label nonInstanceOfLabel = new Label();
            Label endOfCoercionLabel = new Label();
            load(regNr);
            instanceOf(toType);
            mv.visitJumpInsn(IFEQ, nonInstanceOfLabel);
            load(regNr);
            cast(toType);
            store(regNr, toType);
            mv.visitJumpInsn(GOTO, endOfCoercionLabel);

            mv.visitLabel(nonInstanceOfLabel);
            mv.visitTypeInsn(NEW, internalName(toType));
            mv.visitInsn(DUP);
            load(regNr);
            coerceByConstructor(fromType, toType);
            store(regNr, toType);
            mv.visitLabel(endOfCoercionLabel);
        }

        private void coerceByConstructor(Class<?> fromType, Class<?> toType) {
            invokeVirtual(fromType, "toString", String.class);
            if (toType == Character.class) {
                mv.visitInsn(ICONST_0);
                invokeVirtual(String.class, "charAt", char.class, int.class);
                invokeSpecial(Character.class, "<init>", null, char.class);
            } else {
                invokeSpecial(toType, "<init>", null, String.class);
            }
        }

        private void castOrCoercePrimitive(int regNr, Class<?> fromType, Class<?> toType) {
            if (fromType == toType) return;
            load(regNr);
            if (toType.isPrimitive()) {
                castPrimitiveToPrimitive(fromType, toType);
            } else {
                Class<?> toTypeAsPrimitive = convertToPrimitiveType(toType);
                castPrimitiveToPrimitive(fromType, toTypeAsPrimitive);
                invokeStatic(toType, "valueOf", toType, toTypeAsPrimitive);
            }
            store(regNr, toType);
        }

        private void jitExpression(Expression exp) {
            jitExpression(exp, exp.getType());
        }

        private void jitExpression(Expression exp, Class<?> requiredClass) {
            if (exp.isFixed()) {
                push(((FixedExpression) exp).typedValue.value, requiredClass);
            } else {
                if (exp instanceof EvaluatedExpression) {
                    jitEvaluatedExpression((EvaluatedExpression) exp, true);
                } else if (exp instanceof VariableExpression) {
                    jitVariableExpression((VariableExpression) exp, requiredClass);
                } else {
                    jitAritmeticExpression((AritmeticExpression)exp);
                }
            }
        }

        private void jitEvaluatedExpression(EvaluatedExpression exp, boolean firstInvocation) {
            Iterator<Invocation> invocations = exp.invocations.iterator();
            Class<?> currentClass = jitInvocation(invocations.next(), Object.class, firstInvocation);
            while (invocations.hasNext()) {
                currentClass = jitInvocation(invocations.next(), currentClass, false);
            }
        }

        private void jitVariableExpression(VariableExpression exp, Class<?> requiredClass) {
            mv.visitVarInsn(ALOAD, 2);
            push(exp.variableName, String.class);
            invokeInterface(Map.class, "get", Object.class, Object.class);
            if (exp.subsequentInvocations != null) {
                jitEvaluatedExpression(exp.subsequentInvocations, false);
            } else if (requiredClass.isPrimitive()) {
                castToPrimitive(requiredClass);
            }
        }

        private void jitAritmeticExpression(AritmeticExpression aritmeticExpression) {
            if (aritmeticExpression.isStringConcat()) {
                jitStringConcat(aritmeticExpression.left, aritmeticExpression.right);
            } else {
                Class<?> operationType = double.class;
                if (aritmeticExpression.type == Integer.class) operationType = int.class;
                else if (aritmeticExpression.type == Long.class) operationType = long.class;

                jitExpressionToPrimitiveType(aritmeticExpression.left, operationType);
                jitExpressionToPrimitiveType(aritmeticExpression.right, aritmeticExpression.operator.isBitwiseOperation() ? int.class : operationType);
                jitAritmeticOperation(operationType, aritmeticExpression.operator);
            }
        }

        private void jitStringConcat(Expression left, Expression right) {
            invokeConstructor(StringBuilder.class);
            jitExpression(left, String.class);
            invokeVirtual(StringBuilder.class, "append", StringBuilder.class, left.getType());
            jitExpression(right, String.class);
            invokeVirtual(StringBuilder.class, "append", StringBuilder.class, right.getType());
            invokeVirtual(StringBuilder.class, "toString", String.class);
        }

        private void jitExpressionToPrimitiveType(Expression expression, Class<?> primitiveType) {
            jitExpression(expression, Object.class);
            if (expression.isFixed() || expression.getType().isPrimitive()) {
                castPrimitiveToPrimitive(convertToPrimitiveType(expression.getType()), primitiveType);
            } else {
                castToPrimitive(primitiveType);
            }
        }

        private void jitAritmeticOperation(Class<?> operationType, AritmeticOperator operator) {
            if (operationType == int.class) {
                switch(operator) {
                    case ADD:
                        mv.visitInsn(IADD);
                        break;
                    case SUB:
                        mv.visitInsn(ISUB);
                        break;
                    case MUL:
                        mv.visitInsn(IMUL);
                        break;
                    case DIV:
                        mv.visitInsn(IDIV);
                        break;
                    case BW_SHIFT_LEFT:
                        mv.visitInsn(ISHL);
                        break;
                    case BW_SHIFT_RIGHT:
                        mv.visitInsn(ISHR);
                        break;
                }
            } else if (operationType == long.class) {
                switch(operator) {
                    case ADD:
                        mv.visitInsn(LADD);
                        break;
                    case SUB:
                        mv.visitInsn(LSUB);
                        break;
                    case MUL:
                        mv.visitInsn(LMUL);
                        break;
                    case DIV:
                        mv.visitInsn(LDIV);
                        break;
                    case BW_SHIFT_LEFT:
                        mv.visitInsn(LSHL);
                        break;
                    case BW_SHIFT_RIGHT:
                        mv.visitInsn(LSHR);
                        break;
                }
            } else {
                switch(operator) {
                    case ADD:
                        mv.visitInsn(DADD);
                        break;
                    case SUB:
                        mv.visitInsn(DSUB);
                        break;
                    case MUL:
                        mv.visitInsn(DMUL);
                        break;
                    case DIV:
                        mv.visitInsn(DDIV);
                        break;
                }
            }
        }

        private Class<?> jitInvocation(Invocation invocation, Class<?> currentClass, boolean firstInvocation) {
            if (invocation instanceof MethodInvocation) {
                jitMethodInvocation((MethodInvocation)invocation, currentClass, firstInvocation);
            } else if (invocation instanceof ConstructorInvocation) {
                jitConstructorInvocation((ConstructorInvocation) invocation);
            } else if (invocation instanceof ListAccessInvocation) {
                jitListAccessInvocation((ListAccessInvocation) invocation);
            } else if (invocation instanceof MapAccessInvocation) {
                jitMapAccessInvocation((MapAccessInvocation) invocation);
            } else {
                jitFieldAccessInvocation((FieldAccessInvocation)invocation, currentClass, firstInvocation);
            }
            return invocation.getReturnType();
        }

        private void jitMethodInvocation(MethodInvocation invocation, Class<?> currentClass, boolean firstInvocation) {
            Method method = invocation.getMethod();
            if (firstInvocation && (method == null || (method.getModifiers() & Modifier.STATIC) == 0)) {
                mv.visitVarInsn(ALOAD, 1);
            }

            if (method == null) {
                if (firstInvocation) return; // this...
                else throw new RuntimeException("access to this not in first position");
            }

            if (!method.getDeclaringClass().isAssignableFrom(currentClass) && (method.getModifiers() & Modifier.STATIC) == 0) {
                cast(method.getDeclaringClass());
            }

            for (Expression argument : invocation.getArguments()) {
                jitExpression(argument);
            }

            invoke(method);
        }

        private void jitConstructorInvocation(ConstructorInvocation invocation) {
            Constructor constructor = invocation.getConstructor();
            Class<?> clazz = invocation.getReturnType();

            mv.visitTypeInsn(NEW, internalName(clazz));
            mv.visitInsn(DUP);
            for (Expression argument : invocation.getArguments()) {
                jitExpression(argument);
            }
            invokeSpecial(clazz, "<init>", null, constructor.getParameterTypes());
        }

        private void jitListAccessInvocation(ListAccessInvocation invocation) {
            jitExpression(invocation.getIndex(), int.class);
            invokeInterface(List.class, "get", Object.class, int.class);
            if (invocation.getReturnType() != Object.class) {
                cast(invocation.getReturnType());
            }
        }

        private void jitMapAccessInvocation(MapAccessInvocation invocation) {
            jitExpression(invocation.getKey(), invocation.getKeyType());
            invokeInterface(Map.class, "get", Object.class, Object.class);
            if (invocation.getReturnType() != Object.class) {
                cast(invocation.getReturnType());
            }
        }

        private void jitFieldAccessInvocation(FieldAccessInvocation invocation, Class<?> currentClass, boolean firstInvocation) {
            Field field = invocation.getField();
            boolean isStatic = (field.getModifiers() & Modifier.STATIC) != 0;

            if (firstInvocation && !isStatic) {
                mv.visitVarInsn(ALOAD, 1);
            }
            if (!isStatic && !field.getDeclaringClass().isAssignableFrom(currentClass)) {
                cast(field.getDeclaringClass());
            }
            readField(field);
        }
        
        private void jitPrimitiveOperation(BooleanOperator op, Class<?> type) {
            int opCode = toOpCode(op, type);

            Label trueBranchLabel = new Label();
            Label returnLabel = new Label();
            if (type == double.class) {
                mv.visitInsn(DCMPL);
            } else if (type == long.class) {
                mv.visitInsn(LCMP);
            } else if (type == float.class) {
                mv.visitInsn(FCMPL);
            }

            mv.visitJumpInsn(opCode, trueBranchLabel);
            mv.visitInsn(ICONST_0);
            mv.visitJumpInsn(GOTO, returnLabel);
            mv.visitLabel(trueBranchLabel);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(returnLabel);
        }

        private void jitNegation() {
            Label trueBranch = new Label();
            Label falseBranch = new Label();
            mv.visitJumpInsn(IFNE, trueBranch);
            mv.visitInsn(ICONST_1);
            mv.visitJumpInsn(GOTO, falseBranch);
            mv.visitLabel(trueBranch);
            mv.visitInsn(ICONST_0);
            mv.visitLabel(falseBranch);
        }

        private int toOpCode(BooleanOperator op, Class<?> type) {
            if (type == double.class || type == long.class || type == float.class) {
                switch (op) {
                    case EQ: return IFEQ;
                    case NE: return IFNE;
                    case GT: return IFGT;
                    case GE: return IFGE;
                    case LT: return IFLT;
                    case LE: return IFLE;
                }
            } else {
                switch (op) {
                    case EQ: return IF_ICMPEQ;
                    case NE: return IF_ICMPNE;
                    case GT: return IF_ICMPGT;
                    case GE: return IF_ICMPGE;
                    case LT: return IF_ICMPLT;
                    case LE: return IF_ICMPLE;
                }
            }
            throw new RuntimeException("Unknown operator: " + op);
        }

        private Class<?> findCommonClass(Class<?> class1, boolean primitive1, Class<?> class2, boolean primitive2) {
            Class<?> result = null;
            if (class1 == class2) result = class1;
            else if (class1 == Object.class) result = convertFromPrimitiveType(class2);
            else if (class2 == Object.class) result = convertFromPrimitiveType(class1);
            else if (class1 == String.class) result = convertFromPrimitiveType(class2);
            else if (class2 == String.class) result = convertFromPrimitiveType(class1);

            if (result == null) {
                result = findCommonClass(class1, class2, primitive2);
            }
            if (result == null) {
                result = findCommonClass(class2, class1, primitive1);
            }
            if (result == null) {
                throw new RuntimeException("Cannot find a common class between " + class1.getName() + " and " + class2.getName());
            }
            return result == Number.class ? Double.class : result;
        }

        private Class<?> findCommonClass(Class<?> class1, Class<?> class2, boolean canBePrimitive) {
            if (class1.isAssignableFrom(class2)) return class1;

            if (class1 == boolean.class && class2 == Boolean.class) return canBePrimitive ? boolean.class : Boolean.class;
            if (class1 == char.class && class2 == Character.class) return canBePrimitive ? char.class : Character.class;
            if (class1 == byte.class && class2 == Byte.class) return canBePrimitive ? byte.class : Byte.class;
            if (class1 == short.class && class2 == Short.class) return canBePrimitive ? short.class : Short.class;

            if (class1 == Number.class && class2.isPrimitive()) {
                return Double.class;
            }

            if (class1 == int.class || class1 == short.class || class1 == byte.class) {
                if (class2 == Integer.class) return canBePrimitive ? int.class : Integer.class;
                if (class2 == long.class) return long.class;
                if (class2 == Long.class) return canBePrimitive ? long.class : Long.class;
                if (class2 == float.class) return float.class;
                if (class2 == Float.class) return canBePrimitive ? float.class : Float.class;
                if (class2 == double.class) return double.class;
                if (class2 == Double.class) return canBePrimitive ? double.class : Double.class;
                if (class2 == BigInteger.class) return BigInteger.class;
                if (class2 == BigDecimal.class) return BigDecimal.class;
            }

            if (class1 == long.class) {
                if (class2 == int.class) return long.class;
                if (class2 == Integer.class) return canBePrimitive ? long.class : Long.class;
                if (class2 == Long.class) return canBePrimitive ? long.class : Long.class;
                if (class2 == float.class) return double.class;
                if (class2 == Float.class) return canBePrimitive ? double.class : Double.class;
                if (class2 == double.class) return double.class;
                if (class2 == Double.class) return canBePrimitive ? double.class : Double.class;
                if (class2 == BigInteger.class) return BigInteger.class;
                if (class2 == BigDecimal.class) return BigDecimal.class;
            }

            if (class1 == float.class) {
                if (class2 == int.class) return float.class;
                if (class2 == Integer.class) return canBePrimitive ? float.class : Float.class;
                if (class2 == long.class) return double.class;
                if (class2 == Long.class) return canBePrimitive ? double.class : Double.class;
                if (class2 == Float.class) return canBePrimitive ? float.class : Float.class;
                if (class2 == double.class) return double.class;
                if (class2 == Double.class) return canBePrimitive ? double.class : Double.class;
                if (class2 == BigInteger.class) return BigDecimal.class;
                if (class2 == BigDecimal.class) return BigDecimal.class;
            }

            if (class1 == double.class) {
                if (class2 == int.class) return float.class;
                if (class2 == Integer.class) return canBePrimitive ? double.class : Double.class;
                if (class2 == long.class) return double.class;
                if (class2 == Long.class) return canBePrimitive ? double.class : Double.class;
                if (class2 == float.class) return double.class;
                if (class2 == Float.class) return canBePrimitive ? double.class : Double.class;
                if (class2 == Double.class) return canBePrimitive ? double.class : Double.class;
                if (class2 == BigInteger.class) return BigDecimal.class;
                if (class2 == BigDecimal.class) return BigDecimal.class;
            }

            if (class1 == Integer.class) {
                if (class2 == Long.class) return Long.class;
                if (class2 == Float.class) return Float.class;
                if (class2 == Double.class) return Double.class;
                if (class2 == BigInteger.class) return BigInteger.class;
                if (class2 == BigDecimal.class) return BigDecimal.class;
            }

            if (class1 == Long.class) {
                if (class2 == Float.class) return Double.class;
                if (class2 == Double.class) return Double.class;
                if (class2 == BigInteger.class) return BigInteger.class;
                if (class2 == BigDecimal.class) return BigDecimal.class;
            }

            if (class1 == Float.class) {
                if (class2 == Double.class) return Double.class;
                if (class2 == BigInteger.class) return BigDecimal.class;
                if (class2 == BigDecimal.class) return BigDecimal.class;
            }

            if (class1 == Double.class) {
                if (class2 == BigInteger.class) return BigDecimal.class;
                if (class2 == BigDecimal.class) return BigDecimal.class;
            }

            if (class1 == BigInteger.class) {
                if (class2 == BigDecimal.class) return BigDecimal.class;
            }

            return null;
        }

    }
}
