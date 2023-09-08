/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
