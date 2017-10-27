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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.modelcompiler.domain.StockFact;
import org.drools.modelcompiler.domain.StockTick;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionPseudoClock;

import static org.junit.Assert.assertEquals;

public class CepTest extends BaseModelTest {

    public CepTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    private KieModuleModel getCepKieModuleModel() {
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
//        WindowReference w = window(
//                Window.Type.TIME,
//                5,
//                TimeUnit.SECONDS,
//                StockTick.class,
//              s -> s.getCompany().equals( "DROO" )
// );
//        Variable<StockTick> drooV = declarationOf( type( StockTick.class ), window );
//
//        Rule rule = rule( "window" )
//                .build(
//                        input(drooV),
//                        on(drooV).execute(s -> System.out.println(s.getCompany()))
//                      );

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
}
