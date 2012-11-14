package org.jbpm.integrationtests;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ReaderResource;
import org.kie.runtime.StatefulKnowledgeSession;
import org.jbpm.JbpmTestCase;
import org.jbpm.Person;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstanceUpgrader;

public class ProcessUpgradeTest extends JbpmTestCase {
    
    public void testDefaultUpgrade() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.jbpm.Person\n";
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
            "    <ruleSet id=\"2\" name=\"Hello\" ruleFlowGroup=\"hello\" />\n" +
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

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( builder.getKnowledgePackages() );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

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
            "    <ruleSet id=\"2\" name=\"Hello\" ruleFlowGroup=\"hello\" />\n" +
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
        kbase.addKnowledgePackages( builder.getKnowledgePackages() );
        
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
            session, processInstance.getId(), "org.test.ruleflow2", new HashMap<String, Long>());
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        session.fireAllRules();
        
        assertEquals(2, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    public void testMappingUpgrade() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.jbpm.Person\n";
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
            "    <ruleSet id=\"2\" name=\"Hello\" ruleFlowGroup=\"hello\" />\n" +
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

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( builder.getKnowledgePackages() );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

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
            "    <ruleSet id=\"102\" name=\"Hello\" ruleFlowGroup=\"hello\" />\n" +
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
        kbase.addKnowledgePackages( builder.getKnowledgePackages() );
        
        Map<String, Long> mapping = new HashMap<String, Long>();
        mapping.put("2", 102L);
        
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
            session, processInstance.getId(), "org.test.ruleflow2", mapping);
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        session.fireAllRules();
        
        assertEquals(2, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testCompositeMappingUpgrade() throws Exception {
        String rule = "package org.test;\n";
        rule += "import org.jbpm.Person\n";
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
            "        <ruleSet id=\"1\" name=\"Hello\" ruleFlowGroup=\"hello\" />\n" +
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

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( builder.getKnowledgePackages() );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

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
            "        <ruleSet id=\"101\" name=\"Hello\" ruleFlowGroup=\"hello\" />\n" +
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
        kbase.addKnowledgePackages( builder.getKnowledgePackages() );
        
        Map<String, Long> mapping = new HashMap<String, Long>();
        mapping.put("2:1", 101L);
        
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
            session, processInstance.getId(), "org.test.ruleflow2", mapping);
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        session.fireAllRules();
        
        assertEquals(2, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
}
