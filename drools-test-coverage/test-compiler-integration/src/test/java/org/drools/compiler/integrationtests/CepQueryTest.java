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

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests queries using temporal operators on events from two entry points.
 */
@RunWith(Parameterized.class)
public class CepQueryTest {

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }
    
    private final KieBaseTestConfiguration kieBaseTestConfiguration;
    
    private KieSession ksession;

    private SessionPseudoClock clock;
    
    private EntryPoint firstEntryPoint, secondEntryPoint;
    
    public CepQueryTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }
    
    @Before
    public void prepare() {
        final String drl = "package org.drools.compiler.integrationtests\n" + 
                "import " + CepQueryTest.TestEvent.class.getCanonicalName() + ";\n" + 
                "declare TestEvent\n" + 
                "    @role( event )\n" +
                "end\n" + 
                "query EventsAfterZeroToNineSeconds\n" + 
                "    $event : TestEvent() from entry-point FirstStream\n" + 
                "    $result : TestEvent( this after [0s, 9s] $event) from entry-point SecondStream\n" + 
                "end\n";
        
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("cep-query-test", kieBaseTestConfiguration, drl);
        ksession = kbase.newKieSession(KieSessionTestConfiguration.STATEFUL_PSEUDO.getKieSessionConfiguration(), null);
        
        clock = ksession.getSessionClock();
        firstEntryPoint = ksession.getEntryPoint("FirstStream");
        secondEntryPoint = ksession.getEntryPoint("SecondStream");        
    }

    @After
    public void cleanup() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    private void eventsInitialization() {
        secondEntryPoint.insert(new TestEvent("minusOne"));
        clock.advanceTime(5, TimeUnit.SECONDS);

        firstEntryPoint.insert(new TestEvent("zero"));
        secondEntryPoint.insert(new TestEvent("one"));
    }

    /**
     * Tests query using temporal operator 'after' on events from two entry points.
     */
    @Test
    public void testQueryWithAfter() {
        this.eventsInitialization();
        final QueryResults results = ksession.getQueryResults("EventsAfterZeroToNineSeconds");

        assertThat(results.size()).as("Unexpected query result length").isEqualTo(1);
        assertThat(((TestEvent) results.iterator().next().get("$result")).getName()).as("Unexpected query result content").isEqualTo("one");
    }

    /**
     * Simple event used in the test.
     */
    public static class TestEvent {
        private final String name;
        
        public TestEvent(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }        
        @Override
        public String toString() {
            return "TestEvent["+name+"]";
        }
    }
}