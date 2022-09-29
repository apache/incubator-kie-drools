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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.model.codegen.execmodel.CanonicalModelKieProject;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.RuleUnitData;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.util.IoUtils.readFileAsString;

public class RuleUnitProviderImpl implements RuleUnitProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleUnitProviderImpl.class);

    private static final boolean USE_EXEC_MODEL = true;

    private final Map<Class<? extends RuleUnitData>, RuleUnit> ruleUnitMap;

    public RuleUnitProviderImpl() {
        this.ruleUnitMap = loadRuleUnits(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public <T extends RuleUnitData> RuleUnit<T> getRuleUnit(T ruleUnitData) {
        Class<? extends RuleUnitData> ruleUnitDataClass = ruleUnitData.getClass();
        RuleUnit<T> ruleUnit = ruleUnitMap.get(ruleUnitDataClass);
        if (ruleUnit != null) {
            return ruleUnit;
        }
        ruleUnitMap.putAll(generateRuleUnit(ruleUnitData));
        return ruleUnitMap.get(ruleUnitDataClass);
    }

    protected <T extends RuleUnitData> Map<Class<? extends RuleUnitData>, RuleUnit> generateRuleUnit(T ruleUnitData) {
        InternalKieModule kieModule = createRuleUnitKieModule(ruleUnitData.getClass(), USE_EXEC_MODEL);
        KieModuleKieProject kieModuleKieProject = createRuleUnitKieProject(kieModule, USE_EXEC_MODEL);
        return loadRuleUnits(kieModuleKieProject.getClassLoader());
    }

    private Map<Class<? extends RuleUnitData>, RuleUnit> loadRuleUnits(ClassLoader classLoader) {
        Map<Class<? extends RuleUnitData>, RuleUnit> map = new HashMap<>();
        ServiceLoader<RuleUnit> loader = ServiceLoader.load(RuleUnit.class, classLoader);
        for (RuleUnit impl : loader) {
            map.put(((InternalRuleUnit) impl).getRuleUnitDataClass(), impl);
        }
        return map;
    }

    static InternalKieModule createRuleUnitKieModule(Class<?> unitClass, boolean useExecModel) {
        KieServices ks = KieServices.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        for (Resource drlResource : drlResourcesForUnitClass(ks, unitClass)) {
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

    private static Collection<Resource> drlResourcesForUnitClass(KieServices ks, Class<?> unitClass) {
        String unitStatement = "unit " + unitClass.getSimpleName();
        Collection<Resource> resources = new HashSet<>();
        try {
            Enumeration<URL> urlEnumeration = unitClass.getClassLoader().getResources( unitClass.getPackageName().replace('.', '/') );
            while (urlEnumeration.hasMoreElements()) {
                String path = urlEnumeration.nextElement().getPath();
                Stream.of( new File(path).listFiles() )
                        .filter( f -> f.getPath().endsWith(".drl") )
                        .filter( f -> readFileAsString(f).contains(unitStatement) )
                        .peek( f -> LOGGER.debug("Found " + f.getPath() + " in " + unitClass.getSimpleName() + " unit") )
                        .map( ks.getResources()::newFileSystemResource )
                        .forEach( resources::add );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resources;
    }
}
