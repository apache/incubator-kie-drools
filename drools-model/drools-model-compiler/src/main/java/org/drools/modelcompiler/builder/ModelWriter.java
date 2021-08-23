/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.Drools;
import org.kie.api.builder.ReleaseId;

import static org.drools.modelcompiler.CanonicalKieModule.MODEL_VERSION;
import static org.drools.modelcompiler.CanonicalKieModule.getModelFileWithGAV;
import static org.drools.modelcompiler.CanonicalKieModule.getGeneratedClassNamesFile;

public class ModelWriter {

    private final String basePath;

    public ModelWriter() {
        this("src/main/java");
    }

    public ModelWriter(String basePath) {
        this.basePath = basePath;
    }

    public Result writeModel(MemoryFileSystem srcMfs, Collection<PackageSources> packageSources) {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        List<String> modelFiles = new ArrayList<>();

        for (PackageSources pkgSources : packageSources) {
            pkgSources.collectGeneratedFiles( generatedFiles );
            modelFiles.addAll( pkgSources.getModelNames() );
        }

        List<String> sourceFiles = new ArrayList<>();
        for (GeneratedFile generatedFile : generatedFiles) {
            String path = basePath + "/" + generatedFile.getPath();
            sourceFiles.add(path);
            srcMfs.write(path, generatedFile.getData());
        }

        return new Result(sourceFiles, modelFiles);
    }

    private String pojoName(String folderName, String nameAsString) {
        return basePath + "/" + folderName + "/" + nameAsString + ".java";
    }

    public String getBasePath() {
        return basePath;
    }

    public void writeModelFile( Collection<String> modelSources, MemoryFileSystem trgMfs, ReleaseId releaseId) {
        String pkgNames = MODEL_VERSION + Drools.getFullVersion() + "\n";
        if (!modelSources.isEmpty()) {
            pkgNames += modelSources.stream().collect(Collectors.joining("\n"));
        }
        trgMfs.write(getModelFileWithGAV(releaseId), pkgNames.getBytes());
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

        public Result(List<String> sourceFiles, List<String> modelFiles) {
            this.sourceFiles = sourceFiles;
            this.modelFiles = modelFiles;
        }

        public List<String> getSourceFiles() {
            return sourceFiles;
        }

        public List<String> getModelFiles() {
            return modelFiles;
        }
    }
}
