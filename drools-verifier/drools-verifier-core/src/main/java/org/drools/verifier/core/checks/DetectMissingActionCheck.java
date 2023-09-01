package org.drools.verifier.core.checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.checks.base.SingleCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;

public class DetectMissingActionCheck
        extends SingleCheck {

    public DetectMissingActionCheck(final RuleInspector ruleInspector,
                                    final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              configuration,
              CheckType.MISSING_ACTION);
    }

    @Override
    public boolean check() {
        return hasIssues = ruleInspector.atLeastOneConditionHasAValue() && !ruleInspector.atLeastOneActionHasAValue();
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
