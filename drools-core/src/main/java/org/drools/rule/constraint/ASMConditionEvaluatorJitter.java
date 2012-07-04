package org.drools.rule.constraint;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.rule.builder.dialect.asm.ClassGenerator;
import org.drools.rule.builder.dialect.asm.GeneratorHelper;
import org.drools.rule.constraint.ConditionAnalyzer.*;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.util.NullType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.drools.core.util.ClassUtils.convertFromPrimitiveType;
import static org.drools.core.util.ClassUtils.convertToPrimitiveType;
import static org.drools.core.util.StringUtils.generateUUID;
import static org.drools.rule.builder.dialect.asm.GeneratorHelper.matchDeclarationsToTuple;
import static org.drools.rule.constraint.ConditionAnalyzer.isFixed;
import static org.mvel2.asm.Opcodes.*;
import static org.mvel2.asm.Opcodes.IASTORE;

public class ASMConditionEvaluatorJitter {

    public static ConditionEvaluator jitEvaluator(String expression, Condition condition, Declaration[] declarations, ClassLoader classLoader, LeftTuple leftTuple) {
        ClassGenerator generator = new ClassGenerator(getUniqueClassName(), classLoader)
                .setInterfaces(ConditionEvaluator.class)
                .addStaticField(ACC_PRIVATE | ACC_FINAL, "EXPRESSION", String.class, expression)
                .addField(ACC_PRIVATE | ACC_FINAL, "declarations", Declaration[].class)
                .addDefaultConstructor(new ClassGenerator.MethodBody() {
                    public void body(MethodVisitor mv) {
                        putFieldInThisFromRegistry("declarations", Declaration[].class, 1);
                        mv.visitInsn(RETURN);
                    }
                }, Declaration[].class);

        generator.addMethod(ACC_PUBLIC,
                            "evaluate",
                            generator.methodDescr(boolean.class, Object.class, InternalWorkingMemory.class, LeftTuple.class),
                            new EvaluateMethodGenerator(condition, declarations, leftTuple));

        return generator.newInstance(Declaration[].class, declarations);
    }

    private static String getUniqueClassName() {
        return getUniqueName("ConditionEvaluator");
    }

    private static String getUniqueName(String prefix) {
        return prefix + generateUUID();
    }

    private static class EvaluateMethodGenerator extends GeneratorHelper.DeclarationAccessorMethod {
        private static final int LEFT_OPERAND = 5;
        private static final int RIGHT_OPERAND = 7;
        private static final int ARGUMENTS = 9;

        private final Condition condition;
        private final Declaration[] declarations;
        private final LeftTuple leftTuple;

        private int[] declPositions;

        public EvaluateMethodGenerator(Condition condition, Declaration[] declarations, LeftTuple leftTuple) {
            this.condition = condition;
            this.declarations = declarations;
            this.leftTuple = leftTuple;
        }

        public void body(MethodVisitor mv) {
            jitArguments();
            jitCondition(condition);
            mv.visitInsn(IRETURN);
        }

        private void jitArguments() {
            if (declarations == null || declarations.length == 0) {
                return;
            }

            declPositions = new int[declarations.length];
            List<GeneratorHelper.DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(declarations);

            LeftTuple currentLeftTuple = leftTuple;
            mv.visitVarInsn(ALOAD, 3);
            store(4, LeftTuple.class);

            int decPos = ARGUMENTS;
            for (GeneratorHelper.DeclarationMatcher declarationMatcher : declarationMatchers) {
                int i = declarationMatcher.getOriginalIndex();
                if (currentLeftTuple == null || declarationMatcher.getRootDistance() > currentLeftTuple.getIndex()) {
                    getFieldFromThis("declarations", Declaration[].class);
                    push(i);
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitVarInsn(ALOAD, 2); // InternalWorkingMemory
                    mv.visitVarInsn(ALOAD, 1); // Object
                    declPositions[i] = decPos;
                    decPos += storeObjectFromDeclaration(declarationMatcher.getDeclaration(), decPos);
                    continue;
                }

                currentLeftTuple = traverseTuplesUntilDeclaration(currentLeftTuple, declarationMatcher.getRootDistance(), 4);

                getFieldFromThis("declarations", Declaration[].class);
                push(i);
                mv.visitInsn(AALOAD); // declarations[i]
                mv.visitVarInsn(ALOAD, 2); // InternalWorkingMemory
                load(4);
                invokeInterface(LeftTuple.class, "getHandle", InternalFactHandle.class);
                invokeInterface(InternalFactHandle.class, "getObject", Object.class); // leftTuple.getHandle().getObject()

                declPositions[i] = decPos;
                decPos += storeObjectFromDeclaration(declarationMatcher.getDeclaration(), decPos);
            }
        }

