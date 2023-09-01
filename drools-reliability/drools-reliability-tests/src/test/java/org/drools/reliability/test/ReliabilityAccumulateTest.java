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

import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.test.domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityAccumulateTest extends ReliabilityTestBasics {

    public static final String ACCUMULATE_RULE =
            "import " + Person.class.getCanonicalName() + ";" +
                    "global java.util.List results;" +
                    "rule X when\n" +
                    "  accumulate ( $p: Person ( getName().startsWith(\"M\") ); \n" +
                    "                $sum : sum($p.getAge())  \n" +
                    "              )                          \n" +
                    "then\n" +
                    "  results.add($sum);\n" +
                    "end";

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertFailoverInsertFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        createSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);

        insertMatchingPerson("Matching Person One", 37);

        failover();

        restoreSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);

        insertNonMatchingPerson("Non-matching Person", 35);
        insertMatchingPerson("Matching Person Two", 40);

        fireAllRules();

        assertThat(getResults()).as("sum = 37 + 40").containsExactly(77);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertFireInsertFailoverInsertFire_shouldMatchFactInsertedBeforeFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);

        insertMatchingPerson("Matching Person One", 37);

        fireAllRules();

        insertMatchingPerson("Matching Person Two", 40);

        failover();

        restoreSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);

        insertNonMatchingPerson("Non-matching Person", 35);
        insertMatchingPerson("Matching Person Three", 41);

        fireAllRules();

        assertThat(getResults()).as("first sum is 37. second sum is 37 + 40 + 41 = 118").containsExactly(37, 118);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void updateBeforeFailover_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);

        Person p1 = new Person("Mario", 49);
        FactHandle fh1 = insert(p1);
        Person p2 = new Person("Toshiya", 45);
        FactHandle fh2 = insert(p2);

        fireAllRules();

        assertThat(getResults()).as("Matching only Mario. sum = 49").containsExactly(49);
        clearResults();

        p1.setName("SuperMario");
        update(fh1, p1);
        p2.setName("MegaToshiya");
        update(fh2, p2);

        failover();
        restoreSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);

        fireAllRules();
        assertThat(getResults()).as("Matching only MegaToshiya. sum = 45").containsExactly(45);

        failover();
        restoreSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);
        clearResults();

        assertThat(fireAllRules()).as("No new insertion doesn't trigger firing").isZero();
        assertThat(getResults()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void deleteBeforeFailover_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);

        FactHandle matchingPersonOnefactHandle = insertMatchingPerson("Matching Person One", 37);
        insertNonMatchingPerson("Toshiya", 35);

        fireAllRules();
        assertThat(getResults()).containsExactly(37);

        delete(matchingPersonOnefactHandle);

        failover();
        restoreSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);
        clearResults();

        insertMatchingPerson("Matching Person Two", 40);

        fireAllRules();
        assertThat(getResults()).as("Matching Person One is deleted").containsExactly(40);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void deleteAfterFailover_shouldNotMatch(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        createSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);

        Person matchingPersonOne = new Person("Matching Person One", 37);
        insert(matchingPersonOne);

        insertMatchingPerson("Matching Person Two", 40);

        failover();
        restoreSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy);

        Optional<FactHandle> matchingPersonOneFactHandleOpt = getFactHandle(matchingPersonOne);
        assertThat(matchingPersonOneFactHandleOpt).isPresent();

        delete(matchingPersonOneFactHandleOpt.get());

        fireAllRules();
        assertThat(getResults()).as("Matching Person One is deleted").containsExactly(40);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepointsAndKieBaseCache")
    void multipleKieSessions_insertFailoverInsertFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy, boolean useKieBaseCache) {
        KieSession session1 = createSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache);
        KieSession session2 = createSession(ACCUMULATE_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache);

        insert(session1, new Person("Mike-session1", 27)); // insert matching person
        insert(session2, new Person("Toshiya-session2", 34)); // insert non matching person

        assertThat(fireAllRules(session1)).isEqualTo(1);
        assertThat(fireAllRules(session2)).as("If there is no matching fact, accumulate sum returns '0' and the rule is fired").isEqualTo(1);

        failover();

        session1 = restoreSession(session1.getIdentifier(), ACCUMULATE_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache);
        session2 = restoreSession(session2.getIdentifier(), ACCUMULATE_RULE, persistenceStrategy, safepointStrategy, useKieBaseCache);

        insert(session1, new Person("Michael-session1", 42)); // insert matching person
        insert(session2, new Person("Max-session2", 25)); // insert matching person

        assertThat(fireAllRules(session1)).isEqualTo(1);
        assertThat(fireAllRules(session2)).isEqualTo(1);

        assertThat(getResults(session1)).containsExactly(27, 69);
        assertThat(getResults(session2)).containsExactly(0, 25);
    }
}
