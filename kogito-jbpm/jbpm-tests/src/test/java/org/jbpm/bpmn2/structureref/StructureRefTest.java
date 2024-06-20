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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.flow.BooleanStructureRefModel;
import org.jbpm.bpmn2.flow.BooleanStructureRefProcess;
import org.jbpm.bpmn2.flow.DefaultObjectStructureRefModel;
import org.jbpm.bpmn2.flow.DefaultObjectStructureRefProcess;
import org.jbpm.bpmn2.flow.FloatStructureRefModel;
import org.jbpm.bpmn2.flow.FloatStructureRefProcess;
import org.jbpm.bpmn2.flow.IntegerStructureRefModel;
import org.jbpm.bpmn2.flow.IntegerStructureRefProcess;
import org.jbpm.bpmn2.flow.ObjectStructureRefModel;
import org.jbpm.bpmn2.flow.ObjectStructureRefProcess;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.core.datatype.impl.coverter.TypeConverterRegistry;
import org.jbpm.test.utils.ProcessTestHelper;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnVariables;

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
        Application app = ProcessTestHelper.newApplication();

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        org.kie.kogito.process.Process<BooleanStructureRefModel> definition = BooleanStructureRefProcess.newProcess(app);

        org.kie.kogito.process.ProcessInstance<BooleanStructureRefModel> instance = definition.createInstance(definition.createModel());
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        ProcessTestHelper.completeWorkItem(instance, "john", Collections.singletonMap("testHT", "true"));

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testIntegerStructureRef() throws Exception {
        String value = "25";

        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        IntegerStructureRefProcess definition = (IntegerStructureRefProcess) IntegerStructureRefProcess.newProcess(app);

        IntegerStructureRefModel model = definition.createModel();
        org.kie.kogito.process.ProcessInstance<IntegerStructureRefModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        ProcessTestHelper.completeWorkItem(instance, "john", Collections.singletonMap("testHT", value));

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance.variables().getTest()).isEqualTo(Integer.valueOf(value));

    }

    @Test
    public void testFloatStructureRef() throws Exception {
        String value = "5.5";

        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        FloatStructureRefProcess definition = (FloatStructureRefProcess) FloatStructureRefProcess.newProcess(app);

        FloatStructureRefModel model = definition.createModel();
        org.kie.kogito.process.ProcessInstance<FloatStructureRefModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        ProcessTestHelper.completeWorkItem(instance, "john", Collections.singletonMap("testHT", value));

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance.variables().getTest()).isEqualTo(Float.valueOf(value));

    }

    @Test
    public void testObjectStructureRef() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Person.class);

        TypeConverterRegistry.get().register("org.jbpm.bpmn2.objects.Person", (s) -> {
            try {
                return context.createUnmarshaller().unmarshal(new StringReader(s));
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        });

        String value = "<person><id>1</id><name>john</name></person>";

        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        ObjectStructureRefProcess definition = (ObjectStructureRefProcess) ObjectStructureRefProcess.newProcess(app);

        ObjectStructureRefModel model = definition.createModel();
        org.kie.kogito.process.ProcessInstance<ObjectStructureRefModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        ProcessTestHelper.completeWorkItem(instance, "john", Collections.singletonMap("testHT", value));

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance.variables().getTest()).isEqualTo(new Person(1, "john"));

    }

    @Test
    public void testDefaultObjectStructureRef() throws Exception {
        String value = "simple text for testing";

        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        DefaultObjectStructureRefProcess definition = (DefaultObjectStructureRefProcess) DefaultObjectStructureRefProcess.newProcess(app);

        DefaultObjectStructureRefModel model = definition.createModel();
        org.kie.kogito.process.ProcessInstance<DefaultObjectStructureRefModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        ProcessTestHelper.completeWorkItem(instance, "john", Collections.singletonMap("testHT", value));

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(instance.variables().getTest()).isEqualTo(value);

    }

    @Test
    public void testNotExistingVarBooleanStructureRefOnStart() throws Exception {
        Application app = ProcessTestHelper.newApplication();

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        org.kie.kogito.process.Process<BooleanStructureRefModel> definition = BooleanStructureRefProcess.newProcess(app);
        org.kie.kogito.Model model = BpmnVariables.create(Collections.singletonMap("not existing", "invalid boolean"));
        org.kie.kogito.process.ProcessInstance<? extends org.kie.kogito.Model> instance = definition.createInstance(model);
        assertThat(instance.variables().toMap()).doesNotContainKey("non existing");

    }

    @Test
    public void testInvalidBooleanStructureRefOnStart() throws Exception {
        Application app = ProcessTestHelper.newApplication();

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        org.kie.kogito.process.Process<BooleanStructureRefModel> definition = BooleanStructureRefProcess.newProcess(app);
        org.kie.kogito.Model model = BpmnVariables.create(Collections.singletonMap("test", "invalid boolean"));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            org.kie.kogito.process.ProcessInstance<? extends org.kie.kogito.Model> instance = definition.createInstance(model);
        });
    }

    @Test
    public void testInvalidBooleanStructureRefOnWIComplete() throws Exception {

        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        IntegerStructureRefProcess definition = (IntegerStructureRefProcess) IntegerStructureRefProcess.newProcess(app);

        IntegerStructureRefModel model = definition.createModel();
        org.kie.kogito.process.ProcessInstance<IntegerStructureRefModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        try {
            ProcessTestHelper.completeWorkItem(instance, "john", Collections.singletonMap("testHT", true));
            fail("");
        } catch (IllegalArgumentException iae) {
            logger.info("Expected IllegalArgumentException caught: " + iae);
        } catch (Exception e) {
            fail("");
        }

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testInvalidBooleanStructureRefOnStartVerifyErrorMsg() throws Exception {
        Application app = ProcessTestHelper.newApplication();

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        org.kie.kogito.process.Process<BooleanStructureRefModel> definition = BooleanStructureRefProcess.newProcess(app);
        org.kie.kogito.Model model = BpmnVariables.create(Collections.singletonMap("test", "invalid boolean"));

        try {
            definition.createInstance(model);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Can not set java.lang.Boolean field org.jbpm.bpmn2.flow.BooleanStructureRefModel.test to java.lang.String");
        }

    }

    @Test
    public void testNotExistingBooleanStructureRefOnWIComplete() throws Exception {
        String wrongDataOutput = "not existing";

        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", workItemHandler);
        IntegerStructureRefProcess definition = (IntegerStructureRefProcess) IntegerStructureRefProcess.newProcess(app);

        IntegerStructureRefModel model = definition.createModel();
        org.kie.kogito.process.ProcessInstance<IntegerStructureRefModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

        try {
            ProcessTestHelper.completeWorkItem(instance, "john", Collections.singletonMap(wrongDataOutput, true));
            fail("it should not work!");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Data output '" + wrongDataOutput + "' is not defined in process 'IntegerStructureRef' for task 'User Task'");
        }

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);

    }
}
