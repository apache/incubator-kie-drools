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

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.sun.tools.corba.se.idl.ExceptionEntry;
import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.kie.internal.jci.CompilationProblem;;
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

import static java.util.stream.Collectors.groupingBy;

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
        if (getInternalKieModule().getKieModuleModel() != kBaseModel.getKModule()) {
            // if the KieBase belongs to a different kmodule it is not necessary to build it
            return null;
        }
        ModelBuilderImpl modelBuilder = new ModelBuilderImpl(getBuilderConfiguration( kBaseModel, kModule ), isPattern);
        modelBuilders.add(modelBuilder);
        return modelBuilder;
    }

    public static class ByteClassLoader extends ClassLoader {
        private final Map<String, byte[]> extraClassDefs;

        public ByteClassLoader(ClassLoader parent, Map<String, byte[]> extraClassDefs) {
            this.extraClassDefs = new HashMap<String, byte[]>(extraClassDefs);
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.remove(name);
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }

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

                Map<String, byte[]> compiledClasses = new HashMap<>();

                trgMfs
                        .getFileNames()
                        .stream()
                        .filter(fn -> fn.endsWith(".class"))
                        .forEach(f -> {
                            MemoryFile file = (MemoryFile) trgMfs.getFile(f);
                            byte[] fileContents = trgMfs.getFileContents(file);
                            String className = f.replace("/", ".").replace(".class", "");
                            compiledClasses.put(className, fileContents);
                        });

                ByteClassLoader byteClassLoader = new ByteClassLoader(getClassLoader(), compiledClasses);

                for(PackageModel pm : modelBuilder.getPackageModels()) {
                    pm.validateConsequence(byteClassLoader, messages);
                }

                Stream.of(res.getErrors()).collect(groupingBy(CompilationProblem::getFileName))
                    .forEach( (name, errors) -> {
                        errors.forEach( messages::addMessage );
                        File srcFile = srcMfs.getFile( name );
                        if ( srcFile instanceof MemoryFile ) {
                            String src = new String ( srcMfs.getFileContents( ( MemoryFile ) srcFile ) );
                            messages.addMessage( Message.Level.ERROR, name, "Java source of " + name + " in error:\n" + src);
                        }
                    } );

                for (CompilationProblem problem : res.getWarnings()) {
                    messages.addMessage(problem);
                }
            }
        }

        modelWriter.writeModelFile(modelFiles, trgMfs);
    }

    @Override
    protected boolean compileIncludedKieBases() {
        return false;
    }
}