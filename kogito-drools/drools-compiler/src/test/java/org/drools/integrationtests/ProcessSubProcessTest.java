package org.drools.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.rule.Package;

public class ProcessSubProcessTest extends TestCase {

    public void testSubProcess() throws Exception {
        RuleBase ruleBase = readRule(true);
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        ProcessInstance processInstance = ( ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        workingMemory.insert(new Person());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(0, workingMemory.getProcessInstances().size());
    }

    public void testSubProcessCancel() throws Exception {
        RuleBase ruleBase = readRule(true);
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        ProcessInstance processInstance = ( ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        processInstance.setState(ProcessInstance.STATE_ABORTED);
        assertEquals(1, workingMemory.getProcessInstances().size());
    }

    public void testIndependentSubProcessCancel() throws Exception {
        RuleBase ruleBase = readRule(false);
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        ProcessInstance processInstance = ( ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        processInstance.setState(ProcessInstance.STATE_ABORTED);
        assertEquals(0, workingMemory.getProcessInstances().size());
    }

    public void testVariableMapping() throws Exception {
        RuleBase ruleBase = readRule(false);
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("x", "x-value");
        ProcessInstance processInstance = ( ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow", map);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        for (ProcessInstance p: workingMemory.getProcessInstances()) {
    		VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
    			(( ProcessInstance )p).getContextInstance(VariableScope.VARIABLE_SCOPE);
        	if ("com.sample.ruleflow".equals(p.getProcessId())) {
        		assertEquals("x-value", variableScopeInstance.getVariable("x"));
        	} else if ("com.sample.subflow".equals(p.getProcessId())) {
        		assertEquals("x-value", variableScopeInstance.getVariable("y"));
        		assertEquals("z-value", variableScopeInstance.getVariable("z"));
        		assertEquals(7, variableScopeInstance.getVariable("n"));
        		assertEquals(10, variableScopeInstance.getVariable("o"));
        	}
        }
        workingMemory.insert(new Person());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
			processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
    	assertEquals("z-value", variableScopeInstance.getVariable("x"));
    	assertEquals(10, variableScopeInstance.getVariable("m"));
        assertEquals(0, workingMemory.getProcessInstances().size());
    }

	private static RuleBase readRule(boolean independent) throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <variables>\n" +
			"      <variable name=\"x\" >\n" +
			"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
			"        <value></value>\n" +
			"      </variable>\n" +
			"      <variable name=\"m\" >\n" +
			"        <type name=\"org.drools.process.core.datatype.impl.type.IntegerDataType\" />\n" +
			"        <value></value>\n" +
			"      </variable>\n" +
			"    </variables>\n" + 
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <subProcess id=\"2\" name=\"SubProcess\" processId=\"com.sample.subflow\" independent=\"" + independent + "\" >\n" +
			"      <mapping type=\"in\" from=\"x\" to=\"y\" />\n" +
			"      <mapping type=\"in\" from=\"x.length()\" to=\"n\" />\n" +
			"      <mapping type=\"out\" from=\"z\" to=\"x\" />\n" +
			"      <mapping type=\"out\" from=\"o\" to=\"m\" />\n" +
			"    </subProcess>\n" +
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
		source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"com.sample.subflow\" package-name=\"com.sample\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <imports>\n" +
			"      <import name=\"org.drools.Person\" />\n" +
			"    </imports>\n" +
			"    <variables>\n" +
			"      <variable name=\"y\" >\n" +
			"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
			"        <value></value>\n" +
			"      </variable>\n" +
			"      <variable name=\"z\" >\n" +
			"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
			"        <value>z-value</value>\n" +
			"      </variable>\n" +
			"      <variable name=\"n\" >\n" +
			"        <type name=\"org.drools.process.core.datatype.impl.type.IntegerDataType\" />\n" +
			"      </variable>\n" +
			"      <variable name=\"o\" >\n" +
			"        <type name=\"org.drools.process.core.datatype.impl.type.IntegerDataType\" />\n" +
			"        <value>10</value>\n" +
			"      </variable>\n" +
			"    </variables>\n" + 
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
		return ruleBase;
	}
	
    public void testDynamicSubProcess() throws Exception {
        RuleBase ruleBase = readDynamicSubProcess();
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "subflow");
        ProcessInstance processInstance = ( ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        workingMemory.insert(new Person());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(0, workingMemory.getProcessInstances().size());
    }

	private static RuleBase readDynamicSubProcess() throws Exception {
		PackageBuilder builder = new PackageBuilder();
		Reader source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" +
			"\n" +
			"  <header>\n" +
			"    <variables>\n" +
			"      <variable name=\"x\" >\n" +
			"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
			"        <value></value>\n" +
			"      </variable>\n" +
			"    </variables>\n" + 
			"  </header>\n" +
			"\n" +
			"  <nodes>\n" +
			"    <start id=\"1\" name=\"Start\" />\n" +
			"    <subProcess id=\"2\" name=\"SubProcess\" processId=\"com.sample.#{x}\" />\n" +
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
		source = new StringReader(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
			"         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
			"         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
			"         type=\"RuleFlow\" name=\"flow\" id=\"com.sample.subflow\" package-name=\"com.sample\" >\n" +
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
		return ruleBase;
	}
	
}
