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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.List;
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
            "  results.add( $p.getName() );\n" +
            "end";
	private long savedSessionId;
	private KieSession session;

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

        createSession(BASIC_RULE, strategy);

		insertString();
		insertMatchingPersonOne();

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();

        restoreSession(BASIC_RULE, strategy);

		insertNonMatchingPerson();
		insertMatchingPersonTwo();

		session.fireAllRules();

		assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One", "Matching Person Two");
    }


    @ParameterizedTest
    @MethodSource("strategyProvider")
    void noFailover(PersistedSessionOption.Strategy strategy) {


        createSession(BASIC_RULE, strategy);

		insertString();
		insertMatchingPersonOne();

        restoreSession(BASIC_RULE, strategy);

		insertNonMatchingPerson();
		insertMatchingPersonTwo();

		session.fireAllRules();

		assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One", "Matching Person Two");
    }


    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertFireInsertFailoverInsertFire_shouldMatchFactInsertedBeforeFailover(PersistedSessionOption.Strategy strategy) {

        createSession(BASIC_RULE, strategy);

		insertString();
		insertMatchingPersonOne();

		session.fireAllRules();

		insertMatchingPersonTwo();

        failover();

        restoreSession(BASIC_RULE, strategy);

        insertNonMatchingPerson();
        insertMatchingPersonThree();

        session.fireAllRules();

        assertThat(getResults()).containsExactlyInAnyOrder("Matching Person Two", "Matching Person Three");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly") // FULL fails with "ReliablePropagationList; no valid constructor"
    void insertFireFailoverInsertFire_shouldNotRepeatFiredMatch(PersistedSessionOption.Strategy strategy) {
        createSession(BASIC_RULE, strategy);

		insertString();
		insertMatchingPersonOne();

		session.fireAllRules();

        failover();

        restoreSession(BASIC_RULE, strategy);

        insertNonMatchingPerson();
		insertMatchingPersonTwo();

		session.fireAllRules();

		assertThat(getResults()).containsExactlyInAnyOrder("Matching Person Two");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly") // FULL fails with "ReliablePropagationList; no valid constructor"
    void updateBeforeFailover_shouldRecoverFromFailover(PersistedSessionOption.Strategy strategy) {

        createSession(BASIC_RULE, strategy);

        insertString();
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
        restoreSession(BASIC_RULE, strategy);

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("MegaToshiya");

        failover();
        restoreSession(BASIC_RULE, strategy);

        assertThat(session.fireAllRules()).isEqualTo(0);
        assertThat(getResults()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly") // FULL fails with "ReliablePropagationList; no valid constructor"
    void deleteBeforeFailover_shouldRecoverFromFailover(PersistedSessionOption.Strategy strategy) {

        createSession(BASIC_RULE, strategy);

        FactHandle fhString = insertString();
        insertMatchingPersonOne();
        insertNonMatchingPerson();

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One");

        session.delete(fhString);

        failover();
        restoreSession(BASIC_RULE, strategy);

        insertMatchingPersonTwo();

        assertThat(session.fireAllRules()).isEqualTo(0);
        assertThat(getResults()).isEmpty();

        session.insert("T");

        failover();
        restoreSession(BASIC_RULE, strategy);

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder("Toshiya");
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void updateByObjectBeforeFailover_shouldMatchUpdatedFact(PersistedSessionOption.Strategy strategy){

        createSession(BASIC_RULE, strategy);

        session.insert("M");
        session.insert(new Person("Mark", 37)); //insertMatchingPerson("Mark", 37);
        FactHandle fhNicole = session.insert(new Person("Nicole", 32)); //insertNonMatchingPerson("Nicole", 32);

        assertThat(session.fireAllRules()).isEqualTo(1);

        session.update(fhNicole, new Person("Mary", 32)); //updateWithMatchingPerson(fhNicole,new Person("Mary", 32));

        failover();

        restoreSession(BASIC_RULE, strategy);

        assertThat(session.fireAllRules()).isEqualTo(1);

        failover();

        assertThat(session.fireAllRules()).isEqualTo(0);
    }

    private FactHandle insertString() {
		return session.insert("M");
	}

	private void insertMatchingPersonOne() {
		session.insert(new Person("Matching Person One", 37));
	}

	private void insertMatchingPersonTwo() {
		session.insert(new Person("Matching Person Two", 40));
	}
	
	private void insertMatchingPersonThree() {
		session.insert(new Person("Matching Person Three", 41));
	}

	private void insertNonMatchingPerson() {
		session.insert(new Person("Toshiya", 35));
	}

    private List<String> getResults() {
        return (List<String>) session.getGlobal("results");
    }

    private KieSession createSession(String drl, PersistedSessionOption.Strategy strategy) {
        getKieSession(drl, PersistedSessionOption.newSession(strategy));
        savedSessionId = session.getIdentifier();
        return session;
    }

    private KieSession restoreSession(String drl, PersistedSessionOption.Strategy strategy) {
        return getKieSession(drl, PersistedSessionOption.fromSession(savedSessionId, strategy));
    }

    private KieSession getKieSession(String drl, PersistedSessionOption option) {
        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(option);
        session = kbase.newKieSession(conf, null);
        List<String> results = new ArrayList<>();
        session.setGlobal("results", results);
        return session;
    }

}
