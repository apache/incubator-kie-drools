/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.util.StringUtils;
import org.drools.ruleunit.executor.InternalRuleUnitExecutor;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.pmml.PMML4Data;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.pmml_4_2.model.AbstractPMMLData;
import org.kie.pmml.pmml_4_2.model.mining.SegmentExecution;
import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;

import static org.drools.core.command.runtime.pmml.PmmlConstants.DEFAULT_ROOT_PACKAGE;

public class PMML4ExecutionHelper {

    private KieBase kbase;
    private String modelName;
    private List<String> possiblePackageNames;
    private RuleUnitExecutor executor;
    private Class<? extends RuleUnit> ruleUnitClass;
    private DataSource<PMMLRequestData> requestData;
    private DataSource<PMML4Result> resultData;
    private DataSource<PMML4Data> pmmlData;
    private DataSource<SegmentExecution> childModelSegments;
    private DataSource<? extends AbstractPMMLData> miningModelPojo;
    private Map<String, DataSource<? extends Object>> externalDataSources;
    private PMML4Result baseResultHolder;
    private boolean includeMiningDataSources;
    private boolean used;
    private String loggerFileName;

    public static class PMML4ExecutionHelperFactory {

        public static PMML4ExecutionHelper getExecutionHelper(String modelName,
                                                              KieBase kbase) {
            return new PMML4ExecutionHelper(modelName, kbase, false);
        }

        public static PMML4ExecutionHelper getExecutionHelper(String modelName,
                                                              KieBase kbase,
                                                              boolean includeMiningDataSources) {
            return new PMML4ExecutionHelper(modelName, kbase, includeMiningDataSources);
        }

        public static PMML4ExecutionHelper getExecutionHelper(String modelName,
                                                              String classPath,
                                                              KieBaseConfiguration kieBaseConf) {
            return new PMML4ExecutionHelper(modelName, classPath, kieBaseConf, false);
        }

        public static PMML4ExecutionHelper getExecutionHelper(String modelName,
                                                              String classPath,
                                                              KieBaseConfiguration kieBaseConf,
                                                              boolean includeMiningDataSources) {
            return new PMML4ExecutionHelper(modelName, classPath, kieBaseConf, includeMiningDataSources);
        }

        public static PMML4ExecutionHelper getExecutionHelper(String modelName,
                                                              byte[] content,
                                                              KieBaseConfiguration kieBaseConf) {
            return new PMML4ExecutionHelper(modelName, content, kieBaseConf, false);
        }

        public static PMML4ExecutionHelper getExecutionHelper(String modelName,
                                                              byte[] content,
                                                              KieBaseConfiguration kieBaseConf,
                                                              boolean includeMiningDataSources) {
            return new PMML4ExecutionHelper(modelName, content, kieBaseConf, includeMiningDataSources);
        }

        public static PMML4ExecutionHelper getExecutionHelper(String modelName,
                                                              Resource resource,
                                                              KieBaseConfiguration kieBaseConf) {
            return new PMML4ExecutionHelper(modelName, resource, kieBaseConf, false);
        }

        public static PMML4ExecutionHelper getExecutionHelper(String modelName,
                                                              Resource resource,
                                                              KieBaseConfiguration kieBaseConf,
                                                              boolean includeMiningDataSources) {
            return new PMML4ExecutionHelper(modelName, resource, kieBaseConf, includeMiningDataSources);
        }

    }

    private PMML4ExecutionHelper(String modelName, KieBase kbase, boolean includeMiningDataSources) {
        this.kbase = kbase;
        initExecutionHelper(modelName, includeMiningDataSources);
    }

    private PMML4ExecutionHelper(String modelName, String classpathName, KieBaseConfiguration kieBaseConf, boolean includeMiningDataSources) {
        kbase = new KieHelper().addFromClassPath(classpathName).build(kieBaseConf);
        initExecutionHelper(modelName, includeMiningDataSources);
    }

    private PMML4ExecutionHelper(String modelName, byte[] content, KieBaseConfiguration kieBaseConf, boolean includeMiningDataSources) {
        kbase = new KieHelper().addContent(new String(content), ResourceType.PMML).build(kieBaseConf);
        initExecutionHelper(modelName, includeMiningDataSources);
    }

    private PMML4ExecutionHelper(String modelName, Resource resource, KieBaseConfiguration kieBaseConf, boolean includeMiningDataSources) {
        kbase = new KieHelper().addResource(resource, ResourceType.PMML).build(kieBaseConf);
        initExecutionHelper(modelName, includeMiningDataSources);
    }

    private void initExecutionHelper(final String modelName, final boolean includeMiningDataSources) {
        this.modelName = modelName;
        this.possiblePackageNames = new ArrayList<>();
        this.includeMiningDataSources = includeMiningDataSources;
        this.externalDataSources = new HashMap<>();
        initRuleUnitExecutor();
    }

    protected synchronized void initRuleUnitExecutor() throws IllegalStateException {
        if (kbase == null) {
            throw new IllegalStateException("Unable to create executor: KieBase is null or invalid");
        }
        if (used) {
            throw new IllegalStateException("Executor cannot be reinitalized if it has been used");
        }
        if (executor != null) {
            executor.dispose();
        }
        executor = RuleUnitExecutor.create().bind(kbase);
        requestData = executor.newDataSource("request");
        resultData = executor.newDataSource("results");
        pmmlData = executor.newDataSource("pmmlData");
        String startingRuleName = "RuleUnitIndicator";
        if (includeMiningDataSources) {
            childModelSegments = executor.newDataSource("childModelSegments");
            miningModelPojo = executor.newDataSource("miningModelPojo");
            startingRuleName = "Start Mining - " + modelName;
        }
        if (externalDataSources != null && !externalDataSources.isEmpty()) {
            Map<String,DataSource<? extends Object>> tmpMap = new HashMap<>();
            for (String key: externalDataSources.keySet()) {
                DataSource<? extends Object> ds = executor.newDataSource(key);
                tmpMap.put(key, ds);
            }
            externalDataSources.clear();
            externalDataSources.putAll(tmpMap);
        }

        ruleUnitClass = getStartingRuleUnit(startingRuleName);
    }

