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

import org.test.domain.Person;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.conf.PersistedSessionOption;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BeforeAllMethodExtension.class)
public class ReliabilityTestUpdateInDrl extends ReliabilityTestBasics {

    private static final String RULE_UPDATE =
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

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnly")
    void updateInRHS_insertFireFailoverFire_shouldMatchUpdatesFromFirstSession(PersistedSessionOption.Strategy strategy){

        createSession(RULE_UPDATE, strategy);

        insertString("M");
        insertMatchingPerson("Mike",22);
        insertNonMatchingPerson("Eleven", 17);
        insertInteger(17); // person with age=17 will change to 18 (17+1)

        assertThat(session.fireAllRules()).isEqualTo(2); // person with name that starts with M and has age>17 will be added to the results list
        assertThat(getResults()).containsExactlyInAnyOrder(22);

        failover();

        restoreSession(RULE_UPDATE, strategy);
        clearResults();

        insertString("E"); // NonMatchingPerson will match rule X

        assertThat(session.fireAllRules()).isEqualTo(1);
        assertThat(getResults()).containsExactlyInAnyOrder(18);

        failover();

        restoreSession(RULE_UPDATE, strategy);
        clearResults();

        assertThat(session.fireAllRules()).isEqualTo(0);
        assertThat(getResults()).isEmpty();
    }

}

