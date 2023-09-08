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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.time.SessionPseudoClock;
import org.test.domain.ControlEvent;
import org.test.domain.StockTick;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class ObjectReferenceEventTest extends ReliabilityTestBasics {

    // This is not a very meaningful rule. Just to demonstrate object reference with events
    private static final String REF_EVENT =
            "import " + StockTick.class.getCanonicalName() + ";\n" +
                    "import " + ControlEvent.class.getCanonicalName() + ";\n" +
                    "global java.util.List results;\n" +
                    "rule R1 when\n" +
                    "  $s : StockTick()\n" +
                    "  $c : ControlEvent(stockTick == $s, this after[1s,5s] $s)\n" +
                    "then\n" +
                    "  results.add( \"R1\" );\n" +
                    "end";

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // does not support PersistedSessionOption.PersistenceObjectsStrategy.SIMPLE
    void testInsertFailover_ShouldFireRules(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        createSession(REF_EVENT, persistenceStrategy, safepointStrategy, PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES,
                      EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        SessionPseudoClock clock = getSessionClock();

        StockTick stockTick = new StockTick("DROO");
        insert(stockTick);

        clock.advanceTime(2, SECONDS);

        ControlEvent controlEvent = new ControlEvent(stockTick);
        insert(controlEvent);

        failover();
        restoreSession(REF_EVENT, persistenceStrategy, safepointStrategy, PersistedSessionOption.PersistenceObjectsStrategy.OBJECT_REFERENCES,
                       EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        fireAllRules();

        assertThat(getResults()).containsExactly("R1");
    }
}
