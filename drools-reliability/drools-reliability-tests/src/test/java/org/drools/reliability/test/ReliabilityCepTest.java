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
package org.drools.reliability.test;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.time.SessionPseudoClock;
import org.test.domain.StockTick;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityCepTest extends ReliabilityTestBasics {

    private static final String CEP_RULE =
            "import " + StockTick.class.getCanonicalName() + ";" +
                    "global java.util.List results;" +
                    "rule R when\n" +
                    "    $a : StockTick( company == \"DROO\" )\n" +
                    "    $b : StockTick( company == \"ACME\", this after[5s,8s] $a )\n" +
                    "then\n" +
                    "    results.add(\"fired\");\n" +
                    "end\n";

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertAdvanceInsertFailoverFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        SessionPseudoClock clock = getSessionClock();

        insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        insert(new StockTick("ACME"));

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();
        restoreSession(CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock = getSessionClock();

        assertThat(fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("fired");
        clearResults();

        clock.advanceTime(1, TimeUnit.SECONDS);
        insert(new StockTick("ACME"));

        assertThat(fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("fired");
        clearResults();

        clock.advanceTime(3, TimeUnit.SECONDS);
        insert(new StockTick("ACME"));

        assertThat(fireAllRules()).isZero();
        assertThat(getResults()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertAdvanceInsertFailoverFireTwice_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        KieSession session1 = createSession(CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        SessionPseudoClock clock = getSessionClock(session1);

        insert(session1, new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        insert(session1, new StockTick("ACME"));

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();
        session1 = restoreSession(session1.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock = getSessionClock(session1);

        assertThat(fireAllRules(session1)).isEqualTo(1);
        assertThat(getResults(session1)).containsExactlyInAnyOrder("fired");
        clearResults(session1);

        clock.advanceTime(3, TimeUnit.SECONDS);
        insert(session1, new StockTick("ACME"));

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();
        session1 = restoreSession(session1.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock = getSessionClock(session1);

        assertThat(fireAllRules(session1)).isZero();
        assertThat(getResults(session1)).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertAdvanceFailoverExpireFire_shouldExpireAfterFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        SessionPseudoClock clock = getSessionClock();

        insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        insert(new StockTick("ACME"));

        failover();
        restoreSession(CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock = getSessionClock();

        clock.advanceTime(58, TimeUnit.SECONDS);
        assertThat(fireAllRules()).as("DROO is expired, but a match is available.")
                .isEqualTo(1);
        assertThat(getFactHandles()).as("DROO should have expired because @Expires = 60s")
                .hasSize(1);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertAdvanceFireFailoverExpire_shouldExpireAfterFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        SessionPseudoClock clock = getSessionClock();

        insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        insert(new StockTick("ACME"));

        assertThat(fireAllRules()).as("DROO is expired, but a match is available.")
                .isEqualTo(1);

        failover();
        restoreSession(CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock = getSessionClock();

        clock.advanceTime(58, TimeUnit.SECONDS);
        fireAllRules();

        assertThat(getFactHandles()).as("DROO should have expired because @Expires = 60s")
                .hasSize(1);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepointsAndKieBaseCache")
    void multipleKieSessions_insertAdvanceInsertFailoverFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy, boolean useKieBaseCache) {
        KieSession session1 = createSession(CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        KieSession session2 = createSession(CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        SessionPseudoClock clock1 = getSessionClock(session1);
        SessionPseudoClock clock2 = getSessionClock(session2);

        insert(session1, new StockTick("DROO"));
        clock1.advanceTime(6, TimeUnit.SECONDS);
        insert(session1, new StockTick("ACME"));

        insert(session2, new StockTick("DROO"));
        clock2.advanceTime(4, TimeUnit.SECONDS);
        insert(session2, new StockTick("ACME"));

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();
        session1=restoreSession(session1.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock1 = getSessionClock(session1);
        session2=restoreSession(session2.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock2 = getSessionClock(session2);

        assertThat(fireAllRules(session1)).isEqualTo(1);
        assertThat(getResults(session1)).containsExactlyInAnyOrder("fired");
        clearResults(session1);

        assertThat(fireAllRules(session2)).isEqualTo(0);
        assertThat(getResults(session2)).isEmpty();
        clearResults(session2);

        clock1.advanceTime(3, TimeUnit.SECONDS);
        insert(session1, new StockTick("ACME"));

        clock2.advanceTime(2, TimeUnit.SECONDS);
        insert(session2, new StockTick("ACME"));

        failover();
        session1=restoreSession(session1.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock1 = getSessionClock(session1);
        session2=restoreSession(session2.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock2 = getSessionClock(session2);

        assertThat(fireAllRules(session1)).isEqualTo(0);
        assertThat(getResults(session1)).isEmpty();
        clearResults(session1);

        assertThat(fireAllRules(session2)).isEqualTo(1);
        assertThat(getResults(session2)).containsExactlyInAnyOrder("fired");
        clearResults(session2);

    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepointsAndKieBaseCache")
    void multipleKieSessions_insertAdvanceFailoverExpireFire_shouldExpireAfterFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy, boolean useKieBaseCache) {

        KieSession session1 = createSession(CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        KieSession session2 = createSession(CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        SessionPseudoClock clock1 = getSessionClock(session1);
        SessionPseudoClock clock2 = getSessionClock(session2);

        insert(session1, new StockTick("DROO"));
        clock1.advanceTime(6, TimeUnit.SECONDS);
        insert(session1, new StockTick("ACME"));

        insert(session2, new StockTick("DROO"));
        clock2.advanceTime(7, TimeUnit.SECONDS);
        insert(session2, new StockTick("ACME"));

        failover();
        session1 = restoreSession(session1.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock1 = getSessionClock(session1);
        session2 = restoreSession(session2.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock2 = getSessionClock(session2);

        clock1.advanceTime(58, TimeUnit.SECONDS);
        assertThat(fireAllRules(session1)).as("DROO is expired, but a match is available.")
                .isEqualTo(1);
        assertThat(getFactHandles(session1)).as("DROO should have expired because @Expires = 60s")
                .hasSize(1);

        clock1.advanceTime(1, TimeUnit.SECONDS);
        assertThat(fireAllRules(session2)).as("DROO is not expired, a match is available.")
                .isEqualTo(1);
        assertThat(getFactHandles(session2)).hasSize(2);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepointsAndKieBaseCache")
    void multipleKieSessions_insertAdvanceFireFailoverExpire_shouldExpireAfterFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy, boolean useKieBaseCache) {

        KieSession session1 = createSession(CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        SessionPseudoClock clock1 = getSessionClock(session1);
        KieSession session2 = createSession(CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        SessionPseudoClock clock2 = getSessionClock(session2);

        insert(session1, new StockTick("DROO"));
        clock1.advanceTime(6, TimeUnit.SECONDS);
        insert(session1, new StockTick("ACME"));

        insert(session2, new StockTick("DROO"));
        clock2.advanceTime(4, TimeUnit.SECONDS);
        insert(session2, new StockTick("ACME"));

        assertThat(fireAllRules(session1)).isEqualTo(1);
        assertThat(fireAllRules(session2)).isEqualTo(0);

        failover();
        session1 = restoreSession(session1.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock1 = getSessionClock(session1);
        session2 = restoreSession(session2.getIdentifier(), CEP_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock2 = getSessionClock(session2);

        clock1.advanceTime(58, TimeUnit.SECONDS);
        fireAllRules(session1);
        assertThat(getFactHandles(session1)).as("DROO (1) should have expired because @Expires = 60s")
                .hasSize(1);

        clock2.advanceTime(1, TimeUnit.SECONDS);
        insert(session2, new StockTick("ACME"));
        assertThat(fireAllRules(session2)).isEqualTo(1);

        clock2.advanceTime(56, TimeUnit.SECONDS);
        fireAllRules(session2);
        assertThat(getFactHandles(session2)).as("DROO (2) should have expired because @Expires = 60s")
                .hasSize(2);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithAllSafepointsWithActivationKey")
    void insertFireLimitFailoverFire_shouldFireRemainingActivations(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy,
                                                                    PersistedSessionOption.ActivationStrategy activationStrategy) {

        createSession(CEP_RULE, persistenceStrategy, safepointStrategy, activationStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        SessionPseudoClock clock = getSessionClock();

        insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        insert(new StockTick("ACME"));
        clock.advanceTime(1, TimeUnit.SECONDS);
        insert(new StockTick("ACME"));
        clock.advanceTime(500, TimeUnit.MILLISECONDS);
        insert(new StockTick("ACME"));

        fireAllRules(1);
        assertThat(getResults()).as("Firing is limited to 1")
                                .hasSize(1);
        assertThat(getResults()).containsExactly("fired");

        failover();
        restoreSession(CEP_RULE, persistenceStrategy, safepointStrategy, activationStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        fireAllRules();
        assertThat(getResults()).as("All remaining activations should fire")
                                .containsExactlyInAnyOrder("fired", "fired", "fired");
    }
}