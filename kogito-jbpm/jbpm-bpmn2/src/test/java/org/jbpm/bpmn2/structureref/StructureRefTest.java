/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.bpmn2.structureref;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.NewJbpmBpmn2TestBase;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

public class StructureRefTest extends NewJbpmBpmn2TestBase {

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
    public void testNoStructureRef() {
        try {
            KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-NoStructureRef.bpmn2");
            fail("Structure ref must be defined for a process");
        } catch (Exception e ) {
            assertEquals("Exception about parsing errors missing.", "Errors while parsing knowledge base", e.getMessage());
        }
    }
}
