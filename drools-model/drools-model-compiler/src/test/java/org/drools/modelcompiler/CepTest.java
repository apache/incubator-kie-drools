/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.ClockType;
import org.drools.core.common.EventFactHandle;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.modelcompiler.domain.DateTimeHolder;
import org.drools.modelcompiler.domain.StockFact;
import org.drools.modelcompiler.domain.StockTick;
import org.drools.modelcompiler.domain.StockTickEx;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.time.Calendar;
import org.kie.api.time.SessionClock;
import org.kie.api.time.SessionPseudoClock;

import static org.drools.modelcompiler.util.EvaluationUtil.convertDate;
import static org.junit.Assert.assertEquals;

public class CepTest extends BaseModelTest {

    public CepTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    public static KieModuleModel getCepKieModuleModel() {
        KieModuleModel kproj = KieServices.get().newKieModuleModel();
        kproj.newKieBaseModel( "kb" )
                .setDefault( true )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .newKieSessionModel( "ks" )
                .setDefault( true ).setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        return kproj;
    }

    @Test
    public void testAfter() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "    $a : StockTick( company == \"DROO\" )\n" +
                        "    $b : StockTick( company == \"ACME\", this after[5s,8s] $a )\n" +
                        "then\n" +
                        "  System.out.println(\"fired\");\n" +
                        "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testNegatedAfter() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "    $a : StockTick( company == \"DROO\" )\n" +
                        "    $b : StockTick( company == \"ACME\", this not after[5s,8s] $a )\n" +
                        "then\n" +
                        "  System.out.println(\"fired\");\n" +
                        "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 0, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testAfterWithEntryPoints() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "    $a : StockTick( company == \"DROO\" ) from entry-point ep1\n" +
                        "    $b : StockTick( company == \"ACME\", this after[5s,8s] $a ) from entry-point ep2\n" +
                        "then\n" +
                        "  System.out.println(\"fired\");\n" +
                        "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.getEntryPoint( "ep1" ).insert( new StockTick( "DROO" ) );

        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.getEntryPoint( "ep1" ).insert( new StockTick( "ACME" ) );
        assertEquals( 0, ksession.fireAllRules() );

        clock.advanceTime( 1, TimeUnit.SECONDS );
        ksession.getEntryPoint( "ep2" ).insert( new StockTick( "ACME" ) );
        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.getEntryPoint( "ep2" ).insert( new StockTick( "ACME" ) );
        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testSlidingWindow() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "    $a : StockTick( company == \"DROO\" ) over window:length( 2 )\n" +
                        "then\n" +
                        "  System.out.println(\"fired\");\n" +
                        "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        clock.advanceTime( 1, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "DROO" ) );
        clock.advanceTime( 1, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "DROO" ) );
        clock.advanceTime( 1, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );
        clock.advanceTime( 1, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "DROO" ) );

