package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.kie.dmn.feel.lang.EvaluationContext;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public interface DialectHandler {

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getAddOperationMap(EvaluationContext ctx);

    Object executeAdd(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getAndOperationMap(EvaluationContext ctx);

    Object executeAnd(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getEqualOperationMap(EvaluationContext ctx);

    Object executeEqual(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getGteOperationMap(EvaluationContext ctx);

    Object executeGte(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getGtOperationMap(EvaluationContext ctx);

    Object executeGt(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getLteOperationMap(EvaluationContext ctx);

    Object executeLte(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getLtOperationMap(EvaluationContext ctx);

    Object executeLt(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getNotEqualOperationMap(EvaluationContext ctx);

    Object executeNotEqual(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getOrOperationMap(EvaluationContext ctx);

    Object executeOr(Object left, Object right, EvaluationContext ctx);

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getPowOperationMap(EvaluationContext ctx);

    Object executePow(Object left, Object right, EvaluationContext ctx);


}
