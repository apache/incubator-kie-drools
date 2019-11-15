/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scorecards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.util.StringUtils;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.drools.ruleunit.RuleUnit;

import static org.drools.core.command.runtime.pmml.PmmlConstants.DEFAULT_ROOT_PACKAGE;

public final class TestUtil {

    public static Class<? extends RuleUnit> getStartingRuleUnit( String startingRule, InternalKnowledgeBase ikb, List<String> possiblePackages) {
        Map<String, InternalKnowledgePackage> pkgs = ikb.getPackagesMap();
        RuleImpl ruleImpl;
        for (String pkgName : possiblePackages) {
            if (pkgs.containsKey(pkgName)) {
                InternalKnowledgePackage pkg = pkgs.get(pkgName);
                ruleImpl = pkg.getRule(startingRule);
                if (ruleImpl != null) {
                    RuleUnitDescription descr = ikb.getRuleUnitDescriptionRegistry().getDescription(ruleImpl).orElse(null);
                    if (descr != null) {
                        return (Class<? extends RuleUnit>) descr.getRuleUnitClass();
                    }
                }
            }
        }
        return null;
    }

    public static List<String> calculatePossiblePackageNames(String modelId, String... knownPackageNames) {
        List<String> packageNames = new ArrayList<>();
        String javaModelId = modelId.replaceAll("\\s", "");
        String capJavaModelId = StringUtils.ucFirst(javaModelId);
        if (knownPackageNames != null && knownPackageNames.length > 0) {
            for (String knownPkgName : knownPackageNames) {
                packageNames.add(knownPkgName + "." + javaModelId);
                if (!javaModelId.equals(capJavaModelId)) {
                    packageNames.add(knownPkgName + "." + capJavaModelId);
                }
            }
        }
        String basePkgName = DEFAULT_ROOT_PACKAGE + "." + javaModelId;
        packageNames.add(basePkgName);
        if (!javaModelId.equals(capJavaModelId)) {
            packageNames.add(DEFAULT_ROOT_PACKAGE + "." + capJavaModelId);
        }
        return packageNames;
    }

    private TestUtil() {
        // It is forbidden to instantiate util class.
    }
}
