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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.drools.base.facttemplates.Event;
import org.drools.core.event.DebugAgendaEventListener;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.time.SessionPseudoClock;
import org.test.domain.StockTick;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.reliability.infinispan.util.PrototypeUtils.createEvent;

@DisabledIf("isProtoStream")
@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityCepOnceAfterTest extends ReliabilityTestBasics {

    private static final String CEP_RULE = readDrl("once_after.drl");

    private static String readDrl(String fileName) {
        try {
            return Files.readString(Paths.get(ReliabilityCepOnceAfterTest.class.getResource(fileName).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void insertAdvanceInsertFailoverFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(CEP_RULE, persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        session.addEventListener(new DebugAgendaEventListener());

        SessionPseudoClock clock = session.getSessionClock();

        // TODO: create wrapper methods like insertMatchingPerson
        Event sensuHost1Alert = createEvent();
        sensuHost1Alert.set("sensu.host", "host1");
        sensuHost1Alert.set("sensu.process.type", "alert");
        session.insert(sensuHost1Alert);

        clock.advanceTime(1, TimeUnit.MINUTES);
        session.fireAllRules();

        Event sensuHost1Info = createEvent();
        sensuHost1Info.set("sensu.host", "host1");
        sensuHost1Info.set("sensu.process.type", "info");
        session.insert(sensuHost1Info);

        clock.advanceTime(1, TimeUnit.MINUTES);
        session.fireAllRules();

        Event sensuHost2Alert = createEvent();
        sensuHost2Alert.set("sensu.host", "host2");
        sensuHost2Alert.set("sensu.process.type", "alert");
        session.insert(sensuHost2Alert);

        clock.advanceTime(1, TimeUnit.MINUTES);
        session.fireAllRules();

        Event sensuHost1AlertAgain = createEvent();
        sensuHost1AlertAgain.set("sensu.host", "host1");
        sensuHost1AlertAgain.set("sensu.process.type", "alert");
        session.insert(sensuHost1AlertAgain);

        clock.advanceTime(1, TimeUnit.MINUTES);
        session.fireAllRules();

        assertThat(getResults()).isEmpty();

        clock.advanceTime(6, TimeUnit.MINUTES);
        session.fireAllRules();

        assertThat(getResults()).hasSize(2);

        System.out.println(getResults());
    }
}
