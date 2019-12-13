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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.modelcompiler.builder.errors.CompilationProblemErrorResult;
import org.kie.internal.jci.CompilationProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.util.ClassUtils.isJboss;

public class JavaParserCompiler {

    private static final Logger logger          = LoggerFactory.getLogger(JavaParserCompiler.class);

    private static final JavaDialectConfiguration.CompilerType COMPILER_TYPE = isJboss() ?
            JavaDialectConfiguration.CompilerType.ECLIPSE :
            JavaDialectConfiguration.CompilerType.NATIVE;

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

    public static Map<String, Class<?>> compileAll(KnowledgeBuilderImpl kbuilder, ClassLoader classLoader, List<GeneratedClassWithPackage> classes) {
        if (classes == null || classes.isEmpty()) {
            return Collections.emptyMap();
        }

        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();

        String[] resources = writeModel(classes, srcMfs);
        CompilationResult resultCompilation = createEclipseCompiler().compile(resources, srcMfs, trgMfs, classLoader);
        CompilationProblem[] errors = resultCompilation.getErrors();
        if(errors.length != 0) {
            classes.forEach(c -> logger.error(c.toString()));
            for (CompilationProblem error : errors) {
                kbuilder.addBuilderResult(new CompilationProblemErrorResult(error));
            }
            return Collections.emptyMap();
        }

        InternalClassLoader internalClassLoader = AccessController.doPrivileged((PrivilegedAction<InternalClassLoader>) () -> new InternalClassLoader( classLoader, trgMfs ));

        Map<String, Class<?>> result = new HashMap<>();
        for (GeneratedClassWithPackage cls : classes) {
            final String fullClassName = cls.getPackageName() + "." + cls.getGeneratedClass().getNameAsString();
            try {
                result.put(fullClassName, Class.forName(fullClassName, true, internalClassLoader));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException( e );
            }
        }
        return result;
    }

    private static JavaCompiler createEclipseCompiler() {
        EclipseJavaCompiler javaCompiler = (EclipseJavaCompiler) new JavaCompilerFactory().loadCompiler(JavaDialectConfiguration.CompilerType.ECLIPSE, "1.8");
        javaCompiler.setPrefix("src/main/java/");
        return javaCompiler;
    }

    private static String[] writeModel(List<GeneratedClassWithPackage> classes, MemoryFileSystem srcMfs ) {
        List<String> sources = new ArrayList<>();

        for (GeneratedClassWithPackage generatedPojo : classes) {
            final String pkgName = generatedPojo.getPackageName();
            final String folderName = pkgName.replace( '.', '/' );
            final TypeDeclaration generatedClass = generatedPojo.getGeneratedClass();
            final String varsSourceName = String.format("src/main/java/%s/%s.java", folderName, generatedClass.getName());
            srcMfs.write(varsSourceName, toPojoSource(pkgName, generatedPojo.getImports(), generatedPojo.getStaticImports(), generatedClass).getBytes());
            sources.add( varsSourceName );
        }

        return sources.toArray( new String[sources.size()] );
    }

    public static String toPojoSource(String pkgName, Collection<String> imports, Collection<String> staticImports, TypeDeclaration pojo) {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration( pkgName );
        for (String i : imports) {
            cu.addImport(i);
        }
        for (String i : staticImports) {
            cu.addImport(i, true, false);
        }
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
