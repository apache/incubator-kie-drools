package org.drools.verifier.core.cache.inspectors.condition;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.maps.InspectorFactory;

public class ConditionInspectorFactory
        extends InspectorFactory<ConditionInspector, Condition> {

    public ConditionInspectorFactory(final AnalyzerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public ConditionInspector make(final Condition condition) {

        if (condition instanceof FieldCondition) {
            return makeFieldCondition((FieldCondition) condition);
        } else {
            return null;
        }
    }

    private ConditionInspector makeFieldCondition(final FieldCondition condition) {
        if (!condition.getValues().isEmpty() && condition.getFirstValue() instanceof String) {
            return new StringConditionInspector(condition,
                                                configuration);
        } else if (!condition.getValues().isEmpty() && condition.getFirstValue() instanceof Boolean) {
            return new BooleanConditionInspector(condition,
                                                 configuration);
        } else if (!condition.getValues().isEmpty() && condition.getFirstValue() instanceof Integer) {
            return new NumericIntegerConditionInspector(condition,
                                                        configuration);
        } else {
            return new ComparableConditionInspector<>(condition,
                                                      configuration);
        }
    }
}
