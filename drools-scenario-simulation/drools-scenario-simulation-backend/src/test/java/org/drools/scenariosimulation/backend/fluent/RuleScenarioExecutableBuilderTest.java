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
package org.drools.scenariosimulation.backend.fluent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleScenarioExecutableBuilderTest {

    @Test
    public void createBuilder() {
        RuleScenarioExecutableBuilder statelessBuilder = RuleScenarioExecutableBuilder.createBuilder(null, null, true);
        assertThat(statelessBuilder).isInstanceOf(RuleStatelessScenarioExecutableBuilder.class);;

        RuleScenarioExecutableBuilder statefulBuilder1 = RuleScenarioExecutableBuilder.createBuilder(null, null, false);
        assertThat(statefulBuilder1).isInstanceOf(RuleStatefulScenarioExecutableBuilder.class);;

        RuleScenarioExecutableBuilder statefulBuilder2 = RuleScenarioExecutableBuilder.createBuilder(null);
        assertThat(statefulBuilder2).isInstanceOf(RuleStatefulScenarioExecutableBuilder.class);;
    }

    @Test
    public void getAvailableRules() {
        Map<String, List<String>> packagesToRules = Map.of("package1", List.of("rule1", "rule2", "rule3"), "package2", List.of("rule4", "rule5", "rule6"));
        Map<String, List<String>> queryToRules = Map.of("package1", List.of("query1", "query2"), "package2", List.of());
        Map<String, String> ruleToAgendaGroup = Map.of("rule1", "agenda1", "rule2", "agenda1", "rule3", "agenda1", "rule4", "agenda2");

        RuleScenarioExecutableBuilder builder = RuleScenarioExecutableBuilder.createBuilder(null, null, false);

        Set<String> agenda1 = builder.getAvailableRules(createKieBaseMock(packagesToRules, ruleToAgendaGroup, queryToRules), "agenda1");
        assertThat(agenda1).hasSize(5);

        Set<String> agenda2 = builder.getAvailableRules(createKieBaseMock(packagesToRules, ruleToAgendaGroup, queryToRules), "agenda2");
        assertThat(agenda2).hasSize(3);

        Set<String> noAgenda = builder.getAvailableRules(createKieBaseMock(packagesToRules, ruleToAgendaGroup, queryToRules), null);
        assertThat(noAgenda).hasSize(2);
    }

    private KieBase createKieBaseMock(Map<String, List<String>> packagesToRules, Map<String, String> ruleToAgendaGroup, Map<String, List<String>> packagesToQueries) {
        KieBase kieBaseMock = mock(KieBase.class);
        List<KiePackage> kiePackagesMock = new ArrayList<>();
        when(kieBaseMock.getKiePackages()).thenReturn(kiePackagesMock);
        for (Map.Entry<String, List<String>> packageToRule : packagesToRules.entrySet()) {
            kiePackagesMock.add(createKiePackageMock(packageToRule.getKey(), packageToRule.getValue(), ruleToAgendaGroup, packagesToQueries.get(packageToRule.getKey())));
        }
        return kieBaseMock;
    }

    private KiePackage createKiePackageMock(String packageName, List<String> ruleNames, Map<String, String> ruleToAgendaGroup, List<String> queries) {
        KiePackage kiePackageMock = mock(KiePackage.class);
        when(kiePackageMock.getName()).thenReturn(packageName);
        List<Rule> ruleListMock = new ArrayList<>();
        when(kiePackageMock.getRules()).thenReturn(ruleListMock);
        for (String ruleName : ruleNames) {
            ruleListMock.add(createRuleMock(ruleName, ruleToAgendaGroup.get(ruleName)));
        }
        for (String queryName : queries) {
            ruleListMock.add(createQueryImplMock(queryName));
        }
        return kiePackageMock;
    }

    private RuleImpl createRuleMock(String fullName, String agendaGroup) {
        RuleImpl ruleMock = mock(RuleImpl.class);
        when(ruleMock.getName()).thenReturn(fullName);
        when(ruleMock.getPackageName()).thenReturn("");
        when(ruleMock.isMainAgendaGroup()).thenReturn(agendaGroup == null);
        when(ruleMock.getAgendaGroup()).thenReturn(agendaGroup);
        when(ruleMock.getKnowledgeType()).thenCallRealMethod();
        return ruleMock;
    }

    private QueryImpl createQueryImplMock(String fullName) {
        QueryImpl queryImplMock = mock(QueryImpl.class);
        when(queryImplMock.getName()).thenReturn(fullName);
        when(queryImplMock.getPackageName()).thenReturn("");
        when(queryImplMock.getKnowledgeType()).thenCallRealMethod();
        return queryImplMock;
    }
}