package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;

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
     		"      <variable name=\"Person\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"org.drools.Person\" />\n" +
    		"      </variable>\n" +
    		"      <variable name=\"MyObject\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" />\n" +
    		"      </variable>\n" +
    		"      <variable name=\"Number\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.IntegerDataType\" />\n" +
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
            "        <parameter name=\"Content\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>#{Person.name}</value>\n" +
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
            "      <mapping type=\"in\" from=\"Person.name\" to=\"Comment\" />" +
            "      <mapping type=\"out\" from=\"Result\" to=\"MyObject\" />" +
            "      <mapping type=\"out\" from=\"Result.length()\" to=\"Number\" />" +
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
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("UserName", "John Doe");
        Person person = new Person();
        person.setName("John Doe");
        parameters.put("Person", person);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
        	ksession.startProcess("org.drools.actions", parameters);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("John Doe", workItem.getParameter("ActorId"));
        assertEquals("John Doe", workItem.getParameter("Content"));
        assertEquals("John Doe", workItem.getParameter("Comment"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
        parameters = new HashMap<String, Object>();
        parameters.put("UserName", "Jane Doe");
        parameters.put("MyObject", "SomeString");
        person = new Person();
        person.setName("Jane Doe");
        parameters.put("Person", person);
        processInstance = (WorkflowProcessInstance)
        	ksession.startProcess("org.drools.actions", parameters);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Jane Doe", workItem.getParameter("ActorId"));
        assertEquals("SomeString", workItem.getParameter("Attachment"));
        assertEquals("Jane Doe", workItem.getParameter("Content"));
        assertEquals("Jane Doe", workItem.getParameter("Comment"));
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "SomeOtherString");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals("SomeOtherString", processInstance.getVariable("MyObject"));
        assertEquals(15, processInstance.getVariable("Number"));
    }
    
    public void testWorkItemImmediateCompletion() {
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
     		"      <variable name=\"Person\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"org.drools.Person\" />\n" +
    		"      </variable>\n" +
    		"      <variable name=\"MyObject\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"java.lang.Object\" />\n" +
    		"      </variable>\n" +
    		"      <variable name=\"Number\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.IntegerDataType\" />\n" +
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
            "        <parameter name=\"Content\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>#{Person.name}</value>\n" +
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
            "      <mapping type=\"in\" from=\"Person.name\" to=\"Comment\" />" +
            "      <mapping type=\"out\" from=\"Result\" to=\"MyObject\" />" +
            "      <mapping type=\"out\" from=\"Result.length()\" to=\"Number\" />" +
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
    	
        ImmediateTestWorkItemHandler handler = new ImmediateTestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("UserName", "John Doe");
        Person person = new Person();
        person.setName("John Doe");
        parameters.put("Person", person);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
        	ksession.startProcess("org.drools.actions", parameters);
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
    
    private static class ImmediateTestWorkItemHandler implements WorkItemHandler {
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            manager.completeWorkItem(workItem.getId(), null);
        }
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
    }
}
