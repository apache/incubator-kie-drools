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

import com.github.javaparser.ast.body.TypeDeclaration;

public class AccumulateClassWriter {

    protected final TypeDeclaration generatedPojo;
    protected final PackageModel pkgModel;
    private final GeneratedClassWithPackage generatedClassWithPackage;
    private final String name;

    public AccumulateClassWriter(GeneratedClassWithPackage pojo, PackageModel packageModel) {
        TypeDeclaration genClass = pojo.getGeneratedClass();
        this.generatedPojo = genClass;
        this.name = genClass.getNameAsString();
        this.pkgModel = packageModel;
        this.generatedClassWithPackage = pojo;
    }

    public String getSource() {
        return JavaParserCompiler.toPojoSource(
                pkgModel.getName(),
                generatedClassWithPackage.getImports(),
                pkgModel.getStaticImports(),
                generatedClassWithPackage.getGeneratedClass());
    }

    public String getName() {
        return pkgModel.getPathName() + "/" + name + ".java";
    }
}
