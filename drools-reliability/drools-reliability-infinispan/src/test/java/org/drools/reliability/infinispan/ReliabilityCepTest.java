/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability.infinispan;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.time.SessionPseudoClock;
import org.test.domain.StockTick;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Temporarily disabled until implementing ProtoStreamAdaptor for java.util.PriorityQueue")
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
    @MethodSource("strategyProviderStoresOnly") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertAdvanceInsertFailoverFire_shouldRecoverFromFailover(PersistedSessionOption.Strategy strategy) {

        createSession(CEP_RULE, strategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        SessionPseudoClock clock = session.getSessionClock();

        session.insert( new StockTick( "DROO" ) );
        clock.advanceTime( 6, TimeUnit.SECONDS );
        session.insert( new StockTick( "ACME" ) );

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();
        restoreSession(CEP_RULE, strategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);
        clock = session.getSessionClock();

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("fired");
        clearResults();

        clock.advanceTime( 1, TimeUnit.SECONDS );
        session.insert( new StockTick( "ACME" ) );

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("fired");
        clearResults();

        clock.advanceTime( 3, TimeUnit.SECONDS );
        session.insert( new StockTick( "ACME" ) );

        assertThat(session.fireAllRules()).isEqualTo(0);
        assertThat(getResults()).isEmpty();
    }
}
