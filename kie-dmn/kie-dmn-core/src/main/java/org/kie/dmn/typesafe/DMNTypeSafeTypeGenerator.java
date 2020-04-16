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

package org.kie.dmn.typesafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.impl.DMNModelImpl;

public class DMNTypeSafeTypeGenerator {

    private final DMNTypeSafePackageName.Factory packageName;
    private DMNAllTypesIndex index;
    private DMNModelImpl dmnModel;

    private Map<String, TypeDefinition> types = new HashMap<>();

    public DMNTypeSafeTypeGenerator(DMNModel dmnModel, DMNAllTypesIndex index, DMNTypeSafePackageName.Factory packageName) {
        this.dmnModel = (DMNModelImpl) dmnModel;
        this.packageName = packageName;
        this.index = index;
        processTypes();
    }

    private void processTypes() {
        Set<InputDataNode> inputs = dmnModel.getInputs();
        DMNInputSetType inputSetType = new DMNInputSetType(index);
        for (InputDataNode i : inputs) {
            inputSetType.addField(i.getName(), i.getType());
        }
        inputSetType.initFields();

        types.put(inputSetType.getTypeName(), inputSetType);

        for (DMNType type : index.allTypesToGenerate()) {
            DMNDeclaredType dmnDeclaredType = new DMNDeclaredType(index, type);
            types.put(dmnDeclaredType.getTypeName(), dmnDeclaredType);
        }
    }

    public Map<String, String> generateSourceCodeOfAllTypes() {
        Map<String, String> allSources = new HashMap<>();
        DMNTypeSafePackageName packageDeclaration = this.packageName.create(dmnModel);
        for (Map.Entry<String, TypeDefinition> kv : types.entrySet()) {
            ClassOrInterfaceDeclaration generatedClass = new GeneratedClassDeclaration(kv.getValue(),
                                                                                       Collections.emptyList()).toClassDeclaration();

            CompilationUnit cu = new CompilationUnit(packageDeclaration.packageName());
            cu.addType(generatedClass);

            allSources.put(packageDeclaration.appendPackage(kv.getKey()), cu.toString());
        }
        return allSources;
    }
}
