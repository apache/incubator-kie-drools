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

package org.drools.reliability;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.test.domain.Person;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.reliability.CacheManagerFactory.SESSION_CACHE_PREFIX;

/**
 * This class is an integration test with Infinispan DefaultCacheManager and its store configuration.
 * If we want to test drools CacheManager's methods with a fake cacheManager, use CacheManagerTest
 */
@ExtendWith(BeforeAllMethodExtension.class)
class CacheManagerStoreTest extends ReliabilityTestBasics {

    private static final String EMPTY_RULE =
            "global java.util.List results;\n" +
                    "rule R when\n" +
                    "then\n" +
                    "end";

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void removeAllSessionCaches_failoverRemove(PersistedSessionOption.Strategy strategy) {
        createSession(EMPTY_RULE, strategy); // savedSessionId = 0, sessionId = 0
        insertNonMatchingPerson("Toshiya", 10);

        failover();

        assertThat(CacheManagerFactory.INSTANCE.getCacheManager().getCacheNames()).contains(SESSION_CACHE_PREFIX + "0_epDEFAULT", SESSION_CACHE_PREFIX + "0_globals"); // CacheManager knows cache names even after failover

        CacheManagerFactory.INSTANCE.getCacheManager().removeAllSessionCaches(); // must remove all session caches

        restoreSession(EMPTY_RULE, strategy); // restored but no objects in the cache

        Optional<Person> toshiya = getPersonByName(session, "Toshiya");
        assertThat(toshiya).isEmpty(); // So cannot recover the fact
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void ksessionDispose_shouldRemoveCache(PersistedSessionOption.Strategy strategy){

        createSession(EMPTY_RULE, strategy); // sessionId = 0. This creates session_0_epDEFAULT and session_0_globals

        insertNonMatchingPerson("Toshiya", 10);

        disposeSession(); // This should clean up session's cache

        assertThat(CacheManagerFactory.INSTANCE.getCacheManager().getCacheNames()).allMatch(name -> !name.startsWith(SESSION_CACHE_PREFIX));
    }
}
