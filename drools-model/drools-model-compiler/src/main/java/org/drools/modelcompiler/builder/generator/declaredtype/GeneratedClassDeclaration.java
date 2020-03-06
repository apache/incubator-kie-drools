/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;
import static org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator.quote;

class GeneratedClassDeclaration {

    private static final String VALUE = "value";
    static final String OVERRIDE = "Override";

    private Map<String, Class<?>> predefinedClassLevelAnnotation;

    private GenerationResult generationResult;
    private final TypeDefinition typeDefinition;
    private TypeResolver typeResolver;
    private GeneratedHashcode generatedHashcode;
    private GeneratedToString generatedToString;
    private GeneratedEqualsMethod generatedEqualsMethod;
    private ClassOrInterfaceDeclaration generatedClass;
    private final Collection<TypeDefinition> allTypeDefinitions;
    private final Collection<Class<?>> markerInterfaceAnnotations;

    GeneratedClassDeclaration(GenerationResult generationResult,
                              TypeDefinition typeDefinition,
                              TypeResolver typeResolver,
                              Map<String, Class<?>> predefinedClassLevelAnnotation,
                              Collection<TypeDefinition> allTypeDefinitions, Collection<Class<?>> markerInterfaceAnnotations) {

        this.generationResult = generationResult;
        this.typeDefinition = typeDefinition;
        this.typeResolver = typeResolver;
        this.predefinedClassLevelAnnotation = predefinedClassLevelAnnotation;
        this.allTypeDefinitions = allTypeDefinitions;
        this.markerInterfaceAnnotations = markerInterfaceAnnotations;
    }

    ClassOrInterfaceDeclaration toClassDeclaration() {
        String generatedClassName = typeDefinition.getTypeName();
        generatedClass = createBasicDeclaredClass(generatedClassName);

        Collection<TypeFieldDefinition> inheritedFields = findInheritedDeclaredFields();
        if (inheritedFields.isEmpty() && typeDefinition.getFields().isEmpty()) {
            generatedClass.addMember(new GeneratedToString(generatedClassName).method());
            return generatedClass;
        } else {
            return generateFullClass(generatedClassName, inheritedFields);
        }
    }

    private ClassOrInterfaceDeclaration createBasicDeclaredClass(String generatedClassName) {
        ClassOrInterfaceDeclaration basicDeclaredClass = new ClassOrInterfaceDeclaration(
                nodeList(Modifier.publicModifier())
                , false
                , generatedClassName);

        basicDeclaredClass.addImplementedType(Serializable.class.getName()); // Ref: {@link org.drools.core.factmodel.DefaultBeanClassBuilder} by default always receive is Serializable.
        processAnnotations(basicDeclaredClass);

        markerInterfaceAnnotations.forEach(basicDeclaredClass::addImplementedType);
        basicDeclaredClass.addConstructor(Modifier.publicModifier().getKeyword()); // No-args ctor
        return basicDeclaredClass;
    }

    private ClassOrInterfaceDeclaration generateFullClass(String generatedClassName, Collection<TypeFieldDefinition> inheritedFields) {
        boolean hasSuper = typeDefinition.getSuperTypeName() != null;
        if (hasSuper) {
            Optional<Class<?>> optResolvedSuper = typeResolver.resolveType(typeDefinition.getSuperTypeName());
            optResolvedSuper.ifPresent(resolvedSuper -> {
                if (resolvedSuper.isInterface()) {
                    generatedClass.addImplementedType(typeDefinition.getSuperTypeName());
                } else {
                    generatedClass.addExtendedType(typeDefinition.getSuperTypeName());
                }
            });

            if (!optResolvedSuper.isPresent()) {
                generatedClass.addExtendedType(typeDefinition.getSuperTypeName());
            }
        }

        LinkedHashMap<String, TypeFieldDefinition> sortedTypeFields = typeFieldsSortedByPosition();

        generatedHashcode = new GeneratedHashcode(hasSuper);
        generatedToString = new GeneratedToString(generatedClassName);
        generatedEqualsMethod = new GeneratedEqualsMethod(generatedClassName, hasSuper);

        GeneratedConstructor fullArgumentConstructor = GeneratedConstructor.factory(generatedClass, sortedTypeFields);

        List<TypeFieldDefinition> keyFields = processTypeFields(inheritedFields, sortedTypeFields);

        fullArgumentConstructor.generateConstructor(inheritedFields, keyFields);

        if (!keyFields.isEmpty()) {
            generatedClass.addMember(generatedEqualsMethod.method());
            generatedClass.addMember(generatedHashcode.method());
        }

        generatedClass.addMember(generatedToString.method());
        return generatedClass;
    }

