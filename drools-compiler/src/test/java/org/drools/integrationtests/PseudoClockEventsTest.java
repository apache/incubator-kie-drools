/*
 * Copyright 2007 JBoss Inc
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
package org.drools.integrationtests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.StockTick;
import org.drools.StockTickInterface;
import org.drools.compiler.DroolsParserException;
import org.drools.conf.EventProcessingOption;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.SessionClock;
import org.drools.time.impl.PseudoClockScheduler;
import org.junit.Test;

/**
 * Tests related to the pseudo session clock
 */
public class PseudoClockEventsTest extends CommonTestMethodBase {

	int evalFirePseudoClockStockCount = 15;
	private static final String evalFirePseudoClockDeclaration = "package org.drools\n" +
			"\n" +
			"declare StockTick\n" +
			"    @role( event )\n" +
			"end\n\n";
	private static final String evalFirePseudoClockRuleA = "rule A\n"
			+
			"when\n"
			+
			"	$a: StockTick( $priceA: price )\n"
			+
			"	$b: StockTick( $priceA < price )\n"
			+
			"then \n"
			+
			"    System.out.println(\"Rule A fired by thread \" + Thread.currentThread().getName() + \": \" + $a + \", \" + $b);\n"
			+
			"end\n" +
			"";
	private static final String evalFirePseudoClockRuleB = "rule B\n" +
			"when\n" +
			"	$a: StockTick()\n" +
			"	not( StockTick( this after[1,10s] $a ) )\n" +
			"then \n" +
			"    System.out.println(\"Rule B fired by thread \" + Thread.currentThread().getName());\n" +
			"end\n" +
			"";

	@Test(timeout = 6000)
	public void testEvenFirePseudoClockRuleA() throws Exception {

		AgendaEventListener ael = mock(AgendaEventListener.class);

		processStocks(evalFirePseudoClockStockCount, ael,
				evalFirePseudoClockDeclaration + evalFirePseudoClockRuleA);

		verify(ael,
				times(evalFirePseudoClockStockCount * (evalFirePseudoClockStockCount - 1) / 2)).afterActivationFired(
				any(AfterActivationFiredEvent.class));
	}

	@Test(timeout = 6000)
	public void testEvenFirePseudoClockRuleB() throws Exception {

		AgendaEventListener ael = mock(AgendaEventListener.class);

		processStocks(evalFirePseudoClockStockCount, ael,
				evalFirePseudoClockDeclaration + evalFirePseudoClockRuleB);

		verify(ael,
				times(evalFirePseudoClockStockCount - 1)).afterActivationFired(
				any(AfterActivationFiredEvent.class));
	}

	@Test//(timeout = 60000)
	public void testEvenFirePseudoClockRulesAB() throws Exception {

		AgendaEventListener ael = mock(AgendaEventListener.class);

		processStocks(evalFirePseudoClockStockCount, ael,
				evalFirePseudoClockDeclaration + evalFirePseudoClockRuleA + evalFirePseudoClockRuleB);

		final int expectedActivationCount = evalFirePseudoClockStockCount * (evalFirePseudoClockStockCount - 1) / 2
				+ evalFirePseudoClockStockCount - 1;
		verify(ael,
				times(expectedActivationCount)).afterActivationFired(
				any(AfterActivationFiredEvent.class));
	}

	private int processStocks(int stockCount, AgendaEventListener agendaEventListener, String drlContentString)
			throws DroolsParserException, IOException, Exception {
		KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
		kconf.setOption(EventProcessingOption.STREAM);
		KnowledgeBase kbase = loadKnowledgeBaseFromString(kconf, drlContentString);

		KnowledgeSessionConfiguration ksessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
		ksessionConfig.setOption(ClockTypeOption.get("pseudo"));
		ksessionConfig.setProperty("keep.reference", "true");
		final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(ksessionConfig,
				null);
		ksession.addEventListener(agendaEventListener);

		PseudoClockScheduler clock = (PseudoClockScheduler) ksession.<SessionClock> getSessionClock();

		Runnable fireUntilHaltRunnable = new Runnable() {
			public void run() {
				ksession.fireUntilHalt();
			}
		};
		Thread fireUntilHaltThread = new Thread(fireUntilHaltRunnable, "Engine's thread");
		fireUntilHaltThread.start();
		
		Thread.currentThread().setName( "Feeding thread" );

		for (int stIndex = 1; stIndex <= stockCount; stIndex++) {
			clock.advanceTime(20, TimeUnit.SECONDS);
			final StockTickInterface st = new StockTick(stIndex,
					"RHT",
					100 * stIndex,
					1000);
			ksession.insert(st);
			Thread.sleep(1);
		}

		Thread.sleep(2000);
		ksession.halt();

		return stockCount;
	}

}
