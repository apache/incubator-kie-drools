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
package org.drools.verifier.core.cache.inspectors;

import org.drools.verifier.core.cache.inspectors.action.ActionInspector;
import org.drools.verifier.core.cache.inspectors.action.ActionsInspectorMultiMap;
import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.relations.HumanReadable;

/**
 * This class is for debugging purposes. It is way easier to just dump the inspector into a string than compare the
 * values in the super dev mode debugger.
 */
public class RuleInspectorDumper {

    private StringBuilder dump = new StringBuilder();

    private RuleInspector ruleInspector;

    public RuleInspectorDumper(final RuleInspector ruleInspector) {
        this.ruleInspector = ruleInspector;
    }

    public String dump() {

        dump.append("Rule: ");
        dump.append(ruleInspector.getRowIndex());
        dump.append("\n");
        dump.append("\n");

        dumpPatterns();

        dumpConditions();

        dumpActions();

        return dump.toString();
    }

    private void dumpPatterns() {
        dump.append("Patterns{\n");
        InspectorList<PatternInspector> patternsInspector = ruleInspector.getPatternsInspector();
        for (final PatternInspector patternInspector : patternsInspector) {
            dump.append("Pattern{\n");
            dump.append(patternInspector.getPattern().getName());
            dump.append("\n");
            dump.append("Conditions{\n");
            dumpCondition(patternInspector.getConditionsInspector());
            dump.append("}\n");
            dump.append("Actions{\n");
            dumpAction(patternInspector.getActionsInspector());
            dump.append("}\n");
        }
        dump.append("}\n");
    }

    private void dumpConditions() {
        dump.append("Conditions{\n");
        for (final ConditionsInspectorMultiMap conditionsInspectorMultiMap : ruleInspector.getConditionsInspectors()) {
            dumpCondition(conditionsInspectorMultiMap);
        }

        for (final ConditionInspector conditionInspector : ruleInspector.getBrlConditionsInspectors()) {
            dump.append(conditionInspector.toHumanReadableString());
        }

        dump.append("}\n");
    }

    private void dumpActions() {

        dump.append("Actions{\n");

        for (final ActionsInspectorMultiMap actionsInspectorMultiMap : ruleInspector.getActionsInspectors()) {
            dumpAction(actionsInspectorMultiMap);
        }

        for (final ActionInspector actionInspector : ruleInspector.getBrlActionInspectors()) {
            dump.append(actionInspector.toHumanReadableString());
        }

        dump.append("}\n");
    }

    private void dumpAction(final ActionsInspectorMultiMap actionsInspectorMultiMap) {
        for (final Object object : actionsInspectorMultiMap.allValues()) {
            dump.append("Action{\n");
            if (object instanceof HumanReadable) {
                dump.append(((HumanReadable) object).toHumanReadableString());
            } else {
                dump.append(object.toString());
            }
            dump.append("}\n");
        }
    }

    private void dumpCondition(final ConditionsInspectorMultiMap conditionsInspectorMultiMap) {
        for (final ConditionInspector conditionInspector : conditionsInspectorMultiMap.allValues()) {
            dump.append("Condition{\n");
            dump.append(conditionInspector.toHumanReadableString());
            dump.append("}\n");
        }
    }
}
