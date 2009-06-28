package org.drools.integrationtests;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.io.impl.ByteArrayResource;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.workflow.instance.WorkflowProcessInstanceUpgrader;

public class ProcessUpgradeTest extends TestCase {
    
    public void testDefaultUpgrade() throws Exception {
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

        PackageBuilder builder = new PackageBuilder();
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
        Package pkg = builder.getPackage();

        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setRuleBaseUpdateHandler(null);
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase(config);
        ruleBase.addPackage(pkg);

        StatefulSession session = ruleBase.newStatefulSession();

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
        builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process2 ));
        pkg = builder.getPackage();
        ruleBase.addPackage(pkg);
        
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
            session, processInstance.getId(), "org.test.ruleflow2", new HashMap<String, Long>());
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        session.fireAllRules();
        
        assertEquals(2, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    public void testMappingUpgrade() throws Exception {
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

        PackageBuilder builder = new PackageBuilder();
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
        Package pkg = builder.getPackage();

        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setRuleBaseUpdateHandler(null);
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase(config);
        ruleBase.addPackage(pkg);

        StatefulSession session = ruleBase.newStatefulSession();

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
        builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process2 ));
        pkg = builder.getPackage();
        ruleBase.addPackage(pkg);
        
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
        rule += "import org.drools.Person\n";
        rule += "global java.util.List list\n";
        rule += "rule \"Rule 1\"\n";
        rule += "  ruleflow-group \"hello\"\n";
        rule += "when\n";
        rule += "    $p : Person( ) \n";
        rule += "then\n";
        rule += "    list.add( $p );\n";
        rule += "end";

        PackageBuilder builder = new PackageBuilder();
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
        builder.addProcessFromXml( new StringReader( process ));
        Package pkg = builder.getPackage();

        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setRuleBaseUpdateHandler(null);
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase(config);
        ruleBase.addPackage(pkg);

        StatefulSession session = ruleBase.newStatefulSession();

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
        builder = new PackageBuilder();
        builder.addProcessFromXml( new StringReader( process2 ));
        pkg = builder.getPackage();
        ruleBase.addPackage(pkg);
        
        Map<String, Long> mapping = new HashMap<String, Long>();
        mapping.put("2:1", 101L);
        
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
            session, processInstance.getId(), "org.test.ruleflow2", mapping);
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        session.fireAllRules();
        
        assertEquals(2, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testDefaultUpgrade2() throws Exception {
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ByteArrayResource(rule.getBytes()), ResourceType.DRL);
        
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
        kbuilder.add(new ByteArrayResource(process.getBytes()), ResourceType.DRF);
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        Person p = new Person( "bobba fet", 32);
        ksession.insert( p );
        org.drools.runtime.process.ProcessInstance processInstance = ksession.startProcess("org.test.ruleflow");
        
        assertEquals(1, ksession.getProcessInstances().size());
        
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
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ByteArrayResource(process2.getBytes()), ResourceType.DRF);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
            ksession, processInstance.getId(), "org.test.ruleflow2", new HashMap<String, Long>());
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        ksession.fireAllRules();
        
        assertEquals(2, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
}
