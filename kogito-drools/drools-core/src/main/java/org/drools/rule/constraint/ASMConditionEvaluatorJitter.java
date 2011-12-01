package org.drools.rule.constraint;

import org.drools.rule.builder.dialect.asm.ClassGenerator;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.compiler.ExecutableStatement;

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

    public static ConditionEvaluator jit(ExecutableStatement executableStatement, ClassLoader classLoader) {
        return generateConditionEvaluator(new AnalyzedCondition(executableStatement), classLoader);
    }

    private static ConditionEvaluator generateConditionEvaluator(AnalyzedCondition analyzedCondition, ClassLoader classLoader) {
        ClassGenerator generator = new ClassGenerator(getUniqueClassName(), classLoader)
                .setInterfaces(ConditionEvaluator.class)
                .addDefaultConstructor();

        generator.addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(boolean.class, Object.class), new EvaluateMethodGenerator(analyzedCondition));

        return generator.newInstance();
    }

    private static String getUniqueClassName() {
        return "ConditionEvaluator" + generateUUID();
    }

    private static class EvaluateMethodGenerator extends ClassGenerator.MethodBody {
        private AnalyzedCondition analyzedCondition;

        public EvaluateMethodGenerator(AnalyzedCondition analyzedCondition) {
            this.analyzedCondition = analyzedCondition;
        }

        public void body(MethodVisitor mv) {
            if (analyzedCondition.isBinary()) {
                jitBinary(mv);
            } else {
                jitUnary(mv);
            }

            if (analyzedCondition.isNegated()) {
                jitNegation(mv);
            }

            mv.visitInsn(IRETURN);
        }

        private void jitUnary(MethodVisitor mv) {
            jitExpression(mv, analyzedCondition.getLeft());
        }

        private void jitBinary(MethodVisitor mv) {
            AnalyzedCondition.Expression left = analyzedCondition.getLeft();
            AnalyzedCondition.Expression right = analyzedCondition.getRight();
            Class<?> commonType = analyzedCondition.getOperation().needsSameType() ?
                    findCommonClass(left.getType(), !left.canBeNull(), right.getType(), !right.canBeNull()) :
                    null;

            if (commonType != null && commonType.isPrimitive()) {
                jitPrimitiveBinary(mv, left, right, commonType);
            } else {
                jitObjectBinary(mv, left, right, commonType);
            }
        }

        private void jitPrimitiveBinary(MethodVisitor mv, AnalyzedCondition.Expression left, AnalyzedCondition.Expression right, Class<?> type) {
            if (right.isFixed() && right.canBeNull()) {
                // a primitive cannot be null
                mv.visitInsn(analyzedCondition.getOperation() == AnalyzedCondition.BooleanOperator.NE ? ICONST_1 : ICONST_0);
                return;
            }
            jitTopExpression(mv, left, type);
            jitTopExpression(mv, right, type);
            jitPrimitiveOperation(mv, analyzedCondition.getOperation(), type);
        }

        private void jitObjectBinary(MethodVisitor mv, AnalyzedCondition.Expression left, AnalyzedCondition.Expression right, Class<?> type) {
            if (left.isFixed()) {
                throw new RuntimeException("Unmanaged fixed left"); // TODO
            }

            Class<?> leftType = left.getType();
            Class<?> rightType = right.getType();

            jitTopExpression(mv, left, type != null ? type : leftType);
            store(2, leftType);

            jitTopExpression(mv, right, type != null ? type : rightType);
            store(4, rightType);

            Label notNullLabel = jitLeftIsNull(mv, type == null || leftType == type ?
                    jitNullSafeOperationStart(mv) :
                    jitNullSafeCoercion(mv, leftType, type));

            AnalyzedCondition.BooleanOperator operation = analyzedCondition.getOperation();

            if (operation.isEquality()) {
                // if (left == null) return right == null
                checkNullEquality(mv, operation);
            } else {
                // if (left == null) return false
                mv.visitInsn(ICONST_0);
            }

            returnOnNull(mv, notNullLabel);
            loadOperands(mv, right, type, rightType);

            if (operation == AnalyzedCondition.BooleanOperator.CONTAINS) {
                invokeStatic(EvaluatorHelper.class, "contains", boolean.class, Object.class, Object.class);
            } else if (operation == AnalyzedCondition.BooleanOperator.MATCHES) {
                invokeVirtual(type, "matches", boolean.class, String.class);
            } else if (operation.isEquality()) {
                if (type.isInterface()) {
                    invokeInterface(type, "equals", boolean.class, Object.class);
                } else {
                    invokeVirtual(type, "equals", boolean.class, Object.class);
                }
                if (operation == AnalyzedCondition.BooleanOperator.NE) {
                     analyzedCondition.toggleNegation();
                }
            } else {
                if (type.isInterface()) {
                    invokeInterface(type, "compareTo", int.class, type);
                } else {
                    invokeVirtual(type, "compareTo", int.class, type);
                }
                mv.visitInsn(ICONST_0);
                jitPrimitiveOperation(mv, operation, int.class);
            }
        }

        private void returnOnNull(MethodVisitor mv, Label notNullLabel) {
            mv.visitInsn(IRETURN);
            mv.visitLabel(notNullLabel);
        }

        private void loadOperands(MethodVisitor mv, AnalyzedCondition.Expression right, Class<?> type, Class<?> rightType) {
            load(2);
            load(4);
            if (type != null && !right.isFixed() && rightType != type) {
                jitRightCoercion(mv, rightType, type);
            }
        }

        private void checkNullEquality(MethodVisitor mv, AnalyzedCondition.BooleanOperator operation) {
            Label rightNullLabel = new Label();
            Label rightNotNullLabel = new Label();
            load(4);
            mv.visitJumpInsn(IFNULL, rightNullLabel);
            mv.visitInsn(operation == AnalyzedCondition.BooleanOperator.EQ ? ICONST_0 : ICONST_1);
            mv.visitJumpInsn(GOTO, rightNotNullLabel);
            mv.visitLabel(rightNullLabel);
            mv.visitInsn(operation == AnalyzedCondition.BooleanOperator.EQ ? ICONST_1 : ICONST_0);
            mv.visitLabel(rightNotNullLabel);
        }

        private Label jitNullSafeCoercion(MethodVisitor mv, Class<?> fromType, Class<?> toType) {
            Label nullLabel = new Label();
            load(2);
            mv.visitJumpInsn(IFNULL, nullLabel);
            mv.visitTypeInsn(NEW, internalName(toType));
            mv.visitInsn(DUP);
            load(2);
            invokeVirtual(fromType, "toString", String.class);
            invokeSpecial(toType, "<init>", null, String.class);
            store(2, toType);
            return nullLabel;
        }

        private Label jitNullSafeOperationStart(MethodVisitor mv) {
            Label nullLabel = new Label();
            load(2);
            mv.visitJumpInsn(IFNULL, nullLabel);
            return nullLabel;
        }

        private void jitRightCoercion(MethodVisitor mv, Class<?> fromType, Class<?> toType) {
            store(4, fromType);
            mv.visitTypeInsn(NEW, internalName(toType));
            mv.visitInsn(DUP);
            load(4);
            invokeVirtual(fromType, "toString", String.class);
            invokeSpecial(toType, "<init>", null, String.class);
        }

        private Label jitLeftIsNull(MethodVisitor mv, Label nullLabel) {
            Label notNullLabel = new Label();
            mv.visitJumpInsn(GOTO, notNullLabel);
            mv.visitLabel(nullLabel);
            return notNullLabel;
        }

        private void jitTopExpression(MethodVisitor mv, AnalyzedCondition.Expression exp, Class<?> requiredClass) {
            if (exp.isFixed()) {
                push(((AnalyzedCondition.FixedExpression) exp).typedValue.value, requiredClass);
            } else {
                jitEvaluatedExpression(mv, (AnalyzedCondition.EvaluatedExpression) exp);
            }
        }

        private void jitExpression(MethodVisitor mv, AnalyzedCondition.Expression exp) {
            if (exp.isFixed()) {
                push(((AnalyzedCondition.FixedExpression)exp).typedValue.value, exp.getType());
            } else {
                jitEvaluatedExpression(mv, (AnalyzedCondition.EvaluatedExpression) exp);
            }
        }

        private void jitEvaluatedExpression(MethodVisitor mv, AnalyzedCondition.EvaluatedExpression exp) {
            Iterator<AnalyzedCondition.Invocation> invocations = exp.invocations.iterator();
            for (Class<?> currentClass = jitInvocation(mv, invocations.next(), Object.class, true);
                 invocations.hasNext();
                 currentClass = jitInvocation(mv, invocations.next(), currentClass, false));
        }

        private Class<?> jitInvocation(MethodVisitor mv, AnalyzedCondition.Invocation invocation, Class<?> currentClass, boolean firstInvocation) {
            if (invocation instanceof AnalyzedCondition.MethodInvocation) {
                jitMethodInvocation(mv, (AnalyzedCondition.MethodInvocation)invocation, currentClass, firstInvocation);
            } else if (invocation instanceof AnalyzedCondition.ConstructorInvocation) {
                jitConstructorInvocation(mv, (AnalyzedCondition.ConstructorInvocation) invocation);
            } else if (invocation instanceof AnalyzedCondition.ListAccessInvocation) {
                jitListAccessInvocation(mv, (AnalyzedCondition.ListAccessInvocation) invocation);
            } else if (invocation instanceof AnalyzedCondition.MapAccessInvocation) {
                jitMapAccessInvocation(mv, (AnalyzedCondition.MapAccessInvocation) invocation);
            } else {
                jitFieldAccessInvocation(mv, (AnalyzedCondition.FieldAccessInvocation)invocation, currentClass, firstInvocation);
            }
            return invocation.getReturnType();
        }

        private void jitMethodInvocation(MethodVisitor mv, AnalyzedCondition.MethodInvocation invocation, Class<?> currentClass, boolean firstInvocation) {
            Method method = invocation.getMethod();
            if (firstInvocation && (method == null || (method.getModifiers() & Modifier.STATIC) == 0)) {
                mv.visitVarInsn(ALOAD, 1);
            }

            if (method == null) {
                if (firstInvocation) return; // this...
                else throw new RuntimeException("access to this not in first position");
            }

            if (!method.getDeclaringClass().isAssignableFrom(currentClass)) {
                cast(method.getDeclaringClass());
            }

            for (AnalyzedCondition.Expression argument : invocation.getArguments()) {
                jitExpression(mv, argument);
            }

            invoke(method);
        }

        private void jitConstructorInvocation(MethodVisitor mv, AnalyzedCondition.ConstructorInvocation invocation) {
            Constructor constructor = invocation.getConstructor();
            Class<?> clazz = invocation.getReturnType();

            mv.visitTypeInsn(NEW, internalName(clazz));
            mv.visitInsn(DUP);
            for (AnalyzedCondition.Expression argument : invocation.getArguments()) {
                jitExpression(mv, argument);
            }
            invokeSpecial(clazz, "<init>", null, constructor.getParameterTypes());
        }

        private void jitListAccessInvocation(MethodVisitor mv, AnalyzedCondition.ListAccessInvocation invocation) {
            jitTopExpression(mv, invocation.getIndex(), int.class);
            invokeInterface(List.class, "get", Object.class, int.class);
            if (invocation.getReturnType() != Object.class) {
                cast(invocation.getReturnType());
            }
        }

        private void jitMapAccessInvocation(MethodVisitor mv, AnalyzedCondition.MapAccessInvocation invocation) {
            jitTopExpression(mv, invocation.getKey(), invocation.getKeyType());
            invokeInterface(Map.class, "get", Object.class, Object.class);
            if (invocation.getReturnType() != Object.class) {
                cast(invocation.getReturnType());
            }
        }

        private void jitFieldAccessInvocation(MethodVisitor mv, AnalyzedCondition.FieldAccessInvocation invocation, Class<?> currentClass, boolean firstInvocation) {
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
        
        private void jitPrimitiveOperation(MethodVisitor mv, AnalyzedCondition.BooleanOperator op, Class<?> type) {
            jitPrimitiveCompare(mv, toOpCode(op, type), type);
        }

        private void jitPrimitiveCompare(MethodVisitor mv, int opCode, Class<?> type) {
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

        private void jitNegation(MethodVisitor mv) {
            Label trueBranch = new Label();
            Label falseBranch = new Label();
            mv.visitJumpInsn(IFNE, trueBranch);
            mv.visitInsn(ICONST_1);
            mv.visitJumpInsn(GOTO, falseBranch);
            mv.visitLabel(trueBranch);
            mv.visitInsn(ICONST_0);
            mv.visitLabel(falseBranch);
        }

        private static int toOpCode(AnalyzedCondition.BooleanOperator op, Class<?> type) {
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
            throw new RuntimeException("Unknown operation: " + op);
        }
    }

    private static Class<?> findCommonClass(Class<?> class1, boolean primitive1, Class<?> class2, boolean primitive2) {
        if (class1 == class2) return class1;
        if (class1 == Object.class) return class2;
        if (class2 == Object.class) return class1;
        if (class1 == String.class) return class2;
        if (class2 == String.class) return class1;

        Class<?> result = findCommonClass(class1, class2, primitive2);
        if (result == null) {
            result = findCommonClass(class2, class1, primitive1);
        }
        if (result == null) {
            throw new RuntimeException("Cannot find a common class between " + class1.getName() + " and " + class2.getName());
        }
        return result;
    }

    private static Class<?> findCommonClass(Class<?> class1, Class<?> class2, boolean canBePrimitive) {
        if (class1.isAssignableFrom(class2)) return class1;

        if (class1 == boolean.class) {
            if (class2 == Boolean.class) return canBePrimitive ? boolean.class : Boolean.class;
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
