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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
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

    private final TypeDefinition typeDefinition;
    private TypeResolver typeResolver;
    private GeneratedHashcode generatedHashcode;
    private GeneratedToString generatedToString;
    private GeneratedEqualsMethod generatedEqualsMethod;
    private ClassOrInterfaceDeclaration generatedClass;
    private final Collection<Class<?>> markerInterfaceAnnotations;

    GeneratedClassDeclaration(TypeDefinition typeDefinition,
                              TypeResolver typeResolver,
                              Collection<Class<?>> markerInterfaceAnnotations) {

        this.typeDefinition = typeDefinition;
        this.typeResolver = typeResolver;
        this.markerInterfaceAnnotations = markerInterfaceAnnotations;
    }

    ClassOrInterfaceDeclaration toClassDeclaration() {
        String generatedClassName = typeDefinition.getTypeName();
        generatedClass = createBasicDeclaredClass(generatedClassName);

        Collection<TypeFieldDefinition> inheritedFields = typeDefinition.findInheritedDeclaredFields();
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

        markerInterfaceAnnotations.stream().map(Class::getCanonicalName).forEach(basicDeclaredClass::addImplementedType);
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

        List<TypeFieldDefinition> typeFields = typeDefinition.getFields();

        generatedHashcode = new GeneratedHashcode(hasSuper);
        generatedToString = new GeneratedToString(generatedClassName);
        generatedEqualsMethod = new GeneratedEqualsMethod(generatedClassName, hasSuper);

        GeneratedConstructor fullArgumentConstructor = GeneratedConstructor.factory(generatedClass, typeFields);

        for (TypeFieldDefinition tf : typeFields) {
            processTypeField(tf);
        }

        List<TypeFieldDefinition> keyFields = typeDefinition.getKeyFields();

        fullArgumentConstructor.generateConstructor(inheritedFields, keyFields);
        if (!keyFields.isEmpty()) {
            generatedClass.addMember(generatedEqualsMethod.method());
            generatedClass.addMember(generatedHashcode.method());
        }

        generatedClass.addMember(generatedToString.method());
        return generatedClass;
    }

    private void processTypeField(TypeFieldDefinition typeFieldDescr) {
        String fieldName = typeFieldDescr.getFieldName();
        Type returnType = parseType(typeFieldDescr.getObjectType());

        Modifier.Keyword[] modifiers = modifiers(typeFieldDescr);

        FieldDeclaration field;
        if (typeFieldDescr.getInitExpr() == null) {
            field = generatedClass.addField(returnType, fieldName, modifiers);
        } else {
            field = generatedClass.addFieldWithInitializer(returnType, fieldName, parseExpression(typeFieldDescr.getInitExpr()), modifiers);
        }

        if (typeFieldDescr.createAccessors()) {
            field.createSetter();
            MethodDeclaration getter = field.createGetter();

            if (typeFieldDescr.isKeyField()) {
                generatedEqualsMethod.add(getter, fieldName);
                generatedHashcode.addHashCodeForField(fieldName, getter.getType());
            }
        }

        for (AnnotationDefinition ad : typeFieldDescr.getAnnotations()) {
            NormalAnnotationExpr annExpr = field.addAndGetAnnotation(ad.getName());
            for (Map.Entry<String, Object> entry : ad.getValueMap().entrySet()) {
                annExpr.addPair(entry.getKey(), getAnnotationValue(ad.getName(), entry.getKey(), entry.getValue()));
            }
        }

        generatedToString.add(format("+ {0}+{1}", quote(fieldName + "="), fieldName));
    }

    private Modifier.Keyword[] modifiers(TypeFieldDefinition typeFieldDescr) {
        List<Modifier.Keyword> modifiers = new ArrayList<>();
        modifiers.add(Modifier.privateModifier().getKeyword());

        if (typeFieldDescr.isStatic()) {
            modifiers.add(Modifier.staticModifier().getKeyword());
        } else if (typeFieldDescr.isFinal()) {
            modifiers.add(Modifier.finalModifier().getKeyword());
        }

        return modifiers.toArray(new Modifier.Keyword[0]);
    }

    private void processAnnotations(ClassOrInterfaceDeclaration generatedClass) {
        for (AnnotationDefinition ann : typeDefinition.getAnnotations()) {
            NormalAnnotationExpr annExpr = generatedClass.addAndGetAnnotation(ann.getName());
            for (Map.Entry<String, Object> entry : ann.getValueMap().entrySet()) {
                annExpr.addPair(entry.getKey(), getAnnotationValue(ann.getName(), entry.getKey(), entry.getValue()));
            }
        }

        List<AnnotationDefinition> softAnnotations = typeDefinition.getSoftAnnotations();
        if (!softAnnotations.isEmpty()) {
            String softAnnDictionary = softAnnotations.stream().map(a -> "<dt>" + a.getName() + "</dt><dd>" + a.getValuesAsString() + "</dd>").collect(joining());
            JavadocComment generatedClassJavadoc = new JavadocComment("<dl>" + softAnnDictionary + "</dl>");
            generatedClass.setJavadocComment(generatedClassJavadoc);
        }
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
        if (value instanceof Class<?>) {
            return ((Class<?>) value).getName() + ".class";
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
