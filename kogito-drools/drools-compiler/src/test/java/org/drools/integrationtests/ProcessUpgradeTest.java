package org.drools.integrationtests;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.process.instance.ProcessInstance;
import org.drools.rule.Package;
import org.drools.workflow.instance.WorkflowProcessInstanceUpgrader;

public class ProcessUpgradeTest extends TestCase {
    
    public void testUpgrade() throws Exception {
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

        List list = new ArrayList();
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
            session, processInstance.getId(), "org.test.ruleflow2", new HashMap<Long, Long>());
        assertEquals("org.test.ruleflow2", processInstance.getProcessId());
        
        session.fireAllRules();
        
        assertEquals(2, list.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

}
