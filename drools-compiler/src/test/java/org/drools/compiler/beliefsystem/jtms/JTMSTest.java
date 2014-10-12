package org.drools.compiler.beliefsystem.jtms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.core.BeliefSystemType;
import org.drools.compiler.Person;
import org.drools.core.SessionConfiguration;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSystem;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;

public class JTMSTest {

    protected StatefulKnowledgeSession getSessionFromString( String drlString) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        try {
            System.setProperty("drools.negatable", "on");
            kBuilder.add(ResourceFactory.newByteArrayResource(drlString.getBytes()),
                         ResourceType.DRL);
            if (kBuilder.hasErrors()) {
                System.err.println(kBuilder.getErrors());
                fail();
            }
        } finally {
            System.setProperty("drools.negatable", "off");
        }

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase( );
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        KieSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ((SessionConfiguration) ksConf).setBeliefSystemType( BeliefSystemType.JTMS );
        
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession( ksConf, null );
        return kSession;
    }
    
    protected StatefulKnowledgeSession getSessionFromFile( String ruleFile ) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( ResourceFactory.newClassPathResource( ruleFile, getClass() ),
                ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            System.err.println( kBuilder.getErrors() );
            fail();
        }

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase( );
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        KieSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ((SessionConfiguration) ksConf).setBeliefSystemType( BeliefSystemType.JTMS );
        
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession( ksConf, null );
        return kSession;
    }    
    
    @Test(timeout = 10000 )
    public void testPosNegNonConflictingInsertions() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
        		"\n" + 
        		"import org.kie.internal.event.rule.ActivationUnMatchListener;\n" +
        		"import java.util.List \n" +
        		"import org.drools.core.common.AgendaItem;" +
        		"global java.util.List list;\n" + 
        		"\n" + 
        		"rule \"go1\"\n" + 
        		"when\n" + 
        		"    String( this == 'go1' )\n" + 
        		"then\n" + 
                "    insertLogical( 'neg', 'neg' );\n" +         		
        		"end\n" + 
        		"\n" + 
                "rule \"go2\"\n" + 
                "when\n" + 
                "    String( this == 'go2' )\n" + 
                "then\n" + 
                "    insertLogical( 'pos' );\n" +             
                "end\n" + 
                "\n" +         		
                "rule \"Positive\"\n" +
                "when\n" + 
                "    $n : String( this != 'go1' || == 'go2' ) \n" + 
                "then\n" +  
                "    final String s = '+' + $n;" +
                "    final List l = list;" +
                "    l.add( s );\n" +
                "    AgendaItem item = ( AgendaItem ) kcontext.getMatch();" +
                "    item.setActivationUnMatchListener( new ActivationUnMatchListener() {\n" + 
                "        \n" + 
                "        public void unMatch(org.kie.api.runtime.rule.RuleRuntime wm,\n" +
                "                            org.kie.api.runtime.rule.Match activation) {\n" +
                "            l.remove( s );\n" + 
                "        }\n" + 
                "    } );" +
                "end\n" +
                "rule \"Negative\"\n" +
                "when\n" +
                "    $n : String(   _.neg, this != 'go1' || == 'go2' ) \n" +
                "then\n" +
                "    final String s = '-' + $n; \n" +
                "    final List l = list; \n" +
                "    l.add( s ); \n" +
                "    AgendaItem item = ( AgendaItem ) kcontext.getMatch();" +
                "    item.setActivationUnMatchListener( new ActivationUnMatchListener() {\n" +
                "        \n" +
                "        public void unMatch(org.kie.api.runtime.rule.RuleRuntime wm,\n" +
                "                            org.kie.api.runtime.rule.Match activation) {\n" +
                "            l.remove( s );\n" +
                "        }\n" +
                "    } );" +
                "end\n";

        StatefulKnowledgeSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        
        FactHandle fhGo1 = kSession.insert( "go1" );
        kSession.fireAllRules();
        assertTrue( list.contains( "-neg" ) );
        
        assertEquals( 1, kSession.getEntryPoint( "DEFAULT" ).getObjects().size() ); //just go1
        assertEquals( 1, getNegativeObjects(kSession).size() );
        
        FactHandle fhGo2 = kSession.insert( "go2" );
        kSession.fireAllRules();
        assertTrue( list.contains( "-neg" ) );
        assertTrue( list.contains( "+pos" ) );
        
        assertEquals( 3, kSession.getEntryPoint( "DEFAULT" ).getObjects().size() ); //go1, go2, pos
        assertEquals( 1, getNegativeObjects(kSession).size() );
        
        kSession.retract( fhGo1 );
        kSession.fireAllRules();
        assertFalse( list.contains( "-neg" ) );
        assertTrue( list.contains( "+pos" ) ); 
        assertEquals( 2, kSession.getEntryPoint( "DEFAULT" ).getObjects().size() ); //go2, pos
        assertEquals( 0, getNegativeObjects(kSession).size() );

        kSession.retract( fhGo2 );
        kSession.fireAllRules();
        assertFalse( list.contains( "-neg" ) );
        assertFalse( list.contains( "+pos" ) ); 
        assertEquals( 0, kSession.getEntryPoint( "DEFAULT" ).getObjects().size() );
        assertEquals( 0, getNegativeObjects(kSession).size() );
    }

    @Test(timeout = 10000 )
    public void testConflictToggleWithoutGoingEmpty() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
                   "\n" +
                   "import org.kie.internal.event.rule.ActivationUnMatchListener;\n" +
                   "import java.util.List \n" +
                   "import org.drools.core.common.AgendaItem;" +
                   "global java.util.List list;\n" +
                   "\n" +
                   "rule \"go1\"\n" +
                   "when\n" +
                   "    String( this == 'go1' )\n" +
                   "then\n" +
                   "    insertLogical( 'xxx' );\n" +
                   "end\n" +
                   "rule \"go2\"\n" +
                   "when\n" +
                   "    String( this == 'go2' )\n" +
                   "then\n" +
                   "    insertLogical( 'xxx');\n" +
                   "end\n" +
                   "rule \"go3\"\n" +
                   "when\n" +
                   "    String( this == 'go3' )\n" +
                   "then\n" +
                   "    insertLogical( 'xxx');\n" +
                   "end\n" +

                   "\n" +
                   "rule \"go4\"\n" +
                   "when\n" +
                   "    String( this == 'go4' )\n" +
                   "then\n" +
                   "    insertLogical( 'xxx', 'neg' );\n" +
                   "end\n" +
                   "\n" +


                   "rule \"Positive\"\n" +
                   "when\n" +
                   "    $n : String( this == 'xxx' ) \n" +
                   "then\n" +
                   "    final String s = '+' + $n;" +
                   "    final List l = list;" +
                   "    l.add( s );\n" +
                   "    AgendaItem item = ( AgendaItem ) kcontext.getMatch();" +
                   "    item.setActivationUnMatchListener( new ActivationUnMatchListener() {\n" +
                   "        \n" +
                   "        public void unMatch(org.kie.api.runtime.rule.RuleRuntime wm,\n" +
                   "                            org.kie.api.runtime.rule.Match activation) {\n" +
                   "            l.remove( s );\n" +
                   "        }\n" +
                   "    } );" +
                   "end\n" +
                   "rule \"Negative\"\n" +
                   "when\n" +
                   "    $n : String( _.neg, this == 'xxx' )\n" +
                   "then\n" +
                   "    final String s = '-' + $n; \n" +
                   "    final List l = list; \n" +
                   "    l.add( s ); \n" +
                   "    AgendaItem item = ( AgendaItem ) kcontext.getMatch();" +
                   "    item.setActivationUnMatchListener( new ActivationUnMatchListener() {\n" +
                   "        \n" +
                   "        public void unMatch(org.kie.api.runtime.rule.RuleRuntime wm,\n" +
                   "                            org.kie.api.runtime.rule.Match activation) {\n" +
                   "            l.remove( s );\n" +
                   "        }\n" +
                   "    } );" +
                   "end\n" +
                "";

        StatefulKnowledgeSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );

        FactHandle fhGo1 = kSession.insert( "go1" );
        FactHandle fhGo2 = kSession.insert( "go2" );
        FactHandle fhGo3 = kSession.insert( "go3" );

        kSession.fireAllRules();
        System.out.println( list );
        assertTrue(list.contains("+xxx"));

        FactHandle fhGo4 = kSession.insert( "go4" );
        kSession.fireAllRules();
        assertTrue( list.isEmpty());

        kSession.delete(fhGo4);
        kSession.fireAllRules();
        assertTrue( list.contains( "+xxx" ) );
    }
    
    @Test(timeout = 10000 )
    @Ignore("Currently cannot support updates")
    public void testChangeInPositivePrime() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
                "\n" + 
                "import org.kie.internal.event.rule.ActivationUnMatchListener;\n" +
                "import java.util.List \n" +
                "import org.drools.core.common.AgendaItem;" +
                "import org.drools.compiler.Person;" +
                "global java.util.List list;\n" + 
                "\n" + 
                "rule \"go1\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(1); \n" +
                "    insertLogical( p );\n" +                
                "end\n" + 
                "rule \"go2\"\n" + 
                "when\n" + 
                "    String( this == 'go2' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(2); \n" +
                "    insertLogical( p );\n" +                                
                "end\n" + 
                "rule \"go3\"\n" + 
                "when\n" + 
                "    String( this == 'go3' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(3); \n" +
                "    insertLogical( p );\n" +                
                "end\n" +                 
                "\n";
        
        StatefulKnowledgeSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        
        // We want to make sure go1 is prime, and then that it switches to go2
        FactHandle fhGo1 = kSession.insert( "go1" );
        kSession.fireAllRules();                
        FactHandle fhGo2 = kSession.insert( "go2" );
        kSession.fireAllRules();   
        FactHandle fhGo3 = kSession.insert( "go3" );
        kSession.fireAllRules();
        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getEntryPoint( "DEFAULT" );
        assertEquals( 4, ep.getObjects().size() ); //just go1, go2, go3, Person(darth)
        
        int count = 0;
        for ( Object object : ep.getObjects() ) {
            if ( object instanceof Person ) {
                assertEquals( new Integer(1), ((Person)object).getNotInEqualTestObject() );
                count++;
            }
        }
        assertEquals( 1, count );
        
        ObjectHashMap equalityMap =  ep.getTruthMaintenanceSystem().getEqualityKeyMap();
        assertEquals( 1, equalityMap.size() ); // Only Person type is logical
        org.drools.core.util.Iterator it = equalityMap.iterator();
        EqualityKey key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }
              
        assertEquals( 3, key.getBeliefSet().size() );        
        assertEquals( new Integer(1), ((Person)key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject() );
        
        kSession.retract( fhGo1 );
        kSession.fireAllRules();
        it = equalityMap.iterator();
        key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }
              
        assertEquals( 2, key.getBeliefSet().size() );        
        assertEquals( new Integer(3), ((Person)key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject() );
        
        kSession.retract( fhGo3 );
        kSession.fireAllRules();
        it = equalityMap.iterator();
        key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }
              
        assertEquals( 1, key.getBeliefSet().size() );        
        assertEquals( new Integer(2), ((Person)key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject() );
    }    
    
    @Test(timeout = 10000 )
    @Ignore("Currently cannot support updates")
    public void testChangeInNegativePrime() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
                "\n" + 
                "import org.kie.internal.event.rule.ActivationUnMatchListener;\n" +
                "import java.util.List \n" +
                "import org.drools.core.common.AgendaItem;" +
                "import org.drools.compiler.Person;" +
                "global java.util.List list;\n" + 
                "\n" +
                "declare entry-point 'neg' end \n" +
                "" +
                "rule \"go1\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(1); \n" +
                "    insertLogical( p, 'neg' );\n" +                
                "end\n" + 
                "rule \"go2\"\n" + 
                "when\n" + 
                "    String( this == 'go2' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(2); \n" +
                "    insertLogical( p, 'neg' );\n" +                                
                "end\n" + 
                "rule \"go3\"\n" + 
                "when\n" + 
                "    String( this == 'go3' )\n" + 
                "then\n" + 
                "    Person p = new Person( 'darth' ); \n" +
                "    p.setNotInEqualTestObject(3); \n" +
                "    insertLogical( p, 'neg' );\n" +                
                "end\n" +                 
                "\n";

        StatefulKnowledgeSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        
        // We want to make sure go1 is prime, and then that it switches to go2
        FactHandle fhGo1 = kSession.insert( "go1" );
        kSession.fireAllRules();                
        FactHandle fhGo2 = kSession.insert( "go2" );
        kSession.fireAllRules();   
        FactHandle fhGo3 = kSession.insert( "go3" );
        kSession.fireAllRules();
        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getEntryPoint( "DEFAULT" );
        assertEquals( 3, ep.getObjects().size() ); //just go1, go2, go3
        assertEquals( 1, getNegativeObjects(kSession).size() );  // Person(darth)
        
        int count = 0;
        for ( Object object : getNegativeObjects(kSession) ) {
            if ( object instanceof Person ) {
                assertEquals( new Integer(1), ((Person)object).getNotInEqualTestObject() );
                count++;
            }
        }
        assertEquals( 1, count );
        
        ObjectHashMap equalityMap =  ep.getTruthMaintenanceSystem().getEqualityKeyMap();
        assertEquals( 1, equalityMap.size() ); // Only Person type is logical
        org.drools.core.util.Iterator it = equalityMap.iterator();
        EqualityKey key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }
              
        assertEquals( 3, key.getBeliefSet().size() );        
        assertEquals( new Integer(1), ((Person)((JTMSBeliefSetImpl)key.getBeliefSet()).getFactHandle().getObject()).getNotInEqualTestObject() );
        
        kSession.retract( fhGo1 );
        kSession.fireAllRules();
        it = equalityMap.iterator();
        key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }

        assertEquals( 2, key.getBeliefSet().size() );        
        assertEquals( new Integer(3), ((Person)((JTMSBeliefSetImpl)key.getBeliefSet()).getFactHandle().getObject()).getNotInEqualTestObject() );

        kSession.retract( fhGo3 );
        kSession.fireAllRules();
        it = equalityMap.iterator();
        key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }

        assertEquals( 1, key.getBeliefSet().size() );        
        assertEquals( new Integer(2), ((Person)((JTMSBeliefSetImpl)key.getBeliefSet()).getFactHandle().getObject()).getNotInEqualTestObject() );
    }
    
    @Test(timeout = 10000 )
    @Ignore("Currently cannot support updates")
    public void testRetractHandleWhenOnlyNeg() {
        String s = "package org.drools.core.beliefsystem.jtms;\n" +
                "\n" + 
                "import org.kie.internal.event.rule.ActivationUnMatchListener;\n" +
                "import java.util.List \n" +
                "import org.drools.core.common.AgendaItem;" +
                "global java.util.List list;\n" + 
                "\n" + 
                "rule \"go1_1\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    insertLogical( new String( 'neg' ), 'neg' );\n" +                
                "end\n" + 
                "rule \"go1_2\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    insertLogical( new String( 'neg' ), 'neg' );\n" +                
                "end\n" + 
                "rule \"go1_3\"\n" + 
                "when\n" + 
                "    String( this == 'go1' )\n" + 
                "then\n" + 
                "    insertLogical( new String( 'neg' ), 'neg' );\n" +                
                "end\n" +                 
                "\n" +            
                "rule \"Negative\"\n" + 
                "when\n" + 
                "    $n : String(  _.neg, this != 'go1' || == 'go2' ) \n" +
                "then\n" +  
                "    final String s = '-' + $n; \n" +
                "    final List l = list; \n" +
                "    l.add( s ); \n" +
                "    AgendaItem item = ( AgendaItem ) kcontext.getMatch(); \n" +
                "    item.setActivationUnMatchListener( new ActivationUnMatchListener() { \n" + 
                "        \n" + 
                "        public void unMatch(org.kie.api.runtime.rule.RuleRuntime wm, \n" +
                "                            org.kie.api.runtime.rule.Match activation) { \n" +
                "            l.remove( s ); \n" + 
                "        }\n" + 
                "    } );" + 
                "end\n";
        
        StatefulKnowledgeSession kSession =  getSessionFromString( s );
        List list = new ArrayList();
        kSession.setGlobal( "list", list );
        
        FactHandle fhGo1 = kSession.insert( "go1" );
        kSession.fireAllRules();
        assertTrue( list.contains( "-neg" ) );        
        
        assertEquals( 1, kSession.getEntryPoint( "DEFAULT" ).getObjects().size() ); //just go1
        assertEquals( 1, getNegativeObjects(kSession).size() );
        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getEntryPoint( "DEFAULT" );
        ObjectHashMap equalityMap =  ep.getTruthMaintenanceSystem().getEqualityKeyMap();
        assertEquals( 2, equalityMap.size() ); // go1, neg are two different strings.
        org.drools.core.util.Iterator it = equalityMap.iterator();
        EqualityKey key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( "neg") ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }
        
        assertEquals( 3, key.getBeliefSet().size() );

        ep.getTruthMaintenanceSystem().delete( key.getLogicalFactHandle() );

        assertEquals( 0, key.getBeliefSet().size() );     

        assertEquals( 1, kSession.getEntryPoint( "DEFAULT" ).getObjects().size() ); //just go1
        assertEquals( 0, getNegativeObjects(kSession).size() );
        assertEquals( 0, key.getBeliefSet().size() );
        assertEquals( 1, ep.getTruthMaintenanceSystem().getEqualityKeyMap().size() );
    }  
    
    @Test(timeout = 10000 )
    public void testConflictStrict() {
        StatefulKnowledgeSession kSession = getSessionFromFile( "posNegConflict.drl" );

        ArrayList list = new ArrayList();
        kSession.setGlobal( "list", list );

        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getEntryPoint( "DEFAULT" );
        JTMSBeliefSystem bs = ( JTMSBeliefSystem ) ep.getTruthMaintenanceSystem().getBeliefSystem();
        bs.STRICT = true;

        try {
            kSession.fireAllRules();
            fail( "A fact and its negation should have been asserted, but no exception was trhown in strict mode" );
        } catch ( Exception e ) {
        } finally {
            bs.STRICT = false;
        }
    }   

    @Test(timeout = 10000 )
    @Ignore("Currently cannot support updates")
    public void testConflictTMS() {
        StatefulKnowledgeSession kSession = getSessionFromFile( "posNegTms.drl" );

        ArrayList list = new ArrayList();
        kSession.setGlobal( "list", list );

        FactHandle a = kSession.insert( "a" );
        FactHandle b = kSession.insert( "b" );
        FactHandle c = kSession.insert( "c" );
        FactHandle d = kSession.insert( "d" );

        try {
            kSession.fireAllRules();

            assertEquals( 4, kSession.getFactCount() );
            assertEquals( 0, list.size() );

            kSession.retract( a );
            kSession.fireAllRules();

            assertEquals( 3, kSession.getFactCount() );
            assertEquals( 0, list.size() );

            kSession.retract( b );
            kSession.fireAllRules();

            assertEquals( 2, kSession.getFactCount() );
            assertEquals(1, getNegativeObjects(kSession).size());
            assertEquals( 1, list.size() );

            a = kSession.insert( "a" );
            kSession.fireAllRules();

            assertEquals( 3, kSession.getFactCount());
            assertEquals( 0, getNegativeObjects(kSession).size() );
            assertEquals( 1, list.size() );

            kSession.retract( c );
            kSession.fireAllRules();

            assertEquals( 2, kSession.getFactCount() );
            assertEquals( 0, getNegativeObjects(kSession).size() );
            assertEquals( 1, list.size() );

            kSession.retract( d );
            kSession.fireAllRules();

            assertEquals( 2, kSession.getFactCount() );
            assertEquals( 0, getNegativeObjects(kSession).size() );
            assertEquals( 2, list.size() );

            kSession.retract( a );
            kSession.fireAllRules();

            assertEquals( 0, kSession.getFactCount() );
            assertEquals( 0, getNegativeObjects(kSession).size() );
            assertEquals( 2, list.size() );

            c = kSession.insert( "c" );
            kSession.fireAllRules();

            assertEquals( 1, kSession.getFactCount() );
            assertEquals( 1, getNegativeObjects(kSession).size() );
            assertEquals( 3, list.size() );


        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "No exception should have been thrown" );
        }
    }

    public List getNegativeObjects(StatefulKnowledgeSession kSession) {
        List list = new ArrayList();
        Iterator it = ((StatefulKnowledgeSessionImpl) kSession).getObjectStore().iterateNegObjects(null);
        while ( it.hasNext() ) {
            list.add(  it.next() );
        }
        return list;
    }

    @Test
    public void testPrimeJustificationWithEqualityMode() {
        String droolsSource =
                "package org.drools.tms.test; \n" +
                "declare Bar end \n" +
                "" +
                "declare Holder x : Bar end \n" +
                "" +
                "" +
                "rule Init \n" +
                "when \n" +
                "then \n" +
                "   insert( new Holder( new Bar() ) ); \n" +
                "end \n" +

                "rule Justify \n" +
                "when \n" +
                " $s : Integer() \n" +
                " $h : Holder( $b : x ) \n" +
                "then \n" +
                " insertLogical( $b ); \n" +
                "end \n" +

                "rule React \n" +
                "when \n" +
                " $b : Bar(  ) \n" +
                "then \n" +
                " System.out.println( $b );  \n" +
                "end \n" ;

        StatefulKnowledgeSession session = getSessionFromString( droolsSource );

        FactHandle handle1 = session.insert( 10 );
        FactHandle handle2 = session.insert( 20 );

        assertEquals( 4, session.fireAllRules() );

        session.delete( handle1 );
        assertEquals( 0, session.fireAllRules() );
    }

}
