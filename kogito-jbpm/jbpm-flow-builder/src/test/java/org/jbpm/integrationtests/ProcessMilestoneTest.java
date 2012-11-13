package org.jbpm.integrationtests;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.jbpm.JbpmTestCase;
import org.jbpm.Person;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;

public class ProcessMilestoneTest extends JbpmTestCase {
    
    public void testMilestone() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.kie.milestone\" package-name=\"org.jbpm\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <imports>\n" +
            "      <import name=\"org.jbpm.Person\" />\n" +
            "    </imports>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <milestone id=\"2\" name=\"Milestone\" >\n" +
            "      <constraint type=\"rule\" dialect=\"mvel\" >Person( name == \"John Doe\" )</constraint>" +
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
        
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.kie.milestone");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        workingMemory.insert(new Person("Jane Doe", 20));
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        workingMemory.insert(new Person("John Doe", 50));
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testMilestoneWithProcessInstanceConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "         type=\"RuleFlow\" name=\"flow\" id=\"org.kie.milestone\" package-name=\"org.jbpm\" version=\"1\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <imports>\n" +
            "      <import name=\"org.jbpm.Person\" />\n" +
            "      <import name=\"org.jbpm.integrationtests.ProcessMilestoneTest.ProcessUtils\" />\n" +
            "    </imports>\n" +
            "    <variables>\n" +
            "      <variable name=\"name\" >\n" +
            "        <type name=\"org.kie.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <milestone id=\"2\" name=\"Milestone\" >\n" +
            "      <constraint type=\"rule\" dialect=\"mvel\" >processInstance: org.jbpm.ruleflow.instance.RuleFlowProcessInstance()\n" +
            "Person( name == (ProcessUtils.getValue(processInstance, \"name\")) )</constraint>" +
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
        for (DroolsError error: builder.getErrors().getErrors()) {
        	System.err.println(error);
        }
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        
        Person john = new Person("John Doe", 20);
        Person jane = new Person("Jane Doe", 20);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", john.getName());
        ProcessInstance processInstanceJohn = ( ProcessInstance )
            workingMemory.startProcess("org.kie.milestone", params);
        workingMemory.insert(processInstanceJohn);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceJohn.getState());

        params = new HashMap<String, Object>();
        params.put("name", jane.getName());
        ProcessInstance processInstanceJane = ( ProcessInstance )
            workingMemory.startProcess("org.kie.milestone", params);
        workingMemory.insert(processInstanceJane);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceJane.getState());
        
        workingMemory.insert(jane);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstanceJohn.getState());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceJane.getState());
        
        workingMemory.insert(john);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstanceJohn.getState());
    }
    
    public static class ProcessUtils {
    	
    	public static Object getValue(RuleFlowProcessInstance processInstance, String name) {
    		VariableScopeInstance scope = (VariableScopeInstance)
    			processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
    		return scope.getVariable(name);
    	}
    	
    }

}
