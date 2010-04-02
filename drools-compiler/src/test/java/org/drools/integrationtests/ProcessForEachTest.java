package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Package;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class ProcessForEachTest extends TestCase {
    
    
    public void testForEach() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"ForEach\" id=\"org.drools.ForEach\" package-name=\"org.drools\" >\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "    <variables>\n" +
            "      <variable name=\"collection\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <forEach id=\"2\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"collection\" >\n" +
            "      <nodes>\n" +
            "    <actionNode id=\"1\" name=\"Action\" >\n" +
            "        <action type=\"expression\" dialect=\"mvel\" >myList.add(item);</action>\n" +
            "    </actionNode>\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </forEach>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<String> myList = new ArrayList<String>();
        workingMemory.setGlobal("myList", myList);
        List<String> collection = new ArrayList<String>();
        collection.add("one");
        collection.add("two");
        collection.add("three");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", collection);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.ForEach", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(3, myList.size());
    }
    
    public void testForEachLargeList() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"ForEach\" id=\"org.drools.ForEach\" package-name=\"org.drools\" >\n" +
            "  <header>\n" +
            "    <variables>\n" +
            "      <variable name=\"collection\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <forEach id=\"2\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"collection\" >\n" +
            "      <nodes>\n" +
            "        <workItem id=\"1\" name=\"Log\" >\n" +
            "          <work name=\"Log\" >\n" +
            "            <parameter name=\"Message\" >\n" +
            "              <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "          </work>\n" +
            "          <mapping type=\"in\" from=\"item\" to=\"Message\" />" +
            "        </workItem>\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </forEach>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        final List<String> myList = new ArrayList<String>();
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        workingMemory.getWorkItemManager().registerWorkItemHandler("Log", new WorkItemHandler() {
			public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
				String message = (String) workItem.getParameter("Message");
//				System.out.println(message);
				myList.add(message);
				manager.completeWorkItem(workItem.getId(), null);
			}
			public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			}
        });
        List<String> collection = new ArrayList<String>();
        for (int i = 0; i < 10000; i++) {
        	collection.add(i + "");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", collection);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.ForEach", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(10000, myList.size());
    }
    
    public void testForEachCancel() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"ForEach\" id=\"org.drools.ForEach\" package-name=\"org.drools\" >\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "    <variables>\n" +
            "      <variable name=\"collection\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <forEach id=\"2\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"collection\" >\n" +
            "      <nodes>\n" +
			"    <subProcess id=\"1\" name=\"SubProcess\" processId=\"org.drools.subflow\" independent=\"false\" />\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </forEach>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "</process>");
        builder.addRuleFlow(source);
		source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.subflow\" package-name=\"org.drools\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Person\" />\n" +
			"    </imports>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <milestone id=\"2\" name=\"Event Wait\" >\n" +
            "      <constraint type=\"rule\" dialect=\"mvel\" >Person( )</constraint>" +
            "    </milestone>\n" +
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
        List<String> collection = new ArrayList<String>();
        collection.add("one");
        collection.add("two");
        collection.add("three");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", collection);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.ForEach", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(4, workingMemory.getProcessInstances().size());
        processInstance.setState(ProcessInstance.STATE_ABORTED);
        assertEquals(0, workingMemory.getProcessInstances().size());
    }
    
    public void testForEachCancelIndependent() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"ForEach\" id=\"org.drools.ForEach\" package-name=\"org.drools\" >\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"myList\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "    <variables>\n" +
            "      <variable name=\"collection\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <forEach id=\"2\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"collection\" >\n" +
            "      <nodes>\n" +
			"    <subProcess id=\"1\" name=\"SubProcess\" processId=\"org.drools.subflow\" />\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </forEach>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "</process>");
        builder.addRuleFlow(source);
		source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.subflow\" package-name=\"org.drools\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Person\" />\n" +
			"    </imports>\n" +
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <milestone id=\"2\" name=\"Event Wait\" >\n" +
            "      <constraint type=\"rule\" dialect=\"mvel\" >Person( )</constraint>" +
            "    </milestone>\n" +
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
        List<String> collection = new ArrayList<String>();
        collection.add("one");
        collection.add("two");
        collection.add("three");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("collection", collection);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.ForEach", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(4, workingMemory.getProcessInstances().size());
        processInstance.setState(ProcessInstance.STATE_ABORTED);
        assertEquals(3, workingMemory.getProcessInstances().size());
    }
    
}
