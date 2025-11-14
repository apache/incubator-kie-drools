package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.kie.dmn.feel.lang.EvaluationContext;

import java.util.Map;
import java.util.function.BiFunction;

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

}
