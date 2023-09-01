package org.drools.verifier.core.checks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.MultipleValuesForOneActionIssue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.cache.inspectors.PatternInspector;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.checks.base.SingleCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.maps.InspectorMultiMap;
import org.drools.verifier.core.relations.Conflict;
import org.drools.verifier.core.relations.HumanReadable;

public class DetectMultipleValuesForOneActionCheck
        extends SingleCheck {

    private Conflict conflict = Conflict.EMPTY;

    public DetectMultipleValuesForOneActionCheck(final RuleInspector ruleInspector,
                                                 final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              configuration,
              CheckType.MULTIPLE_VALUES_FOR_ONE_ACTION);
    }

    @Override
    public boolean check() {
        conflict = ruleInspector.getPatternsInspector().stream()
                .map(PatternInspector::getActionsInspector)
                .map(InspectorMultiMap::hasConflicts)
                .filter(Conflict::foundIssue)
                .findFirst()
                .orElse(Conflict.EMPTY);

        return hasIssues = conflict != Conflict.EMPTY;
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.WARNING;
    }

    @Override
    protected Issue makeIssue(final Severity severity,
                              final CheckType checkType) {
        return new MultipleValuesForOneActionIssue(severity,
                                                   checkType,
                                                   HumanReadable.toHumanReadableString(conflict.getConflictedItem()),
                                                   HumanReadable.toHumanReadableString(conflict.getConflictingItem()),
                                                   new HashSet<>(List.of(ruleInspector.getRowIndex() + 1)));
    }
}
