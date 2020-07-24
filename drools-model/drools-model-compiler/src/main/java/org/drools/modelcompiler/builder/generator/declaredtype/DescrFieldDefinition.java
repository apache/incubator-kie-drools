/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;

public class DescrFieldDefinition implements FieldDefinition {

    private final String fieldName;
    private final String objectType;
    private final String initExpr;

    private boolean isKeyField = false;
    private boolean createAccessors = true;
    private boolean isStatic = false;
    private boolean isFinal = false;

    private final Map<String, AnnotationDefinition> annotations = new HashMap<>();

    public DescrFieldDefinition(String fieldName, String objectType, String initExpr) {
        this.fieldName = fieldName;
        this.objectType = objectType;
        this.initExpr = initExpr;
    }

    public DescrFieldDefinition(TypeFieldDescr typeFieldDescr) {
        this(typeFieldDescr.getFieldName(),
             typeFieldDescr.getPattern().getObjectType(),
             typeFieldDescr.getInitExpr());
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getObjectType() {
        return objectType;
    }

    @Override
    public String getInitExpr() {
        return initExpr;
    }

    @Override
    public List<AnnotationDefinition> getFieldAnnotations() {
        return Arrays.asList(annotations.values().toArray(new AnnotationDefinition[0]));
    }

    public void addAnnotation(AnnotationDefinition annotationDefinition) {
        annotations.put(annotationDefinition.getName(), annotationDefinition);
    }

    public void addAnnotation(String name) {
        annotations.put(name, new DescrAnnotationDefinition(name));
    }

    public void addAnnotation(String name, String value) {
        annotations.put(name, new DescrAnnotationDefinition(name, value));
    }

    public void addPositionAnnotation(int position) {
        AnnotationDefinition annotationDefinition = DescrAnnotationDefinition.createPositionAnnotation(position);
        annotations.put(annotationDefinition.getName(), annotationDefinition);
    }

    @Override
    public boolean isKeyField() {
        return isKeyField;
    }

    public void setKeyField(Boolean keyField) {
        isKeyField = keyField;
    }

    @Override
    public boolean createAccessors() {
        return createAccessors;
    }

    public void setCreateAccessors(Boolean createAccessors) {
        this.createAccessors = createAccessors;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(Boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(Boolean aFinal) {
        isFinal = aFinal;
    }

    @Override
    public String toString() {
        return "DescrFieldDefinition{" +
                "fieldName='" + fieldName + '\'' +
                ", objectType='" + objectType + '\'' +
                ", initExpr='" + initExpr + '\'' +
                ", isKeyField=" + isKeyField +
                ", createAccessors=" + createAccessors +
                ", isStatic=" + isStatic +
                ", isFinal=" + isFinal +
                ", annotations=" + annotations +
                '}';
    }
}
