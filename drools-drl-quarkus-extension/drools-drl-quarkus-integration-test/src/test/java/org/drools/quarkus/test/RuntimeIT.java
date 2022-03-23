/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.quarkus.test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.drools.drl.quarkus.runtime.KieRuntimeBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class RuntimeIT {

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    @Test
    public void testRuleEvaluation() {
        // canDrinkKS is the default session
        KieSession ksession = runtimeBuilder.newKieSession();

        List<String> pkgNames = ksession.getKieBase().getKiePackages().stream().map(KiePackage::getName).collect(Collectors.toList());
        assertEquals(2, pkgNames.size());
        assertTrue(pkgNames.contains("org.drools.quarkus.test"));
        assertTrue(pkgNames.contains("org.drools.simple.candrink"));

        Result result = new Result();
        ksession.insert(result);
        ksession.insert(new Person("Mark", 17));
        ksession.fireAllRules();

        assertEquals("Mark can NOT drink", result.toString());
    }

    @Test
    public void testCepEvaluation() {
        KieSession ksession = runtimeBuilder.newKieSession("cepKS");

        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));

        assertEquals(1, ksession.fireAllRules());

        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));

        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testFireUntiHalt() {
        KieSession ksession = runtimeBuilder.newKieSession("probeKS");

        new Thread(ksession::fireUntilHalt).start();
        final ProbeCounter pc = new ProbeCounter();

        ksession.insert(pc);
        for (int i = 0; i < 10; i++) {
            ksession.insert(new ProbeEvent(i));
        }

        synchronized (pc) {
            if (pc.getTotal() < 10) {
                try {
                    pc.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        assertEquals(10, pc.getTotal());
    }

    @Test
    public void testAllPkgsKBase() {
        KieBase kBase = runtimeBuilder.getKieBase("allKB");

        List<String> pkgNames = kBase.getKiePackages().stream().map(KiePackage::getName).collect(Collectors.toList());
        assertEquals(4, pkgNames.size());
        assertTrue(pkgNames.contains("org.drools.quarkus.test"));
        assertTrue(pkgNames.contains("org.drools.simple.candrink"));
        assertTrue(pkgNames.contains("org.drools.cep"));
        assertTrue(pkgNames.contains("org.drools.probe"));
    }
}
