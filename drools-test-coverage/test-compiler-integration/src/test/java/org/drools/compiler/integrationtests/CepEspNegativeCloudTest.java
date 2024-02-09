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

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.drools.testcoverage.common.model.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class CepEspNegativeCloudTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public CepEspNegativeCloudTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test(timeout=10000)
    public void testCloudModeExpiration() throws InstantiationException, IllegalAccessException, InterruptedException {
        final String drl = "package org.drools.cloud\n" +
                     "import " + StockTick.class.getCanonicalName() + "\n" +
                     "declare Event\n" +
                     "        @role ( event )\n" +
                     "        name : String\n" +
                     "        value : Object\n" +
                     "end\n" +
                     "declare AnotherEvent\n" +
                     "        @role ( event )\n" +
                     "        message : String\n" +
                     "        type : String\n" +
                     "end\n" +
                     "declare StockTick\n" +
                     "        @role ( event )\n" +
                     "end\n" +
                     "rule \"two events\"\n" +
                     "    when\n" +
                     "        Event( value != null ) from entry-point X\n" +
                     "        StockTick( company != null ) from entry-point X\n" +
                     "    then\n" +
                     "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cep-esp-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final EntryPoint ep = ksession.getEntryPoint("X");

            ep.insert(new StockTick(1, "RHT", 10, 1000));
            int rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(0);

            final FactType event = kbase.getFactType("org.drools.cloud", "Event");
            final Object e1 = event.newInstance();
            event.set(e1, "name", "someKey");
            event.set(e1, "value", "someValue");

            ep.insert(e1);
            rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(1);

            // let some time be spent
            Thread.sleep(1000);

            // check both events are still in memory as we are running in CLOUD mode
            assertThat(ep.getFactCount()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testThrowsWhenCreatingKieBaseUsingWindowsInCloudMode() {
        final String drl =
            "declare TestEvent\n" +
            "    @role( event )\n" +
            "    name : String\n" +
            "end\n" +
            "\n" +
            "rule R when\n" +
            "        TestEvent ( name == \"EventA\" ) over window:time( 1s ) from entry-point EventStream\n" +
            "    then\n" +
            "        // consequence\n" +
            "end\n";

        try {
            KieBaseUtil.getKieBaseFromKieModuleFromDrl("cep-esp-test", kieBaseTestConfiguration, drl);
            fail("Should throw a RuntimeException because the CLOUD kbase is trying to use features only available in STREAM mode");
        } catch (final Exception e) {
        }
    }

    @Test
    public void testTemporalQuery() {
        // BZ-967441
        final String drl =
                 "package org.drools.compiler.integrationtests;\n" +
                 "\n" +
                 "import " + CepEspTest.TestEvent.class.getCanonicalName() + ";\n" +
                 "\n" +
                 "declare TestEvent\n" +
                 "    @role( event )\n" +
                 "end\n" +
                 "\n" +
                 "query EventsBeforeNineSeconds\n" +
                 "   $event : TestEvent() from entry-point EStream\n" +
                 "   $result : TestEvent ( this after [0s, 9s] $event) from entry-point EventStream\n" +
                 "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cep-esp-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        try {
            final SessionPseudoClock clock = ksession.getSessionClock();

            final EntryPoint ePoint = ksession.getEntryPoint("EStream");
            final EntryPoint entryPoint = ksession.getEntryPoint("EventStream");

            ePoint.insert(new CepEspTest.TestEvent("zero"));
            entryPoint.insert(new CepEspTest.TestEvent("one"));
            clock.advanceTime(10, TimeUnit.SECONDS);
            entryPoint.insert(new CepEspTest.TestEvent("two"));
            clock.advanceTime(10, TimeUnit.SECONDS);
            entryPoint.insert(new CepEspTest.TestEvent("three"));
            final QueryResults results = ksession.getQueryResults("EventsBeforeNineSeconds");
            assertThat(results.size()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }
}
