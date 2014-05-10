package org.drools.compiler.integrationtests;

import java.util.concurrent.TimeUnit;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.TimedRuleExectionOption;

/**
 * Run all the tests with the ReteOO engine implementation
 */
public class PseudoClockDoubleNotTest extends CommonTestMethodBase {

    @Test
    public void work3() {
        final boolean doPseudo = true;   //Set to false to test using realtime clock

        StringBuilder sb = new StringBuilder();
        sb.append("package org.drools.compiler\n");
        sb.append("import org.drools.compiler.Cheese;\n");

        sb.append("declare Cheese\n"
                + " @role ( event )\n"
                + "end\n\n");

        sb.append("rule \"Template1\"\n"
                + "\n"
                + "salience 0\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "  $s : Cheese(type==\"smelly\")\n"
                + "  not( Cheese(type==\"a\", this after [0s,3s] $s ) )\n" //First has only 1 'not'
                //                + "  not( Cheese(type==\"b\", this after [0s,5s] $s ) )\n"
                + "\n"
                + "then\n"
                + "  System.out.println(\"Template 1 fired\")\n"
                + "  $s.price=10;\n"
                + "\n"
                + "end\n");
        sb.append("rule \"Template2\"\n"
                + "\n"
                + "salience 0\n"
                + "dialect \"mvel\"\n"
                + "when\n"
                + "  $s : Cheese(type==\"stinky\")\n"
                + "  not( Cheese(type==\"a\", this after [0s,3s] $s ) )\n"
                + "  not( Cheese(type==\"b\", this after [0s,5s] $s ) )\n" //2 * not
                + "\n"
                + "then\n"
                + "  System.out.println(\"Template 2 fired\")\n"
                + "  $s.price=10;\n"
                + "\n"
                + "end\n");

        KieServices kieServices = KieServices.Factory.get();

        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write("src/main/resources/rules.drl", sb.toString());

        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs);
        kieBuilder.buildAll();
        assertEquals(0, kieBuilder.getResults().getMessages().size());

        KieBaseConfiguration kieBaseConfiguration = kieServices.newKieBaseConfiguration();
        kieBaseConfiguration.setOption(EventProcessingOption.STREAM);

        KieContainer kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());

        KieBase kieBase = kieContainer.newKieBase(kieBaseConfiguration);
        KieSessionConfiguration ksconf = kieServices.newKieSessionConfiguration();
        ksconf.setOption(TimedRuleExectionOption.YES);

        if (doPseudo) {
            ksconf.setOption(ClockTypeOption.get("pseudo"));
        }

        KieSession ksession = kieBase.newKieSession(ksconf, null);
        class FireThread implements Runnable {

            KieSession ksession;

            public FireThread(KieSession _k) {
                ksession = _k;
            }

            public void run() {
                ksession.fireUntilHalt();
                System.out.println("FireThread stop");
            }

        }
        Thread t = new Thread(new FireThread(ksession));
        t.start();

        try {
            //Insert a smelly cheese.  Template 1 should fire after 3 seconds passed
            Cheese c = new Cheese("smelly");
            ksession.insert(c);
            if (doPseudo) {
                SessionPseudoClock clock = ksession.getSessionClock();
                clock.advanceTime(10, TimeUnit.SECONDS);
                Thread.sleep(1000);   //sleep a bit to allow the fire to happen
            } else {
                //sleep for 10 seconds for the realtime clock to pass
                Thread.sleep(11000);
            }
            System.out.println("Cheese=" + c);
            //If template 1 fired, price would be set to 0
            assertEquals("Template1 didn't fire", 10, c.getPrice());

            //Insert a stinky cheese. Template 2 should fire after 5 seconds passed
            c = new Cheese("stinky");
            ksession.insert(c);
            if (doPseudo) {
                SessionPseudoClock clock = ksession.getSessionClock();
                clock.advanceTime(10, TimeUnit.SECONDS);
                Thread.sleep(1000);   //sleep a bit to allow the fire to happen
            } else {
                //sleep for 10 seconds for the realtime clock to pass
                Thread.sleep(11000);

            }
            System.out.println("Cheese=" + c);
            //If template 2 fired, price would be set to 0

            assertEquals("Template2 didn't fire", 10, c.getPrice());
        } catch (InterruptedException e) {
        } finally {
            System.out.println("End");
            ksession.halt();
            ksession.dispose();
        }
    }

}
