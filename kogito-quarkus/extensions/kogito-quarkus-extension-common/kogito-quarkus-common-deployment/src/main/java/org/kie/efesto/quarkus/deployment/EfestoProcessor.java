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
package org.kie.efesto.quarkus.deployment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.jboss.logging.Logger;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.identifiers.ReflectiveAppRoot;
import org.kie.efesto.common.api.model.GeneratedClassResource;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.core.service.CompilationManagerImpl;
import org.kie.efesto.runtimemanager.core.service.RuntimeManagerImpl;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.quarkus.common.deployment.KogitoBuildContextBuildItem;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;
import static org.kie.kogito.quarkus.common.deployment.KogitoQuarkusResourceUtils.dumpFilesToDisk;

/**
 * Main class of the Kogito extension
 */
public class EfestoProcessor {

    private static final Logger LOGGER = Logger.getLogger(EfestoProcessor.class);

    @BuildStep
    public List<ReflectiveClassBuildItem> reflectiveEfestoStaticClasses() {
        LOGGER.debug("reflectiveEfestoStaticClasses");
        final List<ReflectiveClassBuildItem> toReturn = new ArrayList<>();
        // Generated Resources
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedResources.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedResource.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedExecutableResource.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedRedirectResource.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedClassResource.class));
        // Identifiers
        toReturn.add(new ReflectiveClassBuildItem(true, true, ModelLocalUriId.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, ReflectiveAppRoot.class));
        // Managers
        toReturn.add(new ReflectiveClassBuildItem(true, true, CompilationManagerImpl.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, RuntimeManagerImpl.class));
        return toReturn;
    }

    @BuildStep
    public List<ReflectiveClassBuildItem> reflectiveEfestoGeneratedClasses(List<EfestoGeneratedClassBuildItem> efestoGeneratedClassBuildItem, KogitoBuildContextBuildItem contextBuildItem) {
        LOGGER.debugf("reflectiveEfestoGeneratedClasses %s %s", efestoGeneratedClassBuildItem, contextBuildItem);
        final List<ReflectiveClassBuildItem> toReturn = new ArrayList<>();
        efestoGeneratedClassBuildItem.forEach(generatedClassBuildItem -> {
            Map<GeneratedFileType, List<GeneratedFile>> mappedGeneratedFiles = generatedClassBuildItem.getGeneratedFiles().stream()
                    .collect(Collectors.groupingBy(GeneratedFile::type));
            List<GeneratedFile> generatedCompiledFiles = mappedGeneratedFiles.getOrDefault(COMPILED_CLASS,
                    Collections.emptyList());
            LOGGER.debugf("generatedCompiledFiles %s", generatedCompiledFiles);
            dumpGeneratedClasses(generatedCompiledFiles, contextBuildItem);
            Collection<ReflectiveClassBuildItem> reflectiveClassBuildItems =
                    makeReflectiveClassBuildItems(generatedCompiledFiles);
            LOGGER.debugf("reflectiveClassBuildItems %s", reflectiveClassBuildItems);
            toReturn.addAll(reflectiveClassBuildItems);
        });
        LOGGER.debugf("toReturn %s", toReturn);
        return toReturn;
    }

    @BuildStep
    public NativeImageResourceBuildItem efestoSPICompilation() {
        LOGGER.debug("efestoSPICompilation()");
        return new NativeImageResourceBuildItem("META-INF/services/org.kie.efesto.compilationmanager.api.service.CompilationManager");
    }

    @BuildStep
    public NativeImageResourceBuildItem efestoSPIRuntime() {
        LOGGER.debug("efestoSPIRuntime()");
        return new NativeImageResourceBuildItem("META-INF/services/org.kie.efesto.runtimemanager.api.service.RuntimeManager");
    }

    @BuildStep
    public NativeImageResourceBuildItem efestoSPICompilationPlugin() {
        LOGGER.debug("efestoSPICompilation()");
        return new NativeImageResourceBuildItem("META-INF/services/org.kie.efesto.compilationmanager.api.service.KieCompilerService");
    }

    @BuildStep
    public NativeImageResourceBuildItem efestoSPIRuntimePlugin() {
        LOGGER.debug("efestoSPIRuntime()");
        return new NativeImageResourceBuildItem("META-INF/services/org.kie.efesto.runtimemanager.api.service.KieRuntimeService");
    }

    private static void dumpGeneratedClasses(List<GeneratedFile> generatedCompiledFiles, KogitoBuildContextBuildItem contextBuildItem) {
        LOGGER.debugf("dumpGeneratedClasses %s %s", generatedCompiledFiles, contextBuildItem);
        List<GeneratedFile> dumpableFiles = getDumpableFiles(generatedCompiledFiles);
        final KogitoBuildContext context = contextBuildItem.getKogitoBuildContext();
        dumpFilesToDisk(context.getAppPaths(), dumpableFiles);

    }

    private static List<GeneratedFile> getDumpableFiles(List<GeneratedFile> generatedCompiledFiles) {
        LOGGER.debugf("getDumpableFiles %s", generatedCompiledFiles);
        return generatedCompiledFiles.stream()
                .map(generatedFile -> {
                    File file = generatedFile.path().toFile();
                    String stringPath = file.getPath();
                    File correctedFile = new File(stringPath.replaceAll("\\.", "/") + ".class");
                    return new GeneratedFile(generatedFile.type(),
                            correctedFile.toPath(),
                            generatedFile.contents());
                })
                .collect(Collectors.toList());
    }

    private static Collection<ReflectiveClassBuildItem> makeReflectiveClassBuildItems(List<GeneratedFile> generatedCompiledFiles) {
        Collection<ReflectiveClassBuildItem> buildItems = new ArrayList<>();
        for (GeneratedFile generatedFile : generatedCompiledFiles) {
            buildItems.add(new ReflectiveClassBuildItem(true, true, generatedFile.relativePath()));
        }
        return buildItems;
    }
}
