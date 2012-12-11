package org.drools.integrationtests;

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
import org.drools.FactA;
import org.drools.FactHandle;
import org.drools.Foo;
import org.drools.Pet;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.io.impl.ByteArrayResource;
import org.drools.rule.Package;
import org.drools.time.impl.PseudoClockScheduler;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.conf.ClockTypeOption;
import org.kie.time.Calendar;
import org.kie.time.SessionClock;

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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        Locale defaultLoc = Locale.getDefault();
        try {
            Locale.setDefault( Locale.UK ); // Because of the date strings in the DRL, fixable with JBRULES-3444
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
    
            KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        } finally {
            Locale.setDefault( defaultLoc );
        }
    }

    @Test
    public void testCalendarsWithCronAndStartAndLimit() throws Exception {
        Locale defaultLoc = Locale.getDefault();
        try {
            Locale.setDefault( Locale.UK ); // Because of the date strings in the DRL, fixable with JBRULES-3444
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

            KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
        } finally {
            Locale.setDefault( defaultLoc );
        }
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
        FactHandle handle = workingMemory.insert( "halt" );
        Thread.sleep( 2000 );

        // now check that rule "halt" fired once, creating one Integer
        assertEquals( 2, workingMemory.getFactCount() );
        workingMemory.retract( handle );
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

    @Test
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
    public void testIntervalTimerWithStringExpressions() throws Exception {
        String str = "package org.simple;\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare Bean\n" +
                "  delay   : String = \"30s\"\n" +
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
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
    public void testIntervalTimerExpressionWithOr() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
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

        kbuilder.add( ResourceFactory.newByteArrayResource( text.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
        
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

        timeService.advanceTime( 900, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        assertEquals( fact1, list.get( 0 ) );
        assertEquals( fact1, list.get( 1 ) );
        
        timeService.advanceTime( 5000, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 4, list.size() );
        assertEquals( fact3, list.get( 2 ) );
        assertEquals( fact3, list.get( 3 ) );  
    }

    @Test
    public void testExprTimeRescheduled() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        String text = "package org.kie.test\n"
                      + "global java.util.List list\n"
                      + "import " + FactA.class.getCanonicalName() + "\n"
                      + "rule r1 timer (expr: f1.field2, f1.field4)\n"
                      + "when\n"                      
                      + "    f1 : FactA() \n"                      
                      + "then\n"
                      + "    list.add( f1 );\n"
                      + "end\n" + "\n";

        kbuilder.add( ResourceFactory.newByteArrayResource( text.getBytes() ), ResourceType.DRL );
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption( ClockTypeOption.get( "pseudo" ) );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession( conf, null );
        
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

        timeService.advanceTime( 2600, TimeUnit.MILLISECONDS );
        ksession.fireAllRules();
        assertEquals( 3, list.size() );
        assertEquals( fact1, list.get( 0 ) );
        assertEquals( fact1, list.get( 1 ) );
        assertEquals( fact1, list.get( 2 ) );
        list.clear();
        
        fact1.setField2( 300 );
        fact1.setField4( 2000 );     
        ksession.update(  fh, fact1 );
        
        // 100 has passed of the 1000, from the previous schedule
        // so that should be deducted from the 300 delay above, meaning 
        //  we only need to increment another 250
        timeService.advanceTime( 250, TimeUnit.MILLISECONDS );
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
    
    
    @Test @Ignore // TODO: fails randomly FIXME
    public void testHaltAfterSomeTimeThenRestart() throws Exception {
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
        
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new ByteArrayResource( drl.getBytes() ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final StatefulSession ksession = ruleBase.newStatefulSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        new Thread( new Runnable(){
            public void run(){ ksession.fireUntilHalt(); }
        } ).start();
        Thread.sleep( 250 );

        ksession.insert( "halt" );
        ksession.insert( "trigger" );
        Thread.sleep( 300 );

        new Thread( new Runnable(){
            public void run(){ ksession.fireUntilHalt(); }
        } ).start();
        Thread.sleep( 200 );

        assertEquals( java.util.Arrays.asList( 0, 0, 0, 5, 0, 0, 0, -5, 0, 0 ), list );
    }



    @Test @Ignore // TODO: fix random failures
    public void testHaltAfterSomeTimeThenRestartButNoLongerHolding() throws Exception {
        String drl = "package org.kie.test;" +
                "global java.util.List list; \n" +
                "\n" +
                "\n" +
                "rule FireAtWill\n" +
                "timer(int:0 100)\n" +
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

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new ByteArrayResource( drl.getBytes() ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final StatefulSession ksession = ruleBase.newStatefulSession();

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        FactHandle handle = ksession.insert( "trigger" );
        new Thread( new Runnable(){
            public void run(){ ksession.fireUntilHalt(); }
        } ).start();
        Thread.sleep( 150 );

        ksession.insert( "halt" );

        Thread.sleep( 200 );
        ksession.retract( handle );

        new Thread( new Runnable(){
            public void run(){ ksession.fireUntilHalt(); }
        } ).start();
        Thread.sleep( 200 );

        assertEquals( 2, list.size() );
        assertEquals( java.util.Arrays.asList( 0, 0 ), list );
    }



}
