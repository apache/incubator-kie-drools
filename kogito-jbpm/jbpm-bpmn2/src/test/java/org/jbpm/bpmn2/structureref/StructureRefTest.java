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
package org.jbpm.bpmn2.structureref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.datatype.impl.coverter.TypeConverterRegistry;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import com.thoughtworks.xstream.XStream;

public class StructureRefTest extends JbpmBpmn2TestCase {

    @Test
    public void testStringStructureRef() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-StringStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("StructureRef");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("testHT", "test value");
        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), res);

        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testBooleanStructureRef() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-BooleanStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("StructureRef");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("testHT", "true");
        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), res);

        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testIntegerStructureRef() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntegerStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("StructureRef");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("testHT", "25");
        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), res);

        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testFloatStructureRef() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-FloatStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("StructureRef");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("testHT", "5.5");
        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), res);

        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testObjectStructureRef() throws Exception {

        String personAsXml = "<org.jbpm.bpmn2.objects.Person><id>1</id><name>john</name></org.jbpm.bpmn2.objects.Person>";
        TypeConverterRegistry.get().register("org.jbpm.bpmn2.objects.Person", (s) -> 
            new XStream().fromXML(personAsXml)
        );
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-ObjectStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("StructureRef");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("testHT", personAsXml);
        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), res);

        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testDefaultObjectStructureRef() throws Exception {

        String value = "simple text for testing";

        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DefaultObjectStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("StructureRef");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("testHT", value);
        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), res);

        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testNotExistingVarBooleanStructureRefOnStart() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-BooleanStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("not existing", "invalid boolean");
        assertThrows(IllegalArgumentException.class, () -> ksession.startProcess("StructureRef", params));

    }

    @Test
    public void testInvalidBooleanStructureRefOnStart() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-BooleanStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", "invalid boolean");
        assertThrows(IllegalArgumentException.class, () -> ksession.startProcess("StructureRef", params));
    }

    @Test
    public void testInvalidBooleanStructureRefOnWIComplete() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntegerStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        ProcessInstance processInstance = ksession.startProcess("StructureRef");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("testHT", true);

        try {
            ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), res);
            fail();
        }  catch (IllegalArgumentException iae) {
            logger.info("Expected IllegalArgumentException caught: " + iae);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testInvalidBooleanStructureRefOnStartVerifyErrorMsg() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-BooleanStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        try {
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("test", "invalid boolean");
	        ksession.startProcess("StructureRef", params);
        } catch (IllegalArgumentException e) {
        	assertEquals("Variable 'test' has incorrect data type expected:java.lang.Boolean actual:java.lang.String", e.getMessage());
        }

    }

    @Test
    public void testInvalidBooleanStructureRefOnStartWithDisabledCheck() throws Exception {
    	// Temporarily disable check for variables strict that is enabled by default for tests
    	VariableScope.setVariableStrictOption(false);
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-BooleanStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("test", "invalid boolean");
        ksession.startProcess("StructureRef", params);
        // enable it back for other tests
        VariableScope.setVariableStrictOption(true);

    }

    @Test
    public void testNotExistingBooleanStructureRefOnWIComplete() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntegerStructureRef.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        ProcessInstance processInstance = ksession.startProcess("StructureRef");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);

        String wrongDataOutput = "not existing";

        Map<String, Object> res = new HashMap<String, Object>();
        res.put(wrongDataOutput, true);

        try {
            ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), res);
            fail();
        }  catch (IllegalArgumentException iae) {
            System.out.println("Expected IllegalArgumentException catched: " + iae);
            assertEquals("Data output '"+ wrongDataOutput +"' is not defined in process 'StructureRef' for task 'User Task'", iae.getMessage());
        } catch (Exception e) {
            fail();
        }

    }
}
