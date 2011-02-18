/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.commons.jci.compilers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.janino.ClassLoaderIClassLoader;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.Compiler;
import org.codehaus.janino.DebuggingInformation;
import org.codehaus.janino.FilterWarningHandler;
import org.codehaus.janino.Location;
import org.codehaus.janino.WarningHandler;
import org.codehaus.janino.Parser.ParseException;
import org.codehaus.janino.Scanner.ScanException;
import org.codehaus.janino.UnitCompiler.ErrorHandler;
import org.codehaus.janino.util.StringPattern;
import org.codehaus.janino.util.resource.Resource;
import org.codehaus.janino.util.resource.ResourceCreator;
import org.codehaus.janino.util.resource.ResourceFinder;
import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.commons.jci.readers.ResourceReader;
import org.drools.commons.jci.stores.ResourceStore;

/**
 * @author tcurdt
 */
public final class JaninoJavaCompiler extends AbstractJavaCompiler {

    private final JaninoJavaCompilerSettings defaultSettings;

    public JaninoJavaCompiler() {
        this(new JaninoJavaCompilerSettings());
    }
    
    public JaninoJavaCompiler( final JaninoJavaCompilerSettings pSettings ) {
        defaultSettings = pSettings;
    }
    
    private final static class JciResource implements Resource {

        private final String name;
        private final byte[] bytes;

        public JciResource( final String pName, final byte[] pBytes ) {
            name = pName;
            bytes = pBytes;
        }

        public String getFileName() {
            return name;
        }

        public long lastModified() {
            return 0;
        }

        public InputStream open() throws IOException {
            return new ByteArrayInputStream(bytes);
        }
    }

    private final class JciOutputStream extends ByteArrayOutputStream {

        private final String name;
        private final ResourceStore store;

        public JciOutputStream( final String pName, final ResourceStore pStore ) {
            name = pName;
            store = pStore;
        }

        public void close() throws IOException {
            super.close();

            final byte[] bytes = toByteArray();

            store.write(name, bytes);
        }
    }
    
    public CompilationResult compile( final String[] pSourceNames, final ResourceReader pResourceReader, final ResourceStore pStore, final ClassLoader pClassLoader, final JavaCompilerSettings pSettings ) {

        final Collection problems = new ArrayList();

        final StringPattern[] pattern = StringPattern.PATTERNS_NONE;

        final Compiler compiler = new Compiler(
                new ResourceFinder() {
                    public Resource findResource( final String pSourceName ) {
                        final byte[] bytes = pResourceReader.getBytes(pSourceName);

                        if (bytes == null) {
                            return null;
                        }

                        return new JciResource(pSourceName, bytes);
                    }
                },
                new ClassLoaderIClassLoader(pClassLoader),
                new ResourceFinder() {
                    public Resource findResource( final String pResourceName ) {
                        final byte[] bytes = pStore.read(pResourceName);

                        if (bytes == null) {
                            return null;
                        }

                        return new JciResource(pResourceName, bytes);
                    }
                },
                new ResourceCreator() {
                    public OutputStream createResource( final String pResourceName ) throws IOException {
                        return new JciOutputStream(pResourceName, pStore);
                    }

                    public boolean deleteResource( final String pResourceName ) {
                        pStore.remove(pResourceName);
                        return true;
                    }
                },
                pSettings.getSourceEncoding(),
                false,
                pSettings.isDebug()?DebuggingInformation.ALL:DebuggingInformation.NONE,
                new FilterWarningHandler(pattern, new WarningHandler() {
                        public void handleWarning( final String pHandle, final String pMessage, final Location pLocation ) {
                            final CompilationProblem problem = new JaninoCompilationProblem(pLocation.getFileName(), pLocation, pMessage, false);
                            if (problemHandler != null) {
                                problemHandler.handle(problem);
                            }
                            problems.add(problem);
                        }
                    })
                );

        compiler.setCompileErrorHandler(new ErrorHandler() {
            public void handleError( final String pMessage, final Location pLocation ) throws CompileException {
                final CompilationProblem problem = new JaninoCompilationProblem(pLocation.getFileName(), pLocation, pMessage, true);
                if (problemHandler != null) {
                    problemHandler.handle(problem);
                }
                problems.add(problem);
            }
        });


        final Resource[] resources = new Resource[pSourceNames.length];
        for (int i = 0; i < pSourceNames.length; i++) {
            final byte[] source = pResourceReader.getBytes(pSourceNames[i]);
            resources[i] = new JciResource(pSourceNames[i], source);
        }
        try {
            compiler.compile(resources);
        } catch ( ScanException e ) {
            problems.add(new JaninoCompilationProblem(e));
        } catch ( ParseException e ) {
            problems.add(new JaninoCompilationProblem(e));
        } catch ( IOException e ) {
            // I'm hoping the existing compiler problems handler catches these            
        } catch ( CompileException e ) {
            // I'm hoping the existing compiler problems handler catches these
        }
        
        final CompilationProblem[] result = new CompilationProblem[problems.size()];
        problems.toArray(result);
        return new CompilationResult(result);
    }

    public JavaCompilerSettings createDefaultSettings() {
        return this.defaultSettings;
    }
    
}
