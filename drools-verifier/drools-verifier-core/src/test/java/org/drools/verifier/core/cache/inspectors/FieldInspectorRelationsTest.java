package org.drools.verifier.core.cache.inspectors;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.ObjectField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class FieldInspectorRelationsTest {

    private AnalyzerConfigurationMock configurationMock;

    private FieldInspector a;
    private FieldInspector b;

    @BeforeEach
    public void setUp() throws Exception {

        configurationMock = new AnalyzerConfigurationMock();

        ObjectField objectField = mock(ObjectField.class);
        a = new FieldInspector(new Field(objectField,
                                         "org.Person",
                                         "String",
                                         "name",
                                         configurationMock),
                               mock(RuleInspectorUpdater.class),
                               mock(AnalyzerConfiguration.class));
        b = new FieldInspector(new Field(objectField,
                                         "org.Person",
                                         "String",
                                         "name",
                                         configurationMock),
                               mock(RuleInspectorUpdater.class),
                               mock(AnalyzerConfiguration.class));
    }

    @Test
    void testRedundancy01() throws Exception {
        assertThat(a.isRedundant(b)).isTrue();
        assertThat(b.isRedundant(a)).isTrue();
    }

    @Test
    void testRedundancy02() throws Exception {
        final FieldInspector x = new FieldInspector(new Field(mock(ObjectField.class),
                        "org.Address",
                        "String",
                        "name",
                        configurationMock),
                mock(RuleInspectorUpdater.class),
                mock(AnalyzerConfiguration.class));

        assertThat(x.isRedundant(b)).isFalse();
        assertThat(b.isRedundant(x)).isFalse();
    }

    @Test
    void testSubsumpt01() throws Exception {
        assertThat(a.subsumes(b)).isTrue();
        assertThat(b.subsumes(a)).isTrue();
    }

    @Test
    void testSubsumpt02() throws Exception {
        final FieldInspector x = new FieldInspector(new Field(mock(ObjectField.class),
                        "org.Address",
                        "String",
                        "name",
                        configurationMock),
                mock(RuleInspectorUpdater.class),
                mock(AnalyzerConfiguration.class));

        assertThat(x.subsumes(b)).isFalse();
        assertThat(b.subsumes(x)).isFalse();
    }
}