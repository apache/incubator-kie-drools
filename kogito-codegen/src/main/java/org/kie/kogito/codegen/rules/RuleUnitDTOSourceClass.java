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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;
import org.kie.kogito.codegen.FileGenerator;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.SingletonStore;

public class RuleUnitDTOSourceClass implements FileGenerator {

    private final RuleUnitDescription ruleUnit;

    private final String targetCanonicalName;
    private final String generatedFilePath;
    private final String packageName;

    public RuleUnitDTOSourceClass(RuleUnitDescription ruleUnit) {
        this.ruleUnit = ruleUnit;

        this.targetCanonicalName = ruleUnit.getSimpleName() + "DTO";
        this.packageName = ruleUnit.getPackageName();
        this.generatedFilePath = (packageName + "." + targetCanonicalName).replace('.', '/') + ".java";
    }

    @Override
    public String generatedFilePath() {
        return generatedFilePath;
    }

    @Override
    public String generate() {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration(packageName);

        ClassOrInterfaceDeclaration dtoClass = cu.addClass(targetCanonicalName, Modifier.Keyword.PUBLIC);
        dtoClass.addImplementedType(String.format("java.util.function.Supplier<%s>", ruleUnit.getSimpleName()));

        MethodDeclaration supplier = dtoClass.addMethod("get", Modifier.Keyword.PUBLIC);
        supplier.addAnnotation(Override.class);
        supplier.setType(ruleUnit.getSimpleName());
        BlockStmt supplierBlock = supplier.createBody();
        supplierBlock.addStatement(String.format("%s unit = new %s();", ruleUnit.getSimpleName(), ruleUnit.getSimpleName()));

        for (RuleUnitVariable unitVarDeclaration : ruleUnit.getUnitVarDeclarations()) {
            FieldProcessor fieldProcessor = new FieldProcessor(unitVarDeclaration);
            FieldDeclaration field = fieldProcessor.createField();
            supplierBlock.addStatement(fieldProcessor.fieldInitializer());
            dtoClass.addMember(field);
            field.createGetter();
            field.createSetter();
        }

        supplierBlock.addStatement("return unit;");

        return cu.toString();
    }

    private static class FieldProcessor {

        final RuleUnitVariable ruleUnitVariable;
        final boolean isDataSource;
        final boolean isSingletonStore;
        private String genericType;

        public FieldProcessor(RuleUnitVariable ruleUnitVariable) {
            this.ruleUnitVariable = ruleUnitVariable;
            this.isDataSource = ruleUnitVariable.isDataSource();
            this.isSingletonStore = SingletonStore.class.isAssignableFrom(ruleUnitVariable.getType());
        }

        private FieldDeclaration createField() {
            Type type = toQueryType();

            VariableDeclarator variableDeclarator = new VariableDeclarator(type, ruleUnitVariable.getName());
            if (isDataSource && !isSingletonStore) {
                variableDeclarator.setInitializer("java.util.Collections.emptyList()");
            }

            return new FieldDeclaration()
                    .setModifiers(Modifier.Keyword.PRIVATE)
                    .addVariable(variableDeclarator);
        }

        // map non-singleton data sources (DataStore, DataStream) to List on the query DTO
        // map SingletonStore to a simple field in the DTO
        // leave simple fields (non-datasource) as they are
        private Type toQueryType() {
            if (isSingletonStore) {
                genericType = ruleUnitVariable.getDataSourceParameterType().getCanonicalName();
                return new ClassOrInterfaceType(null, genericType);
            } else if (isDataSource) {
                genericType = ruleUnitVariable.getDataSourceParameterType().getCanonicalName();
                return new ClassOrInterfaceType(null, "java.util.List")
                        .setTypeArguments(new ClassOrInterfaceType(null, genericType));
            } else {
                return new ClassOrInterfaceType(null, ruleUnitVariable.getType().getCanonicalName());
            }
        }

        private BlockStmt fieldInitializer() {
            BlockStmt supplierBlock = new BlockStmt();

            if (!isDataSource) {
                if (ruleUnitVariable.setter() != null) {
                    supplierBlock.addStatement(String.format("unit.%s(%s);", ruleUnitVariable.setter(), ruleUnitVariable.getName()));
                }
            } else if (DataStream.class.isAssignableFrom(ruleUnitVariable.getType())) {
                if (ruleUnitVariable.setter() != null) {
                    supplierBlock.addStatement(String.format("org.kie.kogito.rules.DataStream<%s> %s = org.kie.kogito.rules.DataSource.createStream();", genericType, ruleUnitVariable.getName()));
                    supplierBlock.addStatement(String.format("unit.%s(%s);", ruleUnitVariable.setter(), ruleUnitVariable.getName()));
                }
                supplierBlock.addStatement(String.format("this.%s.forEach( unit.%s()::append);", ruleUnitVariable.getName(), ruleUnitVariable.getter()));
            } else if (DataStore.class.isAssignableFrom(ruleUnitVariable.getType())) {
                if (ruleUnitVariable.setter() != null) {
                    supplierBlock.addStatement(String.format("org.kie.kogito.rules.DataStore<%s> %s = org.kie.kogito.rules.DataSource.createStore();", genericType, ruleUnitVariable.getName()));
                    supplierBlock.addStatement(String.format("unit.%s(%s);", ruleUnitVariable.setter(), ruleUnitVariable.getName()));
                }
                supplierBlock.addStatement(String.format("this.%s.forEach( unit.%s()::add);", ruleUnitVariable.getName(), ruleUnitVariable.getter()));
            } else if (SingletonStore.class.isAssignableFrom(ruleUnitVariable.getType())) {
                supplierBlock.addStatement(String.format("unit.%s().set(this.%s );", ruleUnitVariable.getter(), ruleUnitVariable.getName()));
            } else {
                throw new IllegalArgumentException("Unknown data source type " + ruleUnitVariable.getType());
            }

            return supplierBlock;
        }
    }
}
