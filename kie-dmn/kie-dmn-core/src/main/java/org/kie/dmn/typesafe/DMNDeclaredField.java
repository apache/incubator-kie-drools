package org.kie.dmn.typesafe;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.core.util.StringUtils;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.kie.dmn.api.core.DMNType;

public class DMNDeclaredField implements FieldDefinition {

    private final Map.Entry<String, DMNType> dmnType;

    DMNDeclaredField(Map.Entry<String, DMNType> dmnType) {
        this.dmnType = dmnType;
    }

    @Override
    public String getFieldName() {
        return dmnType.getKey();
    }

    @Override
    public String getObjectType() {

        return StringUtils.ucFirst(dmnType.getValue().getName());
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
