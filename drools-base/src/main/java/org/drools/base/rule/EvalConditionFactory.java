package org.drools.base.rule;

import org.kie.api.internal.utils.KieService;

public interface EvalConditionFactory extends KieService {

    EvalCondition createEvalCondition(final Declaration[] requiredDeclarations);

    class Factory {

        private static class LazyHolder {

            private static final EvalConditionFactory INSTANCE = createInstance();

            private static EvalConditionFactory createInstance() {
                EvalConditionFactory factory = KieService.load(EvalConditionFactory.class);
                return factory != null ? factory : new EvalConditionFactoryImpl();
            }
        }

        public static EvalConditionFactory get() {
            return LazyHolder.INSTANCE;
        }

        private Factory() {}
    }
}
