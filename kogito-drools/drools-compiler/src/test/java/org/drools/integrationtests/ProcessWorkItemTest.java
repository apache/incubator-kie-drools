package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.rule.Package;

public class ProcessWorkItemTest extends TestCase {
    
    public void testOnEntryExit() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"UserName\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>John Doe</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <workItem id=\"2\" name=\"HumanTask\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "        <parameter name=\"ActorId\" type=\"org.drools.process.core.datatype.impl.type.StringDataType\" >#{UserName}</parameter>\n" +
            "        <parameter name=\"TaskName\" type=\"org.drools.process.core.datatype.impl.type.StringDataType\" >Do something</parameter>\n" +
            "        <parameter name=\"Priority\" type=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        <parameter name=\"Comment\" type=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </work>\n" +
            "    </workItem>\n" +
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
            workingMemory.startProcess("org.drools.actions");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("John Doe", workItem.getParameter("ActorId"));
        workingMemory.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("UserName", "Jane Doe");
        processInstance = workingMemory.startProcess("org.drools.actions", parameters);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
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
