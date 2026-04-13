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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.graphql.dto.PackageInfo;
import org.drools.graphql.dto.RuleInfo;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;

/**
 * Service that extracts rule definitions and package metadata from a {@link KieBase}.
 * Framework-agnostic -- usable from GraphQL resolvers, REST endpoints, or plain Java.
 */
public class RuleMetadataService {

    private final KieBase kieBase;

    public RuleMetadataService(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public List<PackageInfo> getAllPackages() {
        Collection<KiePackage> packages = kieBase.getKiePackages();
        if (packages == null) {
            return Collections.emptyList();
        }
        return packages.stream()
                .map(PackageInfo::from)
                .collect(Collectors.toList());
    }

    public PackageInfo getPackage(String packageName) {
        KiePackage pkg = kieBase.getKiePackage(packageName);
        return pkg != null ? PackageInfo.from(pkg) : null;
    }

    public RuleInfo getRule(String packageName, String ruleName) {
        Rule rule = kieBase.getRule(packageName, ruleName);
        return rule != null ? RuleInfo.from(rule) : null;
    }

    public List<RuleInfo> getAllRules() {
        return kieBase.getKiePackages().stream()
                .flatMap(pkg -> pkg.getRules().stream())
                .map(RuleInfo::from)
                .collect(Collectors.toList());
    }

    public List<RuleInfo> getRulesByPackage(String packageName) {
        KiePackage pkg = kieBase.getKiePackage(packageName);
        if (pkg == null) {
            return Collections.emptyList();
        }
        return pkg.getRules().stream()
                .map(RuleInfo::from)
                .collect(Collectors.toList());
    }

    public List<RuleInfo> searchRules(String namePattern) {
        String lowerPattern = namePattern.toLowerCase();
        return kieBase.getKiePackages().stream()
                .flatMap(pkg -> pkg.getRules().stream())
                .filter(rule -> rule.getName().toLowerCase().contains(lowerPattern))
                .map(RuleInfo::from)
                .collect(Collectors.toList());
    }

    public long getTotalRuleCount() {
        return kieBase.getKiePackages().stream()
                .mapToLong(pkg -> pkg.getRules().size())
                .sum();
    }
}
