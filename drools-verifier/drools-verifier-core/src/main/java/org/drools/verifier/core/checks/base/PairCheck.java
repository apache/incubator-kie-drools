package org.drools.verifier.core.checks.base;

import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;

/**
 * A check that compares a row to another.
 */
public abstract class PairCheck
        extends CheckBase {

    protected final RuleInspector ruleInspector;
    protected final RuleInspector other;

    public PairCheck(final RuleInspector ruleInspector,
                     final RuleInspector other,
                     final AnalyzerConfiguration configuration) {
        super(configuration);

        this.ruleInspector = ruleInspector;
        this.other = other;
    }
}

