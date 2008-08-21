package org.drools.integrationtests;

import static org.drools.integrationtests.SerializationHelper.getSerialisedStatefulSession;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.rule.Package;

public class ProcessMarchallingTest extends TestCase {

    @SuppressWarnings("unchecked")
	public void test1() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.drools.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "  ruleflow-group \"hello\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( rule ));
        
        String process = 
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    		"<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
    		"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    		"    xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
    		"    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
    		"  <header>\n" +
    		"  </header>\n" +
    		"  <nodes>\n" +
    		"    <start id=\"1\" name=\"Start\" />\n" +
    		"    <ruleSet id=\"2\" name=\"Hello\" ruleFlowGroup=\"hello\" />\n" +
    		"    <end id=\"3\" name=\"End\" />\n" +
    		"  </nodes>\n" +
    		"  <connections>\n" +
    		"    <connection from=\"1\" to=\"2\"/>\n" +
			"    <connection from=\"2\" to=\"3\"/>\n" +
			"  </connections>\n" +
			"</process>";
        builder.addProcessFromXml( new StringReader( process ));
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        StatefulSession session = ruleBase.newStatefulSession();

        List<Object> list = new ArrayList<Object>();
        session.setGlobal( "list", list );

        Person p = new Person( "bobba fet", 32);
        session.insert( p );
        session.startProcess("org.test.ruleflow");
        
        assertEquals(1, session.getProcessInstances().size());
        
        session = getSerialisedStatefulSession( session );
        assertEquals(1, session.getProcessInstances().size());
        
        session.fireAllRules();

