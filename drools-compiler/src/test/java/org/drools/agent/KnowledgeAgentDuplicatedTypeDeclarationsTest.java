package org.drools.agent;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.ResourceFactory;
import org.drools.rule.Rule;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;

public class KnowledgeAgentDuplicatedTypeDeclarationsTest extends BaseKnowledgeAgentTest {

    @Test
    public void testDuplicatedDeclarationInOneChangeSet() throws Exception {
        //create a basic drl file with a type declaration and a rule
        String drl1 = this.createCommonDeclaration("ClassA", null, new String[]{"field1 : String"});
        drl1+="\n"+this.createCustomRule(false , null, new String[]{"ruleA"}, null, "$a : ClassA( field1 == \"foo\")\n", "list.add($a.getField1());\n");

        this.fileManager.write("rules1.drl", drl1);

        //create another drl with the same type declaration and another rule
        String drl2 = this.createCommonDeclaration("ClassA", null, new String[]{"field1 : String"});
        drl2+="\n"+this.createCustomRule(false , null, new String[]{"\"ruleA Insert\""}, null, "\n", "ClassA a = new ClassA();\na.setField1(\"foo\");\ninsert(a);\n");
        this.fileManager.write("rules2.drl", drl2);

        System.out.println("\n\n\n");
        System.out.println(drl1);
        System.out.println("\n\n\n");
        System.out.println(drl2);
        System.out.println("\n\n\n");

        //Add the 2 resources into a change-set
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules1.drl' type='DRL' />";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        List<String> list = new ArrayList<String>();

        //Create a new Agent with newInstance=false
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase, false );

        //Agent: take care of them!
        this.applyChangeSet(kagent, ResourceFactory.newByteArrayResource(xml.getBytes()));
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("foo", list.get(0));

