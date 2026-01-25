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
package org.drools.verifier.visitor;

import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.verifier.components.Definition;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.data.VerifierData;

import java.util.List;
import java.util.Map;

public class TypeDeclarationDescrVisitor {

    private final VerifierData data;
    private final RulePackage rulePackage;

    public TypeDeclarationDescrVisitor(VerifierData data, RulePackage rulePackage) {
        this.data = data;
        this.rulePackage = rulePackage;
    }

    public void visit(List<TypeDeclarationDescr> typeDeclarationDescrs) {
        for (TypeDeclarationDescr typeDeclaration : typeDeclarationDescrs) {
            // Create Definition component for DRL type definitions
            Definition definition = new Definition(typeDeclaration, rulePackage);
            data.add(definition);
            Import objectImport = data.getImportByName(typeDeclaration.getTypeName());
            String objectTypeName;
            if (objectImport == null) {
                objectTypeName = typeDeclaration.getTypeName();
            } else {
                objectTypeName = objectImport.getName();
            }

            ObjectType objectType = this.data.getObjectTypeByFullName(objectTypeName);

            if (objectType == null) {
                objectType = new ObjectType(typeDeclaration);
                objectType.setName(typeDeclaration.getTypeName());
                objectType.setFullName(typeDeclaration.getTypeName());
                data.add(objectType);
            }

            for (String fieldName : typeDeclaration.getFields().keySet()) {

                Field field = data.getFieldByObjectTypeAndFieldName(objectType.getFullName(),
                        fieldName);
                if (field == null) {
                    field = ObjectTypeFactory.createField(typeDeclaration.getFields().get(fieldName),fieldName,
                            objectType);
                    field.setFieldType(typeDeclaration.getFields().get(fieldName).getPattern().getObjectType());
                    data.add(field);
                }
            }

            for (AnnotationDescr annDescr : typeDeclaration.getAnnotations()) {
                Map<String, Object> values = typeDeclaration.getAnnotation(annDescr.getName()).getValueMap();
                for (String value : values.keySet()) {
                    objectType.getMetadata().put(annDescr.getName(), value);
                    definition.getMetadata().put(annDescr.getName(), values.get(value));
                }
            }
        }
    }


}
