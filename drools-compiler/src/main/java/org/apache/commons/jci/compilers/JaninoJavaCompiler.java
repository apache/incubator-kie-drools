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

package org.apache.commons.jci.compilers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jci.problems.CompilationProblem;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.ResourceStore;
import org.codehaus.janino.ClassLoaderIClassLoader;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.DebuggingInformation;
import org.codehaus.janino.Descriptor;
import org.codehaus.janino.IClass;
import org.codehaus.janino.IClassLoader;
import org.codehaus.janino.Java;
import org.codehaus.janino.Location;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.UnitCompiler;
import org.codehaus.janino.WarningHandler;
import org.codehaus.janino.Scanner.LocatedException;
import org.codehaus.janino.UnitCompiler.ErrorHandler;
import org.codehaus.janino.util.ClassFile;
import org.drools.util.ClassUtils;

/**
 * @author art@gramlich-net.com
 */
public final class JaninoJavaCompiler extends AbstractJavaCompiler {

    private class CompilingIClassLoader extends IClassLoader {

        private final Map types = new HashMap();
        private final ResourceReader resourceReader;
        private final Map classes;
        private final Collection problems = new ArrayList();

        private CompilingIClassLoader(final ResourceReader pResourceReader, final Map pClasses, final ClassLoader classLoader) {
            super( new ClassLoaderIClassLoader( classLoader ) );
            resourceReader = pResourceReader;
            classes = pClasses;
            super.postConstruct();
        }

        protected Collection getProblems() {
            return problems;
        }
        
        protected IClass findIClass(final String pType) {
            final String className = Descriptor.toClassName(pType);
            if (types.containsKey(pType)) {
                return (IClass) types.get(pType);
            }
            
            // FIXME: should not be tied to the extension            
            final String resourceNameFromClass = className.replace('.', '/') + ".java";

            final byte[] content = resourceReader.getBytes(resourceNameFromClass);
            if (content == null) {
                return null;
            }
            final Reader reader = new BufferedReader(new StringReader(new String(content)));
            Scanner scanner = null;
            try {
                scanner = new Scanner(resourceNameFromClass, reader);
                final Java.CompilationUnit unit = new Parser(scanner).parseCompilationUnit();
                final UnitCompiler uc = new UnitCompiler(unit, this);
                uc.setCompileErrorHandler(new ErrorHandler() {
                    public void handleError(final String pMessage, final Location pOptionalLocation) throws CompileException {
                        final CompilationProblem problem = new JaninoCompilationProblem(pOptionalLocation, pMessage, true);
                        if (problemHandler != null) {
                            problemHandler.handle(problem);
                        }
                        problems.add(problem);
                    }
                });
                uc.setWarningHandler(new WarningHandler() {
                    public void handleWarning(final String pHandle, final String pMessage, final Location pOptionalLocation) {
                        final CompilationProblem problem = new JaninoCompilationProblem(pOptionalLocation, pMessage, false);
                        if (problemHandler != null) {
                            problemHandler.handle(problem);
                        }
                        problems.add(problem);
                    }
                });
                
                final ClassFile[] classFiles = uc.compileUnit(DebuggingInformation.ALL);
                for (int i = 0; i < classFiles.length; i++) {
                    classes.put(classFiles[i].getThisClassName(), classFiles[i].toByteArray());
                }
                final IClass ic = uc.findClass(className);
                if (null != ic) {
                    types.put(pType, ic);
                }
                return ic;
            } catch (final LocatedException e) {
                problems.add(new JaninoCompilationProblem(e));
            } catch (final IOException e) {
                problems.add(new JaninoCompilationProblem(resourceNameFromClass, "IOException:" + e.getMessage(), true));
            } catch (final Exception e) {
                problems.add(new JaninoCompilationProblem(resourceNameFromClass, "Exception:" + e.getMessage(), true));
            } finally {
                if (scanner != null) {
                    try {
                        scanner.close();
                    } catch (IOException e) {
                    	throw new RuntimeException( "IOException occured while compiling " + className, e );
                    }
                }
            }
            return null;
        }
    }

    public CompilationResult compile( final String[] pSourceNames, final ResourceReader pResourceReader, final ResourceStore pStore, final ClassLoader pClassLoader, final JavaCompilerSettings pSettings ) {

        final Map classFilesByName = new HashMap();       
        
        final CompilingIClassLoader icl = new CompilingIClassLoader(pResourceReader, classFilesByName, pClassLoader);
        for (int i = 0; i < pSourceNames.length; i++) {
        	try {
        		icl.loadIClass(Descriptor.fromClassName(ClassUtils.convertResourceToClassName(pSourceNames[i])));
            } catch ( final ClassNotFoundException e ) {
                // @TODO: if an exception is thrown here, how do we handle it?
                e.printStackTrace();
            }        		
        }
        
        // Store all fully compiled classes
        for (Iterator i = classFilesByName.entrySet().iterator(); i.hasNext();) {
            final Map.Entry entry = (Map.Entry)i.next();
            final String clazzName = (String)entry.getKey(); 
            pStore.write(ClassUtils.convertClassToResourcePath(clazzName), (byte[])entry.getValue());
        }
        
        final Collection problems = icl.getProblems();
        final CompilationProblem[] result = new CompilationProblem[problems.size()];
        problems.toArray(result);
        return new CompilationResult(result);
    }

    public JavaCompilerSettings createDefaultSettings() {
        // FIXME
        return null;
    }
    
}
