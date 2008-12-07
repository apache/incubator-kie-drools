package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class ProcessWorkItemTest extends TestCase {
    
    public void testWorkItem() {
    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"UserName\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>John Doe</value>\n" +
    		"      </variable>\n" +
    		"      <variable name=\"MyObject\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"java.lang.Object\" />\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <workItem id=\"2\" name=\"HumanTask\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "        <parameter name=\"ActorId\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>#{UserName}</value>\n" +
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
            "        <parameter name=\"Attachment\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"java.lang.Object\" />\n" +
            "        </parameter>\n" +
            "      </work>\n" +
            "      <mapping type=\"in\" from=\"MyObject\" to=\"Attachment\" />" +
            "      <mapping type=\"out\" from=\"Result\" to=\"MyObject\" />" +
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
        kbuilder.add( ResourceFactory.newReaderResource( source ), ResourceType.DRF );
        
        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kpkgs );        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    	
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("org.drools.actions");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("John Doe", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("UserName", "Jane Doe");
        parameters.put("MyObject", "SomeString");
        processInstance = ksession.startProcess("org.drools.actions", parameters);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Jane Doe", workItem.getParameter("ActorId"));
        assertEquals("SomeString", workItem.getParameter("Attachment"));
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "SomeOtherString");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        VariableScopeInstance variableScope = (VariableScopeInstance)
        	((org.drools.process.instance.ProcessInstance) processInstance)
        		.getContextInstance(VariableScope.VARIABLE_SCOPE);
        assertEquals("SomeOtherString", variableScope.getVariable("MyObject"));
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
