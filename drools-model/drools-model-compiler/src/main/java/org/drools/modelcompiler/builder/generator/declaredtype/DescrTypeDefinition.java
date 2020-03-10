package org.drools.modelcompiler.builder.generator.declaredtype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeFieldDefinition;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.drools.core.util.StreamUtils.optionalToStream;

public class DescrTypeDefinition implements TypeDefinition {

    private static final String SERIAL_VERSION_UID = "serialVersionUID";

    private List<AnnotationDefinition> annotations = new ArrayList<>();

    private final PackageDescr packageDescr;

    private String javaDocComment = "";

    private final TypeDeclarationDescr typeDeclarationDescr;
    private final List<TypeFieldDefinition> typeFieldDefinition;

    public DescrTypeDefinition(PackageDescr packageDescr, TypeDeclarationDescr typeDeclarationDescr) {
        this.packageDescr = packageDescr;
        this.typeDeclarationDescr = typeDeclarationDescr;
        this.typeFieldDefinition = processFields();

        processClassAnnotations();
    }

    private void processClassAnnotations() {
        for (AnnotationDescr ann : typeDeclarationDescr.getAnnotations()) {
            if (ann.getName().equals(SERIAL_VERSION_UID)) {
                DescrFieldDefinition serialVersionField = new DescrFieldDefinition(SERIAL_VERSION_UID,
                                                                                   "long",
                                                                                   ann.getValue("value").toString());
                serialVersionField.setFinal(true);
                serialVersionField.setStatic(true);
                typeFieldDefinition.add(serialVersionField);
            }
            processAnnotations();
        }
    }

    private void processAnnotations() {
        for (AnnotationDescr ann : typeDeclarationDescr.getAnnotations()) {
            annotations.add(new DescrAnnotationDefinition(ann));
        }
    }

    @Override
    public String getTypeName() {
        return typeDeclarationDescr.getTypeName();
    }

    @Override
    public Optional<String> getSuperTypeName() {
        return ofNullable(typeDeclarationDescr.getSuperTypeName());
    }

    @Override
    public List<AnnotationDefinition> getAnnotationsToBeAdded() {
        return annotations.stream().filter(AnnotationDefinition::shouldAddAnnotation).collect(toList());
    }

    @Override
    public String getJavaDocComment() {
        return javaDocComment;
    }

    private static Optional<TypeDeclarationDescr> getSuperType(TypeDeclarationDescr typeDeclarationDescr,
                                                               PackageDescr packageDescr) {
        return ofNullable(typeDeclarationDescr.getSuperTypeName())
                .flatMap(superTypeName -> packageDescr
                        .getTypeDeclarations()
                        .stream()
                        .filter(td -> td.getTypeName().equals(superTypeName))
                        .findFirst());
    }

    @Override
    public List<TypeFieldDefinition> findInheritedDeclaredFields() {
        return findInheritedDeclaredFields(new ArrayList<>(), getSuperType(typeDeclarationDescr, packageDescr));
    }

    private List<TypeFieldDefinition> findInheritedDeclaredFields(List<TypeFieldDefinition> fields, Optional<TypeDeclarationDescr> superType) {
        superType.ifPresent(st -> {
            findInheritedDeclaredFields(fields, getSuperType(st, packageDescr));
            st.getFields()
                    .values()
                    .stream()
                    .map(DescrFieldDefinition::new)
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
        Stream<TypeFieldDefinition> keyFields = typeFieldDefinition.stream().filter(TypeFieldDefinition::isKeyField);

        Stream<TypeFieldDefinition> superTypeKieFields =
                optionalToStream(getSuperType(this.typeDeclarationDescr, packageDescr)
                                         .map(superType -> new DescrTypeDefinition(packageDescr, superType)))
                        .flatMap(t -> t.getKeyFields().stream());

        return Stream.concat(keyFields, superTypeKieFields).collect(toList());
    }

    private List<TypeFieldDefinition> processFields() {
        List<TypeFieldDescr> sortedTypeFields = typeFieldsSortedByPosition();

        List<TypeFieldDefinition> allFields = new ArrayList<>();
        for (TypeFieldDescr typeFieldDescr : sortedTypeFields) {
            allFields.add(processTypeField(findInheritedDeclaredFields().size(), typeFieldDescr));
        }
        return allFields;
    }

    private DescrFieldDefinition processTypeField(int initialPosition, TypeFieldDescr typeFieldDescr) {
        DescrFieldDefinition typeField = new DescrFieldDefinition(typeFieldDescr);

        int position = initialPosition;
        boolean hasPositionAnnotation = false;

        for (AnnotationDescr ann : typeFieldDescr.getAnnotations()) {
            DescrAnnotationDefinition annotationDefinition = new DescrAnnotationDefinition(ann);

            if(annotationDefinition.isKey()) {
                typeField.setKeyField(true);
                typeField.addAnnotation(annotationDefinition);
            } else if(annotationDefinition.isPosition()) {
                position++;
                hasPositionAnnotation = true;
                typeField.addAnnotation(annotationDefinition);
            } else if (annotationDefinition.isClassLevelAnnotation()) {
                annotations.add(annotationDefinition);
            }
        }

        if (!hasPositionAnnotation) {
            typeField.addPositionAnnotation(++position);
        }

        return typeField;
    }

}
