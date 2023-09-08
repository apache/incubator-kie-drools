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
