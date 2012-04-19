package org.drools.util.debug;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.ArrayList;

import org.drools.Cheese;
import org.drools.Cheesery;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.Worker;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.core.util.debug.SessionInspector;
import org.drools.core.util.debug.SessionReporter;
import org.drools.core.util.debug.StatefulKnowledgeSessionInfo;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;

public class SessionInspectorTest {

    @Test
    public void testGetSessionInfo() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_SubNetworks.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_AccumulateWithFromChaining.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_CollectResultsBetaConstraint.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_QueryMemoryLeak.drl" ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "results", new ArrayList<Object>() );
        
        ksession.insert( new Dimension( 100, 50 ) );
        ksession.insert( new Dimension( 130, 80 ) );
        ksession.insert( new Dimension( 50, 40 ) );
        ksession.insert( new Dimension( 50, 40 ) );
        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        ksession.insert( cheesery );
        ksession.insert( new Person( "Bob", "muzzarella") );
        ksession.insert( new Person( "Mark", "brie") );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Double( 10 ) );
        ksession.insert( new Double( 11 ) );
        ksession.insert( new Double( 12 ) );
        ksession.insert( new Double( 13 ) );
        ksession.insert( new Double( 14 ) );
        ksession.insert( new Integer( 15 ) );
        ksession.insert( new Integer( 16 ) );
        ksession.insert( new Integer( 17 ) );
        ksession.insert( new Integer( 18 ) );
        org.drools.runtime.rule.FactHandle handle = ksession.insert( new Worker( ) );
        
        ksession.retract( handle );
        
        SessionInspector inspector = new SessionInspector( ksession );
        
        StatefulKnowledgeSessionInfo info = inspector.getSessionInfo();
        
        String report = SessionReporter.generateReport( "simple", info, null );
        
        assertNotNull( report );
        //System.out.println(report);
        
    }
    
    @Test
    @Ignore("Does not work in the IBM JDK due to a bug in MVEL")
    public void testGetSessionInfoWithCustomTemplate() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_SubNetworks.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_AccumulateWithFromChaining.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_CollectResultsBetaConstraint.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/integrationtests/test_QueryMemoryLeak.drl" ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal( "results", new ArrayList<Object>() );
        
        ksession.insert( new Dimension( 100, 50 ) );
        ksession.insert( new Dimension( 130, 80 ) );
        ksession.insert( new Dimension( 50, 40 ) );
        ksession.insert( new Dimension( 50, 40 ) );
        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        ksession.insert( cheesery );
        ksession.insert( new Person( "Bob", "muzzarella") );
        ksession.insert( new Person( "Mark", "brie") );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Double( 10 ) );
        ksession.insert( new Double( 11 ) );
        ksession.insert( new Double( 12 ) );
        ksession.insert( new Double( 13 ) );
        ksession.insert( new Double( 14 ) );
        ksession.insert( new Integer( 15 ) );
        ksession.insert( new Integer( 16 ) );
        ksession.insert( new Integer( 17 ) );
        ksession.insert( new Integer( 18 ) );
        org.drools.runtime.rule.FactHandle handle = ksession.insert( new Worker( ) );
        
        ksession.retract( handle );
        
        SessionInspector inspector = new SessionInspector( ksession );
        
        StatefulKnowledgeSessionInfo info = inspector.getSessionInfo();

        SessionReporter.addNamedTemplate( "topten", getClass().getResourceAsStream( "customreports.mvel" ) );
        String report = SessionReporter.generateReport( "topten", info, null );
        
        assertNotNull( report );
        //System.out.println(report);
        
    }
    

}
