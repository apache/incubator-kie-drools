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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithConstructors;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration.replaceFieldName;

public interface GeneratedConstructor {

    static GeneratedConstructor factory(NodeWithConstructors<?> generatedClass,
                                        List<? extends FieldDefinition> typeDeclarationFields) {
        if (typeDeclarationFields.size() < 65) {
            return new FullArgumentConstructor(generatedClass, typeDeclarationFields, true, true);
        } else {
            return new NoConstructor();
        }
    }

    static GeneratedConstructor factoryEnum(NodeWithConstructors<?> generatedClass,
                                            List<FieldDefinition> typeDeclarationFields) {
        if (typeDeclarationFields.size() < 65) {
            return new FullArgumentConstructor(generatedClass, typeDeclarationFields, false, false);
        } else {
            return new NoConstructor();
        }
    }

    void generateConstructor(Collection<FieldDefinition> inheritedFields, List<FieldDefinition> keyFields);
}

class FullArgumentConstructor implements GeneratedConstructor {

    private final NodeWithConstructors<?> generatedClass;
    private final boolean shouldCallSuper;
    private final boolean publicConstructor;
    private List<? extends FieldDefinition> typeDeclarationFields;

    FullArgumentConstructor(NodeWithConstructors<?> generatedClass,
                            List<? extends FieldDefinition> typeDeclarationFields,
                            boolean publicConstructor,
                            boolean shouldCallSuper) {
        this.generatedClass = generatedClass;
        this.typeDeclarationFields = typeDeclarationFields;
        this.publicConstructor = publicConstructor;
        this.shouldCallSuper = shouldCallSuper;
    }

    @Override
    public void generateConstructor(Collection<FieldDefinition> inheritedFields, List<FieldDefinition> keyFields) {
        NodeList<Modifier> modifiers = publicConstructor ? new NodeList<>(Modifier.publicModifier()) : new NodeList<Modifier>();
        ConstructorDeclaration constructor = new ConstructorDeclaration(modifiers, generatedClass.getNameAsString());
        NodeList<Statement> fieldAssignStatement = nodeList();

        MethodCallExpr superCall = new MethodCallExpr(null, "super");
        for (FieldDefinition typeField : inheritedFields) {
            if (typeField.isStatic()) {
                continue;
            }
            String fieldName = typeField.getFieldName();
            addConstructorArgument(constructor, typeField.getObjectType(), fieldName);
            superCall.addArgument(fieldName);
            if (typeField.isKeyField()) {
                keyFields.add(typeField);
            }
        }

        if (shouldCallSuper) {
            fieldAssignStatement.add(new ExpressionStmt(superCall));
        }

        int nonStaticFields = 0;
        for (FieldDefinition fieldDefinition : typeDeclarationFields) {
            if (fieldDefinition.isStatic() || fieldDefinition.isOverride()) {
                continue;
            }
            nonStaticFields++;
            String fieldName = fieldDefinition.getFieldName();
            Type returnType = toClassOrInterfaceType(fieldDefinition.getObjectType());
            addConstructorArgument(constructor, returnType, fieldName);
            fieldAssignStatement.add(fieldAssignment(fieldName));
        }

        constructor.setBody(new BlockStmt(fieldAssignStatement));

        if (constructor.getParameters().isNonEmpty()) {
            generatedClass.addMember( constructor );
        }

        if (!keyFields.isEmpty() && keyFields.size() != inheritedFields.size() + nonStaticFields) {
            generateKieFieldsConstructor(keyFields);
        }
    }

    private void generateKieFieldsConstructor(List<FieldDefinition> keyFields) {
        Modifier.Keyword[] modifiers = publicConstructor ? new Modifier.Keyword[]{Modifier.publicModifier().getKeyword()} : new Modifier.Keyword[0];
        ConstructorDeclaration constructor = generatedClass.addConstructor(modifiers);
        NodeList<Statement> fieldStatements = nodeList();
        MethodCallExpr keySuperCall = new MethodCallExpr(null, "super");
        fieldStatements.add(new ExpressionStmt(keySuperCall));

        for (FieldDefinition fieldDefinition : keyFields) {
            String fieldName = fieldDefinition.getFieldName();
            addConstructorArgument(constructor, fieldDefinition.getObjectType(), fieldName);
            Optional<? extends FieldDefinition> typeDefinition = typeDeclarationFields.stream().filter(td -> td.getFieldName().equals(fieldName)).findAny();
            if (typeDefinition.isPresent()) {
                fieldStatements.add(fieldAssignment(fieldName));
            } else {
                keySuperCall.addArgument(fieldName);
            }
        }

        constructor.setBody(new BlockStmt(fieldStatements));
    }

    private Statement fieldAssignment(String fieldName) {
        return replaceFieldName(parseStatement("this.__fieldName = __fieldName;"), fieldName);
    }

    private static void addConstructorArgument(ConstructorDeclaration constructor, String typeName, String fieldName) {
        addConstructorArgument(constructor, toClassOrInterfaceType(typeName), fieldName);
    }

    private static void addConstructorArgument(ConstructorDeclaration constructor, Type fieldType, String fieldName) {
        constructor.addParameter(fieldType, fieldName);
    }
}

class NoConstructor implements GeneratedConstructor {

    @Override
    public void generateConstructor(Collection<FieldDefinition> inheritedFields, List<FieldDefinition> keyFields) {
        // Do not generate constructor here
        // See DeclareTest.testDeclaredTypeWithHundredsProps
    }
}


