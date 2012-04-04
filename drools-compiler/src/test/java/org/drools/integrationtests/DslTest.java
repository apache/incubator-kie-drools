package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.Cheese;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.lang.Expander;
import org.drools.lang.dsl.DefaultExpanderResolver;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;

public class DslTest extends CommonTestMethodBase {
   
    @Test
    public void testMultiLineTemplates() throws Exception {
        final Reader source = new InputStreamReader( getClass().getResourceAsStream( "rule_with_expander_multiline.dslr" ) );
        final Reader dsl = new InputStreamReader( getClass().getResourceAsStream( "test_dsl_multiline.dsl" ) );
        Expander ex =  new DefaultExpanderResolver(dsl).get("*", null);
        String r = ex.expand(source);
        assertEquals("when Car(color==\"Red\") then doSomething();", r.trim());
    }

    @Test
    public void testWithExpanderDSL() throws Exception {
        //final PackageBuilder builder = new PackageBuilder();
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "test_expander.dsl", getClass() ),
                              ResourceType.DSL );
        kbuilder.add( ResourceFactory.newClassPathResource( "rule_with_expander_dsl.dslr", getClass() ) ,
                              ResourceType.DSLR );

        assertFalse( kbuilder.hasErrors() );

        // Check errors
        final String err = kbuilder.getErrors().toString();
        assertEquals( "",
                      err );
        assertEquals( 0,
                      kbuilder.getErrors().size() );
        
        // the compiled package
        final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals( 1, pkgs.size() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );
        kbase    = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession session = createKnowledgeSession(kbase);
        session.insert( new Person( "Bob",
                               "http://foo.bar" ) );
        session.insert( new Cheese( "stilton",
                               42 ) );

        final List messages = new ArrayList();
        session.setGlobal( "messages",
                      messages );
//        wm  = SerializationHelper.serializeObject(wm);
        session.fireAllRules();

        // should have fired
        assertEquals( 1,
                      messages.size() );

    }

    @Test
    public void testWithExpanderMore() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder.add( ResourceFactory.newClassPathResource( "test_expander.dsl", getClass() ),
                              ResourceType.DSL );
        kbuilder.add( ResourceFactory.newClassPathResource( "rule_with_expander_dsl_more.dslr", getClass() ) ,
                              ResourceType.DSLR );

        assertFalse( kbuilder.hasErrors() );

        // Check errors
        final String err = kbuilder.getErrors().toString();
        assertEquals( "",
                      err );
        assertEquals( 0,
                      kbuilder.getErrors().size() );
        
        // the compiled package
        final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals( 1, pkgs.size() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );
        kbase    = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession session = createKnowledgeSession(kbase);
        session.insert( new Person( "rage" ) );
        session.insert( new Cheese( "cheddar",
                               15 ) );

        final List messages = new ArrayList();
        session.setGlobal( "messages",
                      messages );
