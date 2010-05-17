package org.drools.integrationtests;

import static org.drools.integrationtests.SerializationHelper.getSerialisedStatefulSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.drools.compiler.PackageBuilder;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

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
    		"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
    		"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    		"    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
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
    		"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
    		"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    		"    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
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
        assertTrue(handler.getWorkItem() != null);
        
        session = getSerialisedStatefulSession( session );
        assertEquals(1, session.getProcessInstances().size());
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
        	(( ProcessInstance )session.getProcessInstances().iterator().next()).getContextInstance(VariableScope.VARIABLE_SCOPE);
        assertEquals("ThisIsMyValue", variableScopeInstance.getVariable("myVariable"));
        
        session.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        
        assertEquals(0, session.getProcessInstances().size());
    }
    
    public void test3() throws Exception {
        String process1 = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        	"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "  xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "  xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "  type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <imports>\n" +
            "      <import name=\"org.drools.Person\" />\n" +
            "    </imports>\n" +
            "    <swimlanes>\n" +
            "      <swimlane name=\"swimlane\" />\n" +
            "    </swimlanes>\n" +
            "  </header>\n" +
            "\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <end id=\"4\" name=\"End\" />\n" +
            "    <split id=\"5\" name=\"AND\" type=\"1\" />\n" +
            "    <subProcess id=\"6\" name=\"SubProcess\" processId=\"com.sample.subflow\" />\n" +
            "    <actionNode id=\"7\" name=\"Action\" >\n" +
            "      <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Executing action 1\");</action>\n" +
            "	 </actionNode>\n" +
            "    <join id=\"8\" name=\"AND\" type=\"1\" />\n" +
            "    <actionNode id=\"9\" name=\"Action\" >\n" +
            "      <action type=\"expression\" dialect=\"mvel\" >System.out.println(\"Executing action 2\");</action>\n" +
            "    </actionNode>\n" +
            "    <ruleSet id=\"10\" name=\"RuleSet\" ruleFlowGroup=\"flowgroup\" />\n" +
            "    <milestone id=\"11\" name=\"Event Wait\" >\n" +
            "      <constraint type=\"rule\" dialect=\"mvel\" >Person( )</constraint>\n" +
			"    </milestone>\n" +
            "    <workItem id=\"12\" name=\"Log\" >\n" +
            "      <work name=\"Log\" >\n" +
            "        <parameter name=\"Message\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>This is a log message</value>\n" +
            "        </parameter>\n" +
            "      </work>\n" +
            "    </workItem>\n" +
            "    <composite id=\"13\" name=\"CompositeNode\" >\n" +
            "      <variables>\n" +
            "        <variable name=\"x\" >\n" +
            "          <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>x-value</value>\n" +
            "        </variable>\n" +
            "      </variables>\n" +
            "      <nodes>\n" +
            "        <humanTask id=\"1\" name=\"Human Task\" swimlane=\"swimlane\" >\n" +
            "          <work name=\"Human Task\" >\n" +
            "            <parameter name=\"ActorId\" >\n" +
            "              <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "              <value>John Doe</value>\n" +
            "            </parameter>\n" +
            "            <parameter name=\"Priority\" >\n" +
            "              <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "            <parameter name=\"TaskName\" >\n" +
            "              <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "              <value>Do something !</value>\n" +
            "            </parameter>\n" +
            "            <parameter name=\"Comment\" >\n" +
            "              <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "          </work>\n" +
            "        </humanTask>\n" +
            "        <humanTask id=\"2\" name=\"Human Task\" swimlane=\"swimlane\" >\n" +
            "          <work name=\"Human Task\" >\n" +
            "            <parameter name=\"ActorId\" >\n" +
            "              <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "            <parameter name=\"Priority\" >\n" +
            "              <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "            <parameter name=\"TaskName\" >\n" +
            "              <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "              <value>Do something else !</value>\n" +
            "            </parameter>\n" +
            "            <parameter name=\"Comment\" >\n" +
            "              <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "          </work>\n" +
            "          <mapping type=\"in\" from=\"x\" to=\"Priority\" />\n" +
            "        </humanTask>\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"1\" to=\"2\" />\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"2\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </composite>\n" +
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
            "    <connection from=\"13\" to=\"8\" />\n" +
            "    <connection from=\"8\" to=\"9\" />\n" +
            "    <connection from=\"5\" to=\"10\" />\n" +
            "    <connection from=\"5\" to=\"11\" />\n" +
            "    <connection from=\"5\" to=\"12\" />\n" +
            "    <connection from=\"5\" to=\"13\" />\n" +
            "  </connections>\n" +
            "\n" +
            "</process>\n";
        final PackageBuilder builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process1 ));
        
        String process2 =
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
			"      <constraint type=\"rule\" dialect=\"mvel\" >Person( )</constraint>\n" +
			"    </milestone>\n" +
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
        TestWorkItemHandler handler1 = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Log", handler1);
        TestWorkItemHandler handler2 = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler2);
        session.startProcess("com.sample.ruleflow");

        assertEquals(2, session.getProcessInstances().size());
        assertTrue(handler1.getWorkItem() != null);
        long workItemId = handler2.getWorkItem().getId(); 
        assertTrue(workItemId != -1);
        
        session = getSerialisedStatefulSession( session );
        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler2);
        assertEquals(2, session.getProcessInstances().size());

        handler2.reset();
        session.getWorkItemManager().completeWorkItem(workItemId, null);
        assertTrue(handler2.getWorkItem() != null);
        assertEquals("John Doe", handler2.getWorkItem().getParameter("ActorId"));
        assertEquals("x-value", handler2.getWorkItem().getParameter("Priority"));
        
        session.getWorkItemManager().completeWorkItem(handler1.getWorkItem().getId(), null);
        session.getWorkItemManager().completeWorkItem(handler2.getWorkItem().getId(), null);
        session.insert(new Person());
        session.fireAllRules();
        
        assertEquals(0, session.getProcessInstances().size());
    }
    
    public void test4() throws Exception {
        String process = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        	"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "  xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "  xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "  type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" +
            "\n" +
            "    <header>\n" +
            "      <variables>\n" +
            "        <variable name=\"list\" >\n" +
    		"          <type name=\"org.drools.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
    		"        </variable>\n" +
    		"      </variables>\n" +
    		"    </header>\n" +
    		"\n" +
    		"    <nodes>\n" +
    		"      <forEach id=\"4\" name=\"ForEach\" variableName=\"item\" collectionExpression=\"list\" >\n" +
    		"        <nodes>\n" +
    		"          <humanTask id=\"1\" name=\"Human Task\" >\n" +
    		"            <work name=\"Human Task\" >\n" +
    		"              <parameter name=\"Comment\" >\n" +
    		"                <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"ActorId\" >\n" +
    		"                <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"Priority\" >\n" +
    		"                <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"TaskName\" >\n" +
    		"                <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"                <value>Do something: #{item}</value>\n" +
    		"              </parameter>\n" +
    		"            </work>\n" +
    		"          </humanTask>\n" +
    		"          <humanTask id=\"2\" name=\"Human Task Again\" >\n" +
    		"            <work name=\"Human Task\" >\n" +
    		"              <parameter name=\"Comment\" >\n" +
    		"                <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"ActorId\" >\n" +
    		"                <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"Priority\" >\n" +
    		"                <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"TaskName\" >\n" +
    		"                <type name=\"org.drools.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"                <value>Do something else: #{item}</value>\n" +
    		"              </parameter>\n" +
    		"            </work>\n" +
    		"          </humanTask>\n" +
    		"        </nodes>\n" +
    		"        <connections>\n" +
    		"          <connection from=\"1\" to=\"2\" />\n" +
    		"        </connections>\n" +
    		"        <in-ports>\n" +
    		"          <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
    		"        </in-ports>\n" +
    		"        <out-ports>\n" +
    		"          <out-port type=\"DROOLS_DEFAULT\" nodeId=\"2\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
    		"        </out-ports>\n" +
    		"      </forEach>\n" +
    		"      <start id=\"1\" name=\"Start\" />\n" +
    		"      <end id=\"3\" name=\"End\" />\n" +
    		"    </nodes>\n" +
    		"\n" +
    		"    <connections>\n" +
    		"      <connection from=\"1\" to=\"4\" />\n" +
    		"      <connection from=\"4\" to=\"3\" />\n" +
    		"    </connections>\n" +
            "\n" +
            "</process>\n";
        final PackageBuilder builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process ));
        final Package pkg = builder.getPackage();
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        StatefulSession session = ruleBase.newStatefulSession();
        TestListWorkItemHandler handler = new TestListWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        List<String> list = new ArrayList<String>();
        list.add("one");
        list.add("two");
        list.add("three");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("list", list);
        session.startProcess("com.sample.ruleflow", parameters);

        assertEquals(1, session.getProcessInstances().size());
        assertEquals(3, handler.getWorkItems().size());
        
        session = getSerialisedStatefulSession( session );
        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        List<WorkItem> workItems = new ArrayList<WorkItem>(handler.getWorkItems());
        handler.reset();
        for (WorkItem workItem: workItems) {
        	session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        }
        assertEquals(1, session.getProcessInstances().size());
        assertEquals(3, handler.getWorkItems().size());
        
        session = getSerialisedStatefulSession( session );

        for (WorkItem workItem: handler.getWorkItems()) {
        	session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        }
        
        assertEquals(0, session.getProcessInstances().size());
    }
    
    public void test5() throws Exception {
        String process = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        	"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "  xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "  xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "  type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" +
            "\n" +
            "    <header>\n" +
    		"    </header>\n" +
    		"\n" +
    		"    <nodes>\n" +
    		"      <start id=\"1\" name=\"Start\" />\n" +
    		"      <timerNode id=\"4\" name=\"Timer\" delay=\"200\" />\n" +
    		"      <end id=\"3\" name=\"End\" />\n" +
    		"    </nodes>\n" +
    		"\n" +
    		"    <connections>\n" +
    		"      <connection from=\"1\" to=\"4\" />\n" +
    		"      <connection from=\"4\" to=\"3\" />\n" +
    		"    </connections>\n" +
            "\n" +
            "</process>\n";
        final PackageBuilder builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process ));
        final Package pkg = builder.getPackage();
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        final StatefulSession session = ruleBase.newStatefulSession();

        new Thread(new Runnable() {
			public void run() {
	        	session.fireUntilHalt();       	
			}
        }).start();
		
        session.startProcess("com.sample.ruleflow", null);

        assertEquals(1, session.getProcessInstances().size());
        session.halt();
        
        final StatefulSession session2 = getSerialisedStatefulSession( session );
        
		new Thread(new Runnable() {
			public void run() {
	        	session2.fireUntilHalt();       	
			}
        }).start();
		
        Thread.sleep(400);

        assertEquals(0, session2.getProcessInstances().size());
        
        session2.halt();
    }
    
    public void test6() throws Exception {
        String process = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        	"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "  xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "  xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "  type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" +
            "\n" +
            "    <header>\n" +
    		"    </header>\n" +
    		"\n" +
    		"    <nodes>\n" +
    		"      <start id=\"1\" name=\"Start\" />\n" +
    		"      <timerNode id=\"4\" name=\"Timer\" delay=\"200\" />\n" +
    		"      <end id=\"3\" name=\"End\" />\n" +
    		"    </nodes>\n" +
    		"\n" +
    		"    <connections>\n" +
    		"      <connection from=\"1\" to=\"4\" />\n" +
    		"      <connection from=\"4\" to=\"3\" />\n" +
    		"    </connections>\n" +
            "\n" +
            "</process>\n";
        final PackageBuilder builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process ));
        final Package pkg = builder.getPackage();
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(pkg);

        final StatefulSession session = ruleBase.newStatefulSession();
        
		new Thread(new Runnable() {
			public void run() {
	        	session.fireUntilHalt();       	
			}
        }).start();
		
        session.startProcess("com.sample.ruleflow", null);
        assertEquals(1, session.getProcessInstances().size());
        
        StatefulKnowledgeSession ksession = new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) session );
        Marshaller marshaller = MarshallerFactory.newMarshaller( ksession.getKnowledgeBase() );
     

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshall( baos, ksession );
        byte[] b1 = baos.toByteArray();
        session.halt();
        session.dispose();
        Thread.sleep(400);
        
        ByteArrayInputStream bais = new ByteArrayInputStream( b1 );        
        final StatefulSession session2 = ( StatefulSession ) (( StatefulKnowledgeSessionImpl) marshaller.unmarshall( bais ) ).session;
        
		new Thread(new Runnable() {
			public void run() {
	        	session2.fireUntilHalt();       	
			}
        }).start();
		
        Thread.sleep(100);

        assertEquals(0, session2.getProcessInstances().size());
        session2.halt();
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
		public void reset() {
			workItem = null;
		}
    }
    
    private static class TestListWorkItemHandler implements WorkItemHandler {
    	private List<WorkItem> workItems = new ArrayList<WorkItem>();
    	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    		System.out.println("Executing workItem " + workItem.getParameter("TaskName"));
			workItems.add(workItem);
		}
		public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			workItems.remove(workItem);
		}
		public List<WorkItem> getWorkItems() {
			return workItems;
		}
		public void reset() {
			workItems.clear();
		}
    }
    
}