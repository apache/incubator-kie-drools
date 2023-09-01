package org.kie.dmn.feel.runtime;

import java.util.function.BiFunction;

import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.lang.EvaluationContext;

@FunctionalInterface
public interface UnaryTest extends DMNUnaryTest, BiFunction<EvaluationContext, Object, Boolean> {

}
