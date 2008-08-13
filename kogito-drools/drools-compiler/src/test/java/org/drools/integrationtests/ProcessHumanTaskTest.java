package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class ProcessHumanTaskTest extends TestCase {
    
    public void testHumanTask() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.humantask\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <humanTask id=\"2\" name=\"HumanTask\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "        <parameter name=\"ActorId\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>John Doe</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"TaskName\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>Do something</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Priority\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Comment\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "      </work>\n" +
            "    </humanTask>\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        workingMemory.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance =
            workingMemory.startProcess("org.drools.humantask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        workingMemory.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testSwimlane() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.humantask\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <swimlanes>\n" +
            "      <swimlane name=\"actor1\" />\n" +
            "    </swimlanes>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <humanTask id=\"2\" name=\"HumanTask\" swimlane=\"actor1\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "        <parameter name=\"ActorId\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>John Doe</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"TaskName\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>Do something</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Priority\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Comment\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "      </work>\n" +
            "    </humanTask>\n" +
            "    <humanTask id=\"3\" name=\"HumanTask\" swimlane=\"actor1\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "        <parameter name=\"ActorId\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "        <parameter name=\"TaskName\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>Do something else</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Priority\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Comment\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </parameter>\n" +
            "      </work>\n" +
            "    </humanTask>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        workingMemory.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance =
            workingMemory.startProcess("org.drools.humantask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Do something", workItem.getParameter("TaskName"));
        assertEquals("John Doe", workItem.getParameter("ActorId"));
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("ActorId", "Jane Doe");
        workingMemory.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Do something else", workItem.getParameter("TaskName"));
        assertEquals("Jane Doe", workItem.getParameter("ActorId"));
        workingMemory.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    private static class TestWorkItemHandler implements WorkItemHandler {
        private WorkItem workItem;
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            this.workItem = workItem;
        }
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
        public WorkItem getWorkItem() {
            return workItem;
        }
    }
}
