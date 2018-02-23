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
import java.util.function.BiFunction;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.internal.builder.KnowledgeBuilder;

import static org.drools.modelcompiler.builder.JavaParserCompiler.getCompiler;

public class CanonicalModelKieProject extends KieModuleKieProject {

    private final boolean isPattern;

    public static BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> create(boolean isPattern) {
        return (internalKieModule, classLoader) -> new CanonicalModelKieProject(isPattern, internalKieModule, classLoader);
    }

    protected List<ModelBuilderImpl> modelBuilders = new ArrayList<>();

    public CanonicalModelKieProject(boolean isPattern, InternalKieModule kieModule, ClassLoader classLoader) {
        super(kieModule instanceof CanonicalKieModule ? kieModule : new CanonicalKieModule( kieModule ), classLoader);
        this.isPattern = isPattern;
    }

    @Override
    protected KnowledgeBuilder createKnowledgeBuilder(KieBaseModelImpl kBaseModel, InternalKieModule kModule) {
        ModelBuilderImpl modelBuilder = new ModelBuilderImpl(isPattern);
        modelBuilders.add(modelBuilder);
        return modelBuilder;
    }

    @Override
    public void writeProjectOutput(MemoryFileSystem trgMfs, ResultsImpl messages) {
        MemoryFileSystem srcMfs = new MemoryFileSystem();
        ModelWriter modelWriter = new ModelWriter();
        List<String> modelFiles = new ArrayList<>();

        for (ModelBuilderImpl modelBuilder : modelBuilders) {
            final ModelWriter.Result result = modelWriter.writeModel( srcMfs, modelBuilder.getPackageModels() );
            modelFiles.addAll(result.getModelFiles());
            final String[] sources = result.getSources();
            if(sources.length != 0) {
                CompilationResult res = getCompiler().compile(sources, srcMfs, trgMfs, getClassLoader());

                for (CompilationProblem problem : res.getErrors()) {
                    messages.addMessage(problem);
                }
                for (CompilationProblem problem : res.getWarnings()) {
                    messages.addMessage(problem);
                }
            }
        }

        modelWriter.writeModelFile(modelFiles, trgMfs);
    }
}