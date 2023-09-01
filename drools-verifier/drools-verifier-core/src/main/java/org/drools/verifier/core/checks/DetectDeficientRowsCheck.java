package org.drools.verifier.core.checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.checks.base.OneToManyCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;

public class DetectDeficientRowsCheck
        extends OneToManyCheck {

    public DetectDeficientRowsCheck(final RuleInspector ruleInspector,
                                    final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              other -> !ruleInspector.getRule()
                      .getUuidKey()
                      .equals(other.getRule().getUuidKey()) && !other.isEmpty(),
              configuration,
              CheckType.DEFICIENT_ROW);
    }

    @Override
    public boolean check() {
        return hasIssues = !ruleInspector.isEmpty() &&
                ruleInspector.atLeastOneConditionHasAValue() &&
                thereIsAtLeastOneRow() &&
                isDeficient();
    }

    private boolean isDeficient() {
        return !getOtherRows().stream().anyMatch(other -> !isDeficient(other));
    }

    private boolean isDeficient(final RuleInspector other) {
        return ruleInspector.isDeficient(other);
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.WARNING;
    }

    @Override
    protected Issue makeIssue(final Severity severity,
                              final CheckType checkType) {
        return new Issue(severity,
                         checkType,
                         new HashSet<>(List.of(ruleInspector.getRowIndex() + 1))
        );
    }
}
