/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.drl.quarkus.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Produce;
import io.quarkus.deployment.builditem.ArchiveRootBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.pkg.builditem.ArtifactResultBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.vertx.http.deployment.spi.AdditionalStaticResourceBuildItem;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.drl.quarkus.util.deployment.KmoduleKieBaseModelsBuiltItem;
import org.drools.drl.quarkus.util.deployment.OtnClassesByPackageBuildItem;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.project.RuleCodegen;
import org.kie.api.io.Resource;
import org.kie.drl.engine.runtime.mapinput.service.KieRuntimeServiceDrlMapInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.drl.quarkus.util.deployment.DroolsQuarkusResourceUtils.HOT_RELOAD_SUPPORT_PATH;
import static org.drools.drl.quarkus.util.deployment.DroolsQuarkusResourceUtils.compileGeneratedSources;
import static org.drools.drl.quarkus.util.deployment.DroolsQuarkusResourceUtils.createDroolsBuildContext;
import static org.drools.drl.quarkus.util.deployment.DroolsQuarkusResourceUtils.dumpFilesToDisk;
import static org.drools.drl.quarkus.util.deployment.DroolsQuarkusResourceUtils.getHotReloadSupportSource;
import static org.drools.drl.quarkus.util.deployment.DroolsQuarkusResourceUtils.getRuleUnitDefProducerSource;
import static org.drools.drl.quarkus.util.deployment.DroolsQuarkusResourceUtils.registerResources;
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

    private static final String FEATURE = "drools-drl-quarkus-extension";

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public void generateSources( BuildProducer<GeneratedBeanBuildItem> generatedBeans,
                                 BuildProducer<NativeImageResourceBuildItem> resource,
                                 BuildProducer<AdditionalStaticResourceBuildItem> staticResProducer,
                                 BuildProducer<GeneratedResourceBuildItem> genResBI,
                                 BuildProducer<OtnClassesByPackageBuildItem> otnClasesBI,
                                 BuildProducer<KmoduleKieBaseModelsBuiltItem> kbaseModelsBI) {
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
        
        otnClasesBI.produce(new OtnClassesByPackageBuildItem(ruleCodegen.getPackageModels().stream().collect(Collectors.toMap(PackageModel::getName, PackageModel::getOtnsClasses))));
        if (ruleCodegen.getKmoduleKieBaseModels() != null) {
            kbaseModelsBI.produce(new KmoduleKieBaseModelsBuiltItem(ruleCodegen.getKmoduleKieBaseModels()));
        }
    }

    @BuildStep
    public List<ReflectiveClassBuildItem> reflectiveEfestoRules() {
        LOGGER.debug("reflectiveEfestoRules()");
        final List<ReflectiveClassBuildItem> toReturn = new ArrayList<>();
        toReturn.add(new ReflectiveClassBuildItem(true, true, KieRuntimeServiceDrlMapInput.class));
        LOGGER.debug("toReturn {}", toReturn.size());
        return toReturn;
    }
    
    @BuildStep
    @Produce(GeneratedResourceBuildItem.class)
    public void demo( OtnClassesByPackageBuildItem otn, Optional<KmoduleKieBaseModelsBuiltItem> kbaseModels) {
        LOGGER.debug("{}", otn.getOtnClasses());
        if (kbaseModels.isPresent()) {
            LOGGER.debug("{}", kbaseModels.get().getKieBaseModels());            
        }
    }
}
