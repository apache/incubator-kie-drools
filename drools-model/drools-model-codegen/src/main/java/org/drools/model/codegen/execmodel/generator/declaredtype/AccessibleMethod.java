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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import org.drools.base.factmodel.AccessibleFact;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.MethodWithStringBody;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.ast.NodeList.nodeList;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static org.drools.util.ClassUtils.getter2property;
import static org.drools.util.ClassUtils.setter2property;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toStringLiteral;

public class AccessibleMethod {

    private static final String GET_VALUE = "getValue";
    private static final String SUPER_GET_VALUE = "return super.getValue(fieldName);";

    private static final String SET_VALUE = "setValue";
    private static final String SUPER_SET_VALUE = "super.setValue(fieldName, value);";

    private static final String BREAK_STATEMENT = "break;";
    private static final String FIELD_NAME = "fieldName";
    private static final String RETURN_NULL = "return null;";

    private DescrTypeDefinition descrTypeDefinition;
    List<DescrFieldDefinition> fields;

    public AccessibleMethod(DescrTypeDefinition descrTypeDefinition, List<DescrFieldDefinition> fields) {
        this.descrTypeDefinition = descrTypeDefinition;
        this.fields = fields;
    }

    public MethodDefinition getterMethod() {
        return new MethodWithStringBody(GET_VALUE, Object.class.getSimpleName(), getterSwitchStatement().toString())
                .addParameter(String.class.getCanonicalName(), FIELD_NAME)
                .addAnnotation("Override");
    }

    public MethodDefinition setterMethod() {
        return new MethodWithStringBody(SET_VALUE, "void", setterSwitchStatement().toString())
                .addParameter(String.class.getCanonicalName(), FIELD_NAME)
                .addParameter(Object.class.getCanonicalName(), "value")
                .addAnnotation("Override");
    }

    private Statement getterSwitchStatement() {
        SwitchStmt switchStmt = switchOnFieldName();

        NodeList<SwitchEntry> switchEntries = switchStmt.getEntries();
        for (DescrFieldDefinition field : fields) {
            if (!field.isOverride()) {
                switchEntries.add( getValueFromField( field ) );
            }
        }

        Optional<Class<?>> abstractResolvedClass = descrTypeDefinition.getAbstractResolvedClass();

        if (abstractResolvedClass.isPresent()) {
            switchEntries.addAll(superClassGetterEntries(abstractResolvedClass.get()).collect(toList()));
        } else if (descrTypeDefinition.getDeclaredAbstractClass().isPresent()) {
            switchEntries.add(switchEntry(SUPER_GET_VALUE));
        } else {
            switchEntries.add(switchEntry(RETURN_NULL));
        }

        return new BlockStmt(nodeList(switchStmt));
    }

    private Statement setterSwitchStatement() {
        SwitchStmt switchStmt = switchOnFieldName();

        NodeList<SwitchEntry> entries = switchStmt.getEntries();
        for (DescrFieldDefinition field : fields) {
            if (!field.isOverride()) {
                entries.add( setValueFromField( field ) );
            }
        }

        Optional<Class<?>> abstractResolvedClass = descrTypeDefinition.getAbstractResolvedClass();

        if (abstractResolvedClass.isPresent()) {
            entries.addAll(superClassSetterEntries(abstractResolvedClass.get()).collect(toList()));
        } else if (descrTypeDefinition.getDeclaredAbstractClass().isPresent()) {
            entries.add(switchEntry(SUPER_SET_VALUE));
        }

        return new BlockStmt(nodeList(switchStmt));
    }

    private Stream<SwitchEntry> superClassGetterEntries(Class<?> superClass) {
        if (superClassIsAccessibleFact(superClass)) {
            return of(switchEntry(SUPER_GET_VALUE));
        } else {
            return concat(stream(superClass.getDeclaredMethods()).flatMap(this::switchEntryWithGetter),
                          of(switchEntry(RETURN_NULL)));
        }
    }

    private Stream<SwitchEntry> superClassSetterEntries(Class<?> superClass) {
        if (superClassIsAccessibleFact(superClass)) {
            return of(switchEntry(SUPER_SET_VALUE));
        } else {
            return stream(superClass.getDeclaredMethods()).flatMap(this::switchEntryWithSetter);
        }
    }

    private Stream<SwitchEntry> switchEntryWithGetter(Method m) {
        if (m.getParameterCount() == 0) {
            String fieldName = getter2property(m.getName());
            if (fieldName != null) {
                return of(getValueFromProperty(m, fieldName));
            }
        }
        return empty();
    }

    private Stream<SwitchEntry> switchEntryWithSetter(Method m) {
        if (m.getParameterCount() == 1) { // simple setter
            String fieldName = setter2property(m.getName());
            if (fieldName != null) {
                return of(setValueFromProperty(m, fieldName, m.getGenericParameterTypes()[0].getTypeName()));
            }
        }
        return empty();
    }

    private SwitchStmt switchOnFieldName() {
        SwitchStmt switchStmt = new SwitchStmt();
        switchStmt.setSelector(new NameExpr(FIELD_NAME));
        return switchStmt;
    }

    private SwitchEntry getValueFromField(DescrFieldDefinition field) {
        return stringSwitchExpression(field.getFieldName(), format("return this.%s;", field.getFieldName()));
    }

    private SwitchEntry getValueFromProperty(Method m, String fieldName) {
        return stringSwitchExpression(fieldName, format("return this.%s();", m.getName()));
    }

    private SwitchEntry setValueFromField(DescrFieldDefinition field) {
        return stringSwitchExpression(field.getFieldName(),
                                      format("this.%s = (%s)value;", field.getFieldName(), field.getObjectType())
                , BREAK_STATEMENT);
    }

    private SwitchEntry setValueFromProperty(Method m, String fieldName, String type) {
        return stringSwitchExpression(fieldName,
                                      format("this.%s((%s) value);", m.getName(), type),
                                      BREAK_STATEMENT);
    }

    private SwitchEntry switchEntry(String statement) {
        return new SwitchEntry(nodeList(), SwitchEntry.Type.STATEMENT_GROUP, nodeList(parseStatement(statement)));
    }

    private SwitchEntry stringSwitchExpression(String caseLabel, String... statements) {
        NodeList<Statement> switchStatements = nodeList();
        for (String statement : statements) {
            switchStatements.add(parseStatement(statement));
        }
        return new SwitchEntry(nodeList(toStringLiteral(caseLabel)), SwitchEntry.Type.STATEMENT_GROUP, switchStatements);
    }

    private boolean superClassIsAccessibleFact(Class<?> superClass) {
        return AccessibleFact.class.isAssignableFrom(superClass);
    }
}