        private void jitCondition(Condition condition) {
            if (condition instanceof FixedValueCondition) {
                mv.visitInsn(((FixedValueCondition)condition).getFixedValue() ? ICONST_1 : ICONST_0);
                return;
            }

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
                if (singleCondition.getOperation() == BooleanOperator.MATCHES) {
                    jitMatches(singleCondition);
                } else {
                    jitBinary(singleCondition);
                }
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

            if (commonType == Object.class && singleCondition.getOperation().isComparison()) {
                commonType = Comparable.class;
            }

            if (commonType != null && commonType.isPrimitive()) {
                jitPrimitiveBinary(singleCondition, left, right, commonType);
            } else {
                jitObjectBinary(singleCondition, left, right, commonType);
            }
        }

        private void jitMatches(SingleCondition singleCondition) {
            if (!(singleCondition.getRight() instanceof FixedExpression)) {
                jitBinary(singleCondition);
                return;
            }

            final String matchingString = ((FixedExpression)singleCondition.getRight()).getValue().toString();
            final String patternVariableName = getUniqueName("pattern");
            getClassGenerator().addStaticField(ACC_PRIVATE | ACC_FINAL, patternVariableName, Pattern.class, null);
            getClassGenerator().addStaticInitBlock(new ClassGenerator.MethodBody() {
                @Override
                public void body(MethodVisitor mv) {
                    mv.visitLdcInsn(matchingString);
                    invokeStatic(Pattern.class, "compile", Pattern.class, String.class);
                    putStaticField(patternVariableName, Pattern.class);
                }
            });

            jitExpression(singleCondition.getLeft(), String.class);
            store(LEFT_OPERAND, singleCondition.getLeft().getType());

            Label notNullLabel = jitLeftIsNull(singleCondition.getLeft().getType() == String.class ?
                    jitNullSafeOperationStart() :
                    jitNullSafeCoercion(singleCondition.getLeft().getType(), String.class));

            mv.visitInsn(ICONST_0);

            Label nullEvaluation = new Label();
            mv.visitJumpInsn(GOTO, nullEvaluation);
            mv.visitLabel(notNullLabel);

            getStaticField(patternVariableName, Pattern.class);
            load(LEFT_OPERAND);
            invokeVirtual(Pattern.class, "matcher", Matcher.class, CharSequence.class);
            invokeVirtual(Matcher.class, "matches", boolean.class);

            mv.visitLabel(nullEvaluation);
        }

        private void jitPrimitiveBinary(SingleCondition singleCondition, Expression left, Expression right, Class<?> type) {
            if (isFixed(right) && right.canBeNull()) {
                // a primitive cannot be null
                mv.visitInsn(singleCondition.getOperation() == BooleanOperator.NE ? ICONST_1 : ICONST_0);
                return;
            }
            Label nullArg = new Label();
            ensureNotNullArgs(left, right, nullArg);

            jitExpression(left, type);
            castExpressionResultToPrimitive(left, type);
            jitExpression(right, type);
            castExpressionResultToPrimitive(right, type);
            jitPrimitiveOperation(singleCondition.getOperation(), type);
            Label nonNullArg = new Label();
            mv.visitJumpInsn(GOTO, nonNullArg);

            mv.visitLabel(nullArg);
            mv.visitInsn(ICONST_0);
            mv.visitLabel(nonNullArg);
        }

