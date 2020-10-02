/*
 * Copyright 2007 Red Hat, Inc. and/or its affiliates.
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
 *
 * Created on Dec 14, 2007
 */
package org.drools.mvel.integrationtests;

import java.util.concurrent.TimeUnit;

import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.StockTick;
import org.drools.mvel.compiler.StockTickInterface;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionClock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests related to the pseudo session clock
 */
public class PseudoClockEventsTest extends CommonTestMethodBase {

    private static final String evalFirePseudoClockDeclaration =
            "package org.drools.mvel.integrationtests\n" +
            "import " + StockTick.class.getCanonicalName() + "\n" +
            "\n" +
            "declare StockTick\n" +
            "    @role( event )\n" +
            //"    @expires( 1m )\n" +
            "end\n\n";
    private static final String evalFirePseudoClockRuleA       =
            "rule A\n" +
            "when\n" +
            "	$a: StockTick( $priceA: price )\n" +
            "	$b: StockTick( $priceA < price )\n" +
            "then \n" +
            "    System.out.println(\"Rule A fired by thread \" + Thread.currentThread().getName() + \": \" + $a + \", \" + $b);\n" +
            "end\n" +
            "";
    private static final String evalFirePseudoClockRuleB       =
            "rule B\n" +
            "when\n" +
            "	$a: StockTick()\n" +
            "	not( StockTick( this after[1,10s] $a ) )\n" +
            "then \n" +
            "    System.out.println(\"Rule B fired by thread \" + Thread.currentThread().getName());\n" +
            "end\n" +
            "";
    int evalFirePseudoClockStockCount = 5;

    @Test(timeout = 10000)
    public void testEvenFirePseudoClockRuleA() throws Exception {

        AgendaEventListener ael = mock(AgendaEventListener.class);

        processStocks(evalFirePseudoClockStockCount, ael,
                      evalFirePseudoClockDeclaration + evalFirePseudoClockRuleA);

        verify(ael,
               times(evalFirePseudoClockStockCount * (evalFirePseudoClockStockCount - 1) / 2)).afterMatchFired(
                any(AfterMatchFiredEvent.class));
    }

    @Test(timeout = 10000)
    public void testEvenFirePseudoClockRuleB() throws Exception {

        AgendaEventListener ael = mock(AgendaEventListener.class);

        processStocks(evalFirePseudoClockStockCount, ael,
                      evalFirePseudoClockDeclaration + evalFirePseudoClockRuleB);

        verify(ael,
               times(evalFirePseudoClockStockCount - 1)).afterMatchFired(
                any(AfterMatchFiredEvent.class));
    }

    @Test(timeout = 60000)
    public void testEvenFirePseudoClockRulesAB() throws Exception {

        AgendaEventListener ael = mock(AgendaEventListener.class);

        processStocks(evalFirePseudoClockStockCount, ael,
                      evalFirePseudoClockDeclaration + evalFirePseudoClockRuleA + evalFirePseudoClockRuleB);

        final int expectedActivationCount = evalFirePseudoClockStockCount * (evalFirePseudoClockStockCount - 1) / 2
                                            + evalFirePseudoClockStockCount - 1;
        verify(ael,
               times(expectedActivationCount)).afterMatchFired(
                any(AfterMatchFiredEvent.class));
    }

    private int processStocks(int stockCount, AgendaEventListener agendaEventListener, String drlContentString)
            throws Exception {
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption(EventProcessingOption.STREAM);
        KieBase kbase = loadKnowledgeBaseFromString(kconf, drlContentString);

        KieSessionConfiguration ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksessionConfig.setOption(ClockTypeOption.get("pseudo"));
        ksessionConfig.setProperty("keep.reference", "true");
        final KieSession ksession = kbase.newKieSession(ksessionConfig, null);
        ksession.addEventListener(agendaEventListener);

        PseudoClockScheduler clock = (PseudoClockScheduler) ksession.<SessionClock>getSessionClock();

        Thread fireUntilHaltThread = new Thread(ksession::fireUntilHalt, "Engine's thread");
        fireUntilHaltThread.start();
        try {
            Thread.currentThread().setName("Feeding thread");

            for (int stIndex = 1; stIndex <= stockCount; stIndex++) {
                clock.advanceTime(20, TimeUnit.SECONDS);
                Thread.sleep( 100 );
                final StockTickInterface st = new StockTick(stIndex,
                                                            "RHT",
                                                            100 * stIndex,
                                                            100 * stIndex);
                ksession.insert(st);
                Thread.sleep( 100 );
            }

            Thread.sleep(100);
        } finally {
            ksession.halt();
            ksession.dispose();
        }
        
        fireUntilHaltThread.join(5000);

        return stockCount;
    }

}
