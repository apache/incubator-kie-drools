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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
    protected List<Issue> makeIssues(final Severity severity,
                                     final CheckType checkType) {

        final Issue issue = new Issue(severity,
                                      checkType,
                                      new HashSet<>(Arrays.asList(ruleInspector.getRowIndex() + 1,
                                                                  other.getRowIndex() + 1))
        );

        issue.setDebugMessage(new RuleInspectorDumper(ruleInspector).dump() + " ## " + new RuleInspectorDumper(other).dump());

        return Collections.singletonList(issue);
    }
}
