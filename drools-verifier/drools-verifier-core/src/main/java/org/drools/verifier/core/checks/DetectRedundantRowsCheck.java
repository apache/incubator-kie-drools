package org.drools.verifier.core.checks;

import java.util.Arrays;
import java.util.HashSet;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.checks.base.PairCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.configuration.CheckConfiguration;

public class DetectRedundantRowsCheck
        extends PairCheck {

    private CheckType issueType = null;

    private boolean allowRedundancyReporting = true;
    private boolean allowSubsumptionReporting = true;

    public DetectRedundantRowsCheck(final RuleInspector ruleInspector,
                                    final RuleInspector other,
                                    final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              other,
              configuration);
    }

    @Override
    protected Issue makeIssue(final Severity severity,
                              final CheckType checkType) {
        return new Issue(severity,
                         checkType,
                         new HashSet<>(Arrays.asList(ruleInspector.getRowIndex() + 1,
                                                     other.getRowIndex() + 1))
        );
    }

    @Override
    public boolean isActive(final CheckConfiguration checkConfiguration) {

        allowRedundancyReporting = checkConfiguration.getCheckConfiguration()
                .contains(CheckType.REDUNDANT_ROWS);

        allowSubsumptionReporting = checkConfiguration.getCheckConfiguration()
                .contains(CheckType.SUBSUMPTANT_ROWS);

        return allowRedundancyReporting || allowSubsumptionReporting;
    }

    @Override
    protected CheckType getCheckType() {
        return issueType;
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.WARNING;
    }

    @Override
    public boolean check() {
        if (other.atLeastOneActionHasAValue() && ruleInspector.subsumes(other)) {
            if (allowRedundancyReporting && other.subsumes(ruleInspector)) {
                issueType = CheckType.REDUNDANT_ROWS;
                return hasIssues = true;
            } else if (allowSubsumptionReporting) {
                issueType = CheckType.SUBSUMPTANT_ROWS;
                return hasIssues = true;
            }
        }

        return hasIssues = false;
    }
}