    private List<TypeFieldDefinition> processTypeFields(Collection<TypeFieldDefinition> inheritedFields, Map<String, TypeFieldDefinition> typeFields) {
        List<TypeFieldDefinition> keyFields = new ArrayList<>();
        int position = inheritedFields.size();
        for (TypeFieldDefinition typeFieldDescr : typeFields.values()) {
            String fieldName = typeFieldDescr.getFieldName();
            Type returnType = parseType(typeFieldDescr.getPattern().getObjectType());

            FieldDeclaration field = typeFieldDescr.getInitExpr() == null ?
                    generatedClass.addField(returnType, fieldName, Modifier.privateModifier().getKeyword()) :
                    generatedClass.addFieldWithInitializer(returnType, fieldName, parseExpression(typeFieldDescr.getInitExpr()), Modifier.privateModifier().getKeyword());
            field.createSetter();
            MethodDeclaration getter = field.createGetter();

            generatedToString.add(format("+ {0}+{1}", quote(fieldName + "="), fieldName));

            boolean hasPositionAnnotation = false;
            for (AnnotationDefinition ann : typeFieldDescr.getAnnotations()) {
                if (ann.getName().equalsIgnoreCase("key")) {
                    keyFields.add(typeFieldDescr);
                    field.addAnnotation(Key.class.getName());
                    generatedEqualsMethod.add(getter, fieldName);
                    generatedHashcode.addHashCodeForField(fieldName, getter.getType());
                } else if (ann.getName().equalsIgnoreCase("position")) {
                    field.addAndGetAnnotation(Position.class.getName()).addPair(VALUE, "" + ann.getValue());
                    hasPositionAnnotation = true;
                    position++;
                } else if (ann.getName().equalsIgnoreCase("duration") || ann.getName().equalsIgnoreCase("expires") || ann.getName().equalsIgnoreCase("timestamp")) {
                    Class<?> annotationClass = predefinedClassLevelAnnotation.get(ann.getName().toLowerCase());
                    String annFqn = annotationClass.getCanonicalName();
                    NormalAnnotationExpr annExpr = generatedClass.addAndGetAnnotation(annFqn);
                    annExpr.addPair(VALUE, quote(fieldName));
                } else {
                    processAnnotations(field, ann, null);
                }
            }

            if (!hasPositionAnnotation) {
                field.addAndGetAnnotation(Position.class.getName()).addPair(VALUE, "" + position++);
            }
        }
        return keyFields;
    }

    private void processAnnotations(ClassOrInterfaceDeclaration generatedClass) {
        List<AnnotationDefinition> softAnnotations = new ArrayList<>();
        for (AnnotationDefinition ann : typeDefinition.getAnnotations()) {
            if (ann.getName().equals("serialVersionUID")) {
                LongLiteralExpr valueExpr = new LongLiteralExpr(ann.getValue(VALUE).toString());
                generatedClass.addFieldWithInitializer(PrimitiveType.longType(), "serialVersionUID", valueExpr, Modifier.privateModifier().getKeyword()
                        , Modifier.staticModifier().getKeyword(), Modifier.finalModifier().getKeyword());
            } else {
                processAnnotations(generatedClass, ann, softAnnotations);
            }
        }
        if (!softAnnotations.isEmpty()) {
            String softAnnDictionary = softAnnotations.stream().map(a -> "<dt>" + a.getName() + "</dt><dd>" + a.getValuesAsString() + "</dd>").collect(joining());
            JavadocComment generatedClassJavadoc = new JavadocComment("<dl>" + softAnnDictionary + "</dl>");
            generatedClass.setJavadocComment(generatedClassJavadoc);
        }
    }

    private void processAnnotations(NodeWithAnnotations node, AnnotationDefinition ann, List<AnnotationDefinition> softAnnotations) {
        Class<?> annotationClass = predefinedClassLevelAnnotation.get(ann.getName());

        if (annotationClass == null) {
            annotationClass = typeResolver.resolveType(ann.getName()).orElse(null);
            if (annotationClass == null) {
                return;
            }
        }

        String annFqn = annotationClass.getCanonicalName();
        if (annFqn != null) {
            processAnnotation(node, ann, softAnnotations, annotationClass, annFqn);
        } else {
            if (softAnnotations != null) {
                softAnnotations.add(ann);
            }
        }
    }