        private void ensureNotNullArgs(Expression left, Expression right, Label nullArg) {
            ensureNotNullArgs(left, nullArg);
            ensureNotNullArgs(right, nullArg);
        }

        private void ensureNotNullArgs(Expression exp, Label nullArg) {
            if (exp instanceof FixedExpression) {
                if (((FixedExpression) exp).canBeNull()) {
                    mv.visitJumpInsn(GOTO, nullArg);
                }
            } else if (exp instanceof EvaluatedExpression) {
                if (!exp.getType().isPrimitive()) {
                    jitEvaluatedExpression((EvaluatedExpression) exp, true, Object.class);
                    mv.visitJumpInsn(IFNULL, nullArg);
                }
            } else if (exp instanceof VariableExpression) {
                if (!exp.getType().isPrimitive()) {
                    jitVariableExpression((VariableExpression) exp);
                    mv.visitJumpInsn(IFNULL, nullArg);
                }
            } else if (exp instanceof AritmeticExpression) {
                ensureNotNullInAritmeticExpression((AritmeticExpression)exp, nullArg);
            }
        }

        private void ensureNotNullInAritmeticExpression(AritmeticExpression aritmeticExpression, Label nullArg) {
            if (!aritmeticExpression.isStringConcat()) {
                ensureNotNullArgs(aritmeticExpression.left, nullArg);
                ensureNotNullArgs(aritmeticExpression.right, nullArg);
            }
        }

        private void castExpressionResultToPrimitive(Expression expression, Class<?> type) {
            if (!isFixed(expression)) {
                cast(expression.getType(), type);
            }
        }

        private void jitObjectBinary(SingleCondition singleCondition, Expression left, Expression right, Class<?> type) {
            if (isFixed(left)) {
                throw new RuntimeException("Unmanaged fixed left"); // TODO
            }

            Class<?> leftType = isDeclarationExpression(left) ? convertFromPrimitiveType(left.getType()) : left.getType();
            Class<?> rightType = isDeclarationExpression(right) ? convertFromPrimitiveType(right.getType()) : right.getType();

            jitExpression(left, type != null ? type : leftType);
            if (isDeclarationExpression(left) && left.getType().isPrimitive()) {
                castFromPrimitive(left.getType());
            }
            store(LEFT_OPERAND, leftType);

            jitExpression(right, type != null ? type : rightType);
            if (isDeclarationExpression(right) && right.getType().isPrimitive()) {
                castFromPrimitive(right.getType());
            }
            store(RIGHT_OPERAND, rightType);

            Label shortcutEvaluation = new Label();
            BooleanOperator operation = singleCondition.getOperation();
            prepareLeftOperand(operation, type, leftType, rightType, shortcutEvaluation);
            prepareRightOperand(right, type, rightType, shortcutEvaluation);

            load(LEFT_OPERAND);
            load(RIGHT_OPERAND);

            if (operation == BooleanOperator.CONTAINS) {
                invokeStatic(EvaluatorHelper.class, "contains", boolean.class, Object.class, rightType.isPrimitive() ? rightType : Object.class);
            } else if (operation == BooleanOperator.MATCHES) {
                invokeVirtual(type, "matches", boolean.class, String.class);
            } else if (operation == BooleanOperator.SOUNDSLIKE) {
                invokeStatic(EvaluatorHelper.class, "soundslike", boolean.class, String.class, String.class);
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
                    invokeInterface(type, "compareTo", int.class, type == Comparable.class ? Object.class : type);
                } else {
                    invokeVirtual(type, "compareTo", int.class, type);
                }
                mv.visitInsn(ICONST_0);
                jitPrimitiveOperation(operation, int.class);
            }

            mv.visitLabel(shortcutEvaluation);
        }

