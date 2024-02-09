/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator.declaredtype.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import org.drools.model.codegen.execmodel.JavaParserCompiler;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.AnnotationDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.FieldDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodParameter;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.TypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.text.MessageFormat.format;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.util.ClassUtils.getGetterMethod;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.createSimpleAnnotation;
import static org.drools.model.codegen.execmodel.generator.declaredtype.POJOGenerator.quote;

public class GeneratedClassDeclaration {

    private final TypeDefinition typeDefinition;
    private GeneratedHashcode generatedHashcode;
    private GeneratedToString generatedToString;
    private GeneratedEqualsMethod generatedEqualsMethod;
    private ClassOrInterfaceDeclaration generatedClass;
    private final Collection<Class<?>> markerInterfaceAnnotations;

    public static final Logger LOG = LoggerFactory.getLogger(GeneratedClassDeclaration.class);

    public GeneratedClassDeclaration(TypeDefinition typeDefinition,
                                     Collection<Class<?>> markerInterfaceAnnotations) {

        this.typeDefinition = typeDefinition;
        this.markerInterfaceAnnotations = markerInterfaceAnnotations;
    }

    public GeneratedClassDeclaration(TypeDefinition typeDefinition) {
        this(typeDefinition, Collections.emptyList());
    }

    public ClassOrInterfaceDeclaration toClassDeclaration() {
        String generatedClassName = typeDefinition.getTypeName();
        generatedClass = createBasicDeclaredClass(generatedClassName);
        addAnnotations(generatedClass, typeDefinition.getAnnotationsToBeAdded());
        typeDefinition.getJavadoc().ifPresent(generatedClass::setJavadocComment);

        generateInheritanceDefinition();

        Collection<FieldDefinition> inheritedFields = typeDefinition.findInheritedDeclaredFields();
        if (inheritedFields.isEmpty() && typeDefinition.getFields().isEmpty()) {
            generatedClass.addMember(new GeneratedToString(generatedClassName).method());
            typeDefinition.getMethods().forEach(this::addMethod);
        } else {
            generatedClass = generateFullClass(generatedClassName, inheritedFields);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Generated class:%n%s", JavaParserCompiler.getPrettyPrinter().print(generatedClass)));
        }

        return generatedClass;
    }

    private ClassOrInterfaceDeclaration createBasicDeclaredClass(String generatedClassName) {
        ClassOrInterfaceDeclaration basicDeclaredClass = new ClassOrInterfaceDeclaration(
                nodeList(Modifier.publicModifier())
                , false
                , generatedClassName);

        basicDeclaredClass.addImplementedType(Serializable.class.getName()); // Ref: {@link org.drools.base.factmodel.DefaultBeanClassBuilder} by default always receive is Serializable.

        markerInterfaceAnnotations.stream().map(Class::getCanonicalName).forEach(basicDeclaredClass::addImplementedType);
        basicDeclaredClass.addConstructor(Modifier.publicModifier().getKeyword()); // No-args ctor
        return basicDeclaredClass;
    }

    private ClassOrInterfaceDeclaration generateFullClass(String generatedClassName, Collection<FieldDefinition> inheritedFields) {

        boolean hasSuper = typeDefinition.getSuperTypeName().isPresent();

        generatedHashcode = new GeneratedHashcode(hasSuper);
        generatedToString = new GeneratedToString(generatedClassName);
        generatedEqualsMethod = new GeneratedEqualsMethod(generatedClassName, hasSuper);

        List<? extends FieldDefinition> typeFields = typeDefinition.getFields();

        GeneratedConstructor fullArgumentConstructor = GeneratedConstructor.factory(generatedClass, typeFields);

        typeDefinition.getMethods().forEach(this::addMethod);

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

    private void addMethod(MethodDefinition methodDefinition) {

        List<Modifier.Keyword> modifiers = new ArrayList<>();
        if (methodDefinition.isStatic()) {
            modifiers.add(Modifier.Keyword.STATIC);
        }
        if (methodDefinition.isPublic()) {
            modifiers.add(Modifier.Keyword.PUBLIC);
        }
        MethodDeclaration methodDeclaration = generatedClass.addMethod(methodDefinition.getMethodName(),
                                                                       modifiers.toArray(new Modifier.Keyword[0]));
        methodDeclaration.setType(methodDefinition.getReturnType());

        for(MethodParameter mp : methodDefinition.parameters()) {
            methodDeclaration.addParameter(mp.getType(), mp.getName());
        }

        for(AnnotationDefinition a : methodDefinition.getAnnotations()) {
            methodDeclaration.addAnnotation( createSimpleAnnotation(a.getName()) );
        }

        methodDeclaration.setBody(StaticJavaParser.parseBlock(methodDefinition.getBody()));
    }

    private void generateInheritanceDefinition() {
        typeDefinition.getSuperTypeName().ifPresent(generatedClass::addExtendedType);
        typeDefinition.getInterfacesNames().forEach(generatedClass::addImplementedType);
    }

    private void processTypeField(FieldDefinition fieldDefinition) {
        String fieldName = fieldDefinition.getFieldName();
        Type returnType = parseType(fieldDefinition.getObjectType());

        if (fieldDefinition.isOverride()) {
            if (fieldDefinition.createAccessors()) {
                String getterName = getGetterMethod(fieldName);
                MethodDeclaration getter = generatedClass.addMethod( getterName, Modifier.Keyword.PUBLIC );
                getter.addAnnotation( createSimpleAnnotation(Override.class) );
                getter.setType( fieldDefinition.getObjectType() );
                BlockStmt block = new BlockStmt();
                block.addStatement( "return (" + fieldDefinition.getObjectType() + ") super." + getterName + "();" );
                getter.setBody( block );
            }
            return;
        }

        Modifier.Keyword[] modifiers = modifiers(fieldDefinition);

        FieldDeclaration field;
        if (fieldDefinition.getInitExpr() == null) {
            field = generatedClass.addField(returnType, fieldName, modifiers);
        } else {
            field = generatedClass.addFieldWithInitializer(returnType, fieldName, parseExpression(fieldDefinition.getInitExpr()), modifiers);
        }

        fieldDefinition.getJavadocComment().ifPresent(field::setJavadocComment);

        if (fieldDefinition.createAccessors()) {
            MethodDeclaration setter = field.createSetter();
            fieldDefinition.overriddenSetterName().ifPresent(setter::setName);
            fieldDefinition.setterAnnotations().forEach(a -> addAnnotationToMethodDeclaration(setter, a));

            MethodDeclaration getter = field.createGetter();
            fieldDefinition.overriddenGetterName().ifPresent(getter::setName);
            fieldDefinition.getterAnnotations().forEach(a -> addAnnotationToMethodDeclaration(getter, a));

            if (fieldDefinition.isKeyField()) {
                generatedEqualsMethod.add(getter, fieldName);
                generatedHashcode.addHashCodeForField(fieldName, getter.getType());
            }
        }

        addAnnotations(field, fieldDefinition.getFieldAnnotations());

        generatedToString.add(format("+ {0}+{1}", quote(fieldName + "="), fieldName));
    }

    private void addAnnotationToMethodDeclaration(MethodDeclaration setter, AnnotationDefinition a) {
        NormalAnnotationExpr annotation = new NormalAnnotationExpr();
        annotation.setName(a.getName());
        a.getValueMap().forEach(annotation::addPair);
        setter.addAnnotation(annotation);
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
