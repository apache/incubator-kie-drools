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
package org.drools.model.codegen.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.model.codegen.execmodel.ModelSourceClass;
import org.kie.api.builder.model.KieBaseModel;

import static java.util.stream.Collectors.toList;

/**
 * Generates metadata for "classic" kmodule.xml
 */
public class KieSessionModelBuilder {

    private final Map<String, KieBaseModel> kieBaseModels;
    private final Map<String, List<String>> modelByKBase;
    private DroolsModelBuildContext context;

    public KieSessionModelBuilder(DroolsModelBuildContext context, Collection<CodegenPackageSources> packageSources) {
        this(context, packageSources, KieModuleModelWrapper.fromResourcePaths(context.getAppPaths().getPaths()).kieBaseModels());
    }

    public KieSessionModelBuilder(DroolsModelBuildContext context, Collection<CodegenPackageSources> packageSources, Map<String, KieBaseModel> kieBaseModels) {
        this.context = context;
        this.kieBaseModels = kieBaseModels;
        this.modelByKBase = getModelByKBase(packageSources, this.kieBaseModels);
    }

    public List<GeneratedFile> generate() {

        ModelSourceClass modelSourceClass =
                new ModelSourceClass(DroolsModelBuilder.DUMMY_RELEASE_ID, kieBaseModels, modelByKBase);
        ProjectRuntimeGenerator projectRuntimeGenerator =
                new ProjectRuntimeGenerator(modelSourceClass.getModelMethod(), context);

        return Arrays.asList(
                new GeneratedFile(GeneratedFileType.RULE,
                        modelSourceClass.getName(), modelSourceClass.generate()),
                new GeneratedFile(GeneratedFileType.RULE,
                        projectRuntimeGenerator.getName(), projectRuntimeGenerator.generate()));

    }

    private Map<String, List<String>> getModelByKBase(final Collection<CodegenPackageSources> packageSources,
            final Map<String, KieBaseModel> kieBaseModels) {
        final Map<String, String> modelsByPackage = getModelsByPackage(packageSources);
        final Map<String, List<String>> modelsByKBase = new HashMap<>();
        // Helper set of already processed KieBases that is used to avoid infinite loop, when a user
        // can potentially create a loop with includes definition, e.g.
        // a includes b and b includes a
        final Set<String> alreadyProcessesKieBases = new HashSet<>();
        for (final Map.Entry<String, KieBaseModel> entry : kieBaseModels.entrySet()) {
            modelsByKBase.put(entry.getKey(), getModelsForKieBaseWithIncludes(entry.getValue(), modelsByPackage, alreadyProcessesKieBases));
            alreadyProcessesKieBases.clear();
        }
        return modelsByKBase;
    }

    private List<String> getModelsForKieBaseWithIncludes(final KieBaseModel kieBaseModel,
            final Map<String, String> modelsByPackage, final Set<String> alreadyProcessesKieBases) {
        final List<String> modelsForKieBase = getModelsForKieBaseWithoutIncludes(kieBaseModel, modelsByPackage);
        if (kieBaseModel.getIncludes() != null && !kieBaseModel.getIncludes().isEmpty()) {
            for (final String includedKieBaseName : kieBaseModel.getIncludes()) {
                if (!alreadyProcessesKieBases.contains(includedKieBaseName)) {
                    modelsForKieBase.addAll(getModelsForKieBaseWithIncludes(kieBaseModels.get(includedKieBaseName), modelsByPackage, alreadyProcessesKieBases));
                    alreadyProcessesKieBases.add(includedKieBaseName);
                }
            }
        }
        return modelsForKieBase;
    }

    private List<String> getModelsForKieBaseWithoutIncludes(final KieBaseModel kieBaseModel, final Map<String, String> modelsByPackage) {
        final List<String> kieBasePackages = kieBaseModel.getPackages();
        boolean isAllPackages = kieBasePackages.isEmpty() || (kieBasePackages.size() == 1 && kieBasePackages.get(0).equals("*"));
        return isAllPackages ? new ArrayList<>(modelsByPackage.values()) : kieBasePackages.stream().map(modelsByPackage::get).filter(Objects::nonNull).collect(toList());
    }

    private Map<String, String> getModelsByPackage(Collection<CodegenPackageSources> packageSources) {
        Map<String, String> modelsByPackage = new HashMap<>();
        for (CodegenPackageSources pkgSources : packageSources) {
            modelsByPackage.put(pkgSources.getPackageName(), pkgSources.getPackageName() + "." + pkgSources.getRulesFileName());
        }
        return modelsByPackage;
    }
    
    public Map<String, KieBaseModel> getKieBaseModels() {
        return Collections.unmodifiableMap(kieBaseModels);
    }

}