        private void prepareLeftOperand(BooleanOperator operation, Class<?> type, Class<?> leftType, Class<?> rightType, Label shortcutEvaluation) {
            if (leftType.isPrimitive()) {
                if (type != null) {
                    castOrCoercePrimitive(LEFT_OPERAND, leftType, type);
                }
                return;
            }

            Label notNullLabel = jitLeftIsNull(type == null || leftType == type ?
                    jitNullSafeOperationStart() :
                    jitNullSafeCoercion(leftType, type));

            if (operation.isEquality() && !rightType.isPrimitive()) {
                // if (left == null) return right == null
                checkNullEquality();
            } else {
                // if (left == null) return false
                mv.visitInsn(ICONST_0);
            }

            mv.visitJumpInsn(GOTO, shortcutEvaluation);
            mv.visitLabel(notNullLabel);
        }

        private void prepareRightOperand(Expression right, Class<?> type, Class<?> rightType, Label shortcutEvaluation) {
            if (rightType.isPrimitive()) {
                if (type != null) {
                    castOrCoercePrimitive(RIGHT_OPERAND, rightType, type);
                }
                return;
            }

            Label nullLabel = new Label();
            Label notNullLabel = new Label();
            load(RIGHT_OPERAND);
            mv.visitJumpInsn(IFNULL, nullLabel);
            if (type != null && !isFixed(right) && rightType != type) {
                castOrCoerceTo(RIGHT_OPERAND, rightType, type, nullLabel);
            }
            mv.visitJumpInsn(GOTO, notNullLabel);
            mv.visitLabel(nullLabel);
            mv.visitInsn(ICONST_0);
            mv.visitJumpInsn(GOTO, shortcutEvaluation);
            mv.visitLabel(notNullLabel);
        }

        private void checkNullEquality() {
            Label rightNullLabel = new Label();
            Label rightNotNullLabel = new Label();
            load(RIGHT_OPERAND);
            mv.visitJumpInsn(IFNULL, rightNullLabel);
            mv.visitInsn(ICONST_0);
            mv.visitJumpInsn(GOTO, rightNotNullLabel);
            mv.visitLabel(rightNullLabel);
            mv.visitInsn(ICONST_1);
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
            castOrCoerceTo(LEFT_OPERAND, fromType, toType, nullLabel);
            return nullLabel;
        }

        private Label jitLeftIsNull(Label nullLabel) {
            Label notNullLabel = new Label();
            mv.visitJumpInsn(GOTO, notNullLabel);
            mv.visitLabel(nullLabel);
            return notNullLabel;
        }

