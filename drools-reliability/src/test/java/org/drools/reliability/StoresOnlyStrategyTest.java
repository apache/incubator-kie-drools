package org.drools.reliability;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.reliability.ReliabilityTestUtils.failover;

@ExtendWith(BeforeAllMethodExtension.class)
public class StoresOnlyStrategyTest {

    private static final String BASIC_RULE =
            "import " + Person.class.getCanonicalName() + ";" +
                    "global java.util.List results;" +
                    "rule X when\n" +
                    "  $s: String()\n" +
                    "  $p: Person( getName().startsWith($s) )\n" +
                    "then\n" +
                    "  results.add( $p.getAge() );\n" +
                    "end";

    @AfterEach
    public void tearDown() {
        // We can remove this when we implement ReliableSession.dispose() to call CacheManager.removeCachesBySessionId(id)
        CacheManager.INSTANCE.removeAllSessionCaches();
    }

    private KieSession getKieSession(String drl, PersistedSessionOption option) {
        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(option);
        return kbase.newKieSession(conf, null);
    }

    @Test
    void insertFireUpdateFailover_RePropagateUpdates() {
        long id;

        // 1st round
        {
            KieSession firstSession = getKieSession(BASIC_RULE, PersistedSessionOption.newSession(PersistedSessionOption.Strategy.STORES_ONLY));
            List<Integer> results = new ArrayList<>();
            firstSession.setGlobal("results", results);

            id = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Matteo", 41));
            Person pMark = new Person("_Mark", 47);
            FactHandle fhMark = firstSession.insert(pMark);

            assertThat(firstSession.fireAllRules()).isEqualTo(1);
            assertThat(results).containsExactlyInAnyOrder(41);

            pMark.setName("Mark");
            firstSession.update(fhMark, pMark);
        }

        failover();

        // 2nd round
        {
            KieSession secondSession = getKieSession(BASIC_RULE, PersistedSessionOption.fromSession(id, PersistedSessionOption.Strategy.STORES_ONLY));
            List<Integer> results = new ArrayList<>();
            secondSession.setGlobal("results", results);

            try {
                secondSession.insert(new Person("Toshiya", 45));
                secondSession.insert(new Person("Mario", 49));

                assertThat(secondSession.fireAllRules()).isEqualTo(2);
                assertThat(results).containsExactlyInAnyOrder(49,47);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @Test
    void insertUpdateFireFailover_CacheImmutableKey() {
        long id;

        // 1st round
        {
            KieSession firstSession = getKieSession(BASIC_RULE, PersistedSessionOption.newSession(PersistedSessionOption.Strategy.STORES_ONLY));
            List<Integer> results = new ArrayList<>();
            firstSession.setGlobal("results", results);

            id = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Matteo", 41));

            Person pMark = new Person("Mark", 47);
            FactHandle fhMark = firstSession.insert(new Person("Mark", 47));
            pMark.setName("_Mark");
            firstSession.update(fhMark, pMark);

            assertThat(firstSession.fireAllRules()).isEqualTo(1);
            assertThat(results).containsExactlyInAnyOrder(41);
        }

        failover();

        // 2nd round
        {
            KieSession secondSession = getKieSession(BASIC_RULE, PersistedSessionOption.fromSession(id, PersistedSessionOption.Strategy.STORES_ONLY));
            List<Integer> results = new ArrayList<>();
            secondSession.setGlobal("results", results);

            try {
                secondSession.insert(new Person("Toshiya", 45));
                secondSession.insert(new Person("Mario", 49));

                assertThat(secondSession.fireAllRules()).isEqualTo(1);
                assertThat(results).containsExactlyInAnyOrder(49);
            } finally {
                secondSession.dispose();
            }
        }
    }

    @Test
    void fireInsertUpdateFailover_CacheImmutableKey() {
        long id;

        // 1st round
        {
            KieSession firstSession = getKieSession(BASIC_RULE, PersistedSessionOption.newSession(PersistedSessionOption.Strategy.STORES_ONLY));
            List<Integer> results = new ArrayList<>();
            firstSession.setGlobal("results", results);

            id = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Matteo", 41));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);
            assertThat(results).containsExactlyInAnyOrder(41);

            Person pMark = new Person("Mark", 47);
            FactHandle fhMark = firstSession.insert(new Person("Mark", 47));
            pMark.setName("_Mark");
            firstSession.update(fhMark, pMark);
        }

        failover();

        // 2nd round
        {
            KieSession secondSession = getKieSession(BASIC_RULE, PersistedSessionOption.fromSession(id, PersistedSessionOption.Strategy.STORES_ONLY));
            List<Integer> results = new ArrayList<>();
            secondSession.setGlobal("results", results);

            try {
                secondSession.insert(new Person("Toshiya", 45));
                secondSession.insert(new Person("Mario", 49));

                assertThat(secondSession.fireAllRules()).isEqualTo(1);
                assertThat(results).containsExactlyInAnyOrder(49);
            } finally {
                secondSession.dispose();
            }
        }
    }
}
