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
import static java.lang.String.format;
import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.core.util.ClassUtils.setter2property;
import static org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration.OVERRIDE;

public class AccessibleMethod {

    private static final String GET_VALUE = "getValue";
    private static final String SET_VALUE = "setValue";
    private static final String BREAK_STATEMENT = "break;";
    private static final String FIELD_NAME = "fieldName";

    private DescrTypeDefinition descrTypeDefinition;
    List<DescrFieldDefinition> fields;

    public AccessibleMethod(DescrTypeDefinition descrTypeDefinition, List<DescrFieldDefinition> fields) {
        this.descrTypeDefinition = descrTypeDefinition;
        this.fields = fields;
    }

    public MethodDefinition getterMethod() {
        return new MethodWithStringBody(GET_VALUE, Object.class.getSimpleName(), getterSwitchStatement().toString())
                .addParameter(String.class.getCanonicalName(), FIELD_NAME)
                .addAnnotation(OVERRIDE);
    }

    public MethodDefinition setterMethod() {
        return new MethodWithStringBody(SET_VALUE, "void", setterSwitchStatement().toString())
                .addParameter(String.class.getCanonicalName(), FIELD_NAME)
                .addParameter(Object.class.getCanonicalName(), "value")
                .addAnnotation(OVERRIDE);
    }

    private Statement getterSwitchStatement() {
        NodeList<SwitchEntry> entries = nodeList();
        for (DescrFieldDefinition field : fields) {
            entries.add(switchEntry(field.getFieldName(), format("return this.%s;", field.getFieldName())));
        }

        Optional<Class<?>> abstractResolvedClass = descrTypeDefinition.getAbstractResolvedClass();
        Optional<String> declaredSuperType = descrTypeDefinition.getDeclaredAbstractClass();


        abstractResolvedClass.ifPresent(superClass -> addSuperClassGetterProperties(entries, superClass));

        if (declaredSuperType.isPresent() && !abstractResolvedClass.isPresent()) {
            entries.add(defaultCase(format("return super.%s(fieldName);", GET_VALUE)));
        }

        if (!abstractResolvedClass.isPresent() && !declaredSuperType.isPresent()) {
            entries.add(defaultCase("return null;"));
        }

        return new BlockStmt(nodeList(new SwitchStmt(new NameExpr(FIELD_NAME), entries)));
    }

    private void addSuperClassGetterProperties(NodeList<SwitchEntry> entries, Class<?> superClass) {
        if (AccessibleFact.class.isAssignableFrom(superClass)) {
            entries.add(defaultCase(format("return super.%s(fieldName);", GET_VALUE)));
        } else {
            for (Method m : superClass.getDeclaredMethods()) {
                if (m.getParameterCount() == 0) {
                    String fieldName = getter2property(m.getName());
                    if (fieldName != null) {
                        entries.add(switchEntry(fieldName, format("return this.%s();", m.getName())));
                    }
                }
            }
            entries.add(defaultCase("return null;"));
        }
    }

    private SwitchEntry switchEntry(String fieldName, String... statements) {
        NodeList<Statement> switchStatements = nodeList();
        for(String statement : statements) {
            switchStatements.add(parseStatement(statement));
        }
        return new SwitchEntry(nodeList(new StringLiteralExpr(fieldName)), SwitchEntry.Type.EXPRESSION, switchStatements);
    }

    private SwitchEntry defaultCase(String statement) {
        return new SwitchEntry(nodeList(), SwitchEntry.Type.EXPRESSION,
                               nodeList(parseStatement(statement)));
    }

    private Statement setterSwitchStatement() {
        NodeList<SwitchEntry> entries = nodeList();
        for (DescrFieldDefinition field : fields) {
            entries.add(switchEntry(field.getFieldName(), format("this.%s = (%s)value;", field.getFieldName(), field.getObjectType()), BREAK_STATEMENT));
        }

        Optional<Class<?>> abstractClass = descrTypeDefinition.getAbstractResolvedClass();
        abstractClass.ifPresent(superClass -> addSuperClassSetterProperties(entries, superClass));

        if (descrTypeDefinition.getDeclaredAbstractClass().isPresent() && !abstractClass.isPresent()) {
            entries.add(defaultCase("super." + SET_VALUE + "(fieldName, value);"));
        }

        return new BlockStmt(nodeList(new SwitchStmt(new NameExpr(FIELD_NAME), entries)));
    }

    private void addSuperClassSetterProperties(NodeList<SwitchEntry> entries, Class<?> superClass) {
        if (AccessibleFact.class.isAssignableFrom(superClass)) {
            entries.add(defaultCase(format("super.%s(fieldName, value);", SET_VALUE)));
        } else {
            for (Method m : superClass.getDeclaredMethods()) {
                if (m.getParameterCount() == 1) { // simple setter
                    String fieldName = setter2property(m.getName());
                    if (fieldName != null) {
                        String type = m.getGenericParameterTypes()[0].getTypeName();
                        entries.add(switchEntry(fieldName, format("this.%s((%s) value);", m.getName(), type), BREAK_STATEMENT));
                    }
                }
            }
        }
    }
}
