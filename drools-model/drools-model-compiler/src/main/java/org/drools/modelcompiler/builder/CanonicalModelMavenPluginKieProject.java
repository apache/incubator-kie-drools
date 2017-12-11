/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;

public class CanonicalModelMavenPluginKieProject extends CanonicalModelKieProject {

    public CanonicalModelMavenPluginKieProject(InternalKieModule kieModule, ClassLoader classLoader) {
        super(kieModule, classLoader);
    }

    @Override
    public void writeProjectOutput(MemoryFileSystem trgMfs) {
        MemoryFileSystem srcMfs = new MemoryFileSystem();
        List<String> modelFiles = new ArrayList<>();
        ModelWriter modelWriter = new ModelWriter();
        for (ModelBuilderImpl modelBuilder : modelBuilders) {
            ModelWriter.Result result = modelWriter.writeModel(srcMfs, modelBuilder.getPackageModels());
            modelFiles.addAll(result.modelFiles);
            final Folder sourceFolder = srcMfs.getFolder("src/main/java");
            final Folder targetFolder = trgMfs.getFolder(".");
            srcMfs.copyFolder(sourceFolder, trgMfs, targetFolder);
        }
        modelWriter.writeModelFile(modelFiles, trgMfs);
    }
}