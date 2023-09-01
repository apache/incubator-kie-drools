package org.drools.verifier.core.checks;

import java.util.Arrays;
import java.util.HashSet;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.cache.inspectors.RuleInspectorDumper;
import org.drools.verifier.core.checks.base.PairCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;

public class DetectConflictingRowsCheck
        extends PairCheck {

    public DetectConflictingRowsCheck(final RuleInspector ruleInspector,
                                      final RuleInspector other,
                                      final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              other,
              configuration);
    }

    @Override
    protected CheckType getCheckType() {
        return CheckType.CONFLICTING_ROWS;
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.WARNING;
    }

    @Override
    public boolean check() {
        return hasIssues = ruleInspector.conflicts(other);
    }

    @Override
    protected Issue makeIssue(final Severity severity,
                              final CheckType checkType) {
        final Issue issue = new Issue(severity,
                                      checkType,
                                      new HashSet<>(Arrays.asList(ruleInspector.getRowIndex() + 1,
                                                                  other.getRowIndex() + 1))
        );

        issue.setDebugMessage(new RuleInspectorDumper(ruleInspector).dump() + " ## " + new RuleInspectorDumper(other).dump());

        return issue;
    }
}
