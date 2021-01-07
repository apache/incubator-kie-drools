/*
 * Copyright 2005 JBoss Inc
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

package org.drools.ruleunit.command.pmml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.core.command.runtime.pmml.PmmlConstants;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.ruleunit.RuleUnitDescriptionRegistry;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.util.StringUtils;
import org.kie.api.KieBase;
import org.kie.api.pmml.OutputFieldFactory;
import org.kie.api.pmml.PMML4Output;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.ruleunit.ApplyPmmlModelCommandExecutor;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;

public class ApplyPmmlModelCommandExecutorImpl implements ApplyPmmlModelCommandExecutor {

    private Class<? extends RuleUnit> getStartingRuleUnit( String startingRule, InternalKnowledgeBase ikb, List<String> possiblePackages) {
        RuleUnitDescriptionRegistry unitRegistry = ikb.getRuleUnitDescriptionRegistry();
        Map<String, InternalKnowledgePackage> pkgs = ikb.getPackagesMap();
        RuleImpl ruleImpl = null;
        for (String pkgName: possiblePackages) {
            if (pkgs.containsKey(pkgName)) {
                InternalKnowledgePackage pkg = pkgs.get(pkgName);
                ruleImpl = pkg.getRule(startingRule);
                if (ruleImpl != null) {
                    RuleUnitDescription descr = unitRegistry.getDescription(ruleImpl).orElse(null);
                    if (descr != null) {
                        return (Class<? extends RuleUnit>) descr.getRuleUnitClass();
                    }
                }
            }
        }
        return null;
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
        String basePkgName = PmmlConstants.DEFAULT_ROOT_PACKAGE+"."+javaModelId;
        packageNames.add(basePkgName);
        if (!javaModelId.equals(capJavaModelId)) {
            packageNames.add(PmmlConstants.DEFAULT_ROOT_PACKAGE + "." + capJavaModelId);
        }
        return packageNames;
    }

    private <T> T castObject(Object o, Class<T> clazz) {
        T result = null;
        if (o != null && clazz != null) {
            result = clazz.cast(o);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> DataSource<T> createDataSource( RuleUnitExecutor executor, String dsName, Object o) {
        T object = (T) castObject(o, o.getClass());
        return executor.newDataSource(dsName, object);
    }

    @SuppressWarnings("unchecked")
    private <T> void insertDataObject(DataSource<T> ds, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot insert null object into a DataSource");
        }
        T obj = null;
        try {
            obj = (T) object;
        } catch (ClassCastException ccx) {
            throw new IllegalArgumentException("Invalid attempt to insert a " + object.getClass().getName() +
                    " object into a DataSource");
        }
        ds.insert((T) object);
    }

    private KieBase lookupKieBase( RegistryContext ctx) {
        if (ctx == null) {
            return null;
        }
        KieBase kbase = ctx.lookup(KieBase.class);

        if (kbase == null) {
            KieSession session = ctx.lookup(KieSession.class);
            if (session != null) {
                kbase = session.getKieBase();
            }
        }
        return kbase;
    }

    @Override
    public PMML4Result execute( Context context, PMMLRequestData requestData, List<Object> complexInputObjects, String packageName, boolean isMining ) {
        if (isjPMMLAvailableToClassLoader(getClass().getClassLoader())) {
            throw new IllegalStateException("Availability of jPMML module disables ApplyPmmlModelCommand execution. ApplyPmmlModelCommand requires removal of JPMML libs from classpath");
        }
        if (requestData == null) {
            throw new IllegalStateException("ApplyPmmlModelCommandExecutorImpl requires request data (PMMLRequestData) to execute");
        }
        PMML4Result resultHolder = new PMML4Result(requestData.getCorrelationId());
        RegistryContext ctx = (RegistryContext) context;
        if (packageName == null) {
            packageName = (String)ctx.get("packageName");
        }

        KieBase kbase = lookupKieBase(ctx);
        if (kbase == null) {
            resultHolder.setResultCode("ERROR-1");
        } else {
            boolean hasUnits = ((InternalKnowledgeBase)kbase).getRuleUnitDescriptionRegistry().hasUnits();
            if (!hasUnits) {
                resultHolder.setResultCode("ERROR-2");
            } else {
                RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
                DataSource<PMMLRequestData> data = executor.newDataSource("request", requestData);
                DataSource<PMML4Result> resultData = executor.newDataSource("results", resultHolder);
                if (complexInputObjects != null && !complexInputObjects.isEmpty()) {
                    Map<String, DataSource<?>> datasources = new HashMap<>();
                    for (Object obj : complexInputObjects) {
                        String dsName = "externalBean" + obj.getClass().getSimpleName();
                        if (datasources.containsKey(dsName)) {
                            insertDataObject(datasources.get(dsName), obj);
                        } else {
                            datasources.put(dsName, createDataSource(executor, dsName, obj));
                        }
                    }
                }
                executor.newDataSource("pmmlData");
                if (isMining) {
                    executor.newDataSource("childModelSegments");
                    executor.newDataSource("miningModelPojo");
                }
                // Attempt to fix type issues when the unmarshaller
                // doesn't set the parameter's
                // value to an object of the correct type
                Collection<ParameterInfo> parms = requestData.getRequestParams();
                for (ParameterInfo pi : parms) {
                    Class<?> clazz = pi.getType();
                    if (!clazz.isAssignableFrom(pi.getValue().getClass())) {
                        try {
                            Object o = clazz.getDeclaredConstructor(pi.getValue().getClass()).newInstance(pi.getValue());
                            pi.setValue(o);
                        } catch (Throwable t) {
                            resultHolder.setResultCode("ERROR-3");
                            return resultHolder;
                        }
                    }
                }
                data.insert(requestData);
                resultData.insert(resultHolder);
                String startingRule = isMining ? "Start Mining - "+requestData.getModelName():"RuleUnitIndicator";
                List<String> possibleStartingPackages = calculatePossiblePackageNames(requestData.getModelName(), packageName);
                Class<? extends RuleUnit> ruleUnitClass= getStartingRuleUnit(startingRule, (InternalKnowledgeBase)kbase, possibleStartingPackages);
                executor.run(ruleUnitClass);
            }
        }
        List<PMML4Output<?>> outputs = OutputFieldFactory.createOutputsFromResults(resultHolder);
        Optional<ExecutionResultImpl> execRes = Optional.ofNullable(ctx.lookup(ExecutionResultImpl.class));
        ctx.register(PMML4Result.class, resultHolder);
        execRes.ifPresent(result -> {
            result.setResult("results", resultHolder);
        });
        outputs.forEach(out -> {
            execRes.ifPresent(result -> {
                result.setResult(out.getName(), out);
            });
            resultHolder.updateResultVariable(out.getName(), out);
        });

        return resultHolder;
    }

    protected boolean isjPMMLAvailableToClassLoader(ClassLoader classLoader) {
        return org.kie.internal.pmml.PMMLImplementationsUtil.isjPMMLAvailableToClassLoader(classLoader);
    }
}
