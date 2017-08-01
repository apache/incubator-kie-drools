/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.remote.ejb.test.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.remote.ejb.test.mock.WebService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.VariableDesc;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;

/**
 * Testing Async mode for Web service WIH does not make sense with EJBs - spawning own threads violates
 * EJB specification. Asynchronous continuation is advised to be used instead.
 * */
public class EWebServiceTaskTest extends RemoteEjbTest {

    private final static String MODE_SYNC = "SYNC";
    private final static String MODE_ONE_WAY = "ONEWAY";

    private final static String EXPECTED_MESSAGE = "Echo message";
    private final static String RESULT_VARIABLE = "result";

    @BeforeClass
    public static void startWebService() {
        WebService.start();
    }

    @AfterClass
    public static void stopWebService() {
        WebService.stop();
    }

    @Test(timeout = 10000L)
    public void testSyncWebServiceWorkItem() {
        long pid = ejb.startProcess(ProcessDefinitions.WEB_SERVICE_WORK_ITEM, getParameters(MODE_SYNC));
        sleep(2000);

        List<VariableDesc> history = ejb.getVariableHistory(pid, RESULT_VARIABLE);
        assertResult(history);
        assertProcessCompleted(pid);
    }

    @Test(timeout = 10000L)
    public void testOneWayWebServiceWorkItem() {
        long pid = ejb.startProcess(ProcessDefinitions.WEB_SERVICE_WORK_ITEM, getParameters(MODE_ONE_WAY));
        sleep(2000);

        List<VariableDesc> history = ejb.getVariableHistory(pid, RESULT_VARIABLE);
        Assertions.assertThat(history).isNullOrEmpty();
        assertProcessCompleted(pid);
    }

    private Map<String, Object> getParameters(String mode) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("url", WebService.URL + "?wsdl");
        parameters.put("namespace", WebService.WS_NAMESPACE);
        parameters.put("interface", WebService.WS_SERVICE_NAME);
        parameters.put("operation", "echo");
        parameters.put("mode", mode);
        parameters.put("parameters", new String[]{EXPECTED_MESSAGE});
        return parameters;
    }

    private void assertResult(final List<VariableDesc> history) {
        Assertions.assertThat(history).isNotNull().isNotEmpty();
        String result = history.get(history.size() - 1).getNewValue();
        Assertions.assertThat(result).isNotNull().isEqualTo(EXPECTED_MESSAGE);
    }

    private void assertProcessCompleted(long pid) {
        ProcessInstanceDesc log = ejb.getProcessInstanceById(pid);
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getState().intValue()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}
