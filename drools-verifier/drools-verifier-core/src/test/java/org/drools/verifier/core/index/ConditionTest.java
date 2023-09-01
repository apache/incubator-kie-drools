package org.drools.verifier.core.index;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.ConditionSuperType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ConditionTest {

    private Condition<Integer> condition;

    @BeforeEach
    public void setUp() throws Exception {
        condition = new Condition<>(mock(Column.class),
                                  ConditionSuperType.FIELD_CONDITION,
                                  new Values<>(1),
                                  new AnalyzerConfigurationMock()) {

        };
    }

    @Test
    void valueSet() throws Exception {
        assertThat(condition.getValues()).hasSize(1).containsExactly(1);
    }

    @Test
    void changeValue() throws Exception {
        condition.setValue(new Values<>(2));

        assertThat(condition.getValues()).hasSize(1).containsExactly(2);
    }
}