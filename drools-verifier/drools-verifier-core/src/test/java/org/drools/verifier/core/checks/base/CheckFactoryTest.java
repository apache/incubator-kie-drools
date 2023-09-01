package org.drools.verifier.core.checks.base;

import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CheckFactoryTest {

    @Test
    void emptyWhiteList() throws Exception {
        final AnalyzerConfigurationMock configuration = new AnalyzerConfigurationMock(CheckConfiguration.newEmpty());

        assertThat(new CheckFactory(configuration).makeSingleChecks(mock(RuleInspector.class))).isEmpty();
        assertThat(new CheckFactory(configuration).makePairRowCheck(mock(RuleInspector.class), mock(RuleInspector.class))).isNotPresent();
    }

    @Test
    void defaultWhiteList() throws Exception {
        final AnalyzerConfigurationMock configuration = new AnalyzerConfigurationMock(CheckConfiguration.newDefault());

        assertThat(new CheckFactory(configuration).makeSingleChecks(mock(RuleInspector.class))).isNotEmpty();
        assertThat(new CheckFactory(configuration).makePairRowCheck(mock(RuleInspector.class), mock(RuleInspector.class))).isPresent();
    }
}