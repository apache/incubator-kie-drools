package org.kie.dmn.typesafe;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.core.util.StringUtils;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.kie.dmn.api.core.DMNType;

public class DMNDeclaredField implements FieldDefinition {

    private String fieldName;
    private DMNType fieldType;

    DMNDeclaredField(Map.Entry<String, DMNType> dmnType) {
        this.fieldName = dmnType.getKey();
        this.fieldType = dmnType.getValue();
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getObjectType() {
        if (fieldType.isCollection()) {
            String typeName = fieldType.getBaseType().getName();
            return String.format("java.util.Collection<%s>", StringUtils.ucFirst(typeName));
        }
        return StringUtils.ucFirst(fieldType.getName());
    }

    @Override
    public String getInitExpr() {
        return null;
    }

    @Override
    public List<AnnotationDefinition> getAnnotations() {
        return Collections.emptyList();
    }

    @Override
    public boolean isKeyField() {
        return false;
    }

    @Override
    public boolean createAccessors() {
        return true;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
