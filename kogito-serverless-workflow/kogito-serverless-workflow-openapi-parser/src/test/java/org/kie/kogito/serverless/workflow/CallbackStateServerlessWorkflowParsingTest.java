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

class CallbackStateServerlessWorkflowParsingTest extends AbstractServerlessWorkflowParsingTest {

    @ParameterizedTest
    @ValueSource(strings = { "/exec/callback-state.sw.json", "/exec/callback-state.sw.yml" })
    void produceCallbackState(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        // assert the process main parameters
        assertProcessMainParams(process,
                "callback_state",
                "Callback State",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "/exec/callback-state-timeouts.sw.json", "/exec/callback-state-timeouts.sw.yml" })
    void produceCallbackStateWithTimeouts(String workflowLocation) throws Exception {
        RuleFlowProcess process = (RuleFlowProcess) getWorkflowParser(workflowLocation);
        // assert the process main parameters
        assertProcessMainParams(process,
                "callback_state_timeouts",
                "Callback State Timeouts",
                "1.0",
                "org.kie.kogito.serverless",
                RuleFlowProcess.PUBLIC_VISIBILITY);
    }
}
