/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.declaredtype.generator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.type.VoidType;
import org.drools.core.factmodel.AccessibleFact;

import static com.github.javaparser.StaticJavaParser.parseStatement;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.core.util.ClassUtils.setter2property;
import static org.drools.modelcompiler.builder.generator.declaredtype.generator.GeneratedClassDeclaration.OVERRIDE;

public class GeneratedAccessibleMethods {

    private static final String GET_VALUE = "getValue";
    private static final String SET_VALUE = "setValue";

    static MethodDeclaration getterMethod( List<GeneratedMethods.PojoField> fields, Class<?> superClass, boolean hasSuper ) {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(parseType(String.class.getSimpleName()), "fieldName"));

        final MethodDeclaration getter = new MethodDeclaration(nodeList( Modifier.publicModifier()), GET_VALUE,
                parseType(Object.class.getSimpleName()), nodeList(parameters));
        getter.addAnnotation(OVERRIDE);
        getter.setBody( new BlockStmt( nodeList( getterSwitchStmt( fields, superClass, hasSuper ) ) ) );
        return getter;
    }

    private static SwitchStmt getterSwitchStmt( List<GeneratedMethods.PojoField> fields, Class<?> superClass, boolean hasSuper ) {
        NodeList<SwitchEntry> entries = nodeList();
        for (GeneratedMethods.PojoField field : fields) {
            entries.add( new SwitchEntry( new NodeList<>( new StringLiteralExpr(field.name) ), SwitchEntry.Type.EXPRESSION,
                    new NodeList<>( parseStatement("return this." + field.name + ";") )) );
        }

        if (hasSuper) {
            if (superClass == null || AccessibleFact.class.isAssignableFrom( superClass )) {
                entries.add( new SwitchEntry( new NodeList<>(), SwitchEntry.Type.EXPRESSION,
                        new NodeList<>( parseStatement( "return super." + GET_VALUE + "(fieldName);" ) ) ) );
            } else {
                for (Method m : superClass.getDeclaredMethods()) {
                    if (m.getParameterCount() == 0) {
                        String fieldName = getter2property( m.getName() );
                        if (fieldName != null) {
                            entries.add( new SwitchEntry( new NodeList<>( new StringLiteralExpr(fieldName) ), SwitchEntry.Type.EXPRESSION,
                                    new NodeList<>( parseStatement("return this." + m.getName() + "();") )) );
                        }
                    }
                }
                entries.add( new SwitchEntry( new NodeList<>(), SwitchEntry.Type.EXPRESSION,
                        new NodeList<>( parseStatement( "return null;" ) ) ) );
            }
        } else {
            entries.add( new SwitchEntry( new NodeList<>(), SwitchEntry.Type.EXPRESSION,
                    new NodeList<>( parseStatement( "return null;" ) ) ) );
        }

        return new SwitchStmt(new NameExpr( "fieldName" ), entries);
    }

    static MethodDeclaration setterMethod( List<GeneratedMethods.PojoField> fields, Class<?> superClass, boolean hasSuper ) {

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(parseType(String.class.getSimpleName()), "fieldName"));
        parameters.add(new Parameter(parseType(Object.class.getSimpleName()), "value"));

        final MethodDeclaration setter = new MethodDeclaration(nodeList( Modifier.publicModifier()), SET_VALUE, new VoidType(), nodeList(parameters));
        setter.addAnnotation(OVERRIDE);
        setter.setBody( new BlockStmt( nodeList( setterSwitchStmt( fields, superClass, hasSuper ) ) ) );
        return setter;
    }

    private static SwitchStmt setterSwitchStmt( List<GeneratedMethods.PojoField> fields, Class<?> superClass, boolean hasSuper ) {
        NodeList<SwitchEntry> entries = nodeList();
        for (GeneratedMethods.PojoField field : fields) {
            entries.add( new SwitchEntry( new NodeList<>( new StringLiteralExpr(field.name) ), SwitchEntry.Type.EXPRESSION,
                    new NodeList<>( parseStatement("this." + field.name + " = (" + field.type.toString() + ")value;"), parseStatement("break;") )) );
        }

        if (hasSuper) {
            if (superClass == null || AccessibleFact.class.isAssignableFrom( superClass )) {
                entries.add( new SwitchEntry( new NodeList<>(), SwitchEntry.Type.EXPRESSION,
                        new NodeList<>( parseStatement( "super." + SET_VALUE + "(fieldName, value);" ) ) ) );
            } else {
                for (Method m : superClass.getDeclaredMethods()) {
                    if (m.getParameterCount() == 1) {
                        String fieldName = setter2property( m.getName() );
                        if (fieldName != null) {
                            String type = m.getGenericParameterTypes()[0].getTypeName();
                            entries.add( new SwitchEntry( new NodeList<>( new StringLiteralExpr(fieldName) ), SwitchEntry.Type.EXPRESSION,
                                    new NodeList<>( parseStatement("this." + m.getName() + "((" + type +  ") value);"), parseStatement("break;") )) );
                        }
                    }
                }
            }
        }

        return new SwitchStmt(new NameExpr( "fieldName" ), entries);
    }
}
