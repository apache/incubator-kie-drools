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
package org.drools.mvel.integrationtests;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionPseudoClock;


/**
 * Tests behavior of Drools Fusion when several events are being
 * fed into the engine by thread A while the engine had been started by
 * fireUntilHalt by thread B.
 */
@RunWith(Parameterized.class)
public class FireUntilHaltAccumulateTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public FireUntilHaltAccumulateTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    private KieSession statefulSession;

    private StockFactory stockFactory;

    private static String drl = "package org.drools.integrationtests\n" +
                                "\n" +
                                "import java.util.List;\n" +
                                "\n" +
                                "declare Stock\n" +
                                "    @role( event )\n" +
                                "    @expires( 1s ) // setting to a large value causes the test to pass\n" +
                                "    name : String\n" +
                                "    value : Double\n" +
                                "end\n" +
                                "\n" +
                                "rule \"collect events\"\n" +
                                "when\n" +
                                "    stocks := List()\n" +
                                "        from accumulate( $zeroStock : Stock( value == 0.0 );\n" +
                                "                         collectList( $zeroStock ) )\n" +
                                "then\n" +
                                "end";

    @Before
    public void setUp() {
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        final KieSessionConfiguration sessionConfig =
                RuleBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ));

        this.statefulSession = kbase.newKieSession(sessionConfig, null);
        this.stockFactory = new StockFactory(kbase);
    }

    @After
    public void cleanUp() {
        if (this.statefulSession != null) {
            this.statefulSession.dispose();
        }
    }

    /**
     * Events are being collected through accumulate while
     * separate thread inserts other events.
     * <p/>
     * The engine runs in fireUntilHalt mode started in a separate thread.
     * Events may expire during the evaluation of accumulate.
     */
    @Test
    public void testFireUntilHaltWithAccumulateAndExpires() throws Exception {
        // thread for firing until halt
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future sessionFuture = executor.submit((Runnable) statefulSession::fireUntilHalt);

        try {
            for (int iteration = 0; iteration < 100; iteration++) {
                this.populateSessionWithStocks();
            }
            // let the engine finish its job
            Thread.sleep(2000);

        } finally {
            statefulSession.halt();
            // not to swallow possible exception
            sessionFuture.get();
            statefulSession.dispose();
            executor.shutdownNow();
        }
    }

    private void populateSessionWithStocks() {
        final SessionPseudoClock clock = statefulSession.getSessionClock();

        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST1", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST2", 1d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST3", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST4", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST5", 0d));
        clock.advanceTime(1, TimeUnit.SECONDS);
        statefulSession.insert(stockFactory.createStock("ST6", 1d));
    }

    /**
     * Factory creating events used in the test.
     */
    private static class StockFactory {

        private static final String DRL_PACKAGE_NAME = "org.drools.integrationtests";

        private static final String DRL_FACT_NAME = "Stock";

        private final KieBase kbase;

        public StockFactory(final KieBase kbase) {
            this.kbase = kbase;
        }

        public Object createStock(final String name, final Double value) {
            try {
                return this.createDRLStock(name, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to create Stock instance defined in DRL", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to create Stock instance defined in DRL", e);
            }
        }

        private Object createDRLStock(final String name, final Double value)
                throws IllegalAccessException, InstantiationException {

            final FactType stockType = kbase.getFactType(DRL_PACKAGE_NAME, DRL_FACT_NAME);

            final Object stock = stockType.newInstance();
            stockType.set(stock, "name", name);
            stockType.set(stock, "value", value);

            return stock;
        }
    }
}
