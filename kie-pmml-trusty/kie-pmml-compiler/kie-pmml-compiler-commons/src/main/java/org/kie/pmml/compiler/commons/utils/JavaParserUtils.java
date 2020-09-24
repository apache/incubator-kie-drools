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
package org.kie.pmml.compiler.commons.utils;

import java.io.InputStream;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.kie.pmml.commons.exceptions.ExternalException;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to provide shared, helper methods to be invoked by model-specific
 * <b>code-generation</b>
 */
public class JavaParserUtils {

    private static final Logger logger = LoggerFactory.getLogger(JavaParserUtils.class);
    public static final String MAIN_CLASS_NOT_FOUND = "Main class not found";

    private JavaParserUtils() {
    }

    public static CompilationUnit getFromFileName(String fileName) {
        try {
            final InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            return StaticJavaParser.parse(resource);
        } catch (ParseProblemException e) {
            throw new KiePMMLInternalException(String.format("Failed to parse %s due to %s", fileName, e.getMessage()), e);
        } catch (Exception e) {
            throw new ExternalException(String.format("Failed to read %s due to %s", fileName, e.getMessage()), e);
        }
    }

    /**
     * @param className
     * @param packageName
     * @param javaTemplate the name of the <b>file</b> to be used as template source
     * @param modelClassName the name of the class used in the provided template
     * @return
     */
    public static CompilationUnit getKiePMMLModelCompilationUnit(final String className,
                                                                 final String packageName,
                                                                 final String javaTemplate,
                                                                 final String modelClassName) {
        logger.trace("getKiePMMLModelCompilationUnit {} {}", className, packageName);
        CompilationUnit templateCU = getFromFileName(javaTemplate);
        CompilationUnit toReturn = templateCU.clone();
        if (packageName != null && !packageName.isEmpty()) {
            toReturn.setPackageDeclaration(packageName);
        }
        ClassOrInterfaceDeclaration modelTemplate = toReturn.getClassByName(modelClassName)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + modelClassName));
        modelTemplate.setName(className);
        return toReturn;
    }

    /**
     * Return the fully qualified name of the generated class.
     * It throws <code>KiePMMLException</code> if the package name is missing
     * @param cu
     * @return
     *
     */
    public static String getFullClassName(final CompilationUnit cu) {
        String packageName = cu.getPackageDeclaration()
                .orElseThrow(() ->new KiePMMLException("Missing package declaration for " + cu.toString()))
                .getName().asString();
        String className = cu.getType(0).getName().asString();
        return packageName + "." + className;
    }
}
