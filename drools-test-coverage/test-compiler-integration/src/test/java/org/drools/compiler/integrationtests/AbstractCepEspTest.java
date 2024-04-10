/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.drools.testcoverage.common.model.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class AbstractCepEspTest {

    protected final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AbstractCepEspTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test(timeout = 10000)
    public void testAssertBehaviorOnEntryPoints() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("cep-esp-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_CEP_AssertBehaviorOnEntryPoints.drl");
        final KieSession ksession = kbase.newKieSession();
        try {
            final StockTick st1 = new StockTick(1, "RHT", 10, 10);
            final StockTick st2 = new StockTick(1, "RHT", 10, 10);
            final StockTick st3 = new StockTick(2, "RHT", 15, 20);

            final AgendaEventListener ael1 = mock(AgendaEventListener.class);
            ksession.addEventListener(ael1);
            final EntryPoint ep1 = ksession.getEntryPoint("stocktick stream");

            final FactHandle fh1 = ep1.insert(st1);
            final FactHandle fh1_2 = ep1.insert(st1);
            final FactHandle fh2 = ep1.insert(st2);
            final FactHandle fh3 = ep1.insert(st3);

            if (kieBaseTestConfiguration.isIdentity()) {
                assertThat(fh1).isSameAs(fh1_2);
                assertThat(fh1).isNotSameAs(fh2);
                assertThat(fh1).isNotSameAs(fh3);
                assertThat(fh2).isNotSameAs(fh3);

                ksession.fireAllRules();
                // must have fired 3 times, one for each event identity
                verify(ael1, times(3)).afterMatchFired(any(AfterMatchFiredEvent.class));
            } else {
                assertThat(fh1).isSameAs(fh1_2);
                assertThat(fh1).isNotSameAs(fh2);
                assertThat(fh1).isNotSameAs(fh3);

                ksession.fireAllRules();
                // must have fired 2 times, one for each event equality
                verify(ael1, times(2)).afterMatchFired(any(AfterMatchFiredEvent.class));
            }
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000L)
    public void testDuplicateFiring2() {

        final String drl = "package org.test;\n" +
                     "import " + StockTick.class.getCanonicalName() + ";\n " +
                     "" +
                     "global java.util.List list \n" +
                     "" +
                     "declare StockTick @role(event) end \n" +
                     "" +
                     "rule Tick when $s : StockTick() then System.out.println( $s ); end \n" +
                     "" +
                     "rule \"slidingTimeCount\"\n" +
                     "when\n" +
                     "\t$n: Number ( intValue > 0 ) from accumulate ( $e: StockTick() over window:time(3s), count($e))\n" +
                     "then\n" +
                     "  list.add( $n ); \n" +
                     "  System.out.println( \"Events in last 3 seconds: \" + $n );\n" +
                     "end" +
                     "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cep-esp-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        final ArrayList list = new ArrayList();
        try {
            final SessionPseudoClock clock = ksession.getSessionClock();
            ksession.setGlobal("list", list);

            //insert events
            for (int i = 1; i < 3; i++) {
                final StockTick event = new StockTick((i - 1), "XXX", 1.0, 0);
                clock.advanceTime(1001, TimeUnit.MILLISECONDS);
                ksession.insert(event);
                ksession.fireAllRules();
            }

            clock.advanceTime(3001, TimeUnit.MILLISECONDS);
            final StockTick event = new StockTick(3, "XXX", 1.0, 0);
            ksession.insert(event);
            ksession.fireAllRules();

            clock.advanceTime(3001, TimeUnit.MILLISECONDS);
            final StockTick event2 = new StockTick(3, "XXX", 1.0, 0);
            ksession.insert(event2);
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
            if (kieBaseTestConfiguration.isIdentity()) {
                assertThat(list).isEqualTo(Arrays.asList(1L, 2L, 1L, 1L));
            } else {
                assertThat(list).isEqualTo(Arrays.asList(1L, 2L, 1L));
            }
        }
    }
}
