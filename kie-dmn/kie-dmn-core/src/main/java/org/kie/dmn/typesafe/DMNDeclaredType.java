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
package org.kie.dmn.typesafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.util.StringUtils;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.AnnotationDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.FieldDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.TypeDefinition;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;

class DMNDeclaredType implements TypeDefinition {

    private DMNAllTypesIndex index;
    private final DMNType dmnType;
    List<DMNDeclaredField> fields = new ArrayList<>();
    List<AnnotationDefinition> annotations = new ArrayList<>();
    private String javadoc;
    private DMNStronglyCodeGenConfig codeGenConfig;

    DMNDeclaredType(DMNAllTypesIndex index, DMNType dmnType, DMNStronglyCodeGenConfig codeGenConfig) {
        this.index = index;
        this.dmnType = dmnType;
        this.codeGenConfig = codeGenConfig;
        initFields();
    }

    @Override
    public String getTypeName() {
        return asJavaSimpleName(dmnType);
    }

    public static String asJavaSimpleName(DMNType dmnType) {
        String sn = StringUtils.ucFirst(CodegenStringUtil.escapeIdentifier(dmnType.getName()));
        if (DMNTypeUtils.isInnerComposite(dmnType)) {
            String parentSN = asJavaSimpleName(DMNTypeUtils.getBelongingType(dmnType));
            sn = parentSN + "_" + sn;
        }
        return sn;
    }

    @Override
    public List<? extends FieldDefinition> getFields() {
        return fields;
    }

    private void initFields() {
        Map<String, DMNType> dmnFields = dmnType.getFields();
        FieldGenStrategy fieldGenStrategy = FieldGenStrategy.getFieldGenStrategy(dmnFields.keySet(), getTypeName());
        for (Map.Entry<String, DMNType> field : dmnFields.entrySet()) {
            DMNDeclaredField dmnDeclaredField = new DMNDeclaredField(index, field, codeGenConfig, fieldGenStrategy);
            fields.add(dmnDeclaredField);
        }
    }

    @Override
    public List<FieldDefinition> getKeyFields() {
        return Collections.emptyList();
    }

    @Override
    public Optional<String> getSuperTypeName() {
        return Optional.ofNullable(dmnType.getBaseType())
                .map(DMNType::getName)
                .map(StringUtils::ucFirst)
                .filter(index::isIndexedClass);
    }

    @Override
    public List<String> getInterfacesNames() {
        return Collections.singletonList(FEELPropertyAccessible.class.getCanonicalName());
    }

    @Override
    public List<MethodDefinition> getMethods() {
        return new FEELPropertyAccessibleImplementation(fields, this).getMethods();
    }

    @Override
    public List<AnnotationDefinition> getAnnotationsToBeAdded() {
        return annotations;
    }

    @Override
    public List<FieldDefinition> findInheritedDeclaredFields() {
        return Collections.emptyList();
    }

    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
    }

    @Override
    public Optional<String> getJavadoc() {
        return Optional.of(this.javadoc);
    }
}
