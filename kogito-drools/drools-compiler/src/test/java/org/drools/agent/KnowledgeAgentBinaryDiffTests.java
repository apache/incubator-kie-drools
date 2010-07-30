package org.drools.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.core.util.FileManager;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.drools.rule.Rule;
import org.drools.runtime.StatefulKnowledgeSession;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

public class KnowledgeAgentBinaryDiffTests extends TestCase {

    FileManager fileManager;
    private Server server;

    @Override
    protected void setUp() throws Exception {
        fileManager = new FileManager();
        fileManager.setUp();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();
        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();

        this.server = new Server(0);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(fileManager.getRootDirectory().getPath());
        System.out.println("root : " + fileManager.getRootDirectory().getPath());

        server.setHandler(resourceHandler);

        server.start();

        System.out.println("Server running on port "+this.getPort());
    }

    private int getPort(){
        return this.server.getConnectors()[0].getLocalPort();
    }

    @Override
    protected void tearDown() throws Exception {
        fileManager.tearDown();
        ResourceFactory.getResourceChangeNotifierService().stop();
        ResourceFactory.getResourceChangeScannerService().stop();
        ((ResourceChangeNotifierImpl) ResourceFactory.getResourceChangeNotifierService()).reset();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();

        server.stop();
    }

    public void testDifferentDateExpires() throws Exception {

        final String attribute1 = "date-expires \"4-jan-2010\"";
        final String attribute2 = "date-expires \"5-jan-2010\"";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals(2010, rule.getDateExpires().get(GregorianCalendar.YEAR));
                    assertEquals(GregorianCalendar.JANUARY, rule.getDateExpires().get(GregorianCalendar.MONTH));
                    assertEquals(4, rule.getDateExpires().get(GregorianCalendar.DAY_OF_MONTH));
                }else if (attribute.equals(attribute2)){
                    assertEquals(2010, rule.getDateExpires().get(GregorianCalendar.YEAR));
                    assertEquals(GregorianCalendar.JANUARY, rule.getDateExpires().get(GregorianCalendar.MONTH));
                    assertEquals(5, rule.getDateExpires().get(GregorianCalendar.DAY_OF_MONTH));
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }

    public void testDifferentDateEffective() throws Exception {

        final String attribute1 = "date-effective \"4-jan-2010\"";
        final String attribute2 = "date-effective \"5-jan-2010\"";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals(2010, rule.getDateEffective().get(GregorianCalendar.YEAR));
                    assertEquals(GregorianCalendar.JANUARY, rule.getDateEffective().get(GregorianCalendar.MONTH));
                    assertEquals(4, rule.getDateEffective().get(GregorianCalendar.DAY_OF_MONTH));
                }else if (attribute.equals(attribute2)){
                    assertEquals(2010, rule.getDateEffective().get(GregorianCalendar.YEAR));
                    assertEquals(GregorianCalendar.JANUARY, rule.getDateEffective().get(GregorianCalendar.MONTH));
                    assertEquals(5, rule.getDateEffective().get(GregorianCalendar.DAY_OF_MONTH));
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }


    public void testDifferentDialect() throws Exception {

        final String attribute1 = "dialect \"java\"";
        final String attribute2 = "dialect \"mvel\"";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals("java", rule.getDialect());
                }else if (attribute.equals(attribute2)){
                    assertEquals("mvel", rule.getDialect());
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }

    public void testDifferentRuleFlowGroup() throws Exception {

        final String attribute1 = "ruleflow-group \"g1\"";
        final String attribute2 = "ruleflow-group \"g2\"";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals("g1", rule.getRuleFlowGroup());
                }else if (attribute.equals(attribute2)){
                    assertEquals("g2", rule.getRuleFlowGroup());
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }

    public void testDifferentAutoFocus() throws Exception {

        final String attribute1 = "auto-focus false";
        final String attribute2 = "auto-focus true";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals(false, rule.getAutoFocus());
                }else if (attribute.equals(attribute2)){
                    assertEquals(true, rule.getAutoFocus());
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }

    public void testDifferentAgendaGroup() throws Exception {

        final String attribute1 = "agenda-group \"g1\"";
        final String attribute2 = "agenda-group \"g2\"";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals("g1", rule.getAgendaGroup());
                }else if (attribute.equals(attribute2)){
                    assertEquals("g2", rule.getAgendaGroup());
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }

    public void testDifferentLockOnActive() throws Exception {

        final String attribute1 = "lock-on-active false";
        final String attribute2 = "lock-on-active true";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals(false, rule.isLockOnActive());
                }else if (attribute.equals(attribute2)){
                    assertEquals(true, rule.isLockOnActive());
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }

    public void testDifferentNoLoop() throws Exception {

        final String attribute1 = "no-loop false";
        final String attribute2 = "no-loop true";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals(false, rule.isNoLoop());
                }else if (attribute.equals(attribute2)){
                    assertEquals(true, rule.isNoLoop());
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }

    public void testDifferentActivationGroup() throws Exception {

        final String attribute1 = "activation-group \"1\"";
        final String attribute2 = "activation-group \"2\"";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals("1", rule.getActivationGroup());
                }else if (attribute.equals(attribute2)){
                    assertEquals("2", rule.getActivationGroup());
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }
    

