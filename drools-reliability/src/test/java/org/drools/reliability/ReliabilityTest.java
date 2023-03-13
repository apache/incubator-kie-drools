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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.reliability.ReliabilityTestUtils.failover;

@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityTest {

    private static final String BASIC_RULE =
            "import " + Person.class.getCanonicalName() + ";" +
            "global java.util.List results;" +
            "rule X when\n" +
            "  $s: String()\n" +
            "  $p: Person( getName().startsWith($s) )\n" +
            "then\n" +
            "  results.add( $p.getAge() );\n" +
            "end";

    private static final String TWO_RULES =
            "import " + Person.class.getCanonicalName() + ";" +
                    "global java.util.List results;" +
                    "rule X when\n" +
                    "  $s: String()\n" +
                    "  $p: Person( getName().startsWith($s), getAge()>17 )\n" +
                    "then\n" +
                    "  results.add( $p.getAge() );\n" +
                    "end\n" +
                    "rule Birthday when\n" +
                    "  $a: Integer()\n" +
                    "  $p: Person( getAge() == $a )\n" +
                    "then\n" +
                    "  $p.setAge( $a + 1 );\n" +
                    "  update($p);\n" +
                    "end";

    static Stream<PersistedSessionOption.Strategy> strategyProvider() {
        return Stream.of(PersistedSessionOption.Strategy.STORES_ONLY, PersistedSessionOption.Strategy.FULL);
    }

    static Stream<PersistedSessionOption.Strategy> strategyProviderStoresOnly() {
        return Stream.of(PersistedSessionOption.Strategy.STORES_ONLY);
    }

    @AfterEach
    public void tearDown() {
        // We can remove this when we implement ReliableSession.dispose() to call CacheManager.removeCachesBySessionId(id)
        CacheManager.INSTANCE.removeAllSessionCaches();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertFailoverInsertFire_shouldRecoverFromFailover(PersistedSessionOption.Strategy strategy) {

        long savedSessionId;

        // 1st round
        {
            KieSession firstSession = getFirstKieSession(BASIC_RULE, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));
        }

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();

        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(BASIC_RULE, savedSessionId, strategy);

            try {
                secondSession.insert(new Person("Edson", 35));
                secondSession.insert(new Person("Mario", 40));

                assertThat(secondSession.fireAllRules()).isEqualTo(2);
                assertThat(getResults(secondSession)).containsExactlyInAnyOrder(37, 40);
            } finally {
                secondSession.dispose();
            }
        }
    }

    private List<Integer> getResults(KieSession kieSession) {
        return (List<Integer>) kieSession.getGlobal("results");
    }

    private KieSession getFirstKieSession(String drl, PersistedSessionOption.Strategy strategy) {
        return getKieSession(drl, PersistedSessionOption.newSession(strategy));
    }

    private KieSession getSubsequentKieSession(String drl, long savedSessionId, PersistedSessionOption.Strategy strategy) {
        return getKieSession(drl, PersistedSessionOption.fromSession(savedSessionId, strategy));
    }

    private KieSession getKieSession(String drl, PersistedSessionOption option) {
        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(option);
        KieSession kieSession = kbase.newKieSession(conf, null);
        List<Integer> results = new ArrayList<>();
        kieSession.setGlobal("results", results);
        return kieSession;
    }

    private Optional<Person> getPersonByName(KieSession kieSession, String name) {
        return kieSession.getObjects(new ClassObjectFilter(Person.class))
                .stream()
                .map(Person.class::cast)
                .filter(p -> p.getName().equals(name) ).findFirst();
    }

    @ParameterizedTest
    @MethodSource("strategyProvider")
    void noFailover(PersistedSessionOption.Strategy strategy) {

        long savedSessionId;

        // 1st round
        {
            KieSession firstSession = getFirstKieSession(BASIC_RULE, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));
        }

        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(BASIC_RULE, savedSessionId, strategy);

            try {
                secondSession.insert(new Person("Toshiya", 35));
                secondSession.insert(new Person("Mario", 40));

                assertThat(secondSession.fireAllRules()).isEqualTo(2);
                assertThat(getResults(secondSession)).containsExactlyInAnyOrder(37, 40);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertFireInsertFailoverInsertFire_shouldMatchFactInsertedBeforeFailover(PersistedSessionOption.Strategy strategy) {
        long savedSessionId;

        // 1st round
        {
            KieSession firstSession = getFirstKieSession(BASIC_RULE, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Matteo", 41));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);
            assertThat(getResults(firstSession)).containsExactlyInAnyOrder(41);

            firstSession.insert(new Person("Mark", 47)); // This is not yet matched
        }

        failover();

        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(BASIC_RULE, savedSessionId, strategy);

            try {
                secondSession.insert(new Person("Toshiya", 45));
                secondSession.insert(new Person("Mario", 49));

                assertThat(secondSession.fireAllRules()).isEqualTo(2);
                assertThat(getResults(secondSession)).containsExactlyInAnyOrder(47, 49);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertFireFailoverInsertFire_shouldNotRepeatFiredMatch(PersistedSessionOption.Strategy strategy) {
        long savedSessionId;
        // 1st round
        {
            KieSession firstSession = getFirstKieSession(BASIC_RULE, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);
        }

        failover();

        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(BASIC_RULE, savedSessionId, strategy);

            try {
                secondSession.insert(new Person("Edson", 35));
                secondSession.insert(new Person("Mario", 40));

                assertThat(secondSession.fireAllRules()).isEqualTo(1); // Only Mario matches.
                assertThat(getResults(secondSession)).containsExactlyInAnyOrder(40);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly") // FULL fails with "ReliablePropagationList; no valid constructor"
    void updateInRHS_insertFireFailoverInsertFire_shouldNotMatchUpdatedFact(PersistedSessionOption.Strategy strategy) {
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.util.List results;" +
                        "rule X when\n" +
                        "  $s: String()\n" +
                        "  $p: Person( getName().startsWith($s) )\n" +
                        "then\n" +
                        "  results.add( $p.getAge() );\n" +
                        "  $p.setName(\"-\");\n" +
                        "  update($p); \n" + // updated Person will not match
                       "end";

        long savedSessionId;
        // 1st round
        {
            KieSession firstSession = getFirstKieSession(drl, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));
            firstSession.insert(new Person("Nicole", 27));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);
        }

        failover();

        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(BASIC_RULE, savedSessionId, strategy);

            try {
                secondSession.insert(new Person("John", 22));
                secondSession.insert(new Person("Mary", 42));

                assertThat(secondSession.fireAllRules()).isEqualTo(1);
                assertThat(getResults(secondSession)).containsExactlyInAnyOrder(42);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void cacheImmutableKey_insertUpdateFireFailover_cacheEntriesShouldNotIncrease(PersistedSessionOption.Strategy strategy){
        long savedSessionId;
        // 1st round
        {
            KieSession firstSession = getFirstKieSession(BASIC_RULE, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            FactHandle fhMary = firstSession.insert(new Person("Mary", 37));
            FactHandle fhNicole = firstSession.insert(new Person("Nicole", 32));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);

            // get fact handle object, update
            Person pMary = (Person) firstSession.getObject(fhMary);
            pMary.setName("Mary2");
            firstSession.update(fhMary, pMary);

            // update fact handle with a new object
            firstSession.update(fhNicole, new Person("Nicole", 39) );

        }

        failover();
        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(BASIC_RULE, savedSessionId, strategy);

            try {
                assertThat(secondSession.getObjects( new ClassObjectFilter( Person.class) ).size()).isEqualTo(2);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @Disabled("Fails in the first assertion of 2nd round, fact handle fhNicole  points to 'Nicole' in secondSession")
    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void updateByFH_insertFireUpdateFailover_shouldMatchUpdatedFact(PersistedSessionOption.Strategy strategy){
        long savedSessionId;
        // 1st round
        {
            KieSession firstSession = getFirstKieSession(BASIC_RULE, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));
            FactHandle fhNicole = firstSession.insert(new Person("Nicole", 32));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);

            Person pMary = (Person) firstSession.getObject(fhNicole);
            pMary.setName("Mary");
            firstSession.update(fhNicole, pMary);
        }

        failover();
        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(BASIC_RULE, savedSessionId, strategy);

            assertThat(this.getPersonByName(secondSession,"Mary").isEmpty()).isFalse();

            try {
                assertThat(secondSession.fireAllRules()).isEqualTo(1);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @Disabled("Fails in the second assertion of 2nd round, fact handle fhNicole  points to 'Mary' but fhNicole.hasMatches=true")
    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void updateByObject_insertFireUpdateFailover_shouldMatchUpdatedFact(PersistedSessionOption.Strategy strategy){
        long savedSessionId;
        // 1st round
        {
            KieSession firstSession = getFirstKieSession(BASIC_RULE, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));
            FactHandle fhNicole = firstSession.insert(new Person("Nicole", 32));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);

            Person pMary = new Person("Mary", 32);
            firstSession.update(fhNicole, pMary);
        }

        failover();
        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(BASIC_RULE, savedSessionId, strategy);

            this.getPersonByName(secondSession,"Mary").ifPresent(person -> assertThat(person.getAge()).isEqualTo(32));

            try {
                assertThat(secondSession.fireAllRules()).isEqualTo(1);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @Disabled("Fails in the first assertion of 2nd round, Eleven is populated with Age=17")
    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void updateInRHS_insertFireFailoverFire_shouldMatchUpdatesFromFirstSession(PersistedSessionOption.Strategy strategy){
        long savedSessionId;
        // 1st round
        {
            KieSession firstSession = getFirstKieSession(TWO_RULES, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mike", 22));
            firstSession.insert(new Person("Eleven", 17));
            firstSession.insert(17); // person with age=17 will change to 18 (17+1)

            firstSession.fireAllRules(); // person with name that starts with M and has age>17 will be added to the results list

            assertThat(getResults(firstSession)).containsExactlyInAnyOrder(22);

            // ensure that Eleven's age was updated to 18
            this.getPersonByName(firstSession,"Eleven").ifPresent(person -> assertThat(person.getAge()).isEqualTo(18));
        }
        failover();
        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(TWO_RULES, savedSessionId, strategy);

            // ensure that Eleven's updated version (age=18) was populated into secondSession
            this.getPersonByName(secondSession,"Eleven").ifPresent(person -> assertThat(person.getAge()).isEqualTo(18));

            try {
                secondSession.insert("E");
                secondSession.fireAllRules();
                assertThat(getResults(secondSession)).containsExactlyInAnyOrder(18);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void insertFireFailoverInsertFire_shouldMatchFactFromFirstSession(PersistedSessionOption.Strategy strategy) {
        long savedSessionId;
        // 1st round
        {
            KieSession firstSession = getFirstKieSession(BASIC_RULE, strategy);

            savedSessionId = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mike", 17));
            firstSession.insert(new Person("Eleven", 16));

            firstSession.fireAllRules();

            assertThat(getResults(firstSession)).containsExactlyInAnyOrder(17);
        }
        failover();
        // 2nd round
        {
            KieSession secondSession = getSubsequentKieSession(BASIC_RULE, savedSessionId, strategy);
            secondSession.insert("E");

            try {
                secondSession.fireAllRules();
                assertThat(getResults(secondSession)).containsExactlyInAnyOrder(16);
            } finally {
                secondSession.dispose();
            }
        }
    }
}
