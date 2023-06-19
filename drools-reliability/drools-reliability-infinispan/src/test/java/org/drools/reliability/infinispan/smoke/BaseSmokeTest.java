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

package org.drools.reliability.infinispan.smoke;

import org.drools.reliability.infinispan.BeforeAllMethodExtension;
import org.drools.reliability.infinispan.ReliabilityTestBasics;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.test.domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BeforeAllMethodExtension.class)
public class BaseSmokeTest extends ReliabilityTestBasics {

    private static final String BASIC_RULE =
            "import " + Person.class.getCanonicalName() + ";" +
            "global java.util.List results;" +
            "rule X when\n" +
            "  $s: String()\n" +
            "  $p: Person( getName().startsWith($s) )\n" +
            "then\n" +
            "  results.add( $p.getName() );\n" +
            "end";

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertFailoverInsertFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {
        createSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insert("M");
		insertMatchingPerson("Matching Person One", 37);

        //-- Assume JVM down here. Fail-over to other JVM or rebooted JVM
        //-- ksession and kbase are lost. CacheManager is recreated. Client knows only "id"
        failover();

        restoreSession(BASIC_RULE, persistenceStrategy, safepointStrategy);

		insertNonMatchingPerson("Toshiya", 35);
        insertMatchingPerson("Matching Person Two", 40);

		fireAllRules();

		assertThat(getResults()).containsExactlyInAnyOrder("Matching Person One", "Matching Person Two");
    }
}
