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
package org.kie.pmml.models.regression.compiler.utils;

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

import org.kie.pmml.commons.exceptions.KiePMMLInternalException;

public class KiePMMLRegressionCompiler {

    private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();
    private static final List<String> OPTIONS = Arrays.asList("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8");

    /**
     * Compile the given sources and add compiled classes to the given <code>ClassLoader</code>
     * <b>classNameSourceMap</b>' key must be the <b>FQDN</b> of the class to compile
     * @param classNameSourceMap
     * @param classLoader
     * @return
     */
    public static Map<String, Class<?>> compile(Map<String, String> classNameSourceMap, ClassLoader classLoader) {
        Map<String, KiePMMLSourceCode> sourceCodes = classNameSourceMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                                     entry -> new KiePMMLSourceCode(entry.getKey(), entry.getValue())));
        KiePMMLClassLoader kiePMMLClassLoader = new KiePMMLClassLoader(classLoader);
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        KiePMMLFileManager fileManager = new KiePMMLFileManager(JAVA_COMPILER.getStandardFileManager(null, null, null), kiePMMLClassLoader);
        JavaCompiler.CompilationTask task = JAVA_COMPILER.getTask(null, fileManager, collector, OPTIONS, null, sourceCodes.values());
        boolean result = task.call();
        if (!result || !collector.getDiagnostics().isEmpty()) {
            StringBuilder errorBuilder = new StringBuilder();
            errorBuilder.append("Compilation failed");
            for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
                errorBuilder.append("\r\n");
                errorBuilder.append(diagnostic.getKind());
                errorBuilder.append("; line: ");
                errorBuilder.append(diagnostic.getLineNumber());
                errorBuilder.append("; ");
                errorBuilder.append(diagnostic.getMessage(Locale.US));
            }
            throw new KiePMMLInternalException(errorBuilder.toString());
        }
        Map<String, Class<?>> toReturn = new HashMap<>();
        for (String className : sourceCodes.keySet()) {
            try {
                toReturn.put(className, kiePMMLClassLoader.loadClass(className));
            } catch (ClassNotFoundException e) {
                throw new KiePMMLInternalException(e.getMessage(), e);
            }
        }
        return toReturn;
    }
}
