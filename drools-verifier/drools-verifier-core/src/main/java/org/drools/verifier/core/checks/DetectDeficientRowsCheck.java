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

import java.util.Collections;
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
    protected List<Issue> makeIssues(final Severity severity,
                                     final CheckType checkType) {
        return Collections.singletonList(
                new Issue(severity,
                          checkType,
                          new HashSet<>(Collections.singleton(ruleInspector.getRowIndex() + 1))
                )
        );
    }
}
