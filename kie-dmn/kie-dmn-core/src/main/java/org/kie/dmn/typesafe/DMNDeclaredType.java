package org.kie.dmn.typesafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.core.util.StringUtils;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.FEELPropertyAccessible;

class DMNDeclaredType implements TypeDefinition {

    private DMNAllTypesIndex index;
    private final DMNType dmnType;
    private final String originalTypeName;
    List<DMNDeclaredField> fields = new ArrayList<>();
    List<AnnotationDefinition> annnotations = new ArrayList<>();

    DMNDeclaredType(DMNAllTypesIndex index, DMNType dmnType) {
        this.index = index;
        this.dmnType = dmnType;
        this.originalTypeName = dmnType.getName();
        initFields();
    }

    @Override
    public String getTypeName() {
        return StringUtils.ucFirst(dmnType.getName());
    }

    @Override
    public List<? extends FieldDefinition> getFields() {
        return fields;
    }

    private void initFields() {
        Map<String, DMNType> dmnFields = dmnType.getFields();
        for (Map.Entry<String, DMNType> field : dmnFields.entrySet()) {
            DMNDeclaredField dmnDeclaredField = new DMNDeclaredField(index, field);
            fields.add(dmnDeclaredField);
        }
    }

    @Override
    public List<FieldDefinition> getKeyFields() {
        return Collections.emptyList();
    }

    @Override
    public Optional<String> getSuperTypeName() {
        return Optional.ofNullable(dmnType.getBaseType()).map(DMNType::getName);
    }

    @Override
    public List<String> getInterfacesNames() {
        return Collections.singletonList(FEELPropertyAccessible.class.getCanonicalName());
    }

    @Override
    public List<MethodDefinition> getMethods() {
        return new FeelPropertyTemplate(fields, originalTypeName).getMethods();
    }

    @Override
    public List<AnnotationDefinition> getAnnotationsToBeAdded() {
        return annnotations;
    }

    @Override
    public List<FieldDefinition> findInheritedDeclaredFields() {
        return Collections.emptyList();
    }
}
