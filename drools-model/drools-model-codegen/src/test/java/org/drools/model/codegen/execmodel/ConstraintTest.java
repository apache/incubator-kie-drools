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
package org.drools.model.codegen.execmodel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintTest extends BaseModelTest {

    private static final String RULE_STRING = "package constraintexpression\n" +
            "\n" +
            "import " + Person.class.getCanonicalName() + "\n" +
            "import java.util.List; \n" +
            "import java.math.BigDecimal; \n" +
            "global List<BigDecimal> bigDecimalListGlobal; \n" +
            "rule \"r1\"\n" +
            "when \n" +
            "    $p : Person($amount: (money == null ? BigDecimal.valueOf(100.0) : money))\n" +
            "then \n" +
            "    System.out.println($amount); \n" +
            "    System.out.println($p); \n" +
            "    bigDecimalListGlobal.add($amount); \n " +
            "end \n";

    private KieSession ksession;

    private List<BigDecimal> bigDecimalListGlobal;

    public ConstraintTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Before
    public void setup() {
        ksession = getKieSession(RULE_STRING);
        bigDecimalListGlobal = new ArrayList<>();
        ksession.setGlobal("bigDecimalListGlobal", bigDecimalListGlobal);
    }

    @Test
    public void testConstraintWithMoney() {
        try {
            BigDecimal money = BigDecimal.valueOf(34.45);
            Person person = new Person("", money);
            ksession.insert(person);
            int rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(1);
            assertThat(bigDecimalListGlobal).isNotEmpty().containsExactly(money);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testConstraintWithoutMoney() {
        try {
            Person person = new Person();
            ksession.insert(person);
            int rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(1);
            assertThat(bigDecimalListGlobal).isNotEmpty().containsExactly(BigDecimal.valueOf(100.0));
        } finally {
            ksession.dispose();
        }
    }
}
