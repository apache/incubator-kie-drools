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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.model.codegen.execmodel.generator.declaredtype.api.AnnotationDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.FieldDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.SimpleAnnotationDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.TypeDefinition;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.FEELPropertyAccessible;

abstract class AbstractDMNSetType implements TypeDefinition {

    List<DMNDeclaredField> dmnFields = new ArrayList<>();
    List<FieldDefinition> extraFields = new ArrayList<>();

    Map<String, DMNType> fieldsKey = new HashMap<>();

    List<AnnotationDefinition> annotations = new ArrayList<>();
    private DMNAllTypesIndex index;
    private String javadoc;

    private DMNStronglyCodeGenConfig codeGenConfig;

    AbstractDMNSetType(DMNAllTypesIndex index, DMNStronglyCodeGenConfig codeGenConfig) {
        this.index = index;
        this.codeGenConfig = codeGenConfig;
    }

    public void addField(String key, DMNType type) {
        fieldsKey.put(key, type);
    }

    @Override
    public List<? extends FieldDefinition> getFields() {
        List<FieldDefinition> combinedFields = new ArrayList<>();
        combinedFields.addAll(dmnFields);
        combinedFields.addAll(extraFields);
        return combinedFields;
    }

    public void initFields() {
        FieldGenStrategy fieldGenStrategy = FieldGenStrategy.getFieldGenStrategy(fieldsKey.keySet(), getTypeName());
        for (Map.Entry<String, DMNType> f : fieldsKey.entrySet()) {
            DMNDeclaredField dmnDeclaredField = new DMNDeclaredField(index, f, codeGenConfig, fieldGenStrategy);
            dmnFields.add(dmnDeclaredField);
        }
        extraFields.add(new DefinedKeySetField());
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
        return new FEELPropertyAccessibleImplementation(dmnFields, this).getMethods();
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

    class DefinedKeySetField implements FieldDefinition {

        @Override
        public String getFieldName() {
            return "definedKeySet";
        }

        @Override
        public String getObjectType() {
            return "java.util.Set<String>";
        }

        @Override
        public String getInitExpr() {
            return "new java.util.HashSet<>()";
        }

        @Override
        public boolean isKeyField() {
            return false;
        }

        @Override
        public boolean createAccessors() {
            return false;
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public List<AnnotationDefinition> getFieldAnnotations() {
            List<AnnotationDefinition> annoList = new ArrayList<>();
            if (codeGenConfig.isWithMPOpenApiAnnotation()) {
                annoList.add(new SimpleAnnotationDefinition("org.eclipse.microprofile.openapi.annotations.media.Schema").addValue("hidden", "true"));
            }
            if (codeGenConfig.isWithIOSwaggerOASv3Annotation()) {
                annoList.add(new SimpleAnnotationDefinition("io.swagger.v3.oas.annotations.media.Schema").addValue("hidden", "true"));
            }
            return annoList;
        }
    }
}
