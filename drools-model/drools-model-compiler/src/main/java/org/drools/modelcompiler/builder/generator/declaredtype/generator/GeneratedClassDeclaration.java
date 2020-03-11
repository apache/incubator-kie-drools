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

package org.drools.modelcompiler.builder.generator.declaredtype.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeResolver;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static java.text.MessageFormat.format;
import static org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator.quote;

public class GeneratedClassDeclaration {

    static final String OVERRIDE = "Override";

    private final TypeDefinition typeDefinition;
    private TypeResolver typeResolver;
    private GeneratedHashcode generatedHashcode;
    private GeneratedToString generatedToString;
    private GeneratedEqualsMethod generatedEqualsMethod;
    private ClassOrInterfaceDeclaration generatedClass;
    private final Collection<Class<?>> markerInterfaceAnnotations;

    public GeneratedClassDeclaration(TypeDefinition typeDefinition,
                              TypeResolver typeResolver,
                              Collection<Class<?>> markerInterfaceAnnotations) {

        this.typeDefinition = typeDefinition;
        this.typeResolver = typeResolver;
        this.markerInterfaceAnnotations = markerInterfaceAnnotations;
    }

    public ClassOrInterfaceDeclaration toClassDeclaration() {
        String generatedClassName = typeDefinition.getTypeName();
        generatedClass = createBasicDeclaredClass(generatedClassName);
        addAnnotations(generatedClass, typeDefinition.getAnnotationsToBeAdded());

        Collection<FieldDefinition> inheritedFields = typeDefinition.findInheritedDeclaredFields();
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

        markerInterfaceAnnotations.stream().map(Class::getCanonicalName).forEach(basicDeclaredClass::addImplementedType);
        basicDeclaredClass.addConstructor(Modifier.publicModifier().getKeyword()); // No-args ctor
        return basicDeclaredClass;
    }

    private ClassOrInterfaceDeclaration generateFullClass(String generatedClassName, Collection<FieldDefinition> inheritedFields) {
        generateInheritanceDefinition();

        boolean hasSuper = typeDefinition.getSuperTypeName().isPresent();

        generatedHashcode = new GeneratedHashcode(hasSuper);
        generatedToString = new GeneratedToString(generatedClassName);
        generatedEqualsMethod = new GeneratedEqualsMethod(generatedClassName, hasSuper);

        List<FieldDefinition> typeFields = typeDefinition.getFields();

        GeneratedConstructor fullArgumentConstructor = GeneratedConstructor.factory(generatedClass, typeFields);

        for (FieldDefinition tf : typeFields) {
            processTypeField(tf);
        }

        List<FieldDefinition> keyFields = typeDefinition.getKeyFields();

        fullArgumentConstructor.generateConstructor(inheritedFields, keyFields);
        if (!keyFields.isEmpty()) {
            generatedClass.addMember(generatedEqualsMethod.method());
            generatedClass.addMember(generatedHashcode.method());
        }

        generatedClass.addMember(generatedToString.method());
        return generatedClass;
    }

    private void generateInheritanceDefinition() {
        typeDefinition.getSuperTypeName().ifPresent( superTypeName -> {
            Optional<Class<?>> optResolvedSuper = typeResolver.resolveType(superTypeName);
            optResolvedSuper.ifPresent(resolvedSuper -> {
                if (resolvedSuper.isInterface()) {
                    generatedClass.addImplementedType(superTypeName);
                } else {
                    generatedClass.addExtendedType(superTypeName);
                }
            });

            if (!optResolvedSuper.isPresent()) {
                generatedClass.addExtendedType(superTypeName);
            }
        });
    }

    private void processTypeField(FieldDefinition fieldDefinition) {
        String fieldName = fieldDefinition.getFieldName();
        Type returnType = parseType(fieldDefinition.getObjectType());

        Modifier.Keyword[] modifiers = modifiers(fieldDefinition);

        FieldDeclaration field;
        if (fieldDefinition.getInitExpr() == null) {
            field = generatedClass.addField(returnType, fieldName, modifiers);
        } else {
            field = generatedClass.addFieldWithInitializer(returnType, fieldName, parseExpression(fieldDefinition.getInitExpr()), modifiers);
        }

        if (fieldDefinition.createAccessors()) {
            field.createSetter();
            MethodDeclaration getter = field.createGetter();

            if (fieldDefinition.isKeyField()) {
                generatedEqualsMethod.add(getter, fieldName);
                generatedHashcode.addHashCodeForField(fieldName, getter.getType());
            }
        }

        addAnnotations(field, fieldDefinition.getAnnotations());

        generatedToString.add(format("+ {0}+{1}", quote(fieldName + "="), fieldName));
    }

    private void addAnnotations(NodeWithAnnotations<?> fieldAnnotated, List<AnnotationDefinition> annotations) {
        for (AnnotationDefinition ad : annotations) {
            NormalAnnotationExpr annExpr = fieldAnnotated.addAndGetAnnotation(ad.getName());
            for (Map.Entry<String, String> entry : ad.getValueMap().entrySet()) {
                annExpr.addPair(entry.getKey(), entry.getValue());
            }
        }
    }

    private Modifier.Keyword[] modifiers(FieldDefinition fieldDefinition) {
        List<Modifier.Keyword> modifiers = new ArrayList<>();
        modifiers.add(Modifier.privateModifier().getKeyword());

        if (fieldDefinition.isStatic()) {
            modifiers.add(Modifier.staticModifier().getKeyword());
        } else if (fieldDefinition.isFinal()) {
            modifiers.add(Modifier.finalModifier().getKeyword());
        }

        return modifiers.toArray(new Modifier.Keyword[0]);
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
}
