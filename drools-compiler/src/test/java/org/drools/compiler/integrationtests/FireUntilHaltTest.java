/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FireUntilHaltTest extends CommonTestMethodBase {

    @Test
    public void testSubmitOnFireUntilHalt() throws InterruptedException {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list;" +
                "rule R when\n" +
                "    Person( happy, age >= 18 )\n" +
                "then\n" +
                "    list.add(\"happy adult\");" +
                "end";

        final KieSession kSession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        final List<String> list = new ArrayList<>();
        kSession.setGlobal("list", list);

        new Thread(kSession::fireUntilHalt).start();

        final Person p = new Person("me", 17, true);
        final FactHandle fh = kSession.insert(p);

        Thread.sleep(100L);
        assertEquals(0, list.size());

        kSession.submit(kieSession -> {
            p.setAge(18);
            p.setHappy(false);
            kieSession.update(fh, p);
        });

        Thread.sleep(100L);
        assertEquals(0, list.size());

        kSession.submit(kieSession -> {
            p.setHappy(true);
            kieSession.update(fh, p);
        });

        Thread.sleep(100L);
        assertEquals(1, list.size());

        kSession.halt();
        kSession.dispose();
    }

    @Test
    public void testFireAllWhenFiringUntilHalt() throws InterruptedException {
        final KieBase kbase = getKnowledgeBase();
        final KieSession ksession = createKnowledgeSession(kbase);

        final Thread t1 = new Thread(ksession::fireUntilHalt);
        final Thread t2 = new Thread(ksession::fireAllRules);
        t1.start();
        Thread.sleep(500);
        t2.start();
        // give the chance for t2 to finish
        Thread.sleep(1000);
        final boolean aliveT2 = t2.isAlive();
        ksession.halt();
        Thread.sleep(1000);
        final boolean aliveT1 = t1.isAlive();
        if (t2.isAlive()) {
            t2.interrupt();
        }
        if (t1.isAlive()) {
            t1.interrupt();
        }
        assertFalse("T2 should have finished", aliveT2);
        assertFalse("T1 should have finished", aliveT1);
    }

    @Test
    public void testFireUntilHaltFailingAcrossEntryPoints() throws Exception {
        String rule1 = "package org.drools.compiler\n";
        rule1 += "global java.util.List list\n";
        rule1 += "rule testFireUntilHalt\n";
        rule1 += "when\n";
        rule1 += "       Cheese()\n";
        rule1 += "  $p : Person() from entry-point \"testep2\"\n";
        rule1 += "then \n";
        rule1 += "  list.add( $p ) ;\n";
        rule1 += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(rule1);
        final KieSession ksession = createKnowledgeSession(kbase);
        final EntryPoint ep = ksession.getEntryPoint("testep2");

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert(new Cheese("cheddar"));
        ksession.fireAllRules();

        final Thread t1 = new Thread(ksession::fireUntilHalt);
        t1.start();

        Thread.sleep(500);
        ep.insert(new Person("darth"));
        Thread.sleep(500);
        ksession.halt();
        t1.join(5000);
        final boolean alive = t1.isAlive();
        if (alive) {
            t1.interrupt();
        }
        assertFalse("Thread should have died!", alive);
        assertEquals(1, list.size());
    }
}
