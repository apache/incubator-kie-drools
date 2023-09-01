package org.drools.verifier.core.cache.inspectors.condition;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.FieldCondition;

public class BooleanConditionInspector
        extends ComparableConditionInspector {

    public BooleanConditionInspector(final FieldCondition<Boolean> fieldCondition,
                                     final AnalyzerConfiguration configuration) {
        super(fieldCondition,
              configuration);
    }

    @Override
    public boolean isRedundant(final Object other) {
        if (this.equals(other)) {
            return true;
        }
        if (other instanceof BooleanConditionInspector) {
            switch (operator) {
                case EQUALS:
                    switch (((BooleanConditionInspector) other).operator) {
                        case EQUALS:
                            return getValues().containsAll(((BooleanConditionInspector) other).getValues());
                        case NOT_EQUALS:
                            return !getValue().equals(((BooleanConditionInspector) other).getValue());
                        default:
                            return false;
                    }
                case NOT_EQUALS:
                    switch (((BooleanConditionInspector) other).operator) {
                        case EQUALS:
                            return !getValues().equals(((BooleanConditionInspector) other).getValues());
                        case NOT_EQUALS:
                            return getValues().containsAll(((BooleanConditionInspector) other).getValues());
                        default:
                            return false;
                    }
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean conflicts(final Object other) {
        return !isRedundant(other);
    }

    @Override
    public boolean overlaps(final Object other) {
        return isRedundant(other);
    }

    @Override
    public boolean subsumes(final Object other) {
        return isRedundant(other);
    }

    @Override
    public String toHumanReadableString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(field.getFactType());
        stringBuilder.append(".");
        stringBuilder.append(field.getName());
        stringBuilder.append(" ");
        stringBuilder.append(operator);
        stringBuilder.append(" ");
        stringBuilder.append(getValues());

        return stringBuilder.toString();
    }
}
