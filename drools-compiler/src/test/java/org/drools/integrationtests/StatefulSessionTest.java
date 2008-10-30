package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.knowledge.definitions.KnowledgePackage;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class StatefulSessionTest extends TestCase {
    final List list = new ArrayList();

    protected KnowledgeBase getKnowledgeBase() throws Exception {
        return KnowledgeBaseFactory.newKnowledgeBase();
    }

    public void testSingleObjectAssert() throws Exception {
        StatefulKnowledgeSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );        

        Future futureAssert = session.asyncInsert( stilton );               
        Future futureFireAllRules = session.asyncFireAllRules();
        
        int i = 0;
        while ( !futureFireAllRules.isDone() ) {
            Thread.sleep( 100 );
            if (i++ > 5) {
                fail( "Future should have finished by now" );
            }
        }        
               
        assertTrue( futureAssert.get() instanceof FactHandle );
        assertEquals( "stilton",
                      list.get( 0 ) );
        
    }
    
    public void testArrayObjectAssert() throws Exception {
        StatefulKnowledgeSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );        

        Future futureAssert = session.asyncInsert( new Object[] { stilton } );                
        Future futureFireAllRules = session.asyncFireAllRules();
        
        int i = 0;
        while ( !futureFireAllRules.isDone() ) {
            Thread.sleep( 100 );
            if (i++ > 5) {
                fail( "Future should have finished by now" );
            }
        }        
               
        
        assertTrue( futureAssert.get() instanceof List );
        assertTrue( ((List)futureAssert.get()).get( 0 ) instanceof FactHandle );

        assertEquals( "stilton",
                      list.get( 0 ) );        
    } 
    
    public void testCollectionObjectAssert() throws Exception {
        StatefulKnowledgeSession session = getSession();

        final Cheese stilton = new Cheese( "stilton",
                                           5 );        

        List collection = new ArrayList();
        collection.add( stilton );
        Future futureAssert = session.asyncInsert( collection );
        
        Future futureFireAllRules = session.asyncFireAllRules();
        int i = 0;
        while ( !futureFireAllRules.isDone() ) {
            Thread.sleep( 100 );
            if (i++ > 5) {
                fail( "Future should have finished by now" );
            }
        }        
               
        assertTrue( futureAssert.get() instanceof List );
        assertTrue( ((List)futureAssert.get()).get( 0 ) instanceof FactHandle );
        
        assertEquals( "stilton",
                      list.get( 0 ) );
    }   
    
    public void testHasExceptionSingleAssert()throws Exception {

        StatefulKnowledgeSession session = getExceptionSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );      

        Future futureAssert = session.asyncInsert( brie );

        Future futureFireAllRules = session.asyncFireAllRules();
        int i = 0;
        while ( !futureFireAllRules.isDone() ) {
            Thread.sleep( 100 );
            if (i++ > 5) {
                fail( "Future should have finished by now" );
            }
        }   
         
        assertTrue( futureAssert.get() instanceof FactHandle );          
        try {
            futureFireAllRules.get();
            fail( "Exception Should have been thrown" );
        } catch ( ExecutionException e ) {
            // pass
        }
    }
    
    public void testHasExceptionArrayAssert()throws Exception {

        StatefulKnowledgeSession session = getExceptionSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );      

        Future futureAssert = session.asyncInsert( new Object[] { brie } );                
        Future futureFireAllRules = session.asyncFireAllRules();
        
        int i = 0;
        while ( !futureFireAllRules.isDone() ) {
            Thread.sleep( 300 );
            if (i++ > 5) {
                fail( "Future should have finished by now" );
            }
        }        
                       
        assertTrue( futureAssert.get() instanceof List );
        assertTrue( ((List)futureAssert.get()).get( 0 ) instanceof FactHandle );    
        try {
            futureFireAllRules.get();
            fail( "Exception Should have been thrown" );
        } catch ( ExecutionException e ) {
            //pass
        }
    }   
    
    public void testHasExceptionCollectionAssert()throws Exception {

        StatefulKnowledgeSession session = getExceptionSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );      

        List collection = new ArrayList();
        collection.add( brie );
        Future futureAssert = session.asyncInsert( collection );             
        Future futureFireAllRules = session.asyncFireAllRules();
        
        int i = 0;
        while ( !futureFireAllRules.isDone() ) {
            Thread.sleep( 100 );
            if (i++ > 5) {
                fail( "Future should have finished by now" );
            }
        }        
                       
        assertTrue( futureAssert.get() instanceof List );
        assertTrue( ((List)futureAssert.get()).get( 0 ) instanceof FactHandle );        
        try {
            futureFireAllRules.get();
            fail( "Exception Should have been thrown" );
        } catch ( ExecutionException e ) {
            //pass
        }
    }    
    
    public void testSequentialException() {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setSequential( true );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( config );
        
        try {
            kbase.newStatefulKnowledgeSession();
            fail("cannot have a stateful session with sequential set to true" );
        } catch ( Exception e ) {
            
        }
    }
    
    private StatefulKnowledgeSession getExceptionSession() throws Exception {
        KnowledgeBuilder kbuilder =  KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_ConsequenceException.drl" ) ) );
        final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        KnowledgeBase kbase = getKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );
        kbase    = SerializationHelper.serializeObject( kbase );
        return kbase.newStatefulKnowledgeSession();
    }

    private StatefulKnowledgeSession getSession() throws Exception {        
        KnowledgeBuilder kbuilder =  KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "literal_rule_test.drl"  ) ) );
        final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();        

        KnowledgeBase kbase = getKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );
        kbase    = SerializationHelper.serializeObject( kbase );
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        session.setGlobal( "list",
                           this.list );
        return session;
    }
}
