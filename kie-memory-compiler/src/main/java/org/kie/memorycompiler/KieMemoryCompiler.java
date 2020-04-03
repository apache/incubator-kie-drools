/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.memorycompiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class KieMemoryCompiler {

    private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();
    private static final List<String> OPTIONS = Arrays.asList("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8");

    private KieMemoryCompiler() {
    }

    /**
     * Compile the given sources and add compiled classes to the given <code>ClassLoader</code>
     * <b>classNameSourceMap</b>' key must be the <b>FQDN</b> of the class to compile
     *
     * @param classNameSourceMap
     * @param classLoader
     * @return
     */
    public static Map<String, Class<?>> compile(Map<String, String> classNameSourceMap, ClassLoader classLoader) {
        Map<String, KieMemoryCompilerSourceCode> sourceCodes = classNameSourceMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                                               entry -> new KieMemoryCompilerSourceCode(entry.getKey(), entry.getValue())));
        KieMemoryCompilerClassLoader kieMemoryCompilerClassLoader = new KieMemoryCompilerClassLoader(classLoader);
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        KieMemoryCompilerFileManager fileManager = new KieMemoryCompilerFileManager(JAVA_COMPILER.getStandardFileManager(null, null, null), kieMemoryCompilerClassLoader);
        JavaCompiler.CompilationTask task = JAVA_COMPILER.getTask(null, fileManager, collector, OPTIONS, null, sourceCodes.values());

        boolean compilationSuccess = task.call();
        boolean hasCompilerError = collector.getDiagnostics().stream().anyMatch(d -> d.getKind().equals(Diagnostic.Kind.ERROR));
        if (!compilationSuccess || hasCompilerError) {
            compilerError(collector);
        }

        Map<String, Class<?>> toReturn = new HashMap<>();
        for (String className : sourceCodes.keySet()) {
            try {
                toReturn.put(className, kieMemoryCompilerClassLoader.loadClass(className));
            } catch (ClassNotFoundException e) {
                throw new KieMemoryCompilerException(e.getMessage(), e);
            }
        }
        return toReturn;
    }

    private static void compilerError(DiagnosticCollector<JavaFileObject> collector) {
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append("Compilation failed");
        for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
            errorBuilder.append(" file: ");
            errorBuilder.append(diagnostic.getSource());
            errorBuilder.append("\r\n");
            errorBuilder.append(diagnostic.getKind());
            errorBuilder.append("; line: ");
            errorBuilder.append(diagnostic.getLineNumber());
            errorBuilder.append("; ");
            errorBuilder.append(diagnostic.getMessage(Locale.US));
        }
        throw new KieMemoryCompilerException(errorBuilder.toString());
    }
}
