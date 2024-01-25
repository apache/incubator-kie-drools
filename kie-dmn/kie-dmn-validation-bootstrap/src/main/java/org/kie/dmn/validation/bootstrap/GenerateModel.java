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
package org.kie.dmn.validation.bootstrap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.model.codegen.execmodel.CanonicalModelKieProject;
import org.drools.model.codegen.execmodel.ModelBuilderImpl;
import org.drools.model.codegen.execmodel.ModelWriter;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.util.IoUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateModel {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateModel.class);

    private final File kieDmnValidationBaseDir;

    public GenerateModel(File kieDmnValidationBaseDir) {
        this.kieDmnValidationBaseDir = kieDmnValidationBaseDir;
    }

    public void generate() throws IOException {
        KieServices ks = KieServices.Factory.get();
        final KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(kieDmnValidationBaseDir);

        kieBuilder.buildAll(ValidationBootstrapProject::new,
                            s -> !s.contains("src/test/java") && !s.contains("src\\test\\java") &&
                                    !s.contains("DMNValidator") && // <- to break circularity which is only caused by the KieBuilder trying to early compile everything by itself
                                    !s.contains("dtanalysis"));

        Results results = kieBuilder.getResults();
        results.getMessages().forEach(m -> LOG.info("{}", m.toString()));

        InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
        List<String> generatedFiles = kieModule.getFileNames()
                                               .stream()
                                               .filter(f -> f.endsWith("java"))
                                               .collect(Collectors.toList());

        LOG.info("Executable model will result in {} code generated files...", generatedFiles.size());
        generatedFiles.forEach(LOG::debug);

        MemoryFileSystem mfs = ((MemoryKieModule) ((CanonicalKieModule) kieModule).getInternalKieModule()).getMemoryFileSystem();
        for (String generatedFile : generatedFiles) {
            final MemoryFile f = (MemoryFile) mfs.getFile(generatedFile);
            final Path newFile = Paths.get(kieDmnValidationBaseDir.getAbsolutePath(),
                                           "target",
                                           "generated-sources",
                                           "bootstrap",
                                           f.getPath().asString());

            try {
                Files.deleteIfExists(newFile); //NOSONAR javasecurity:S2083 base dir kieDmnValidationBaseDir is provided as configuration by design, static analysis exclusion applies to these 3 lines
                Files.createDirectories(newFile.getParent()); //NOSONAR
                Files.copy(f.getContents(), newFile, StandardCopyOption.REPLACE_EXISTING); //NOSONAR
            } catch (IOException e) {
                LOG.error("Exception", e);
                throw new RuntimeException("Unable to write file", e);
            }
        }

        byte[] droolsModelFileContent = mfs.getMap()
                                           .entrySet()
                                           .stream()
                                           .filter(kv -> kv.getKey().startsWith(CanonicalKieModule.MODEL_FILE_DIRECTORY) &&
                                                         kv.getKey().endsWith(CanonicalKieModule.MODEL_FILE_NAME))
                                           .map(Map.Entry::getValue)
                                           .findFirst()
                                           .orElseThrow(RuntimeException::new);
        List<String> lines = new BufferedReader(new StringReader(new String(droolsModelFileContent))).lines().collect(Collectors.toList());
        lines.forEach(LOG::debug);
        String vbMain = new String(IoUtils.readBytesFromInputStream(ValidationBootstrapMain.class.getResourceAsStream("ValidationBootstrapModels.java")));
        String v1x = lines.stream().filter(x -> x.startsWith("org.kie.dmn.validation.DMNv1x.Rules")).findFirst().orElseThrow(RuntimeException::new);
        String v11 = lines.stream().filter(x -> x.startsWith("org.kie.dmn.validation.DMNv1_1.Rules")).findFirst().orElseThrow(RuntimeException::new);
        String v12 = lines.stream().filter(x -> x.startsWith("org.kie.dmn.validation.DMNv1_2.Rules")).findFirst().orElseThrow(RuntimeException::new);
        vbMain = vbMain.replaceAll("\\$V1X_MODEL\\$", v1x);
        vbMain = vbMain.replaceAll("\\$V11_MODEL\\$", v11);
        vbMain = vbMain.replaceAll("\\$V12_MODEL\\$", v12);
        final Path validationEntryPointFile = Paths.get(kieDmnValidationBaseDir.getAbsolutePath(),
                                                        "target",
                                                        "generated-sources",
                                                        "bootstrap",
                                                        "org", "kie", "dmn", "validation", "bootstrap", "ValidationBootstrapModels.java");

        LOG.info("Writing code generated ValidationBootstrapModels class into: {}", validationEntryPointFile);
        try {
            Files.deleteIfExists(validationEntryPointFile); //NOSONAR javasecurity:S2083 base dir kieDmnValidationBaseDir is provided as configuration by design, static analysis exclusion applies to these 3 lines
            Files.createDirectories(validationEntryPointFile.getParent()); //NOSONAR
            Files.copy(new ByteArrayInputStream(vbMain.getBytes()), validationEntryPointFile, StandardCopyOption.REPLACE_EXISTING); //NOSONAR

        } catch (IOException e) {
            LOG.error("Exception", e);
            throw new RuntimeException("Unable to write file", e);
        }
    }

    public static class ValidationBootstrapProject extends CanonicalModelKieProject {

        public ValidationBootstrapProject(InternalKieModule kieModule, ClassLoader classLoader) {
            super(kieModule, classLoader);
        }

        @Override
        public void writeProjectOutput(MemoryFileSystem trgMfs, BuildContext buildContext) {
            MemoryFileSystem srcMfs = new MemoryFileSystem();
            List<String> modelFiles = new ArrayList<>();
            List<String> ruleUnitClassNames = new ArrayList<>();
            ModelWriter modelWriter = new ModelWriter();
            for (ModelBuilderImpl modelBuilder : modelBuilders.values()) {
                ModelWriter.Result result = modelWriter.writeModel(srcMfs, modelBuilder.getPackageSources());
                modelFiles.addAll(result.getModelFiles());
                ruleUnitClassNames.addAll( result.getRuleUnitClassNames() );

                final Folder sourceFolder = srcMfs.getFolder("src/main/java");
                final Folder targetFolder = trgMfs.getFolder(".");
                srcMfs.copyFolder(sourceFolder, trgMfs, targetFolder);
            }
            modelWriter.writeModelFile(modelFiles, trgMfs, getInternalKieModule().getReleaseId());
            modelWriter.writeRuleUnitServiceFile(ruleUnitClassNames, trgMfs);
        }
    }
}
