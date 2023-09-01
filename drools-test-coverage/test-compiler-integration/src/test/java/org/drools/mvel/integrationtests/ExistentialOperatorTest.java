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
package org.drools.mvel.integrationtests;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

@RunWith(Parameterized.class)
public class ExistentialOperatorTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ExistentialOperatorTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testForallAfterOr() throws Exception {
        // DROOLS-2710
        String str =
                "package redhat\n" +
                "declare Fact\n" +
                "    integer : int\n" +
                "    string1 : String\n" +
                "    string2 : String\n" +
                "end\n" +
                "rule \"Rule\"\n" +
                "when\n" +
                "Fact(string2 == \"Y\")\n" +
                "(\n" +
                "    exists (Fact(integer == 42)) or\n" +
                "    Fact(integer == 43)\n" +
                ")\n" +
                "forall (Fact(string1 == \"X\"))\n" +
                "then\n" +
                "end";

        KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession kieSession = kieBase.newKieSession();

        FactType factType = kieBase.getFactType("redhat", "Fact");

        Object fact = factType.newInstance();
        factType.set(fact, "string1", "X");
        factType.set(fact, "string2", "Y");
        factType.set(fact, "integer", 42);

        kieSession.insert(fact);

        int n = kieSession.fireAllRules();
        assertThat(n).isEqualTo(1);
    }
}
