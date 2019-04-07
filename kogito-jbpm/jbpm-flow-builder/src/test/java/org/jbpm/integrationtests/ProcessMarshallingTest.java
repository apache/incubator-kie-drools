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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.integrationtests.handler.TestWorkItemHandler;
import org.jbpm.integrationtests.test.Person;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.integrationtests.JbpmSerializationHelper.getSerialisedStatefulKnowledgeSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProcessMarshallingTest extends AbstractBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessMarshallingTest.class);

    @Test
    @SuppressWarnings("unchecked")
	public void testMarshallingProcessInstancesAndGlobals() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.jbpm.integrationtests.test.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "  ruleflow-group \"hello\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

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
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource(new StringReader(rule)), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( process ) ), ResourceType.DRF );

        KieSession ksession = createKieSession(kbuilder.getKnowledgePackages().toArray(new KiePackage[0]));
        ksession.getEnvironment().set("org.jbpm.rule.task.waitstate", true);

        List<Object> list = new ArrayList<Object>();
        ksession.setGlobal( "list", list );

        Person p = new Person( "bobba fet", 32);
        ksession.insert( p );
        ksession.startProcess("org.test.ruleflow");
        
        assertEquals(1, ksession.getProcessInstances().size());
                
        ksession = JbpmSerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, true );
        assertEquals(1, ksession.getProcessInstances().size());
        
        ksession.fireAllRules();
        
        assertEquals( 1, ((List<Object>) ksession.getGlobal("list")).size());
        assertEquals( p, ((List<Object>) ksession.getGlobal("list")).get(0));
        assertEquals(0, ksession.getProcessInstances().size());
    }
    
    @Test
    public void testMarshallingProcessInstanceWithWorkItem() throws Exception {
        String process = 
    		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    		"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
    		"    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    		"    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
    		"    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
    		"  <header>\n" +
    		"    <variables>\n" +
    		"      <variable name=\"myVariable\" >\n" +
    		"        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"        <value>OldValue</value>\n" +
    		"      </variable>\n" +
    		"    </variables>\n" +
    		"  </header>\n" +
    		"  <nodes>\n" +
    		"    <start id=\"1\" name=\"Start\" />\n" +
    		"    <workItem id=\"2\" name=\"Email\" >\n" +
    		"      <work name=\"Email\" >\n" +
    		"        <parameter name=\"Subject\" >\n" +
    		"          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"          <value>Mail</value>\n" +
    		"        </parameter>\n" +
    		"        <parameter name=\"Text\" >\n" +
    		"          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"          <value>This is an email</value>\n" +
    		"        </parameter>\n" +
    		"        <parameter name=\"To\" >\n" +
    		"          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"          <value>you@mail.com</value>\n" +
    		"        </parameter>\n" +
    		"        <parameter name=\"From\" >\n" +
    		"          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
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
        builder.addProcessFromXml( new StringReader( process ));

        KieSession session = createKieSession(builder.getPackages());
        
        TestWorkItemHandler handler = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Email", handler);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("myVariable", "ThisIsMyValue");
        session.startProcess("org.test.ruleflow", variables);

        assertEquals(1, session.getProcessInstances().size());
        assertTrue(handler.getWorkItem() != null);
        
        session = getSerialisedStatefulKnowledgeSession(session);
        assertEquals(1, session.getProcessInstances().size());
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
        	(( ProcessInstance )session.getProcessInstances().iterator().next()).getContextInstance(VariableScope.VARIABLE_SCOPE);
        assertEquals("ThisIsMyValue", variableScopeInstance.getVariable("myVariable"));
        
        session.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        
        assertEquals(0, session.getProcessInstances().size());
    }
    
    @Test
    public void testMarshallingWithHumanTaskAndRule() throws Exception {
        String process1 = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        	"<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "  xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "  xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "  type=\"RuleFlow\" name=\"ruleflow\" id=\"com.sample.ruleflow\" package-name=\"com.sample\" >\n" +
            "\n" +
            "  <header>\n" +
            "    <imports>\n" +
            "      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" +
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
            "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>This is a log message</value>\n" +
            "        </parameter>\n" +
            "      </work>\n" +
            "    </workItem>\n" +
            "    <composite id=\"13\" name=\"CompositeNode\" >\n" +
            "      <variables>\n" +
            "        <variable name=\"x\" >\n" +
            "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>x-value</value>\n" +
            "        </variable>\n" +
            "      </variables>\n" +
            "      <nodes>\n" +
            "        <humanTask id=\"1\" name=\"Human Task\" swimlane=\"swimlane\" >\n" +
            "          <work name=\"Human Task\" >\n" +
            "            <parameter name=\"ActorId\" >\n" +
            "              <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "              <value>John Doe</value>\n" +
            "            </parameter>\n" +
            "            <parameter name=\"Priority\" >\n" +
            "              <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "            <parameter name=\"TaskName\" >\n" +
            "              <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "              <value>Do something !</value>\n" +
            "            </parameter>\n" +
            "            <parameter name=\"Comment\" >\n" +
            "              <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "          </work>\n" +
            "        </humanTask>\n" +
            "        <humanTask id=\"2\" name=\"Human Task\" swimlane=\"swimlane\" >\n" +
            "          <work name=\"Human Task\" >\n" +
            "            <parameter name=\"ActorId\" >\n" +
            "              <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "            <parameter name=\"Priority\" >\n" +
            "              <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "            </parameter>\n" +
            "            <parameter name=\"TaskName\" >\n" +
            "              <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "              <value>Do something else !</value>\n" +
            "            </parameter>\n" +
            "            <parameter name=\"Comment\" >\n" +
            "              <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
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
            "      <import name=\"org.jbpm.integrationtests.test.Person\" />\n" +
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
            "import org.jbpm.integrationtests.test.Person;\n" +
            "rule \"Hello\" ruleflow-group \"flowgroup\"\n" +
            "    when\n" +
            "    then\n" +
            "        System.out.println( \"Hello\" );\n" +
            "end";
        builder.addPackageFromDrl( new StringReader( rule ));
        
        KieSession session = createKieSession(builder.getPackages());
        
        TestWorkItemHandler handler1 = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Log", handler1);
        TestWorkItemHandler handler2 = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler2);
        session.startProcess("com.sample.ruleflow");

        assertEquals(2, session.getProcessInstances().size());
        assertTrue(handler1.getWorkItem() != null);
        long workItemId = handler2.getWorkItem().getId(); 
        assertTrue(workItemId != -1);
        
        session = getSerialisedStatefulKnowledgeSession(session);
        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler2);
        assertEquals(2, session.getProcessInstances().size());

        handler2.reset();
        session.getWorkItemManager().completeWorkItem(workItemId, null);
        assertTrue(handler2.getWorkItem() != null);
        assertEquals("John Doe", handler2.getWorkItem().getParameter("SwimlaneActorId"));
        assertEquals("x-value", handler2.getWorkItem().getParameter("Priority"));
        
        session.getWorkItemManager().completeWorkItem(handler1.getWorkItem().getId(), null);
        session.getWorkItemManager().completeWorkItem(handler2.getWorkItem().getId(), null);
        session.insert(new Person());
        session.fireAllRules();
        
        assertEquals(0, session.getProcessInstances().size());
    }
    
    @Test
    public void testMarshallingWithMultipleHumanTasks() throws Exception {
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
    		"          <type name=\"org.jbpm.process.core.datatype.impl.type.ObjectDataType\" className=\"java.util.List\" />\n" +
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
    		"                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"ActorId\" >\n" +
    		"                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"Priority\" >\n" +
    		"                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"TaskName\" >\n" +
    		"                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"                <value>Do something: #{item}</value>\n" +
    		"              </parameter>\n" +
    		"            </work>\n" +
    		"          </humanTask>\n" +
    		"          <humanTask id=\"2\" name=\"Human Task Again\" >\n" +
    		"            <work name=\"Human Task\" >\n" +
    		"              <parameter name=\"Comment\" >\n" +
    		"                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"ActorId\" >\n" +
    		"                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"Priority\" >\n" +
    		"                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
    		"              </parameter>\n" +
    		"              <parameter name=\"TaskName\" >\n" +
    		"                <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
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
        builder.addProcessFromXml( new StringReader( process ));

        KieSession session = createKieSession(builder.getPackages());
        
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
        
//        session = getSerialisedStatefulSession( session );
//        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        List<WorkItem> workItems = new ArrayList<WorkItem>(handler.getWorkItems());
        handler.reset();
        for (WorkItem workItem: workItems) {
        	session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        }
        assertEquals(1, session.getProcessInstances().size());
        assertEquals(3, handler.getWorkItems().size());
        
        session = getSerialisedStatefulKnowledgeSession(session);

        for (WorkItem workItem: handler.getWorkItems()) {
        	session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        }
        assertEquals(0, session.getProcessInstances().size());
    }
    
    @Test @Ignore
    public void testMarshallingProcessInstanceWithTimer() throws Exception {
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
        builder.addProcessFromXml( new StringReader( process ));

        final KieSession session = createKieSession(builder.getPackages());
        
        session.startProcess("com.sample.ruleflow", null);
        assertEquals(1, session.getProcessInstances().size());
        session.halt();
        
        final StatefulKnowledgeSession session2 = getSerialisedStatefulKnowledgeSession(session);
       
        int sleeps = 3;
        int procInstsAlive = session2.getProcessInstances().size();
        while( procInstsAlive > 0 && sleeps > 0 ) { 
            Thread.sleep(1000);
            --sleeps;
            procInstsAlive = session2.getProcessInstances().size();
        }
        assertEquals(0, session2.getProcessInstances().size());
        
        session2.halt();
    }
    
    @Test
    public void testTimerOnUnmarshalledSession() throws Exception {
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
    		"      <timerNode id=\"4\" name=\"Timer\" delay=\"1000\" />\n" +
    		"      <end id=\"3\" name=\"End\" />\n" +
    		"    </nodes>\n" +
    		"\n" +
    		"    <connections>\n" +
    		"      <connection from=\"1\" to=\"4\" />\n" +
    		"      <connection from=\"4\" to=\"3\" />\n" +
    		"    </connections>\n" +
            "\n" +
            "</process>\n";
        builder.addProcessFromXml( new StringReader( process ));

        KieSession session = createKieSession(builder.getPackages());
        
        session.startProcess("com.sample.ruleflow", null);
        
        // serialize session
        Marshaller marshaller = MarshallerFactory.newMarshaller( session.getKieBase() );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshall( baos, session );
        byte[] b1 = baos.toByteArray();
        baos.close();
       
        // hope that timer hasn't fired yet?
        assertEquals(1, session.getProcessInstances().size());
       
        // dispose of session
        session.dispose();
        
        // deserialize session
        ByteArrayInputStream bais = new ByteArrayInputStream( b1 );        
        StatefulKnowledgeSession session2 = (StatefulKnowledgeSession) marshaller.unmarshall( bais );

        // make sure time job runs
        int sleeps = 3;
        int procInstsAlive = session2.getProcessInstances().size();
        while( procInstsAlive > 0 && sleeps > 0 ) { 
            Thread.yield();
            Thread.sleep(1000);
            --sleeps;
            procInstsAlive = session2.getProcessInstances().size();
        }
       
        // verify
        assertEquals(0, session2.getProcessInstances().size());
    }
  
    private static class TestListWorkItemHandler implements WorkItemHandler {
    	private List<WorkItem> workItems = new ArrayList<WorkItem>();
    	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    	    logger.debug("Executing workItem {}", workItem.getParameter("TaskName"));
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
    
    @Test
    public void testVariablePersistenceMarshallingStrategies() throws Exception {
        String process = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow\" package-name=\"org.test\" >\n" +
            "  <header>\n" +
            "    <variables>\n" +
            "      <variable name=\"myVariable\" >\n" +
            "        <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "        <value>OldValue</value>\n" +
            "      </variable>\n" +
            "      <variable name=\"myPerson\" >\n" +
            "        <type name=\"org.jbpm.process.core.datatype.impl.type.ObjectDataType\" className=\"org.jbpm.integrationtests.test.Person\"/>\n" +
            "      </variable>\n" +
            "    </variables>\n" +
            "  </header>\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <workItem id=\"2\" name=\"Email\" >\n" +
            "      <work name=\"Report\" >\n" +
            "        <parameter name=\"Subject\" >\n" +
            "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>Mail</value>\n" +
            "        </parameter>\n" +
            "        <parameter name=\"Subject\" >\n" +
            "          <type name=\"org.jbpm.process.core.datatype.impl.type.StringDataType\" />\n" +
            "          <value>Mail</value>\n" +
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
        builder.addProcessFromXml( new StringReader( process ));

        KieSession session = createKieSession(builder.getPackages());
        
        TestWorkItemHandler handler = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Report", handler);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("myVariable", "ThisIsMyValue");
        Person myPerson = new Person("Nikola Tesla", 156 );
        variables.put("myPerson", myPerson);
        session.startProcess("org.test.ruleflow", variables);

        assertEquals(1, session.getProcessInstances().size());
        assertTrue(handler.getWorkItem() != null);
        
        session = getSerialisedStatefulKnowledgeSession(session);
        assertEquals(1, session.getProcessInstances().size());
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
            (( ProcessInstance )session.getProcessInstances().iterator().next()).getContextInstance(VariableScope.VARIABLE_SCOPE);
        assertEquals("ThisIsMyValue", variableScopeInstance.getVariable("myVariable"));
        assertEquals(myPerson, variableScopeInstance.getVariable("myPerson"));
        
        session.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        
        assertEquals(0, session.getProcessInstances().size());
    }
}