    private void processAnnotation(NodeWithAnnotations node, AnnotationDefinition ann, List<AnnotationDefinition> softAnnotations, Class<?> annotationClass, String annFqn) {
        NormalAnnotationExpr annExpr = node.addAndGetAnnotation(annFqn);
        for (Map.Entry<String, Object> entry : ann.getValueMap().entrySet()) {
            try {
                annotationClass.getMethod(entry.getKey());
                annExpr.addPair(entry.getKey(), getAnnotationValue(annFqn, entry.getKey(), entry.getValue()));
            } catch (NoSuchMethodException e) {
                if (softAnnotations == null) {
                    addBuilderResult(new PojoGenerationError("Unknown annotation property " + entry.getKey()));
                }
            }
        }
    }

    private void addBuilderResult(PojoGenerationError error) {
        generationResult.error(error);
    }

    private LinkedHashMap<String, TypeFieldDefinition> typeFieldsSortedByPosition() {
        Collection<TypeFieldDefinition> typeFields = typeDefinition.getFields().values();
        TypeFieldDefinition[] sortedTypes = new TypeFieldDefinition[typeFields.size()];

        List<TypeFieldDefinition> nonPositionalFields = new ArrayList<>();
        for (TypeFieldDefinition descr : typeFields) {
            AnnotationDefinition ann = descr.getAnnotation("Position");
            if (ann == null) {
                nonPositionalFields.add(descr);
            } else {
                int pos = Integer.parseInt(ann.getValue());
                sortedTypes[pos] = descr;
            }
        }

        int counter = 0;
        for (TypeFieldDefinition descr : nonPositionalFields) {
            for (; sortedTypes[counter] != null; counter++) {
                ;
            }
            sortedTypes[counter++] = descr;
        }

        LinkedHashMap<String, TypeFieldDefinition> sortedTypeField = new LinkedHashMap<>();
        for (TypeFieldDefinition t : sortedTypes) {
            sortedTypeField.put(t.getFieldName(), t);
        }

        return sortedTypeField;
    }

    private List<TypeFieldDefinition> findInheritedDeclaredFields() {
        return findInheritedDeclaredFields(new ArrayList<>(), getSuperType(typeDefinition));
    }

    private List<TypeFieldDefinition> findInheritedDeclaredFields(List<TypeFieldDefinition> fields, Optional<TypeDefinition> supertType) {
        supertType.ifPresent(st -> {
            findInheritedDeclaredFields(fields, getSuperType(st));
            fields.addAll(st.getFields().values());
        });
        return fields;
    }

    private Optional<TypeDefinition> getSuperType(TypeDefinition typeDeclaration) {
        return typeDeclaration.getSuperTypeName() != null ?
                allTypeDefinitions.stream().filter(td -> td.getTypeName().equals(typeDeclaration.getSuperTypeName())).findFirst() :
                Optional.empty();
    }

    static Statement replaceFieldName(Statement statement, String fieldName) {
        statement.findAll(NameExpr.class)
                .stream()
                .filter(n -> n.getName().toString().equals("__fieldName"))
                .forEach(n -> n.replace(new NameExpr(fieldName)));
        statement.findAll(FieldAccessExpr.class)
                .stream()
                .filter(n -> n.getName().toString().equals("__fieldName"))
                .forEach(n -> n.replace(new FieldAccessExpr(n.getScope(), fieldName)));
        return statement;
    }

    private static String getAnnotationValue(String annotationName, String valueName, Object value) {
        if (value instanceof Class) {
            return ((Class) value).getName() + ".class";
        }
        if (value.getClass().isArray()) {
            String valueString = Stream.of((Object[]) value).map(Object::toString).collect(joining(",", "{", "}"));
            return valueString.replace('[', '{').replace(']', '}');
        }
        return getAnnotationValue(annotationName, valueName, value.toString());
    }

    private static String getAnnotationValue(String annotationName, String valueName, String value) {
        if (annotationName.equals(Role.class.getCanonicalName())) {
            return Role.Type.class.getCanonicalName() + "." + value.toUpperCase();
        } else if (annotationName.equals(org.kie.api.definition.type.Expires.class.getCanonicalName())) {
            if (VALUE.equals(valueName)) {
                return quote(value);
            } else if ("policy".equals(valueName)) {
                return org.kie.api.definition.type.Expires.Policy.class.getCanonicalName() + "." + value.toUpperCase();
            } else {
                throw new UnsupportedOperationException("Unrecognized annotation value for Expires: " + valueName);
            }
        } else if ((annotationName.equals(org.kie.api.definition.type.Duration.class.getCanonicalName()) ||
                annotationName.equals(org.kie.api.definition.type.Timestamp.class.getCanonicalName())) && VALUE.equals(valueName)) {
            return quote(value);
        }
        return value;
    }
}
