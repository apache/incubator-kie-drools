package org.jbpm.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.io.impl.ReaderResource;
import org.jbpm.JbpmTestCase;
import org.jbpm.Message;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.runtime.ObjectFilter;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.rule.FactHandle;

public class ProcessActionTest extends JbpmTestCase {
    
    public void testOnEntryExit() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <workItem id=\"2\" name=\"HumanTask\" >\n" +
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
            "      <onEntry>\n" +
            "        <action type=\"expression\" name=\"Print\" dialect=\"mvel\" >list.add(\"Executing on entry action\");</action>\n" + 
            "      </onEntry>\n" + 
            "      <onExit>\n" +
            "        <action type=\"expression\" name=\"Print\" dialect=\"java\" >list.add(\"Executing on exit action1\");</action>\n" +
            "        <action type=\"expression\" name=\"Print\" dialect=\"java\" >list.add(\"Executing on exit action2\");</action>\n" +
            "      </onExit>\n" +
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
        kbuilder.add(new ReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance =
            ksession.startProcess("org.drools.actions");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        WorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);
        assertEquals(1, list.size());
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(3, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testActionContextJava() {
    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.jbpm.Message\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"variable\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>SomeText</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
			"    <actionNode id=\"2\" name=\"MyActionNode\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >System.out.println(\"Triggered\");\n" +
			"String myVariable = (String) kcontext.getVariable(\"variable\");\n" +
			"System.out.println(kcontext.getKnowledgeRuntime());\n" +
			"list.add(myVariable);\n" +
			"String nodeName = kcontext.getNodeInstance().getNodeName();\n" +
			"list.add(nodeName);\n" +
			"insert( new Message() );\n" +
			"</action>\n" +
			"    </actionNode>\n" + 
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        kbuilder.add(new ReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance =
            ksession.startProcess("org.drools.actions");
        assertEquals(2, list.size());
        assertEquals("SomeText", list.get(0));
        assertEquals("MyActionNode", list.get(1));
        Collection<FactHandle> factHandles = ksession.getFactHandles(new ObjectFilter() {
			public boolean accept(Object object) {
				return object instanceof Message;
			}
        });
        assertFalse(factHandles.isEmpty());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
	public void testActionContextMVEL() {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.jbpm.Message\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"variable\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>SomeText</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
			"    <actionNode id=\"2\" name=\"MyActionNode\" >\n" +
			"      <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Triggered\");\n" +
			"System.out.println(kcontext.getKnowledgeRuntime());\n" +
			"String myVariable = (String) kcontext.getVariable(\"variable\");\n" +
			"list.add(myVariable);\n" +
			"String nodeName = kcontext.getNodeInstance().getNodeName();\n" +
			"list.add(nodeName);\n" +
			"insert( new Message() );\n" +
			"</action>\n" +
			"    </actionNode>\n" + 
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        kbuilder.add(new ReaderResource(source), ResourceType.DRF);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance =
            ksession.startProcess("org.drools.actions");
        assertEquals(2, list.size());
        assertEquals("SomeText", list.get(0));
        assertEquals("MyActionNode", list.get(1));
        Collection<FactHandle> factHandles = ksession.getFactHandles(new ObjectFilter() {
			public boolean accept(Object object) {
				return object instanceof Message;
			}
        });
        assertFalse(factHandles.isEmpty());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

	public void testActionVariableJava() {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.jbpm.integrationtests.ProcessActionTest.TestVariable\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"person\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"org.jbpm.integrationtests.ProcessActionTest.TestVariable\" />\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
			"    <actionNode id=\"2\" name=\"MyActionNode\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >System.out.println(\"Triggered\");\n" +
			"list.add(person.getName());\n" +
			"</action>\n" +
			"    </actionNode>\n" + 
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        kbuilder.add(new ReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        TestVariable person = new TestVariable("John Doe");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", person);
        ProcessInstance processInstance =
            ksession.startProcess("org.drools.actions", params);
        assertEquals(1, list.size());
        assertEquals("John Doe", list.get(0));
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
	
	public void testActionVariableMVEL() {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.jbpm.integrationtests.ProcessActionTest.TestVariable\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"person\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"TestVariable\" />\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
			"    <actionNode id=\"2\" name=\"MyActionNode\" >\n" +
			"      <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Triggered\");\n" +
			"list.add(person.name);\n" +
			"</action>\n" +
			"    </actionNode>\n" + 
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        kbuilder.add(new ReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        TestVariable person = new TestVariable("John Doe");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", person);
        ProcessInstance processInstance =
            ksession.startProcess("org.drools.actions", params);
        assertEquals(1, list.size());
        assertEquals("John Doe", list.get(0));
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
	
    public void testActionNameConflict() {
    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions1\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
			"    <actionNode id=\"2\" name=\"MyActionNode\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"Action1\");</action>\n" +
			"    </actionNode>\n" + 
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        kbuilder.add(new ReaderResource(source), ResourceType.DRF);
        source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.actions2\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
			"    <actionNode id=\"2\" name=\"MyActionNode\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"Action2\");</action>\n" +
			"    </actionNode>\n" + 
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        kbuilder.add(new ReaderResource(source), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance =
            ksession.startProcess("org.drools.actions1");
        assertEquals(1, list.size());
        assertEquals("Action1", list.get(0));
        list.clear();
        processInstance =
        	ksession.startProcess("org.drools.actions2");
        assertEquals(1, list.size());
        assertEquals("Action2", list.get(0));
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
	
	public static class TestVariable {
		
		private String name;
		
		public TestVariable(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	
	}
}
