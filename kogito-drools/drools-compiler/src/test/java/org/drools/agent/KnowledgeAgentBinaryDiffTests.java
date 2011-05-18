package org.drools.agent;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.io.ResourceFactory;
import org.drools.rule.Rule;
import org.drools.runtime.StatefulKnowledgeSession;

import org.junit.Test;

import static org.junit.Assert.*;

public class KnowledgeAgentBinaryDiffTests extends BaseKnowledgeAgentTest {

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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
    
    @Test
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

    @Test
    public void testDifferentLHS() throws Exception {
        File f1 = fileManager.write( "rule1.drl",
                                     createDefaultRule( "rule1" ) );


        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase, false);
        
        applyChangeSet( kagent, ResourceFactory.newUrlResource(fxml.toURI().toURL()) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule1"));

        list.clear();
        
        File f2 = fileManager.write( "rule1.drl",
                                     createVersionedRule( "rule1", "2" ) );
        
        scan(kagent);

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.insert("Some String");
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule1-V2"));

        kagent.dispose();
    }
    
    @Test
    public void testDifferentConsequences() throws Exception {

        File f1 = fileManager.write( "rule1.drl",
                                     createDefaultRule( "rule1" ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase, false);
        
        applyChangeSet( kagent, ResourceFactory.newUrlResource(fxml.toURI().toURL()) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule1"));

        list.clear();

        fileManager.write( "rule1.drl",
                           createVersionedRule( "rule1", "2" ) );
        
        scan( kagent );

        // Use the same session for incremental build test
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.insert("Some String");
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertTrue(list.contains("rule1-V2"));

        kagent.dispose();
    }

    @Test
    public void testEvalCondition() throws Exception{

        String rule1 = "";
        rule1 += "package org.drools\n";
        rule1 += "global Integer salesChannelId;\n";
        rule1 += "global Boolean includeFinishing;\n";
        rule1 += "global java.util.List list;\n";
        rule1 += "rule \"Rule A\"\n";
        rule1 += "  dialect \"java\"\n";
        rule1 += "  ruleflow-group \"finishing-price\"\n";
        rule1 += "  when\n";
        rule1 += "      $s : String()\n";
        rule1 += "      eval(salesChannelId @operator@ 4 && includeFinishing)\n";
        rule1 += "  then\n";
        rule1 += "      list.add(\"@message@\");\n";
        rule1 += "  end\n";
        
        fileManager.write( "rule1.drl",
                                       rule1.replaceAll("@message@", "Rule A fired!").replaceAll("@operator@", "!=") );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase, false);
        
        applyChangeSet( kagent, ResourceFactory.newUrlResource(fxml.toURI().toURL()) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.setGlobal("salesChannelId", 1);
        ksession.setGlobal("includeFinishing", true);
        ksession.insert("Some String");
        ksession.fireAllRules();
        ksession.dispose();

        //assertEquals(1, list.size());
        //assertTrue(list.contains("Rule A fired!"));

        list.clear();
        
        
        fileManager.write( "rule1.drl",
                                     rule1.replaceAll("@message@", "Rule A V2 fired!").replaceAll("@operator@", "==") );
        
        scan(kagent);

        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.setGlobal("salesChannelId", 4);
        ksession.setGlobal("includeFinishing", true);
        ksession.insert("Some String");
        ksession.fireAllRules();
        ksession.dispose();

        //assertEquals(1, list.size());
        //assertTrue(list.contains("Rule A V2 fired!"));

        kagent.dispose();
    }
    
    @Test
    public void testEvalConditionOnBinaryPackages() throws Exception{

        String rule1 = "";
        rule1 += "package org.drools\n";
        rule1 += "global Integer salesChannelId;\n";
        rule1 += "global Boolean includeFinishing;\n";
        rule1 += "global java.util.List list;\n";
        rule1 += "rule \"Rule A\"\n";
        rule1 += "  dialect \"java\"\n";
        rule1 += "  ruleflow-group \"finishing-price\"\n";
        rule1 += "  when\n";
        rule1 += "      $s : String()\n";
        rule1 += "      eval(salesChannelId @operator@ 4 && includeFinishing)\n";
        rule1 += "  then\n";
        rule1 += "      list.add(\"@message@\");\n";
        rule1 += "  end\n";
        //Create an empty file
        File ruleFile = fileManager.write( "rule1.pkg","");
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule1.replaceAll("@message@", "Rule A fired!").replaceAll("@operator@", "!=").getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgePackage pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg, ruleFile );
        
        
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.pkg' type='PKG' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase, false);
        
        applyChangeSet( kagent, ResourceFactory.newUrlResource(fxml.toURI().toURL()) );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ksession.setGlobal("salesChannelId", 1);
        ksession.setGlobal("includeFinishing", true);
        ksession.insert("Some String");
        ksession.fireAllRules();
        ksession.dispose();

        //assertEquals(1, list.size());
        //assertTrue(list.contains("Rule A fired!"));

        list.clear();
        
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(rule1.replaceAll("@message@", "Rule A V2 fired!").replaceAll("@operator@", "==").getBytes() ),
                      ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        pkg = (KnowledgePackage) kbuilder.getKnowledgePackages().iterator().next();
        writePackage( pkg, ruleFile );
        
        scan(kagent);

        ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("list", list);
        ksession.setGlobal("salesChannelId", 4);
        ksession.setGlobal("includeFinishing", true);
        ksession.insert("Some String");
        ksession.fireAllRules();
        ksession.dispose();

        //assertEquals(1, list.size());
        //assertTrue(list.contains("Rule A V2 fired!"));

        kagent.dispose();
    }
    
    private void differentRuleAttributeTest(String attribute1, String attribute2,RuleAttributeAsserter asserter) throws Exception {
        File f1 = fileManager.write( "rule1.drl",
                                     createAttributeRule( "rule1", attribute1 ) );

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:"+this.getPort()+"/rule1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase, false);

        applyChangeSet( kagent, ResourceFactory.newUrlResource(fxml.toURI().toURL()));

        org.drools.rule.Rule rule = (org.drools.rule.Rule) kagent.getKnowledgeBase().getRule("org.drools.test", "rule1");

        assertNotNull(rule);
        asserter.assertRuleAttribute(attribute1, rule);

        File f2 = fileManager.write( "rule1.drl",
                                     createAttributeRule( "rule1", attribute2 ) );
        
        scan( kagent );
        
        rule = (org.drools.rule.Rule) kagent.getKnowledgeBase().getRule("org.drools.test", "rule1");
        assertNotNull(rule);
        asserter.assertRuleAttribute(attribute2, rule);

        kagent.dispose();
    }
    
}

interface RuleAttributeAsserter{
    void assertRuleAttribute(String attribute, org.drools.rule.Rule rule);
}
