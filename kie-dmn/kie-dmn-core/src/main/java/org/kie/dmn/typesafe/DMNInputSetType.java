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

    DMNInputSetType(DMNAllTypesIndex index) {
        this.index = index;
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
            DMNDeclaredField dmnDeclaredField = new DMNDeclaredField(index, f);
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
        return new FeelPropertyTemplate(fields, "InputSet").getMethods();
    }

    @Override
    public List<AnnotationDefinition> getAnnotationsToBeAdded() {
        return annotations;
    }

    @Override
    public List<FieldDefinition> findInheritedDeclaredFields() {
        return Collections.emptyList();
    }
}
