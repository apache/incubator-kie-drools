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

import java.util.Collections;
import java.util.Optional;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.ImpossibleMatchIssue;
import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.api.reporting.Severity;
import org.drools.verifier.core.cache.inspectors.PatternInspector;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.cache.inspectors.condition.ComparableConditionInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.verifier.core.checks.base.SingleCheck;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.relations.Conflict;

import static org.drools.verifier.core.relations.HumanReadable.toHumanReadableString;

public class DetectImpossibleMatchCheck
        extends SingleCheck {

    private Conflict conflict = Conflict.EMPTY;

    public DetectImpossibleMatchCheck(final RuleInspector ruleInspector,
                                      final AnalyzerConfiguration configuration) {
        super(ruleInspector,
              configuration,
              CheckType.IMPOSSIBLE_MATCH);
    }

    @Override
    public boolean check() {
        conflict = ruleInspector.getPatternsInspector().stream()
                .map(PatternInspector::getConditionsInspector)
                .map(ConditionsInspectorMultiMap::hasConflicts)
                .filter(Conflict::foundIssue)
                .findFirst()
                .orElse(Conflict.EMPTY);

        return hasIssues = conflict != Conflict.EMPTY;
    }

    @Override
    protected Severity getDefaultSeverity() {
        return Severity.ERROR;
    }

    @Override
    protected Issue makeIssue(final Severity severity,
                              final CheckType checkType) {
        return new ImpossibleMatchIssue(severity,
                                        checkType,
                                        Integer.toString(ruleInspector.getRowIndex() + 1),
                                        getFactType(),
                                        getFieldName(),
                                        toHumanReadableString(conflict.getOrigin()
                                                                      .getConflictedItem()),
                                        toHumanReadableString(conflict.getOrigin()
                                                                      .getConflictingItem()),
                                        Collections.singleton(ruleInspector.getRowIndex() + 1));
    }

    private String getFieldName() {
        final Optional<Field> field = getField();
        if (field.isPresent()) {
            return field.get().getName();
        } else {
            return "";
        }
    }

    private String getFactType() {
        final Optional<Field> field = getField();
        if (field.isPresent()) {
            return field.get().getFactType();
        } else {
            return "";
        }
    }

    private Optional<Field> getField() {
        if (conflict.getOrigin()
                .getConflictedItem() instanceof ComparableConditionInspector) {

            final Field field = ((ComparableConditionInspector) conflict.getOrigin()
                    .getConflictedItem()).getField();

            return Optional.of(field);
        } else {
            return Optional.empty();
        }
    }
}
