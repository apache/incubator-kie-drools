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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.reliability.ReliabilityTestUtils.failover;

@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityTest {

    private static final String BASIC_RULE =
            "import " + Person.class.getCanonicalName() + ";" +
            "rule X when\n" +
            "  $s: String()\n" +
            "  $p: Person( getName().startsWith($s) )\n" +
            "then\n" +
            "  System.out.println( $p.getAge() );\n" +
            "end";

    @AfterEach
    public void tearDown() {
        // We can remove this when we implement ReliableSession.dispose() to call CacheManager.removeCachesBySessionId(id)
        CacheManager.INSTANCE.removeAllSessionCaches();
    }

    @Disabled("Fails with org.infinispan.persistence.spi.PersistenceException: ReliablePropagationList; no valid constructor")
    @Test
    void basic_insert_failover_insert_fire() {

        long id;

        // 1st round
        {
            KieSession firstSession = getKieSession(BASIC_RULE, PersistedSessionOption.newSession());

            id = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));
        }

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();

        // 2nd round
        {
            KieSession secondSession = getKieSession(BASIC_RULE, PersistedSessionOption.fromSession(id));

            try {
                secondSession.insert(new Person("Edson", 35));
                secondSession.insert(new Person("Mario", 40));

                assertThat(secondSession.fireAllRules()).isEqualTo(2);
            } finally {
                secondSession.dispose();
            }
        }
    }

    private KieSession getKieSession(String drl, PersistedSessionOption option) {
        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(option);
        return kbase.newKieSession(conf, null);
    }

    @Test
    void noFailover() {
        KieSession firstSession = getKieSession(BASIC_RULE, PersistedSessionOption.newSession());

        try{
            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));
            firstSession.insert(new Person("Helen", 54));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);

            firstSession.insert(new Person("Nicole", 27));

            assertThat(firstSession.fireAllRules()).isZero();

        } finally {
            firstSession.dispose();
        }
    }

    @Disabled("It fails at the assertion, secondSession.fireAllRules() returns 0.")
    @Test
    void basic_insert_fire_failover_insert_fire() {
        long id;
        {
            KieSession firstSession = getKieSession(BASIC_RULE, PersistedSessionOption.newSession());

            id = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);
        }

        failover();

        {
            KieSession secondSession = getKieSession(BASIC_RULE, PersistedSessionOption.fromSession(id));

            try {
                secondSession.insert(new Person("Edson", 35));
                secondSession.insert(new Person("Mario", 40));

                assertThat(secondSession.fireAllRules()).isEqualTo(1); // Only Mario matches.
            } finally {
                secondSession.dispose();
            }
        }
    }

    @Disabled("It fails at the assertion, secondSession.fireAllRules() returns 0.")
    @Test
    void updateInRHS_insert_fire_failover_insert_fire() {
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule X when\n" +
                        "  $s: String()\n" +
                        "  $p: Person( getName().startsWith($s) )\n" +
                        "then\n" +
                        "  System.out.println( $p.getAge() );\n" +
                        "  $p.setName(\"-\");\n" +
                        "  update($p); \n" + // updated Person will not match
                       "end";

        long id;
        {
            KieSession firstSession = getKieSession(drl, PersistedSessionOption.newSession());

            id = firstSession.getIdentifier();

            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));
            firstSession.insert(new Person("Nicole", 27));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);
        }

        failover();

        {
            KieSession secondSession = getKieSession(drl, PersistedSessionOption.fromSession(id));

            try {
                secondSession.insert(new Person("John", 22));
                secondSession.insert(new Person("Mary", 42));

                assertThat(secondSession.fireAllRules()).isEqualTo(1);
            } finally {
                secondSession.dispose();
            }
        }

    }
}
