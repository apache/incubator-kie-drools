package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

public class DescrDeclaredTypeDefinition implements TypeDefinition {

    private static final Map<String, Class<?>> predefinedClassLevelAnnotation = new HashMap<>();

    private List<AnnotationDefinition> annotations = new ArrayList<>();

    private final PackageDescr packageDescr;

    static {
        predefinedClassLevelAnnotation.put("role", Role.class);
        predefinedClassLevelAnnotation.put("duration", Duration.class);
        predefinedClassLevelAnnotation.put("expires", Expires.class);
        predefinedClassLevelAnnotation.put("timestamp", Timestamp.class);
    }

    private final TypeDeclarationDescr typeDeclarationDescr;
    private final List<TypeFieldDefinition> typeFieldDefinition;

    public DescrDeclaredTypeDefinition(PackageDescr packageDescr, TypeDeclarationDescr typeDeclarationDescr) {
        this.packageDescr = packageDescr;
        this.typeDeclarationDescr = typeDeclarationDescr;
        this.typeFieldDefinition = processFields();
    }

    @Override
    public String getTypeName() {
        return typeDeclarationDescr.getTypeName();
    }

    @Override
    public String getSuperTypeName() {
        return typeDeclarationDescr.getSuperTypeName();
    }

    @Override
    public List<AnnotationDefinition> getAnnotations() {
        return annotations;
    }

    @Override
    public List<AnnotationDefinition> getSoftAnnotations() {
        return new ArrayList<>();
    }

    private Optional<TypeDeclarationDescr> getSuperType(TypeDeclarationDescr typeDeclarationDescr) {
        if (getSuperTypeName() != null) {
            return packageDescr
                    .getTypeDeclarations()
                    .stream()
                    .filter(td -> {
                        String superTypeName = typeDeclarationDescr.getSuperTypeName();
                        return td.getTypeName().equals(superTypeName);
                    })
                    .findFirst();
        }
        return Optional.empty();
    }

    @Override
    public List<TypeFieldDefinition> findInheritedDeclaredFields() {
        return findInheritedDeclaredFields(new ArrayList<>(), getSuperType(typeDeclarationDescr));
    }

    private List<TypeFieldDefinition> findInheritedDeclaredFields(List<TypeFieldDefinition> fields, Optional<TypeDeclarationDescr> superType) {
        superType.ifPresent(st -> {
            findInheritedDeclaredFields(fields, getSuperType(st));
            st.getFields()
                    .values()
                    .stream()
                    .map(DescrDeclearedTypeFieldDefinition::new)
                    .forEach(fields::add);
        });
        return fields;
    }

    private List<TypeFieldDescr> typeFieldsSortedByPosition() {
        Map<String, TypeFieldDescr> typeFields = typeDeclarationDescr.getFields();
        TypeFieldDescr[] sortedTypes = new TypeFieldDescr[typeFields.size()];

        List<TypeFieldDescr> nonPositionalFields = new ArrayList<>();
        for (TypeFieldDescr descr : typeFields.values()) {
            AnnotationDescr ann = descr.getAnnotation("Position");
            if (ann == null) {
                nonPositionalFields.add(descr);
            } else {
                int pos = Integer.parseInt(ann.getValue().toString());
                sortedTypes[pos] = descr;
            }
        }

        int counter = 0;
        for (TypeFieldDescr descr : nonPositionalFields) {
            for (; sortedTypes[counter] != null; counter++) {
                ;
            }
            sortedTypes[counter++] = descr;
        }

        return Arrays.asList(sortedTypes);
    }

    @Override
    public List<TypeFieldDefinition> getFields() {
        return typeFieldDefinition;
    }

    @Override
    public List<TypeFieldDefinition> getKeyFields() {
        return typeFieldDefinition.stream().filter(TypeFieldDefinition::isKeyField).collect(Collectors.toList());
    }

    private List<TypeFieldDefinition> processFields() {
        List<TypeFieldDescr> sortedTypeFields = typeFieldsSortedByPosition();

        List<TypeFieldDefinition> allFields = new ArrayList<>();
        int position = findInheritedDeclaredFields().size();

        for (TypeFieldDescr typeFieldDescr : sortedTypeFields) {
            DescrDeclearedTypeFieldDefinition f = new DescrDeclearedTypeFieldDefinition(typeFieldDescr);

            allFields.add(f);
            boolean hasPositionAnnotation = false;
            for (AnnotationDescr ann : typeFieldDescr.getAnnotations()) {
                if (ann.getName().equalsIgnoreCase("key")) {
                    f.setKeyField(true);
                    f.addAnnotation(Key.class.getName());
                } else if (ann.getName().equalsIgnoreCase("position")) {
                    f.addAnnotation(Position.class.getName(), String.valueOf(ann.getValue()));
                    position++;
                    hasPositionAnnotation = true;
                } else if (ann.getName().equalsIgnoreCase("duration") || ann.getName().equalsIgnoreCase("expires") || ann.getName().equalsIgnoreCase("timestamp")) {
                    Class<?> annotationClass = predefinedClassLevelAnnotation.get(ann.getName().toLowerCase());
                    String annFqn = annotationClass.getCanonicalName();
                    annotations.add(new DescrDeclaredTypeAnnotationDefinition(annFqn, ""));
                }
            }

            if (!hasPositionAnnotation) {
                f.addAnnotation(Position.class.getName(), String.valueOf(position++));
            }
        }
        return allFields;
    }

}
