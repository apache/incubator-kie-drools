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

@ExtendWith(BeforeAllMethodExtension.class)
public class ReliabilityTest {

    private final String BASIC_DRL = "import " + Person.class.getCanonicalName() + ";" +
                                      "rule X when\n" +
                                      "  $s: String()\n" +
                                      "  $p: Person( getName().startsWith($s) )\n" +
                                      "then\n" +
                                      "  System.out.println( $p.getAge() );\n" +
                                      "end";

    @AfterEach
    public void tearDown() {
        CacheManager.INSTANCE.removeCache("cacheSession_0");
    }

    @Test
    public void failover_fireAtLast() {
        KieBase kbase = new KieHelper().addContent(BASIC_DRL, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(PersistedSessionOption.newSession());
        KieSession firstSession = kbase.newKieSession(conf, null);

        long id = firstSession.getIdentifier();

        firstSession.insert("M");
        firstSession.insert(new Person("Mark", 37));

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. Client knows only "id"

        KieBase kbase2 = new KieHelper().addContent(BASIC_DRL, ResourceType.DRL).build();
        KieSessionConfiguration conf2 = KieServices.get().newKieSessionConfiguration();
        conf2.setOption(PersistedSessionOption.fromSession(id));
        KieSession secondSession = kbase2.newKieSession(conf2, null);

        try {
            secondSession.insert(new Person("Edson", 35));
            secondSession.insert(new Person("Mario", 40));

            assertThat(secondSession.fireAllRules()).isEqualTo(2);

        } finally {
            secondSession.dispose();
        }
    }

    @Test
    public void noFailover() { // a plain use case without fail-over
        KieBase kbase = new KieHelper().addContent(BASIC_DRL, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(PersistedSessionOption.newSession());
        KieSession firstSession = kbase.newKieSession(conf, null);

        try {
            firstSession.insert("M");
            firstSession.insert(new Person("Mark", 37));
            firstSession.insert(new Person("Helen", 54));

            assertThat(firstSession.fireAllRules()).isEqualTo(1);

            firstSession.insert(new Person("Nicole", 27));

            assertThat(firstSession.fireAllRules()).isEqualTo(0);

        } finally {
            firstSession.dispose();
        }
    }

    //@Disabled("It fails at the assertion, secondSession.fireAllRules() returns 0.")
    @Test
    public void fireBeforeAndAfterFailover() {
        KieBase kbase = new KieHelper().addContent(BASIC_DRL, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(PersistedSessionOption.newSession());
        KieSession firstSession = kbase.newKieSession(conf, null);

        long id = firstSession.getIdentifier();

        firstSession.insert("M");
        firstSession.insert(new Person("Mark", 37));

        firstSession.fireAllRules();

        //-- Assume JVM down here

        KieBase kbase2 = new KieHelper().addContent(BASIC_DRL, ResourceType.DRL).build();
        KieSessionConfiguration conf2 = KieServices.get().newKieSessionConfiguration();
        conf2.setOption(PersistedSessionOption.fromSession(id));
        KieSession secondSession = kbase2.newKieSession(conf2, null);

        try {
            secondSession.insert(new Person("Edson", 35));
            secondSession.insert(new Person("Mario", 40));

            // Replay scenario : fired = 2
            // Continue scenario : fired = 1
            assertThat(secondSession.fireAllRules()).isEqualTo(2);
        } finally {
            secondSession.dispose();
        }
    }

    //@Disabled("It fails at the assertion, secondSession.fireAllRules() returns 0.")
    @Test
    public void update_fireBeforeAndAfterFailover() {
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule X when\n" +
                     "  $s: String()\n" +
                     "  $p: Person( getName().startsWith($s) )\n" +
                     "then\n" +
                     "  System.out.println( $p.getAge() );\n" +
                     "  $p.setName(\"-\");\n" +
                     "  update($p); \n" +
                     "end";

        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(PersistedSessionOption.newSession());
        KieSession firstSession = kbase.newKieSession(conf, null);

        long id = firstSession.getIdentifier();

        firstSession.insert("M");
        firstSession.insert(new Person("Mark", 37));
        firstSession.insert(new Person("Nicole", 27));

        assertThat(firstSession.fireAllRules()).isEqualTo(1);

        //-- Assume JVM down here

        KieBase kbase2 = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSessionConfiguration conf2 = KieServices.get().newKieSessionConfiguration();
        conf2.setOption(PersistedSessionOption.fromSession(id));
        KieSession secondSession = kbase2.newKieSession(conf2, null);

        try {
            secondSession.insert(new Person("John", 22));
            secondSession.insert(new Person("Mary", 42));

            assertThat(secondSession.fireAllRules()).isEqualTo(1);

        } finally {
            secondSession.dispose();
        }

    }

}
