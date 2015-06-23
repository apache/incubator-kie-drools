/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.errors.ErrorHandler;
import org.drools.compiler.builder.impl.errors.SrcErrorHandler;
import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.commons.jci.stores.ResourceStore;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.core.common.ProjectClassLoader;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.drools.core.util.ClassUtils.convertResourceToClassName;

public class ProjectJavaCompiler {

    private final JavaCompiler compiler;

    public ProjectJavaCompiler(KnowledgeBuilderConfigurationImpl pkgConf) {
        this((JavaDialectConfiguration) pkgConf.getDialectConfiguration("java"));
    }

    public ProjectJavaCompiler(JavaDialectConfiguration configuration) {
        compiler = JavaCompilerFactory.getInstance().loadCompiler(configuration);
    }

    public List<KnowledgeBuilderResult> compileAll(ProjectClassLoader projectClassLoader,
                                                   List<String> classList,
                                                   MemoryResourceReader src) {

        List<KnowledgeBuilderResult> results = new ArrayList<KnowledgeBuilderResult>();

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
            Map<String, ErrorHandler> errorHandlerMap = new HashMap<String, ErrorHandler>();

            for ( int i = 0; i < result.getErrors().length; i++ ) {
                final CompilationProblem err = result.getErrors()[i];
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
        public void write(String pResourceName, byte[] pResourceData) {
            projectClassLoader.defineClass(convertResourceToClassName(pResourceName), pResourceName, pResourceData);
        }

        @Override
        public void write(final String resourceName, final byte[] clazzData, boolean createFolder) {
            write(resourceName, clazzData);
        }

        @Override
        public byte[] read(String pResourceName) {
            return projectClassLoader.getBytecode(pResourceName);
        }

        @Override
        public void remove(String pResourceName) {
            throw new UnsupportedOperationException("org.drools.compiler.compiler.ProjectJavaCompiler.ProjectResourceStore.remove -> TODO");
        }
    }
}