//        wm  = SerializationHelper.serializeObject(wm);
        session.fireAllRules();

        // should have NONE, as both conditions should be false.
        assertEquals( 0,
                      messages.size() );

        session.insert( new Person( "fire" ) );
        session.fireAllRules();

        // still no firings
        assertEquals( 0,
                      messages.size() );

        session.insert( new Cheese( "brie",
                               15 ) );

        session.fireAllRules();

        // YOUR FIRED
        assertEquals( 1,
                      messages.size() );
    }

    @Test @Ignore
    public void testEmptyDSL() throws Exception {
        // FIXME eterelli / mic_hat not sure what to do with this?
        final String DSL = "# This is an empty dsl file.";  // gives antlr <EOF> error
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "test_expander.dsl", getClass() ),
                              ResourceType.DSL );
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( DSL)  ) ,
                              ResourceType.DSLR );

        assertFalse( kbuilder.hasErrors() ); // trying to expand Cheese() pattern

        // Check errors
        final String err = kbuilder.getErrors().toString();
        assertEquals( "",
                      err );
        assertEquals( 0,
                      kbuilder.getErrors().size() );
        
        // the compiled package
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals( 0, pkgs.size() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );
        kbase    = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession session = createKnowledgeSession(kbase);

        pkgs = SerializationHelper.serializeObject( pkgs );
        assertNull( pkgs );
    }

    @Test
    public void testDSLWithIndividualConstraintMappings() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_dslWithIndividualConstraints.dsl", getClass() ),
                              ResourceType.DSL );
        kbuilder.add( ResourceFactory.newClassPathResource( "test_dslWithIndividualConstraints.dslr", getClass() ) ,
                              ResourceType.DSLR );

        assertFalse( kbuilder.hasErrors() );

        // Check errors
        final String err = kbuilder.getErrors().toString();
        assertEquals( "",
                      err );
        assertEquals( 0,
                      kbuilder.getErrors().size() );
        
        // the compiled package
        final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();
        assertEquals( 1, pkgs.size() );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );
        kbase    = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession session = createKnowledgeSession(kbase);
        List results = new ArrayList();
        session.setGlobal( "results",
                      results );
        Cheese cheese = new Cheese( "stilton",
                                    42 );
        session.insert( cheese );

//        wm  = SerializationHelper.serializeObject(wm);
        session.fireAllRules();

        // should have fired
        assertEquals( 1,
                      results.size() );
        assertEquals( cheese,
                      results.get( 0 ) );

    }

    @Test
    public void testDSL() {
        String dsl = "[when]There is a Person=Person()\n"
                + "[when]-named {name}=name == \"{name}\"\n"
                + "[when]-aged less than {age}=age < {age}\n"
                + "[then]Log {message}=list.add({message});";

        String drl = "import org.drools.Person;\n"
                + "global java.util.List list\n"
                + "rule R1\n"
                + "when\n"
                + "There is a Person\n"
                + "-named Mario\n"
                + "-aged less than 40\n"
                + "then\n"
                // + "System.out.println(\"OK\");\n"
                + "Log \"OK\"\n"
                + "end\n";

        assertTrue(doTest(dsl, drl).contains("OK"));
    }

    @Test
    public void testDSLWithVariableBinding() {
        String dsl = "[when]There is a Person=$p : Person()\n"
                + "[when]-named {name}=name == \"{name}\"\n"
                + "[when]-aged less than {age}=age < {age}\n"
                + "[then]Log person name=list.add($p.getName());";

        String drl = "import org.drools.Person;\n"
                + "global java.util.List list\n"
                + "rule R1\n"
                + "when\n"
                + "There is a Person\n"
                + "-named Mario\n"
                + "-aged less than 40\n"
                + "then\n"
                + "Log person name\n"
                + "end\n";

        assertTrue(doTest(dsl, drl).contains("Mario"));
    }

    @Test
    public void testDSLWithCommentedBlock() {
        // JBRULES-3445
        String dsl = "[when]There is a Person=Person()\n"
                + "[when]-named {name}=name == \"{name}\"\n"
                + "[when]-aged less than {age}=age < {age}\n"
                + "[then]Log {message}=list.add({message});";

        String drl = "import org.drools.Person;\n"
                + "global java.util.List list\n"
                + "rule R1\n"
                + "when\n"
                + "/*There is a Cheese\n"
                + "-of type Gorgonzola*/\n"
                + "There is a Person\n"
                + "-named Mario\n"
                + "-aged less than 40\n"
                + "then\n"
                + "Log \"OK\"\n"
                + "end\n";

        assertTrue(doTest(dsl, drl).contains("OK"));
    }

    private List doTest(String dsl, String drl) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(dsl.getBytes()), ResourceType.DSL );
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DSLR );

        assertFalse(kbuilder.hasErrors());

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert(new Person("Mario", 38));
        ksession.fireAllRules();
        ksession.dispose();

        return list;
    }
}
