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
package org.drools.ecj;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.drools.util.ClassUtils;
import org.drools.util.IoUtils;
import org.drools.util.PortablePath;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
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
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.kie.memorycompiler.AbstractJavaCompiler;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.resources.ResourceReader;
import org.kie.memorycompiler.resources.ResourceStore;

/**
 * Eclipse compiler implementation
 */
public final class EclipseJavaCompiler extends AbstractJavaCompiler {
    
    private String sourceFolder = "";

    private final EclipseJavaCompilerSettings defaultSettings;

    public EclipseJavaCompiler() {
        this(new EclipseJavaCompilerSettings(), "");
    }

    public EclipseJavaCompiler( final Map pSettings ) {
        defaultSettings = new EclipseJavaCompilerSettings(pSettings);
    }

    public EclipseJavaCompiler( final EclipseJavaCompilerSettings pSettings, String sourceFolder ) {
        defaultSettings = pSettings;
        this.sourceFolder = sourceFolder;
    }

    @Override
    public void setSourceFolder( String sourceFolder ) {
        this.sourceFolder = sourceFolder;
    }

    public String getPathName(String fullPath) {
        if ( sourceFolder.length() == 0 ) {
            return fullPath;
        }
        
        if ( fullPath.charAt( 0 )  == '/') {
             return fullPath.substring( sourceFolder.length() + 1 );
        } else {
            return fullPath.substring( sourceFolder.length() );
        }
    }

    final class CompilationUnit implements ICompilationUnit {

        final private String fsFileName;
        final private String clazzName;
        final private String fileName;
        final private char[] typeName;
        final private char[][] packageName;
        final private ResourceReader reader;

        CompilationUnit( final ResourceReader pReader, final String pSourceFile ) {
            reader = pReader;

            fsFileName = pSourceFile;

            clazzName = ClassUtils.convertResourceToClassName( decode(getPathName( pSourceFile )) );
            
            fileName = decode(pSourceFile);
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

        private String decode( final String path ) {
            try {
                return URLDecoder.decode( path, "UTF-8" );
            } catch ( UnsupportedEncodingException e ) {
                return path;
            }
        }

        public char[] getFileName() {
            return fileName.toCharArray();
        }

        public char[] getContents() {
            final byte[] content = reader.getBytes(fsFileName);

            if (content == null) {
                return null;
            }

            return new String(content, IoUtils.UTF8_CHARSET).toCharArray();
        }

        public char[] getMainTypeName() {
            return typeName;
        }

        public char[][] getPackageName() {
            return packageName;
        }

        public boolean ignoreOptionalProblems() {
            return true;
        }
    }


