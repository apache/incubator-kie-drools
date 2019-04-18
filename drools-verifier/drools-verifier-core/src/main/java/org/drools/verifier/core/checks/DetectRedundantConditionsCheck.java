/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.core.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.RedundantConditionsIssue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.cache.inspectors.ConditionMasterInspector;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.verifier.core.checks.base.SingleCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.meta.ConditionParentType;
import org.drools.verifier.core.maps.InspectorMultiMap;
import org.drools.verifier.core.maps.util.RedundancyResult;

public class DetectRedundantConditionsCheck
        extends SingleCheck {

    private RedundancyResult<ConditionParentType, ConditionInspector> result;

    public DetectRedundantConditionsCheck(final RuleInspector ruleInspector,
                                          final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              configuration,
              CheckType.REDUNDANT_CONDITIONS_TITLE);
    }

    @Override
    public boolean check() {
        result = ruleInspector.getPatternsInspector().stream()
                .map(ConditionMasterInspector::getConditionsInspector)
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
    protected List<Issue> makeIssues(final Severity severity,
                                     final CheckType checkType) {
        final ArrayList<Issue> issues = new ArrayList<>();
        issues.add(new RedundantConditionsIssue(severity,
                                                checkType,
                                                this.result.getParent()
                                                        .getName(),
                                                this.result.get(0)
                                                        .toHumanReadableString(),
                                                this.result.get(1)
                                                        .toHumanReadableString(),
                                                new HashSet<>(Arrays.asList(ruleInspector.getRowIndex() + 1))));

        return issues;
    }
}

