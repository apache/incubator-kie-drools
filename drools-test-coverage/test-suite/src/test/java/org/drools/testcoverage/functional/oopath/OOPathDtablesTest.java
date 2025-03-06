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
package org.drools.testcoverage.functional.oopath;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.InternationalAddress;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.ResourceUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.builder.DecisionTableInputType;

import static org.assertj.core.api.Assertions.*;

/**
 * Test basic OOPath expressions used in Decision tables (*.xls, *.xlsx, *.csv)
 * in both RuleTable and Queries as well.
 */
public class OOPathDtablesTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseConfigurations().stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void xlsWithOOPathTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieSession kieSession = getKieSessionFromXls(kieBaseTestConfiguration, "oopath.drl.xls");
        testOOPathWithDTable(kieSession);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void xlsxWithOOPathTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieSession kieSession = getKieSessionFromXlsx(kieBaseTestConfiguration, "oopath.drl.xlsx");
        testOOPathWithDTable(kieSession);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void csvWithOOPathTest(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final KieSession kieSession = getKieSessionFromCsv(kieBaseTestConfiguration, "oopath.drl.csv");
        testOOPathWithDTable(kieSession);
    }

    private void testOOPathWithDTable(final KieSession kieSession) {
        final List<String> list = new ArrayList<>();
        populateKieSession(kieSession, list);

        final int rulesFired = kieSession.fireAllRules();

        assertThat(rulesFired).isEqualTo(2);
        verifyRuleFireResults(list);
        verifyQueryResults(kieSession.getQueryResults("listSafeCities"));
    }

    private KieSession getKieSessionFromCsv(KieBaseTestConfiguration kieBaseTestConfiguration, final String csvFile) {
        final Resource resource =
                ResourceUtil.getDecisionTableResourceFromClasspath(csvFile, getClass(), DecisionTableInputType.CSV);

        return KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, resource).newKieSession();
    }

    private KieSession getKieSessionFromXls(KieBaseTestConfiguration kieBaseTestConfiguration, final String xlsFile) {
        return getKieSessionFromExcel(kieBaseTestConfiguration, xlsFile, DecisionTableInputType.XLS);
    }

    private KieSession getKieSessionFromXlsx(KieBaseTestConfiguration kieBaseTestConfiguration, final String xlsxFile) {
        return getKieSessionFromExcel(kieBaseTestConfiguration, xlsxFile, DecisionTableInputType.XLSX);
    }

    private KieSession getKieSessionFromExcel(KieBaseTestConfiguration kieBaseTestConfiguration, final String file, final DecisionTableInputType fileType) {
        final Resource resource = ResourceUtil.getDecisionTableResourceFromClasspath(file, getClass(), fileType);

        return KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, resource).newKieSession();
    }

    private void populateKieSession(final KieSession kieSession, final List<String> list) {
        kieSession.setGlobal("list", list);
        final Person[] persons = prepareData();
        for (final Person p : persons) {
            kieSession.insert(p);
        }
    }

    private Person[] prepareData() {
        final Person bruno = new Person("Bruno", 25);
        bruno.setAddress(new InternationalAddress("Some Street", 10, "Nice City", "Safecountry"));

        final Person robert = new Person("Robert", 17);
        robert.setAddress(new InternationalAddress("Some Street", 12, "Small City", "Riskyland"));

        final Person joe = new Person("Joe", 11);
        joe.setAddress(new InternationalAddress("Some Street", 13, "Big City", "Safecountry"));

        final Person mike = new Person("Mike", 25);
        mike.setAddress(new Address("Some Street", 14, "Local City"));

        return new Person[]{bruno, robert, joe, mike};
    }

    private void verifyQueryResults(final QueryResults results) {
        assertThat(results).isNotEmpty();
        final QueryResultsRow resultsRow = results.iterator().next();
        assertThat(resultsRow.get("$cities")).isInstanceOf(List.class);
        final List<String> cities = (List<String>) resultsRow.get("$cities");
        assertThat(cities).containsExactlyInAnyOrder("Nice City", "Big City");
    }

    private void verifyRuleFireResults(final List<String> list) {
        assertThat(list).containsExactlyInAnyOrder("SafeDriver", "Risky Driver");
    }
}
