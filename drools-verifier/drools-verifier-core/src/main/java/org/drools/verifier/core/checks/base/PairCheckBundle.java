package org.drools.verifier.core.checks.base;

import java.util.List;

import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.util.PortablePreconditions;

public class PairCheckBundle
        extends PriorityListCheck {

    protected final RuleInspector ruleInspector;
    protected final RuleInspector other;

    public PairCheckBundle(final RuleInspector ruleInspector,
                           final RuleInspector other,
                           final List<Check> filteredSet) {
        super(filteredSet);

        this.ruleInspector = PortablePreconditions.checkNotNull("ruleInspector",
                                                                ruleInspector);
        this.other = PortablePreconditions.checkNotNull("other",
                                                        other);
    }

    public RuleInspector getRuleInspector() {
        return ruleInspector;
    }

    public RuleInspector getOther() {
        return other;
    }
}
