package org.drools.verifier.core.checks;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.core.cache.inspectors.PatternInspector;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.cache.inspectors.action.ActionInspector;
import org.drools.verifier.core.checks.base.SingleCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.maps.InspectorMultiMap;
import org.drools.verifier.core.maps.util.RedundancyResult;

public abstract class DetectRedundantActionBase
        extends SingleCheck {

    protected PatternInspector patternInspector;

    protected RedundancyResult<ObjectField, ActionInspector> result;

    DetectRedundantActionBase(final RuleInspector ruleInspector,
                              final AnalyzerConfiguration configuration,
                              final CheckType checkType) {
        super(ruleInspector,
              configuration,
              checkType);
    }

    @Override
    public boolean check() {
        result = ruleInspector.getPatternsInspector().stream()
                .map(PatternInspector::getActionsInspector)
                .map(InspectorMultiMap::hasRedundancy)
                .filter(RedundancyResult::isTrue)
                .findFirst().orElse(null);

        return hasIssues = result != null;
    }
}