        assertEquals( 2, ksession.fireAllRules() );
    }

    @Test
    public void testNotAfter() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "    $a : StockTick( company == \"DROO\" )\n" +
                "    not( StockTick( company == \"ACME\", this after[5s,8s] $a ) )\n" +
                "then\n" +
                "  System.out.println(\"fired\");\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick("DROO") );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockTick("ACME") );

        clock.advanceTime( 10, TimeUnit.SECONDS );
        assertEquals(0, ksession.fireAllRules());

        ksession.insert( new StockTick("DROO") );
        clock.advanceTime( 3, TimeUnit.SECONDS );
        ksession.insert( new StockTick("ACME") );

        clock.advanceTime( 10, TimeUnit.SECONDS );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindow() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare window DeclaredWindow\n" +
                "    StockTick( company == \"DROO\" ) over window:time( 5s )\n" +
                "end\n" +
                "rule R when\n" +
                "    $a : StockTick() from window DeclaredWindow\n" +
                "then\n" +
                "  System.out.println($a.getCompany());\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        clock.advanceTime( 2, TimeUnit.SECONDS );
        ksession.insert( new StockTick("DROO") );
        clock.advanceTime( 2, TimeUnit.SECONDS );
        ksession.insert( new StockTick("DROO") );
        clock.advanceTime( 2, TimeUnit.SECONDS );
        ksession.insert( new StockTick("ACME") );
        clock.advanceTime( 2, TimeUnit.SECONDS );
        ksession.insert( new StockTick("DROO") );

        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindowWithEntryPoint() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "declare window DeclaredWindow\n" +
                "    StockTick( company == \"DROO\" ) over window:time( 5s ) from entry-point ticks\n" +
                "end\n" +
                "rule R when\n" +
                "    $a : StockTick() from window DeclaredWindow\n" +
                "then\n" +
                "  System.out.println($a.getCompany());\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        EntryPoint ep = ksession.getEntryPoint("ticks");

        clock.advanceTime( 2, TimeUnit.SECONDS );
        ep.insert( new StockTick("DROO") );
        clock.advanceTime( 2, TimeUnit.SECONDS );
        ep.insert( new StockTick("DROO") );
        clock.advanceTime( 2, TimeUnit.SECONDS );
        ep.insert( new StockTick("ACME") );
        clock.advanceTime( 2, TimeUnit.SECONDS );
        ep.insert( new StockTick("DROO") );

        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindowOnEventInTypeDeclaration() throws Exception {
        String str =
                "declare String\n" +
                "  @role( event )\n" +
                "end\n" +
                "declare window DeclaredWindow\n" +
                "    String( ) over window:time( 5s )\n" +
                "end\n" +
                "rule R when\n" +
                "    $a : String( this == \"DROO\" ) from window DeclaredWindow\n" +
                "then\n" +
                "  System.out.println($a);\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( "ACME" );
        ksession.insert( "DROO" );

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindowOnDeclaredType() throws Exception {
        String str =
                "declare MyEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "declare window DeclaredWindow\n" +
                "    MyEvent( ) over window:time( 5s )\n" +
                "end\n" +
                "rule Init when\n" +
                "then\n" +
                "  insert(new MyEvent());\n" +
                "end\n" +
                "rule R when\n" +
                "    $a : MyEvent() from window DeclaredWindow\n" +
                "then\n" +
                "  System.out.println($a);\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindowWith2Arguments() throws Exception {
        String str =
                "declare String\n" +
                "  @role( event )\n" +
                "end\n" +
                "declare window DeclaredWindow\n" +
                "    String( length == 4, this.startsWith(\"D\") ) over window:time( 5s )\n" +
                "end\n" +
                "rule R when\n" +
                "    $a : String() from window DeclaredWindow\n" +
                "then\n" +
                "  System.out.println($a);\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( "ACME" );
        ksession.insert( "DROO" );

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testWithDeclaredEvent() throws Exception {
        String str =
                "import " + StockFact.class.getCanonicalName() + ";\n" +
                "declare StockFact @role( event ) end;\n" +
                "rule R when\n" +
                "    $a : StockFact( company == \"DROO\" )\n" +
                "    $b : StockFact( company == \"ACME\", this after[5s,8s] $a )\n" +
                "then\n" +
                "  System.out.println(\"fired\");\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockFact( "DROO" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockFact( "ACME" ) );

        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockFact( "ACME" ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testExpireEventOnEndTimestamp() throws Exception {
        String str =
                "package org.drools.compiler;\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "global java.util.List resultsAfter;\n" +
                "\n" +
                "rule \"after[60,80]\"\n" +
                "when\n" +
                "$a : StockTick( company == \"DROO\" )\n" +
                "$b : StockTick( company == \"ACME\", this after[60,80] $a )\n" +
                "then\n" +
                "       resultsAfter.add( $b );\n" +
                "end";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        List<StockTick> resultsAfter = new ArrayList<StockTick>();
        ksession.setGlobal("resultsAfter", resultsAfter);

        // inserting new StockTick with duration 30 at time 0 => rule
        // after[60,80] should fire when ACME lasts at 100-120
        ksession.insert(new StockTick("DROO", 30));

        clock.advanceTime(100, TimeUnit.MILLISECONDS);

        ksession.insert(new StockTick("ACME", 20));

        ksession.fireAllRules();

        assertEquals(1, resultsAfter.size());
    }

    @Test
    public void testExpireEventOnEndTimestampWithDeclaredEvent() throws Exception {
        String str =
                "package org.drools.compiler;\n" +
                "import " + StockFact.class.getCanonicalName() + ";\n" +
                "global java.util.List resultsAfter;\n" +
                "\n" +
                "declare StockFact\n" +
                "    @role( event )\n" +
                "    @duration( duration )\n" +
                "end\n" +
                "\n" +
                "rule \"after[60,80]\"\n" +
                "when\n" +
                "$a : StockFact( company == \"DROO\" )\n" +
                "$b : StockFact( company == \"ACME\", this after[60,80] $a )\n" +
                "then\n" +
                "       resultsAfter.add( $b );\n" +
                "end";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        List<StockTick> resultsAfter = new ArrayList<StockTick>();
        ksession.setGlobal("resultsAfter", resultsAfter);

        // inserting new StockTick with duration 30 at time 0 => rule
        // after[60,80] should fire when ACME lasts at 100-120
        ksession.insert(new StockFact("DROO", 30));

        clock.advanceTime(100, TimeUnit.MILLISECONDS);

        ksession.insert(new StockFact("ACME", 20));

        ksession.fireAllRules();

        assertEquals(1, resultsAfter.size());
    }

    @Test
    public void testExpires() throws Exception {
        String str =
                "package org.drools.compiler;\n" +
                "import " + StockFact.class.getCanonicalName() + ";\n" +
                "\n" +
                "declare StockFact\n" +
                "    @role( value = event )\n" +
                "    @expires( value = 2s, policy = TIME_SOFT )\n" +
                "end\n" +
                "\n" +
                "rule \"expiration\"\n" +
                "when\n" +
                "   StockFact( company == \"DROO\" )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockFact("DROO"));

        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(1, ksession.getObjects().size());

        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(0, ksession.getObjects().size());
    }
    
    @Test
    public void testDeclareAndExpires() throws Exception {
        String str =
                "package org.drools.compiler;\n" +
                "declare StockFact\n" +
                "    @role( value = event )\n" +
                "    @expires( value = 2s, policy = TIME_SOFT )\n" +
                "    company : String\n" +
                "    duration : long\n" +
                "end\n" +
                "\n" +
                "rule \"expiration\"\n" +
                "when\n" +
                "   StockFact( company == \"DROO\" )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        FactType stockFactType = ksession.getKieBase().getFactType("org.drools.compiler", "StockFact");
        Object DROO = stockFactType.newInstance();
        stockFactType.set(DROO, "company", "DROO");

        ksession.insert(DROO);

        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(1, ksession.getObjects().size());

        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.fireAllRules();
        assertEquals(0, ksession.getObjects().size());
    }

    @Test
    public void testNoEvent() {
        String str =
                "declare BaseEvent\n" +
                "  @role(event)\n" +
                "end\n" +
                "\n" +
                "declare Event extends BaseEvent\n" +
                "  @role(event)\n" +
                "  property : String\n" +
                "end\n" +
                "\n" +
                "declare NotEvent extends BaseEvent\n" +
                "  @role(event)\n" +
                "  property : String\n" +
                "end\n" +
                "\n" +
                "rule \"not equal\" when\n" +
                "    not (\n" +
                "      ( and\n" +
                "          $e : BaseEvent( ) over window:length(3) from entry-point entryPoint\n" +
                "          NotEvent( this == $e, property == \"value\" ) from entry-point entryPoint\n" +
                "      )\n" +
                "    )\n" +
                "then\n" +
                "\n" +
                "end";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        assertEquals( 1, ksession.fireAllRules() );
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

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

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

    @Test
    public void testAfterWithAnd() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "    $a : StockTick( company == \"DROO\" )\n" +
                "    $b : StockTick( company == \"ACME\" && this after[5s,8s] $a )\n" +
                "then\n" +
                "  System.out.println(\"fired\");\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testAfterOnLongFields() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "    $a : StockTick( company == \"DROO\" )\n" +
                "    $b : StockTick( company == \"ACME\", timeFieldAsLong after[5,8] $a.timeFieldAsLong )\n" +
                "then\n" +
                "  System.out.println(\"fired\");\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ).setTimeField( 0 ) );
        ksession.insert( new StockTick( "ACME" ).setTimeField( 6 ) );

        assertEquals( 1, ksession.fireAllRules() );

        ksession.insert( new StockTick( "ACME" ).setTimeField( 10 ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testAfterOnDateFields() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "    $a : StockTick( company == \"DROO\" )\n" +
                "    $b : StockTick( company == \"ACME\", timeFieldAsDate after[5,8] $a.timeFieldAsDate )\n" +
                "then\n" +
                "  System.out.println(\"fired\");\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ).setTimeField( 0 ) );
        ksession.insert( new StockTick( "ACME" ).setTimeField( 6 ) );

        assertEquals( 1, ksession.fireAllRules() );

        ksession.insert( new StockTick( "ACME" ).setTimeField( 10 ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testAfterOnDateFieldsWithBinding() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "    $a : StockTick( company == \"DROO\", $aTime : timeFieldAsDate )\n" +
                "    $b : StockTick( company == \"ACME\", timeFieldAsDate after[5,8] $aTime )\n" +
                "then\n" +
                "  System.out.println(\"fired\");\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        clock.advanceTime( 100, TimeUnit.MILLISECONDS );
        ksession.insert( new StockTick( "DROO" ).setTimeField( 0 ) );
        ksession.insert( new StockTick( "ACME" ).setTimeField( 6 ) );

        assertEquals( 1, ksession.fireAllRules() );

        ksession.insert( new StockTick( "ACME" ).setTimeField( 10 ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testAfterOnFactAndField() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "    $a : StockTick( company == \"DROO\" )\n" +
                "    $b : StockTick( company == \"ACME\", timeFieldAsLong after[5,8] $a )\n" +
                "then\n" +
                "  System.out.println(\"fired\");\n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ).setTimeField( 0 ) );
        clock.advanceTime( 6, TimeUnit.MILLISECONDS );
        ksession.insert( new StockTick( "ACME" ).setTimeField( 6 ) );

        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.MILLISECONDS );
        ksession.insert( new StockTick( "ACME" ).setTimeField( 10 ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testComplexTimestamp() {
        final String str =
                "import " + Message.class.getCanonicalName() + "\n" +
                "declare " + Message.class.getCanonicalName() + "\n" +
                "   @role( event ) \n" +
                "   @timestamp( getProperties().get( 'timestamp' ) - 1 ) \n" +
                "   @duration( getProperties().get( 'duration' ) + 1 ) \n" +
                "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);

        try {
            final Message msg = new Message();
            final Properties props = new Properties();
            props.put("timestamp", 99);
            props.put("duration", 52);
            msg.setProperties(props);

            final EventFactHandle efh = (EventFactHandle) ksession.insert(msg);
            assertEquals(98, efh.getStartTimestamp());
            assertEquals(53, efh.getDuration());
        } finally {
            ksession.dispose();
        }
    }

    public static class Message {

        private Properties properties;
        private Timestamp timestamp;
        private Long duration;

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(final Properties properties) {
            this.properties = properties;
        }

        public Timestamp getStartTime() {
            return timestamp;
        }

        public void setStartTime(final Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public Long getDuration() {
            return duration;
        }

        public void setDuration(final Long duration) {
            this.duration = duration;
        }
    }

    @Test
    public void testTimerWithMillisPrecision() {
        final String drl = "import " + MyEvent.class.getCanonicalName() + "\n" +
                "import " + AtomicInteger.class.getCanonicalName() + "\n" +
                "declare MyEvent\n" +
                "    @role( event )\n" +
                "    @timestamp( timestamp )\n" +
                "    @expires( 10ms )\n" +
                "end\n" +
                "\n" +
                "rule R\n" +
                "    timer (int: 0 1; start=$startTime, repeat-limit=0 )\n" +
                "    when\n" +
                "       $event: MyEvent ($startTime : timestamp)\n" +
                "       $counter : AtomicInteger(get() > 0)\n" +
                "    then\n" +
                "        System.out.println(\"RG_TEST_TIMER WITH \" + $event + \" AND \" + $counter);\n" +
                "        modify($counter){\n" +
                "            decrementAndGet()\n" +
                "        }\n" +
                "end";

        KieSession ksession = getKieSession(getCepKieModuleModel(), drl);

        try {
            final long now = 1000;
            final PseudoClockScheduler sessionClock = ksession.getSessionClock();
            sessionClock.setStartupTime(now - 10);

            final AtomicInteger counter = new AtomicInteger(1);
            final MyEvent event1 = new MyEvent(now - 8);
            final MyEvent event2 = new MyEvent(now - 7);
            final MyEvent event3 = new MyEvent(now - 6);

            ksession.insert(counter);
            ksession.insert(event1);
            ksession.insert(event2);
            ksession.insert(event3);

            ksession.fireAllRules(); // Nothing Happens
            assertEquals(1, counter.get());

            sessionClock.advanceTime(10, TimeUnit.MILLISECONDS);
            ksession.fireAllRules();

            assertEquals(0, counter.get());
        } finally {
            ksession.dispose();
        }
    }

    public static class MyEvent {
        private long timestamp;
        public MyEvent(final long timestamp ) { this.timestamp = timestamp; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(final long timestamp ) { this.timestamp = timestamp; }
        public String toString() { return "MyEvent{" + "timestamp=" + timestamp + '}';  }
    }

    public static class Quote {

        private String symbol;
        private Double price;

        public Quote() {
        }

        public Quote( String symbol, Double price ) {
            this.symbol = symbol;
            this.price = price;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol( String symbol ) {
            this.symbol = symbol;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice( Double price ) {
            this.price = price;
        }
    }

    public enum OrderType {
        SELL,
        BUY
    }

    public static class Order {
        private String username;
        private OrderType orderType;

        private Long timestamp;
        private String quote;
        private Double price;
        private Integer number;

        public Order() {
        }

        public Order( String quote, OrderType orderType ) {
            this.quote = quote;
            this.orderType = orderType;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername( String username ) {
            this.username = username;
        }

        public OrderType getOrderType() {
            return orderType;
        }

        public void setOrderType( OrderType ordertype ) {
            this.orderType = ordertype;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp( Long timestamp ) {
            this.timestamp = timestamp;
        }

        public String getQuote() {
            return quote;
        }

        public void setQuote( String quote ) {
            this.quote = quote;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice( Double price ) {
            this.price = price;
        }
    }

    @Test
    public void testCollectWithDeclaredWindow() {
        // DROOLS-4492
        final String drl =
                "import " + Quote.class.getCanonicalName() + "\n" +
                "import " + Order.class.getCanonicalName() + "\n" +
                "import " + OrderType.class.getCanonicalName() + "\n" +
                "import java.util.List\n" +
                "\n" +
                "declare Order\n" +
                "  @role(event)\n" +
                "  @expires( 2m )\n" +
                "end\n" +
                "\n" +
                "declare window LastThreeOrders\n" +
                "    Order() over window:length(3)\n" +
                "end\n" +
                "\n" +
                "rule \"3 BUY Orders of same Quote\"\n" +
                "when\n" +
                "    $q : Quote()\n" +
                "    $list : List( size == 3 ) from collect ( Order( orderType == OrderType.BUY , quote == $q.symbol ) from window LastThreeOrders )\n" +
                "then\n" +
                "    System.out.println(drools.getRule().getName());\n" +
                "    $q.setPrice($q.getPrice()+0.01);\n" +
                "    for ( Object $o : $list){\n" +
                "        System.out.println(\"Retracting \"+$o);\n" +
                "        delete($o);\n" +
                "    }\n" +
                "end";

        KieSession ksession = getKieSession( getCepKieModuleModel(), drl );

        try {
            ksession.insert( new Quote("RHT", 10.0) );
            ksession.insert( new Order("RHT", OrderType.BUY) );
            ksession.insert( new Order("RHT", OrderType.BUY) );
            ksession.insert( new Order("RHT", OrderType.BUY) );

            assertEquals( 1, ksession.fireAllRules() );

        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAfterBindingFirst() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "    $a : StockTick( company == \"DROO\" )\n" +
                        "    $b : StockTick( company == \"ACME\", $a after[5s,8s] this )\n" +
                        "then\n" +
                        "  System.out.println(\"fired\");\n" +
                        "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "ACME" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "DROO" ) );

        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "DROO" ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testAfterOnLongFieldsBindingFirst() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                     "declare StockTick @timestamp(timeFieldAsLong) end\n" +
                     "rule R when\n" +
                     "    $a : StockTick( company == \"DROO\" )\n" +
                     "    StockTick( company == \"ACME\", $a.timeFieldAsLong after[5,8] timeFieldAsLong )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockTick("ACME").setTimeField(0));
        ksession.insert(new StockTick("DROO").setTimeField(6));

        assertEquals(1, ksession.fireAllRules());

        ksession.insert(new StockTick("DROO").setTimeField(10));

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testBefore() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "    $a : StockTick( company == \"DROO\" )\n" +
                        "    $b : StockTick( company == \"ACME\", this before[5s,8s] $a )\n" +
                        "then\n" +
                        "  System.out.println(\"fired\");\n" +
                        "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "ACME" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "DROO" ) );

        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "DROO" ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testBeforeBindingFirst() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "    $a : StockTick( company == \"DROO\" )\n" +
                        "    $b : StockTick( company == \"ACME\", $a before[5s,8s] this )\n" +
                        "then\n" +
                        "  System.out.println(\"fired\");\n" +
                        "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 1, ksession.fireAllRules() );

        clock.advanceTime( 4, TimeUnit.SECONDS );
        ksession.insert( new StockTick( "ACME" ) );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testBeforeOnLongFields() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                     "declare StockTick @timestamp(timeFieldAsLong) end\n" +
                     "rule R when\n" +
                     "    $a : StockTick( company == \"DROO\" )\n" +
                     "    $b : StockTick( company == \"ACME\", timeFieldAsLong before[5,8] $a.timeFieldAsLong )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockTick("ACME").setTimeField(0));
        ksession.insert(new StockTick("DROO").setTimeField(6));

        assertEquals(1, ksession.fireAllRules());

        ksession.insert(new StockTick("DROO").setTimeField(10));

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testBeforeOnLongFieldsBindingFirst() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                     "declare StockTick @timestamp(timeFieldAsLong) end\n" +
                     "rule R when\n" +
                     "    $a : StockTick( company == \"DROO\" )\n" +
                     "    $b : StockTick( company == \"ACME\", $a.timeFieldAsLong before[5,8] timeFieldAsLong )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockTick("DROO").setTimeField(0));
        ksession.insert(new StockTick("ACME").setTimeField(6));

        assertEquals(1, ksession.fireAllRules());

        ksession.insert(new StockTick("ACME").setTimeField(10));

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testBeforeOnLongFieldsWithDifferentMethod() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                     "import " + StockTickEx.class.getCanonicalName() + ";\n" +
                     "declare StockTick @timestamp(timeFieldAsLong) end\n" +
                     "declare StockTickEx @timestamp(timeFieldExAsLong) end\n" +
                     "rule R when\n" +
                     "    $a : StockTickEx( company == \"DROO\" )\n" +
                     "    StockTick( company == \"ACME\", timeFieldAsLong before[5,8] $a.timeFieldExAsLong )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockTick("ACME").setTimeField(0));
        ksession.insert(new StockTickEx("DROO").setTimeFieldEx(6));

        assertEquals(1, ksession.fireAllRules());

        ksession.insert(new StockTickEx("DROO").setTimeFieldEx(10));

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testAfterOnLongFieldsBindingFirstWithDifferentMethod() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                     "import " + StockTickEx.class.getCanonicalName() + ";\n" +
                     "declare StockTick @timestamp(timeFieldAsLong) end\n" +
                     "declare StockTickEx @timestamp(timeFieldExAsLong) end\n" +
                     "rule R when\n" +
                     "    $a : StockTickEx( company == \"DROO\" )\n" +
                     "    StockTick( company == \"ACME\", $a.timeFieldExAsLong after[5,8] timeFieldAsLong )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockTick("ACME").setTimeField(0));
        ksession.insert(new StockTickEx("DROO").setTimeFieldEx(6));

        assertEquals(1, ksession.fireAllRules());

        ksession.insert(new StockTickEx("DROO").setTimeFieldEx(10));

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testLiteral() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                     "declare StockTick @timestamp(timeFieldAsLong) end\n" +
                     "rule R when\n" +
                     "    $a : StockTick( timeFieldAsLong after \"01-Jan-2016\" )\n" +
                     "then\n" +
                     "  System.out.println(\"fired\");\n" +
                     "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);

        long time = LocalDateTime.of(2020, 1, 1, 0, 0, 0).atZone(ZoneId.of("UTC")).toInstant().getEpochSecond() * 1000;
        ksession.insert(new StockTick("DROO").setTimeField(time));

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testZonedDateTime() throws Exception {
        String str =
                "import " + DateTimeHolder.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "    $a : DateTimeHolder(  )\n" +
                        "    $b : DateTimeHolder( zonedDateTime after[5s,8s] $a.zonedDateTime )\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new DateTimeHolder( ZonedDateTime.now() ) );
        ksession.insert( new DateTimeHolder( ZonedDateTime.now().plusSeconds(6) ) );

        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testCalendarsWithCronAndStartAndEnd() throws Exception {
        final Locale defaultLoc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK); // Because of the date strings in the DRL, fixable with JBRULES-3444
            final String str =
                    "package org.simple \n" +
                            "global java.util.List list \n" +
                            "rule xxx \n" +
                            "  date-effective \"02-Jan-2010\"\n" +
                            "  date-expires \"06-Jan-2010\"\n" +
                            "  calendars \"cal1\"\n" +
                            "  timer (cron: 0 0 0 * * ?) " +
                            "when \n" +
                            "then \n" +
                            "  list.add(\"fired\"); \n" +
                            "end  \n";

            KieSession ksession = getKieSession(getCepKieModuleModel(), str);
            try {
                final List list = new ArrayList();
                final PseudoClockScheduler timeService = ksession.getSessionClock();
                final Date date = convertDate("01-Jan-2010");

                final Calendar cal1 = timestamp -> true;

                final long oneDay = 60 * 60 * 24;
                ksession.getCalendars().set("cal1", cal1);
                ksession.setGlobal("list", list);

                timeService.advanceTime(date.getTime(), TimeUnit.MILLISECONDS);
                ksession.fireAllRules();
                assertEquals(0, list.size());

                timeService.advanceTime(oneDay, TimeUnit.SECONDS);
                ksession.fireAllRules();
                assertEquals(0, list.size());

                timeService.advanceTime(oneDay, TimeUnit.SECONDS); // day 3
                ksession.fireAllRules();
                assertEquals(1, list.size());

                timeService.advanceTime(oneDay, TimeUnit.SECONDS);
                ksession.fireAllRules();
                assertEquals(2, list.size());

                timeService.advanceTime(oneDay, TimeUnit.SECONDS);   // day 5
                ksession.fireAllRules();
                assertEquals(3, list.size());

                timeService.advanceTime(oneDay, TimeUnit.SECONDS);
                ksession.fireAllRules();
                assertEquals(3, list.size());
            } finally {
                ksession.dispose();
            }
        } finally {
            Locale.setDefault(defaultLoc);
        }
    }
}
