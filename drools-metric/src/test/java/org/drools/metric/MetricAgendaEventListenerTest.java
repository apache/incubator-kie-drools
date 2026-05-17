/*
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
package org.drools.metric;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.Search;
import org.drools.metric.profiling.MetricAgendaEventListener;
import org.drools.metric.util.RuleMetricStats;
import org.drools.metric.util.SessionMetricCollector;
import org.drools.mvel.compiler.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link MetricAgendaEventListener} with a real KieSession.
 */
public class MetricAgendaEventListenerTest extends AbstractMetricTest {

    private MetricAgendaEventListener profilingListener;

    @AfterEach
    public void cleanupProfiling() {
        // Clean up our new meters
        if (registry != null) {
            Search.in(registry)
                    .name(name -> name.startsWith("org.drools.metric.rules.fired")
                            || name.startsWith("org.drools.metric.session."))
                    .meters()
                    .forEach(registry::remove);
        }
    }

    @Test
    public void testFireCountMatchesReturnValue() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1\n" +
                "when\n" +
                "  $p : Person(age > 5)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        profilingListener = new MetricAgendaEventListener();
        profilingListener.attach(ksession);

        List<Person> people = IntStream.range(0, 10)
                .mapToObj(i -> new Person("John" + i, i))
                .collect(Collectors.toList());
        people.forEach(ksession::insert);

        int fired = ksession.fireAllRules();
        ksession.dispose();

        SessionMetricCollector collector = profilingListener.getCollector();
        assertThat(collector.getTotalRulesFired()).isEqualTo(fired);
        assertThat(collector.getTotalFiringTimeMillis()).isGreaterThan(0.0);
    }

    @Test
    public void testPerRuleBreakdownForMultipleRules() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1\n" +
                "when\n" +
                "  $p : Person(age > 7)\n" +
                "then\n" +
                "end\n" +
                "rule R2\n" +
                "when\n" +
                "  $p : Person(age < 3)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        profilingListener = new MetricAgendaEventListener();
        profilingListener.attach(ksession);

        List<Person> people = IntStream.range(0, 10)
                .mapToObj(i -> new Person("John" + i, i))
                .collect(Collectors.toList());
        people.forEach(ksession::insert);

        int fired = ksession.fireAllRules();
        ksession.dispose();

        SessionMetricCollector collector = profilingListener.getCollector();
        assertThat(collector.getTotalRulesFired()).isEqualTo(fired);
        assertThat(collector.getDistinctRuleCount()).isEqualTo(2);

        // Verify both rules have stats (with package prefix)
        assertThat(collector.getAllRuleStats().keySet())
                .anyMatch(name -> name.endsWith("R1"))
                .anyMatch(name -> name.endsWith("R2"));

        // R1 matches age > 7 → persons 8, 9 → 2 firings
        RuleMetricStats r1Stats = collector.getAllRuleStats().entrySet().stream()
                .filter(e -> e.getKey().endsWith("R1"))
                .findFirst().get().getValue();
        assertThat(r1Stats.getFireCount()).isEqualTo(2);

        // R2 matches age < 3 → persons 0, 1, 2 → 3 firings
        RuleMetricStats r2Stats = collector.getAllRuleStats().entrySet().stream()
                .filter(e -> e.getKey().endsWith("R2"))
                .findFirst().get().getValue();
        assertThat(r2Stats.getFireCount()).isEqualTo(3);
    }

    @Test
    public void testMatchCreatedAndCancelledCounts() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1\n" +
                "when\n" +
                "  $p : Person(age > 5)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        profilingListener = new MetricAgendaEventListener();
        profilingListener.attach(ksession);

        List<Person> people = IntStream.range(0, 10)
                .mapToObj(i -> new Person("John" + i, i))
                .collect(Collectors.toList());
        people.forEach(ksession::insert);

        ksession.fireAllRules();
        ksession.dispose();

        SessionMetricCollector collector = profilingListener.getCollector();
        // matchCreated should be >= rules fired
        assertThat(collector.getMatchesCreated()).isGreaterThanOrEqualTo(collector.getTotalRulesFired());
    }

    @Test
    public void testMicrometerRuleFiredCounter() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1\n" +
                "when\n" +
                "  $p : Person(age > 5)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        profilingListener = new MetricAgendaEventListener();
        profilingListener.attach(ksession);

        List<Person> people = IntStream.range(0, 10)
                .mapToObj(i -> new Person("John" + i, i))
                .collect(Collectors.toList());
        people.forEach(ksession::insert);

        ksession.fireAllRules();
        ksession.dispose();

        // Verify new Micrometer metrics
        java.util.Collection<Counter> ruleCounters = Search.in(registry)
                .name("org.drools.metric.rules.fired")
                .counters();
        assertThat(ruleCounters).isNotEmpty();

        java.util.Collection<Timer> firingTimers = Search.in(registry)
                .name("org.drools.metric.session.firing.time")
                .timers();
        assertThat(firingTimers).isNotEmpty();
    }

    @Test
    public void testFactCountIntegration() {
        String str =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R1\n" +
                "when\n" +
                "  $p : Person(age > 5)\n" +
                "then\n" +
                "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newKieSession();

        profilingListener = new MetricAgendaEventListener();
        profilingListener.attach(ksession);

        List<Person> people = IntStream.range(0, 10)
                .mapToObj(i -> new Person("John" + i, i))
                .collect(Collectors.toList());
        people.forEach(ksession::insert);

        SessionMetricCollector collector = profilingListener.getCollector();
        assertThat(collector.getCurrentFactCount()).isEqualTo(10);

        ksession.fireAllRules();

        // Fact count should still be 10 (rules don't retract)
        assertThat(collector.getCurrentFactCount()).isEqualTo(10);

        ksession.dispose();
    }
}
