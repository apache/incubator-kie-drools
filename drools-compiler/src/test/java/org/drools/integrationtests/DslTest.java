package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.lang.Expander;
import org.drools.lang.dsl.DefaultExpanderResolver;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;

public class DslTest {
   
    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }


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

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
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

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
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

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

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

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
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

}
