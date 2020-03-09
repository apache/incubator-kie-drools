package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.descr.TypeFieldDescr;

public class DescrDeclearedTypeFieldDefinition implements TypeFieldDefinition {

    private final String fieldName;
    private final String objectType;
    private final String initExpr;

    private Boolean isKeyField = false;

    private final Map<String, AnnotationDefinition> annotations = new HashMap<>();

    public DescrDeclearedTypeFieldDefinition(String fieldName, String objectType, String initExpr) {
        this.fieldName = fieldName;
        this.objectType = objectType;
        this.initExpr = initExpr;
    }

    public DescrDeclearedTypeFieldDefinition(TypeFieldDescr typeFieldDescr) {
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
    public List<AnnotationDefinition> getAnnotations() {
        return Arrays.asList(annotations.values().toArray(new AnnotationDefinition[0]));
    }

    @Override
    public void addAnnotation(String name) {
        annotations.put(name, new DescrDeclaredTypeAnnotationDefinition(name, ""));
    }

    @Override
    public void addAnnotation(String name, String value) {
        annotations.put(name, new DescrDeclaredTypeAnnotationDefinition(name, "", value));
    }

    @Override
    public boolean isKeyField() {
        return isKeyField;
    }

    public void setKeyField(Boolean keyField) {
        isKeyField = keyField;
    }
}
