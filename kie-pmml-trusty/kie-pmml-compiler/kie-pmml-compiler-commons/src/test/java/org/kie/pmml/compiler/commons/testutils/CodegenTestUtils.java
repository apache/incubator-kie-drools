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
package org.kie.pmml.compiler.commons.testutils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.junit.Assert.fail;

/**
 * Utility methods for Codegen-related tests
 */
public class CodegenTestUtils {

    public static void commonValidateCompilation(BlockStmt body, List<Parameter> parameters) {
        ClassOrInterfaceDeclaration classOrInterfaceType = new ClassOrInterfaceDeclaration();
        classOrInterfaceType.setName("CommCodeTest");
        MethodDeclaration toAdd = new MethodDeclaration();
        toAdd.setType("void");
        toAdd.setName("TestingMethod");
        toAdd.setParameters(NodeList.nodeList(parameters));
        toAdd.setBody(body);
        classOrInterfaceType.addMember(toAdd);
        CompilationUnit compilationUnit =  StaticJavaParser.parse("");
        compilationUnit.setPackageDeclaration("org.kie.pmml.compiler.commons.utils");
        compilationUnit.addType(classOrInterfaceType);
        Map<String, String> sourcesMap = Collections.singletonMap("org.kie.pmml.compiler.commons.utils.CommCodeTest", compilationUnit.toString());
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static void commonValidateCompilation(MethodDeclaration methodDeclaration) {
        ClassOrInterfaceDeclaration classOrInterfaceType = new ClassOrInterfaceDeclaration();
        classOrInterfaceType.setName("CommCodeTest");
        classOrInterfaceType.addMember(methodDeclaration);
        CompilationUnit compilationUnit =  StaticJavaParser.parse("");
        compilationUnit.setPackageDeclaration("org.kie.pmml.compiler.commons.utils");
        compilationUnit.addType(classOrInterfaceType);
        Map<String, String> sourcesMap = Collections.singletonMap("org.kie.pmml.compiler.commons.utils.CommCodeTest", compilationUnit.toString());
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
