package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.workflow.instance.node.DynamicNodeInstance;
import org.drools.workflow.instance.node.DynamicUtils;

public class ProcessDynamicNodeTest extends TestCase {
    
    public void TODOtestDynamicActions() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.dynamic\" package-name=\"org.drools\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" + 
            "    <dynamic id=\"2\" name=\"DynamicNode\" >\n" +
            "      <nodes>\n" +
            "        <actionNode id=\"1\" name=\"Action1\" >\n" +
            "          <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Action1\");\n" +
            "list.add(\"Action1\");</action>\n" +
            "        </actionNode>\n" +
            "        <actionNode id=\"2\" name=\"Action2\" >\n" +
            "          <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Action2\");\n" +
            "list.add(\"Action2\");</action>\n" +
            "        </actionNode>\n" +
            "        <actionNode id=\"3\" name=\"Action3\" >\n" +
            "          <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Action3\");\n" +
            "list.add(\"Action3\");</action>\n" +
            "        </actionNode>\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"1\" to=\"3\" />\n" +
            "      </connections>\n" +
            "      <in-ports/>\n" +
            "      <out-ports/>\n" +
            "    </dynamic>\n" +
            "    <actionNode id=\"3\" name=\"Action4\" >\n" +
            "      <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Action4\");\n" +
            "list.add(\"Action4\");</action>\n" +
            "    </actionNode>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "  </connections>\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        for (DroolsError error: builder.getErrors().getErrors()) {
        	System.err.println(error);
        }
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<String> list = new ArrayList<String>();
        workingMemory.setGlobal("list", list);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.dynamic");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(4, list.size());
    }

    public void TODOtestDynamicAsyncActions() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.dynamic\" package-name=\"org.drools\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" + 
            "    <dynamic id=\"2\" name=\"DynamicNode\" >\n" +
            "      <nodes>\n" +
            "        <workItem id=\"1\" name=\"Work\" >\n" +
            "          <work name=\"Work\" />\n" +
            "        </workItem>\n" +
            "        <actionNode id=\"2\" name=\"Action2\" >\n" +
            "          <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Action2\");\n" +
            "list.add(\"Action2\");</action>\n" +
            "        </actionNode>\n" +
            "        <actionNode id=\"3\" name=\"Action3\" >\n" +
            "          <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Action3\");\n" +
            "list.add(\"Action3\");</action>\n" +
            "        </actionNode>\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"1\" to=\"3\" />\n" +
            "      </connections>\n" +
            "      <in-ports/>\n" +
            "      <out-ports/>\n" +
            "    </dynamic>\n" +
            "    <actionNode id=\"3\" name=\"Action4\" >\n" +
            "      <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Action4\");\n" +
            "list.add(\"Action4\");</action>\n" +
            "    </actionNode>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "  </connections>\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        for (DroolsError error: builder.getErrors().getErrors()) {
        	System.err.println(error);
        }
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<String> list = new ArrayList<String>();
        workingMemory.setGlobal("list", list);
        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        workingMemory.getWorkItemManager().registerWorkItemHandler("Work", testHandler);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.dynamic");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(1, list.size());
        WorkItem workItem = testHandler.getWorkItem(); 
        assertNotNull(workItem);
        workingMemory.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(3, list.size());
    }
    
    public void testAddDynamicWorkItem() {
    	Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.dynamic\" package-name=\"org.drools\" >\n" +
                "\n" +
                "  <header>\n" +
                "  </header>\n" +
                "\n" +
                "  <nodes>\n" +
                "    <start id=\"1\" name=\"Start\" />\n" + 
                "    <dynamic id=\"2\" name=\"DynamicNode\" >\n" +
                "      <nodes>\n" +
                "        <actionNode id=\"1\" name=\"Action\" >\n" +
                "          <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Action\");</action>\n" +
                "        </actionNode>\n" +
                "      </nodes>\n" +
                "      <connections>\n" +
                "      </connections>\n" +
                "      <in-ports/>\n" +
                "      <out-ports/>\n" +
                "    </dynamic>\n" +
                "    <end id=\"3\" name=\"End\" />\n" +
                "  </nodes>\n" +
                "\n" +
                "  <connections>\n" +
                "    <connection from=\"1\" to=\"2\" />\n" +
                "    <connection from=\"2\" to=\"3\" />\n" +
                "  </connections>\n" +
                "</process>");
    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
		TestWorkItemHandler handler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
		// start a new process instance
		ProcessInstance processInstance = (ProcessInstance) ksession.startProcess("org.drools.dynamic");
		DynamicNodeInstance dynamicContext = (DynamicNodeInstance) 
			((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("TaskName", "Dynamic Task");
		assertNull(handler.getWorkItem());
		assertEquals(0, dynamicContext.getNodeInstances().size());
		DynamicUtils.addDynamicWorkItem(dynamicContext, ksession, "Human Task", parameters);
		assertNotNull(handler.getWorkItem());
		assertEquals(1, dynamicContext.getNodeInstances().size());
		logger.close();
    }

    public void testAddDynamicSubProcess() {
    	Reader source = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.dynamic\" package-name=\"org.drools\" >\n" +
                "\n" +
                "  <header>\n" +
                "  </header>\n" +
                "\n" +
                "  <nodes>\n" +
                "    <start id=\"1\" name=\"Start\" />\n" + 
                "    <dynamic id=\"2\" name=\"DynamicNode\" >\n" +
                "      <nodes>\n" +
                "        <actionNode id=\"1\" name=\"Action\" >\n" +
                "          <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Action\");</action>\n" +
                "        </actionNode>\n" +
                "      </nodes>\n" +
                "      <connections>\n" +
                "      </connections>\n" +
                "      <in-ports/>\n" +
                "      <out-ports/>\n" +
                "    </dynamic>\n" +
                "    <end id=\"3\" name=\"End\" />\n" +
                "  </nodes>\n" +
                "\n" +
                "  <connections>\n" +
                "    <connection from=\"1\" to=\"2\" />\n" +
                "    <connection from=\"2\" to=\"3\" />\n" +
                "  </connections>\n" +
                "</process>");
    	Reader source2 = new StringReader(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
                "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
                "         type=\"RuleFlow\" name=\"subflow\" id=\"org.drools.subflow\" package-name=\"org.drools\" >\n" +
                "\n" +
                "  <header>\n" +
        		"    <variables>\n" +
        		"      <variable name=\"x\" >\n" +
        		"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
        		"        <value>SomeText</value>\n" +
        		"      </variable>\n" +
        		"    </variables>\n" +
                "  </header>\n" +
                "\n" +
                "  <nodes>\n" +
                "    <start id=\"1\" name=\"Start\" />\n" + 
                "    <actionNode id=\"2\" name=\"Action\" >\n" +
                "      <action type=\"expression\" dialect=\"mvel\" >System.out.println(x);</action>\n" +
                "    </actionNode>\n" +
                "    <workItem id=\"3\" name=\"Work\" >\n" +
	            "      <work name=\"Human Task\" />\n" +
	            "    </workItem>\n" +
	            "    <end id=\"4\" name=\"End\" />\n" +
                "  </nodes>\n" +
                "\n" +
                "  <connections>\n" +
                "    <connection from=\"1\" to=\"2\" />\n" +
                "    <connection from=\"2\" to=\"3\" />\n" +
                "    <connection from=\"3\" to=\"4\" />\n" +
                "  </connections>\n" +
                "</process>");
    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newReaderResource(source), ResourceType.DRF);
		kbuilder.add(ResourceFactory.newReaderResource(source2), ResourceType.DRF);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
		TestWorkItemHandler handler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
		// start a new process instance
		ProcessInstance processInstance = (ProcessInstance) ksession.startProcess("org.drools.dynamic");
		DynamicNodeInstance dynamicContext = (DynamicNodeInstance) 
			((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("x", "NewValue");
		assertNull(handler.getWorkItem());
		assertEquals(0, dynamicContext.getNodeInstances().size());
		DynamicUtils.addDynamicSubProcess(dynamicContext, ksession, "org.drools.subflow", parameters);
		assertNotNull(handler.getWorkItem());
		assertEquals(1, dynamicContext.getNodeInstances().size());
		logger.close();
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
