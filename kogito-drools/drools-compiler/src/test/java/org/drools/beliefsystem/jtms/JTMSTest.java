package org.drools.beliefsystem.jtms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drools.BeliefSystemType;
import org.drools.Person;
import org.drools.SessionConfiguration;
import org.drools.common.EqualityKey;
import org.drools.common.NamedEntryPoint;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.rule.FactHandle;

public class JTMSTest {
    protected StatefulKnowledgeSession getSessionFromString( String drlString) {
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( ResourceFactory.newByteArrayResource( drlString.getBytes() ),
                      ResourceType.DRL );
        if ( kBuilder.hasErrors() ) {
            System.err.println( kBuilder.getErrors() );
            fail();
        }

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase( );
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KnowledgeSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ((SessionConfiguration) ksConf).setBeliefSystemType( BeliefSystemType.JTMS );
        
        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession( ksConf, null );
        return kSession;
    }    
    
    @Test
    public void testPosNegNonConflictingInsertions() {
        String s = "package org.drools.beliefsystem.jtms;\n" +
        		"\n" + 
        		"import org.kie.event.rule.ActivationUnMatchListener;\n" +
        		"import java.util.List \n" +
        		"import org.drools.common.AgendaItem;" +
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
                "    AgendaItem item = ( AgendaItem ) kcontext.getActivation();" +
                "    item.setActivationUnMatchListener( new ActivationUnMatchListener() {\n" + 
                "        \n" + 
                "        public void unMatch(org.kie.runtime.rule.WorkingMemory wm,\n" +
                "                            org.kie.runtime.rule.Match activation) {\n" +
                "            l.remove( s );\n" + 
                "        }\n" + 
                "    } );" +
                "end\n" +
                "rule \"Negative\"\n" +
                "when\n" +
                "    $n : String(  this != 'go1' || == 'go2' ) from entry-point 'neg' \n" +
                "then\n" +
                "    final String s = '-' + $n; \n" +
                "    final List l = list; \n" +
                "    l.add( s ); \n" +
                "    AgendaItem item = ( AgendaItem ) kcontext.getActivation();" +
                "    item.setActivationUnMatchListener( new ActivationUnMatchListener() {\n" +
                "        \n" +
                "        public void unMatch(org.kie.runtime.rule.WorkingMemory wm,\n" +
                "                            org.kie.runtime.rule.Match activation) {\n" +
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
        
        assertEquals( 1, kSession.getWorkingMemoryEntryPoint( "DEFAULT" ).getObjects().size() ); //just go1
        assertEquals( 1, kSession.getWorkingMemoryEntryPoint( "neg" ).getObjects().size() ); // neg
        
        FactHandle fhGo2 = kSession.insert( "go2" );
        kSession.fireAllRules();
        assertTrue( list.contains( "-neg" ) );
        assertTrue( list.contains( "+pos" ) );
        
        assertEquals( 3, kSession.getWorkingMemoryEntryPoint( "DEFAULT" ).getObjects().size() ); //go1, go2, pos
        assertEquals( 1, kSession.getWorkingMemoryEntryPoint( "neg" ).getObjects().size() ); // neg        
        
        kSession.retract( fhGo1 );
        kSession.fireAllRules();
        assertFalse( list.contains( "-neg" ) );
        assertTrue( list.contains( "+pos" ) ); 
        assertEquals( 2, kSession.getWorkingMemoryEntryPoint( "DEFAULT" ).getObjects().size() ); //go2, pos
        assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "neg" ).getObjects().size() );         

        kSession.retract( fhGo2 );
        kSession.fireAllRules();
        assertFalse( list.contains( "-neg" ) );
        assertFalse( list.contains( "+pos" ) ); 
        assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "DEFAULT" ).getObjects().size() );
        assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "neg" ).getObjects().size() ); 
    }
    
    @Test
    public void testChangeInPositivePrime() {
        String s = "package org.drools.beliefsystem.jtms;\n" +
                "\n" + 
                "import org.kie.event.rule.ActivationUnMatchListener;\n" +
                "import java.util.List \n" +
                "import org.drools.common.AgendaItem;" +
                "import org.drools.Person;" +
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
        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getWorkingMemoryEntryPoint( "DEFAULT" );
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
        it = equalityMap.iterator();
        key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }
              
        assertEquals( 2, key.getBeliefSet().size() );        
        assertEquals( new Integer(2), ((Person)key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject() );
        
        kSession.retract( fhGo2 );
        it = equalityMap.iterator();
        key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }
              
        assertEquals( 1, key.getBeliefSet().size() );        
        assertEquals( new Integer(3), ((Person)key.getBeliefSet().getFactHandle().getObject()).getNotInEqualTestObject() );        
    }    
    
    @Test
    public void testChangeInNegativePrime() {
        String s = "package org.drools.beliefsystem.jtms;\n" +
                "\n" + 
                "import org.kie.event.rule.ActivationUnMatchListener;\n" +
                "import java.util.List \n" +
                "import org.drools.common.AgendaItem;" +
                "import org.drools.Person;" +
                "global java.util.List list;\n" + 
                "\n" + 
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
                "\n" +
                "rule \"init neg ep\"\n" + 
                "when\n" + 
                "    String( ) from entry-point 'neg' \n" + 
                "then\n" +                
                "end\n" +                 
                "\n"                
                ;
        
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
        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getWorkingMemoryEntryPoint( "DEFAULT" );
        assertEquals( 3, ep.getObjects().size() ); //just go1, go2, go3

        NamedEntryPoint negEp = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getWorkingMemoryEntryPoint( "neg" );
        assertEquals( 1, negEp.getObjects().size() ); //just Person(darth)        
        
        int count = 0;
        for ( Object object : negEp.getObjects() ) {
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
        assertEquals( new Integer(1), ((Person)((JTMSBeliefSet)key.getBeliefSet()).getNegativeFactHandle().getObject()).getNotInEqualTestObject() );
        
        kSession.retract( fhGo1 );
        it = equalityMap.iterator();
        key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }

        assertEquals( 2, key.getBeliefSet().size() );        
        assertEquals( new Integer(3), ((Person)((JTMSBeliefSet)key.getBeliefSet()).getNegativeFactHandle().getObject()).getNotInEqualTestObject() );

        kSession.retract( fhGo3 );
        it = equalityMap.iterator();
        key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( new Person( "darth") ) ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }

        assertEquals( 1, key.getBeliefSet().size() );        
        assertEquals( new Integer(2), ((Person)((JTMSBeliefSet)key.getBeliefSet()).getNegativeFactHandle().getObject()).getNotInEqualTestObject() );          
    }
    
    @Test
    public void testRetractHandleWhenOnlyNeg() {
        String s = "package org.drools.beliefsystem.jtms;\n" +
                "\n" + 
                "import org.kie.event.rule.ActivationUnMatchListener;\n" +
                "import java.util.List \n" +
                "import org.drools.common.AgendaItem;" +
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
                "    $n : String(  this != 'go1' || == 'go2' ) from entry-point 'neg' \n" + 
                "then\n" +  
                "    final String s = '-' + $n; \n" +
                "    final List l = list; \n" +
                "    l.add( s ); \n" +
                "    AgendaItem item = ( AgendaItem ) kcontext.getActivation(); \n" +
                "    item.setActivationUnMatchListener( new ActivationUnMatchListener() { \n" + 
                "        \n" + 
                "        public void unMatch(org.kie.runtime.rule.WorkingMemory wm, \n" +
                "                            org.kie.runtime.rule.Match activation) { \n" +
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
        
        assertEquals( 1, kSession.getWorkingMemoryEntryPoint( "DEFAULT" ).getObjects().size() ); //just go1
        assertEquals( 1, kSession.getWorkingMemoryEntryPoint( "neg" ).getObjects().size() ); // neg
        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getWorkingMemoryEntryPoint( "DEFAULT" );
        ObjectHashMap equalityMap =  ep.getTruthMaintenanceSystem().getEqualityKeyMap();
        assertEquals( 2, equalityMap.size() ); // go1, neg are two different strings.
        org.drools.core.util.Iterator it = equalityMap.iterator();
        EqualityKey key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        while ( !key.getFactHandle().getObject().equals( "neg") ) {
            key = ( EqualityKey  ) (( ObjectEntry ) it.next() ).getValue();
        }
        
        assertEquals( 3, key.getBeliefSet().size() );        
        kSession.retract( key.getBeliefSet().getFactHandle() );  
        assertEquals( 0, key.getBeliefSet().size() );     

        assertEquals( 1, kSession.getWorkingMemoryEntryPoint( "DEFAULT" ).getObjects().size() ); //just go1
        assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "neg" ).getObjects().size() ); // neg
        assertEquals( 0, key.getBeliefSet().size() );
        assertEquals( 1, ep.getTruthMaintenanceSystem().getEqualityKeyMap().size() );
    }  
    
    @Test
    public void testConflictStrict() {
        StatefulKnowledgeSession kSession = getSessionFromFile( "posNegConflict.drl" );

        ArrayList list = new ArrayList();
        kSession.setGlobal( "list", list );

        
        NamedEntryPoint ep = ( NamedEntryPoint ) ((StatefulKnowledgeSessionImpl)kSession).getWorkingMemoryEntryPoint( "DEFAULT" );
        JTMSBeliefSystem bs = ( JTMSBeliefSystem ) ep.getTruthMaintenanceSystem().getBeliefSystem();
        bs.STRICT = true;

        try {
            kSession.fireAllRules();
            fail( "A fact and its negation should have been asserted, but no exception was thorwn in strict mode" );
        } catch ( Exception e ) {
        } finally {
            bs.STRICT = false;
        }
    }   

    @Test    
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
            assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "neg" ).getFactCount() );
            assertEquals( 0, list.size() );

            kSession.retract( a );
            kSession.fireAllRules();

            assertEquals( 3, kSession.getFactCount() );
            assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "neg" ).getFactCount() );
            assertEquals( 0, list.size() );

            kSession.retract( b );
            kSession.fireAllRules();

            assertEquals( 2, kSession.getFactCount() );
            assertEquals( 1, kSession.getWorkingMemoryEntryPoint( "neg" ).getFactCount() );
            assertEquals( 1, list.size() );

            a = kSession.insert( "a" );
            kSession.fireAllRules();

            assertEquals( 3, kSession.getFactCount());
            assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "neg" ).getFactCount() );
            assertEquals( 1, list.size() );

            kSession.retract( c );
            kSession.fireAllRules();

            assertEquals( 2, kSession.getFactCount() );
            assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "neg" ).getFactCount() );
            assertEquals( 1, list.size() );

            kSession.retract( d );
            kSession.fireAllRules();

            assertEquals( 2, kSession.getFactCount() );
            assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "neg" ).getFactCount() );
            assertEquals( 2, list.size() );

            kSession.retract( a );
            kSession.fireAllRules();

            assertEquals( 0, kSession.getFactCount() );
            assertEquals( 0, kSession.getWorkingMemoryEntryPoint( "neg" ).getFactCount() );
            assertEquals( 2, list.size() );

            c = kSession.insert( "c" );
            kSession.fireAllRules();

            assertEquals( 1, kSession.getFactCount() );
            assertEquals( 1, kSession.getWorkingMemoryEntryPoint( "neg" ).getFactCount() );
            assertEquals( 3, list.size() );


        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "No exception should have been thrown" );
        }
    }    
}
