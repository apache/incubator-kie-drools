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
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.rule.Package;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.runtime.StatefulKnowledgeSession;

public class ProcessSplitTest extends TestCase {
    
    public void testSplitWithProcessInstanceConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.integrationtests.ProcessSplitTest.ProcessUtils\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "    <variables>\n" +
            "      <variable name=\"name\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(context.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"rule\" dialect=\"mvel\" >eval(true)</constraint>" +
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" name=\"constraint\" priority=\"1\" type=\"rule\" dialect=\"mvel\" >processInstance: org.drools.ruleflow.instance.RuleFlowProcessInstance()" +
            "Person( name == (ProcessUtils.getValue(processInstance, \"name\")) )</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(context.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<Long> list = new ArrayList<Long>();
        workingMemory.setGlobal("list", list);

        Person john = new Person("John Doe", 20);
        Person jane = new Person("Jane Doe", 20);
        Person julie = new Person("Julie Doe", 20);
        workingMemory.insert(john);
        workingMemory.insert(jane);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", john.getName());
        ProcessInstance processInstance1 = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        params = new HashMap<String, Object>();
        params.put("name", jane.getName());
        ProcessInstance processInstance2 = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        params = new HashMap<String, Object>();
        params.put("name", julie.getName());
        ProcessInstance processInstance3 = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance1.getState());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance2.getState());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance3.getState());
        assertEquals(2, list.size());
    }

    public void testSplitWithProcessInstanceConstraint2() {
    	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.runtime.process.WorkflowProcessInstance\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "    <variables>\n" +
            "      <variable name=\"name\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(kcontext.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"rule\" dialect=\"mvel\" >eval(true)</constraint>" +
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" name=\"constraint\" priority=\"1\" type=\"rule\" dialect=\"mvel\" >processInstance: WorkflowProcessInstance()" +
            "Person( name == ( processInstance.getVariable(\"name\") ) )</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(kcontext.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        kbuilder.add( ResourceFactory.newReaderResource( source ), ResourceType.DRF );
        for (KnowledgeBuilderError error: kbuilder.getErrors()) {
        	System.out.println(error);
        }
        
        Collection<KnowledgePackage> kpkgs = kbuilder.getKnowledgePackages();
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kpkgs );        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<Long> list = new ArrayList<Long>();
        ksession.setGlobal("list", list);

        Person john = new Person("John Doe", 20);
        Person jane = new Person("Jane Doe", 20);
        Person julie = new Person("Julie Doe", 20);
        ksession.insert(john);
        ksession.insert(jane);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", john.getName());
        org.drools.runtime.process.ProcessInstance processInstance1 =
            ksession.startProcess("org.drools.process-split", params);
        
        params = new HashMap<String, Object>();
        params.put("name", jane.getName());
        org.drools.runtime.process.ProcessInstance processInstance2 =
        	ksession.startProcess("org.drools.process-split", params);
        
        params = new HashMap<String, Object>();
        params.put("name", julie.getName());
        org.drools.runtime.process.ProcessInstance processInstance3 =
            ksession.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance1.getState());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance2.getState());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance3.getState());
        assertEquals(2, list.size());
    }

    public void testSplitWithMVELContextConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.integrationtests.ProcessSplitTest.ProcessUtils\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "    <variables>\n" +
            "      <variable name=\"person\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"org.drools.Person\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(context.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"code\" dialect=\"mvel\" >return true;</constraint>" +
            // TODO:  &amp;&amp; ((Person) context.getVariable(\"person\")).name != null
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" priority=\"1\" type=\"code\" dialect=\"mvel\" >return context.getVariable(\"person\") != null;</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(context.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<Long> list = new ArrayList<Long>();
        workingMemory.setGlobal("list", list);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", new Person("John Doe"));
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
    }
    
    public void testSplitWithJavaContextConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.integrationtests.ProcessSplitTest.ProcessUtils\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "    <variables>\n" +
            "      <variable name=\"name\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(context.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"code\" dialect=\"java\" >return true;</constraint>" +
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" priority=\"1\" type=\"code\" dialect=\"java\" >return context.getVariable(\"name\") != null &amp;&amp; ((String) context.getVariable(\"name\")).length() > 0;</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(context.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<Long> list = new ArrayList<Long>();
        workingMemory.setGlobal("list", list);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "John Doe");
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
    }
    
    public void testSplitWithMVELkContextConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.integrationtests.ProcessSplitTest.ProcessUtils\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "    <variables>\n" +
            "      <variable name=\"person\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"org.drools.Person\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(kcontext.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"code\" dialect=\"mvel\" >return true;</constraint>" +
            // TODO:  &amp;&amp; ((org.drools.Person) context.getVariable(\"person\")).name != null
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" priority=\"1\" type=\"code\" dialect=\"mvel\" >return context.getVariable(\"person\") != null;</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(kcontext.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<Long> list = new ArrayList<Long>();
        workingMemory.setGlobal("list", list);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", new Person("John Doe"));
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
    }
    
    public void testSplitWithJavakContextConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.integrationtests.ProcessSplitTest.ProcessUtils\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "    <variables>\n" +
            "      <variable name=\"name\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(kcontext.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"code\" dialect=\"java\" >return true;</constraint>" +
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" priority=\"1\" type=\"code\" dialect=\"java\" >return kcontext.getVariable(\"name\") != null &amp;&amp; ((String) kcontext.getVariable(\"name\")).length() > 0;</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(kcontext.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<Long> list = new ArrayList<Long>();
        workingMemory.setGlobal("list", list);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "John Doe");
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
    }
    
    public void testSplitWithMVELVariableConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.integrationtests.ProcessSplitTest.ProcessUtils\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "    <variables>\n" +
            "      <variable name=\"name\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(context.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"code\" dialect=\"mvel\" >return true;</constraint>" +
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" priority=\"1\" type=\"code\" dialect=\"mvel\" >return name != null &amp;&amp; name.length > 0;</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(context.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<Long> list = new ArrayList<Long>();
        workingMemory.setGlobal("list", list);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "John Doe");
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
    }
    
    public void testSplitWithJavaVariableConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.integrationtests.ProcessSplitTest.ProcessUtils\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "    <variables>\n" +
            "      <variable name=\"name\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(context.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"code\" dialect=\"java\" >return true;</constraint>" +
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" priority=\"1\" type=\"code\" dialect=\"java\" >return name != null &amp;&amp; name.length() > 0;</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(context.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<Long> list = new ArrayList<Long>();
        workingMemory.setGlobal("list", list);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "John Doe");
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
    }

    public void testSplitWithMVELGlobalConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.integrationtests.ProcessSplitTest.ProcessUtils\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "    <variables>\n" +
            "      <variable name=\"name\" >\n" +
            "        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(context.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"code\" dialect=\"mvel\" >return true;</constraint>" +
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" priority=\"1\" type=\"code\" dialect=\"mvel\" >return list != null &amp;&amp; list.size >= 0;</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(context.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<Long> list = new ArrayList<Long>();
        workingMemory.setGlobal("list", list);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "John Doe");
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
    }
    
    public void testSplitWithJavaGlobalConstraint() {
        PackageBuilder builder = new PackageBuilder();
        Reader source = new StringReader(
            "<process xmlns=\"http://drools.org/drools-5.0/process\"" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "         xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"" +
            "         type=\"RuleFlow\" name=\"ruleflow\" id=\"org.drools.process-split\" package-name=\"org.drools\" >" +
            "" +
            "  <header>" +
            "    <imports>" +
            "      <import name=\"org.drools.Person\" />" +
            "      <import name=\"org.drools.integrationtests.ProcessSplitTest.ProcessUtils\" />" +
            "    </imports>" +
            "    <globals>" +
            "      <global identifier=\"list\" type=\"java.util.List\" />" +
            "    </globals>" +
            "  </header>" +
            "" +
            "  <nodes>" +
            "    <actionNode id=\"2\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >insert(context.getProcessInstance());</action>" +
            "    </actionNode>" +
            "    <split id=\"4\" name=\"Split\" type=\"2\" >" +
            "      <constraints>" +
            "        <constraint toNodeId=\"8\" toType=\"DROOLS_DEFAULT\" priority=\"2\" type=\"code\" dialect=\"java\" >return true;</constraint>" +
            "        <constraint toNodeId=\"6\" toType=\"DROOLS_DEFAULT\" priority=\"1\" type=\"code\" dialect=\"java\" >return list != null &amp;&amp; list.size() >= 0;</constraint>" +
            "      </constraints>" +
            "    </split>" +
            "    <end id=\"8\" name=\"End\" />" +
            "    <actionNode id=\"6\" name=\"Action\" >" +
            "        <action type=\"expression\" dialect=\"mvel\" >list.add(context.getProcessInstance().getId());</action>" +
            "    </actionNode>" +
            "    <start id=\"1\" name=\"Start\" />" +
            "    <end id=\"3\" name=\"End\" />" +
            "  </nodes>" +
            "  <connections>" +
            "    <connection from=\"1\" to=\"2\" />" +
            "    <connection from=\"2\" to=\"4\" />" +
            "    <connection from=\"4\" to=\"8\" />" +
            "    <connection from=\"4\" to=\"6\" />" +
            "    <connection from=\"6\" to=\"3\" />" +
            "  </connections>" +
            "" +
            "</process>");
        builder.addRuleFlow(source);
        Package pkg = builder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkg );
        WorkingMemory workingMemory = ruleBase.newStatefulSession();
        List<Long> list = new ArrayList<Long>();
        workingMemory.setGlobal("list", list);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "John Doe");
        ProcessInstance processInstance = ( ProcessInstance )
            workingMemory.startProcess("org.drools.process-split", params);
        
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertEquals(1, list.size());
    }
    
    public static class ProcessUtils {
    	
    	public static Object getValue(RuleFlowProcessInstance processInstance, String name) {
    		VariableScopeInstance scope = (VariableScopeInstance)
    			processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
    		return scope.getVariable(name);
    	}
    	
    }

}
