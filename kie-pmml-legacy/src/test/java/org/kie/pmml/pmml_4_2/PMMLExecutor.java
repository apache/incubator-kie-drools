/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.pmml_4_2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.util.StringUtils;
import org.drools.ruleunit.executor.InternalRuleUnitExecutor;
import org.kie.api.KieBase;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.pmml.PMML4Data;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;

import static org.drools.core.command.runtime.pmml.PmmlConstants.DEFAULT_ROOT_PACKAGE;

public class PMMLExecutor {
    private KieBase kieBase;
    private String loggingFileName;
    private boolean runWithLogging;

    public PMMLExecutor(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public PMMLExecutor(KieBase kieBase, String loggingFileName) {
        this.kieBase = kieBase;
        this.loggingFileName = loggingFileName;
    }

    public KieBase getKieBase() {
        return kieBase;
    }

    public String getLoggingFileName() {
        return loggingFileName;
    }

    public void setLoggingFileName(String loggingFileName) {
        this.loggingFileName = loggingFileName;
    }

    public boolean isRunWithLogging() {
        return runWithLogging;
    }

    public void setRunWithLogging( boolean runWithLogging ) {
        this.runWithLogging = runWithLogging;
    }

    public PMML4Result run(PMMLRequestData requestData) {
        return run(requestData, Optional.empty());
    }

    public PMML4Result run(PMMLRequestData requestData, PMML4Data pmml4Data) {
        return run(requestData, Optional.of(pmml4Data));
    }

    private PMML4Result run(PMMLRequestData requestData,
            Optional<PMML4Data> data) {
        KieRuntimeLogger logger = null;
        RuleUnitExecutor ruleUnitExecutor = RuleUnitExecutor.create().bind(kieBase);
        if (runWithLogging) {
            if (loggingFileName != null) {
                logger = (( InternalRuleUnitExecutor )ruleUnitExecutor).addFileLogger(loggingFileName);
            } else {
                logger = ((InternalRuleUnitExecutor)ruleUnitExecutor).addConsoleLogger();
            }
        }
        DataSource<PMMLRequestData> requestDataSource = ruleUnitExecutor.newDataSource("request");;
        DataSource<PMML4Result> resultDataSource = ruleUnitExecutor.newDataSource("results");
        DataSource<PMML4Data> pmmlDataSource = ruleUnitExecutor.newDataSource("pmmlData");

        requestDataSource.insert(requestData);
        PMML4Result resultHandler = new PMML4Result();
        resultDataSource.insert(resultHandler);
        if (data.isPresent()) {
            pmmlDataSource.insert(data.get());
        }

        try {
            ruleUnitExecutor.run(startingRuleUnit("RuleUnitIndicator", requestData.getModelName(),
                    "org.drools.scorecards.example"));
        } finally {
            if (logger != null) {
                logger.close();
            }
        }
        return resultHandler;
    }

    private List<String> calculatePossiblePackageNames(String modelId, String...knownPackageNames) {
        List<String> packageNames = new ArrayList<>();
        String javaModelId = modelId.replaceAll("\\s","");
        String capJavaModelId = StringUtils.ucFirst(javaModelId);
        if (knownPackageNames != null && knownPackageNames.length > 0) {
            for (String knownPkgName: knownPackageNames) {
                packageNames.add(knownPkgName + "." + javaModelId);
                if (!javaModelId.equals(capJavaModelId)) {
                    packageNames.add(knownPkgName + "." + capJavaModelId);
                }
            }
        }
        String basePkgName = DEFAULT_ROOT_PACKAGE+"."+javaModelId;
        packageNames.add(basePkgName);
        if (!javaModelId.equals(capJavaModelId)) {
            packageNames.add(DEFAULT_ROOT_PACKAGE+"."+capJavaModelId);
        }
        return packageNames;
    }

    protected Class<? extends RuleUnit> startingRuleUnit( String startingRule, String modelId, String...knownPackageNames) {
        List<String> possiblePackages = calculatePossiblePackageNames(modelId, knownPackageNames);
        InternalKnowledgeBase internalKnowledgeBase = (InternalKnowledgeBase) kieBase;

        Map<String,InternalKnowledgePackage> pkgs = internalKnowledgeBase.getPackagesMap();
        RuleImpl ruleImpl;
        for (String pkgName: possiblePackages) {
            if (pkgs.containsKey(pkgName)) {
                InternalKnowledgePackage pkg = pkgs.get(pkgName);
                ruleImpl = pkg.getRule(startingRule);
                if (ruleImpl != null) {
                    RuleUnitDescription descr = internalKnowledgeBase.getRuleUnitDescriptionRegistry().getDescription(ruleImpl).orElse(null);
                    if (descr != null) {
                        return (Class<? extends RuleUnit>) descr.getRuleUnitClass();
                    }
                }
            }
        }
        return null;
    }
}
