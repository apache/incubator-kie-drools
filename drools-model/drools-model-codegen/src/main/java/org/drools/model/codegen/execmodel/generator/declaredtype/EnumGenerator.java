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
package org.drools.model.codegen.execmodel.generator.declaredtype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import org.drools.drl.ast.descr.EnumDeclarationDescr;
import org.drools.drl.ast.descr.EnumLiteralDescr;
import org.drools.drl.ast.descr.TypeFieldDescr;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.FieldDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.generator.GeneratedConstructor;

import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.util.StringUtils.ucFirst;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class EnumGenerator {

    private EnumDeclaration enumDeclaration;

    private List<FieldDeclaration> fields = new ArrayList<>();

    public EnumGenerator() {
    }

    public TypeDeclaration generate(EnumDeclarationDescr enumDeclarationDescr) {

        NodeList<Modifier> modifiers = nodeList(Modifier.publicModifier());

        enumDeclaration = new EnumDeclaration(modifiers, enumDeclarationDescr.getFullTypeName());

        for (Map.Entry<String, TypeFieldDescr> field : enumDeclarationDescr.getFields().entrySet()) {
            addField(field);
        }

        for (EnumLiteralDescr enumLiteral : enumDeclarationDescr.getLiterals()) {
            addEnumerationValue(enumLiteral);
        }

        createConstructor(enumDeclarationDescr);

        return enumDeclaration;
    }

    private void createConstructor(EnumDeclarationDescr enumDeclarationDescr) {
        List<FieldDefinition> enumFields = enumDeclarationDescr
                .getFields()
                .values()
                .stream()
                .map(DescrFieldDefinition::new)
                .collect(Collectors.toList());

        GeneratedConstructor fullArgumentConstructor = GeneratedConstructor.factoryEnum(enumDeclaration, enumFields);
        fullArgumentConstructor.generateConstructor(Collections.emptyList(), Collections.emptyList());
    }

    private void addField(Map.Entry<String, TypeFieldDescr> field) {
        Type type = toClassOrInterfaceType(field.getValue().getPattern().getObjectType());
        String key = field.getKey();
        FieldDeclaration fieldDeclaration = enumDeclaration.addField(type, key);
        fields.add(fieldDeclaration);

        createGetter(type, key);
    }

    private void createGetter(Type type, String key) {
        String accessorName = "get" + ucFirst(key);
        MethodDeclaration getterDeclaration = new MethodDeclaration(nodeList(Modifier.publicModifier()), type, accessorName);
        getterDeclaration.setBody(new BlockStmt(nodeList(new ReturnStmt(new NameExpr(key)))));
        enumDeclaration.addMember(getterDeclaration);
    }

    private void addEnumerationValue(EnumLiteralDescr enumLiteral) {
        EnumConstantDeclaration element = new EnumConstantDeclaration(enumLiteral.getName());
        for (String constructorArgument : enumLiteral.getConstructorArgs()) {
            element.addArgument(new NameExpr(constructorArgument));
        }
        enumDeclaration.addEntry(element);
    }
}
