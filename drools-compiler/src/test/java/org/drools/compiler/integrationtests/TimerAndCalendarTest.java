package org.drools.compiler.integrationtests;

import org.drools.compiler.Alarm;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.Foo;
import org.drools.compiler.Pet;
import org.drools.compiler.StockTick;
import org.drools.core.base.UndefinedCalendarExcption;
import org.drools.core.time.SessionPseudoClock;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.core.util.DateUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.TimedRuleExectionOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.Calendar;
import org.kie.api.time.SessionClock;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimerAndCalendarTest extends CommonTestMethodBase {

    @Test(timeout=15000)
    public void testDuration() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("test_Duration.drl");
        KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        final FactHandle brieHandle = (FactHandle) ksession.insert( brie );

        ksession.fireAllRules();

        // now check for update
        assertEquals( 0,
                      list.size() );

        // sleep for 500ms
        Thread.sleep( 500 );


        ksession.fireAllRules();
        // now check for update
        assertEquals( 1,
                      list.size() );
    }

    @Test(timeout=10000)
    public void testDurationWithNoLoop() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("test_Duration_with_NoLoop.drl");
        KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        final FactHandle brieHandle = (FactHandle) ksession.insert( brie );

        ksession.fireAllRules();

        // now check for update
        assertEquals( 0,
                      list.size() );

        // sleep for 300ms
        Thread.sleep( 300 );

        ksession.fireAllRules();
        // now check for update
        assertEquals( 1,
                      list.size() );
    }

    @Test(timeout=10000)
    public void testDurationMemoryLeakonRepeatedUpdate() throws Exception {
        String str = "";
        str += "package org.drools.compiler.test\n";
        str += "import org.drools.compiler.Alarm\n";
        str += "global java.util.List list;";
        str += "rule \"COMPTEUR\"\n";
        str += "  timer (int: 50s)\n";
        str += "  when\n";
        str += "    $alarm : Alarm( number < 5 )\n";
        str += "  then\n";
        str += "    $alarm.incrementNumber();\n";
        str += "    list.add( $alarm );\n";
        str += "    update($alarm);\n";
        str += "end\n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        timeService.advanceTime( new Date().getTime(), TimeUnit.MILLISECONDS );

        List list = new ArrayList();
        ksession.setGlobal( "list",
                            list );
        ksession.insert( new Alarm() );

        ksession.fireAllRules();

        for ( int i = 0; i < 6; i++ ) {
            timeService.advanceTime( 55, TimeUnit.SECONDS );
            ksession.fireAllRules();
        }

        assertEquals(5,
                     list.size() );
    }
    
    @Test(timeout=10000)
    public void testFireRuleAfterDuration() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("test_FireRuleAfterDuration.drl");
        KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        final FactHandle brieHandle = (FactHandle) ksession.insert( brie );

        ksession.fireAllRules();

        // now check for update
        assertEquals( 0,
                      list.size() );

        // sleep for 300ms
        Thread.sleep( 300 );

        ksession.fireAllRules();

        // now check for update
        assertEquals( 2,
                      list.size() );

    }
    
    @Test(timeout=10000)
    public void testNoProtocolIntervalTimer() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  duration (30s 10s) ";
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);

        List list = new ArrayList();
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        timeService.advanceTime( new Date().getTime(), TimeUnit.MILLISECONDS );
        
        ksession.setGlobal( "list", list );
        
        ksession.fireAllRules();
        assertEquals( 0, list.size() );
        
        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );
        
        timeService.advanceTime( 15, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        
        timeService.advanceTime( 3, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        
        timeService.advanceTime( 2, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
    }
    
    @Test(timeout=10000)
    public void testIntervalTimer() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  timer (int:30s 10s) ";
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);
        
        List list = new ArrayList();

        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        timeService.advanceTime(new Date().getTime(), TimeUnit.MILLISECONDS);
        
        ksession.setGlobal("list", list);
        
        ksession.fireAllRules();
        assertEquals(0, list.size());
        
        timeService.advanceTime(20, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(0, list.size());
        
        timeService.advanceTime(15, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(1, list.size());
        
        timeService.advanceTime(3, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(1, list.size());
        
        timeService.advanceTime(2, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(2, list.size());
        
        timeService.advanceTime(10, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(3, list.size());
    }

    @Test(timeout=10000)
    public void testIntervalTimerWithoutFire() throws Exception {
        String str =
                "package org.simple \n" +
                "global java.util.List list \n" +
                "rule xxx \n" +
                "  timer (int:30s 10s) " +
                "when \n" +
                "then \n" +
                "  list.add(\"fired\"); \n" +
                "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        conf.setOption( TimedRuleExectionOption.YES );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);

        final CyclicBarrier barrier = new CyclicBarrier(2);

        AgendaEventListener agendaEventListener = new DefaultAgendaEventListener() {
            public void afterMatchFired(org.kie.api.event.rule.AfterMatchFiredEvent event) {
                try {
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ksession.addEventListener(agendaEventListener);

        List list = new ArrayList();

        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        timeService.advanceTime( new Date().getTime(), TimeUnit.MILLISECONDS );

        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals(0, list.size());

        timeService.advanceTime(35, TimeUnit.SECONDS);
        barrier.await();
        barrier.reset();
        assertEquals(1, list.size());

        timeService.advanceTime(10, TimeUnit.SECONDS);
        barrier.await();
        barrier.reset();
        assertEquals(2, list.size());

        timeService.advanceTime(10, TimeUnit.SECONDS);
        barrier.await();
        barrier.reset();
        assertEquals( 3, list.size() );
    }

    @Test(timeout=10000)
    public void testExprIntervalTimerRaceCondition() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  timer (expr: $i, $i) \n";
        str += "when \n";
        str += "   $i : Long() \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);

        List list = new ArrayList();

        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        timeService.advanceTime( new Date().getTime(), TimeUnit.MILLISECONDS );

        ksession.setGlobal( "list", list );
        FactHandle fh = (FactHandle) ksession.insert( 10000l );

        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        timeService.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );


        timeService.advanceTime( 17, TimeUnit.SECONDS );
        ksession.update( fh, 5000l );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
    }

    @Test(timeout=10000)
    public void testUnknownProtocol() throws Exception {
        wrongTimerExpression("xyz:30");
    }

    @Test(timeout=10000)
    public void testMissingColon() throws Exception {
        wrongTimerExpression("int 30");
    }

    @Test(timeout=10000)
    public void testMalformedExpression() throws Exception {
        wrongTimerExpression("30s s30");
    }

    @Test(timeout=10000)
    public void testMalformedIntExpression() throws Exception {
        wrongTimerExpression("int 30s");
    }

    @Test(timeout=10000)
    public void testMalformedCronExpression() throws Exception {
        wrongTimerExpression("cron: 0/30 * * * * *");
    }

    private void wrongTimerExpression(String timer) {
        String str = "";
        str += "package org.simple \n";
        str += "rule xxx \n";
        str += "  timer (" + timer + ") ";
        str += "when \n";
        str += "then \n";
        str += "end  \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        assertTrue( kbuilder.hasErrors() );
    }

    @Test(timeout=10000)
    public void testCronTimer() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  timer (cron:15 * * * * ?) ";
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);
        
        List list = new ArrayList();
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
        Date date = df.parse( "2009-01-01T00:00:00.000-0000" );
        
        timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
        
        ksession.setGlobal( "list", list );
  
        ksession.fireAllRules();
        assertEquals( 0, list.size() );
                
        timeService.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );
                 
        timeService.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        
        timeService.advanceTime( 30, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        
        timeService.advanceTime( 30, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
    }
    
    @Test(timeout=10000)
    public void testCalendarNormalRuleSingleCalendar() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\"\n";
        str += "when \n";
        str += "  String()\n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";


        Calendar calFalse = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
                return false;
            }
        };
        
        Calendar calTrue = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
                return true;
            }
        };

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);
        
        List list = new ArrayList();
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
        Date date = df.parse( "2009-01-01T00:00:00.000-0000" );
        
        ksession.getCalendars().set( "cal1", calTrue );
        
        timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
        ksession.setGlobal( "list", list );
        ksession.insert( "o1" );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
                
        timeService.advanceTime( 10, TimeUnit.SECONDS );
        ksession.insert( "o2" );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        
        ksession.getCalendars().set( "cal1", calFalse );
        timeService.advanceTime( 10, TimeUnit.SECONDS );
        ksession.insert( "o3" );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        
        ksession.getCalendars().set( "cal1", calTrue );
        timeService.advanceTime( 30, TimeUnit.SECONDS );
        ksession.insert( "o4" );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
    }

    @Test
    public void testUndefinedCalendar() throws Exception {
        String str = "";
        str += "rule xxx \n";
        str += "  calendars \"cal1\"\n";
        str += "when \n";
        str += "then \n";
        str += "end  \n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase);

        try {
            ksession.fireAllRules();
            fail("should throw UndefinedCalendarExcption");
        } catch (UndefinedCalendarExcption e) { }
    }

    @Test(timeout=10000)
    public void testCalendarNormalRuleMultipleCalendars() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\", \"cal2\"\n";
        str += "when \n";
        str += "  String()\n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);

        Calendar calFalse = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
                return false;
            }
        };
        
        Calendar calTrue = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
                return true;
            }
        };
        
        List list = new ArrayList();
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
        Date date = df.parse( "2009-01-01T00:00:00.000-0000" );
        
        ksession.getCalendars().set( "cal1", calTrue );
        ksession.getCalendars().set( "cal2", calTrue );
        
        timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
        ksession.setGlobal( "list", list );
        ksession.insert( "o1" );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );

        ksession.getCalendars().set( "cal2", calFalse );
        timeService.advanceTime( 10, TimeUnit.SECONDS );
        ksession.insert( "o2" );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
                
        ksession.getCalendars().set( "cal1", calFalse );
        timeService.advanceTime( 10, TimeUnit.SECONDS );
        ksession.insert( "o3" );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        
        ksession.getCalendars().set( "cal1", calTrue );
        ksession.getCalendars().set( "cal2", calTrue );
        timeService.advanceTime( 30, TimeUnit.SECONDS );
        ksession.insert( "o4" );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
    }
    
    @Test(timeout=10000)
    public void testCalendarsWithCron() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\", \"cal2\"\n";
        str += "  timer (cron:15 * * * * ?) ";
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);
        
        List list = new ArrayList();
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
        Date date = df.parse( "2009-01-01T00:00:00.000-0000" );
        
        timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
        
        final Date date1 = new Date( date.getTime() +  (15 * 1000) );
        final Date date2 = new Date( date1.getTime() + (60 * 1000) );
        final Date date3 = new Date( date2.getTime() + (60 * 1000) );
        final Date date4 = new Date( date3.getTime() + (60 * 1000) );
        
        Calendar cal1 = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
                if ( timestamp == date1.getTime() ) {
                    return true;
                } else if ( timestamp == date4.getTime() ) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        
        Calendar cal2 = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
               if ( timestamp == date2.getTime() ) {
                    return false;
                }  else if ( timestamp == date3.getTime() ) {
                    return true;
                } else {
                    return true;
                }
            }
        };
        
        ksession.getCalendars().set( "cal1", cal1 );
        ksession.getCalendars().set( "cal2", cal2 );
        
        ksession.setGlobal( "list", list );
                         
        ksession.fireAllRules();
        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
                      
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );

        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
        
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 4, list.size() );
    }
    
    @Test(timeout=10000)
    public void testCalendarsWithIntervals() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\", \"cal2\"\n";
        str += "  timer (15s 60s) "; //int: protocol is assumed
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);
        
        List list = new ArrayList();
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
        Date date = df.parse( "2009-01-01T00:00:00.000-0000" );
        
        timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
        
        final Date date1 = new Date( date.getTime() +  (15 * 1000) );
        final Date date2 = new Date( date1.getTime() + (60 * 1000) );
        final Date date3 = new Date( date2.getTime() + (60 * 1000) );
        final Date date4 = new Date( date3.getTime() + (60 * 1000) );
        
        Calendar cal1 = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
                if ( timestamp == date1.getTime() ) {
                    return true;
                } else if ( timestamp == date4.getTime() ) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        
        Calendar cal2 = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
               if ( timestamp == date2.getTime() ) {
                    return false;
                }  else if ( timestamp == date3.getTime() ) {
                    return true;
                } else {
                    return true;
                }
            }
        };
        
        ksession.getCalendars().set( "cal1", cal1 );
        ksession.getCalendars().set( "cal2", cal2 );
        
        ksession.setGlobal( "list", list );
                         
        ksession.fireAllRules();
        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
                      
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );

        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
        
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 4, list.size() );
    }
    
    @Test(timeout=10000)
    public void testCalendarsWithIntervalsAndStartAndEnd() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\"\n";
        str += "  timer (0d 1d; start=3-JAN-2010, end=5-JAN-2010) "; //int: protocol is assumed
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);
        
        List list = new ArrayList();
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        DateFormat df = new SimpleDateFormat( "dd-MMM-yyyy", Locale.UK );
        Date date = df.parse( "1-JAN-2010" );
        
        Calendar cal1 = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
                return true;
            }
        };
        
        long oneDay = 60 * 60 * 24;
        ksession.getCalendars().set( "cal1", cal1 );
        ksession.setGlobal( "list", list );
        
        timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );
        
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );
                      
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );  // day 3
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );   // day 5
        ksession.fireAllRules();
        assertEquals( 3, list.size() );

        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
    }
    
    @Test(timeout=10000)
    public void testCalendarsWithIntervalsAndStartAndLimit() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\"\n";
        str += "  timer (0d 1d; start=3-JAN-2010, repeat-limit=4) "; //int: protocol is assumed
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ClockTypeOption.get("pseudo"));

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);
        
        List list = new ArrayList();
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        DateFormat df = new SimpleDateFormat( "dd-MMM-yyyy", Locale.UK );
        Date date = df.parse( "1-JAN-2010" );
        
        Calendar cal1 = new Calendar() {
            public boolean isTimeIncluded(long timestamp) {
                return true;
            }
        };
        
        long oneDay = 60 * 60 * 24;
        ksession.getCalendars().set( "cal1", cal1 );
        ksession.setGlobal( "list", list );
        
        timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );
        
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );
                      
        timeService.advanceTime( oneDay, TimeUnit.SECONDS ); // day 3
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );   // day 5
        ksession.fireAllRules();
        assertEquals( 3, list.size() );

        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
    }
    
    @Test(timeout=10000)
    public void testCalendarsWithCronAndStartAndEnd() throws Exception {
        Locale defaultLoc = Locale.getDefault();
        try {
            Locale.setDefault( Locale.UK ); // Because of the date strings in the DRL, fixable with JBRULES-3444
            String str =
                "package org.simple \n" +
                "global java.util.List list \n" +
                "rule xxx \n" +
                "  date-effective \"2-JAN-2010\"\n" +
                "  date-expires \"6-JAN-2010\"\n" +
                "  calendars \"cal1\"\n" +
                "  timer (cron: 0 0 0 * * ?) " +
                "when \n" +
                "then \n" +
                "  list.add(\"fired\"); \n" +
                "end  \n";

            KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
            conf.setOption(ClockTypeOption.get("pseudo"));

            KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
            KieSession ksession = createKnowledgeSession(kbase, conf);
            
            List list = new ArrayList();
            PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
            DateFormat df = new SimpleDateFormat( "dd-MMM-yyyy", Locale.UK );
            Date date = df.parse( "1-JAN-2010" );
            
            Calendar cal1 = new Calendar() {
                public boolean isTimeIncluded(long timestamp) {
                    return true;
                }
            };
            
            long oneDay = 60 * 60 * 24;
            ksession.getCalendars().set( "cal1", cal1 );
            ksession.setGlobal( "list", list );
            
            timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
            ksession.fireAllRules();
            assertEquals( 0, list.size() );
            
            timeService.advanceTime( oneDay, TimeUnit.SECONDS );
            ksession.fireAllRules();
            assertEquals( 0, list.size() );
                          
            timeService.advanceTime( oneDay, TimeUnit.SECONDS ); // day 3
            ksession.fireAllRules();
            assertEquals( 1, list.size() );
                 
            timeService.advanceTime( oneDay, TimeUnit.SECONDS );
            ksession.fireAllRules();
            assertEquals( 2, list.size() );
            
            timeService.advanceTime( oneDay, TimeUnit.SECONDS );   // day 5
            ksession.fireAllRules();
            assertEquals( 3, list.size() );
    
            timeService.advanceTime( oneDay, TimeUnit.SECONDS );
            ksession.fireAllRules();
            assertEquals( 3, list.size() );
        } finally {
            Locale.setDefault( defaultLoc );
        }
    }

    @Test(timeout=10000)
    public void testCalendarsWithCronAndStartAndLimit() throws Exception {
        Locale defaultLoc = Locale.getDefault();
        try {
            Locale.setDefault( Locale.UK ); // Because of the date strings in the DRL, fixable with JBRULES-3444
            String str =
                    "package org.simple \n" +
                    "global java.util.List list \n" +
                    "rule xxx \n" +
                    "  date-effective \"2-JAN-2010\"\n" +
                    "  calendars \"cal1\"\n" +
                    // FIXME: I have to set the repeate-limit to 6 instead of 4 becuase
                    // it is incremented regardless of the effective date
                    "  timer (cron: 0 0 0 * * ?; repeat-limit=6) " +
                    "when \n" +
                    "then \n" +
                    "  list.add(\"fired\"); \n" +
                    "end  \n";

            KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
            conf.setOption(ClockTypeOption.get("pseudo"));

            KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
            KieSession ksession = createKnowledgeSession(kbase, conf);

            List list = new ArrayList();
            PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
            DateFormat df = new SimpleDateFormat( "dd-MMM-yyyy", Locale.UK );
            Date date = df.parse( "1-JAN-2010" );

            Calendar cal1 = new Calendar() {
                public boolean isTimeIncluded(long timestamp) {
                    return true;
                }
            };

            long oneDay = 60 * 60 * 24;
            ksession.getCalendars().set( "cal1", cal1 );
            ksession.setGlobal( "list", list );

            timeService.advanceTime( date.getTime(), TimeUnit.MILLISECONDS );
            ksession.fireAllRules();
            assertEquals( 0, list.size() );

            timeService.advanceTime( oneDay, TimeUnit.SECONDS );
            ksession.fireAllRules();
            assertEquals( 0, list.size() );

            timeService.advanceTime( oneDay, TimeUnit.SECONDS ); // day 3
            ksession.fireAllRules();
            assertEquals( 1, list.size() );

            timeService.advanceTime( oneDay, TimeUnit.SECONDS );
            ksession.fireAllRules();
            assertEquals( 2, list.size() );

            timeService.advanceTime( oneDay, TimeUnit.SECONDS );   // day 5
            ksession.fireAllRules();
            assertEquals( 3, list.size() );

            timeService.advanceTime( oneDay, TimeUnit.SECONDS );
            ksession.fireAllRules();
            assertEquals( 4, list.size() );
        } finally {
            Locale.setDefault( defaultLoc );
        }
    }
    
    @Test(timeout=10000)
    public void testTimerWithNot() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("test_Timer_With_Not.drl");
        KieSession ksession = createKnowledgeSession(kbase);

        ksession.fireAllRules();
        Thread.sleep( 200 );
        ksession.fireAllRules();
        Thread.sleep( 200 );
        ksession.fireAllRules();
        // now check that rule "wrap A" fired once, creating one B
        assertEquals( 2, ksession.getFactCount() );
    }

    @Test(timeout=10000)
    public void testHaltWithTimer() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("test_Halt_With_Timer.drl");
        final KieSession ksession = createKnowledgeSession(kbase);

        new Thread( new Runnable(){
            public void run(){ ksession.fireUntilHalt(); }
            } ).start();
        Thread.sleep( 1000 );
        FactHandle handle = (FactHandle) ksession.insert( "halt" );
        Thread.sleep( 2000 );

        // now check that rule "halt" fired once, creating one Integer
        assertEquals( 2, ksession.getFactCount() );
        ksession.retract( handle );
    }

    @Test(timeout=10000)
    public void testTimerRemoval() {
        try {
            String str = "package org.drools.compiler.test\n" +
                    "import " + TimeUnit.class.getName() + "\n" +
            		"global java.util.List list \n" +
            		"global " + CountDownLatch.class.getName() + " latch\n" + 
                    "rule TimerRule \n" + 
                    "   timer (int:100 50) \n" +
                    "when \n" + 
                    "then \n" +
                    "        //forces it to pause until main thread is ready\n" +
                    "        latch.await(10, TimeUnit.MINUTES); \n" +
                    "        list.add(list.size()); \n" +  
                    " end";

            KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
            KieSession ksession = createKnowledgeSession(kbase);

            CountDownLatch latch = new CountDownLatch(1);
            List list = Collections.synchronizedList( new ArrayList() );
            ksession.setGlobal( "list", list );
            ksession.setGlobal( "latch", latch );            
            
            ksession.fireAllRules();           
            Thread.sleep(500); // this makes sure it actually enters a rule
            kbase.removeRule("org.drools.compiler.test", "TimerRule");
            ksession.fireAllRules();
            latch.countDown();
            Thread.sleep(500); // allow the last rule, if we were in the middle of one to actually fire, before clearing
            ksession.fireAllRules();
            list.clear();
            Thread.sleep(500); // now wait to see if any more fire, they shouldn't
            ksession.fireAllRules();
            assertEquals( 0, list.size() );
            ksession.dispose();
        } catch (InterruptedException e) {
            throw new RuntimeException( e );
        }
    }

    @Test(timeout=10000)
    public void testIntervalTimerWithLongExpressions() throws Exception {
        String str = "package org.simple;\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare Bean\n" +
                "  delay   : long = 30000\n" +
                "  period  : long = 10000\n" +
                "end\n" +

                "\n" +
                "rule init \n" +
                "when \n" +
                "then \n" +
                " insert( new Bean() );\n" +
                "end \n" +
                "\n" +
                "rule xxx\n" +
                "  salience ($d) \n" +
                "  timer( expr: $d, $p; start=3-JAN-2010 )\n" +
                "when\n" +
                "  Bean( $d : delay, $p : period )\n" +
                "then\n" +
                "  list.add( \"fired\" );\n" +
                "end";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ClockTypeOption.get("pseudo"));

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);

        List list = new ArrayList();

        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        timeService.setStartupTime( DateUtils.parseDate("3-JAN-2010").getTime() );

        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        timeService.advanceTime( 15, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );

        timeService.advanceTime( 3, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );

        timeService.advanceTime( 2, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );

        timeService.advanceTime( 10, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
    }


    @Test(timeout=10000)
    public void testIntervalTimerWithStringExpressions() throws Exception {
        checkIntervalTimerWithStringExpressions(false, "3-JAN-2010");
    }

    @Test(timeout=10000)
    public void testIntervalTimerWithAllExpressions() throws Exception {
        checkIntervalTimerWithStringExpressions(true, "3-JAN-2010");
    }

    @Test(timeout=10000)
    public void testIntervalTimerWithStringExpressionsAfterStart() throws Exception {
        checkIntervalTimerWithStringExpressions(false, "3-FEB-2010");
    }

    @Test(timeout=10000)
    public void testIntervalTimerWithAllExpressionsAfterStart() throws Exception {
        checkIntervalTimerWithStringExpressions(true, "3-FEB-2010");
    }

    private void checkIntervalTimerWithStringExpressions(boolean useExprForStart, String startTime) throws Exception {
        String str = "package org.simple;\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare Bean\n" +
                "  delay   : String = \"30s\"\n" +
                "  period  : long = 60000\n" +
                "  start   : String = \"3-JAN-2010\"\n" +
                "end\n" +
                "\n" +
                "rule init \n" +
                "when \n" +
                "then \n" +
                " insert( new Bean() );\n" +
                "end \n" +
                "\n" +
                "rule xxx\n" +
                "  salience ($d) \n" +
                "  timer( expr: $d, $p; start=" + (useExprForStart ? "$s" : "3-JAN-2010") +" )\n" +
                "when\n" +
                "  Bean( $d : delay, $p : period, $s : start )\n" +
                "then\n" +
                "  list.add( \"fired\" );\n" +
                "end";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ClockTypeOption.get("pseudo"));

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str );
        KieSession ksession = createKnowledgeSession(kbase, conf);

        List list = new ArrayList();

        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        timeService.setStartupTime( DateUtils.parseDate(startTime).getTime() );

        ksession.setGlobal( "list", list );

        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );

        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );

        timeService.advanceTime( 40, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );

        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );

        // simulate a pause in the use of the engine by advancing the system clock
        timeService.setStartupTime(DateUtils.parseDate("3-MAR-2010").getTime());
        list.clear();

        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() ); // fires once to recover from missing activation

        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );

        timeService.advanceTime( 20, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );

        timeService.advanceTime( 40, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );

        timeService.advanceTime( 60, TimeUnit.SECONDS );
        ksession.fireAllRules();
        assertEquals( 4, list.size() );
    }

    @Test(timeout=10000)
    public void testIntervalTimerExpressionWithOr() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "import " + FactA.class.getCanonicalName() + "\n"
                      + "import " + Foo.class.getCanonicalName() + "\n"
                      + "import " + Pet.class.getCanonicalName() + "\n"
                      + "rule r1 timer (expr: f1.field2, f1.field2; repeat-limit=3)\n"
                      + "when\n"                      
                      + "    foo: Foo()\n" 
                      + "    ( Pet()  and f1 : FactA( field1 == 'f1') ) or \n"
                      + "    f1 : FactA(field1 == 'f2') \n"                      
                      + "then\n"
                      + "    list.add( f1 );\n"
                      + "    foo.setId( 'xxx' );\n"
                      + "end\n" + "\n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(text);
        KieSession ksession = createKnowledgeSession(kbase, conf);
        
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        timeService.advanceTime( new Date().getTime(), TimeUnit.MILLISECONDS );
        
        List list = new ArrayList();
        ksession.setGlobal( "list", list );        
        ksession.insert ( new Foo(null, null) );
        ksession.insert ( new Pet(null) );
        
        FactA fact1 = new FactA();
        fact1.setField1( "f1" );
        fact1.setField2( 250 );
        
        FactA fact3 = new FactA();
        fact3.setField1( "f2" );
        fact3.setField2( 1000 );
        
        ksession.insert( fact1 );
        ksession.insert( fact3 );
        
        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        timeService.advanceTime( 300, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( fact1, list.get( 0 ) );

        timeService.advanceTime( 300, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        assertEquals( fact1, list.get( 1 ) );

        timeService.advanceTime( 300, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() ); // did not change, repeat-limit kicked in

        timeService.advanceTime( 300, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
        assertEquals( fact3, list.get( 2 ) );

        timeService.advanceTime( 1000, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 4, list.size() );
        assertEquals( fact3, list.get( 3 ) );

        timeService.advanceTime( 1000, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 4, list.size() ); // did not change, repeat-limit kicked in
    }

    @Test(timeout=10000)
    public void testExprTimeRescheduled() throws Exception {
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "import " + FactA.class.getCanonicalName() + "\n"
                      + "rule r1 timer (expr: f1.field2, f1.field4)\n"
                      + "when\n"                      
                      + "    f1 : FactA() \n"                      
                      + "then\n"
                      + "    list.add( f1 );\n"
                      + "end\n" + "\n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );

        KnowledgeBase kbase = loadKnowledgeBaseFromString(text);
        KieSession ksession = createKnowledgeSession(kbase, conf);
        
        PseudoClockScheduler timeService = ( PseudoClockScheduler ) ksession.<SessionClock>getSessionClock();
        timeService.advanceTime( new Date().getTime(), TimeUnit.MILLISECONDS );
        
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        FactA fact1 = new FactA();
        fact1.setField1( "f1" );
        fact1.setField2( 500 );
        fact1.setField4( 1000 );
        FactHandle fh = (FactHandle) ksession.insert (fact1 );        
                
        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        timeService.advanceTime( 1100, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( fact1, list.get( 0 ) );

        timeService.advanceTime( 1100, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        assertEquals( fact1, list.get( 1 ) );

        timeService.advanceTime( 400, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
        assertEquals( fact1, list.get( 2 ) );
        list.clear();

        // the activation state of the rule is not changed so the timer isn't reset
        // since the timer alredy fired it will only use only the period that now will be set to 2000
        fact1.setField2( 300 );
        fact1.setField4( 2000 );
        ksession.update(  fh, fact1 );
        ksession.fireAllRules();

        // 100 has passed of the 1000, from the previous schedule
        // so that should be deducted from the 2000 period above, meaning
        //  we only need to increment another 1950
        timeService.advanceTime( 1950, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        assertEquals( fact1, list.get( 0 ) );
        list.clear();

        timeService.advanceTime( 1000, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        timeService.advanceTime( 700, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 0, list.size() );

        timeService.advanceTime( 300, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 1, list.size() );
        
    }    
    
    
    @Test(timeout=10000) @Ignore
    public void testHaltAfterSomeTimeThenRestart() throws Exception {
        if ( CommonTestMethodBase.phreak == RuleEngineOption.RETEOO ) {
            return; // fails randomly for Rete
        }

        String drl = "package org.kie.test;" +
                "global java.util.List list; \n" +
                "\n" +
                "\n" +
                "rule FireAtWill\n" +
                "timer(int:0 100)\n" +
                "when  \n" +
                "then \n" +
                "  list.add( 0 );\n" +
                "end\n" +
                "\n" +
                "rule ImDone\n" +
                "when\n" +
                "  String( this == \"halt\" )\n" +
                "then\n" +
                "  drools.halt();\n" +
                "end\n" +
                "\n" +
                "rule Hi \n" +
                "salience 10 \n" +
                "when \n" +
                "  String( this == \"trigger\" ) \n" +
                "then \n " +
                "  list.add( 5 ); \n" +
                "end \n" +
                "\n" +
                "rule Lo \n" +
                "salience -5 \n" +
                "when \n" +
                "  String( this == \"trigger\" ) \n" +
                "then \n " +
                "  list.add( -5 ); \n" +
                "end \n"
                ;


        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        final KieSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        new Thread( new Runnable(){
            public void run(){ ksession.fireUntilHalt(); }
        } ).start();
        Thread.sleep( 250 );

        assertEquals( java.util.Arrays.asList( 0, 0, 0 ), list );

        ksession.insert( "halt" );
        ksession.insert( "trigger" );
        Thread.sleep( 300 );
        assertEquals( java.util.Arrays.asList( 0, 0, 0 ), list );

        new Thread( new Runnable(){
            public void run(){ ksession.fireUntilHalt(); }
        } ).start();
        Thread.sleep( 200 );

        assertEquals( java.util.Arrays.asList( 0, 0, 0, 5, 0, -5, 0, 0 ), list );
    }



    @Test (timeout=10000)
    public void testHaltAfterSomeTimeThenRestartButNoLongerHolding() throws Exception {
        if ( CommonTestMethodBase.phreak == RuleEngineOption.RETEOO ) {
            return; // fails randomly for Rete
        }

        String drl = "package org.kie.test;" +
                "global java.util.List list; \n" +
                "\n" +
                "\n" +
                "rule FireAtWill\n" +
                "   timer(int:0 100)\n" +
                "when  \n" +
                "  eval(true)" +
                "  String( this == \"trigger\" )" +
                "then \n" +
                "  list.add( 0 );\n" +
                "end\n" +
                "\n" +
                "rule ImDone\n" +
                "when\n" +
                "  String( this == \"halt\" )\n" +
                "then\n" +
                "  drools.halt();\n" +
                "end\n" +
                "\n"
                ;

        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        final KieSession ksession = createKnowledgeSession(kbase);

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle handle = (FactHandle) ksession.insert( "trigger" );
        new Thread( new Runnable(){
            public void run(){ ksession.fireUntilHalt(); }
        } ).start();
        Thread.sleep( 150 );
        assertEquals( 2, list.size() ); // delay 0, repeat after 100
        assertEquals( java.util.Arrays.asList( 0, 0 ), list );

        ksession.insert( "halt" );

        Thread.sleep( 200 );
        ksession.retract( handle );
        assertEquals( 2, list.size() ); // halted, no more rule firing

        new Thread( new Runnable(){
            public void run(){ ksession.fireUntilHalt(); }
        } ).start();
        Thread.sleep( 500 );

        assertEquals( 2, list.size() );
        assertEquals( java.util.Arrays.asList( 0, 0 ), list );
    }

    @Test
    public void testExpiredPropagations() throws InterruptedException {
        // DROOLS-244
        String drl = "package org.drools.test;\n" +
                     "\n" +
                     "import org.drools.compiler.StockTick;\n" +
                     "global java.util.List list;\n" +
                     "\n" +
                     "declare StockTick\n" +
                     "\t@role( event )\n" +
                     "\t@timestamp( time )\n" +
                     "end\n" +
                     "\n" +
                     "declare window ATicks\n" +
                     " StockTick( company == \"AAA\" ) over window:time( 1s ) " +
                     " from entry-point \"AAA\"\n" +
                     "end\n" +
                     "\n" +
                     "declare window BTicks\n" +
                     " StockTick( company == \"BBB\" ) over window:time( 1s ) " +
                     " from entry-point \"BBB\"\n" +
                     "end\n" +
                     "\n" +
                     "rule Ticks \n" +
                     " when\n" +
                     " String()\n" +
                     " accumulate( $x : StockTick() from window ATicks, $a : count( $x ) )\n" +
                     " accumulate( $y : StockTick() from window BTicks, $b : count( $y ) )\n" +
                     " accumulate( $z : StockTick() over window:time( 1s ), $c : count( $z ) )\n" +
                     " then\n" +
                     " list.add( $a );\n" +
                     " list.add( $b );\n" +
                     " list.add( $c );\n" +
                     "end";


        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ) , ResourceType.DRL );
        if ( kbuilder.hasErrors() ) { fail( kbuilder.getErrors().toString() ); }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        KieSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
        ArrayList list = new ArrayList( );
        ksession.setGlobal( "list", list );

        SessionPseudoClock clock = ( SessionPseudoClock ) ksession.getSessionClock();

        clock.advanceTime( 1100, TimeUnit.MILLISECONDS );

        StockTick tick = new StockTick( 0, "AAA", 1.0, 0 );
        StockTick tock = new StockTick( 1, "BBB", 1.0, 2500 );
        StockTick tack = new StockTick( 1, "CCC", 1.0, 2700 );

        EntryPoint epa = ksession.getEntryPoint("AAA");
        EntryPoint epb = ksession.getEntryPoint("BBB");

        epa.insert( tick );
        epb.insert( tock );
        ksession.insert( tack );

        FactHandle handle = ksession.insert("go1");
        ksession.fireAllRules();
        System.out.println( "***** " + list + " *****");
        assertEquals( Arrays.asList(0L, 1L, 1L), list );
        list.clear();
        ksession.retract( handle );

        clock.advanceTime( 2550, TimeUnit.MILLISECONDS );

        handle = ksession.insert( "go2" );
        ksession.fireAllRules();
        System.out.println( "***** " + list + " *****");
        assertEquals( Arrays.asList( 0L, 0L, 1L ), list );
        list.clear();
        ksession.retract( handle );

        clock.advanceTime( 500, TimeUnit.MILLISECONDS );

        handle = ksession.insert( "go3" );
        ksession.fireAllRules();
        System.out.println( "***** " + list + " *****");
        assertEquals( Arrays.asList( 0L, 0L, 0L ), list );
        list.clear();
        ksession.retract( handle );

        ksession.dispose();
    }

    @Test
    public void testCronFire() throws InterruptedException {
        // BZ-1059372
        String drl = "package test.drools\n" +
                     "rule TestRule " +
                     "  timer (cron:* * * * * ?) " +
                     "when\n" +
                     "    String() " +
                     "    Integer() " +
                     "then\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(drl);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        int repetitions = 10000;
        for (int j = 0; j < repetitions; j++ ) {
            ksession.insert( j );
        }

        ksession.insert( "go" );
        ksession.fireAllRules();
    }

    @Test(timeout = 10000) @Ignore("the listener callback holds some locks so blocking in it is not safe")
    public void testRaceConditionWithTimedRuleExectionOption() throws Exception {
        // BZ-1073880
        String str = "package org.simple \n" +
                     "global java.util.List list \n" +
                     "rule xxx @Eager(true)\n" +
                     "  timer (int:30s 10s) "
                     + "when \n" +
                     "  $s: String()\n" +
                     "then \n" +
                     "  list.add($s); \n" +
                     "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ClockTypeOption.get("pseudo"));
        conf.setOption(TimedRuleExectionOption.YES);

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = createKnowledgeSession(kbase, conf);

        final CyclicBarrier barrier = new CyclicBarrier(2);
        final AtomicBoolean aBool = new AtomicBoolean(true);
        AgendaEventListener agendaEventListener = new DefaultAgendaEventListener() {
            public void afterMatchFired(org.kie.api.event.rule.AfterMatchFiredEvent event) {
                try {
                    if (aBool.get()) {
                        barrier.await();
                        aBool.set(false);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        ksession.addEventListener(agendaEventListener);

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        // Using the Pseudo Clock.
        SessionClock clock = ksession.getSessionClock();
        SessionPseudoClock pseudoClock = (SessionPseudoClock) clock;

        // Insert the event.
        String eventOne = "one";
        ksession.insert(eventOne);

        // Advance the time .... so the timer will fire.
        pseudoClock.advanceTime(10000, TimeUnit.MILLISECONDS);

        // Rule doesn't fire in PHREAK. This is because you need to call 'fireAllRules' after you've inserted the fact, otherwise the timer
        // job is not created.

        ksession.fireAllRules();

        // Rule still doesn't fire, because the DefaultTimerJob is created now, and now we need to advance the timer again.

        pseudoClock.advanceTime(30000, TimeUnit.MILLISECONDS);
        barrier.await();
        barrier.reset();
        aBool.set(true);

        pseudoClock.advanceTime(10000, TimeUnit.MILLISECONDS);
        barrier.await();
        barrier.reset();
        aBool.set(true);

        String eventTwo = "two";
        ksession.insert(eventTwo);
        ksession.fireAllRules();

        // 60
        pseudoClock.advanceTime(10000, TimeUnit.MILLISECONDS);
        barrier.await();
        barrier.reset();
        aBool.set(true);

        // 70
        pseudoClock.advanceTime(10000, TimeUnit.MILLISECONDS);
        barrier.await();
        barrier.reset();
        aBool.set(true);

        //From here, the second rule should fire.
        //phaser.register();
        pseudoClock.advanceTime(10000, TimeUnit.MILLISECONDS);
        barrier.await();
        barrier.reset();
        aBool.set(true);

        // Now 2 rules have fired, and those will now fire every 10 seconds.
        pseudoClock.advanceTime(20000, TimeUnit.MILLISECONDS);
        barrier.await();
        barrier.reset();

        pseudoClock.advanceTime(20000, TimeUnit.MILLISECONDS);
        aBool.set(true);
        barrier.await();
        barrier.reset();

        pseudoClock.advanceTime(20000, TimeUnit.MILLISECONDS);
        aBool.set(true);
        barrier.await();
        barrier.reset();

        ksession.destroy();
    }

    @Test
    public void testSharedTimers() throws Exception {
        // DROOLS-451
        String str = "package org.simple \n" +
                     "global java.util.List list \n" +
                     "rule R1\n" +
                     "  timer (int:30s 10s) " +
                     "when \n" +
                     "  $i: Integer()\n" +
                     "then \n" +
                     "  System.out.println(\"1\");\n" +
                     "  list.add(\"1\"); \n" +
                     "end  \n" +
                     "rule R2\n" +
                     "  timer (int:30s 10s) " +
                     "when \n" +
                     "  $i: Integer()\n" +
                     "then \n" +
                     "  System.out.println(\"2\");\n" +
                     "  list.add(\"2\"); \n" +
                     "end  \n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ClockTypeOption.get("pseudo"));
        conf.setOption(TimedRuleExectionOption.YES);

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = createKnowledgeSession(kbase, conf);

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        SessionClock clock = ksession.getSessionClock();
        SessionPseudoClock pseudoClock = (SessionPseudoClock) clock;

        ksession.insert(1);
        ksession.fireAllRules();
        pseudoClock.advanceTime(35, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(2, list.size());
    }

    @Test
    public void testIntervalRuleInsertion() throws Exception {
        // DROOLS-620
        // Does not fail when using pseudo clock due to the subsequent call to fireAllRules
        String str =
                "package org.simple\n" +
                "global java.util.List list\n" +
                "import org.drools.compiler.Alarm\n" +
                "rule \"Interval Alarm\"\n" +
                "timer(int: 1s 1s)\n" +
                "when " +
                "    not Alarm()\n" +
                "then\n" +
                "    insert(new Alarm());\n" +
                "    list.add(\"fired\"); \n" +
                "end\n";

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(TimedRuleExectionOption.YES);
        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = createKnowledgeSession(kbase, conf);

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertEquals( 0, list.size() );
        Thread.sleep( 3000 );
        assertEquals( 1, list.size() );
    }
}
