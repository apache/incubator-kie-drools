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
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.api.reporting.ValueForActionIsSetTwiceIssue;
import org.drools.verifier.core.cache.inspectors.PatternInspector;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.maps.InspectorMultiMap;
import org.drools.verifier.core.maps.util.RedundancyResult;

public class DetectRedundantActionValueCheck
        extends DetectRedundantActionBase {

    public DetectRedundantActionValueCheck(final RuleInspector ruleInspector,
                                           final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              configuration,
              CheckType.VALUE_FOR_ACTION_IS_SET_TWICE);
    }

    @Override
    public boolean check() {
        result = ruleInspector.getPatternsInspector().stream()
                .filter(p -> p.getPattern().getBoundName() != null)
                .peek(p -> patternInspector = p)
                .map(PatternInspector::getActionsInspector)
                .map(InspectorMultiMap::hasRedundancy)
                .filter(RedundancyResult::isTrue)
                .findFirst().orElse(null);

        return hasIssues = result != null;
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.WARNING;
    }

    @Override
    protected List<Issue> makeIssues(final Severity severity,
                                     final CheckType checkType) {
        final ArrayList<Issue> result = new ArrayList<Issue>();
        result.add(new ValueForActionIsSetTwiceIssue(severity,
                                                     checkType,
                                                     this.result.get(0)
                                                             .toHumanReadableString(),
                                                     this.result.get(1)
                                                             .toHumanReadableString(),
                                                     new HashSet<>(Arrays.asList(ruleInspector.getRowIndex() + 1))));

        return result;
    }
}
