package org.drools.verifier.core.checks.base;

import java.util.function.Predicate;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.maps.InspectorList;

public abstract class OneToManyCheck
        extends SingleCheck {

    private final InspectorList<RuleInspector> ruleInspectors;
    private Predicate<RuleInspector> filter;

    public OneToManyCheck(final RuleInspector ruleInspector,
                          final Predicate<RuleInspector> filter,
                          final AnalyzerConfiguration configuration,
                          final CheckType checkType) {
        this(ruleInspector,
             configuration,
             checkType);
        this.filter = filter;
    }

    public OneToManyCheck(final RuleInspector ruleInspector,
                          final AnalyzerConfiguration configuration,
                          final CheckType checkType) {
        super(ruleInspector,
              configuration,
              checkType);
        ruleInspectors = new InspectorList<>(configuration);
    }

    protected boolean thereIsAtLeastOneRow() {
        return getOtherRows().size() >= 1;
    }

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    public InspectorList<RuleInspector> getOtherRows() {
        ruleInspectors.clear();
        ruleInspectors.addAll(ruleInspector.getCache().all(filter));
        return ruleInspectors;
    }
}
