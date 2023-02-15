/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

import org.drools.compiler.builder.conf.DecisionTableConfigurationImpl;
import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.drl.extensions.DecisionTableProvider;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.model.codegen.execmodel.CanonicalModelKieProject;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.drools.ruleunits.impl.conf.RuleConfigImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.util.IoUtils.readFileAsString;

public class RuleUnitProviderImpl implements RuleUnitProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleUnitProviderImpl.class);

    private static final boolean USE_EXEC_MODEL = true;

    private final Map<String, RuleUnit> ruleUnitMap;

    public RuleUnitProviderImpl() {
        this.ruleUnitMap = loadRuleUnits(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public <T extends RuleUnitData> RuleUnit<T> getRuleUnit(T ruleUnitData) {
        String ruleUnitName = getRuleUnitName(ruleUnitData);
        RuleUnit<T> ruleUnit = ruleUnitMap.get(ruleUnitName);
        if (ruleUnit != null) {
            return ruleUnit;
        }
        ruleUnitMap.putAll(generateRuleUnit(ruleUnitData));
        return ruleUnitMap.get(ruleUnitName);
    }

    protected <T extends RuleUnitData> Map<String, RuleUnit> generateRuleUnit(T ruleUnitData) {
        InternalKieModule kieModule = createRuleUnitKieModule(ruleUnitData.getClass(), USE_EXEC_MODEL);
        KieModuleKieProject kieModuleKieProject = createRuleUnitKieProject(kieModule, USE_EXEC_MODEL);
        return loadRuleUnits(kieModuleKieProject.getClassLoader());
    }

    private Map<String, RuleUnit> loadRuleUnits(ClassLoader classLoader) {
        Map<String, RuleUnit> map = new HashMap<>();
        ServiceLoader<RuleUnit> loader = ServiceLoader.load(RuleUnit.class, classLoader);
        for (RuleUnit impl : loader) {
            map.put( getRuleUnitName( ((InternalRuleUnit) impl).getRuleUnitDataClass() ), impl);
        }
        return map;
    }

    protected String getRuleUnitName(RuleUnitData ruleUnitData) {
        if (ruleUnitData instanceof NamedRuleUnitData) {
            return ((NamedRuleUnitData) ruleUnitData).getUnitName();
        }
        return getRuleUnitName(ruleUnitData.getClass());
    }

    protected String getRuleUnitName(Class<? extends RuleUnitData> ruleUnitDataClass) {
        return ruleUnitDataClass.getCanonicalName();
    }

    static InternalKieModule createRuleUnitKieModule(Class<?> unitClass, boolean useExecModel) {
        KieServices ks = KieServices.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        for (Resource drlResource : ruleResourcesForUnitClass(ks, unitClass)) {
            kfs.write(drlResource);
        }
        return (InternalKieModule) ks.newKieBuilder( kfs )
                .getKieModule(useExecModel ? ExecutableModelProject.class : DrlProject.class);
    }

    static KieModuleKieProject createRuleUnitKieProject(InternalKieModule kieModule, boolean useExecModel) {
        return useExecModel ?
                new CanonicalModelKieProject(kieModule, kieModule.getModuleClassLoader()) :
                new KieModuleKieProject(kieModule, kieModule.getModuleClassLoader());
    }

    private static Collection<Resource> ruleResourcesForUnitClass(KieServices ks, Class<?> unitClass) {
        String unitStatement = "unit " + unitClass.getSimpleName();
        Collection<Resource> resources = new HashSet<>();
        try {
            Enumeration<URL> urlEnumeration = unitClass.getClassLoader().getResources(unitClass.getPackageName().replace('.', '/'));
            while (urlEnumeration.hasMoreElements()) {
                String path = urlEnumeration.nextElement().getPath();
                Optional.ofNullable(new File(path).listFiles())
                        .stream()
                        .flatMap(Arrays::stream)
                        .filter(f -> doesDrlContainUnit(f, unitStatement) || doesXlsContainUnit(f, unitClass.getSimpleName()))
                        .map(ks.getResources()::newFileSystemResource)
                        .forEach(resource -> {
                            LOGGER.debug("Found {} in {} unit", resource.getSourcePath(), unitClass.getSimpleName());
                            resources.add(resource);
                        });
            }
        } catch (IOException e) {
            throw new RuleUnitGenerationException("Exception while creating KieModule", e);
        }
        return resources;
    }

    private static boolean doesDrlContainUnit(File file, String unitStatement) {
        return file.getName().endsWith(".drl") && readFileAsString(file).contains(unitStatement);
    }

    private static boolean doesXlsContainUnit(File file, String unitName) {
        if (file.getName().endsWith(".drl.xls") || file.getName().endsWith(".drl.xlsx")) {
            DecisionTableProvider decisionTableProvider = DecisionTableFactory.getDecisionTableProvider();
            if (decisionTableProvider == null) {
                LOGGER.warn("decision table {} is found, but DecisionTableProvider implementation is not found in the classpath. Please add drools-decisiontables as a dependency", file.getName());
                return false;
            }
            Map<String, List<String[]>> dtableProperties = decisionTableProvider.loadPropertiesFromFile(file, new DecisionTableConfigurationImpl());
            return doDecisionTablePropertiesContainUnit(dtableProperties, unitName);
        }
        return false;
    }

    private static boolean doDecisionTablePropertiesContainUnit(Map<String, List<String[]>> dtableProperties, String unitName) {
        List<String[]> unitValues = dtableProperties.get("unit");
        return unitValues != null && unitValues.stream().anyMatch(valueArray -> valueArray.length > 0 && valueArray[0] != null && valueArray[0].trim().equals(unitName));
    }

    @Override
    public RuleConfig newRuleConfig() {
        return new RuleConfigImpl();
    }

}
