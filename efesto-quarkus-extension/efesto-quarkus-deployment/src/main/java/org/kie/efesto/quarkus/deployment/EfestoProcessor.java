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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.jboss.logging.Logger;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.common.api.model.GeneratedClassResource;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.core.service.CompilationManagerImpl;
import org.kie.efesto.runtimemanager.core.service.RuntimeManagerImpl;

import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;

/**
 * Main class of the Kogito extension
 */
public class KogitoEfestoProcessor {

    private static final Logger LOGGER = Logger.getLogger(KogitoEfestoProcessor.class);

    @BuildStep
    public List<ReflectiveClassBuildItem> reflectiveEfestoStaticClasses() {
        LOGGER.infof("reflectiveEfestoStaticClasses");
        final List<ReflectiveClassBuildItem> toReturn = new ArrayList<>();
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedResources.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedResource.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedExecutableResource.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedRedirectResource.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, GeneratedClassResource.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, FRI.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, CompilationManagerImpl.class));
        toReturn.add(new ReflectiveClassBuildItem(true, true, RuntimeManagerImpl.class));
        return toReturn;
    }

    @BuildStep
    public List<ReflectiveClassBuildItem> reflectiveEfestoGeneratedClasses(Collection<GeneratedFile> generatedFiles) {
        LOGGER.infof("reflectiveEfestoGeneratedClasses %s",  generatedFiles);
        final List<ReflectiveClassBuildItem> toReturn = new ArrayList<>();


        Map<GeneratedFileType, List<GeneratedFile>> mappedGeneratedFiles = generatedFiles.stream()
                .collect(Collectors.groupingBy(GeneratedFile::type));
        List<GeneratedFile> generatedCompiledFiles = mappedGeneratedFiles.getOrDefault(COMPILED_CLASS,
                                                                                       Collections.emptyList());
        LOGGER.infof("generatedCompiledFiles {}", generatedCompiledFiles);
        Collection<ReflectiveClassBuildItem> reflectiveClassBuildItems =
                makeReflectiveClassBuildItems(generatedCompiledFiles);
        LOGGER.infof("reflectiveClassBuildItems {}", reflectiveClassBuildItems);
        toReturn.addAll(reflectiveClassBuildItems);
        LOGGER.infof("toReturn {}", toReturn);
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

    private static Collection<ReflectiveClassBuildItem> makeReflectiveClassBuildItems(List<GeneratedFile> generatedCompiledFiles) {
        Collection<ReflectiveClassBuildItem> buildItems = new ArrayList<>();
        for (GeneratedFile generatedFile : generatedCompiledFiles) {
            buildItems.add(new ReflectiveClassBuildItem(true, true, generatedFile.relativePath()));
        }
        return buildItems;
    }
}
