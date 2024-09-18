/*
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
package org.kie.kogito.codegen.usertask;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.jbpm.process.core.Work;
import org.kie.kogito.codegen.api.ConfigGenerator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;

public class UserTaskConfigGenerator implements ConfigGenerator {

    private List<Work> collectedResources;
    private TemplatedGenerator templateGenerator;

    public UserTaskConfigGenerator(KogitoBuildContext context, List<Work> collectedResources) {
        this.collectedResources = collectedResources;
        templateGenerator = TemplatedGenerator.builder()
                .withTemplateBasePath("/class-templates/usertask")
                .build(context, "UserTaskConfig");
    }

    @Override
    public String configClassName() {
        return "UserTaskConfig";
    }

    @Override
    public GeneratedFile generate() {
        CompilationUnit unit = templateGenerator.compilationUnit().get();
        String packageName = unit.getPackageDeclaration().get().getNameAsString();
        unit.getPackageDeclaration().get().setName(packageName);

        ClassOrInterfaceDeclaration clazzDeclaration = unit.findFirst(ClassOrInterfaceDeclaration.class).get();
        clazzDeclaration.setName(configClassName());

        ConstructorDeclaration declaration = clazzDeclaration.findFirst(ConstructorDeclaration.class).get();
        declaration.setName(configClassName());

        return new GeneratedFile(GeneratedFileType.SOURCE, Path.of(packageName.replaceAll("\\.", File.separator), configClassName() + ".java"), unit.toString());
    }

}
