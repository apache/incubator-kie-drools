package org.drools.verifier.core.checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.RedundantConditionsIssue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.cache.inspectors.PatternInspector;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.verifier.core.checks.base.SingleCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.maps.InspectorMultiMap;
import org.drools.verifier.core.maps.util.RedundancyResult;

public class DetectRedundantConditionsCheck
        extends SingleCheck {

    private RedundancyResult<ObjectField, ConditionInspector> result;

    public DetectRedundantConditionsCheck(final RuleInspector ruleInspector,
                                          final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              configuration,
              CheckType.REDUNDANT_CONDITIONS_TITLE);
    }

    @Override
    public boolean check() {
        result = ruleInspector.getPatternsInspector().stream()
                .map(PatternInspector::getConditionsInspector)
                .map(InspectorMultiMap::hasRedundancy)
                .filter(RedundancyResult::isTrue)
                .findFirst().orElse(null);

        return hasIssues = result != null;
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.NOTE;
    }

    @Override
    protected Issue makeIssue(final Severity severity,
                              final CheckType checkType) {
        return new RedundantConditionsIssue(severity,
                                            checkType,
                                            result.getParent()
                                                    .getFactType(),
                                            result.getParent()
                                                    .getName(),
                                            result.get(0)
                                                    .toHumanReadableString(),
                                            result.get(1)
                                                    .toHumanReadableString(),
                                            new HashSet<>(List.of(ruleInspector.getRowIndex() + 1)));
    }
}

