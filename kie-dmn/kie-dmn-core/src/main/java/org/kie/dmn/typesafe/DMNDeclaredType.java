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
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodWithStringBody;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.FEELPropertyAccessible;

class DMNDeclaredType implements TypeDefinition {

    private final DMNType dmnType;

    DMNDeclaredType(DMNType dmnType) {
        this.dmnType = dmnType;
    }

    @Override
    public String getTypeName() {
        return StringUtils.ucFirst(dmnType.getName());
    }

    @Override
    public List<FieldDefinition> getFields() {
        List<FieldDefinition> fields = new ArrayList<>();
        Map<String, DMNType> dmnFields = dmnType.getFields();
        for (Map.Entry<String, DMNType> f : dmnFields.entrySet()) {
            DMNDeclaredField dmnDeclaredField = new DMNDeclaredField(f);
            fields.add(dmnDeclaredField);
        }
        return fields;
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
        List<MethodDefinition> allMethods = new ArrayList<>();


        String allFeelPropertiesBody = " { return java.util.Collections.emptyMap(); } ";

        MethodWithStringBody  allFEELProperties = new MethodWithStringBody(
                "allFEELProperties", "java.util.Map<String, Object>", allFeelPropertiesBody
        );


        allMethods.add(allFEELProperties);

        return allMethods;
    }

    @Override
    public List<AnnotationDefinition> getAnnotationsToBeAdded() {
        return Collections.emptyList();
    }

    @Override
    public List<FieldDefinition> findInheritedDeclaredFields() {
        return Collections.emptyList();
    }
}
