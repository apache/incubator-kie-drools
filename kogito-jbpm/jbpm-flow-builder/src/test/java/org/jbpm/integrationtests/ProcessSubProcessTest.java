/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.integrationtests.test.Person;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;

import static org.junit.Assert.assertEquals;

public class ProcessSubProcessTest extends AbstractBaseTest {

    @Test
    public void testSubProcess() throws Exception {
        KieSession workingMemory = createStatefulKnowledgeSessionFromRule(true);
        ProcessInstance processInstance = ( ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        workingMemory.insert(new Person());
        workingMemory.fireAllRules();
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(0, workingMemory.getProcessInstances().size());
    }

    @Test
    public void testSubProcessCancel() throws Exception {
        KieSession workingMemory = createStatefulKnowledgeSessionFromRule(true);
        org.jbpm.process.instance.ProcessInstance processInstance = ( org.jbpm.process.instance.ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        processInstance.setState(ProcessInstance.STATE_ABORTED);
        assertEquals(1, workingMemory.getProcessInstances().size());
    }

    @Test
    public void testIndependentSubProcessCancel() throws Exception {
        KieSession workingMemory = createStatefulKnowledgeSessionFromRule(false);
        org.jbpm.process.instance.ProcessInstance processInstance = ( org.jbpm.process.instance.ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        processInstance.setState(ProcessInstance.STATE_ABORTED);
        assertEquals(0, workingMemory.getProcessInstances().size());
    }

    @Test
    public void testVariableMapping() throws Exception {
        KieSession workingMemory = createStatefulKnowledgeSessionFromRule(true);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("x", "x-value");
        org.jbpm.process.instance.ProcessInstance processInstance = ( org.jbpm.process.instance.ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow", map);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        for (ProcessInstance p: workingMemory.getProcessInstances()) {
    		VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
    			(( org.jbpm.process.instance.ProcessInstance )p).getContextInstance(VariableScope.VARIABLE_SCOPE);
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
        workingMemory.fireAllRules();
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
			processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
    	assertEquals("z-value", variableScopeInstance.getVariable("x"));
    	assertEquals(10, variableScopeInstance.getVariable("m"));
        assertEquals(0, workingMemory.getProcessInstances().size());
    }

    private static KieSession createStatefulKnowledgeSessionFromRule(boolean independentSubProcess) throws Exception { 
        KieBase ruleBase = readRule(independentSubProcess);
        return ruleBase.newKieSession();
    }
    
	private static KieBase readRule(boolean independent) throws Exception {
		KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
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
			"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
			"        <value></value>\n" +
			"      </variable>\n" +
			"      <variable name=\"m\" >\n" +
			"        <type name=\"org.jbpm.process.core.datatype.impl.type.IntegerDataType\" />\n" +
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
			"      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" +
			"    </imports>\n" +
			"    <variables>\n" +
			"      <variable name=\"y\" >\n" +
			"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
			"        <value></value>\n" +
			"      </variable>\n" +
			"      <variable name=\"z\" >\n" +
			"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
			"        <value>z-value</value>\n" +
			"      </variable>\n" +
			"      <variable name=\"n\" >\n" +
			"        <type name=\"org.jbpm.process.core.datatype.impl.type.IntegerDataType\" />\n" +
			"      </variable>\n" +
			"      <variable name=\"o\" >\n" +
			"        <type name=\"org.jbpm.process.core.datatype.impl.type.IntegerDataType\" />\n" +
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
		InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addPackages(Arrays.asList(builder.getPackages()));
		return kbase;
	}
	
	@Test
    public void testDynamicSubProcess() throws Exception {
        KieBase kbase = readDynamicSubProcess();
        KieSession workingMemory = kbase.newKieSession();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "subflow");
        ProcessInstance processInstance = ( ProcessInstance )
    		workingMemory.startProcess("com.sample.ruleflow", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        assertEquals(2, workingMemory.getProcessInstances().size());
        workingMemory.insert(new Person());
        workingMemory.fireAllRules();
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(0, workingMemory.getProcessInstances().size());
    }

	private static KieBase readDynamicSubProcess() throws Exception {
		KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
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
			"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
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
			"      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" +
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
		InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addPackages(Arrays.asList(builder.getPackages()));
		return kbase;
	}
	
}
