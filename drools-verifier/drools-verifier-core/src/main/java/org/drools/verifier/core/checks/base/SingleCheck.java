package org.drools.verifier.core.checks.base;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;

public abstract class SingleCheck
        extends CheckBase
        implements Comparable<SingleCheck> {

    protected final RuleInspector ruleInspector;
    private final CheckType checkType;

    public SingleCheck(final RuleInspector ruleInspector,
                       final AnalyzerConfiguration configuration,
                       final CheckType checkType) {
        super(configuration);
        this.ruleInspector = ruleInspector;
        this.checkType = checkType;
    }

    @Override
    protected CheckType getCheckType() {
        return checkType;
    }

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    @Override
    public int compareTo(final SingleCheck singleCheck) {
        return ruleInspector.getRowIndex() - singleCheck.getRuleInspector().getRowIndex();
    }
}
