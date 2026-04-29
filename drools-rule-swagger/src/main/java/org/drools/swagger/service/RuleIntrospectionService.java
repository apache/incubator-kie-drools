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
package org.drools.swagger.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.swagger.model.FactTypeInfo;
import org.drools.swagger.model.FieldInfo;
import org.drools.swagger.model.GlobalInfo;
import org.drools.swagger.model.PackageInfo;
import org.drools.swagger.model.RuleInfo;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Global;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.internal.definition.rule.InternalRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleIntrospectionService {

    private static final Logger LOG = LoggerFactory.getLogger(RuleIntrospectionService.class);

    private final KieBase kieBase;

    public RuleIntrospectionService(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public List<PackageInfo> getAllPackages() {
        return kieBase.getKiePackages().stream()
                .map(this::toPackageInfo)
                .collect(Collectors.toList());
    }

    public PackageInfo getPackage(String packageName) {
        KiePackage kiePackage = kieBase.getKiePackage(packageName);
        if (kiePackage == null) {
            return null;
        }
        return toPackageInfo(kiePackage);
    }

    public List<RuleInfo> getRulesInPackage(String packageName) {
        KiePackage kiePackage = kieBase.getKiePackage(packageName);
        if (kiePackage == null) {
            return List.of();
        }
        return kiePackage.getRules().stream()
                .map(this::toRuleInfo)
                .collect(Collectors.toList());
    }

    public RuleInfo getRule(String packageName, String ruleName) {
        Rule rule = kieBase.getRule(packageName, ruleName);
        if (rule == null) {
            return null;
        }
        return toRuleInfo(rule);
    }

    public List<FactTypeInfo> getFactTypesInPackage(String packageName) {
        KiePackage kiePackage = kieBase.getKiePackage(packageName);
        if (kiePackage == null) {
            return List.of();
        }
        return kiePackage.getFactTypes().stream()
                .map(this::toFactTypeInfo)
                .collect(Collectors.toList());
    }

    public FactTypeInfo getFactType(String packageName, String typeName) {
        FactType factType = kieBase.getFactType(packageName, typeName);
        if (factType == null) {
            return null;
        }
        return toFactTypeInfo(factType);
    }

    public Map<String, Object> getSummary() {
        Collection<KiePackage> packages = kieBase.getKiePackages();
        int totalRules = packages.stream()
                .mapToInt(p -> p.getRules().size())
                .sum();
        int totalFactTypes = packages.stream()
                .mapToInt(p -> p.getFactTypes().size())
                .sum();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalPackages", packages.size());
        summary.put("totalRules", totalRules);
        summary.put("totalFactTypes", totalFactTypes);
        summary.put("packageNames", packages.stream()
                .map(KiePackage::getName)
                .collect(Collectors.toList()));
        return summary;
    }

    private PackageInfo toPackageInfo(KiePackage kiePackage) {
        PackageInfo info = new PackageInfo();
        info.setName(kiePackage.getName());
        info.setRules(kiePackage.getRules().stream()
                .map(this::toRuleInfo)
                .collect(Collectors.toList()));
        info.setFactTypes(kiePackage.getFactTypes().stream()
                .map(this::toFactTypeInfo)
                .collect(Collectors.toList()));
        info.setFunctionNames(new ArrayList<>(kiePackage.getFunctionNames()));
        info.setGlobals(kiePackage.getGlobalVariables().stream()
                .map(this::toGlobalInfo)
                .collect(Collectors.toList()));
        return info;
    }

    private RuleInfo toRuleInfo(Rule rule) {
        RuleInfo info = new RuleInfo();
        info.setName(rule.getName());
        info.setPackageName(rule.getPackageName());
        info.setMetadata(rule.getMetaData());
        info.setLoadOrder(rule.getLoadOrder());

        if (rule instanceof InternalRule internalRule) {
            info.setFullyQualifiedName(internalRule.getFullyQualifiedName());
            info.setSalience(internalRule.getSalienceValue());
            info.setAgendaGroup(internalRule.getAgendaGroup());
            info.setActivationGroup(internalRule.getActivationGroup());
            info.setRuleFlowGroup(internalRule.getRuleFlowGroup());
            info.setNoLoop(internalRule.isNoLoop());
            info.setLockOnActive(internalRule.isLockOnActive());
        } else {
            info.setFullyQualifiedName(rule.getPackageName() + "." + rule.getName());
        }

        return info;
    }

    private FactTypeInfo toFactTypeInfo(FactType factType) {
        FactTypeInfo info = new FactTypeInfo();
        info.setName(factType.getName());
        info.setSimpleName(factType.getSimpleName());
        info.setPackageName(factType.getPackageName());
        info.setSuperClass(factType.getSuperClass());
        info.setMetadata(factType.getMetaData());

        List<FieldInfo> fields = new ArrayList<>();
        if (factType.getFields() != null) {
            for (FactField ff : factType.getFields()) {
                fields.add(toFieldInfo(ff));
            }
        }
        info.setFields(fields);
        info.setSampleJson(generateSampleJson(factType));
        return info;
    }

    private FieldInfo toFieldInfo(FactField factField) {
        return new FieldInfo(
                factField.getName(),
                factField.getType() != null ? factField.getType().getSimpleName() : "Object",
                factField.isKey(),
                factField.getIndex(),
                factField.getMetaData()
        );
    }

    private GlobalInfo toGlobalInfo(Global global) {
        return new GlobalInfo(global.getName(), global.getType());
    }

    private Map<String, Object> generateSampleJson(FactType factType) {
        Map<String, Object> sample = new LinkedHashMap<>();
        if (factType.getFields() == null) {
            return sample;
        }
        for (FactField field : factType.getFields()) {
            sample.put(field.getName(), getDefaultValueForType(field.getType()));
        }
        return sample;
    }

    private Object getDefaultValueForType(Class<?> type) {
        if (type == null) {
            return null;
        }
        String name = type.getSimpleName();
        return switch (name) {
            case "String" -> "";
            case "int", "Integer" -> 0;
            case "long", "Long" -> 0L;
            case "double", "Double" -> 0.0;
            case "float", "Float" -> 0.0f;
            case "boolean", "Boolean" -> false;
            case "short", "Short" -> (short) 0;
            case "byte", "Byte" -> (byte) 0;
            case "char", "Character" -> "";
            case "BigDecimal", "BigInteger" -> 0;
            default -> new LinkedHashMap<>();
        };
    }
}
