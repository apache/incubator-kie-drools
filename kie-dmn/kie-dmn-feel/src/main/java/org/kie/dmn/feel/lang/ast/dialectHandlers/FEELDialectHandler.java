/*
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
package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.kie.dmn.feel.lang.EvaluationContext;

import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Handler implementation of the DialectHandler interface providing FEEL specific
 * functionalities
 */
public class FEELDialectHandler extends DefaultDialectHandler implements DialectHandler {

    /**
     * Builds the Feel specific 'Addition' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the Feel specific 'Addition' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getAddOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // String + String concatenates, else null
        map.put(
                new CheckedPredicate((left, right) -> left instanceof String || right instanceof String, false),
                (left, right) -> {
                    if (left instanceof String leftString && right instanceof String rightString) {
                        return leftString + rightString;
                    }
                    return null;
                });

        // date + number → null
        map.put(
                new CheckedPredicate((left, right) -> left instanceof LocalDate && right instanceof Number, true),
                (left, right) -> null);

        // number + date → null
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof LocalDate, true),
                (left, right) -> null);

        // Number + null → null
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right == null, false),
                (left, right) -> null);

        // null + Number → null
        map.put(
                new CheckedPredicate((left, right) -> left == null && right instanceof Number, false),
                (left, right) -> null);

        map.putAll(getCommonAddOperations(ctx));
        return map;
    }

    /**
     * Builds the Feel specific 'And' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the Feel specific 'And' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getAndOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        map.putAll(getCommonAndOperations(ctx));
        return map;
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getEqualOperations(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonEqualOperations(ctx));
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getGteOperations(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonGteOperations(ctx));
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getGtOperations(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonGtOperations(ctx));
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getLteOperations(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonLteOperations(ctx));
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getLtOperations(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonLtOperations(ctx));
    }

    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getNotEqualOperations(EvaluationContext ctx) {
        return new LinkedHashMap<>(getCommonNotEqualOperations(ctx));
    }

    /**
     * Builds the Feel specific 'OR' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the Feel specific 'OR' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getOrOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        map.putAll(getCommonOrOperations(ctx));

        return map;
    }

    /**
     * Builds the Feel specific 'Power' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the Feel specific 'Power' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getPowOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // Either null → null
        map.put(
                new CheckedPredicate((left, right) -> left instanceof String || right instanceof String, false),
                (left, right) -> null);

        map.putAll(getCommonPowOperations(ctx));
        return map;
    }

    /**
     * Builds the Feel specific 'Substraction' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the Feel specific 'Substraction' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getSubOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        map.put(
                new CheckedPredicate((left, right) -> (left instanceof String || right instanceof String), false),
                (left, right) -> null);
        map.putAll(getCommonSubOperations(ctx));
        return map;
    }

    /**
     * Builds the Feel specific 'Multiplication' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the Feel specific 'Multiplication' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getMultOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();
        // String * Number or Number * String → invalid
        map.put(
                new CheckedPredicate((left, right) -> (left instanceof Number && right instanceof String) ||
                        (left instanceof String && right instanceof Number), false),
                (left, right) -> null);

        // String * String → invalid
        map.put(
                new CheckedPredicate((left, right) -> left instanceof String && right instanceof String, false),
                (left, right) -> null);

        // Temporal * Number → invalid
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Temporal && right instanceof Number, false),
                (left, right) -> null);

        // Number * Temporal → invalid
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Temporal, false),
                (left, right) -> null);

        // Duration * null → null + error
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right == null, true),
                (left, right) -> null);

        // null * Duration → null + error
        map.put(
                new CheckedPredicate((left, right) -> left == null && right instanceof Duration, true),
                (left, right) -> null);

        // ChronoPeriod * null → null + error
        map.put(
                new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right == null, true),
                (left, right) -> null);

        // null * ChronoPeriod → null + error
        map.put(
                new CheckedPredicate((left, right) -> left == null && right instanceof ChronoPeriod, true),
                (left, right) -> null);

        // FEEL-specific: Duration * Duration → null
        map.put(
                new CheckedPredicate((left, right) -> left instanceof Duration && right instanceof Duration, true),
                (left, right) -> null);

        // ChronoPeriod * ChronoPeriod → null
        map.put(
                new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right instanceof ChronoPeriod, true),
                (left, right) -> null);

        map.putAll(getCommonMultOperations(ctx));
        return map;
    }

    /**
     * Builds the Feel specific 'Division' operations.
     * 
     * @param ctx : Current Evaluation context
     * @return : a Map of CheckedPredicate to BiFunction representing the Feel specific 'Division' operations
     */
    @Override
    public Map<CheckedPredicate, BiFunction<Object, Object, Object>> getDivisionOperations(EvaluationContext ctx) {
        Map<CheckedPredicate, BiFunction<Object, Object, Object>> map = new LinkedHashMap<>();

        // string ÷ number or number ÷ string → null
        map.put(new CheckedPredicate((left, right) -> (left instanceof Number && right instanceof String) ||
                (left instanceof String && right instanceof Number), false),
                (left, right) -> null);

        // string ÷ string → null
        map.put(new CheckedPredicate((left, right) -> left instanceof String && right instanceof String, false),
                (left, right) -> null);

        // number ÷ duration → invalid
        map.put(new CheckedPredicate((left, right) -> left instanceof Number && right instanceof Duration, true),
                (left, right) -> null);

        // number ÷ period → invalid
        map.put(new CheckedPredicate((left, right) -> left instanceof Number && right instanceof ChronoPeriod, true),
                (left, right) -> null);

        // temporal ÷ number (other than Duration/Period) → invalid
        map.put(new CheckedPredicate((left, right) -> left instanceof Temporal && right instanceof Number, false),
                (left, right) -> null);

        // duration ÷ null → null
        map.put(new CheckedPredicate((left, right) -> left instanceof Duration && right == null, false),
                (left, right) -> null);

        // null ÷ duration → null
        map.put(new CheckedPredicate((left, right) -> left == null && right instanceof Duration, false),
                (left, right) -> null);

        // period ÷ null → null
        map.put(new CheckedPredicate((left, right) -> left instanceof ChronoPeriod && right == null, false),
                (left, right) -> null);

        // null ÷ period → null
        map.put(new CheckedPredicate((left, right) -> left == null && right instanceof ChronoPeriod, false),
                (left, right) -> null);

        // null ÷ Number → null
        map.put(new CheckedPredicate((left, right) -> left == null && right instanceof Number, false),
                (left, right) -> null);

        map.putAll(getCommonDivisionOperations(ctx));

        return map;
    }

}
