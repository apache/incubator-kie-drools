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
package org.jbpm.bpmn2;

import org.jbpm.bpmn2.flow.BPMN2_LambdaExpressionParserModel;
import org.jbpm.bpmn2.flow.BPMN2_LambdaExpressionParserProcess;
import org.jbpm.test.utils.ProcessTestHelper;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;

import static org.assertj.core.api.Assertions.assertThat;

public class ScriptLambdaValidationTest extends JbpmBpmn2TestCase {

    @Test
    public void testLambdaScriptParser() {
        Application app = ProcessTestHelper.newApplication();

        org.kie.kogito.process.Process<BPMN2_LambdaExpressionParserModel> minimalProcess = BPMN2_LambdaExpressionParserProcess.newProcess(app);
        BPMN2_LambdaExpressionParserModel model = minimalProcess.createModel();
        org.kie.kogito.process.ProcessInstance<BPMN2_LambdaExpressionParserModel> instance = minimalProcess.createInstance(model);
        instance.start();
        var vars = instance.variables().toMap();
        var vp = (org.jbpm.bpmn2.objects.User) vars.get("validPerson");
        assertThat(vp).isNotNull();
        assertThat(vp.getName()).isEqualTo("TestUser");

    }
}