        private void castOrCoerceTo(int regNr, Class<?> fromType, Class<?> toType, Label nullLabel) {
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

            boolean isNumber = Number.class.isAssignableFrom(toType);

            Label tryOk = null;
            Label inCatch = null;
            if (isNumber) {
                Label beforeTry = new Label();
                tryOk = new Label();
                inCatch = new Label();
                mv.visitTryCatchBlock(beforeTry, tryOk, inCatch, "java/lang/NumberFormatException");
                mv.visitLabel(beforeTry);
            }

            mv.visitTypeInsn(NEW, internalName(toType));
            mv.visitInsn(DUP);
            load(regNr);
            coerceByConstructor(fromType, toType);
            store(regNr, toType);

            if (isNumber) {
                Label afterCatch = new Label();
                mv.visitLabel(tryOk);
                mv.visitJumpInsn(GOTO, afterCatch);
                mv.visitLabel(inCatch);
                mv.visitInsn(POP);
                mv.visitInsn(ACONST_NULL);
                mv.visitVarInsn(ASTORE, regNr);
                mv.visitJumpInsn(GOTO, nullLabel);
                mv.visitLabel(afterCatch);
            }

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
            if (fromType == toType) {
                return;
            }
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

        private Class<?> jitExpression(Expression exp) {
            return jitExpression(exp, exp.getType());
        }

        private Class<?> jitExpression(Expression exp, Class<?> requiredClass) {
            if (exp instanceof FixedExpression) {
                push(((FixedExpression) exp).getValue(), requiredClass);
                return exp.getType();
            } else if (exp instanceof EvaluatedExpression) {
                return jitEvaluatedExpression((EvaluatedExpression) exp, true, Object.class);
            } else if (exp instanceof VariableExpression) {
                return jitVariableExpression((VariableExpression) exp);
            } else if (exp instanceof AritmeticExpression) {
                return jitAritmeticExpression((AritmeticExpression)exp);
            } else if (exp instanceof ArrayCreationExpression) {
                return jitArrayCreationExpression((ArrayCreationExpression) exp);
            } else {
                throw new RuntimeException("Unknown expression: " + exp);
            }
        }

        private Class<?> jitArrayCreationExpression(ArrayCreationExpression exp) {
            createArray(exp.getComponentType(), exp.items.size());
            for (int i = 0; i < exp.items.size(); i++) {
                mv.visitInsn(DUP);
                mv.visitLdcInsn(i);
                jitExpression(exp.items.get(i));
                mv.visitInsn(getCodeForType(exp.getComponentType(), IASTORE));
            }
            return exp.getType();
        }

        private Class<?> jitEvaluatedExpression(EvaluatedExpression exp, boolean firstInvocation, Class<?> currentClass) {
            if (exp.firstExpression != null) {
                currentClass = jitExpression(exp.firstExpression, currentClass);
                if (exp.firstExpression instanceof FixedExpression) {
                    firstInvocation = false;
                }
            }
            Iterator<Invocation> invocations = exp.invocations.iterator();
            currentClass = jitInvocation(invocations.next(), currentClass, firstInvocation);
            while (invocations.hasNext()) {
                currentClass = jitInvocation(invocations.next(), currentClass, false);
            }
            return currentClass;
        }

        private Class<?> jitVariableExpression(VariableExpression exp) {
            jitReadVariable(exp.variableName);
            return exp.subsequentInvocations != null ?
                    jitEvaluatedExpression(exp.subsequentInvocations, false, exp.getVariableType()) :
                    exp.getType();
        }

        private void jitReadVariable(String variableName) {
            for (int i = 0; i < declarations.length; i++) {
                if (declarations[i].getBindingName().equals(variableName)) {
                    load(declPositions[i]);
                    return;
                }
            }
            throw new RuntimeException("Unknown variable name: " + variableName);
        }

        private Class<?> jitAritmeticExpression(AritmeticExpression aritmeticExpression) {
            if (aritmeticExpression.isStringConcat()) {
                jitStringConcat(aritmeticExpression.left, aritmeticExpression.right);
            } else {
                Class<?> operationType = aritmeticExpression.getType();
                jitExpressionToPrimitiveType(aritmeticExpression.left, operationType);
                jitExpressionToPrimitiveType(aritmeticExpression.right, aritmeticExpression.operator.isBitwiseOperation() ? int.class : operationType);
                jitAritmeticOperation(operationType, aritmeticExpression.operator);
            }
            return aritmeticExpression.getType();
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
            jitExpression(expression, primitiveType);
            if (!isFixed(expression)) {
                cast(expression.getType(), primitiveType);
            }
        }

        private boolean isDeclarationExpression(Expression expression) {
            return expression instanceof VariableExpression && ((VariableExpression)expression).subsequentInvocations == null;
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
                    case MOD:
                        mv.visitInsn(IREM);
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
                    case MOD:
                        mv.visitInsn(LREM);
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
                    case MOD:
                        mv.visitInsn(DREM);
                        break;
                }
            }
        }

        private Class<?> jitInvocation(Invocation invocation, Class<?> currentClass, boolean firstInvocation) {
            if (invocation instanceof MethodInvocation) {
                jitMethodInvocation((MethodInvocation)invocation, currentClass, firstInvocation);
            } else if (invocation instanceof ConstructorInvocation) {
                jitConstructorInvocation((ConstructorInvocation) invocation);
            } else if (invocation instanceof ArrayAccessInvocation) {
                jitArrayAccessInvocation((ArrayAccessInvocation) invocation);
            } else if (invocation instanceof ArrayLengthInvocation) {
                jitArrayLenghtInvocation();
            } else if (invocation instanceof ListAccessInvocation) {
                jitListAccessInvocation((ListAccessInvocation) invocation);
            } else if (invocation instanceof MapAccessInvocation) {
                jitMapAccessInvocation((MapAccessInvocation) invocation, firstInvocation);
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
                if (!firstInvocation) {
                    mv.visitVarInsn(ALOAD, 1);
                }
                return;
            }

            if (!method.getDeclaringClass().isAssignableFrom(currentClass) && (method.getModifiers() & Modifier.STATIC) == 0) {
                cast(method.getDeclaringClass());
            }

            int argumentCounter = 0;
            for (Expression argument : invocation.getArguments()) {
                cast(jitExpression(argument), method.getParameterTypes()[argumentCounter++]);
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

        private void jitArrayAccessInvocation(ArrayAccessInvocation invocation) {
            cast(invocation.getArrayType());
            jitExpression(invocation.getIndex(), int.class);
            mv.visitInsn(getCodeForType(invocation.getReturnType(), IALOAD));
        }

        private void jitArrayLenghtInvocation() {
            invokeStatic(EvaluatorHelper.class, "arrayLenght", int.class, Object.class);
        }

        private void jitListAccessInvocation(ListAccessInvocation invocation) {
            jitExpression(invocation.getIndex(), int.class);
            invokeInterface(List.class, "get", Object.class, int.class);
            if (invocation.getReturnType() != Object.class) {
                cast(invocation.getReturnType());
            }
        }

        private void jitMapAccessInvocation(MapAccessInvocation invocation, boolean firstInvocation) {
            if (firstInvocation) {
                mv.visitVarInsn(ALOAD, 1);
                cast(Map.class);
            }
            Class<?> keyClass = jitExpression(invocation.getKey(), invocation.getKeyType());
            if (keyClass.isPrimitive()) {
                convertPrimitiveToObject(keyClass);
            }
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
            if (class1 == class2) {
                result = class1;
            } else if (class1 == NullType.class) {
                result = convertFromPrimitiveType(class2);
            } else if (class2 == NullType.class) {
                result = convertFromPrimitiveType(class1);
            } else if (class1 == Object.class) {
                result = convertFromPrimitiveType(class2);
            } else if (class2 == Object.class) {
                result = convertFromPrimitiveType(class1);
            } else if (class1 == String.class) {
                result = convertFromPrimitiveType(class2);
            } else if (class2 == String.class) {
                result = convertFromPrimitiveType(class1);
            }

            if (result == null) {
                result = findCommonClass(class1, class2, primitive2);
            }
            if (result == null) {
                result = findCommonClass(class2, class1, primitive1);
            }
            if (result == null) {
                throw new RuntimeException( "Cannot find a common class between " + class1.getName() + " and " + class2.getName() +
                                             " ||  " + class1.hashCode() + " vs " + class2.hashCode()
                );
            }
            return result == Number.class ? Double.class : result;
        }

        private Class<?> findCommonClass(Class<?> class1, Class<?> class2, boolean canBePrimitive) {
            if (class1.isAssignableFrom(class2)) {
                return class1;
            }

            if (class1 == boolean.class && class2 == Boolean.class) {
                return canBePrimitive ? boolean.class : Boolean.class;
            }
            if (class1 == char.class && class2 == Character.class) {
                return canBePrimitive ? char.class : Character.class;
            }
            if (class1 == byte.class && class2 == Byte.class) {
                return canBePrimitive ? byte.class : Byte.class;
            }
            if (class1 == short.class && class2 == Short.class) {
                return canBePrimitive ? short.class : Short.class;
            }

            if (class1 == Number.class && class2.isPrimitive()) {
                return Double.class;
            }

            if (class1 == int.class || class1 == short.class || class1 == byte.class) {
                if (class2 == Integer.class) {
                    return canBePrimitive ? int.class : Integer.class;
                }
                if (class2 == long.class) {
                    return long.class;
                }
                if (class2 == Long.class) {
                    return canBePrimitive ? long.class : Long.class;
                }
                if (class2 == float.class) {
                    return float.class;
                }
                if (class2 == Float.class) {
                    return canBePrimitive ? float.class : Float.class;
                }
                if (class2 == double.class) {
                    return double.class;
                }
                if (class2 == Double.class) {
                    return canBePrimitive ? double.class : Double.class;
                }
                if (class2 == BigInteger.class) {
                    return BigInteger.class;
                }
                if (class2 == BigDecimal.class) {
                    return BigDecimal.class;
                }
            }

            if (class1 == long.class) {
                if (class2 == int.class) {
                    return long.class;
                }
                if (class2 == Integer.class) {
                    return canBePrimitive ? long.class : Long.class;
                }
                if (class2 == Long.class) {
                    return canBePrimitive ? long.class : Long.class;
                }
                if (class2 == float.class) {
                    return double.class;
                }
                if (class2 == Float.class) {
                    return canBePrimitive ? double.class : Double.class;
                }
                if (class2 == double.class) {
                    return double.class;
                }
                if (class2 == Double.class) {
                    return canBePrimitive ? double.class : Double.class;
                }
                if (class2 == BigInteger.class) {
                    return BigInteger.class;
                }
                if (class2 == BigDecimal.class) {
                    return BigDecimal.class;
                }
            }

            if (class1 == float.class) {
                if (class2 == int.class) {
                    return float.class;
                }
                if (class2 == Integer.class) {
                    return canBePrimitive ? float.class : Float.class;
                }
                if (class2 == long.class) {
                    return double.class;
                }
                if (class2 == Long.class) {
                    return canBePrimitive ? double.class : Double.class;
                }
                if (class2 == Float.class) {
                    return canBePrimitive ? float.class : Float.class;
                }
                if (class2 == double.class) {
                    return double.class;
                }
                if (class2 == Double.class) {
                    return canBePrimitive ? double.class : Double.class;
                }
                if (class2 == BigInteger.class) {
                    return BigDecimal.class;
                }
                if (class2 == BigDecimal.class) {
                    return BigDecimal.class;
                }
            }

            if (class1 == double.class) {
                if (class2 == int.class) {
                    return float.class;
                }
                if (class2 == Integer.class) {
                    return canBePrimitive ? double.class : Double.class;
                }
                if (class2 == long.class) {
                    return double.class;
                }
                if (class2 == Long.class) {
                    return canBePrimitive ? double.class : Double.class;
                }
                if (class2 == float.class) {
                    return double.class;
                }
                if (class2 == Float.class) {
                    return canBePrimitive ? double.class : Double.class;
                }
                if (class2 == Double.class) {
                    return canBePrimitive ? double.class : Double.class;
                }
                if (class2 == BigInteger.class) {
                    return BigDecimal.class;
                }
                if (class2 == BigDecimal.class) {
                    return BigDecimal.class;
                }
            }

            if (class1 == Integer.class) {
                if (class2 == Long.class) {
                    return Long.class;
                }
                if (class2 == Float.class) {
                    return Float.class;
                }
                if (class2 == Double.class) {
                    return Double.class;
                }
                if (class2 == BigInteger.class) {
                    return BigInteger.class;
                }
                if (class2 == BigDecimal.class) {
                    return BigDecimal.class;
                }
            }

            if (class1 == Long.class) {
                if (class2 == Float.class) {
                    return Double.class;
                }
                if (class2 == Double.class) {
                    return Double.class;
                }
                if (class2 == BigInteger.class) {
                    return BigInteger.class;
                }
                if (class2 == BigDecimal.class) {
                    return BigDecimal.class;
                }
            }

            if (class1 == Float.class) {
                if (class2 == Double.class) {
                    return Double.class;
                }
                if (class2 == BigInteger.class) {
                    return BigDecimal.class;
                }
                if (class2 == BigDecimal.class) {
                    return BigDecimal.class;
                }
            }

            if (class1 == Double.class) {
                if (class2 == BigInteger.class) {
                    return BigDecimal.class;
                }
                if (class2 == BigDecimal.class) {
                    return BigDecimal.class;
                }
            }

            if (class1 == BigInteger.class) {
                if (class2 == BigDecimal.class) {
                    return BigDecimal.class;
                }
            }

            return null;
        }
    }
}
