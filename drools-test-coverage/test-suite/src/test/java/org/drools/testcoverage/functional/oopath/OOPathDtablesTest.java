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

package org.drools.testcoverage.functional.oopath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.InternationalAddress;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.ResourceUtil;
import org.junit.Test;
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

    @Test
    public void xlsWithOOPathTest() {
        KieSession kieSession = getKieSessionFromXls("oopath.xls");
        List<String> list = new ArrayList<>();
        populateKieSession(kieSession, list);

        assertThat(kieSession.fireAllRules()).isEqualTo(2);
        verifyRuleFireResults(list);
        verifyQueryResults(kieSession.getQueryResults("listSafeCities"));
    }

    @Test
    public void xlsxWithOOPathTest() {
        KieSession kieSession = getKieSessionFromXlsx("oopath.xlsx");
        List<String> list = new ArrayList<>();
        populateKieSession(kieSession, list);

        assertThat(kieSession.fireAllRules()).isEqualTo(2);
        verifyRuleFireResults(list);
        verifyQueryResults(kieSession.getQueryResults("listSafeCities"));
    }

    @Test
    public void csvWithOOPathTest() {
        KieSession kieSession = getKieSessionFromCsv("oopath.csv");
        List<String> list = new ArrayList<>();
        populateKieSession(kieSession, list);

        assertThat(kieSession.fireAllRules()).isEqualTo(2);
        verifyRuleFireResults(list);
        verifyQueryResults(kieSession.getQueryResults("listSafeCities"));
    }

    private KieSession getKieSessionFromCsv(String csvFile) {
        final Resource resource =
                ResourceUtil.getDecisionTableResourceFromClasspath(csvFile, getClass(), DecisionTableInputType.CSV);

        return KieBaseUtil.getKieBaseFromResources(true, resource).newKieSession();
    }

    private KieSession getKieSessionFromXls(String xlsFile) {
        return getKieSessionFromExcel(xlsFile, DecisionTableInputType.XLS);
    }

    private KieSession getKieSessionFromXlsx(String xlsxFile) {
        return getKieSessionFromExcel(xlsxFile, DecisionTableInputType.XLSX);
    }

    private KieSession getKieSessionFromExcel(String file, DecisionTableInputType fileType) {
        final Resource resource = ResourceUtil.getDecisionTableResourceFromClasspath(file, getClass(), fileType);

        return KieBaseUtil.getKieBaseFromResources(true, resource).newKieSession();
    }

    private void populateKieSession(KieSession kieSession, List<String> list) {
        kieSession.setGlobal("list", list);
        Person[] persons = prepareData();
        for (Person p : persons) {
            kieSession.insert(p);
        }
    }

    private Person[] prepareData() {
        Person[] persons = new Person[4];
        persons[0] = new Person("Bruno", 25);
        persons[0].setAddress(new InternationalAddress("Some Street", 10, "Nice City", "Safecountry"));

        persons[1] = new Person("Robert", 17);
        persons[1].setAddress(new InternationalAddress("Some Street", 12, "Small City", "Riskyland"));

        persons[2] = new Person("Joe", 11);
        persons[2].setAddress(new InternationalAddress("Some Street", 13, "Big City", "Safecountry"));

        persons[3] = new Person("Mike", 25);
        persons[3].setAddress(new Address("Some Street", 14, "Local City"));

        return persons;
    }

    private void verifyQueryResults(QueryResults results) {
        assertThat(results).isNotEmpty();
        final QueryResultsRow resultsRow = results.iterator().next();
        assertThat(resultsRow.get("$cities")).isInstanceOf(List.class);
        final List<String> cities = (List<String>) resultsRow.get("$cities");
        assertThat(cities).hasSize(2);
        assertThat(cities).hasOnlyElementsOfType(String.class);
        assertThat(cities).containsAll(Arrays.asList("Nice City", "Big City"));
        assertThat(cities).doesNotContainAnyElementsOf(Arrays.asList("Small City", "Local City"));
    }

    private void verifyRuleFireResults(List<String> list) {
        assertThat(list.size()).isEqualTo(2);
        assertThat(list).containsOnly("SafeDriver", "Risky Driver");
        assertThat(list).containsAll(Arrays.asList("SafeDriver", "Risky Driver"));
    }
}
