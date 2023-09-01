package org.kie.pmml.models.drools.tuples;

import java.io.Serializable;
import java.util.Objects;

import org.kie.pmml.api.enums.OPERATOR;

/**
 * Tupla representing the operator and the value to be applied to a given field
 */
public class KiePMMLOperatorValue implements Serializable {

    public static final String VALUE_CONSTRAINT_PATTERN = "value %s %s";
    private static final long serialVersionUID = 4850428778643763607L;
    private final OPERATOR operator;
    private final Object value;
    private final String constraintsString;

    public KiePMMLOperatorValue(OPERATOR operator, Object value) {
        this.operator = operator;
        this.value = value;
        constraintsString = buildConstraintsString();
    }

    public OPERATOR getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }

    public String getConstraintsAsString() {
        return constraintsString;
    }

    @Override
    public String toString() {
        return "KiePMMLOperatorValue{" +
                "operator='" + operator + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLOperatorValue that = (KiePMMLOperatorValue) o;
        return Objects.equals(operator, that.operator) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, value);
    }

    protected String buildConstraintsString() {
        return String.format(VALUE_CONSTRAINT_PATTERN, operator.getOperator(), value);
    }
}
