/*
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
package org.kie.kogito.serverless.workflow;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.kie.kogito.serverless.workflow.WorkflowTestUtils.assertProcessMainParams;

class SwitchStateServerlessWorkflowParsingTest extends AbstractServerlessWorkflowParsingTest {

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-data-condition-transition.sw.json", "/exec/switch-state-data-condition-transition.sw.yml" })
    void switchStateDataConditionTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_data_condition_transition",
                "Switch State Data Condition Transition Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-data-condition-end.sw.json", "/exec/switch-state-data-condition-end.sw.yml" })
    void switchStateDataConditionEnd(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_data_condition_end",
                "Switch State Data Condition End Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-event-condition-timeouts-end.sw.json", "/exec/switch-state-event-condition-timeouts-end.sw.yml" })
    void switchStateEventConditionTimeoutsEnd(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_event_condition_timeouts_end",
                "Switch State Event Condition Timeouts End Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-event-condition-timeouts-transition.sw.json", "/exec/switch-state-event-condition-timeouts-transition.sw.yml" })
    void switchStateEventConditionTimeoutsTransition(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_event_condition_timeouts_transition",
                "Switch State Event Condition Timeouts Transition Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);

    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/switch-state-event-condition-timeouts-transition2.sw.json", "/exec/switch-state-event-condition-timeouts-transition2.sw.yml" })
    void switchStateEventConditionTimeoutsTransition2(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        assertProcessMainParams(process,
                "switch_state_event_condition_timeouts_transition2",
                "Switch State Event Condition Timeouts Transition2 Test",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);
    }
}
