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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.FactHandle;
import org.test.domain.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.reliability.test.ReliabilityTest.BASIC_RULE;

/**
 * This test class is to keep tests for FullStrategy, which is suspended at the moment
 */
@Disabled("FullStrategy is suspended at the moment")
@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityFullStrategyTest extends ReliabilityTestBasics {

    @ParameterizedTest
    @MethodSource("strategyProviderFull")
    void insertFailover_propListShouldNotBeEmpty(PersistedSessionOption.PersistenceStrategy strategy) {
        KieSession session = createSession(BASIC_RULE, strategy);

        insert("M");
        insertMatchingPerson("Maria", 30);

        failover();

        restoreSession(BASIC_RULE, strategy);

        assertThat(fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderFull")
    void insertFireFailover_shouldNotRepeatFiredMatch(PersistedSessionOption.PersistenceStrategy strategy) {
        createSession(BASIC_RULE, strategy);

        insert("M");
        insertMatchingPerson("Maria", 30);

        fireAllRules();

        failover();

        restoreSession(BASIC_RULE, strategy);

        assertThat(fireAllRules()).isZero();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderFull")
    void insertUpdateFailover_shouldNotFiredMatch(PersistedSessionOption.PersistenceStrategy strategy) {
        createSession(BASIC_RULE, strategy);

        insert("M");
        FactHandle fhMaria = insertMatchingPerson("Maria", 30);

        updateWithNonMatchingPerson(fhMaria, new Person("Nicole", 32));

        failover();

        restoreSession(BASIC_RULE, strategy);

        assertThat(fireAllRules()).isZero();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderFull")
    void insertNonMatching_Failover_UpdateWithMatching_ShouldFiredMatch(PersistedSessionOption.PersistenceStrategy strategy) {
        createSession(BASIC_RULE, strategy);

        insert("N");
        FactHandle fhMaria = insertMatchingPerson("Maria", 30);

        failover();

        restoreSession(BASIC_RULE, strategy);

        updateWithMatchingPerson(fhMaria, new Person("Nicole", 32));

        assertThat(fireAllRules()).isEqualTo(1);
    }
}