        ksession.dispose();
        kagent.dispose();
    }


    @Test
    public void testDuplicatedDeclarationInMultipleChangeSets() throws Exception {
        //create a basic drl file with a type declaration and a rule
        String drl1 = this.createCommonDeclaration("ClassA", null, new String[]{"field1 : String"});
        drl1+="\n"+this.createCustomRule(false , null, new String[]{"\"ruleA Insert\""}, null, "\n", "System.out.println(\"Firing 'ruleA Insert'\");\nClassA a = new ClassA();\na.setField1(\"foo\");\ninsert(a);\n");


        this.fileManager.write("rules1.drl", drl1);

        //Add the 2 resources into a change-set
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        List<String> list = new ArrayList<String>();

        //Create a new Agent with newInstance=false
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase, false, false );

        kagent.setSystemEventListener(new PrintStreamSystemEventListener(System.out));

        //Agent: take care of them!
        this.applyChangeSet(kagent, ResourceFactory.newByteArrayResource(xml.getBytes()));

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        Assert.assertTrue(list.isEmpty());

        //create a second drl also containing a declaration of ClassA
        String drl2 = this.createCommonDeclaration("ClassA", null, new String[]{"field1 : String"});
        drl2+="\n"+this.createCustomRule(false , null, new String[]{"ruleA"}, null, "$a : ClassA( field1 == \"foo\")\n", "list.add($a.getField1());\n");

        this.fileManager.write("rules2.drl", drl2);

        System.out.println("\n\n\n----------------\n\n\n");

        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        //Agent: take care of them!
        this.applyChangeSet(kagent, ResourceFactory.newByteArrayResource(xml.getBytes()));

        Rule[] rules = ((KnowledgeBaseImpl)kagent.getKnowledgeBase()).getRuleBase().getPackages()[0].getRules();
        for (Rule rule : rules) {
            System.out.println("\t"+rule.getName());
        }


        ksession.fireAllRules();

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("foo", list.get(0));

        ksession.dispose();
        kagent.dispose();
    }

    @Test
    public void testDuplicatedDeclarationInMultipleChangeSets2() throws Exception {
        //create a basic drl file with a type declaration and a rule
        String drl1 = this.createCommonDeclaration("ClassA", null, new String[]{"field1 : String"});
        drl1+="\n"+this.createCustomRule(false , null, new String[]{"ruleA"}, null, "$a : ClassA( field1 == \"foo\")\n", "list.add($a.getField1());\n");

        this.fileManager.write("rules1.drl", drl1);

        //Add the 2 resources into a change-set
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        List<String> list = new ArrayList<String>();

        //Create a new Agent with newInstance=false
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase, false );

        kagent.setSystemEventListener( new PrintStreamSystemEventListener( System.out ) );

        //Agent: take care of them!
        this.applyChangeSet( kagent, ResourceFactory.newByteArrayResource(xml.getBytes() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        Assert.assertTrue( list.isEmpty() );

        //create a second drl also containing a declaration of ClassA
        String drl2 = this.createCommonDeclaration("ClassA", null, new String[]{"field1 : String"});
        drl2+="\n"+this.createCustomRule(false , null, new String[]{"\"ruleA Insert\""}, null, "\n", "System.out.println(\"Firing 'ruleA Insert'\");\nClassA a = new ClassA();\na.setField1(\"foo\");\ninsert(a);\n");

        this.fileManager.write("rules2.drl", drl2);

        System.out.println("\n\n\n----------------\n\n\n");

        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        //Agent: take care of them!
        this.applyChangeSet(kagent, ResourceFactory.newByteArrayResource(xml.getBytes()));

        Rule[] rules = ( (KnowledgeBaseImpl) kagent.getKnowledgeBase() ).getRuleBase().getPackages()[0].getRules();
        for ( Rule rule : rules ) {
            System.out.println("\t"+rule.getName());
        }


        ksession.fireAllRules();

        Assert.assertEquals( 1, list.size() );
        Assert.assertEquals( "foo", list.get( 0 ) );

        ksession.dispose();
        kagent.dispose();
    }




    @Test
    public void testLaterDeclarationAsEvent() throws Exception {
        //create a basic drl file with a type declaration
        String drl1 = "package org.drools.test1; \n" +
                "\n" +
                "declare Bean \n" +
                "  id : String \n" +
                "end";

        this.fileManager.write("rules1.drl", drl1);

        //Add the 2 resources into a change-set
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        //Create a new Agent with newInstance=false
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase, false );

        List<String> list = new ArrayList<String>();

        kagent.setSystemEventListener( new PrintStreamSystemEventListener( System.out ) );

        //Agent: take care of them!
        this.applyChangeSet( kagent, ResourceFactory.newByteArrayResource( xml.getBytes() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );


        ksession.fireAllRules();





        //create a second drl also containing a declaration of ClassA
        String drl2 = "package org.drools.test2;\n" +
                "\n" +
//                "import org.drools.test1.Bean;\n" +
                "import org.drools.Person;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare Person\n" +
                "@role(event)\n" +
                "end\n" +
                "\n" +
                "declare Bean2\n" +
                "  id : String\n" +
                "end\n" +
                "\n" +
                "rule \"Data\"\n" +
                "when\n" +
                "  $b : Person()\n" +
                "  not  Bean2( this before $b )\n" +
                "then\n" +
                "  list.add(\"foo\");\n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Person( \"x\" ) );\n" +
                "end";

        this.fileManager.write("rules2.drl", drl2);

        System.out.println("\n\n\n----------------\n\n\n");
//
        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        //Agent: take care of them!
        this.applyChangeSet(kagent, ResourceFactory.newByteArrayResource(xml.getBytes()));

        Rule[] rules = ( (KnowledgeBaseImpl) kagent.getKnowledgeBase() ).getRuleBase().getPackages()[0].getRules();
        for ( Rule rule : rules ) {
            System.out.println("\t"+rule.getName());
        }
        ksession.setGlobal( "list", list);

        ksession.fireAllRules();

        Assert.assertEquals( 1, list.size() );
        Assert.assertEquals( "foo", list.get( 0 ) );

        ksession.dispose();
        kagent.dispose();
    }





    @Test
    public void testLaterRedeclarationAsEvent() throws Exception {
        //create a basic drl file with a type declaration
        String drl1 = "package org.drools.test1; \n" +
                "\n" +
                "declare Bean \n" +
                "  id : String \n" +
                "end";

        this.fileManager.write("rules1.drl", drl1);

        //Add the 2 resources into a change-set
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules1.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        //Create a new Agent with newInstance=false
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgent kagent = this.createKAgent( kbase, false, true );

        List<String> list = new ArrayList<String>();

        kagent.setSystemEventListener( new PrintStreamSystemEventListener( System.out ) );

        //Agent: take care of them!
        this.applyChangeSet( kagent, ResourceFactory.newByteArrayResource( xml.getBytes() ) );

        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );


        ksession.fireAllRules();





        //create a second drl also containing a declaration of ClassA
        String drl2 = "package org.drools.test2;\n" +
                "\n" +
                "import org.drools.test1.Bean;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare Bean\n" +
                "@role(event)\n" +
                "end\n" +
                "\n" +
                "declare Bean2\n" +
                "  id : String\n" +
                "end\n" +
                "\n" +
                "rule \"Data\"\n" +
                "when\n" +
                "  $b : Bean()\n" +
                "  not  Bean2( this before $b )\n" +
                "then\n" +
                "  list.add(\"foo\");\n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Bean( \"x\" ) );\n" +
                "end";

        this.fileManager.write("rules2.drl", drl2);

        System.out.println("\n\n\n----------------\n\n\n");
        //
        xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='http://localhost:" + this.getPort() + "/rules2.drl' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";

        //Agent: take care of them!
        this.applyChangeSet(kagent, ResourceFactory.newByteArrayResource(xml.getBytes()));

        Rule[] rules = ( (KnowledgeBaseImpl) kagent.getKnowledgeBase() ).getRuleBase().getPackages()[0].getRules();
        for ( Rule rule : rules ) {
            System.out.println("\t"+rule.getName());
        }
        ksession.setGlobal( "list", list);

        ksession.fireAllRules();

        Assert.assertEquals( 1, list.size() );
        Assert.assertEquals( "foo", list.get( 0 ) );

        ksession.dispose();
        kagent.dispose();
    }


}
