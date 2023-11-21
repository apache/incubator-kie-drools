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
package org.drools.quarkus.deployment;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ArchiveRootBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.resteasy.reactive.spi.GeneratedJaxRsResourceBuildItem;
import io.quarkus.vertx.http.deployment.spi.AdditionalStaticResourceBuildItem;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.quarkus.util.deployment.GlobalsBuildItem;
import org.drools.quarkus.util.deployment.KmoduleKieBaseModelsBuiltItem;
import org.drools.quarkus.util.deployment.PatternsTypesBuildItem;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.project.RuleCodegen;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.HOT_RELOAD_SUPPORT_PATH;
import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.compileGeneratedSources;
import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.createDroolsBuildContext;
import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.dumpFilesToDisk;
import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.getHotReloadSupportSource;
import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.getRuleUnitDefProducerSource;
import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.registerResources;
import static org.drools.quarkus.util.deployment.DroolsQuarkusResourceUtils.toClassName;
import static org.drools.model.codegen.project.RuleCodegen.ofResources;

public class DroolsAssetsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsAssetsProcessor.class);

    @Inject
    ArchiveRootBuildItem root;
    @Inject
    LiveReloadBuildItem liveReload;
    @Inject
    CurateOutcomeBuildItem curateOutcomeBuildItem;
    @Inject
    CombinedIndexBuildItem combinedIndexBuildItem;
    @Inject
    OutputTargetBuildItem outputTargetBuildItem;

    private static final String FEATURE = "drools-quarkus-extension";

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public void generateSources( BuildProducer<GeneratedBeanBuildItem> generatedBeans,
                                 BuildProducer<NativeImageResourceBuildItem> resource,
                                 BuildProducer<AdditionalStaticResourceBuildItem> staticResProducer,
                                 BuildProducer<GeneratedResourceBuildItem> genResBI,
                                 BuildProducer<PatternsTypesBuildItem> otnClasesBI,
                                 BuildProducer<KmoduleKieBaseModelsBuiltItem> kbaseModelsBI,
                                 BuildProducer<GlobalsBuildItem> globalsBI,
                                 BuildProducer<GeneratedJaxRsResourceBuildItem> jaxrsProducer) {
        DroolsModelBuildContext context =
                createDroolsBuildContext(outputTargetBuildItem.getOutputDirectory(), root.getPaths(), combinedIndexBuildItem.getIndex());

        Collection<Resource> resources = ResourceCollector.fromPaths(context.getAppPaths().getPaths());

        RuleCodegen ruleCodegen = ofResources(context, resources);
        Collection<GeneratedFile> generatedFiles = ruleCodegen.generate();
        generatedFiles.addAll(getRuleUnitDefProducerSource(combinedIndexBuildItem.getIndex()));

        // The HotReloadSupportClass has to be generated only during the first model generation
        // During actual hot reloads it will be regenerated by the compilation providers in order to retrigger this build step
        if (!liveReload.isLiveReload()) {
            generatedFiles.add(new GeneratedFile(GeneratedFileType.SOURCE, HOT_RELOAD_SUPPORT_PATH + ".java", getHotReloadSupportSource()));
        }

        // dump files to disk
        dumpFilesToDisk(context.getAppPaths(), generatedFiles);

        Collection<ResolvedDependency> dependencies = curateOutcomeBuildItem.getApplicationModel().getRuntimeDependencies();

        // build Java source code and register the generated beans
        Collection<GeneratedBeanBuildItem> generatedBeanBuildItems =
                compileGeneratedSources(context, dependencies, generatedFiles, liveReload.isLiveReload());
        generatedBeanBuildItems.forEach(generatedBeans::produce);

        registerResources(generatedFiles, staticResProducer, resource, genResBI);
        
        otnClasesBI.produce(new PatternsTypesBuildItem(ruleCodegen.getPackageModels().stream().collect(Collectors.toMap(PackageModel::getName, PackageModel::getOtnsClasses))));
        if (ruleCodegen.getKmoduleKieBaseModels() != null) {
            kbaseModelsBI.produce(new KmoduleKieBaseModelsBuiltItem(ruleCodegen.getKmoduleKieBaseModels()));
        }
        globalsBI.produce(new GlobalsBuildItem(ruleCodegen.getPackageModels().stream().collect(Collectors.toMap(PackageModel::getName, PackageModel::getGlobals))));

        Set<String> restResourceClassNameSet = generatedFiles.stream()
                .filter(file -> file.type() == GeneratedFileType.REST)
                .map(file -> toClassName(file.path().toString()))
                .collect(Collectors.toSet());
        generatedBeanBuildItems.stream()
                .filter(b -> restResourceClassNameSet.contains(b.getName()))
                .forEach(b -> jaxrsProducer.produce(new GeneratedJaxRsResourceBuildItem(b.getName(), b.getData())));
    }
}
