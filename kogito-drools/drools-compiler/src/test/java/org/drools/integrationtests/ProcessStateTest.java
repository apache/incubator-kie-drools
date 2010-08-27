package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.workflow.instance.node.StateNodeInstance;

public class ProcessStateTest extends TestCase {
    
    public void testManualSignalState() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" name=\"StateA\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" name=\"toB\" />\n" +
             "       <constraint toNodeId=\"4\" name=\"toC\" />\n" +
            "      </constraints>\n" +
            "    </state>\n" +
            "    <state id=\"3\" name=\"StateB\" />\n" +
            "    <state id=\"4\" name=\"StateC\" />\n" +
            "    <end id=\"5\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"2\" to=\"4\" />\n" +
            "    <connection from=\"3\" to=\"2\" />\n" +
            "    <connection from=\"4\" to=\"5\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        // start process
        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance)
            workingMemory.startProcess("org.drools.state");
        // should be in state A
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        assertEquals(1, nodeInstances.size());
        StateNodeInstance stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertEquals("StateA", stateInstance.getNodeName());
        // signal "toB" so we move to state B
        processInstance.signalEvent("signal", "toB");
        nodeInstances = processInstance.getNodeInstances();
        assertEquals(1, nodeInstances.size());
        stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertEquals("StateB", stateInstance.getNodeName());
        // if no constraint specified for a connection,
        // we default to the name of the target node
        // signal "StateA", so we move back to state A
        processInstance.signalEvent("signal", "StateA");
        nodeInstances = processInstance.getNodeInstances();
        assertEquals(1, nodeInstances.size());
        stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertEquals("StateA", stateInstance.getNodeName());
        // signal "toC" so we move to state C
        processInstance.signalEvent("signal", "toC");
        nodeInstances = processInstance.getNodeInstances();
        assertEquals(1, nodeInstances.size());
        stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertEquals("StateC", stateInstance.getNodeName());
        // signal something completely wrong, this should simply be ignored
        processInstance.signalEvent("signal", "Invalid");
        nodeInstances = processInstance.getNodeInstances();
        assertEquals(1, nodeInstances.size());
        stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertEquals("StateC", stateInstance.getNodeName());
        // signal "End", so we move to the end
        processInstance.signalEvent("signal", "End");
        nodeInstances = processInstance.getNodeInstances();
        assertEquals(0, nodeInstances.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    public void testImmediateStateConstraint1() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" name=\"one\" >\n" +
            "            eval(true)" +
            "        </constraint>"+
             "       <constraint toNodeId=\"4\" name=\"two\" >\n" +
            "           eval(false)" +
            "        </constraint>"+
            "      </constraints>\n" +
            "    </state>\n" +
			"    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
			"    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"6\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "    <connection from=\"2\" to=\"5\" />\n" +
            "    <connection from=\"5\" to=\"6\" />\n" +
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
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.state");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
        assertEquals("1", list.get(0));
    }
    
    public void testImmediateStateConstraintPriorities1() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" name=\"one\" priority=\"1\" >\n" +
            "            eval(true)" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" name=\"two\" priority=\"2\" >\n" +
            "           eval(true)" +
            "        </constraint>"+
            "      </constraints>\n" +
            "    </state>\n" +
			"    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
			"    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"6\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "    <connection from=\"2\" to=\"5\" />\n" +
            "    <connection from=\"5\" to=\"6\" />\n" +
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
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.state");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
        assertEquals("1", list.get(0));
    }
    
    public void testImmediateStateConstraintPriorities2() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" name=\"one\" priority=\"2\" >\n" +
            "            eval(true)" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" name=\"two\" priority=\"1\" >\n" +
            "           eval(true)" +
            "        </constraint>"+
            "      </constraints>\n" +
            "    </state>\n" +
			"    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
			"    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"6\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "    <connection from=\"2\" to=\"5\" />\n" +
            "    <connection from=\"5\" to=\"6\" />\n" +
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
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.state");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
        assertEquals("2", list.get(0));
    }
    
    public void testDelayedStateConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Person\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" name=\"one\" >\n" +
            "            Person( age &gt; 21 )" +
            "        </constraint>"+
             "       <constraint toNodeId=\"4\" name=\"two\" >\n" +
            "           Person( age &lt;= 21 )" +
            "        </constraint>"+
            "      </constraints>\n" +
            "    </state>\n" +
			"    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
			"    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"6\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "    <connection from=\"2\" to=\"5\" />\n" +
            "    <connection from=\"5\" to=\"6\" />\n" +
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
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.state");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertTrue(list.isEmpty());
        Person person = new Person("John Doe", 30);
        workingMemory.insert(person);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
        assertEquals("1", list.get(0));
    }
    
    public void testDelayedStateConstraint2() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Person\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" name=\"age &gt; 21\" >\n" +
            "            Person( age &gt; 21 )" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" name=\"age &lt;=21 \" >\n" +
            "           Person( age &lt;= 21 )" +
            "        </constraint>"+
            "      </constraints>\n" +
            "    </state>\n" +
			"    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
			"    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"6\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "    <connection from=\"2\" to=\"5\" />\n" +
            "    <connection from=\"5\" to=\"6\" />\n" +
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
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.state");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertTrue(list.isEmpty());
        Person person = new Person("John Doe", 20);
        workingMemory.insert(person);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
        assertEquals("2", list.get(0));
    }
    
    public void FIXMEtestDelayedStateConstraintPriorities1() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Person\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" name=\"one\" priority=\"1\" >\n" +
            "            Person( )" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" name=\"two\" priority=\"2\" >\n" +
            "           Person( )" +
            "        </constraint>"+
            "      </constraints>\n" +
            "    </state>\n" +
			"    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
			"    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"6\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "    <connection from=\"2\" to=\"5\" />\n" +
            "    <connection from=\"5\" to=\"6\" />\n" +
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
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.state");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertTrue(list.isEmpty());
        Person person = new Person("John Doe", 30);
        workingMemory.insert(person);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
        assertEquals("1", list.get(0));
    }
    
    public void FIXMEtestDelayedStateConstraintPriorities2() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Person\" />\n" +
			"    </imports>\n" +
			"    <globals>\n" +
			"      <global identifier=\"list\" type=\"java.util.List\" />\n" +
			"    </globals>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" name=\"one\" priority=\"2\" >\n" +
            "            Person( )" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" name=\"two\" priority=\"1\" >\n" +
            "           Person( )" +
            "        </constraint>"+
            "      </constraints>\n" +
            "    </state>\n" +
			"    <actionNode id=\"3\" name=\"ActionNode1\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"1\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
			"    <actionNode id=\"5\" name=\"ActionNode2\" >\n" +
			"      <action type=\"expression\" dialect=\"java\" >list.add(\"2\");</action>\n" +
			"    </actionNode>\n" +
            "    <end id=\"6\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "    <connection from=\"3\" to=\"4\" />\n" +
            "    <connection from=\"2\" to=\"5\" />\n" +
            "    <connection from=\"5\" to=\"6\" />\n" +
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
        ProcessInstance processInstance = workingMemory.startProcess("org.drools.state");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertTrue(list.isEmpty());
        Person person = new Person("John Doe", 30);
        workingMemory.insert(person);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
        assertEquals("2", list.get(0));
    }
    
    public void testActionState() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "    <variables>\n" +
            "      <variable name=\"s\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        <value>a</value>\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" name=\"State\" >\n" +
            "      <onEntry>" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(\"Action1\" + s);</action>\n" +
            "        <action type=\"expression\" dialect=\"java\" >list.add(\"Action2\" + s);</action>\n" +
            "      </onEntry>\n" +
            "      <onExit>\n" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(\"Action3\" + s);</action>\n" +
            "        <action type=\"expression\" dialect=\"java\" >list.add(\"Action4\" + s);</action>\n" +
            "      </onExit>\n" +
            "    </state>\n" +
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
        List<String> list = new ArrayList<String>();
        workingMemory.setGlobal("list", list);
        // start process
        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance)
            workingMemory.startProcess("org.drools.state");
        // should be in state A
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        assertEquals(1, nodeInstances.size());
        StateNodeInstance stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertEquals("State", stateInstance.getNodeName());
        assertEquals(2, list.size());
        assertTrue(list.contains("Action1a"));
        assertTrue(list.contains("Action2a"));
        processInstance.signalEvent("signal", "End");
        nodeInstances = processInstance.getNodeInstances();
        assertEquals(0, nodeInstances.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(4, list.size());
        assertTrue(list.contains("Action3a"));
        assertTrue(list.contains("Action4a"));
    }
    
    public void testTimerState() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.drools.state\" package-name=\"org.drools\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "    <variables>\n" +
            "      <variable name=\"s\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        <value>a</value>\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <state id=\"2\" name=\"State\" >\n" +
            "      <timers>\n" +
            "        <timer id=\"1\" delay=\"1s\" period=\"2s\" >\n" +
            "          <action type=\"expression\" dialect=\"mvel\" >list.add(\"Timer1\" + s);</action>\n" +
            "        </timer>\n" +
            "        <timer id=\"2\" delay=\"1s\" period=\"2s\" >\n" +
            "          <action type=\"expression\" dialect=\"mvel\" >list.add(\"Timer2\" + s);</action>\n" +
            "        </timer>\n" +
            "      </timers>\n" +
            "    </state>\n" +
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
        final StatefulSession workingMemory = ruleBase.newStatefulSession();
        List<String> list = new ArrayList<String>();
        workingMemory.setGlobal("list", list);
        new Thread(new Runnable() {
			public void run() {
				workingMemory.fireUntilHalt();
			}
        }).start();
        // start process
        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance)
            workingMemory.startProcess("org.drools.state");
        // should be in state A
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Collection<NodeInstance> nodeInstances = processInstance.getNodeInstances();
        assertEquals(1, nodeInstances.size());
        StateNodeInstance stateInstance = (StateNodeInstance) nodeInstances.iterator().next();
        assertEquals("State", stateInstance.getNodeName());
        assertEquals(0, list.size());
        try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}
        assertEquals(4, list.size());
        assertTrue(list.contains("Timer1a"));
        assertTrue(list.contains("Timer2a"));
        processInstance.signalEvent("signal", "End");
        nodeInstances = processInstance.getNodeInstances();
        assertEquals(0, nodeInstances.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(4, list.size());
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
        assertEquals(4, list.size());
        workingMemory.halt();
    }
    
}
