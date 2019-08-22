/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.command.runtime.pmml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.ruleunit.RuleUnitDescription;
import org.drools.core.ruleunit.RuleUnitDescriptionRegistry;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.KieBase;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.pmml.OutputFieldFactory;
import org.kie.api.pmml.PMML4Output;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.command.RegistryContext;

@XmlRootElement(name="apply-pmml-model-command")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplyPmmlModelCommand implements ExecutableCommand<PMML4Result>, IdentifiableResult {
    private static final long serialVersionUID = 19630331;
    @XmlAttribute(name="outIdentifier")
    private String outIdentifier;
    @XmlAttribute(name="packageName")
    private String packageName;
    @XmlAttribute(name="hasMining")
    private Boolean hasMining;
    @XmlElement(name="requestData")
    private PMMLRequestData requestData;
    @XmlElements(
        @XmlElement(name = "complexInputObject", type = Object.class)
    )
    private List<Object> complexInputObjects;

    
    public ApplyPmmlModelCommand() {
        // Necessary for JAXB
        super();
    }
    
    public ApplyPmmlModelCommand(PMMLRequestData requestData) {
        initialize(requestData, null, null);
    }

    public ApplyPmmlModelCommand(PMMLRequestData requestData, List<Object> complexInputList) {
        initialize(requestData, complexInputList, null);
    }
    
    public ApplyPmmlModelCommand(PMMLRequestData requestData, List<Object> complexInputList, Boolean hasMining) {
        initialize(requestData, complexInputList, hasMining);
    }

    private void initialize(PMMLRequestData requestData, List<Object> complexInputList, Boolean hasMining) {
        this.requestData = requestData;
        this.complexInputObjects = complexInputList != null ? new ArrayList(complexInputList) : new ArrayList<>();
        this.hasMining = hasMining != null ? hasMining : Boolean.FALSE;
    }
    
    public PMMLRequestData getRequestData() {
        return requestData;
    }

    public void setRequestData(PMMLRequestData requestData) {
        this.requestData = requestData;
    }
    
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public Boolean getHasMining() {
        return hasMining;
    }

    public void setHasMining(Boolean hasMining) {
        this.hasMining = hasMining;
    }
    
    public boolean isMining() {
        if (hasMining == null || hasMining.booleanValue() == false) return false;
        return true;
    }

    public void addComplexInputObject(Object o) {
        if (o != null) {
            this.complexInputObjects.add(o);
        }
    }

    @Override
    public String getOutIdentifier() {
        return outIdentifier;
    }

    @Override
    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }
    
    private Class<? extends RuleUnit> getStartingRuleUnit(String startingRule, InternalKnowledgeBase ikb, List<String> possiblePackages) {
        RuleUnitDescriptionRegistry unitRegistry = ikb.getRuleUnitDescriptionRegistry();
        Map<String,InternalKnowledgePackage> pkgs = ikb.getPackagesMap();
        RuleImpl ruleImpl = null;
        for (String pkgName: possiblePackages) {
            if (pkgs.containsKey(pkgName)) {
                InternalKnowledgePackage pkg = pkgs.get(pkgName);
                ruleImpl = pkg.getRule(startingRule);
                if (ruleImpl != null) {
                    RuleUnitDescription descr = unitRegistry.getDescription(ruleImpl).orElse(null);
                    if (descr != null) {
                        return descr.getRuleUnitClass();
                    }
                }
            }
        }
        return null;
    }
    
    private List<String> calculatePossiblePackageNames(String modelId, String...knownPackageNames) {
        List<String> packageNames = new ArrayList<>();
        String javaModelId = modelId.replaceAll("\\s","");
        if (knownPackageNames != null && knownPackageNames.length > 0) {
            for (String knownPkgName: knownPackageNames) {
                packageNames.add(knownPkgName + "." + javaModelId);
            }
        }
        String basePkgName = PmmlConstants.DEFAULT_ROOT_PACKAGE+"."+javaModelId;
        packageNames.add(basePkgName);
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
    private <T> DataSource<T> createDataSource(RuleUnitExecutor executor, String dsName, Object o) {
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
    
    private KieBase lookupKieBase(RegistryContext ctx) {
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
    public PMML4Result execute(Context context) {
        if (isjPMMLAvailableToClassLoader(getClass().getClassLoader())) {
            throw new IllegalStateException("Availability of jPMML module disables ApplyPmmlModelCommand execution. ApplyPmmlModelCommand requires removal of JPMML libs from classpath");
        }
        if (requestData == null) {
            throw new IllegalStateException("ApplyPmmlModelCommand requires request data (PMMLRequestData) to execute");
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
                DataSource<PMMLRequestData> data = executor.newDataSource("request", this.requestData);
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
                if (isMining()) {
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
                String startingRule = isMining() ? "Start Mining - "+requestData.getModelName():"RuleUnitIndicator";
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

    private boolean isjPMMLAvailableToClassLoader(ClassLoader classLoader) {
        try {
            classLoader.loadClass("org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    
}
