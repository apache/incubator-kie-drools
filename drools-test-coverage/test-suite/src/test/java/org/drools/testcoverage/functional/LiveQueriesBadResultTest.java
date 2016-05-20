/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;

import java.util.ArrayList;

/**
 * Tests bad using and accessing to livequeries.
 */
public class LiveQueriesBadResultTest {

    private ArrayList<Object> inserted, updated, deleted;

    @Before
    public void initialize() {
        inserted = new ArrayList<>();
        updated = new ArrayList<>();
        deleted = new ArrayList<>();
    }

    @Test(expected = RuntimeException.class)
    public void testCallingLiveQueryWithoutParametersButItHasParams() {

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

        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), true, "query.drl");
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr"));

        ksession.openLiveQuery("queryWithParams", new Object[] {}, listener);
    }

    @Test(expected = RuntimeException.class)
    public void testBadAccessToParameterWithoutType() {

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

        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(
                getClass(),
                false,
                "query-bad-parametr-access.drl");
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr", 25));

        ksession.openLiveQuery("queryWithParamWithoutType", new Object[] { "Petr", 26 }, listener);
    }

    @Test(expected = RuntimeException.class)
    public void testAccessToNotExistingVariable() {

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

        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), true, "query.drl");
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr", 25));

        ksession.openLiveQuery("simple query with no parameters", new Object[] { "Petr", 26 }, listener);
    }

    @Test(expected = RuntimeException.class)
    public void testOfBadListener() {
        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), true, "query.drl");
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr", 25));

        ksession.openLiveQuery("simple query with no parameters", new Object[] { "Petr", 26 },
                (ViewChangedEventListener) null);
    }

    @Test(expected = RuntimeException.class)
    public void testOfBadParameters() {

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

        final KieBase kieBase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), true, "query.drl");
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Petr", 25));

        ksession.openLiveQuery("queryWithParamWithoutType", (Object[]) null, listener);
    }

}
