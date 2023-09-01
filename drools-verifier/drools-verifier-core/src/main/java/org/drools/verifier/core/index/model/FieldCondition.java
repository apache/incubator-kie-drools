package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.util.PortablePreconditions;

public class FieldCondition<T extends Comparable>
        extends Condition {

    private final Field field;
    private String operator;

    public FieldCondition(final Field field,
                          final Column column,
                          final String operator,
                          final Values<T> values,
                          final AnalyzerConfiguration configuration) {
        super(column,
              ConditionSuperType.FIELD_CONDITION,
              values,
              configuration);

        this.field = PortablePreconditions.checkNotNull("field",
                                                        field);
        this.operator = PortablePreconditions.checkNotNull("operator",
                                                           operator);
    }

    public Field getField() {
        return field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(final String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return field + " " + operator + " " + getValues();
    }

    @Override
    public Key[] keys() {
        return super.keys();
    }

    public static KeyDefinition[] keyDefinitions() {
        return Condition.keyDefinitions();
    }
}
