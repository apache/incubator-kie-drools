package org.drools.verifier.core.index;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.ActionSuperType;
import org.drools.verifier.core.index.model.Column;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ActionTest {

    private Action action;

    @BeforeEach
    public void setUp() throws Exception {
        action = new Action(mock(Column.class),
                            ActionSuperType.FIELD_ACTION,
                            new Values<>(true),
                            new AnalyzerConfigurationMock()) {
        };
    }

    @Test
    void valueSet() throws Exception {
        assertThat(action.getValues()).hasSize(1).containsExactly(true);
    }

    @Test
    void changeValue() throws Exception {
        action.setValue(new Values<>(false));

        assertThat(action.getValues()).hasSize(1).containsExactly(false);
    }
}