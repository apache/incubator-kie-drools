/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.common.accessor.compiler;

import java.io.IOException;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public final class StringGeneratedJavaCompilerFacade {

    private final StringGeneratedClassLoader classLoader;
    private final JavaCompiler compiler;
    private final DiagnosticCollector<JavaFileObject> diagnosticCollector;

    public StringGeneratedJavaCompilerFacade(ClassLoader loader) {
        compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Cannot find the system Java compiler.\n"
                    + "Maybe you're using the JRE without the JDK: either the classpath lacks a jar (tools.jar)"
                    + " xor the modulepath lacks a module (java.compiler).");
        }
        classLoader = new StringGeneratedClassLoader(loader);
        diagnosticCollector = new DiagnosticCollector<>();
    }

    public synchronized <T> Class<? extends T> compile(String fullClassName, String javaSource, Class<T> superType) {
        StringGeneratedSourceFileObject fileObject;
        fileObject = new StringGeneratedSourceFileObject(fullClassName, javaSource);

        JavaFileManager standardFileManager = compiler.getStandardFileManager(diagnosticCollector, null, null);
        try (StringGeneratedJavaFileManager javaFileManager = new StringGeneratedJavaFileManager(standardFileManager, classLoader)) {
            CompilationTask task = compiler.getTask(null, javaFileManager, diagnosticCollector,
                    null, null, Collections.singletonList(fileObject));
            boolean success = task.call();
            if (!success) {
                final Pattern linePattern = Pattern.compile("\n");
                String compilationMessages = diagnosticCollector.getDiagnostics().stream()
                        .map(d -> d.getKind() + ":[" + d.getLineNumber() + "," + d.getColumnNumber() +"] " + d.getMessage(null)
                        + "\n        " + (d.getLineNumber() <= 0 ? "" : linePattern.splitAsStream(javaSource).skip(d.getLineNumber() - 1).findFirst().orElse("")))
                        .collect(Collectors.joining("\n"));
                throw new IllegalStateException("The generated class (" + fullClassName + ") failed to compile.\n"
                        + compilationMessages);
            }
        } catch (IOException e) {
            throw new IllegalStateException("The generated class (" + fullClassName + ") failed to compile because the "
                    + JavaFileManager.class.getSimpleName() + " didn't close.", e);
        }
        Class<T> compiledClass;
        try {
            compiledClass = (Class<T>) classLoader.loadClass(fullClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("The generated class (" + fullClassName
                    + ") compiled, but failed to load.", e);
        }
        if (!superType.isAssignableFrom(compiledClass)) {
            throw new ClassCastException("The generated compiledClass (" + compiledClass
                    + ") cannot be assigned to the superclass/interface (" + superType + ").");
        }
        return compiledClass;
    }

}

