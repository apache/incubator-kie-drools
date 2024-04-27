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
package org.jbpm.bpmn2.structureref;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.coverter.TypeConverterRegistry;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

public class StructureRefTest extends JbpmBpmn2TestCase {

    @Test
    public void testStringStructureRef() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-StringStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("StructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<>();
        res.put("testHT", "test value");
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getStringId(), res);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testBooleanStructureRef() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BooleanStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("StructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<>();
        res.put("testHT", "true");
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getStringId(), res);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testIntegerStructureRef() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntegerStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("StructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<>();
        res.put("testHT", "25");
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getStringId(), res);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testFloatStructureRef() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-FloatStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("StructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<>();
        res.put("testHT", "5.5");
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getStringId(), res);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testObjectStructureRef() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Person.class);
        String personAsXml = "<person><id>1</id><name>john</name></person>";
        TypeConverterRegistry.get().register("org.jbpm.bpmn2.objects.Person", (s) -> {
            try {
                return context.createUnmarshaller().unmarshal(new StringReader(s));
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        });
        kruntime = createKogitoProcessRuntime("BPMN2-ObjectStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("StructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<>();
        res.put("testHT", personAsXml);
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getStringId(), res);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testDefaultObjectStructureRef() throws Exception {

        String value = "simple text for testing";

        kruntime = createKogitoProcessRuntime("BPMN2-DefaultObjectStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("StructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<>();
        res.put("testHT", value);
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getStringId(), res);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

    @Test
    public void testNotExistingVarBooleanStructureRefOnStart() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BooleanStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("not existing", "invalid boolean");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> kruntime.startProcess("StructureRef", params));

    }

    @Test
    public void testInvalidBooleanStructureRefOnStart() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BooleanStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("test", "invalid boolean");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> kruntime.startProcess("StructureRef", params));
    }

    @Test
    public void testInvalidBooleanStructureRefOnWIComplete() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntegerStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        KogitoProcessInstance processInstance = kruntime.startProcess("StructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<>();
        res.put("testHT", true);

        try {
            kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), res);
            fail("");
        } catch (IllegalArgumentException iae) {
            logger.info("Expected IllegalArgumentException caught: " + iae);
        } catch (Exception e) {
            fail("");
        }

    }

    @Test
    public void testInvalidBooleanStructureRefOnStartVerifyErrorMsg() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BooleanStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("test", "invalid boolean");
            kruntime.startProcess("StructureRef", params);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Variable 'test' has incorrect data type expected:java.lang.Boolean actual:java.lang.String");
        }

    }

    @Test
    public void testInvalidBooleanStructureRefOnStartWithDisabledCheck() throws Exception {
        // Temporarily disable check for variables strict that is enabled by default for tests
        VariableScope.setVariableStrictOption(false);
        kruntime = createKogitoProcessRuntime("BPMN2-BooleanStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("test", "invalid boolean");
        kruntime.startProcess("StructureRef", params);
        // enable it back for other tests
        VariableScope.setVariableStrictOption(true);
    }

    @Test
    public void testNotExistingBooleanStructureRefOnWIComplete() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-IntegerStructureRef.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        KogitoProcessInstance processInstance = kruntime.startProcess("StructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        String wrongDataOutput = "not existing";

        Map<String, Object> res = new HashMap<>();
        res.put(wrongDataOutput, true);

        try {
            kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), res);
            fail("");
        } catch (IllegalArgumentException iae) {
            System.out.println("Expected IllegalArgumentException catched: " + iae);
            assertThat(iae.getMessage()).isEqualTo("Data output '" + wrongDataOutput + "' is not defined in process 'StructureRef' for task 'User Task'");
        } catch (Exception e) {
            fail("");
        }

    }
}
