/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.functional;

import java.util.Collection;
import org.assertj.core.api.Assertions;
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
        Assertions.assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
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

        Assertions.assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
    }

    @Test
    public void testQueryWithoutName() {
        final KieBuilder kieBuilder =
                KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration, getClass(), false, "query-without-name.drl");
        Assertions.assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
    }

    @Test(expected = RuntimeException.class)
    public void testQueryCalledWithoutParamsButItHasParams() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, true, "query.drl");
        final KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr"));

        ksession.getQueryResults("personWithName");
    }

    @Test
    public void testBadAccessToParameterWithoutType() {
        final KieBuilder kieBuilder =
                KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration, getClass(), false, "query-bad-parametr-access.drl");
        Assertions.assertThat(kieBuilder.getResults().getMessages(Level.ERROR).isEmpty()).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAccessToNotExistingVariable() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration,true, "query.drl");
        final KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr"));

        final QueryResults results = ksession.getQueryResults("simple query with no parameters");
        results.iterator().next().get("bad");
    }
}
