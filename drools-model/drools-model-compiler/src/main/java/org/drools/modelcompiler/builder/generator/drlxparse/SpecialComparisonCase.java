/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.drlxparse;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser.isNumber;
import static org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser.isObject;
import static org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser.operatorToName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.uncastExpr;

// TODO need to add a specific case for map.
// Also it would be better to move every coercion case here
abstract class SpecialComparisonCase {

    TypedExpression left;
    TypedExpression right;

    SpecialComparisonCase(TypedExpression left, TypedExpression right) {
        this.left = left;
        this.right = right;
    }

    String getMethodName(BinaryExpr.Operator operator) {
        return "org.drools.modelcompiler.util.EvaluationUtil." + operatorToName(operator);
    }

    abstract ConstraintParser.SpecialComparisonResult createCompareMethod(BinaryExpr.Operator operator);

    static SpecialComparisonCase specialComparisonFactory(TypedExpression left, TypedExpression right) {
        if (isNumber(left) && !isObject(right) || isNumber(right) && !isObject(left)) { // Don't coerce Object yet. EvaluationUtil will handle it dynamically later
            Optional<Class<?>> leftCast = typeNeedsCast(left.getType());
            Optional<Class<?>> rightCast = typeNeedsCast(right.getType());
            if (leftCast.isPresent() || rightCast.isPresent()) {
                return new NumberComparisonWithCast(left, right, leftCast, rightCast);
            } else {
                return new NumberComparisonWithoutCast(left, right);
            }
        } else {
            return new PlainEvaluation(left, right);
        }
    }

    private static Optional<Class<?>> typeNeedsCast(Type t) {
        boolean needCast = t.equals(Object.class) || Map.class.isAssignableFrom((Class<?>) t) || List.class.isAssignableFrom((Class<?>) t);
        if (needCast) {
            return Optional.of((Class<?>) t);
        } else {
            return Optional.empty();
        }
    }

    public TypedExpression getLeft() {
        return left;
    }

    public TypedExpression getRight() {
        return right;
    }
}

class NumberComparisonWithoutCast extends SpecialComparisonCase {

    NumberComparisonWithoutCast(TypedExpression left, TypedExpression right) {
        super(left, right);
    }

    @Override
    public ConstraintParser.SpecialComparisonResult createCompareMethod(BinaryExpr.Operator operator) {
        String methodName = getMethodName(operator) + "Numbers";
        MethodCallExpr compareMethod = new MethodCallExpr(null, methodName);
        compareMethod.addArgument(uncastExpr(left.getExpression()));
        compareMethod.addArgument(uncastExpr(right.getExpression()));
        return new ConstraintParser.SpecialComparisonResult(compareMethod, left, right);
    }
}

class NumberComparisonWithCast extends SpecialComparisonCase {

    Optional<Class<?>> leftTypeCast;
    Optional<Class<?>> rightTypeCast;

    NumberComparisonWithCast(TypedExpression left, TypedExpression right, Optional<Class<?>> leftTypeCast, Optional<Class<?>> rightTypeCast) {
        super(left, right);
        this.leftTypeCast = leftTypeCast;
        this.rightTypeCast = rightTypeCast;
    }

    @Override
    public ConstraintParser.SpecialComparisonResult createCompareMethod(BinaryExpr.Operator operator) {
        String methodName = getMethodName(operator) + "Numbers";
        MethodCallExpr compareMethod = new MethodCallExpr(null, methodName);

        ClassOrInterfaceType numberClass = toClassOrInterfaceType(Number.class);

        if(leftTypeCast.isPresent()) {
            CastExpr castExpr = new CastExpr(numberClass, left.getExpression());
            compareMethod.addArgument(castExpr);
            this.left = right.cloneWithNewExpression(castExpr);
        } else {
            compareMethod.addArgument(left.getExpression());
        }


        if(rightTypeCast.isPresent()) {
            CastExpr castExpr = new CastExpr(numberClass, right.getExpression());
            this.right = right.cloneWithNewExpression(castExpr);
            compareMethod.addArgument(castExpr);
        } else {
            compareMethod.addArgument(right.getExpression());
        }

        return new ConstraintParser.SpecialComparisonResult(compareMethod, this.left, this.right);
    }
}

class PlainEvaluation extends SpecialComparisonCase {

    PlainEvaluation(TypedExpression left, TypedExpression right) {
        super(left, right);
    }

    @Override
    public ConstraintParser.SpecialComparisonResult createCompareMethod(BinaryExpr.Operator operator) {
        String methodName = getMethodName(operator);
        MethodCallExpr compareMethod = new MethodCallExpr(null, methodName);
        compareMethod.addArgument(uncastExpr(left.getExpression()));
        compareMethod.addArgument(uncastExpr(right.getExpression()));
        return new ConstraintParser.SpecialComparisonResult(compareMethod, left, right);
    }
}
