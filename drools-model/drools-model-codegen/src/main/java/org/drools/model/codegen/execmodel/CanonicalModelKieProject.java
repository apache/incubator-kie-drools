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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.CompilationProblemAdapter;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.util.ClassUtils;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.api.builder.Message;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.drools.util.PortablePath;

import static java.util.stream.Collectors.groupingBy;
import static org.drools.model.codegen.execmodel.JavaParserCompiler.getCompiler;

public class CanonicalModelKieProject extends KieModuleKieProject {

    public static BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> create() {
        return (internalKieModule, classLoader) -> new CanonicalModelKieProject(internalKieModule, classLoader);
    }

    protected Map<String, ModelBuilderImpl> modelBuilders = new HashMap<>();

    public CanonicalModelKieProject(InternalKieModule kieModule, ClassLoader classLoader) {
        super(kieModule instanceof CanonicalKieModule ? kieModule : new CanonicalKieModule( kieModule ), classLoader);
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
                                                                               false);
        modelBuilders.put(kBaseModel.getName(), modelBuilder);
        return modelBuilder;
    }

    @Override
    public void writeProjectOutput(MemoryFileSystem trgMfs, BuildContext buildContext) {
        MemoryFileSystem srcMfs = new MemoryFileSystem();
        ModelWriter modelWriter = new ModelWriter();
        Collection<String> modelFiles = new HashSet<>();
        Collection<String> sourceFiles = new HashSet<>();
        Collection<String> ruleUnitClassNames = new HashSet<>();

        Map<String, List<String>> modelsByKBase = new HashMap<>();
        for (Map.Entry<String, ModelBuilderImpl> modelBuilder : modelBuilders.entrySet()) {
            ModelWriter.Result result = modelWriter.writeModel( srcMfs, modelBuilder.getValue().getPackageSources() );
            modelFiles.addAll( result.getModelFiles() );
            sourceFiles.addAll( result.getSourceFiles() );
            ruleUnitClassNames.addAll( result.getRuleUnitClassNames() );

            List<String> modelFilesForKieBase = new ArrayList<>();
            modelFilesForKieBase.addAll( result.getModelFiles() );
            modelFilesForKieBase.addAll( ((CanonicalModelBuildContext) buildContext).getNotOwnedModelFiles(modelBuilders, modelBuilder.getKey()) );
            modelsByKBase.put( modelBuilder.getKey(), modelFilesForKieBase );
        }

        InternalKieModule kieModule = getInternalKieModule();
        ModelSourceClass modelSourceClass = new ModelSourceClass( kieModule.getReleaseId(), kieModule.getKieModuleModel().getKieBaseModels(), modelsByKBase, hasDynamicClassLoader() );
        String projectSourcePath = modelWriter.getBasePath().asString() + "/" + modelSourceClass.getName();
        srcMfs.write(projectSourcePath, modelSourceClass.generate().getBytes());
        sourceFiles.add( projectSourcePath );

        Set<PortablePath> origFileNames = new HashSet<>(trgMfs.getFilePaths());

        String[] sources = sourceFiles.toArray(new String[sourceFiles.size()]);
        CompilationResult res = getCompiler().compile(sources, srcMfs, trgMfs, getClassLoader());

        Stream.of(res.getErrors()).collect(groupingBy( CompilationProblem::getFileName))
                .forEach( (name, errors) -> {
                    errors.forEach( m -> buildContext.getMessages().addMessage(new CompilationProblemAdapter( m )) );
                    File srcFile = srcMfs.getFile( name );
                    if ( srcFile instanceof MemoryFile ) {
                        String src = new String ( srcMfs.getFileContents( ( MemoryFile ) srcFile ) );
                        buildContext.getMessages().addMessage( Message.Level.ERROR, name, "Java source of " + name + " in error:\n" + src);
                    }
                } );

        for (CompilationProblem problem : res.getWarnings()) {
            buildContext.getMessages().addMessage(new CompilationProblemAdapter(problem));
        }

        if (ProjectClassLoader.isEnableStoreFirst()) {
            Set<PortablePath> generatedClassPaths = new HashSet<>(trgMfs.getFilePaths());
            generatedClassPaths.removeAll(origFileNames);
            Set<String> generatedClassNames = generatedClassPaths.stream()
                    .map(PortablePath::asString)
                    .map(ClassUtils::convertResourceToClassName)
                    .collect(Collectors.toSet());
            modelWriter.writeGeneratedClassNamesFile(generatedClassNames, trgMfs, getInternalKieModule().getReleaseId());
        }

        modelWriter.writeModelFile(modelFiles, trgMfs, getInternalKieModule().getReleaseId());
        modelWriter.writeRuleUnitServiceFile(ruleUnitClassNames, trgMfs);
    }

    @Override
    protected boolean compileIncludedKieBases() {
        return false;
    }

    @Override
    public BuildContext createBuildContext(ResultsImpl results) {
        return new CanonicalModelBuildContext(results);
    }
}