package org.drools.reteoo.integrationtests;

import org.drools.core.ClockType;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionClock;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CepEspTest {
    public static class Event {
        private int type;
        private int value;
        private long time;

        public Event( int type, int value, long time ) {
            this.type = type;
            this.value = value;
            this.time = time;
        }

        public int getType() {
            return type;
        }

        public void setType( int type ) {
            this.type = type;
        }

        public int getValue() {
            return value;
        }

        public void setValue( int value ) {
            this.value = value;
        }

        public long getTime() {
            return time;
        }

        public void setTime( long time ) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "Event{" +
                   "type=" + type +
                   ", value=" + value +
                   ", time=" + ( ( time % 10000 ) )+
                   '}';
        }
    }

    @Test
    public void testEventTimestamp() {
        // DROOLS-268
        String drl = "\n" +
                     "import org.drools.reteoo.integrationtests.CepEspTest.Event; \n" +
                     "global java.util.List list; \n" +
                     "global org.drools.core.time.SessionPseudoClock clock; \n" +
                     "" +
                     "declare Event \n" +
                     " @role( event )\n" +
                     " @timestamp( time ) \n" +
                     " @expires( 10000000 ) \n" +
                     "end \n" +
                     "" +
                     "" +
                     "rule \"inform about E1\"\n" +
                     "when\n" +
                     " $event1 : Event( type == 1 )\n" +
                     " //there is an event (T2) with value 0 between 0,2m after doorClosed\n" +
                     " $event2: Event( type == 2, value == 1, this after [0, 1200ms] $event1, $timestamp : time )\n" +
                     " //there is no newer event (T2) within the timeframe\n" +
                     " not Event( type == 2, this after [0, 1200ms] $event1, time > $timestamp ) \n" +
                     "then\n" +
                     " list.add( clock.getCurrentTime() ); \n " +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        baseConfig.setOption( RuleEngineOption.RETEOO );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()) );

        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        ArrayList list = new ArrayList( );
        ksession.setGlobal( "list", list );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        ksession.setGlobal( "clock", clock );

        ksession.insert( new Event( 1, -1, clock.getCurrentTime() ) ); // 0
        clock.advanceTime(600, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) ); // 600
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) ); // 700
        clock.advanceTime(300, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) ); // 1000
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 1, clock.getCurrentTime() ) ); // 1100
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();
        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) ); // 1300

        clock.advanceTime(1000, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();

        assertFalse( list.isEmpty() );
        assertEquals( 1, list.size() );
        Long time = (Long) list.get( 0 );

        assertTrue( time > 1000 && time < 1500 );

        ksession.dispose();
    }

    @Test
    public void testEventTimestamp2() {
        // DROOLS-268
        String drl = "\n" +
                     "import org.drools.reteoo.integrationtests.CepEspTest.Event; \n" +
                     "global java.util.List list; \n" +
                     "global org.drools.core.time.SessionPseudoClock clock; \n" +
                     "" +
                     "declare Event \n" +
                     " @role( event )\n" +
                     " @timestamp( time ) \n" +
                     " @expires( 10000000 ) \n" +
                     "end \n" +
                     "" +
                     "" +
                     "rule \"inform about E1\"\n" +
                     "when\n" +
                     " $event1 : Event( type == 1 )\n" +
                     " $event2: Event( type == 2 )\n" +
                     " not Event( type == 3, this after [0, 1000ms] $event1 ) \n" +
                     "then\n" +
                     " list.add( clock.getCurrentTime() ); \n " +
                     "end\n" +
                     "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        baseConfig.setOption( RuleEngineOption.RETEOO );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );

        //init stateful knowledge session
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( sessionConfig, null );
        ArrayList list = new ArrayList( );
        ksession.setGlobal( "list", list );

        SessionPseudoClock clock = (SessionPseudoClock) ksession.<SessionClock>getSessionClock();
        ksession.setGlobal( "clock", clock );

        ksession.insert( new Event( 1, 0, clock.getCurrentTime() ) );
        clock.advanceTime(600, TimeUnit.MILLISECONDS);
        ksession.fireAllRules();

        ksession.insert( new Event( 2, 0, clock.getCurrentTime() ) );
        clock.advanceTime(600, TimeUnit.MILLISECONDS);
        ksession.insert( new Event( 3, 0, clock.getCurrentTime() ) );
        ksession.fireAllRules();

        assertFalse( list.isEmpty() );
        assertEquals( 1, list.size() );
        long time = (Long) list.get( 0 );

        assertTrue( time >= 1000 );

        ksession.dispose();
    }

    @Test
    public void testCollectAfterRetract() {
        // BZ-1015109
        String drl =
                "import org.drools.reteoo.integrationtests.CepEspTest.SimpleFact;\n" +
                "import java.util.List;\n" +
                "global List list;\n" +
                "\n" +
                "declare SimpleFact\n" +
                "    @role( event )\n" +
                "end\n" +
                "\n" +
                "rule \"Retract facts if 2 or more\" salience 1000\n" +
                "when\n" +
                "    $facts : List( size > 0 ) from collect( SimpleFact() )\n" +
                "then\n" +
                "    for (Object f: new java.util.LinkedList($facts)) {\n" +
                "        System.out.println(\"Retracting \"+f);\n" +
                "        retract(f);\n" +
                "    }\n" +
                "end\n" +
                "\n" +
                "rule \"Still facts in WM\"\n" +
                "when\n" +
                "    $facts : List( size != 0 ) from collect( SimpleFact() )\n" +
                "then\n" +
                "System.out.println( \"bubu\" );\n" +
                "    list.add( $facts.size() );\n" +
                "end\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KieBaseConfiguration baseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        baseConfig.setOption( EventProcessingOption.STREAM );
        baseConfig.setOption( RuleEngineOption.RETEOO );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( baseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert(new SimpleFact("id1"));
        ksession.insert(new SimpleFact("id2"));
        ksession.insert(new SimpleFact("id3"));

        ksession.fireAllRules();
        System.out.println(list);
        assertEquals(0, ksession.getFactCount());
        assertEquals(0, list.size());
    }

    public static class SimpleFact {

        private String status = "NOK";
        private final String id;

        public SimpleFact(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(final String s) {
            status = s;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName()+" (id="+id+", status=" + status+")";
        }
    }
}
