package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Package;

public class ProcessExceptionHandlerTest extends TestCase {
    
    public void testFaultWithoutHandler() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <fault id=\"2\" name=\"Fault\" faultName=\"myFault\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        PackageBuilderErrors errors = builder.getErrors();
        if (errors != null && !errors.isEmpty()) {
	        for (DroolsError error: errors.getErrors()) {
	        	System.err.println(error);
	        }
	        fail("Package could not be compiled");
        }
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.exception");
        assertEquals(ProcessInstance.STATE_ABORTED, processInstance.getState());
    }
    
    public void testProcessExceptionHandlerAction() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "    <variables>\n" +
            "      <variable name=\"SomeVar\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        <value>SomeValue</value>\n" +
            "      </variable>\n" +
            "      <variable name=\"faultVar\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
			"    <exceptionHandlers>\n" +
			"      <exceptionHandler faultName=\"myFault\" type=\"action\" faultVariable=\"faultVar\" >\n" +
			"        <action type=\"expression\" name=\"Print\" dialect=\"java\" >list.add(context.getVariable(\"faultVar\"));</action>\n" +
			"      </exceptionHandler>\n" +
			"    </exceptionHandlers>\n" +
			"  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <fault id=\"2\" name=\"Fault\" faultName=\"myFault\" faultVariable=\"SomeVar\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<String> list = new ArrayList<String>();
        workingMemory.setGlobal("list", list);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.exception");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(1, list.size());
        assertEquals("SomeValue", list.get(0));
    }
    
    public void testProcessExceptionHandlerTriggerNode() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"    <exceptionHandlers>\n" +
			"      <exceptionHandler faultName=\"myFault\" type=\"action\"  >\n" +
			"        <action type=\"expression\" name=\"Complete\" dialect=\"java\" >((org.drools.process.instance.ProcessInstance) context.getProcessInstance()).setState(org.drools.process.instance.ProcessInstance.STATE_COMPLETED);</action>\n" +
			"      </exceptionHandler>\n" +
			"    </exceptionHandlers>\n" +
			"  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <fault id=\"2\" name=\"Fault\" faultName=\"myFault\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.exception");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testCompositeNodeExceptionHandlerTriggerNode() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <composite id=\"2\" name=\"Composite\" >\n" +
            "      <variables>\n" +
            "        <variable name=\"SomeVar\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>SomeValue</value>\n" +
            "        </variable>\n" +
            "        <variable name=\"FaultVariable\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </variable>\n" +
            "      </variables>\n" +
    		"      <exceptionHandlers>\n" +
    		"        <exceptionHandler faultName=\"MyFault\" type=\"action\" faultVariable=\"FaultVariable\" >\n" +
    		"          <action type=\"expression\" name=\"Trigger\" dialect=\"java\" >context.getProcessInstance().signalEvent(\"MyEvent\", null);</action>\n" +
    		"        </exceptionHandler>\n" +
    		"      </exceptionHandlers>\n" +
            "      <nodes>\n" +
            "        <fault id=\"1\" name=\"Fault\" faultName=\"MyFault\" faultVariable=\"SomeVar\" />\n" +
            "        <eventNode id=\"2\" name=\"Event\" >\n" +
            "          <eventFilters>\n" +
            "            <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
            "          </eventFilters>\n" +
            "        </eventNode>\n" +
            "        <actionNode id=\"3\" name=\"Action\" >\n" +
            "          <action type=\"expression\" dialect=\"java\" >list.add(context.getVariable(\"FaultVariable\"));</action>\n" +
            "        </actionNode>\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"2\" to=\"3\" />\n" +
    		"      </connections>\n" +
    		"      <in-ports>\n" +
    		"        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
    		"      </in-ports>\n" +
    		"      <out-ports>\n" +
    		"        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"3\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
    		"      </out-ports>\n" +
    		"    </composite>\n" +
            "    <end id=\"3\" name=\"End\" />" +
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
        List<String> list = new ArrayList<String>();
        workingMemory.setGlobal("list", list);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.exception");
        assertEquals(1, list.size());
        assertEquals("SomeValue", list.get(0));
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testNestedExceptionHandler() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.exception\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
			"    <exceptionHandlers>\n" +
			"      <exceptionHandler faultName=\"otherFault\" type=\"action\" >\n" +
			"        <action type=\"expression\" name=\"Print\" dialect=\"java\" >list.add(\"Triggered global exception scope\");</action>\n" +
			"      </exceptionHandler>\n" +
			"    </exceptionHandlers>\n" +
			"  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <composite id=\"2\" name=\"Composite\" >\n" +
            "      <variables>\n" +
            "        <variable name=\"SomeVar\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>SomeValue</value>\n" +
            "        </variable>\n" +
            "        <variable name=\"FaultVariable\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        </variable>\n" +
            "      </variables>\n" +
    		"      <exceptionHandlers>\n" +
    		"        <exceptionHandler faultName=\"MyFault\" type=\"action\" faultVariable=\"FaultVariable\" >\n" +
    		"          <action type=\"expression\" name=\"Trigger\" dialect=\"java\" >((org.drools.workflow.instance.node.CompositeNodeInstance) context.getNodeInstance()).signalEvent(\"MyEvent\", null);</action>\n" +
    		"        </exceptionHandler>\n" +
    		"      </exceptionHandlers>\n" +
            "      <nodes>\n" +
            "        <fault id=\"1\" name=\"Fault\" faultName=\"MyFault\" faultVariable=\"SomeVar\" />\n" +
            "        <eventNode id=\"2\" name=\"Event\" >\n" +
            "          <eventFilters>\n" +
            "            <eventFilter type=\"eventType\" eventType=\"MyEvent\" />\n" +
            "          </eventFilters>\n" +
            "        </eventNode>\n" +
            "        <fault id=\"3\" name=\"Fault\" faultName=\"otherFault\" />\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"2\" to=\"3\" />\n" +
    		"      </connections>\n" +
    		"      <in-ports>\n" +
    		"        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
    		"      </in-ports>\n" +
    		"    </composite>\n" +
            "    <end id=\"3\" name=\"End\" />" +
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
        List<String> list = new ArrayList<String>();
        workingMemory.setGlobal("list", list);
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.exception");
        assertEquals(1, list.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
    }
    
}