        assertEquals( 1, ((List<Object>) session.getGlobal("list")).size());
        assertEquals( p, ((List<Object>) session.getGlobal("list")).get(0));
        assertEquals(0, session.getProcessInstances().size());
    }
    
    public void test2() throws Exception {
        String process = 
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    		"<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
    		"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    		"    xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
    		"    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
    		"  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"myVariable\" >\n" +
    		"        <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>OldValue</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
    		"  </header>\n" +
    		"  <nodes>\n" +
    		"    <start id=\"1\" name=\"Start\" />\n" +
    		"    <workItem id=\"2\" name=\"Email\" >\n" +
    		"      <work name=\"Email\" >\n" +
    		"        <parameter name=\"Subject\" >\n" +
    		"          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"          <value>Mail</value>\n" +
    		"        </parameter>\n" +
    		"        <parameter name=\"Text\" >\n" +
    		"          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"          <value>This is an email</value>\n" +
    		"        </parameter>\n" +
    		"        <parameter name=\"To\" >\n" +
    		"          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"          <value>you@mail.com</value>\n" +
    		"        </parameter>\n" +
    		"        <parameter name=\"From\" >\n" +
    		"          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"          <value>me@mail.com</value>\n" +
    		"        </parameter>\n" +
    		"      </work>\n" +
    		"    </workItem>\n" +
    		"    <end id=\"3\" name=\"End\" />\n" +
    		"  </nodes>\n" +
    		"  <connections>\n" +
    		"    <connection from=\"1\" to=\"2\"/>\n" +
			"    <connection from=\"2\" to=\"3\"/>\n" +
			"  </connections>\n" +
			"</process>";
        final PackageBuilder builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process ));
        final Package pkg = builder.getPackage();

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        StatefulSession session = ruleBase.newStatefulSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Email", handler);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("myVariable", "ThisIsMyValue");
        session.startProcess("org.test.ruleflow", variables);

        assertEquals(1, session.getProcessInstances().size());
        assertTrue(handler.getWorkItemId() != -1);
        
        session = getSerialisedStatefulSession( session );
        assertEquals(1, session.getProcessInstances().size());
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
        	session.getProcessInstances().iterator().next().getContextInstance(VariableScope.VARIABLE_SCOPE);
        assertEquals("ThisIsMyValue", variableScopeInstance.getVariable("myVariable"));
        
        session.getWorkItemManager().completeWorkItem(handler.getWorkItemId(), null);
        
        assertEquals(0, session.getProcessInstances().size());
    }
    
    public void test3() throws Exception {
        String process1 = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        	"<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
            "  xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "  xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
            "  type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <imports>\n" +
            "      <import name=\"org.drools.Person\" />\n" +
            "    </imports>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
            "    <split id=\"5\" name=\"AND\" type=\"1\" />\n" +
            "    <subProcess id=\"6\" name=\"SubProcess\" processId=\"com.sample.subflow\" />\n" +
            "    <actionNode id=\"7\" name=\"Action\" >\n" +
            "      <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Executing action 1\");</action>\n" +
            "	 </actionNode>/n" +
            "    <join id=\"8\" name=\"AND\" type=\"1\" />\n" +
            "    <actionNode id=\"9\" name=\"Action\" >\n" +
            "      <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Executing action 2\");</action>\n" +
            "    </actionNode>\n" +
            "    <ruleSet id=\"10\" name=\"RuleSet\" ruleFlowGroup=\"flowgroup\" />\n" +
            "    <milestone id=\"11\" name=\"Event Wait\" >Person( )</milestone>\n" +
            "    <workItem id=\"12\" name=\"Log\" >\n" +
            "      <work name=\"Log\" >\n" +
            "        <parameter name=\"Message\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>This is a log message</value>\n" +
            "        </parameter>\n" +
            "      </work>\n" +
            "    </workItem>\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"9\" to=\"4\" />\n" +
            "    <connection from=\"1\" to=\"5\" />\n" +
            "    <connection from=\"5\" to=\"6\" />\n" +
            "    <connection from=\"5\" to=\"7\" />\n" +
            "    <connection from=\"7\" to=\"8\" />\n" +
            "    <connection from=\"6\" to=\"8\" />\n" +
            "    <connection from=\"10\" to=\"8\" />\n" +
            "    <connection from=\"11\" to=\"8\" />\n" +
            "    <connection from=\"12\" to=\"8\" />\n" +
            "    <connection from=\"8\" to=\"9\" />\n" +
            "    <connection from=\"5\" to=\"10\" />\n" +
            "    <connection from=\"5\" to=\"11\" />\n" +
            "    <connection from=\"5\" to=\"12\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>\n";
        final PackageBuilder builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process1 ));
        
        String process2 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-4.0/process\"\n" +
            "         xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xs:schemaLocation=\"http://drools.org/drools-4.0/process drools-processes-4.0.xsd\"\n" +
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
            "    <milestone id=\"2\" name=\"Event Wait\" >Person( )</milestone>\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\" />\n" +
            "    <connection from=\"2\" to=\"3\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>\n";
        builder.addProcessFromXml( new StringReader( process2 ));
        
        String rule = 
            "package com.sample\n" +
            "import org.drools.Person;\n" +
            "rule \"Hello\" ruleflow-group \"flowgroup\"\n" +
            "    when\n" +
            "    then\n" +
            "        System.out.println( \"Hello\" );\n" +
            "end";
        builder.addPackageFromDrl( new StringReader( rule ));
        
        final Package pkg = builder.getPackage();
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        StatefulSession session = ruleBase.newStatefulSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Log", handler);
        session.startProcess("com.sample.ruleflow");

        assertEquals(2, session.getProcessInstances().size());
        assertTrue(handler.getWorkItemId() != -1);
        
        session = getSerialisedStatefulSession( session );
        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(session);
        assertEquals(2, session.getProcessInstances().size());

        session.getWorkItemManager().completeWorkItem(handler.getWorkItemId(), null);
        session.insert(new Person());
        session.fireAllRules();
        logger.writeToDisk();
        
        assertEquals(0, session.getProcessInstances().size());
    }
    
    private static class TestWorkItemHandler implements WorkItemHandler {
    	private long workItemId = -1;
    	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
			workItemId = workItem.getId();
		}
		public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		}
		public long getWorkItemId() {
			return workItemId;
		}
    }
    
}