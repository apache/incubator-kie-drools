package org.drools.verifier.core.index.query;

import java.util.Collection;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Columns;
import org.drools.verifier.core.index.model.ObjectType;
import org.drools.verifier.core.index.model.ObjectTypes;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.model.Rules;
import org.drools.verifier.core.index.select.QueryCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class QueryableIndexTest {

    private QueryableIndex queryableIndex;

    @Mock
    private QueryCallback<Collection<Rule>> rulesQueryCallback;

    @Mock
    private QueryCallback<Column> firstColumnQueryCallback;

    @Mock
    private QueryCallback<ObjectType> objectTypeQueryCallback;

    @Captor
    private ArgumentCaptor<Collection<Rule>> rulesArgumentCaptor;

    @Captor
    private ArgumentCaptor<Column> firstColumnArgumentCaptor;

    @Captor
    private ArgumentCaptor<ObjectType> objectTypeArgumentCaptor;

    private AnalyzerConfiguration configuration;
    private Column firstColumn;

    @BeforeEach
    public void setUp() throws Exception {
        configuration = new AnalyzerConfigurationMock();

        final Rules rules = new Rules();
        rules.add(new Rule(0, configuration));
        rules.add(new Rule(1, configuration));
        rules.add(new Rule(2, configuration));

        final Columns columns = new Columns();
        firstColumn = new Column(0, configuration);
        columns.add(firstColumn);
        columns.add(new Column(1, configuration));

        final ObjectTypes objectTypes = new ObjectTypes();
        objectTypes.add(new ObjectType("Person", configuration));
        objectTypes.add(new ObjectType("Address", configuration));

        queryableIndex = new QueryableIndex(rules, columns, objectTypes);
    }

    @Test
    void queryAllRules() throws Exception {

        queryableIndex.getRules()
                .where(Rule.index()
                        .any())
                .select()
                .all(rulesQueryCallback);

        verify(rulesQueryCallback).callback(rulesArgumentCaptor.capture());

        assertThat(rulesArgumentCaptor.getValue()).hasSize(3);
    }

    @Test
    void queryFirstColumn() throws Exception {

        queryableIndex.getColumns()
                .where(Column.index()
                        .any())
                .select()
                .first(firstColumnQueryCallback);

        verify(firstColumnQueryCallback).callback(firstColumnArgumentCaptor.capture());

        assertThat(firstColumnArgumentCaptor.getValue()).isEqualTo(firstColumn);
    }

    @Test
    void makeSureFirstAndLastObjectTypesAreTheSame() throws Exception {

        queryableIndex.getObjectTypes()
                .where(ObjectType.type()
                        .is("Person"))
                .select()
                .first(objectTypeQueryCallback);

        verify(objectTypeQueryCallback).callback(objectTypeArgumentCaptor.capture());

        final ObjectType first = objectTypeArgumentCaptor.getValue();

        reset(objectTypeQueryCallback);

        queryableIndex.getObjectTypes()
                .where(ObjectType.type()
                        .is("Person"))
                .select()
                .last(objectTypeQueryCallback);

        verify(objectTypeQueryCallback).callback(objectTypeArgumentCaptor.capture());

        final ObjectType last = objectTypeArgumentCaptor.getValue();

        assertThat(first.getType()).isEqualTo("Person");
        assertThat(last).isEqualTo(first);
    }
}