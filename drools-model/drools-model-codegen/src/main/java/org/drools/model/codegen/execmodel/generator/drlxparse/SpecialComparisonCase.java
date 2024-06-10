/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator.drlxparse;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.codegen.execmodel.generator.TypedExpression;

import static java.util.Optional.of;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toJavaParserType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.uncastExpr;
import static org.drools.model.codegen.execmodel.generator.drlxparse.ConstraintParser.isNumber;
import static org.drools.model.codegen.execmodel.generator.drlxparse.ConstraintParser.operatorToName;

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
        if (isNumber(left) && !isObject(right.getRawClass()) || isNumber(right) && !isObject(left.getRawClass())) { // Don't coerce Object yet. EvaluationUtil will handle it dynamically later
            if (typeNeedsCast(left.getType()) || typeNeedsCast(right.getType())) {
                return new ComparisonWithCast(true, left, right, of(Number.class), of(Number.class));
            } else {
                return new NumberComparisonWithoutCast(left, right);
            }
        }
        if (!isComparable(left.getRawClass()) && !isComparable(right.getRawClass())){
            return new ComparisonWithCast(left, right, of(Comparable.class), of(Comparable.class));
        }
        return new PlainEvaluation(left, right);
    }

    private static boolean typeNeedsCast(Type t) {
        return t instanceof Class && ( isObject((Class<?>)t) || isMap((Class<?>) t) || isList((Class<?>) t) );
    }

    private static boolean isList(Class<?> t) {
        return List.class.isAssignableFrom(t);
    }

    private static boolean isMap(Class<?> t) {
        return Map.class.isAssignableFrom(t);
    }

    private static boolean isComparable(Class<?> t) {
        return Comparable.class.isAssignableFrom(t) || Number.class.isAssignableFrom(t) || t.isPrimitive();
    }

    static boolean isObject(Class<?> clazz) {
        return clazz.equals(Object.class);
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

class ComparisonWithCast extends SpecialComparisonCase {

    private boolean isNumberComparison;
    Optional<Class<?>> leftTypeCast;
    Optional<Class<?>> rightTypeCast;

    ComparisonWithCast(boolean isNumberComparison,
                       TypedExpression left,
                       TypedExpression right,
                       Optional<Class<?>> leftTypeCast,
                       Optional<Class<?>> rightTypeCast) {
        super(left, right);
        this.isNumberComparison = isNumberComparison;
        this.leftTypeCast = leftTypeCast;
        this.rightTypeCast = rightTypeCast;
    }

    ComparisonWithCast(TypedExpression left,
                       TypedExpression right,
                       Optional<Class<?>> leftTypeCast,
                       Optional<Class<?>> rightTypeCast) {
        this(false, left, right, leftTypeCast, rightTypeCast);
    }

    @Override
    public ConstraintParser.SpecialComparisonResult createCompareMethod(BinaryExpr.Operator operator) {
        // Numbers have a more specific comparison method with this suffix
        // See org.drools.modelcompiler.util.EvaluationUtil.greaterThanNumbers(java.lang.Number, java.lang.Number)
        String numberMethod = isNumberComparison ? "Numbers" : "";
        String methodName = getMethodName(operator) + numberMethod;
        MethodCallExpr compareMethod = new MethodCallExpr(null, methodName);

        if(leftTypeCast.isPresent()) {
            CastExpr castExpr = new CastExpr(toJavaParserType(leftTypeCast.get()), left.getExpression());
            compareMethod.addArgument(castExpr);
            this.left = left.cloneWithNewExpression(castExpr);
        } else {
            compareMethod.addArgument(left.getExpression());
        }


        if(rightTypeCast.isPresent()) {
            CastExpr castExpr = new CastExpr(toJavaParserType(rightTypeCast.get()), right.getExpression());
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
