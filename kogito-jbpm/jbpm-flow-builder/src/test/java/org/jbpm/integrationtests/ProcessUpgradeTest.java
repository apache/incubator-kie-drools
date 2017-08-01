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

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.ReaderResource;
import org.jbpm.integrationtests.handler.TestWorkItemHandler;
import org.jbpm.integrationtests.test.Person;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.instance.WorkflowProcessInstanceUpgrader;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;

public class ProcessUpgradeTest extends AbstractBaseTest {
    
    @Test
    public void testDefaultUpgrade() throws Exception {
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

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( new ReaderResource( new StringReader( rule )), ResourceType.DRL );
        
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
            "    <workItem id=\"2\" name=\"Hello\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "      </work>\n" +
            "    </workItem>\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\"/>\n" +
            "    <connection from=\"2\" to=\"3\"/>\n" +
            "  </connections>\n" +
            "</process>";
        builder.add( new ReaderResource( new StringReader( process )), ResourceType.DRF );
        
//        RuleBaseConfiguration config = new RuleBaseConfiguration();
//        config.setRuleBaseUpdateHandler(null);

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( builder.getKnowledgePackages() );
        KieSession session = kbase.newKieSession();

        TestWorkItemHandler handler = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        List<String> list = new ArrayList<String>();
        session.setGlobal( "list", list );

        Person p = new Person( "bobba fet", 32);
        session.insert( p );
        ProcessInstance processInstance = ( ProcessInstance ) session.startProcess("org.test.ruleflow");
        
        assertEquals(1, session.getProcessInstances().size());
        
        String process2 = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow2\" package-name=\"org.test\" >\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "  </header>\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <workItem id=\"2\" name=\"Hello\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "      </work>\n" +
            "    </workItem>\n" +
            "    <actionNode id=\"4\" name=\"Action\" >" +
            "      <action type=\"expression\" dialect=\"java\">System.out.println();\n" +
            "list.add(\"Executed\");</action>\n" +
            "    </actionNode>\n" + 
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\"/>\n" +
            "    <connection from=\"2\" to=\"4\"/>\n" +
            "    <connection from=\"4\" to=\"3\"/>\n" +
            "  </connections>\n" +
            "</process>";
        builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( new ReaderResource( new StringReader( process2 )), ResourceType.DRF );
        kbase.addPackages( builder.getKnowledgePackages() );
        
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
            session, processInstance.getId(), "org.test.ruleflow2", new HashMap<String, Long>());
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        session.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

