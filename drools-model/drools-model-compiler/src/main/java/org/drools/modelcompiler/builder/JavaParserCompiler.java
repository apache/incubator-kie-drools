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

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.printer.PrettyPrinter;
import org.drools.javaparser.printer.PrettyPrinterConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JavaParserCompiler {

    private static final JavaDialectConfiguration.CompilerType COMPILER_TYPE = JavaDialectConfiguration.CompilerType.NATIVE;

    private static final JavaCompiler JAVA_COMPILER = createCompiler();

    private static final PrettyPrinter PRETTY_PRINTER = createPrettyPrinter();

    private static JavaCompiler createCompiler() {
        JavaCompiler javaCompiler = JavaCompilerFactory.getInstance().loadCompiler( COMPILER_TYPE, "1.8" );
        if (COMPILER_TYPE == JavaDialectConfiguration.CompilerType.ECLIPSE) {
            ((EclipseJavaCompiler )javaCompiler).setPrefix( "src/main/java/" );
        }
        return javaCompiler;
    }

    public static JavaCompiler getCompiler() {
        return JAVA_COMPILER;
    }

    private static PrettyPrinter createPrettyPrinter() {
        PrettyPrinterConfiguration config = new PrettyPrinterConfiguration();
        config.setColumnAlignParameters( true );
        config.setColumnAlignFirstMethodChain( true );
        return new PrettyPrinter( config );
    }

    public static PrettyPrinter getPrettyPrinter() {
        return PRETTY_PRINTER;
    }

    public static Map<String, Class<?>> compileAll( ClassLoader classLoader, String pkgName, List<GeneratedClassWithPackage> classes ) {
        if (classes == null || classes.isEmpty()) {
            return Collections.emptyMap();
        }

        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();

        List<ClassOrInterfaceDeclaration> allGeneratedClasses = classes.stream().map(GeneratedClassWithPackage::getGeneratedClass).collect(Collectors.toList());
        String[] resources = writeModel(pkgName, allGeneratedClasses, srcMfs );
        CompilationResult resultCompilation = getCompiler().compile(resources, srcMfs, trgMfs, classLoader);
        CompilationProblem[] errors = resultCompilation.getErrors();
        if(errors.length != 0) {
            throw new RuntimeException("Error during compilation " + Arrays.stream(errors).map(CompilationProblem::getMessage).collect(Collectors.joining("\n")));
        }

        InternalClassLoader internalClassLoader = new InternalClassLoader( classLoader, trgMfs );

        Map<String, Class<?>> result = new HashMap<>();
        for (GeneratedClassWithPackage cls : classes) {
            String fullClassName = pkgName + "." + cls.getGeneratedClass().getNameAsString();
            try {
                result.put(fullClassName, Class.forName(fullClassName, true, internalClassLoader));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException( e );
            }
        }
        return result;
    }

    private static String[] writeModel( String pkgName, List<ClassOrInterfaceDeclaration> classes, MemoryFileSystem srcMfs ) {
        List<String> sources = new ArrayList<>();

        String folderName = pkgName.replace( '.', '/' );

        for (ClassOrInterfaceDeclaration generatedPojo : classes) {
            final String source = toPojoSource( pkgName, generatedPojo );
            final String varsSourceName = "src/main/java/" + folderName + "/" + generatedPojo.getName() + ".java";
            srcMfs.write( varsSourceName, source.getBytes() );
            sources.add( varsSourceName );
        }

        return sources.toArray( new String[sources.size()] );
    }

    private static String toPojoSource(String pkgName, ClassOrInterfaceDeclaration pojo) {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration( pkgName );
        cu.addType(pojo);
        return getPrettyPrinter().print(cu);
    }

    static class InternalClassLoader extends ClassLoader {
        private final MemoryFileSystem mfs;

        public InternalClassLoader(ClassLoader parent, MemoryFileSystem mfs) {
            super(parent);
            this.mfs = mfs;
        }

        public Class<?> loadClass(String className) throws ClassNotFoundException {
            try {
                return super.loadClass( className );
            } catch (ClassNotFoundException cnfe) { }

            String fileName = className.replace( '.', '/' ) + ".class";
            byte[] bytes = mfs.getBytes(fileName);

            if (bytes == null) {
                throw new ClassNotFoundException(className);
            }

            return defineClass(className, bytes, 0, bytes.length);
        }
    }
}
