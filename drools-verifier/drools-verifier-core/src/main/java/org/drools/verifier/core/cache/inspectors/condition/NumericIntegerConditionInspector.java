package org.drools.verifier.core.cache.inspectors.condition;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.relations.Operator;

public class NumericIntegerConditionInspector
        extends ComparableConditionInspector<Integer> {

    public NumericIntegerConditionInspector(final FieldCondition<Integer> fieldCondition,
                                            final AnalyzerConfiguration configuration) {
        super(fieldCondition,
              configuration);
    }

    @Override
    public boolean subsumes(Object other) {
        if (other instanceof NumericIntegerConditionInspector) {
            final NumericIntegerConditionInspector anotherPoint = (NumericIntegerConditionInspector) other;
            if (anotherPoint != null && anotherPoint.getValue() != null) {
                if ((anotherPoint.getOperator().equals(Operator.LESS_THAN) && operator.equals(Operator.LESS_OR_EQUAL))) {
                    return covers(anotherPoint.getValue() - 1);
                } else if ((anotherPoint.getOperator().equals(Operator.GREATER_OR_EQUAL) && operator.equals(Operator.GREATER_THAN))) {
                    if (getValue().equals(anotherPoint.getValue() - 1)) {
                        return true;
                    }
                } else if ((anotherPoint.getOperator().equals(Operator.GREATER_THAN) && operator.equals(Operator.GREATER_OR_EQUAL))) {
                    return covers(anotherPoint.getValue() + 1);
                } else if ((anotherPoint.getOperator().equals(Operator.LESS_OR_EQUAL) && operator.equals(Operator.LESS_THAN))) {
                    if (getValue().equals(anotherPoint.getValue() + 1)) {
                        return true;
                    }
                }
            }
        }

        return super.subsumes(other);
    }
}
