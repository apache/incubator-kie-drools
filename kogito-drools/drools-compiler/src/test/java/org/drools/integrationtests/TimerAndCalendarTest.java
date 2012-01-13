package org.drools.integrationtests;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.drools.Alarm;
import org.drools.Cheese;
import org.drools.CommonTestMethodBase;
import org.drools.FactHandle;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.Calendar;
import org.drools.time.SessionClock;
import org.drools.time.impl.PseudoClockScheduler;
import org.junit.Test;

public class TimerAndCalendarTest extends CommonTestMethodBase {
    
    @Test
    public void testDuration() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Duration.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        final FactHandle brieHandle = workingMemory.insert( brie );

        workingMemory.fireAllRules();

        // now check for update
        assertEquals( 0,
                      list.size() );

        // sleep for 300ms
        Thread.sleep( 300 );

        // now check for update
        assertEquals( 1,
                      list.size() );

    }

    @Test
    public void testDurationWithNoLoop() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Duration_with_NoLoop.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        final FactHandle brieHandle = workingMemory.insert( brie );

        workingMemory.fireAllRules();

        // now check for update
        assertEquals( 0,
                      list.size() );

        // sleep for 300ms
        Thread.sleep( 300 );

        // now check for update
        assertEquals( 1,
                      list.size() );
    }

    @Test
    public void testDurationMemoryLeakonRepeatedUpdate() throws Exception {
        String str = "";
        str += "package org.drools.test\n";
        str += "import org.drools.Alarm\n";
        str += "global java.util.List list;";
        str += "rule \"COMPTEUR\"\n";
        str += "  timer 50\n";
        str += "  when\n";
        str += "    $alarm : Alarm( number < 5 )\n";
        str += "  then\n";
        str += "    $alarm.incrementNumber();\n";
        str += "    list.add( $alarm );\n";
        str += "    update($alarm);\n";
        str += "end\n";

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( str ) );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list",
                           list );
        session.insert( new Alarm() );

        session.fireAllRules();

        Thread.sleep( 1000 );

        assertEquals( 5,
                      list.size() );
        assertEquals( 0,
                      session.getAgenda().getScheduledActivations().length );
    }
    
    @Test
    public void testFireRuleAfterDuration() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_FireRuleAfterDuration.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        final List list = new ArrayList();
        workingMemory.setGlobal( "list",
                                 list );

        final Cheese brie = new Cheese( "brie",
                                        12 );
        final FactHandle brieHandle = workingMemory.insert( brie );

        workingMemory.fireAllRules();

        // now check for update
        assertEquals( 0,
                      list.size() );

        // sleep for 300ms
        Thread.sleep( 300 );

        workingMemory.fireAllRules();

        // now check for update
        assertEquals( 2,
                      list.size() );

    }
    
    @Test
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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
    
    @Test
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
        
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
    
    @Test
    public void testUnknownProtocol() throws Exception {
        wrongTimerExpression("xyz:30");
    }

    @Test
    public void testMissingColon() throws Exception {
        wrongTimerExpression("int 30");
    }

    @Test
    public void testMalformedExpression() throws Exception {
        wrongTimerExpression("30s s30");
    }

    @Test
    public void testMalformedIntExpression() throws Exception {
        wrongTimerExpression("int 30s");
    }

    @Test
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

    @Test
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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
    
    @Test
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }

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
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }

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
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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
    
    @Test
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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
        assertEquals( 1, list.size() );
                      
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 2, list.size() );

        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 3, list.size() );
        
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 4, list.size() );
    }
    
    @Test
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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
        assertEquals( 1, list.size() );
                      
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 2, list.size() );

        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 3, list.size() );
        
        timeService.advanceTime( 60, TimeUnit.SECONDS );
        assertEquals( 4, list.size() );
    }
    
    @Test
    public void testCalendarsWithIntervalsAndStartAndEnd() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\"\n";
        str += "  timer (0d 1d start=3-JAN-2010 end=5-JAN-2010) "; //int: protocol is assumed
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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
        assertEquals( 0, list.size() );
                      
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );  // day 3
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );   // day 5    
        assertEquals( 3, list.size() );

        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        assertEquals( 3, list.size() );
    }
    
    @Test
    public void testCalendarsWithIntervalsAndStartAndLimit() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\"\n";
        str += "  timer (0d 1d start=3-JAN-2010 repeat-limit=4) "; //int: protocol is assumed
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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
        assertEquals( 0, list.size() );
                      
        timeService.advanceTime( oneDay, TimeUnit.SECONDS ); // day 3  
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );   // day 5    
        assertEquals( 3, list.size() );

        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        assertEquals( 3, list.size() );
    }
    
    @Test
    public void testCalendarsWithCronAndStartAndEnd() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\"\n";
        str += "  timer (cron: 0 0 0 * * ? start=3-JAN-2010 end=5-JAN-2010) ";
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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
        assertEquals( 0, list.size() );
                      
        timeService.advanceTime( oneDay, TimeUnit.SECONDS ); // day 3  
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );   // day 5  
        assertEquals( 3, list.size() );

        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        assertEquals( 3, list.size() );
    }

    @Test
    public void testCalendarsWithCronAndStartAndLimit() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  calendars \"cal1\"\n";
        str += "  timer (cron: 0 0 0 * * ? start=3-JAN-2010 repeat-limit=4) ";
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            System.out.println( kbuilder.getErrors() );
            assertTrue( kbuilder.hasErrors() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
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
        assertEquals( 0, list.size() );
                      
        timeService.advanceTime( oneDay, TimeUnit.SECONDS ); // day 3  
        assertEquals( 1, list.size() );
             
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        assertEquals( 2, list.size() );
        
        timeService.advanceTime( oneDay, TimeUnit.SECONDS );   // day 5    
        assertEquals( 3, list.size() );

        timeService.advanceTime( oneDay, TimeUnit.SECONDS );
        assertEquals( 3, list.size() );
    }
    
    @Test
    public void testTimerWithNot() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Timer_With_Not.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory.fireAllRules();
        Thread.sleep( 1500 );

        // now check that rule "wrap A" fired once, creating one B
        assertEquals( 2, workingMemory.getFactCount() );
    }

    @Test
    public void testHaltWithTimer() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Halt_With_Timer.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final StatefulSession workingMemory = ruleBase.newStatefulSession();

        new Thread( new Runnable(){
            public void run(){ workingMemory.fireUntilHalt(); }
            } ).start();
        Thread.sleep( 1000 );
        workingMemory.insert( "halt" );
        Thread.sleep( 2000 );

        // now check that rule "halt" fired once, creating one Integer
        assertEquals( 2, workingMemory.getFactCount() );
    }

    @Test
    public void testTimerRemoval() {
        try {
            String str = "package org.drools.test\n" +
                    "import " + TimeUnit.class.getName() + "\n" +
            		"global java.util.List list \n" +
            		"global " + CountDownLatch.class.getName() + " latch\n" + 
                    "rule TimerRule \n" + 
                    "   timer (int:0 50) \n" + 
                    "when \n" + 
                    "then \n" +
                    "        //forces it to pause until main thread is ready\n" +
                    "        latch.await(10, TimeUnit.MINUTES); \n" +
                    "        list.add(list.size()); \n" +  
                    " end";
            
            final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            // this will parse and compile in one step
            kbuilder.add(ResourceFactory.newByteArrayResource( str.getBytes()), ResourceType.DRL);

            // Check the builder for errors
            if (kbuilder.hasErrors()) {
                System.out.println(kbuilder.getErrors().toString());
                throw new RuntimeException("Unable to compile \"TimerRule.drl\".");
            }            

            // get the compiled packages (which are serializable)
            final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

            // add the packages to a knowledgebase (deploy the knowledge packages).
            final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages(pkgs);

            CountDownLatch latch = new CountDownLatch(1);
            final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            List list = Collections.synchronizedList( new ArrayList() );
            ksession.setGlobal( "list", list );
            ksession.setGlobal( "latch", latch );            
            
            ksession.fireAllRules();           
            Thread.sleep(200); // this makes sure it actually enters a rule
            kbase.removeRule("org.drools.test", "TimerRule");
            latch.countDown();
            Thread.sleep(100); // allow the last rule, if we were in the middle of one to actually fire, before clearing
            list.clear();
            Thread.sleep(500); // now wait to see if any more fire, they shouldn't
            assertEquals( 0, list.size() );
            ksession.dispose();
        } catch (InterruptedException e) {
            throw new RuntimeException( e );
        }
    }    
    
    
    
}
