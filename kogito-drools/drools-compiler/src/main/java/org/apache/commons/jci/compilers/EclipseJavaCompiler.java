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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.jci.problems.CompilationProblem;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.ResourceStore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

public final class EclipseJavaCompiler extends AbstractJavaCompiler {

    private final Map settings;

    public EclipseJavaCompiler() {
        this(new EclipseJavaCompilerSettings());
    }

    public EclipseJavaCompiler(final Map pSettings) {
        settings = pSettings;
    }

    public EclipseJavaCompiler(final EclipseJavaCompilerSettings pSettings) {
        settings = pSettings.getMap();
    }

    final class CompilationUnit implements ICompilationUnit {

        final private String clazzName;
        final private String fileName;
        final private char[] typeName;
        final private char[][] packageName;
        final private ResourceReader reader;

        CompilationUnit(final ResourceReader pReader, final String pClazzName) {
            reader = pReader;
            clazzName = pClazzName;
            clazzName.replace('.', '/');
            fileName = clazzName.replace('.', '/') + ".java";
            int dot = clazzName.lastIndexOf('.');
            if (dot > 0) {
                typeName = clazzName.substring(dot + 1).toCharArray();
            } else {
                typeName = clazzName.toCharArray();
            }
            final StringTokenizer izer = new StringTokenizer(clazzName, ".");
            packageName = new char[izer.countTokens() - 1][];
            for (int i = 0; i < packageName.length; i++) {
                packageName[i] = izer.nextToken().toCharArray();
            }
        }

        public char[] getFileName() {
            return fileName.toCharArray();
        }

        public char[] getContents() {
            return new String(reader.getBytes(fileName)).toCharArray();
        }

        public char[] getMainTypeName() {
            return typeName;
        }

        public char[][] getPackageName() {
            return packageName;
        }
    }

