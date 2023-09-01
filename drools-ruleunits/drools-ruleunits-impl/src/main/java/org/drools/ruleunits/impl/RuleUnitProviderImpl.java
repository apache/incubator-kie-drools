/**
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
package org.drools.ruleunits.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
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
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

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
import static org.drools.util.IoUtils.readJarEntryAsString;
import static org.drools.util.JarUtils.normalizeSpringBootResourceUrlPath;

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
                URL resourceUrl = urlEnumeration.nextElement();
                String protocol = resourceUrl.getProtocol();
                switch (protocol) {
                    case "file":
                        collectResourcesInFileSystem(ks, unitClass, unitStatement, resources, resourceUrl);
                        break;
                    case "jar":
                        collectResourcesInJar(ks, resources, unitClass, unitStatement, resourceUrl);
                        break;
                    default:
                        // ignore
                }

            }
        } catch (IOException e) {
            throw new RuleUnitGenerationException("Exception while creating KieModule", e);
        }
        return resources;
    }

    private static void collectResourcesInFileSystem(KieServices ks, Class<?> unitClass, String unitStatement, Collection<Resource> resources, URL resourceUrl) {
        String path = resourceUrl.getPath();
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

    private static void collectResourcesInJar(KieServices ks, Collection<Resource> resources, Class<?> unitClass, String unitStatement, URL resourceUrl) {
        String path = resourceUrl.getPath();                       // file:/path/to/xxx.jar!org/example

        int jarSuffixIndex = path.indexOf(".jar!/");
        String jarPath = path.substring(5, jarSuffixIndex + 4);    // /path/to/xxx.jar
        String directoryPath = path.substring(jarSuffixIndex + 6); // org/example

        directoryPath = normalizeSpringBootResourceUrlPath(directoryPath);

        try (JarFile jarFile = new JarFile(new File(jarPath))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().startsWith(directoryPath + "/") && !jarEntry.isDirectory()) {
                    if (doesDrlContainUnit(jarFile, jarEntry, unitStatement) || doesXlsContainUnit(jarFile, jarEntry, unitClass.getSimpleName())) {
                        Resource resource = ks.getResources().newClassPathResource(jarEntry.getName(), unitClass.getClassLoader());
                        LOGGER.debug("Found {} in {} unit", resource.getSourcePath(), unitClass.getSimpleName());
                        resources.add(resource);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuleUnitGenerationException("Exception while collecting resources in a jar file", e);
        }
    }

    private static boolean doesDrlContainUnit(JarFile jarFile, JarEntry jarEntry, String unitStatement) {
        return jarEntry.getName().endsWith(".drl") && readJarEntryAsString(jarFile, jarEntry).contains(unitStatement);
    }

    private static boolean doesXlsContainUnit(JarFile jarFile, JarEntry jarEntry, String unitName) {
        if (jarEntry.getName().endsWith(".drl.xls") || jarEntry.getName().endsWith(".drl.xlsx")) {
            DecisionTableProvider decisionTableProvider = DecisionTableFactory.getDecisionTableProvider();
            if (decisionTableProvider == null) {
                LOGGER.warn("decision table {} is found, but DecisionTableProvider implementation is not found in the classpath. Please add drools-decisiontables as a dependency", jarEntry.getName());
                return false;
            }
            try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                Map<String, List<String[]>> dtableProperties = decisionTableProvider.loadPropertiesFromInputStream(inputStream, new DecisionTableConfigurationImpl());
                return doDecisionTablePropertiesContainUnit(dtableProperties, unitName);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return false;
    }

    @Override
    public RuleConfig newRuleConfig() {
        return new RuleConfigImpl();
    }

    @Override
    public <T extends RuleUnitData> int invalidateRuleUnits(Class<T> ruleUnitDataClass) {
        if (NamedRuleUnitData.class.isAssignableFrom(ruleUnitDataClass)) {
            // NamedRuleUnitData may create multiple RuleUnits
            List<String> invalidateKeys = ruleUnitMap.entrySet()
                    .stream()
                    .filter(entry -> hasSameRuleUnitDataClass(entry.getValue(), ruleUnitDataClass))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            invalidateKeys.forEach(ruleUnitMap::remove);
            return invalidateKeys.size();
        } else {
            String ruleUnitName = getRuleUnitName(ruleUnitDataClass);
            RuleUnit remove = ruleUnitMap.remove(ruleUnitName);
            return remove == null ? 0 : 1;
        }
    }

    private static <T extends RuleUnitData> boolean hasSameRuleUnitDataClass(RuleUnit ruleUnit, Class<T> ruleUnitDataClass) {
        if (ruleUnit instanceof InternalRuleUnit) {
            return ((InternalRuleUnit) ruleUnit).getRuleUnitDataClass().equals(ruleUnitDataClass);
        } else {
            return false;
        }
    }
}
