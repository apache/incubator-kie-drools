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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.FEELPropertyAccessible;

class DMNInputSetType implements TypeDefinition {
    List<DMNDeclaredField> fields = new ArrayList<>();

    Map<String, DMNType> fieldsKey = new HashMap<>();

    List<AnnotationDefinition> annotations = new ArrayList<>();
    private DMNAllTypesIndex index;
    private String javadoc;

    private boolean withJacksonAnnotation;

    DMNInputSetType(DMNAllTypesIndex index, boolean withJacksonAnnotation) {
        this.index = index;
        this.withJacksonAnnotation = withJacksonAnnotation;
    }

    public void addField(String key, DMNType type) {
        fieldsKey.put(key, type);
    }

    @Override
    public String getTypeName() {
        return "InputSet";
    }

    @Override
    public List<? extends FieldDefinition> getFields() {
        return fields;
    }

    public void initFields() {
        for (Map.Entry<String, DMNType> f : fieldsKey.entrySet()) {
            DMNDeclaredField dmnDeclaredField = new DMNDeclaredField(index, f, withJacksonAnnotation);
            fields.add(dmnDeclaredField);
        }
    }

    @Override
    public List<FieldDefinition> getKeyFields() {
        return Collections.emptyList();
    }

    @Override
    public Optional<String> getSuperTypeName() {
        return Optional.empty();
    }

    @Override
    public List<String> getInterfacesNames() {
        return Collections.singletonList(FEELPropertyAccessible.class.getCanonicalName());
    }

    @Override
    public List<MethodDefinition> getMethods() {
        return new FEELPropertyAccessibleImplementation(fields).getMethods();
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
