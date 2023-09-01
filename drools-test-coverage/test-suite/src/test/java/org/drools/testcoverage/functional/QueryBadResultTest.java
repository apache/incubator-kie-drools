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

import java.util.Collection;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests bad using and accessing to queries.
 */
@RunWith(Parameterized.class)
public class QueryBadResultTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public QueryBadResultTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testQueriesWithSameNameInOneFile() {
        final KieBuilder kieBuilder =
                KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration, getClass(), false, "query-two-same-names.drl");
        assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void testQueriesWithSameNameInTwoFiles() {
        final KieBuilder kieBuilder =
                KieUtil.getKieBuilderFromClasspathResources(
                        kieBaseTestConfiguration,
                        getClass(),
                        false,
                        "query-same-name-1.drl",
                        "query-same-name-2.drl");

        assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void testQueryWithoutName() {
        final KieBuilder kieBuilder =
                KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration, getClass(), false, "query-without-name.drl");
        assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void testQueryCalledWithoutParamsButItHasParams() {
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

    @Test(expected = IllegalArgumentException.class)
    public void testAccessToNotExistingVariable() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration,"query.drl");
        final KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr"));

        final QueryResults results = ksession.getQueryResults("simple query with no parameters");
        results.iterator().next().get("bad");
    }
}
