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

import org.drools.reliability.core.StorageManagerFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.test.domain.Person;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.reliability.core.StorageManagerFactory.SESSION_STORAGE_PREFIX;

/**
 * This class is an integration test with Infinispan embedded and remote cache manager to verify cache persistence.
 * If we want to test drools CacheManager's methods with a fake cacheManager, use CacheManagerTest
 */
@ExtendWith(BeforeAllMethodExtension.class)
class CachePersistenceTest extends ReliabilityTestBasics {

    private static final String EMPTY_RULE =
            "global java.util.List results;\n" +
                    "rule R when\n" +
                    "then\n" +
                    "end";

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void removeAllSessionCaches_shouldRemoveAllSessionCachesEvenAfterFailover(PersistedSessionOption.PersistenceStrategy strategy) {
        createSession(EMPTY_RULE, strategy); // savedSessionId = 0, sessionId = 0
        insertNonMatchingPerson("Toshiya", 10);

        failover();

        assertThat(StorageManagerFactory.get().getStorageManager().getStorageNames()).contains(SESSION_STORAGE_PREFIX + "0_epDEFAULT", SESSION_STORAGE_PREFIX + "0_globals"); // CacheManager knows cache names even after failover

        StorageManagerFactory.get().getStorageManager().removeAllSessionStorages(); // must remove all session caches

        restoreSession(EMPTY_RULE, strategy); // restored but no objects in the cache

        Optional<Person> toshiya = getPersonByName("Toshiya");
        assertThat(toshiya).isEmpty(); // So cannot recover the fact
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void ksessionDispose_shouldRemoveCache(PersistedSessionOption.PersistenceStrategy strategy){

        createSession(EMPTY_RULE, strategy); // sessionId = 0. This creates session_0_epDEFAULT and session_0_globals
        long sessionId = getSessionIdentifier();

        insertNonMatchingPerson("Toshiya", 10);

        disposeSession(); // This should clean up session's cache

        assertThat(StorageManagerFactory.get().getStorageManager().getStorageNames()).allMatch(name -> !name.startsWith(SESSION_STORAGE_PREFIX + sessionId));
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void missingDispose_shouldNotReuseOrphanedCache(PersistedSessionOption.PersistenceStrategy strategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        KieSession firstSession = createSession(EMPTY_RULE, strategy, safepointStrategy);
        assertThat(persistedSessionIds.get(firstSession.getIdentifier()))
                .as("firstSession's persisted session id = 0")
                .isEqualTo(0);

        insertNonMatchingPerson(firstSession,"Toshiya", 10);

        Optional<Person> toshiyaInFirstSession = getPersonByName(firstSession, "Toshiya");
        assertThat(toshiyaInFirstSession).isNotEmpty();

        // disposeSession() for the first session is missing. Simulating that the first session is completely lost from client side.
        failover();

        // create a new session.
        KieSession secondSession = createSession(EMPTY_RULE, strategy, safepointStrategy);
        assertThat(persistedSessionIds.get(secondSession.getIdentifier()))
                .as("second session's persisted session id = 1. Don't reuse the orphaned cache")
                .isEqualTo(1);

        Optional<Person> toshiyaInSecondSession = getPersonByName(secondSession, "Toshiya");
        assertThat(toshiyaInSecondSession)
                .as("new session doesn't have the fact")
                .isEmpty();

        failover();

        // restore secondSession
        KieSession thirdSession = restoreSession(secondSession.getIdentifier(), EMPTY_RULE, strategy, safepointStrategy);
        assertThat(persistedSessionIds.get(thirdSession.getIdentifier()))
                .as("third session's persisted session id = 1. Don't reuse the orphaned cache")
                .isEqualTo(1);

        Optional<Person> toshiyaInThirdSession = getPersonByName(thirdSession, "Toshiya");
        assertThat(toshiyaInThirdSession)
                .as("thirdSession takes over the secondSession's cache. It doesn't have the fact")
                .isEmpty();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void reliableSessionCounter_shouldNotHaveTheSameIdAsPreviousKsession(PersistedSessionOption.PersistenceStrategy strategy) {
        createSession(EMPTY_RULE, strategy); // new session. sessionId = 0
        long firstSessionId = getSessionIdentifier();

        failover();

        createSession(EMPTY_RULE, strategy); // new session. sessionId = 1
        long secondSessionId = getSessionIdentifier();

        assertThat(secondSessionId).isNotEqualTo(firstSessionId); // sessionId should not be the same
    }
}
