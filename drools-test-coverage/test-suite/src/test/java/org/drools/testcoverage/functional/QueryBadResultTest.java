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
package org.drools.testcoverage.functional;

import java.util.stream.Stream;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests bad using and accessing to queries.
 */
public class QueryBadResultTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueriesWithSameNameInOneFile(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieBuilder kieBuilder =
                KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration, getClass(), false, "query-two-same-names.drl");
        assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueriesWithSameNameInTwoFiles(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieBuilder kieBuilder =
                KieUtil.getKieBuilderFromClasspathResources(
                        kieBaseTestConfiguration,
                        getClass(),
                        false,
                        "query-same-name-1.drl",
                        "query-same-name-2.drl");

        assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryWithoutName(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieBuilder kieBuilder =
                KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration, getClass(), false, "query-without-name.drl");
        assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueryCalledWithoutParamsButItHasParams(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "query.drl");
        final KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr"));

        try {
            ksession.getQueryResults("personWithName");
            fail("invocation with wrong number of arguments must fail");
        } catch (RuntimeException e) {
            assertThat(e.getMessage().contains("wrong number of arguments")).isTrue();
        }
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAccessToNotExistingVariable(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration,"query.drl");
        final KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr"));

        final QueryResults results = ksession.getQueryResults("simple query with no parameters");
        assertThatIllegalArgumentException().isThrownBy(() -> results.iterator().next().get("bad"));
    }
}