    public CompilationResult compile(
            final String[] pSourceFiles,
            final ResourceReader pReader,
            final ResourceStore pStore,
            final ClassLoader pClassLoader,
            final JavaCompilerSettings pSettings
            ) {


        final Collection problems = new ArrayList();

        final ICompilationUnit[] compilationUnits = new ICompilationUnit[pSourceFiles.length];
        for (int i = 0; i < compilationUnits.length; i++) {
            final PortablePath sourceFile = PortablePath.of(pSourceFiles[i]);

            if (pReader.isAvailable(sourceFile)) {
                compilationUnits[i] = new CompilationUnit(pReader, sourceFile.asString());
            } else {
                // log.error("source not found " + sourceFile);

                final CompilationProblem problem = new CompilationProblem() {

                    public int getEndColumn() {
                        return 0;
                    }

                    public int getEndLine() {
                        return 0;
                    }

                    public String getFileName() {
                        return sourceFile.asString();
                    }

                    public String getMessage() {
                        return "Source " + sourceFile.asString() + " could not be found";
                    }

                    public int getStartColumn() {
                        return 0;
                    }

                    public int getStartLine() {
                        return 0;
                    }

                    public boolean isError() {
                        return true;
                    }

                    public String toString() {
                        return getMessage();
                    }
                };

                problems.add(problem);
            }
        }

        if (problems.size() > 0) {
            final CompilationProblem[] result = new CompilationProblem[problems.size()];
            problems.toArray(result);
            return new CompilationResult(result);
        }

        final IErrorHandlingPolicy policy = DefaultErrorHandlingPolicies.proceedWithAllProblems();
        final IProblemFactory problemFactory = new DefaultProblemFactory(Locale.getDefault());
        final INameEnvironment nameEnvironment = new INameEnvironment() {

            public NameEnvironmentAnswer findType( final char[][] pCompoundTypeName ) {
                final StringBuilder result = new StringBuilder();
                for (int i = 0; i < pCompoundTypeName.length; i++) {
                    if (i != 0) {
                        result.append('.');
                    }
                    result.append(pCompoundTypeName[i]);
                }

                return findType(result.toString());
            }

            public NameEnvironmentAnswer findType( final char[] pTypeName, final char[][] pPackageName ) {
                final StringBuilder result = new StringBuilder();
                for (char[] chars : pPackageName) {
                    result.append(chars);
                    result.append('.');
                }

                result.append(pTypeName);
                return findType(result.toString());
            }

            private NameEnvironmentAnswer findType( final String pClazzName ) {

                final String resourceName = ClassUtils.convertClassToResourcePath( pClazzName);

                final byte[] clazzBytes = pStore.read( resourceName );
                if (clazzBytes != null) {
                    try {
                        return createNameEnvironmentAnswer(pClazzName, clazzBytes);
                    } catch (final ClassFormatException e) {
                        throw new RuntimeException( "ClassFormatException in loading class '" + pClazzName + "' with JCI." );
                    }
                }

                try (InputStream is = pClassLoader.getResourceAsStream(resourceName)) {
                    if (is == null) {
                        return null;
                    }

                    if ( ClassUtils.isCaseSenstiveOS() ) {
                        // check it really is a class, this issue is due to windows case sensitivity issues for the class org.kie.Process and path org/droosl/process
                        try {
                            pClassLoader.loadClass( pClazzName );
                        } catch ( ClassNotFoundException | NoClassDefFoundError e ) {
                            return null;
                        }
                    }

                    final byte[] buffer = new byte[8192];
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream(buffer.length)) {
                        int count;
                        while ((count = is.read(buffer, 0, buffer.length)) > 0) {
                            baos.write(buffer, 0, count);
                        }
                        baos.flush();
                        return createNameEnvironmentAnswer(pClazzName, baos.toByteArray());
                    }
                } catch ( final IOException e ) {
                    throw new RuntimeException( "could not read class",
                                                e );
                } catch ( final ClassFormatException e ) {
                    throw new RuntimeException( "wrong class format",
                                                e );
                }
            }

            private NameEnvironmentAnswer createNameEnvironmentAnswer(final String pClazzName, final byte[] clazzBytes) throws ClassFormatException {                
                final char[] fileName = pClazzName.toCharArray();
                final ClassFileReader classFileReader = new ClassFileReader(clazzBytes, fileName, true);
                return new NameEnvironmentAnswer(classFileReader, null);
            }

            private boolean isSourceAvailable(final String pClazzName, final ResourceReader pReader) {
                // FIXME: this should not be tied to the extension
                final String javaSource = pClazzName.replace('.', '/') + ".java";
                final String classSource = pClazzName.replace('.', '/') + ".class";
                return pReader.isAvailable( sourceFolder + javaSource ) || pReader.isAvailable( sourceFolder + classSource );
            }

            private boolean isPackage( final String pClazzName ) {
                try (InputStream is = pClassLoader.getResourceAsStream(ClassUtils.convertClassToResourcePath(pClazzName))) {
                    if (is != null) {
                        if (ClassUtils.isWindows() || ClassUtils.isOSX()) {
                            // check it really is a class, this issue is due to windows case sensitivity issues for the class org.kie.Process and path org/droosl/process

                            try {
                                Class cls = pClassLoader.loadClass(pClazzName);
                                if (cls != null) {
                                    return false;
                                }
                            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                                return true;
                            }
                        }
                    }
                    return is == null && !isSourceAvailable(pClazzName, pReader);
                } catch (IOException e) {
                    throw new RuntimeException("Cannot open or close resource stream!", e);
                }
            }

            public boolean isPackage( char[][] parentPackageName, char[] pPackageName ) {
                final StringBuilder result = new StringBuilder();
                if (parentPackageName != null) {
                    for (int i = 0; i < parentPackageName.length; i++) {
                        if (i != 0) {
                            result.append('.');
                        }
                        result.append(parentPackageName[i]);
                    }
                }

                if (parentPackageName != null && parentPackageName.length > 0) {
                    result.append('.');
                }
                result.append(pPackageName);
                return isPackage(result.toString());
            }

            public void cleanup() {
            }
        };

        final ICompilerRequestor compilerRequestor = pResult -> {
            if (pResult.hasProblems()) {
                final IProblem[] iproblems = pResult.getProblems();
                for (final IProblem iproblem : iproblems) {
                    final CompilationProblem problem = new EclipseCompilationProblem(iproblem);
                    problems.add(problem);
                }
            }
            if (!pResult.hasErrors()) {
                final ClassFile[] clazzFiles = pResult.getClassFiles();
                for (final ClassFile clazzFile : clazzFiles) {
                    final char[][] compoundName = clazzFile.getCompoundName();
                    final StringBuilder clazzName = new StringBuilder();
                    for (int j = 0; j < compoundName.length; j++) {
                        if (j != 0) {
                            clazzName.append('.');
                        }
                        clazzName.append(compoundName[j]);
                    }
                    pStore.write(clazzName.toString().replace('.', '/') + ".class", clazzFile.getBytes());
                }
            }
        };

        final Map settingsMap = new EclipseJavaCompilerSettings(pSettings).toNativeSettings();
        CompilerOptions compilerOptions = new CompilerOptions(settingsMap);
        compilerOptions.parseLiteralExpressionsAsConstants = false;
        
        final Compiler compiler = new Compiler(nameEnvironment, policy, compilerOptions, compilerRequestor, problemFactory);

        if ( true ) { //JavaCompiler.DUMP_GENERATED_CLASSES ) {
            dumpUnits( compilationUnits, pReader );
        }

        compiler.compile(compilationUnits);

        final CompilationProblem[] result = new CompilationProblem[problems.size()];
        problems.toArray(result);
        return new CompilationResult(result);
    }

    private void dumpUnits( ICompilationUnit[] compilationUnits, ResourceReader reader ) {
        for (ICompilationUnit unit : compilationUnits) {
            String name = ( (CompilationUnit) unit ).fileName;
            String source = new String( reader.getBytes( name ) );
//            System.out.println(source);
//            try {
//                IoUtils.write( new java.io.File(name.replace( '/', '.' )), reader.getBytes( name ) );
//            } catch (IOException e) {
//                throw new RuntimeException( e );
//            }
        }
    }

    public JavaCompilerSettings createDefaultSettings() {
        return this.defaultSettings;
    }
}
