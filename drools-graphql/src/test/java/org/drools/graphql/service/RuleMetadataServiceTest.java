/*
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
package org.drools.graphql.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.graphql.dto.PackageInfo;
import org.drools.graphql.dto.RuleInfo;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleMetadataServiceTest {

    private KieBase kieBase;
    private RuleMetadataService service;

    @BeforeEach
    void setUp() {
        kieBase = mock(KieBase.class);

        Rule rule1 = mockRule("Rule A", "com.example", 1, Map.of("author", "test"));
        Rule rule2 = mockRule("Rule B", "com.example", 2, Collections.emptyMap());
        Rule rule3 = mockRule("Fraud Check", "com.other", 1, Map.of("priority", "high"));

        KiePackage pkg1 = mockPackage("com.example", Arrays.asList(rule1, rule2));
        KiePackage pkg2 = mockPackage("com.other", Collections.singletonList(rule3));

        when(kieBase.getKiePackages()).thenReturn(Arrays.asList(pkg1, pkg2));
        when(kieBase.getKiePackage("com.example")).thenReturn(pkg1);
        when(kieBase.getKiePackage("com.other")).thenReturn(pkg2);
        when(kieBase.getKiePackage("nonexistent")).thenReturn(null);
        when(kieBase.getRule("com.example", "Rule A")).thenReturn(rule1);
        when(kieBase.getRule("com.example", "Missing")).thenReturn(null);

        service = new RuleMetadataService(kieBase);
    }

    @Test
    void shouldReturnAllPackages() {
        List<PackageInfo> packages = service.getAllPackages();
        assertThat(packages).hasSize(2);
        assertThat(packages).extracting(PackageInfo::getName)
                .containsExactlyInAnyOrder("com.example", "com.other");
    }

    @Test
    void shouldReturnPackageByName() {
        PackageInfo pkg = service.getPackage("com.example");
        assertThat(pkg).isNotNull();
        assertThat(pkg.getName()).isEqualTo("com.example");
        assertThat(pkg.getRules()).hasSize(2);
    }

    @Test
    void shouldReturnNullForMissingPackage() {
        assertThat(service.getPackage("nonexistent")).isNull();
    }

    @Test
    void shouldReturnRuleByName() {
        RuleInfo rule = service.getRule("com.example", "Rule A");
        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("Rule A");
        assertThat(rule.getPackageName()).isEqualTo("com.example");
        assertThat(rule.getLoadOrder()).isEqualTo(1);
        assertThat(rule.getMetadata()).hasSize(1);
        assertThat(rule.getMetadata().get(0).getKey()).isEqualTo("author");
    }

    @Test
    void shouldReturnNullForMissingRule() {
        assertThat(service.getRule("com.example", "Missing")).isNull();
    }

    @Test
    void shouldReturnAllRules() {
        List<RuleInfo> rules = service.getAllRules();
        assertThat(rules).hasSize(3);
    }

    @Test
    void shouldReturnRulesByPackage() {
        List<RuleInfo> rules = service.getRulesByPackage("com.example");
        assertThat(rules).hasSize(2);
        assertThat(rules).extracting(RuleInfo::getName)
                .containsExactlyInAnyOrder("Rule A", "Rule B");
    }

    @Test
    void shouldReturnEmptyForMissingPackageRules() {
        assertThat(service.getRulesByPackage("nonexistent")).isEmpty();
    }

    @Test
    void shouldSearchRulesByName() {
        List<RuleInfo> results = service.searchRules("fraud");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Fraud Check");
    }

    @Test
    void shouldReturnTotalRuleCount() {
        assertThat(service.getTotalRuleCount()).isEqualTo(3);
    }

    @SuppressWarnings("unchecked")
    private static Rule mockRule(String name, String pkg, int loadOrder, Map<String, Object> meta) {
        Rule rule = mock(Rule.class);
        when(rule.getName()).thenReturn(name);
        when(rule.getPackageName()).thenReturn(pkg);
        when(rule.getLoadOrder()).thenReturn(loadOrder);
        when(rule.getMetaData()).thenReturn(meta);
        return rule;
    }

    @SuppressWarnings("unchecked")
    private static KiePackage mockPackage(String name, List<Rule> rules) {
        KiePackage pkg = mock(KiePackage.class);
        when(pkg.getName()).thenReturn(name);
        when(pkg.getRules()).thenReturn((Collection) rules);
        when(pkg.getQueries()).thenReturn(Collections.emptyList());
        when(pkg.getFunctionNames()).thenReturn(Collections.emptyList());
        when(pkg.getFactTypes()).thenReturn(Collections.emptyList());
        when(pkg.getGlobalVariables()).thenReturn(Collections.emptyList());
        return pkg;
    }
}