    public RuleUnitExecutor getExecutor() {
        return executor;
    }

    public KieBase getKbase() {
        return kbase;
    }

    public void setKbase(KieBase kbase) {
        this.kbase = kbase;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * Returns a copy of the possible package names
     * NOTE: Do not attempt to use this copy to add package names
     * @return
     */
    protected List<String> getPossiblePackageNames() {
        return new ArrayList<>(possiblePackageNames);
    }

    public boolean addPossiblePackageName(String packageName) {
        boolean retval = possiblePackageNames.add(packageName);
        initRuleUnitExecutor();
        return retval;
    }

    public void setPossiblePackageNames(List<String> possiblePackageNames) {
        this.possiblePackageNames = possiblePackageNames;
        initRuleUnitExecutor();
    }

    public DataSource<PMMLRequestData> getRequestData() {
        return requestData;
    }

    public DataSource<PMML4Result> getResultData() {
        return resultData;
    }

    public DataSource<PMML4Data> getPmmlData() {
        return pmmlData;
    }

    public DataSource<SegmentExecution> getChildModelSegments() {
        return childModelSegments;
    }

    public DataSource<? extends AbstractPMMLData> getMiningModelPojo() {
        return miningModelPojo;
    }

    public PMML4Result getBaseResultHolder() {
        return baseResultHolder;
    }

    public boolean isIncludeMiningDataSources() {
        return includeMiningDataSources;
    }

    public void setIncludeMiningDataSources(boolean includeMiningDataSources) {
        this.includeMiningDataSources = includeMiningDataSources;
        initRuleUnitExecutor();
    }

    public synchronized void initModel() {
        if (ruleUnitClass == null) {
            throw new IllegalStateException("PMML model cannot be initialized. Missing ruleUnitClass.");
        }
        if (used) {
            used = false;
            initRuleUnitExecutor();
        }
        executor.run(ruleUnitClass);
    }

    public synchronized PMML4Result submitRequest(PMMLRequestData request, Map<String,List<Object>> externalData) 
           throws InvalidParameterException, IllegalStateException {
        if (request == null) {
            throw new InvalidParameterException("PMML model cannot be applied to a null request");
        }
        if (ruleUnitClass == null) {
            throw new IllegalStateException("PMML model cannot be applied. Missing ruleUnitClass.");
        }
        /*
         * If the executor was previously used then we need to re-initialize it
         */
        if (used) {
            used = false;
            initRuleUnitExecutor();
        }

        KieRuntimeLogger logger = loggerFileName != null ?
                (( InternalRuleUnitExecutor )executor).addFileLogger(loggerFileName) : null;
        try {
            requestData.insert(request);
            baseResultHolder = new PMML4Result(request.getCorrelationId());
            resultData.insert(baseResultHolder);
            if (externalData != null && !externalData.isEmpty()) {
                externalData.entrySet().forEach(entry -> {
                    DataSource ds = externalDataSources.get(entry.getKey());
                    if (ds != null) {
                        entry.getValue().forEach(value -> { ds.insert(value); });
                    }
                });
            }
            executor.run(ruleUnitClass);
        } finally {
            if (logger != null) {
                logger.close();
            }
        }
        used = true;
        return baseResultHolder;

    }

    /**
     * Submits a request to the rule unit executor and the model gets applied
     * NOTE: The results of previous submissions will be overwritten
     * @param request
     * @return
     * @throws InvalidParameterException
     * @throws IllegalStateException
     */
    public synchronized PMML4Result submitRequest(PMMLRequestData request)
            throws InvalidParameterException, IllegalStateException {
        return submitRequest(request,null);
    }

    protected Class<? extends RuleUnit> getStartingRuleUnit(String startingRule) throws IllegalStateException {
        if (kbase == null) {
            throw new IllegalStateException("Cannot determine starting rule unit. KieBase is null");
        }
        InternalKnowledgeBase ikb = (InternalKnowledgeBase) kbase;
        Map<String, InternalKnowledgePackage> pkgs = ikb.getPackagesMap();
        RuleImpl ruleImpl;
        for (String pkgName : calculatePossiblePackageNames()) {
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

    protected List<String> calculatePossiblePackageNames() {
        return calculatePossiblePackageNames(modelName, possiblePackageNames.toArray(new String[]{}));
    }

    protected List<String> calculatePossiblePackageNames(String modelId, String... knownPackageNames) {
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
        packageNames.add(DEFAULT_ROOT_PACKAGE + "." + javaModelId);
        if (!javaModelId.equals(capJavaModelId)) {
            packageNames.add(DEFAULT_ROOT_PACKAGE + "." + capJavaModelId);
        }
        return packageNames;
    }

    public void turnOnFileLogger(String loggerFileName) {
        this.loggerFileName = loggerFileName;
    }

    public void turnOffFileLogger() {
        this.loggerFileName = null;
    }

    public synchronized void addExternalDataSource(String dataSourceName) {
        if (!externalDataSources.containsKey(dataSourceName)) {
            externalDataSources.put(dataSourceName, null);
        }
    }
}
