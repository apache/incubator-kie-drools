package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Package;

public class ProcessStateTest extends TestCase {
    
    public void testSignalState() {
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
            "    <state id=\"2\" name=\"State\" />\n" +
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
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.state");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        processInstance.signalEvent("signal", null);
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
            "    <state id=\"2\" dialect=\"mvel\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" toType=\"DROOLS_DEFAULT\" name=\"one\" type=\"rule\" dialect=\"mvel\" >\n" +
            "            eval(true)" +
            "        </constraint>"+
             "       <constraint toNodeId=\"4\" toType=\"DROOLS_DEFAULT\" name=\"two\" type=\"rule\" dialect=\"mvel\" >\n" +
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
            "    <state id=\"2\" dialect=\"mvel\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" toType=\"DROOLS_DEFAULT\" name=\"one\" priority=\"1\" type=\"rule\" dialect=\"mvel\" >\n" +
            "            eval(true)" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" toType=\"DROOLS_DEFAULT\" name=\"two\" priority=\"2\" type=\"rule\" dialect=\"mvel\" >\n" +
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
            "    <state id=\"2\" dialect=\"mvel\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" toType=\"DROOLS_DEFAULT\" name=\"one\" priority=\"2\" type=\"rule\" dialect=\"mvel\" >\n" +
            "            eval(true)" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" toType=\"DROOLS_DEFAULT\" name=\"two\" priority=\"1\" type=\"rule\" dialect=\"mvel\" >\n" +
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
            "    <state id=\"2\" dialect=\"mvel\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" toType=\"DROOLS_DEFAULT\" name=\"one\" type=\"rule\" dialect=\"mvel\" >\n" +
            "            Person( age &gt; 21 )" +
            "        </constraint>"+
             "       <constraint toNodeId=\"4\" toType=\"DROOLS_DEFAULT\" name=\"two\" type=\"rule\" dialect=\"mvel\" >\n" +
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
            "    <state id=\"2\" dialect=\"mvel\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" toType=\"DROOLS_DEFAULT\" name=\"one\" type=\"rule\" dialect=\"mvel\" >\n" +
            "            Person( age &gt; 21 )" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" toType=\"DROOLS_DEFAULT\" name=\"two\" type=\"rule\" dialect=\"mvel\" >\n" +
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
            "    <state id=\"2\" dialect=\"mvel\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" toType=\"DROOLS_DEFAULT\" name=\"one\" priority=\"1\" type=\"rule\" dialect=\"mvel\" >\n" +
            "            Person( )" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" toType=\"DROOLS_DEFAULT\" name=\"two\" priority=\"2\" type=\"rule\" dialect=\"mvel\" >\n" +
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
            "    <state id=\"2\" dialect=\"mvel\" >\n" +
            "      <constraints>\n" +
            "        <constraint toNodeId=\"3\" toType=\"DROOLS_DEFAULT\" name=\"one\" priority=\"2\" type=\"rule\" dialect=\"mvel\" >\n" +
            "            Person( )" +
            "        </constraint>"+
             "       <constraint toNodeId=\"5\" toType=\"DROOLS_DEFAULT\" name=\"two\" priority=\"1\" type=\"rule\" dialect=\"mvel\" >\n" +
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
    
}
