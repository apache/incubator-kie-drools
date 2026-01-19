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

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import org.kie.dmn.feel.lang.EvaluationContext;

public interface DialectHandler {

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getAddOperations(EvaluationContext ctx);

    Object executeAdd(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getAndOperations(EvaluationContext ctx);

    Object executeAnd(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getEqualOperations(EvaluationContext ctx);

    Object executeEqual(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getGteOperations(EvaluationContext ctx);

    Object executeGte(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getGtOperations(EvaluationContext ctx);

    Object executeGt(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getLteOperations(EvaluationContext ctx);

    Object executeLte(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getLtOperations(EvaluationContext ctx);

    Object executeLt(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getNotEqualOperations(EvaluationContext ctx);

    Object executeNotEqual(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getOrOperations(EvaluationContext ctx);

    Object executeOr(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getPowOperations(EvaluationContext ctx);

    Object executePow(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getSubOperations(EvaluationContext ctx);

    Object executeSub(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getMultOperations(EvaluationContext ctx);

    Object executeMult(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getDivisionOperations(EvaluationContext ctx);

    Object executeDivision(Object left, Object right, EvaluationContext ctx);

    Boolean compare(Object left, Object right, BiPredicate<Comparable, Comparable> op);

}