    public org.apache.commons.jci.compilers.CompilationResult compile(
            final String[] pClazzNames,
            final ResourceReader pReader,
            final ResourceStore pStore,
            final ClassLoader classLoader
            ) {
        
        final Map settingsMap = settings;
        final Set clazzIndex = new HashSet();
        ICompilationUnit[] compilationUnits = new ICompilationUnit[pClazzNames.length];
        for (int i = 0; i < compilationUnits.length; i++) {
            final String clazzName = pClazzNames[i];
            compilationUnits[i] = new CompilationUnit(pReader, clazzName);
            clazzIndex.add(clazzName);
        }

        final IErrorHandlingPolicy policy = DefaultErrorHandlingPolicies.proceedWithAllProblems();
        final IProblemFactory problemFactory = new DefaultProblemFactory(Locale.getDefault());
        final INameEnvironment nameEnvironment = new INameEnvironment() {

            public NameEnvironmentAnswer findType( final char[][] compoundTypeName ) {
                final StringBuffer result = new StringBuffer();
                for (int i = 0; i < compoundTypeName.length; i++) {
                    if (i != 0) {
                        result.append('.');
                    }
                    result.append(compoundTypeName[i]);
                }
                return findType(result.toString());
            }

            public NameEnvironmentAnswer findType( final char[] typeName, final char[][] packageName ) {
                final StringBuffer result = new StringBuffer();
                for (int i = 0; i < packageName.length; i++) {
                    result.append(packageName[i]);
                    result.append('.');
                }
                result.append(typeName);
                return findType(result.toString());
            }

            private NameEnvironmentAnswer findType( final String clazzName ) {
                byte[] clazzBytes = pStore.read(clazzName);
                if (clazzBytes != null) {
                    // log.debug("loading from store " + clazzName);
                    final char[] fileName = clazzName.toCharArray();
                    try {
                        final ClassFileReader classFileReader = new ClassFileReader(clazzBytes, fileName, true);
                        return new NameEnvironmentAnswer(classFileReader, null);
                    } catch (final ClassFormatException e) {
                    	// @TODO: we need to handle this better, maybe a runtime exception?
                    	e.printStackTrace();                    	
                        //log.error("wrong class format", e);
                    }
                } else {
                    if (pReader.isAvailable(clazzName.replace('.', '/') + ".java")) {
                        ICompilationUnit compilationUnit = new CompilationUnit(pReader, clazzName);
                        return new NameEnvironmentAnswer(compilationUnit, null);
                    }

                    final String resourceName = clazzName.replace('.', '/') + ".class";
                    final InputStream is = classLoader.getResourceAsStream(resourceName);
                    if (is != null) {
                        final byte[] buffer = new byte[8192];
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream(buffer.length);
                        int count;
                        try {
                            while ((count = is.read(buffer, 0, buffer.length)) > 0) {
                                baos.write(buffer, 0, count);
                            }
                            baos.flush();
                            clazzBytes = baos.toByteArray();
                            final char[] fileName = clazzName.toCharArray();
                            ClassFileReader classFileReader =
                                new ClassFileReader(clazzBytes, fileName, true);
                            return new NameEnvironmentAnswer(classFileReader, null);
                        } catch (final IOException e) {
                        	// @TODO: we need to handle this better, maybe a runtime exception?
                        	e.printStackTrace();                        	
                            // log.error("could not read class", e);
                        } catch (final ClassFormatException e) {
                        	// @TODO: we need to handle this better, maybe a runtime exception?
                        	e.printStackTrace();                        	
                            // log.error("wrong class format", e);
                        } finally {
                            try {
                                baos.close();
                            } catch (final IOException oe) {
                            	// @TODO: we need to handle this better, maybe a runtime exception?
                            	oe.printStackTrace();                            	
                                //log.error("could not close output stream", oe);
                            }
                            try {
                                is.close();
                            } catch (final IOException ie) {
                            	// @TODO: we need to handle this better, maybe a runtime exception?
                            	ie.printStackTrace();                            	
                                //log.error("could not close input stream", ie);
                            }
                        }
                    }
                }
                return null;
            }

            private boolean isPackage( final String clazzName ) {
                final String resourceName = clazzName.replace('.', '/') + ".class";
                final URL resource = classLoader.getResource(resourceName);
                return resource == null;
            }

            public boolean isPackage( char[][] parentPackageName, char[] packageName ) {
                final StringBuffer result = new StringBuffer();
                if (parentPackageName != null) {
                    for (int i = 0; i < parentPackageName.length; i++) {
                        if (i != 0) {
                            result.append('.');
                        }
                        result.append(parentPackageName[i]);
                    }
                }
                if (Character.isUpperCase(packageName[0])) {
                    return false;
                }
                if (parentPackageName != null && parentPackageName.length > 0) {
                    result.append('.');
                }
                result.append(packageName);
                return isPackage(result.toString());
            }

            public void cleanup() {
            }
        };

        final Collection problems = new ArrayList();
        final ICompilerRequestor compilerRequestor = new ICompilerRequestor() {
            public void acceptResult( CompilationResult result ) {
                if (result.hasProblems()) {
                    final IProblem[] iproblems = result.getProblems();
                    for (int i = 0; i < iproblems.length; i++) {
                        final IProblem iproblem = iproblems[i];
                        final CompilationProblem problem = new EclipseCompilationProblem(iproblem); 
                        if (problemHandler != null) {
                            problemHandler.handle(problem);
                        }
                        problems.add(problem);
                    }
                }
                if (!result.hasErrors()) {
                    final ClassFile[] clazzFiles = result.getClassFiles();
                    for (int i = 0; i < clazzFiles.length; i++) {
                        final ClassFile clazzFile = clazzFiles[i];
                        final char[][] compoundName = clazzFile.getCompoundName();
                        final StringBuffer clazzName = new StringBuffer();
                        for (int j = 0; j < compoundName.length; j++) {
                            if (j != 0) {
                                clazzName.append('.');
                            }
                            clazzName.append(compoundName[j]);
                        }
                        pStore.write(clazzName.toString(), clazzFile.getBytes());
                    }
                }
            }
        };

        final Compiler compiler =
            new Compiler(nameEnvironment, policy, settingsMap, compilerRequestor, problemFactory, false);

        compiler.compile(compilationUnits);

        final CompilationProblem[] result = new CompilationProblem[problems.size()];
        problems.toArray(result);
        return new org.apache.commons.jci.compilers.CompilationResult(result);
    }
}
