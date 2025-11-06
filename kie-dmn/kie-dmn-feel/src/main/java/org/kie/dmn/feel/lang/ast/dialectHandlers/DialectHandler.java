package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.kie.dmn.feel.lang.EvaluationContext;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public interface DialectHandler {

    Map<DefaultDialectHandler.CheckedPredicate, BiFunction<Object, Object, Object>> getAddOperationMap(EvaluationContext ctx);

    Object executeAdd(Object left, Object right, EvaluationContext ctx);

    default List<BiPredicate<Object, Object>> getNotifiedPredicates() {
        return List.of();
    }
}
