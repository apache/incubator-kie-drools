package org.drools.agent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class KnowledgeAgentDSLTest extends BaseKnowledgeAgentTest {

    @Test
    public void testDSLAndIncrementalChangeSet() throws Exception {

        //create a basic dsl file
        this.fileManager.write("myExpander.dsl", this.createCommonDSL(null));

        //create a basic dslr file
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.dslr' type='DSLR' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/myExpander.dsl' type='DSL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        List<String> list = new ArrayList<String>();

        //Create a new Agent with newInstace=false
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase, false );

        //Agent: take care of them!
        this.applyChangeSet(kagent, ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal("list", list);
        ksession.insert(new Person());
        ksession.fireAllRules();

        //The rule should be fired
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule1"));

        list.clear();

        //the dsl is now modified.
        File expander = this.fileManager.write("myExpander.dsl", this.createCommonDSL("name == \"John\""));

        //We also need to mark the dslr file as modified, so the rules could
        //be regenerated
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        this.scan(kagent);
        
        ksession.insert(new Person());
        ksession.fireAllRules();

        //The rule was modified then no rule got fired.
        assertEquals(0, list.size());

        Person p = new Person();
        p.setName("John");
        ksession.insert(p);
        ksession.fireAllRules();

        //The new fact activated and fired the modified rule
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule1"));

        //let's add a new rule
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule(new String[]{"Rule1","Rule2"}));

        this.scan(kagent);

        list.clear();
        ksession.fireAllRules();

        //because we already had a John inserted, Rule2 gets fired.
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule2"));

        //let's remove Rule1 and Rule2 and add a new rule: Rule3
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule3"));

        this.scan(kagent);
        
        //we want to use a new ksession.
        ksession.dispose();
        ksession = kbase.newStatefulKnowledgeSession();

        //insert John
        list.clear();
        ksession.setGlobal("list", list);
        ksession.insert(p);
        ksession.fireAllRules();

        //The only existing rule must be fired
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule3"));

        //let's delete the dsl file (errors are expected)
        this.fileManager.deleteFile(expander);

        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));
        
        try {
            this.scan(kagent);
            fail( "Knowledge should fail to compile" );
        } catch (Exception e) {
            
        }

        ksession.dispose();
        kagent.dispose();
    }

    @Test
    public void testDSLAndNewInstance() throws Exception {

        //create a basic dsl file
        this.fileManager.write("myExpander.dsl", this.createCommonDSL(null));

        //create a basic dslr file
        this.fileManager.write("rules.dslr",this.createCommonDSLRRule("Rule1"));

        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules.dslr' type='DSLR' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/myExpander.dsl' type='DSL' />";
        xml += "    </add> ";
        xml += "</change-set>";
        File fxml = fileManager.write( "changeset.xml",
                                       xml );

        List<String> list = new ArrayList<String>();

        //Create a new Agent with newInstace=true
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent(kbase,true);

        //Agent: take care of them!
        this.applyChangeSet(kagent,ResourceFactory.newUrlResource(fxml.toURI().toURL()));
        StatefulKnowledgeSession ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

        ksession.setGlobal("list", list);
        ksession.insert(new Person());
        ksession.fireAllRules();

        //The rule should be fired
        assertEquals(1, list.size());
        assertTrue(list.contains("Rule1"));
        list.clear();

        ksession.dispose();

        //Let's modify the dsl file
        File expander = this.fileManager.write("myExpander.dsl", this.createCommonDSL("name == \"John\""));

        //We also need to mark the dslr file as modified, so the rules could
        //be regenerated
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        this.scan(kagent);

        //get a new ksession
        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

        //A Person without name shouldn't fire any rule now (because it was
        //modified)
        ksession.setGlobal("list", list);
        ksession.insert(new Person());
        ksession.fireAllRules();

        assertEquals(0, list.size());

        //A "John" Person should fire the modified rule
        Person p = new Person();
        p.setName("John");
        ksession.insert(p);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains("Rule1"));

        ksession.dispose();

        //Let's add a new Rule
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule(new String[]{"Rule1","Rule2"}));

        this.scan(kagent);
        
        //get a new ksession
        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

        //A "John" Person now should fire 2 rules
        list.clear();
        ksession.setGlobal("list", list);
        ksession.insert(p);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("Rule1"));
        assertTrue(list.contains("Rule2"));

        ksession.dispose();

        //Let's remove both rules and add a new one: Rule3
        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule3"));

        this.scan(kagent);
        
        //get a new ksession
        ksession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();

        //A "John" Person now should only Rule3 (the other two rules were removes)
        list.clear();
        ksession.setGlobal("list", list);
        ksession.insert(p);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertTrue(list.contains("Rule3"));

        ksession.dispose();

        //let's delete the dsl file (errors are expected)
        fileManager.deleteFile(expander);

        this.fileManager.write("rules.dslr", this.createCommonDSLRRule("Rule1"));

        try {
            this.scan(kagent);
            //Compilation errors should occurred
            fail( "Knowledge should fail to compile" );
        } catch (Exception e) {
            
        }

        
        kagent.dispose();
    }
}
