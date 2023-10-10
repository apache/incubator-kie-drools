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
package org.drools.model.codegen.execmodel;

import org.drools.base.util.Drools;
import org.drools.codegen.common.GeneratedFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.util.PortablePath;
import org.kie.api.builder.ReleaseId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.drools.modelcompiler.CanonicalKieModule.MODEL_VERSION;
import static org.drools.modelcompiler.CanonicalKieModule.RULE_UNIT_SERVICES_FILE;
import static org.drools.modelcompiler.CanonicalKieModule.getGeneratedClassNamesFile;
import static org.drools.modelcompiler.CanonicalKieModule.getModelFileWithGAV;

public class ModelWriter {

    private final PortablePath basePath;

    public ModelWriter() {
        this("src/main/java");
    }

    public ModelWriter(String basePath) {
        this.basePath = PortablePath.of(basePath);
    }

    public Result writeModel(MemoryFileSystem srcMfs, Collection<PackageSources> packageSources) {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        List<String> modelFiles = new ArrayList<>();
        List<String> ruleUnitClassNames = new ArrayList<>();

        for (PackageSources pkgSources : packageSources) {
            pkgSources.collectGeneratedFiles( generatedFiles );
            modelFiles.addAll( pkgSources.getModelNames() );
            ruleUnitClassNames.addAll( pkgSources.getRuleUnitClassNames() );
        }

        List<String> sourceFiles = new ArrayList<>();
        for (GeneratedFile generatedFile : generatedFiles) {
            PortablePath path = basePath.resolve(generatedFile.relativePath());
            sourceFiles.add(path.asString());
            srcMfs.write(path, generatedFile.contents());
        }

        return new Result(sourceFiles, modelFiles, ruleUnitClassNames);
    }

    public PortablePath getBasePath() {
        return basePath;
    }

    public void writeModelFile( Collection<String> modelSources, MemoryFileSystem trgMfs, ReleaseId releaseId) {
        String pkgNames = MODEL_VERSION + Drools.getFullVersion() + "\n";
        if (!modelSources.isEmpty()) {
            pkgNames += modelSources.stream().collect(Collectors.joining("\n"));
        }
        trgMfs.write(getModelFileWithGAV(releaseId), pkgNames.getBytes());
    }

    public void writeRuleUnitServiceFile(Collection<String> ruleUnitClassNames, MemoryFileSystem trgMfs) {
        if (!ruleUnitClassNames.isEmpty()) {
            trgMfs.write(RULE_UNIT_SERVICES_FILE, ruleUnitClassNames.stream().collect(Collectors.joining("\n")).getBytes());
        }
    }

    public void writeGeneratedClassNamesFile(Set<String> generatedClassNames, MemoryFileSystem trgMfs, ReleaseId releaseId) {
        trgMfs.write(getGeneratedClassNamesFile(releaseId), generatedClassNamesFileContent(generatedClassNames).getBytes());
    }

    public static String generatedClassNamesFileContent(Set<String> generatedClassNames) {
        String content = "";
        if (!generatedClassNames.isEmpty()) {
            content = generatedClassNames.stream().collect(Collectors.joining("\n"));
        }
        return content;
    }

    public static class Result {

        private final List<String> sourceFiles;
        private final List<String> modelFiles;
        private final List<String> ruleUnitClassNames;

        public Result(List<String> sourceFiles, List<String> modelFiles, List<String> ruleUnitClassNames) {
            this.sourceFiles = sourceFiles;
            this.modelFiles = modelFiles;
            this.ruleUnitClassNames = ruleUnitClassNames;
        }

        public List<String> getSourceFiles() {
            return sourceFiles;
        }

        public List<String> getModelFiles() {
            return modelFiles;
        }

        public List<String> getRuleUnitClassNames() {
            return ruleUnitClassNames;
        }
    }
}
