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
package org.drools.compiler.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.errors.ErrorHandler;
import org.drools.compiler.builder.impl.errors.SrcErrorHandler;
import org.drools.compiler.kie.builder.impl.CompilationProblemAdapter;
import org.drools.util.PortablePath;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.jci.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerFactory;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.resources.MemoryResourceReader;
import org.kie.memorycompiler.resources.ResourceStore;

import static org.drools.util.ClassUtils.convertResourceToClassName;

public class ProjectJavaCompiler {

    private final JavaCompiler compiler;

    public ProjectJavaCompiler(KnowledgeBuilderConfigurationImpl pkgConf) {
        this(( JavaConfiguration ) pkgConf.getDialectConfiguration("java"));
    }

    public ProjectJavaCompiler(JavaConfiguration configuration) {
        compiler = JavaCompilerFactory.loadCompiler(configuration);
    }

    public List<KnowledgeBuilderResult> compileAll( ProjectClassLoader projectClassLoader,
                                                    List<String> classList,
                                                    MemoryResourceReader src) {

        List<KnowledgeBuilderResult> results = new ArrayList<>();

        if ( classList.isEmpty() ) {
            return results;
        }
        final String[] classes = new String[classList.size()];
        classList.toArray( classes );

        CompilationResult result = compiler.compile( classes,
                                                     src,
                                                     new ProjectResourceStore(projectClassLoader),
                                                     projectClassLoader );

        if ( result.getErrors().length > 0 ) {
            Map<String, ErrorHandler> errorHandlerMap = new HashMap<>();

            for ( int i = 0; i < result.getErrors().length; i++ ) {
                final CompilationProblem err = new CompilationProblemAdapter( result.getErrors()[i] );
                ErrorHandler handler = errorHandlerMap.get( err.getFileName() );
                if (handler == null) {
                    handler = new SrcErrorHandler("Src compile error");
                    errorHandlerMap.put(err.getFileName(), handler);
                }
                handler.addError( err );
            }

            for (ErrorHandler handler : errorHandlerMap.values()) {
                if (handler.isInError()) {
                    results.add(handler.getError());
                }
            }
        }

        return results;
    }

    private static class ProjectResourceStore implements ResourceStore {

        private final ProjectClassLoader projectClassLoader;

        private ProjectResourceStore(ProjectClassLoader projectClassLoader) {
            this.projectClassLoader = projectClassLoader;
        }

        @Override
        public void write(PortablePath resourcePath, byte[] pResourceData) {
            projectClassLoader.defineClass(convertResourceToClassName(resourcePath.asString()), resourcePath.asString(), pResourceData);
        }

        @Override
        public void write(PortablePath resourcePath, final byte[] clazzData, boolean createFolder) {
            write(resourcePath.asString(), clazzData);
        }

        @Override
        public byte[] read(PortablePath resourcePath) {
            return projectClassLoader.getBytecode(resourcePath.asString());
        }

        @Override
        public void remove(PortablePath resourcePath) {
            throw new UnsupportedOperationException("org.drools.compiler.compiler.ProjectJavaCompiler.ProjectResourceStore.remove -> TODO");
        }
    }
}
