package org.drools.verifier.core.cache.inspectors;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.ObjectType;
import org.drools.verifier.core.index.model.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PatternInspectorTest {

    private AnalyzerConfigurationMock configurationMock;

    private PatternInspector a;
    private PatternInspector b;

    @BeforeEach
    public void setUp() throws Exception {
        configurationMock = new AnalyzerConfigurationMock();

        a = new PatternInspector(new Pattern("a",
                                             new ObjectType("org.Person",
                                                            configurationMock),
                                             configurationMock),
                                 mock(RuleInspectorUpdater.class),
                                 mock(AnalyzerConfiguration.class));
        b = new PatternInspector(new Pattern("b",
                                             new ObjectType("org.Person",
                                                            configurationMock),
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
        final PatternInspector x = new PatternInspector(new Pattern("x",
                        new ObjectType("org.Address",
                                configurationMock),
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
        final PatternInspector x = new PatternInspector(new Pattern("x",
                        new ObjectType("org.Address",
                                configurationMock),
                        configurationMock),
                mock(RuleInspectorUpdater.class),
                mock(AnalyzerConfiguration.class));

        assertThat(x.subsumes(b)).isFalse();
        assertThat(b.subsumes(x)).isFalse();
    }
}