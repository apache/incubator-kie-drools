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
package org.kie.efesto.common.utils;

import java.io.InputStream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.util.FileUtils.getInputStreamFromFileNameAndClassLoader;

public class JavaParserUtils {

    private static final Logger logger = LoggerFactory.getLogger(JavaParserUtils.class.getName());
    public static final String MAIN_CLASS_NOT_FOUND = "Main class not found";

    private JavaParserUtils() {
    }

    /**
     * @param className
     * @param packageName
     * @param javaTemplate   the name of the <b>file</b> to be used as template source
     * @param modelClassName the name of the class used in the provided template
     * @return
     */
    public static CompilationUnit getCompilationUnit(final String className,
                                                     final String packageName,
                                                     final String javaTemplate,
                                                     final String modelClassName) {
        logger.trace("getCompilationUnit {} {}", className, packageName);
        CompilationUnit templateCU = getFromFileName(javaTemplate);
        CompilationUnit toReturn = templateCU.clone();
        if (packageName != null && !packageName.isEmpty()) {
            toReturn.setPackageDeclaration(packageName);
        }
        ClassOrInterfaceDeclaration modelTemplate = toReturn.getClassByName(modelClassName)
                .orElseThrow(() -> new KieEfestoCommonException(MAIN_CLASS_NOT_FOUND + ": " + modelClassName));
        modelTemplate.setName(className);
        return toReturn;
    }

    /**
     * Return the fully qualified name of the generated class.
     * It throws <code>KiePMMLException</code> if the package name is missing
     *
     * @param cu
     * @return
     */
    public static String getFullClassName(final CompilationUnit cu) {
        String packageName = cu.getPackageDeclaration()
                .orElseThrow(() -> new KieEfestoCommonException("Missing package declaration for " + cu))
                .getName().asString();
        String className = cu.getType(0).getName().asString();
        return packageName + "." + className;
    }

    private static CompilationUnit getFromFileName(String fileName) {
        InputStream resource = getInputStreamFromFileNameAndClassLoader(fileName,
                                                                        JavaParserUtils.class.getClassLoader())
                .orElseThrow(() -> new KieEfestoCommonException(String.format("Failed to find InputStream for %s",
                                                                              fileName)));
        try {
            return StaticJavaParser.parse(resource);
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to parse %s due to %s", fileName,
                                                             e.getMessage()), e);
        }
    }
}
