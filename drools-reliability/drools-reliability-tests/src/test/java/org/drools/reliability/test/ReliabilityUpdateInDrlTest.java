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

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.test.domain.Person;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityUpdateInDrlTest extends ReliabilityTestBasics {

    private static final String RULE_UPDATE =
            "import " + Person.class.getCanonicalName() + ";" +
                    "global java.util.List results;" +
                    "rule X when\n" +
                    "  $s: String()\n" +
                    "  $p: Person( getName().startsWith($s), getAge()>17 )\n" +
                    "then\n" +
                    "  results.add( $p.getName() );\n" +
                    "end\n" +
                    "rule Birthday when\n" +
                    "  $a: Integer()\n" +
                    "  $p: Person( getAge() == $a )\n" +
                    "then\n" +
                    "  $p.setAge( $a + 1 );\n" +
                    "  update($p);\n" +
                    "end";

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertFireUpdateFailoverFire_shouldMatchUpdatesFromFirstSession(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy){

        createSession(RULE_UPDATE, persistenceStrategy, safepointStrategy);

        insert("M");
        insertMatchingPerson("Mike",22);
        insertNonMatchingPerson("Eleven", 17);
        insert(17); // person with age=17 will change to 18 (17+1)

        assertThat(fireAllRules()).isEqualTo(2); // person with name that starts with M and has age>17 will be added to the results list
        assertThat(getResults()).containsExactlyInAnyOrder("Mike");

        failover();

        restoreSession(RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults();

        insert("E"); // NonMatchingPerson will match rule X

        assertThat(fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("Eleven");

        failover();

        restoreSession(RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults();

        assertThat(fireAllRules()).isZero();
        assertThat(getResults()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertFailoverInsertFailoverInsertFire_shouldRecoverAndMatchRules(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy){
        createSession(RULE_UPDATE, persistenceStrategy, safepointStrategy);

        insert("M");
        insertMatchingPerson("Mike",22);
        insertNonMatchingPerson("Eleven", 17);

        failover();

        restoreSession(RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults();

        insert(17);

        failover();

        restoreSession(RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults();

        insert("E");

        assertThat(fireAllRules()).isEqualTo(3);
        assertThat(getResults()).containsExactlyInAnyOrder("Mike","Eleven");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertFailoverDeleteFailoverInsert_shouldRecoverAndMatchRules(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy){
        createSession(RULE_UPDATE, persistenceStrategy, safepointStrategy);

        insert("M");
        insert("E");
        insertMatchingPerson("Mike",22);
        Person pEleven = new Person("Eleven", 16);
        insert(pEleven);

        failover();

        restoreSession(RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults();

        Optional<FactHandle> getFactHandleForPerson = getFactHandle(pEleven);
        assertThat(getFactHandleForPerson.isEmpty()).isFalse();
        delete(getFactHandleForPerson.get());

        failover();

        restoreSession(RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults();

        insert(16);
        insertMatchingPerson("Eleven",16);

        assertThat(fireAllRules()).isEqualTo(2);
        assertThat(getResults()).containsExactlyInAnyOrder("Mike");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void multipleKieSession_insertFireUpdateInsertFailoverFire_shouldMatchUpdatesAfterFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        KieSession session1 = createSession(RULE_UPDATE, persistenceStrategy, safepointStrategy);
        insert(session1, "M");
        insertMatchingPerson(session1, "Mike", 16);   // rule X match

        KieSession session2 = createSession(RULE_UPDATE, persistenceStrategy, safepointStrategy);
        insertMatchingPerson(session2, "Eleven", 18);   // no rule match

        assertThat(fireAllRules(session1)).isEqualTo(0);
        assertThat(fireAllRules(session2)).isEqualTo(0);

        insert(session1, 16);    // rule Birthday match (session 1)
        insert(session2, "E");

        failover();
        session1 = restoreSession(session1.getIdentifier(), RULE_UPDATE, persistenceStrategy, safepointStrategy);
        clearResults(session1);
        session2 = restoreSession(session2.getIdentifier(), RULE_UPDATE, persistenceStrategy, safepointStrategy);
        clearResults(session2);

        assertThat(fireAllRules(session1)).isEqualTo(1);
        assertThat(getResults(session1)).isEmpty();

        assertThat(fireAllRules(session2)).isEqualTo(1);
        assertThat(getResults(session2)).containsExactlyInAnyOrder("Eleven");

        insert(session1, 17);   // rule Birthday match (session 1)
        insert(session2, 17);   // rule Birthday will not match (session 2)

        failover();
        session1 = restoreSession(session1.getIdentifier(), RULE_UPDATE, persistenceStrategy, safepointStrategy);
        clearResults(session1);
        session2 = restoreSession(session2.getIdentifier(), RULE_UPDATE, persistenceStrategy, safepointStrategy);
        clearResults(session2);

        assertThat(fireAllRules(session1)).isEqualTo(2);
        assertThat(getResults(session1)).containsExactlyInAnyOrder("Mike");

        assertThat(fireAllRules(session2)).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void multipleKieSession_insertFailoverInsertFailoverInsertFire_shouldRecoverAndMatchRules(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy){
        KieSession session1 = createSession(RULE_UPDATE, persistenceStrategy, safepointStrategy);
        KieSession session2 = createSession(RULE_UPDATE, persistenceStrategy, safepointStrategy);

        insert(session1,"M");
        insertMatchingPerson(session1,"Mike",22);
        insertNonMatchingPerson(session2,"Eleven", 17);

        failover();

        session1 = restoreSession(session1.getIdentifier(), RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults(session1);
        session2 = restoreSession(session2.getIdentifier(), RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults(session2);

        insert(session2,17);

        failover();

        session1 = restoreSession(session1.getIdentifier(), RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults(session1);
        session2 = restoreSession(session2.getIdentifier(), RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults(session2);

        insert(session2,"E");

        assertThat(fireAllRules(session1)).isEqualTo(1);
        assertThat(getResults(session1)).containsExactlyInAnyOrder("Mike");

        assertThat(fireAllRules(session2)).isEqualTo(2);
        assertThat(getResults(session2)).containsExactlyInAnyOrder("Eleven");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void multiplKieSession_insertFailoverDeleteFailoverInsert_shouldRecoverAndMatchRules(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy){
        KieSession session1 = createSession(RULE_UPDATE, persistenceStrategy, safepointStrategy);
        KieSession session2 = createSession(RULE_UPDATE, persistenceStrategy, safepointStrategy);

        insert(session1,"M");
        insert(session2,"E");
        insertMatchingPerson(session1,"Mike",22);
        Person pEleven = new Person("Eleven", 16);
        insert(session2, pEleven);

        failover();

        session1 = restoreSession(session1.getIdentifier(), RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults(session1);
        session2 = restoreSession(session2.getIdentifier(), RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults(session2);

        Optional<FactHandle> getFactHandleForPerson = getFactHandle(session2, pEleven);
        assertThat(getFactHandleForPerson.isEmpty()).isFalse();
        delete(session2, getFactHandleForPerson.get());

        failover();

        session1 = restoreSession(session1.getIdentifier(), RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults(session1);
        session2 = restoreSession(session2.getIdentifier(), RULE_UPDATE, persistenceStrategy,safepointStrategy);
        clearResults(session2);

        insert(session2,16);
        insertMatchingPerson(session2,"Eleven",16);

        assertThat(fireAllRules(session1)).isEqualTo(1);
        assertThat(getResults(session1)).containsExactlyInAnyOrder("Mike");
        assertThat(fireAllRules(session2)).isEqualTo(1);
        assertThat(getResults(session2)).isEmpty();
    }

}

