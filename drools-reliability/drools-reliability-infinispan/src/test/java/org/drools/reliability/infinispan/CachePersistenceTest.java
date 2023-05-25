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

import java.util.Optional;

import org.drools.reliability.core.StorageManagerFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.test.domain.Person;

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

        Optional<Person> toshiya = getPersonByName(session, "Toshiya");
        assertThat(toshiya).isEmpty(); // So cannot recover the fact
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void ksessionDispose_shouldRemoveCache(PersistedSessionOption.PersistenceStrategy strategy){

        createSession(EMPTY_RULE, strategy); // sessionId = 0. This creates session_0_epDEFAULT and session_0_globals

        insertNonMatchingPerson("Toshiya", 10);

        disposeSession(); // This should clean up session's cache

        assertThat(StorageManagerFactory.get().getStorageManager().getStorageNames()).allMatch(name -> !name.startsWith(SESSION_STORAGE_PREFIX + savedSessionId));
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void missingDispose_shouldNotReuseOrphanedCache(PersistedSessionOption.PersistenceStrategy strategy) {
        createSession(EMPTY_RULE, strategy); // sessionId = 0
        insertNonMatchingPerson("Toshiya", 10);

        // disposeSession() is missing
        failover();

        createSession(EMPTY_RULE, strategy); // new session. If sessionId = 0, it will potentially reuse the orphaned cache

        Optional<Person> toshiya = getPersonByName(session, "Toshiya");
        assertThat(toshiya).isEmpty(); // new session doesn't trigger re-propagation

        failover();

        restoreSession(EMPTY_RULE, strategy); // restoreSession triggers re-propagation

        toshiya = getPersonByName(session, "Toshiya");
        assertThat(toshiya).isEmpty(); // should not reuse the orphaned cache
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void reliableSessionCounter_shouldNotHaveTheSameIdAsPreviousKsession(PersistedSessionOption.PersistenceStrategy strategy) {
        createSession(EMPTY_RULE, strategy); // new session. sessionId = 0
        long firstSessionId = session.getIdentifier();

        failover();

        createSession(EMPTY_RULE, strategy); // new session. sessionId = 1
        long secondSessionId = session.getIdentifier();

        assertThat(secondSessionId).isNotEqualTo(firstSessionId); // sessionId should not be the same
    }
}
