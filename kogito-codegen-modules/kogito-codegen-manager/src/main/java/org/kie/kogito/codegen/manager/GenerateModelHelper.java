/*
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
package org.kie.kogito.codegen.manager;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.ApplicationGenerator;
import org.kie.kogito.codegen.core.utils.ApplicationGeneratorDiscovery;
import org.kie.kogito.codegen.manager.processes.PersistenceGenerationHelper;

import static org.drools.codegen.common.GeneratedFileType.COMPILED_CLASS;
import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;
import static org.kie.kogito.codegen.manager.CompilerHelper.RESOURCES;
import static org.kie.kogito.codegen.manager.CompilerHelper.SOURCES;

public class GenerateModelHelper {

    private GenerateModelHelper() {
    }

    public record GenerateModelInfo(ClassLoader projectClassLoader,
            KogitoBuildContext kogitoBuildContext,
            boolean generatePartial,
            Map<String, String> properties,
            File outputDirectory,
            List<String> runtimeClassPathElements,
            File baseDir,
            String javaSourceEncoding,
            String javaVersion,
            String schemaVersion,
            boolean keepSources) {

        public GenerateModelInfo(ClassLoader projectClassLoader, KogitoBuildContext kogitoBuildContext, BuilderManager.BuildInfo buildInfo) {
            this(projectClassLoader, kogitoBuildContext, buildInfo.generatePartial(), buildInfo.properties(),
                    buildInfo.outputDirectory().toFile(),
                    buildInfo.runtimeClassPathElements(),
                    buildInfo.projectBaseAbsolutePath().toFile(),
                    buildInfo.javaSourceEncoding(),
                    buildInfo.javaVersion(),
                    buildInfo.jsonSchemaVersion(),
                    buildInfo.keepSources());
        }
    }

    public record GenerateModelFilesInfo(KogitoBuildContext kogitoBuildContext,
            boolean generatePartial) {

        public GenerateModelFilesInfo(GenerateModelInfo generateModelInfo) {
            this(generateModelInfo.kogitoBuildContext,
                    generateModelInfo.generatePartial);
        }
    }

    public static void generateModel(GenerateModelInfo generateModelInfo) {
        if (generateModelInfo.outputDirectory == null) {
            throw new IllegalStateException("outputDirectory is null");
        }
        boolean indexFileDirectorySet = isIndexFileDirectorySet(generateModelInfo.outputDirectory);
        if (indexFileDirectorySet) {
            System.clearProperty(INDEXFILE_DIRECTORY_PROPERTY);
        }

        /* 1. Execute Code Generation based on KIE models */
        Map<String, Collection<GeneratedFile>> generatedModelFiles = generateModelFiles(new GenerateModelFilesInfo(generateModelInfo));

        CompilerHelper.CompileInfo compileInfo =
                new CompilerHelper.CompileInfo(generatedModelFiles.get(SOURCES),
                        generatedModelFiles.get(RESOURCES), generateModelInfo);

        /* 2. Persist the models' code-generated source and resources files in target (maven) or gradle (build) */
        GeneratedFileManager.dumpGeneratedFiles(compileInfo.generatedSources(), compileInfo.baseDir().toPath());
        GeneratedFileManager.dumpGeneratedFiles(compileInfo.resources(), compileInfo.baseDir().toPath());

        /* 3. Compile and persist compiled files in target (maven) or gradle (build) */
        CompilerHelper.compileAndDump(compileInfo);

        /*
         * 4. Execute Code Generation based on process extensions modules.
         * This MUST be executed AFTER models' code-generated sources are compiled
         * The code generated in this phase DEPENDS on the code previously generated.
         */
        Map<String, Collection<GeneratedFile>> generatedPersistenceFiles =
                PersistenceGenerationHelper.generatePersistenceFiles(generateModelInfo.kogitoBuildContext, generateModelInfo.projectClassLoader, generateModelInfo.schemaVersion);

        compileInfo =
                new CompilerHelper.CompileInfo(generatedPersistenceFiles.get(SOURCES),
                        generatedPersistenceFiles.get(RESOURCES), generateModelInfo);

        /* 5. Persist the process extension code-generated source and resources files in target (maven) or gradle (build) */
        GeneratedFileManager.dumpGeneratedFiles(generatedPersistenceFiles.get(SOURCES), compileInfo.baseDir().toPath());
        GeneratedFileManager.dumpGeneratedFiles(generatedPersistenceFiles.get(RESOURCES), compileInfo.baseDir().toPath());

        /* 6. Compile and persist compiled files in target (maven) or gradle (build) */
        CompilerHelper.compileAndDump(compileInfo);

        if (!generateModelInfo.keepSources()) {
            GeneratedFileManager.deleteFilesByExtension(generateModelInfo.outputDirectory().toPath(), "drl");
        }
    }

    public static Map<String, Collection<GeneratedFile>> generateModelFiles(GenerateModelFilesInfo generateModelFilesInfo) {
        ApplicationGenerator appGen = ApplicationGeneratorDiscovery.discover(generateModelFilesInfo.kogitoBuildContext());

        Collection<GeneratedFile> generatedFiles;
        if (generateModelFilesInfo.generatePartial()) {
            generatedFiles = appGen.generateComponents();
        } else {
            generatedFiles = appGen.generate();
        }
        Collection<GeneratedFile> generatedClasses = new HashSet<>();
        Collection<GeneratedFile> generatedResources = new HashSet<>();
        generatedFiles.forEach(generatedFile -> {
            switch (generatedFile.category()) {
                case SOURCE -> generatedClasses.add(generatedFile);
                case INTERNAL_RESOURCE, STATIC_HTTP_RESOURCE -> generatedResources.add(generatedFile);
                case COMPILED_CLASS -> generatedResources.add(new GeneratedFile(COMPILED_CLASS, convertPath(generatedFile.path().toString()), generatedFile.contents()));
                default -> throw new IllegalStateException("Unexpected file with category: " + generatedFile.category());
            }
        });
        return Map.of(SOURCES, generatedClasses, RESOURCES, generatedResources);
    }

    static boolean isIndexFileDirectorySet(File outputDirectory) {
        boolean toReturn = false;
        if (System.getProperty(INDEXFILE_DIRECTORY_PROPERTY) == null) {
            System.setProperty(INDEXFILE_DIRECTORY_PROPERTY, outputDirectory.toString());
            toReturn = true;
        }
        return toReturn;
    }

    private static String convertPath(String toConvert) {
        return toConvert.replace('.', File.separatorChar) + ".class";
    }
}
