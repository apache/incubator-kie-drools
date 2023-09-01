package org.drools.verifier.core.index;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Conditions;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.index.select.AllListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class ConditionsListenerTest {

    private Conditions conditions;
    private AllListener allListener;
    private AnalyzerConfigurationMock configuration;

    @BeforeEach
    public void setUp() throws Exception {
        configuration = new AnalyzerConfigurationMock();

        conditions = new Conditions();

        allListener = mock(AllListener.class);
        conditions
                .where(Condition.value()
                               .any())
                .listen()
                .all(allListener);
    }

    @Test
    void testListen() throws Exception {
        conditions.add(new FieldCondition<>(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        configuration),
                new Column(1, configuration),
                "==",
                new Values<>(10),
                configuration));

        verify(allListener).onAllChanged(anyCollection());
    }

    @Test
    void testUpdate() throws Exception {
        final Condition condition = new FieldCondition<>(new Field(mock(ObjectField.class),
                        "Person",
                        "String",
                        "name",
                        configuration),
                new Column(1, configuration),
                "==",
                new Values<>(10),
                configuration);
        conditions.add(condition);

        reset(allListener);

        condition.setValue(new Values<>(20));

        verify(allListener).onAllChanged(anyCollection());
    }
}