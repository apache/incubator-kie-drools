/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.declaredtype;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import org.drools.core.factmodel.AccessibleFact;
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodWithStringBody;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.core.util.ClassUtils.setter2property;
import static org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration.OVERRIDE;

public class AccessibleMethod {

    private static final String GET_VALUE = "getValue";
    private static final String SET_VALUE = "setValue";

    private DescrTypeDefinition descrTypeDefinition;
    List<DescrFieldDefinition> fields;

    public AccessibleMethod(DescrTypeDefinition descrTypeDefinition, List<DescrFieldDefinition> fields) {
        this.descrTypeDefinition = descrTypeDefinition;
        this.fields = fields;
    }

    public MethodDefinition getterMethod() {
        return new MethodWithStringBody(GET_VALUE, Object.class.getSimpleName(), getterSwitchStmt().toString())
                .addParameter(String.class.getCanonicalName(), "fieldName")
                .addAnnotation(OVERRIDE);
    }

    private Statement getterSwitchStmt() {
        NodeList<SwitchEntry> entries = nodeList();
        for (DescrFieldDefinition field : fields) {
            entries.add(new SwitchEntry(new NodeList<>(new StringLiteralExpr(field.getFieldName())), SwitchEntry.Type.EXPRESSION,
                                        new NodeList<>(parseStatement("return this." + field.getFieldName() + ";"))));
        }

        Optional<Class<?>> abstractClass = descrTypeDefinition.getAbstractClass();
        abstractClass.ifPresent(superClass -> {
            if (AccessibleFact.class.isAssignableFrom(superClass)) {
                defaultCase(entries, "return super.", GET_VALUE, "(fieldName);");
            } else {
                for (Method m : superClass.getDeclaredMethods()) {
                    if (m.getParameterCount() == 0) {
                        String fieldName = getter2property(m.getName());
                        if (fieldName != null) {
                            entries.add(new SwitchEntry(new NodeList<>(new StringLiteralExpr(fieldName)), SwitchEntry.Type.EXPRESSION,
                                                        new NodeList<>(parseStatement("return this." + m.getName() + "();"))));
                        }
                    }
                }
                entries.add(new SwitchEntry(new NodeList<>(), SwitchEntry.Type.EXPRESSION,
                                            new NodeList<>(parseStatement("return null;"))));
            }
        });


        if(descrTypeDefinition.getDeclaredAbstractClass().isPresent() && !abstractClass.isPresent()) {
            defaultCase(entries, "return super.", GET_VALUE, "(fieldName);");
        }

        if (!abstractClass.isPresent() && !descrTypeDefinition.getDeclaredAbstractClass().isPresent()) {
            entries.add(new SwitchEntry(new NodeList<>(), SwitchEntry.Type.EXPRESSION,
                                        new NodeList<>(parseStatement("return null;"))));
        }

        return new BlockStmt(nodeList(new SwitchStmt(new NameExpr("fieldName"), entries)));
    }

    private void defaultCase(NodeList<SwitchEntry> entries, String s, String getValue, String s2) {
        entries.add(new SwitchEntry(new NodeList<>(), SwitchEntry.Type.EXPRESSION,
                                    new NodeList<>(parseStatement(s + getValue + s2))));
    }

    public MethodDefinition setterMethod() {
        return new MethodWithStringBody(SET_VALUE, "void", setterSwitchStmt().toString())
                .addParameter(String.class.getCanonicalName(), "fieldName")
                .addParameter(Object.class.getCanonicalName(), "value")
                .addAnnotation(OVERRIDE);
    }

    private Statement setterSwitchStmt() {
        NodeList<SwitchEntry> entries = nodeList();
        for (DescrFieldDefinition field : fields) {
            entries.add(caseStatement(field.getFieldName(), field.getFieldName(), " = (", field.getObjectType(), ")value;"));
        }

        Optional<Class<?>> abstractClass = descrTypeDefinition.getAbstractClass();
        abstractClass.ifPresent(superClass -> {
            if (AccessibleFact.class.isAssignableFrom(superClass)) {
                defaultCase(entries, "super.", SET_VALUE, "(fieldName, value);");
            } else {
                for (Method m : superClass.getDeclaredMethods()) {
                    if (m.getParameterCount() == 1) { // simple setter
                        String fieldName = setter2property(m.getName());
                        if (fieldName != null) {
                            String type = m.getGenericParameterTypes()[0].getTypeName();
                            entries.add(caseStatement(fieldName, m.getName(), "((", type, ") value);"));
                        }
                    }
                }
            }
        });

        if(descrTypeDefinition.getDeclaredAbstractClass().isPresent() && !abstractClass.isPresent()) {
            defaultCase(entries, "super.", SET_VALUE, "(fieldName, value);");
        }

        return new BlockStmt(nodeList(new SwitchStmt(new NameExpr("fieldName"), entries)));
    }

    private SwitchEntry caseStatement(String fieldName, String fieldName2, String s, String objectType, String s2) {
        return new SwitchEntry(new NodeList<>(new StringLiteralExpr(fieldName)), SwitchEntry.Type.EXPRESSION,
                               new NodeList<>(parseStatement("this." + fieldName2 + s + objectType + s2), parseStatement("break;")));
    }
}
