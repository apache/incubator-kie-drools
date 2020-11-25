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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.kie.builder.impl.CompilationProblemAdapter;
import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.api.builder.Message;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;

import static java.util.stream.Collectors.groupingBy;

import static org.drools.modelcompiler.builder.JavaParserCompiler.getCompiler;

public class CanonicalModelKieProject extends KieModuleKieProject {

    private final boolean isPattern;

    public static BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> create(boolean isPattern) {
        return (internalKieModule, classLoader) -> new CanonicalModelKieProject(isPattern, internalKieModule, classLoader);
    }

    protected Map<String, ModelBuilderImpl> modelBuilders = new HashMap<>();

    public CanonicalModelKieProject(boolean isPattern, InternalKieModule kieModule, ClassLoader classLoader) {
        super(kieModule instanceof CanonicalKieModule ? kieModule : new CanonicalKieModule( kieModule ), classLoader);
        this.isPattern = isPattern;
    }

    @Override
    protected KnowledgeBuilder createKnowledgeBuilder(KieBaseModelImpl kBaseModel, InternalKieModule kModule) {
        if (getInternalKieModule().getKieModuleModel() != kBaseModel.getKModule()) {
            // if the KieBase belongs to a different kmodule it is not necessary to build it
            return null;
        }
        KnowledgeBuilderConfigurationImpl builderConfiguration = getBuilderConfiguration(kBaseModel, kModule);
        ModelBuilderImpl<PackageSources> modelBuilder = new ModelBuilderImpl<>(PackageSources::dumpSources,
                                                                               builderConfiguration,
                                                                               kModule.getReleaseId(),
                                                                               isPattern,
                                                                               false);
        modelBuilders.put(kBaseModel.getName(), modelBuilder);
        return modelBuilder;
    }

    @Override
    public void writeProjectOutput(MemoryFileSystem trgMfs, ResultsImpl messages) {
        MemoryFileSystem srcMfs = new MemoryFileSystem();
        ModelWriter modelWriter = new ModelWriter();
        Collection<String> modelFiles = new HashSet<>();
        Collection<String> sourceFiles = new HashSet<>();

        Map<String, List<String>> modelsByKBase = new HashMap<>();
        for (Map.Entry<String, ModelBuilderImpl> modelBuilder : modelBuilders.entrySet()) {
            ModelWriter.Result result = modelWriter.writeModel( srcMfs, modelBuilder.getValue().getPackageSources() );
            modelFiles.addAll( result.getModelFiles() );
            sourceFiles.addAll( result.getSourceFiles() );
            modelsByKBase.put( modelBuilder.getKey(), result.getModelFiles() );
        }

        InternalKieModule kieModule = getInternalKieModule();
        ModelSourceClass modelSourceClass = new ModelSourceClass( kieModule.getReleaseId(), kieModule.getKieModuleModel().getKieBaseModels(), modelsByKBase, hasDynamicClassLoader() );
        String projectSourcePath = modelWriter.getBasePath() + "/" + modelSourceClass.getName();
        srcMfs.write(projectSourcePath, modelSourceClass.generate().getBytes());
        sourceFiles.add( projectSourcePath );

        String[] sources = sourceFiles.toArray(new String[sourceFiles.size()]);
        if (sources.length != 0) {
            CompilationResult res = getCompiler().compile(sources, srcMfs, trgMfs, getClassLoader());

            Stream.of(res.getErrors()).collect(groupingBy( CompilationProblem::getFileName))
                    .forEach( (name, errors) -> {
                        errors.forEach( m -> messages.addMessage(new CompilationProblemAdapter( m )) );
                        File srcFile = srcMfs.getFile( name );
                        if ( srcFile instanceof MemoryFile ) {
                            String src = new String ( srcMfs.getFileContents( ( MemoryFile ) srcFile ) );
                            messages.addMessage( Message.Level.ERROR, name, "Java source of " + name + " in error:\n" + src);
                        }
                    } );

            for (CompilationProblem problem : res.getWarnings()) {
                messages.addMessage(new CompilationProblemAdapter(problem));
            }
        }

        modelWriter.writeModelFile(modelFiles, trgMfs, getInternalKieModule().getReleaseId());
    }

    @Override
    protected boolean compileIncludedKieBases() {
        return false;
    }
}