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
package org.drools.model.codegen.execmodel;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.compiler.JavaDialectConfiguration;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.CompilationProblemAdapter;
import org.drools.model.codegen.execmodel.errors.CompilationProblemErrorResult;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.JavaCompiler;
import org.drools.util.PortablePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.compiler.JavaDialectConfiguration.createDefaultCompiler;
import static org.drools.util.ClassUtils.isJboss;

public class JavaParserCompiler {

    private static final Logger logger = LoggerFactory.getLogger(JavaParserCompiler.class);

    private static final JavaCompiler JAVA_COMPILER = isJboss() ?
                                                      JavaDialectConfiguration.createEclipseCompiler() :
                                                      JavaDialectConfiguration.createDefaultCompiler();

    private static final DefaultPrettyPrinter PRETTY_PRINTER = createPrettyPrinter();

    public static JavaCompiler getCompiler() {
        return JAVA_COMPILER;
    }

    private static DefaultPrettyPrinter createPrettyPrinter() {
        DefaultPrinterConfiguration config = new DefaultPrinterConfiguration();
        config.addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.COLUMN_ALIGN_PARAMETERS, true));
        config.addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.COLUMN_ALIGN_FIRST_METHOD_CHAIN, true));
        return new DefaultPrettyPrinter( config );
    }

    public static DefaultPrettyPrinter getPrettyPrinter() {
        return PRETTY_PRINTER;
    }

    public static Map<String, Class<?>> compileAll(BuildResultCollector resultAccumulator, ClassLoader classLoader, List<GeneratedClassWithPackage> classes) {
        if (classes == null || classes.isEmpty()) {
            return Collections.emptyMap();
        }

        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();

        String[] resources = writeModel(classes, srcMfs);
        CompilationResult resultCompilation = createDefaultCompiler().compile(resources, srcMfs, trgMfs, classLoader);
        CompilationProblem[] errors = resultCompilation.getErrors();
        if (errors.length != 0) {
            classes.forEach(c -> logger.error(c.toString()));
            for (CompilationProblem error : errors) {
                resultAccumulator.addBuilderResult(new CompilationProblemErrorResult(new CompilationProblemAdapter( error )));
            }
            return Collections.emptyMap();
        }

        InternalClassLoader internalClassLoader = AccessController.doPrivileged((PrivilegedAction<InternalClassLoader>) () -> new InternalClassLoader( classLoader, trgMfs ));
        return loadClasses(getClassNames(classLoader, trgMfs), internalClassLoader);
    }

    private static Map<String, Class<?>> loadClasses(List<String> classNames, InternalClassLoader internalClassLoader) {
        Map<String, Class<?>> result = new HashMap<>();
        for (String className : classNames) {
            try {
                result.put(className, Class.forName(className, true, internalClassLoader));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException( e );
            }
        }
        return result;
    }

    private static List<String> getClassNames(ClassLoader classLoader, MemoryFileSystem trgMfs) {
        List<String> classNames = new ArrayList<>();
        for (Map.Entry<PortablePath, byte[]> entry : trgMfs.getMap().entrySet()) {
            String className = entry.getKey().asClassName();
            classNames.add(className);
            if (classLoader instanceof ProjectClassLoader && ((ProjectClassLoader) classLoader).isDynamic()) {
                ((ProjectClassLoader) classLoader).storeClass(className, entry.getValue());
            }
        }
        return classNames;
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
            cu.getImports().add( new ImportDeclaration(new Name(i), false, false ) );
        }
        for (String i : staticImports) {
            cu.getImports().add( new ImportDeclaration(new Name(i), true, false ) );
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
