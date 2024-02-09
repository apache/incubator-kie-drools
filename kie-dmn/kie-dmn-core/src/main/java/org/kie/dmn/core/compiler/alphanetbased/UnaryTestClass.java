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
package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;

import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;

public class UnaryTestClass {
    private final String input;
    private final DMNFEELHelper feel;
    private final CompilerContext compilerContext;
    private final Type type;

    public UnaryTestClass(String input,
                          DMNFEELHelper feel,
                          CompilerContext compilerContext,
                          Type type) {
        this.input = input;
        this.feel = feel;
        this.compilerContext = compilerContext;
        this.type = type;
    }

    public void compileUnaryTestAndAddTo(Map<String, String> allGeneratedSources,
                                         String className,
                                         String classNameWithPackage,
                                         String packageName) {
        ClassOrInterfaceDeclaration sourceCode = feel.generateUnaryTestsSource(
                compilerContext,
                input,
                type,
                false);

        replaceSimpleNameWith(sourceCode, "TemplateCompiledFEELUnaryTests", className);

        sourceCode.setName(className);

        CompilationUnit cu = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = cu.addClass(className);
        classOrInterfaceDeclaration.replace(sourceCode);

        allGeneratedSources.put(classNameWithPackage, cu.toString());
    }
}
