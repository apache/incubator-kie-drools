package org.kie.pmml.models.drools.tuples;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.OPERATOR;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue.VALUE_CONSTRAINT_PATTERN;

public class KiePMMLOperatorValueTest {

    @Test
    void getConstraintsAsString() {
        OPERATOR operator = OPERATOR.LESS_THAN;
        Object value = 234;
        KiePMMLOperatorValue kiePMMLOperatorValue = new KiePMMLOperatorValue(operator, value);
        String retrieved = kiePMMLOperatorValue.getConstraintsAsString();
        String expected = String.format(VALUE_CONSTRAINT_PATTERN, operator.getOperator(), value);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void buildConstraintsString() {
        OPERATOR operator = OPERATOR.LESS_THAN;
        Object value = 234;
        KiePMMLOperatorValue kiePMMLOperatorValue = new KiePMMLOperatorValue(operator, value);
        String retrieved = kiePMMLOperatorValue.buildConstraintsString();
        String expected = String.format(VALUE_CONSTRAINT_PATTERN, operator.getOperator(), value);
        assertThat(retrieved).isEqualTo(expected);
    }
}