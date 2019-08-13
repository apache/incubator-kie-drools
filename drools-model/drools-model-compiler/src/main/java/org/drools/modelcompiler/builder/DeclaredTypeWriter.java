/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class DeclaredTypeWriter {

    protected final ClassOrInterfaceDeclaration generatedPojo;
    protected final PackageModel pkgModel;
    private final String name;

    public DeclaredTypeWriter(ClassOrInterfaceDeclaration generatedPojo, PackageModel pkgModel) {
        this.generatedPojo = generatedPojo;
        this.name = generatedPojo.getNameAsString();
        this.pkgModel = pkgModel;
    }

    public String getSource() {
        String source = JavaParserCompiler.toPojoSource(
                pkgModel.getName(),
                pkgModel.getImports(),
                pkgModel.getStaticImports(),
                generatedPojo);
        pkgModel.logRule(source);
        return source;
    }

    public String getName() {
        return pkgModel.getPathName() + "/" + name + ".java";
    }
}
