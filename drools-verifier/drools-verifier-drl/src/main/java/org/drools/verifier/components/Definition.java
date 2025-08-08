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
package org.drools.verifier.components;

import org.drools.drl.ast.descr.TypeDeclarationDescr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Definition extends PackageComponent<TypeDeclarationDescr> {

    private String typeName;
    private String superTypeName;
    private Map<String, Object> metadata = new HashMap<>();
    private Map<String, String> declaredFields = new HashMap<>();

    public Definition(TypeDeclarationDescr descr, RulePackage rulePackage) {
        super(descr, rulePackage);
        this.typeName = descr.getTypeName();
        this.superTypeName = descr.getSuperTypeName();
        initializeDeclaredFields(descr);
    }

    protected Definition(TypeDeclarationDescr descr, String packageName) {
        super(descr, packageName);
        this.typeName = descr.getTypeName();
        this.superTypeName = descr.getSuperTypeName();
        initializeDeclaredFields(descr);
    }

    private void initializeDeclaredFields(TypeDeclarationDescr descr) {
        if (descr.getFields() != null) {
            for (String fieldName : descr.getFields().keySet()) {
                String fieldType = descr.getFields().get(fieldName).getPattern().getObjectType();
                this.declaredFields.put(fieldName, fieldType);
            }
        }
    }

    @Override
    public String getPath() {
        return String.format("%s/definition[@name='%s']", 
                           getPackagePath(), 
                           getTypeName());
    }

    @Override
    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.DEFINITION;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSuperTypeName() {
        return superTypeName;
    }

    public void setSuperTypeName(String superTypeName) {
        this.superTypeName = superTypeName;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getDeclaredFields() {
        return declaredFields;
    }

    public void setDeclaredFields(Map<String, String> declaredFields) {
        this.declaredFields = declaredFields;
    }

    public boolean hasField(String fieldName) {
        return declaredFields.containsKey(fieldName);
    }

    public String getFieldType(String fieldName) {
        return declaredFields.get(fieldName);
    }

    public Set<String> getFieldNames() {
        return declaredFields.keySet();
    }

    @Override
    public String toString() {
        return "Definition{" +
                "typeName='" + typeName + '\'' +
                ", packageName='" + getPackageName() + '\'' +
                ", superTypeName='" + superTypeName + '\'' +
                ", declaredFields=" + declaredFields +
                '}';
    }
}