        assertEquals(1, list.size());
    }

    @Test
    public void testMappingUpgrade() throws Exception {
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

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( new ReaderResource( new StringReader( rule )), ResourceType.DRL );
        
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
            "    <workItem id=\"2\" name=\"Hello\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "      </work>\n" +
            "    </workItem>\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\"/>\n" +
            "    <connection from=\"2\" to=\"3\"/>\n" +
            "  </connections>\n" +
            "</process>";
        builder.add( new ReaderResource( new StringReader( process )), ResourceType.DRF );
        
//      RuleBaseConfiguration config = new RuleBaseConfiguration();
//      config.setRuleBaseUpdateHandler(null);

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( builder.getKnowledgePackages() );
        KieSession session = kbase.newKieSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        List<String> list = new ArrayList<String>();
        session.setGlobal( "list", list );

        Person p = new Person( "bobba fet", 32);
        session.insert( p );
        ProcessInstance processInstance = ( ProcessInstance ) session.startProcess("org.test.ruleflow");
        
        assertEquals(1, session.getProcessInstances().size());
        
        String process2 = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow2\" package-name=\"org.test\" >\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "  </header>\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <workItem id=\"102\" name=\"Hello\" >\n" +
            "      <work name=\"Human Task\" >\n" +
            "      </work>\n" +
            "    </workItem>\n" +
            "    <actionNode id=\"4\" name=\"Action\" >" +
            "      <action type=\"expression\" dialect=\"java\">System.out.println();\n" +
            "list.add(\"Executed\");</action>\n" +
            "    </actionNode>\n" + 
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"102\"/>\n" +
            "    <connection from=\"102\" to=\"4\"/>\n" +
            "    <connection from=\"4\" to=\"3\"/>\n" +
            "  </connections>\n" +
            "</process>";
        builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( new ReaderResource( new StringReader( process2 )), ResourceType.DRF );
        kbase.addPackages( builder.getKnowledgePackages() );
        
        Map<String, Long> mapping = new HashMap<String, Long>();
        mapping.put("2", 102L);
        
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
            session, processInstance.getId(), "org.test.ruleflow2", mapping);
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        session.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        
        assertEquals(1, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    @Test
    public void testCompositeMappingUpgrade() throws Exception {
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

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add(new ByteArrayResource(rule.getBytes()), ResourceType.DRL);
        
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
            "    <composite id=\"2\" name=\"Composite\" >\n" +
            "      <nodes>\n" +
            "        <workItem id=\"1\" name=\"Hello\" >\n" +
            "          <work name=\"Human Task\" >\n" +
            "          </work>\n" +
            "        </workItem>\n" +
            "      </nodes>\n" +
            "      <connections>\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"1\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </composite>\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\"/>\n" +
            "    <connection from=\"2\" to=\"3\"/>\n" +
            "  </connections>\n" +
            "</process>";
        builder.add( new ReaderResource( new StringReader( process )), ResourceType.DRF );
        
//      RuleBaseConfiguration config = new RuleBaseConfiguration();
//      config.setRuleBaseUpdateHandler(null);

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( builder.getKnowledgePackages() );
        KieSession session = kbase.newKieSession();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        List<String> list = new ArrayList<String>();
        session.setGlobal( "list", list );

        Person p = new Person( "bobba fet", 32);
        session.insert( p );
        ProcessInstance processInstance = ( ProcessInstance ) session.startProcess("org.test.ruleflow");
        
        assertEquals(1, session.getProcessInstances().size());
        
        String process2 = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" +
            "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n" +
            "    type=\"RuleFlow\" name=\"ruleflow\" id=\"org.test.ruleflow2\" package-name=\"org.test\" >\n" +
            "  <header>\n" +
            "    <globals>\n" +
            "      <global identifier=\"list\" type=\"java.util.List\" />\n" +
            "    </globals>\n" +
            "  </header>\n" +
            "  <nodes>\n" +
            "    <start id=\"1\" name=\"Start\" />\n" +
            "    <composite id=\"2\" name=\"Composite\" >\n" +
            "      <nodes>\n" +
            "        <workItem id=\"101\" name=\"Hello\" >\n" +
            "          <work name=\"Human Task\" >\n" +
            "          </work>\n" +
            "        </workItem>\n" +
            "        <actionNode id=\"2\" name=\"Action\" >" +
            "          <action type=\"expression\" dialect=\"java\">System.out.println();\n" +
            "list.add(\"Executed\");</action>\n" +
            "        </actionNode>\n" + 
            "      </nodes>\n" +
            "      <connections>\n" +
            "        <connection from=\"101\" to=\"2\"/>\n" +
            "      </connections>\n" +
            "      <in-ports>\n" +
            "        <in-port type=\"DROOLS_DEFAULT\" nodeId=\"101\" nodeInType=\"DROOLS_DEFAULT\" />\n" +
            "      </in-ports>\n" +
            "      <out-ports>\n" +
            "        <out-port type=\"DROOLS_DEFAULT\" nodeId=\"2\" nodeOutType=\"DROOLS_DEFAULT\" />\n" +
            "      </out-ports>\n" +
            "    </composite>\n" +
            "    <end id=\"3\" name=\"End\" />\n" +
            "  </nodes>\n" +
            "  <connections>\n" +
            "    <connection from=\"1\" to=\"2\"/>\n" +
            "    <connection from=\"2\" to=\"3\"/>\n" +
            "  </connections>\n" +
            "</process>";
        builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.add( new ReaderResource( new StringReader( process2 )), ResourceType.DRF );
        kbase.addPackages( builder.getKnowledgePackages() );
        
        Map<String, Long> mapping = new HashMap<String, Long>();
        mapping.put("2:1", 101L);
        
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
            session, processInstance.getId(), "org.test.ruleflow2", mapping);
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        session.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        
        assertEquals(1, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
}
