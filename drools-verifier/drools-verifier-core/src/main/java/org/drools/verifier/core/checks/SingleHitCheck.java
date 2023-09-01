package org.drools.verifier.core.checks;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.api.reporting.SingleHitLostIssue;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.checks.base.PairCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;

public class SingleHitCheck
        extends PairCheck {

    public SingleHitCheck(final RuleInspector ruleInspector,
                          final RuleInspector other,
                          final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              other,
              configuration);
    }

    @Override
    protected CheckType getCheckType() {
        return CheckType.SINGLE_HIT_LOST;
    }

    @Override
    public boolean check() {
        return hasIssues =
                ruleInspector.getRule().getActivationTime().overlaps(other.getRule().getActivationTime())
                        && ruleInspector.getConditionsInspectors().subsumes(other.getConditionsInspectors())
                        && ruleInspector.getBrlConditionsInspectors().subsumes(other.getBrlConditionsInspectors
                        ());
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.NOTE;
    }

    @Override
    protected Issue makeIssue(final Severity severity,
                              final CheckType checkType) {
        return new SingleHitLostIssue(severity,
                                      checkType,
                                      Integer.toString(ruleInspector.getRowIndex() + 1),
                                      Integer.toString(other.getRowIndex() + 1));
    }
}
