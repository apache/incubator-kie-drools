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

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.api.reporting.SingleHitLostIssue;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.checks.base.PairCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;

public class SingleHitCheck
        extends PairCheck {

    public SingleHitCheck(final RuleInspector ruleInspector,
                          final RuleInspector other,
                          final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              other,
              configuration);
    }

    @Override
    protected CheckType getCheckType() {
        return CheckType.SINGLE_HIT_LOST;
    }

    @Override
    public boolean check() {
        return hasIssues =
                ruleInspector.getRule().getActivationTime().overlaps(other.getRule().getActivationTime())
                        && ruleInspector.getConditionsInspectors().subsumes(other.getConditionsInspectors())
                        && ruleInspector.getBrlConditionsInspectors().subsumes(other.getBrlConditionsInspectors
                        ());
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.NOTE;
    }

    @Override
    protected Issue makeIssue(final Severity severity,
                              final CheckType checkType) {
        return new SingleHitLostIssue(severity,
                                      checkType,
                                      Integer.toString(ruleInspector.getRowIndex() + 1),
                                      Integer.toString(other.getRowIndex() + 1));
    }
}