    public void testDifferentSalience() throws Exception {

        final String attribute1 = "salience 1";
        final String attribute2 = "salience 2";

        RuleAttributeAsserter asserter = new RuleAttributeAsserter() {

            public void assertRuleAttribute(String attribute, Rule rule) {
                if (attribute.equals(attribute1)){
                    assertEquals("1", rule.getSalience().toString());
                }else if (attribute.equals(attribute2)){
                    assertEquals("2", rule.getSalience().toString());
                }else{
                    throw new IllegalArgumentException("Unexpected attribute "+attribute);
                }
            }
        };

        this.differentRuleAttributeTest(attribute1, attribute2, asserter);

    }

    public void testDifferentLHS() throws Exception {

        String header1 = "";
        header1 += "package org.drools.test\n";
        header1 += "global java.util.List list\n\n";

        String rule1 = this.createCommonRule("rule1");


        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(header1);
        output.write(rule1);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule1"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        //String rule1v3 = this.createCommonRule("rule1","3");
        String rule1v2 = "";
        rule1v2 += "rule rule1\n";
        rule1v2 += "when\n";
        rule1v2 += "\tString()\n";
        rule1v2 += "then\n";
        rule1v2 += "list.add( drools.getRule().getName()+\"-V2\");\n";
        rule1v2 += "end\n";

        output = new BufferedWriter(new FileWriter(f1));
        output.write(header1);
        output.write(rule1v2);
        output.close();
        Thread.sleep(3000);

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.insert("Some String");
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule1-V2"));

        kagent.monitorResourceChangeEvents(false);
    }
    
    
    public void testDifferentConsequences() throws Exception {

        String header1 = "";
        header1 += "package org.drools.test\n";
        header1 += "global java.util.List list\n\n";

        String rule1 = this.createCommonRule("rule1");


        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(header1);
        output.write(rule1);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule1"));

        list.clear();

        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        String rule1v2 = this.createCommonRule("rule1", "2");

        output = new BufferedWriter(new FileWriter(f1));
        output.write(header1);
        output.write(rule1v2);
        output.close();
        Thread.sleep(3000);

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.insert("Some String");
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule1-V2"));

        kagent.monitorResourceChangeEvents(false);
    }






//

    private void differentRuleAttributeTest(String attribute1, String attribute2,RuleAttributeAsserter asserter) throws Exception {

        String header1 = "";
        header1 += "package org.drools.test\n";
        header1 += "global java.util.List list\n\n";

        String rule1 = "";
        rule1 += "rule rule1\n";
        rule1 += attribute1+"\n";
        rule1 += "when\n";
        rule1 += "\tString()\n";
        rule1 += "then\n";
        rule1 += "list.add( drools.getRule().getName());\n";
        rule1 += "end\n";


        File f1 = fileManager.newFile("rule1.drl");
        Writer output = new BufferedWriter(new FileWriter(f1));
        output.write(header1);
        output.write(rule1);
        output.close();

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.newFile("changeset.xml");
        output = new BufferedWriter(new FileWriter(fxml));
        output.write(xml);
        output.close();

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase);

        kagent.applyChangeSet(ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        org.drools.rule.Rule rule = (org.drools.rule.Rule) kagent.getKnowledgeBase().getRule("org.drools.test", "rule1");

        assertNotNull(rule);
        asserter.assertRuleAttribute(attribute1, rule);


        // have to sleep here as linux lastModified does not do milliseconds
        // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
        Thread.sleep(2000);

        String rule1v2 = "";
        rule1v2 += "rule rule1\n";
        rule1v2 += attribute2+"\n";
        rule1v2 += "when\n";
        rule1v2 += "\tString()\n";
        rule1v2 += "then\n";
        rule1v2 += "list.add( drools.getRule().getName());\n";
        rule1v2 += "end\n";

        output = new BufferedWriter(new FileWriter(f1));
        output.write(header1);
        output.write(rule1v2);
        output.close();
        Thread.sleep(3000);

        rule = (org.drools.rule.Rule) kagent.getKnowledgeBase().getRule("org.drools.test", "rule1");
        assertNotNull(rule);
        asserter.assertRuleAttribute(attribute2, rule);

        kagent.monitorResourceChangeEvents(false);
    }

    private KnowledgeAgent createKAgent(KnowledgeBase kbase) {
        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        sconf.setProperty("drools.resource.scanner.interval", "2");
        ResourceFactory.getResourceChangeScannerService().configure(sconf);

        //System.setProperty(KnowledgeAgentFactory.PROVIDER_CLASS_NAME_PROPERTY_NAME, "org.drools.agent.impl.KnowledgeAgentProviderImpl");

        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty("drools.agent.scanDirectories", "true");
        aconf.setProperty("drools.agent.scanResources", "true");
        // Testing incremental build here
        aconf.setProperty("drools.agent.newInstance", "false");



        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "test agent", kbase, aconf);

        assertEquals("test agent", kagent.getName());

        return kagent;
    }

    private String createCommonRule(String ruleName) {
        StringBuilder sb = new StringBuilder();
        sb.append("rule ");
        sb.append(ruleName);
        sb.append("\n");
        sb.append("when\n");
        sb.append("then\n");
        sb.append("list.add( drools.getRule().getName() );\n");
        sb.append("end\n");

        return sb.toString();
    }    

    private String createCommonRule(String ruleName, String version) {
        StringBuilder sb = new StringBuilder();
        sb.append("rule ");
        sb.append(ruleName);
        sb.append("\n");
        sb.append("when\n");
        sb.append("then\n");
        sb.append("list.add( drools.getRule().getName()+\"-V" + version + "\");\n");
        sb.append("end\n");

        return sb.toString();
    }

    
}

interface RuleAttributeAsserter{
    void assertRuleAttribute(String attribute, org.drools.rule.Rule rule);
}