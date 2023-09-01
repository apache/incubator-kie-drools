package org.drools.base.rule;

public class EvalConditionFactoryImpl implements EvalConditionFactory {

    @Override
    public EvalCondition createEvalCondition(final Declaration[] requiredDeclarations) {
        return new EvalCondition(requiredDeclarations);
    }
}
