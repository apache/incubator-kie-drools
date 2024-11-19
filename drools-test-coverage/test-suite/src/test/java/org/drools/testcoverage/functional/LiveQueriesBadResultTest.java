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

import java.util.ArrayList;
import java.util.stream.Stream;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests bad using and accessing to livequeries.
 */
public class LiveQueriesBadResultTest {

    private ArrayList<Object> inserted, updated, deleted;

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    @BeforeEach
    public void initialize() {
        inserted = new ArrayList<>();
        updated = new ArrayList<>();
        deleted = new ArrayList<>();
    }

    @Disabled("TODO - check correct exception in this test when DROOLS-2186 is fixed.")
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testCallingLiveQueryWithoutParametersButItHasParams(KieBaseTestConfiguration kieBaseTestConfiguration) {

        final ViewChangedEventListener listener = new ViewChangedEventListener() {

            @Override
            public void rowUpdated(Row row) {
                updated.add(row.get("person"));
            }

            @Override
            public void rowInserted(Row row) {
                inserted.add(row.get("person"));
            }

            @Override
            public void rowDeleted(Row row) {
                deleted.add(row.get("person"));
            }
        };

        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration,
                "query.drl");
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr"));

        ksession.openLiveQuery("queryWithParams", new Object[] {}, listener);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAccessToNotExistingVariable(KieBaseTestConfiguration kieBaseTestConfiguration) {

        ViewChangedEventListener listener = new ViewChangedEventListener() {

            @Override
            public void rowUpdated(Row row) {
                updated.add(row.get("bad"));
            }

            @Override
            public void rowInserted(Row row) {
                inserted.add(row.get("bad"));
            }

            @Override
            public void rowDeleted(Row row) {
                deleted.add(row.get("bad"));
            }
        };

        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration,
                "query.drl");
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr", 25));


        assertThatThrownBy(() -> ksession.openLiveQuery("simple query with no parameters", new Object[]{}, listener))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The identifier 'bad' does not exist as a bound variable for this query");
    }

    @Disabled("TODO - check correct exception in this test when DROOLS-2187 is fixed.")
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOfBadParameters(KieBaseTestConfiguration kieBaseTestConfiguration) {

        ViewChangedEventListener listener = new ViewChangedEventListener() {

            @Override
            public void rowUpdated(Row row) {
                updated.add(row.get("person"));
            }

            @Override
            public void rowInserted(Row row) {
                inserted.add(row.get("person"));
            }

            @Override
            public void rowDeleted(Row row) {
                deleted.add(row.get("person"));
            }
        };

        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration,
                "query.drl");
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr", 25));

        ksession.openLiveQuery("queryWithParamWithoutType", null, listener);
    }

}
