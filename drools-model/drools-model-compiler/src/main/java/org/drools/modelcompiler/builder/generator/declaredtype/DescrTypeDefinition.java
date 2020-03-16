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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.drools.compiler.compiler.AnnotationDeclarationError;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeResolver;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.drools.core.util.StreamUtils.optionalToStream;
import static org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator.quote;

public class DescrTypeDefinition implements TypeDefinition {

    private static final String SERIAL_VERSION_UID = "serialVersionUID";

    private List<AnnotationDefinition> annotations = new ArrayList<>();
    private final PackageDescr packageDescr;

    private final TypeDeclarationDescr typeDeclarationDescr;
    private final List<FieldDefinition> fieldDefinition;

    private final TypeResolver typeResolver;

    private List<DroolsError> errors = new ArrayList<>();

    public DescrTypeDefinition(PackageDescr packageDescr, TypeDeclarationDescr typeDeclarationDescr, TypeResolver typeResolver) {
        this.packageDescr = packageDescr;
        this.typeDeclarationDescr = typeDeclarationDescr;
        this.typeResolver = typeResolver;
        this.fieldDefinition = processFields();

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
                fieldDefinition.add(serialVersionField);
            }
            try {
                annotations.add(DescrAnnotationDefinition.fromDescr(typeResolver, ann));
            } catch (UnkownAnnotationClassException | UnknownKeysInAnnotation e) {
                // Do not do anything
            }
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
    public List<FieldDefinition> findInheritedDeclaredFields() {
        return findInheritedDeclaredFields(new ArrayList<>(), getSuperType(typeDeclarationDescr, packageDescr));
    }

    private List<FieldDefinition> findInheritedDeclaredFields(List<FieldDefinition> fields, Optional<TypeDeclarationDescr> superType) {
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
    public List<FieldDefinition> getFields() {
        return fieldDefinition;
    }

    @Override
    public List<FieldDefinition> getKeyFields() {
        Stream<FieldDefinition> keyFields = fieldDefinition.stream().filter(FieldDefinition::isKeyField);

        Stream<FieldDefinition> superTypeKieFields =
                optionalToStream(getSuperType(this.typeDeclarationDescr, packageDescr)
                                         .map(superType -> new DescrTypeDefinition(packageDescr, superType, typeResolver)))
                        .flatMap(t -> t.getKeyFields().stream());

        return Stream.concat(keyFields, superTypeKieFields).collect(toList());
    }

    private List<FieldDefinition> processFields() {
        List<TypeFieldDescr> sortedTypeFields = typeFieldsSortedByPosition();

        int position = findInheritedDeclaredFields().size();
        List<FieldDefinition> allFields = new ArrayList<>();
        for (TypeFieldDescr typeFieldDescr : sortedTypeFields) {
            ProcessedTypeField processedTypeField = processTypeField(position, typeFieldDescr);

            allFields.add(processedTypeField.fieldDefinition);
            position = processedTypeField.position;
        }
        return allFields;
    }

    private ProcessedTypeField processTypeField(int position, TypeFieldDescr typeFieldDescr) {
        DescrFieldDefinition typeField = new DescrFieldDefinition(typeFieldDescr);

        List<DescrAnnotationDefinition> parsedAnnotations = typeFieldDescr.getAnnotations().stream()
                .map(this::createAnnotationDefinition)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        parsedAnnotations.stream().filter(a -> !a.isPosition()).forEach(a -> processDefinitions(typeField, a));

        int currentFieldPosition = setFieldPosition(position, typeField, parsedAnnotations);

        return new ProcessedTypeField(typeField, currentFieldPosition);
    }

    private void processDefinitions(DescrFieldDefinition typeField, DescrAnnotationDefinition annotationDefinition) {
        if (annotationDefinition.isKey()) {
            typeField.setKeyField(true);
            typeField.addAnnotation(annotationDefinition);
        } else if (annotationDefinition.isClassLevelAnnotation()) {
            annotations.add(new DescrAnnotationDefinition(annotationDefinition.getName(),
                                                          quote(typeField.getFieldName())));
        } else {
            typeField.addAnnotation(annotationDefinition);
        }
    }

    private int setFieldPosition(int initialPosition, DescrFieldDefinition typeField, List<DescrAnnotationDefinition> allAnnotations) {
        int currentFieldPosition = initialPosition;
        Optional<DescrAnnotationDefinition> positionAnnotation = allAnnotations
                .stream()
                .filter(DescrAnnotationDefinition::isPosition)
                .findFirst();

        if (positionAnnotation.isPresent()) {
            currentFieldPosition++;
            typeField.addAnnotation(positionAnnotation.get());
        } else {
            typeField.addPositionAnnotation(currentFieldPosition++);
        }
        return currentFieldPosition;
    }

    private Optional<DescrAnnotationDefinition> createAnnotationDefinition(AnnotationDescr ann) {
        try {
            return of(DescrAnnotationDefinition.fromDescr(typeResolver, ann));
        } catch (UnknownKeysInAnnotation e) {
            e.getValues().stream()
                    .map(p -> new AnnotationDeclarationError(ann, "Unknown annotation property " + p))
                    .forEach(errors::add);
            return empty();
        } catch (UnkownAnnotationClassException e) {
            // Do not add annotation and silently fail
            return empty();
        }
    }

    public List<DroolsError> getErrors() {
        return errors;
    }

    static class ProcessedTypeField {

        DescrFieldDefinition fieldDefinition;
        Integer position;

        public ProcessedTypeField(DescrFieldDefinition fieldDefinition, Integer position) {
            this.fieldDefinition = fieldDefinition;
            this.position = position;
        }
    }
}
