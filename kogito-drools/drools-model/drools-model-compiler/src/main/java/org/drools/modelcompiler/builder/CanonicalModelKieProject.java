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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.util.Drools;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.api.builder.Message;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.jci.CompilationProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;
import static org.drools.modelcompiler.CanonicalKieModule.MODEL_FILE;
import static org.drools.modelcompiler.CanonicalKieModule.MODEL_VERSION;
import static org.drools.modelcompiler.CanonicalKieModule.createFromClassLoader;
import static org.drools.modelcompiler.builder.JavaParserCompiler.getCompiler;

public class CanonicalModelKieProject extends KieModuleKieProject {

    Logger logger = LoggerFactory.getLogger(CanonicalModelKieProject.class);

    public static final String PROJECT_RUNTIME_CLASS = "org.drools.project.model.ProjectRuntime";
    public static final String PROJECT_RUNTIME_RESOURCE_CLASS = PROJECT_RUNTIME_CLASS.replace('.', '/') + ".class";
    protected static final String PROJECT_RUNTIME_SOURCE = "src/main/java/" + PROJECT_RUNTIME_CLASS.replace('.', '/') + ".java";

    public static final String PROJECT_MODEL_CLASS = "org.drools.project.model.ProjectModel";
    public static final String PROJECT_MODEL_RESOURCE_CLASS = PROJECT_MODEL_CLASS.replace('.', '/') + ".class";
    protected static final String PROJECT_MODEL_SOURCE = "src/main/java/" + PROJECT_MODEL_CLASS.replace('.', '/') + ".java";

    private final boolean isPattern;

    public static BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> create(boolean isPattern) {
        return (internalKieModule, classLoader) -> new CanonicalModelKieProject(isPattern, internalKieModule, classLoader);
    }

    protected List<ModelBuilderImpl> modelBuilders = new ArrayList<>();

    public CanonicalModelKieProject(boolean isPattern, InternalKieModule kieModule, ClassLoader classLoader) {
        super(kieModule instanceof CanonicalKieModule ? kieModule : createFromClassLoader(classLoader, kieModule), classLoader);
        this.isPattern = isPattern;
    }

    @Override
    protected KnowledgeBuilder createKnowledgeBuilder(KieBaseModelImpl kBaseModel, InternalKieModule kModule) {
        ModelBuilderImpl modelBuilder = new ModelBuilderImpl(getBuilderConfiguration(kBaseModel, kModule), isPattern);
        modelBuilders.add(modelBuilder);
        return modelBuilder;
    }

    @Override
    public void writeProjectOutput(MemoryFileSystem trgMfs, ResultsImpl messages) {
        MemoryFileSystem srcMfs = new MemoryFileSystem();
        ModelWriter modelWriter = new ModelWriter();
        List<String> modelFiles = new ArrayList<>();
        List<String> sourceFiles = new ArrayList<>();

        for (ModelBuilderImpl modelBuilder : modelBuilders) {
            final ModelWriter.Result result = modelWriter.writeModel(srcMfs, modelBuilder.getPackageModels());
            modelFiles.addAll(result.getModelFiles());
            sourceFiles.addAll(result.getSources());
        }

        KieModuleModelMethod modelMethod = new KieModuleModelMethod(kBaseModels);
        if (!sourceFiles.isEmpty()) {
            String[] sources = sourceFiles.toArray(new String[sourceFiles.size() + 2]);

            new ModelSourceClass(
                    getInternalKieModule().getReleaseId(),
                    modelMethod,
                    modelFiles)
                    .write(srcMfs);

            sources[sources.length - 2] = PROJECT_MODEL_SOURCE;

            String projectSourceClass = new ProjectSourceClass(modelMethod).generate();
            logger.debug(projectSourceClass);
            srcMfs.write(PROJECT_RUNTIME_SOURCE, projectSourceClass.getBytes());
            sources[sources.length - 1] = PROJECT_RUNTIME_SOURCE;

            CompilationResult res = getCompiler().compile(sources, srcMfs, trgMfs, getClassLoader());

            Stream.of(res.getErrors()).collect(groupingBy(CompilationProblem::getFileName))
                    .forEach((name, errors) -> {
                        errors.forEach(messages::addMessage);
                        File srcFile = srcMfs.getFile(name);
                        if (srcFile instanceof MemoryFile) {
                            String src = new String(srcMfs.getFileContents((MemoryFile) srcFile));
                            messages.addMessage(Message.Level.ERROR, name, "Java source of " + name + " in error:\n" + src);
                        }
                    });

            for (CompilationProblem problem : res.getWarnings()) {
                messages.addMessage(problem);
            }
        } else {
            new ModelSourceClass(getInternalKieModule().getReleaseId(), modelMethod, modelFiles)
                    .write(srcMfs);
            CompilationResult res = getCompiler().compile(new String[]{PROJECT_MODEL_SOURCE}, srcMfs, trgMfs, getClassLoader());
            System.out.println(res.getErrors());
        }

        writeModelFile(modelFiles, trgMfs);
    }

    protected void writeModelFile(List<String> modelSources, MemoryFileSystem trgMfs) {
        String pkgNames = MODEL_VERSION + Drools.getFullVersion() + "\n";
        if (!modelSources.isEmpty()) {
            pkgNames += modelSources.stream().collect(Collectors.joining("\n"));
        }
        trgMfs.write(MODEL_FILE, pkgNames.getBytes());
    }
}