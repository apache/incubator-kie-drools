package org.drools.verifier.core.cache.inspectors;

import java.util.ArrayList;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Actions;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Conditions;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldAction;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FieldInspectorUpdateTest {

    @Mock
    ObjectField objectField;

    @Mock
    RuleInspectorUpdater ruleInspectorUpdater;
    private FieldCondition fieldCondition;
    private FieldAction fieldAction;

    private AnalyzerConfigurationMock configurationMock;

    @BeforeEach
    public void setUp() throws Exception {

        configurationMock = new AnalyzerConfigurationMock();

        final Field field = spy(new Field(objectField,
                                          "org.Person",
                                          "String",
                                          "name",
                                          configurationMock));

        fieldCondition = makeCondition(field);
        fieldAction = makeAction(field);

        new FieldInspector(field,
                           ruleInspectorUpdater,
                           new AnalyzerConfigurationMock());
    }

    private FieldCondition makeCondition(final Field field) {
        final FieldCondition fieldAction = new FieldCondition(field,
                                                              mock(Column.class),
                                                              "==",
                                                              new Values(11),
                                                              configurationMock);
        final ArrayList<Condition> actionsList = new ArrayList<>();
        actionsList.add(fieldAction);
        final Conditions conditions = new Conditions(actionsList);
        when(field.getConditions()).thenReturn(conditions);

        return fieldAction;
    }

    private FieldAction makeAction(final Field field) {
        final FieldAction fieldAction = new FieldAction(field,
                                                        mock(Column.class),
                                                        new Values(11),
                                                        configurationMock);
        final ArrayList<Action> actionsList = new ArrayList<>();
        actionsList.add(fieldAction);
        final Actions actions = new Actions(actionsList);
        when(field.getActions()).thenReturn(actions);

        return fieldAction;
    }

    @Test
    void updateAction() throws Exception {
        fieldAction.setValue(new Values(20));

        verify(ruleInspectorUpdater).resetActionsInspectors();
    }

    @Test
    void updateCondition() throws Exception {
        fieldCondition.setValue(new Values(20));

        verify(ruleInspectorUpdater).resetConditionsInspectors();
    }
}