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

import org.drools.reliability.core.ReliableKieSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.test.domain.Person;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityTest extends ReliabilityTestBasics {

    private static final String BASIC_RULE =
            "import " + Person.class.getCanonicalName() + ";" +
            "global java.util.List results;" +
            "rule X when\n" +
            "  $s: String()\n" +
            "  $p: Person( getName().startsWith($s) )\n" +
            "then\n" +
            "  results.add( $p.getName() );\n" +
            "end";

    @Test
    void createAndUseOfNonReliableSession_shouldWorkNormally() {
        createSession(BASIC_RULE, null);

        insertString("M");
        insertMatchingPerson("Matching Person One", 37);

        insertNonMatchingPerson("Toshiya", 35);
        insertMatchingPerson("Matching Person Two", 40);

        session.fireAllRules();

        assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One", "Matching Person Two");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertFailoverInsertFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        createSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insertString("M");
		insertMatchingPerson("Matching Person One", 37);

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();

        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insertNonMatchingPerson("Toshiya", 35);
        insertMatchingPerson("Matching Person Two", 40);

		session.fireAllRules();

		assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One", "Matching Person Two");
    }


    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // With Remote, FULL fails with "ReliablePropagationList; no valid constructor" even without failover
    void noFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insertString("M");
		insertMatchingPerson("Matching Person One", 37);

        if (safepointStrategy == PersistedSessionOption.SafepointStrategy.EXPLICIT) {
            ((ReliableKieSession) session).safepoint();
        }
        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insertNonMatchingPerson("Toshiya", 41);
		insertMatchingPerson("Matching Person Two", 40);

        session.fireAllRules();

		assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One", "Matching Person Two");
    }


    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertFireInsertFailoverInsertFire_shouldMatchFactInsertedBeforeFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insertString("M");
		insertMatchingPerson("Matching Person One", 37);

		session.fireAllRules();

        insertMatchingPerson("Matching Person Two", 40);

        failover();

        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);
        clearResults();

        insertNonMatchingPerson("Toshiya", 35);
        insertMatchingPerson("Matching Person Three", 41);

        session.fireAllRules();

        assertThat(getResults()).containsExactlyInAnyOrder("Matching Person Two", "Matching Person Three");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithAllSafepoints") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertFireFailoverInsertFire_shouldNotRepeatFiredMatch(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        createSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insertString("M");
		insertMatchingPerson("Matching Person One", 37);

		session.fireAllRules();

        failover();

        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

        insertNonMatchingPerson("Toshiya", 35);
		insertMatchingPerson("Matching Person Two", 40);

		session.fireAllRules();

		assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One", "Matching Person Two");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // FULL fails with "ReliablePropagationList; no valid constructor"
    void updateBeforeFailover_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

        insertString("M");
        Person p1 = new Person("Mario", 49);
        FactHandle fh1 = session.insert(p1);
        Person p2 = new Person("Toshiya", 45);
        FactHandle fh2 = session.insert(p2);

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("Mario");

        p1.setName("SuperMario");
        session.update(fh1, p1);
        p2.setName("MegaToshiya");
        session.update(fh2, p2);

        failover();
        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("Mario", "MegaToshiya");

        failover();
        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);
        clearResults();

        assertThat(session.fireAllRules()).isZero();
        assertThat(getResults()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // FULL fails with "ReliablePropagationList; no valid constructor"
    void deleteBeforeFailover_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

        FactHandle fhString = insertString("M");
        insertMatchingPerson("Matching Person One",37);
        insertNonMatchingPerson("Toshiya",35);

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One");

        session.delete(fhString);

        failover();
        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);
        clearResults();

        insertMatchingPerson("Matching Person Two",40);

        assertThat(session.fireAllRules()).isZero();
        assertThat(getResults()).isEmpty();

        insertString("T");

        failover();
        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("Toshiya");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void updateByObjectBeforeFailover_shouldMatchUpdatedFact(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy){

        createSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

        insertString("M");
        insertMatchingPerson("Mark", 37);
        FactHandle fhNicole = insertNonMatchingPerson("Nicole", 32);

        assertThat(session.fireAllRules()).isEqualTo(1);

        updateWithMatchingPerson(fhNicole,new Person("Mary", 32));

        failover();

        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

        assertThat(session.fireAllRules()).isEqualTo(1);

        failover();

        assertThat(session.fireAllRules()).isZero();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderFull")
    void insertFailover_propListShouldNotBeEmpty(PersistedSessionOption.PersistenceStrategy strategy){
        createSession(BASIC_RULE, strategy);

        insertString("M");
        insertMatchingPerson("Maria", 30);

        failover();

        restoreSession(BASIC_RULE, strategy);


        assertThat(session.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderFull")
    void insertFireFailover_shouldNotRepeatFiredMatch(PersistedSessionOption.PersistenceStrategy strategy){
        createSession(BASIC_RULE, strategy);

        insertString("M");
        insertMatchingPerson("Maria", 30);

        session.fireAllRules();

        failover();

        restoreSession(BASIC_RULE, strategy);

        assertThat(session.fireAllRules()).isZero();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderFull")
    void insertUpdateFailover_shouldNotFiredMatch(PersistedSessionOption.PersistenceStrategy strategy){
        createSession(BASIC_RULE, strategy);

        insertString("M");
        FactHandle fhMaria = insertMatchingPerson("Maria", 30);

        updateWithNonMatchingPerson(fhMaria, new Person("Nicole", 32));

        failover();

        restoreSession(BASIC_RULE, strategy);

        assertThat(session.fireAllRules()).isZero();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderFull")
    void insertNonMatching_Failover_UpdateWithMatching_ShouldFiredMatch(PersistedSessionOption.PersistenceStrategy strategy){
        createSession(BASIC_RULE, strategy);

        insertString("N");
        FactHandle fhMaria = insertMatchingPerson("Maria", 30);

        failover();

        restoreSession(BASIC_RULE, strategy);

        updateWithMatchingPerson(fhMaria, new Person("Nicole",32));

        assertThat(session.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints") // FAILS in STORES_ONLY, EXPLICIT
    void multipleKieSessions_BasicTest(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        KieSession session1 = createSession_m(BASIC_RULE, persistenceStrategy, safepointStrategy);
        KieSession session2 = createSession_m(BASIC_RULE, persistenceStrategy, safepointStrategy);

        session1.insert("M");
        session2.insert("N");

        session1.insert(new Person("Mike-session1",27)); // insert matching person
        session2.insert(new Person("Mary-session2",34)); // insert non matching person

        assertThat(session1.fireAllRules()).isEqualTo(1);
        assertThat(session2.fireAllRules()).isEqualTo(0);

        failover();

        session1 = restoreSession(session1.getIdentifier(), BASIC_RULE, persistenceStrategy, safepointStrategy);
        session2 = restoreSession(session2.getIdentifier(), BASIC_RULE, persistenceStrategy, safepointStrategy);

        // clear results
        ((List<Object>) session1.getGlobal("results")).clear();
        ((List<Object>) session2.getGlobal("results")).clear();

        session1.insert(new Person("Michael-session1",42)); // insert matching person
        session2.insert(new Person("Nancy-session2",25)); // insert matching person

        assertThat(session1.fireAllRules()).isEqualTo(1);
        assertThat(session2.fireAllRules()).isEqualTo(1);
    }

}
