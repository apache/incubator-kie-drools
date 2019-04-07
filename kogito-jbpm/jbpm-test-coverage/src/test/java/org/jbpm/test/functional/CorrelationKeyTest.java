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

package org.jbpm.test.functional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;

public class CorrelationKeyTest extends JbpmTestCase {

    private static final String PROCESS = "org.jbpm.test.functional.CorrelationKey";
    private static final String VARIABLE_ID = "procVar";
    private static final String VARIABLE_VALUE = "procVarValue";

    private static final String SIMPLE_KEY = "mySimpleCorrelationKey";
    private static final List<String> COMPOSED_KEY = Arrays.asList("firstPartOfKey", "secondPartOfKey");

    private CorrelationKeyFactory keyFactory;
    private CorrelationAwareProcessRuntime ksession;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        keyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        ksession = (CorrelationAwareProcessRuntime) createKSession("org/jbpm/test/functional/CorrelationKey.bpmn2");
    }

    @Test
    public void testSimpleKey() {
        CorrelationKey key = keyFactory.newCorrelationKey(SIMPLE_KEY);
        ProcessInstance processInstance = ksession.startProcess(PROCESS, key, null);
        assertProcessInstanceActive(processInstance.getId());

        CorrelationKey keyCopy = keyFactory.newCorrelationKey(SIMPLE_KEY);
        ProcessInstance processInstanceCopy = ksession.getProcessInstance(keyCopy);
        Assertions.assertThat(processInstanceCopy.getId()).isEqualTo(processInstance.getId());
    }

    @Test
    public void testMultiValuedKey() {
        CorrelationKey key = keyFactory.newCorrelationKey(COMPOSED_KEY);
        ProcessInstance processInstance = ksession.startProcess(PROCESS, key, null);
        assertProcessInstanceActive(processInstance.getId());

        CorrelationKey keyCopy = keyFactory.newCorrelationKey(COMPOSED_KEY);
        ProcessInstance processInstanceCopy = ksession.getProcessInstance(keyCopy);
        Assertions.assertThat(processInstanceCopy.getId()).isEqualTo(processInstance.getId());
    }

    @Test
    public void testNotUniqueSimpleKey() {
        CorrelationKey key = keyFactory.newCorrelationKey(SIMPLE_KEY);

        ProcessInstance processInstance = ksession.startProcess(PROCESS, key, null);
        assertProcessInstanceActive(processInstance.getId());

        try {
            ksession.startProcess(PROCESS, key, null);
            Assertions.fail("Not unique correlation key used. Exception should have been thrown.");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Assertions.assertThat(ex.getMessage()).contains("already exists");
        }
    }

    @Test
    public void testNotUniqueMultiValuedKey() {
        CorrelationKey key = keyFactory.newCorrelationKey(COMPOSED_KEY);

        ProcessInstance processInstance = ksession.startProcess(PROCESS, key, null);
        assertProcessInstanceActive(processInstance.getId());

        try {
            ksession.startProcess(PROCESS, key, null);
            Assertions.fail("Not unique correlation key used. Exception should have been thrown.");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Assertions.assertThat(ex.getMessage()).contains("already exists");
        }
    }

    @Test
    public void testGetNotExistingSimpleKey() {
        CorrelationKey key = keyFactory.newCorrelationKey(SIMPLE_KEY);
        ProcessInstance processInstance = ksession.getProcessInstance(key);
        Assertions.assertThat(processInstance).isNull();
    }

    @Test
    public void testGetNotExistingMultiValuedKey() {
        CorrelationKey key = keyFactory.newCorrelationKey(COMPOSED_KEY);
        ProcessInstance processInstance = ksession.getProcessInstance(key);
        Assertions.assertThat(processInstance).isNull();
    }

    @Test
    public void testStartProcessParametersPassing() {
        CorrelationKey key = keyFactory.newCorrelationKey(SIMPLE_KEY);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(VARIABLE_ID, VARIABLE_VALUE);

        ProcessInstance processInstance = ksession.startProcess(PROCESS, key, parameters);
        assertProcessInstanceActive(processInstance.getId());

        List<? extends VariableInstanceLog> variables = getLogService().findVariableInstances(processInstance.getId());
        Assertions.assertThat(variables).isNotEmpty();
        Assertions.assertThat(variables.get(0).getVariableId()).isEqualTo(VARIABLE_ID);
        Assertions.assertThat(variables.get(0).getValue()).isEqualTo(VARIABLE_VALUE);
    }

    @Test
    public void testCreateProcessInstanceParametersPassing() {
        CorrelationKey key = keyFactory.newCorrelationKey(SIMPLE_KEY);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(VARIABLE_ID, VARIABLE_VALUE);

        ProcessInstance processInstance = ksession.createProcessInstance(PROCESS, key, parameters);
        assertProcessInstanceNeverRun(processInstance.getId());

        List<? extends VariableInstanceLog> variables = getLogService().findVariableInstances(processInstance.getId());
        Assertions.assertThat(variables).isNotEmpty();
        Assertions.assertThat(variables.get(0).getVariableId()).isEqualTo(VARIABLE_ID);
        Assertions.assertThat(variables.get(0).getValue()).isEqualTo(VARIABLE_VALUE);
    }

    @Test
    public void testMultiValuedKeyUniqueButInclusive() {
        // JBPM-5897
        CorrelationKey key1 = keyFactory.newCorrelationKey(Arrays.asList("ABC", "DEF", "DEF"));
        ProcessInstance processInstance = ksession.startProcess(PROCESS, key1, null);
        assertProcessInstanceActive(processInstance.getId());

        CorrelationKey key2 = keyFactory.newCorrelationKey(Arrays.asList("ABC", "DEF", "GHI"));
        ProcessInstance processInstance2 = ksession.startProcess(PROCESS, key2, null);
        assertProcessInstanceActive(processInstance2.getId());
    }
}
