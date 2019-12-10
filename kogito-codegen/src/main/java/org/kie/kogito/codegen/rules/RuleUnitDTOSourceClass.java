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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.rules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.core.util.ClassUtils;
import org.kie.kogito.codegen.FileGenerator;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.DataStream;

import static org.drools.core.util.StringUtils.ucFirst;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class RuleUnitDTOSourceClass implements FileGenerator {

    private final Class<?> ruleUnit;

    private final String targetCanonicalName;
    private final String generatedFilePath;

    public RuleUnitDTOSourceClass( Class<?> ruleUnit ) {
        this.ruleUnit = ruleUnit;

        this.targetCanonicalName = ruleUnit.getSimpleName() + "DTO";
        this.generatedFilePath = (ruleUnit.getPackage().getName() + "." + targetCanonicalName).replace('.', '/') + ".java";
    }

    @Override
    public String generatedFilePath() {
        return generatedFilePath;
    }

    @Override
    public String generate() {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration( ruleUnit.getPackage().getName() );

        ClassOrInterfaceDeclaration dtoClass = cu.addClass( targetCanonicalName, com.github.javaparser.ast.Modifier.Keyword.PUBLIC );
        dtoClass.addImplementedType( "java.util.function.Supplier<" + ruleUnit.getSimpleName() + ">" );

        MethodDeclaration supplier = dtoClass.addMethod( "get", com.github.javaparser.ast.Modifier.Keyword.PUBLIC );
        supplier.addAnnotation( Override.class );
        supplier.setType( ruleUnit.getSimpleName() );
        BlockStmt supplierBlock = supplier.createBody();
        supplierBlock.addStatement( ruleUnit.getSimpleName() + " unit = new " + ruleUnit.getSimpleName() + "();" );

        processUnitFields(field -> processField(dtoClass, supplierBlock, field));

        supplierBlock.addStatement( "return unit;" );

        return cu.toString();
    }

    private void processUnitFields(Consumer<FieldDescriptor> fieldProcessor) {
        Stream.of( ruleUnit.getDeclaredFields() )
                .map( this::introspectField )
                .filter( Objects::nonNull )
                .forEach( fieldProcessor );
    }

    private void processField(ClassOrInterfaceDeclaration dtoClass, BlockStmt supplierBlock, FieldDescriptor field) {
        Class<?> rawType = toRawClass(field.type);
        boolean isDataSource = DataSource.class.isAssignableFrom( rawType );
        String typeName = field.type.toString();
        String genericType = null;

        if ( isDataSource ) {
            int genericStart = typeName.indexOf( '<' );
            if (genericStart > 0) {
                genericType = typeName.substring( genericStart+1, typeName.length()-1 ).trim();
                typeName = "java.util.List" + typeName.substring( genericStart );
            } else {
                genericType = "Object";
                typeName = "java.util.List";
            }
        }
        dtoClass.addField( typeName, field.name, com.github.javaparser.ast.Modifier.Keyword.PRIVATE );

        MethodDeclaration getter = dtoClass.addMethod( "get" + ucFirst(field.name), com.github.javaparser.ast.Modifier.Keyword.PUBLIC );
        getter.setType( typeName );
        getter.createBody().addStatement( "return this." + field.name + ";");

        String setterName = "set" + ucFirst(field.name);
        MethodDeclaration setter = dtoClass.addMethod( setterName, com.github.javaparser.ast.Modifier.Keyword.PUBLIC );
        setter.addParameter( typeName, field.name );
        setter.createBody().addStatement( "this." + field.name + " = " + field.name + ";");

        if (genericType != null) {
            String singleSetterName = setterName.endsWith( "s" ) ? setterName.substring( 0, setterName.length()-1 ) : setterName + "Single";
            MethodDeclaration singleValueSetter = dtoClass.addMethod( singleSetterName, com.github.javaparser.ast.Modifier.Keyword.PUBLIC );
            singleValueSetter.addParameter( genericType, field.name );
            singleValueSetter.createBody()
                    .addStatement( "this." + field.name + " = new java.util.ArrayList<>();")
                    .addStatement( "this." + field.name + ".add(" + field.name + ");");
        }

        if (isDataSource) {
            boolean isDataStream = DataStream.class.isAssignableFrom( rawType );
            String sourceType = isDataStream ? "Stream" : "Store";
            String addMethod = isDataStream ? "append" : "add";

            if ( field.kind == FieldKind.GETTABLE ) {
                supplierBlock.addStatement( "this." + field.name + ".forEach( unit." + field.getter + "()::" + addMethod + " );" );
                return;
            } else {
                supplierBlock.addStatement( "org.kie.kogito.rules.Data" + sourceType + "<" + genericType + "> " + field.name + " = org.kie.kogito.rules.DataSource.create" + sourceType + "();" );
                supplierBlock.addStatement( "this." + field.name + ".forEach( " + field.name + "::" + addMethod + " );" );
            }
        }

        if (field.kind == FieldKind.PUBLIC) {
            supplierBlock.addStatement( "unit." + field.name + " = " + field.name + ";" );
        } else {
            supplierBlock.addStatement( "unit." + setterName + "( " + field.name + " );" );
        }
    }

    private FieldDescriptor introspectField( Field field ) {
        String name = field.getName();
        if ( Modifier.isPublic( field.getModifiers() ) ) {
            return new FieldDescriptor( field.getGenericType(), name, FieldKind.PUBLIC );
        }

        Method getter = ClassUtils.getAccessor(ruleUnit, name);
        if (getter == null) {
            return null;
        }

        try {
            ruleUnit.getMethod( "set" + ucFirst(name), field.getType() );
            return new FieldDescriptor( field.getGenericType(), name, getter.getName(), FieldKind.SETTABLE );
        } catch (NoSuchMethodException e) {
            if ( DataSource.class.isAssignableFrom( field.getType() ) ) {
                return new FieldDescriptor( field.getGenericType(), name, getter.getName(), FieldKind.GETTABLE );
            }
        }
        return null;
    }

    private static class FieldDescriptor {
        private final Type type;
        private final String name;
        private final String getter;
        private final FieldKind kind;

        private FieldDescriptor( Type type, String name, FieldKind kind ) {
            this(type, name, null, kind);
        }

        private FieldDescriptor( Type type, String name, String getter, FieldKind kind ) {
            this.type = type;
            this.name = name;
            this.getter = getter;
            this.kind = kind;
        }

        @Override
        public String toString() {
            return "FieldDescriptor{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    ", getter='" + getter + '\'' +
                    ", kind=" + kind +
                    '}';
        }
    }

    private enum FieldKind {
        PUBLIC, GETTABLE, SETTABLE
    }
}
