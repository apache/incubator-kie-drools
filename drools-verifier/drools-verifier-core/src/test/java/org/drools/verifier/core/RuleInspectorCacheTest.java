package org.drools.verifier.core;


import org.drools.verifier.core.cache.RuleInspectorCache;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.IndexImpl;
import org.drools.verifier.core.index.model.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RuleInspectorCacheTest {

    private RuleInspectorCache cache;

    @BeforeEach
    public void setUp() throws Exception {
        final Index index = new IndexImpl();
        final AnalyzerConfiguration configuration = new AnalyzerConfigurationMock();

        cache = new RuleInspectorCache(index, configuration);

        cache.addRule(new Rule(0, configuration));
        cache.addRule(new Rule(1, configuration));
        cache.addRule(new Rule(2, configuration));
        cache.addRule(new Rule(3, configuration));
        cache.addRule(new Rule(4, configuration));
        cache.addRule(new Rule(5, configuration));
        cache.addRule(new Rule(6, configuration));
    }

    @Test
    void testInit() throws Exception {
        assertThat(cache.all()).hasSize(7);
    }

    @Test
    void testRemoveRow() throws Exception {
        cache.removeRow(3);

        assertThat(cache.all()).hasSize(6).extracting(ri -> ri.getRowIndex()).containsExactlyInAnyOrder(0, 1, 2, 3, 4, 5);

    }
}