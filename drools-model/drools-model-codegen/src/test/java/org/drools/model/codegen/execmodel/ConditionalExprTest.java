/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class ConditionalExprTest extends BaseModelTest {

    private static final String RULE_STRING = "package constraintexpression\n" +
            "\n" +
            "import " + Person.class.getCanonicalName() + "\n" +
            "import java.util.List; \n" +
            "global List<Boolean> booleanListGlobal; \n" +
            "rule \"r1\"\n" +
            "when \n" +
            "    $p : Person($booleanVariable: (name != null ? true : false))\n" +
            "then \n" +
            "    System.out.println($booleanVariable); \n" +
            "    System.out.println($p); \n" +
            "    booleanListGlobal.add($booleanVariable); \n " +
            "end \n";

    private KieSession ksession;
    private List<Boolean> booleanListGlobal;

    public ConditionalExprTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Before
    public void setup() {
        ksession = getKieSession(RULE_STRING);
        booleanListGlobal = new ArrayList<>();
        ksession.setGlobal("booleanListGlobal", booleanListGlobal);
    }

    @Test
    public void testConditionalExpressionWithNamedPerson() {
        try {
            Person person = new Person("someName");
            ksession.insert(person);
            int rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(1);
            assertThat(booleanListGlobal).isNotEmpty().containsExactly(Boolean.TRUE);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testConditionalExpressionWithUnnamedPerson() {
        try {
            Person person = new Person();
            ksession.insert(person);
            int rulesFired = ksession.fireAllRules();
            assertThat(rulesFired).isEqualTo(1);
            assertThat(booleanListGlobal).isNotEmpty().containsExactly(Boolean.FALSE);
        } finally {
            ksession.dispose();
        }
    }

}