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
import org.kie.kogito.rules.SingletonStore;

public class RuleUnitDTOSourceClass implements RuleFileGenerator {

    private final RuleUnitDescription ruleUnit;

    private final String targetCanonicalName;
    private final String generatedFilePath;
    private final String packageName;
    private final RuleUnitHelper ruleUnitHelper;

    public RuleUnitDTOSourceClass( RuleUnitDescription ruleUnit, RuleUnitHelper ruleUnitHelper ) {
        this.ruleUnit = ruleUnit;

        this.targetCanonicalName = ruleUnit.getSimpleName() + "DTO";
        this.packageName = ruleUnit.getPackageName();
        this.ruleUnitHelper = ruleUnitHelper;
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
            FieldProcessor fieldProcessor = new FieldProcessor(unitVarDeclaration, ruleUnitHelper );
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
        final RuleUnitHelper ruleUnitHelper;
        final boolean isSingletonStore;
        private String genericType;

        public FieldProcessor( RuleUnitVariable ruleUnitVariable, RuleUnitHelper ruleUnitHelper ) {
            this.ruleUnitVariable = ruleUnitVariable;
            this.isDataSource = ruleUnitVariable.isDataSource();
            this.ruleUnitHelper = ruleUnitHelper;
            this.isSingletonStore = ruleUnitHelper.isAssignableFrom(SingletonStore.class, ruleUnitVariable.getType());
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
            return ruleUnitHelper.fieldInitializer( ruleUnitVariable, genericType, isDataSource );
        }
    }
}
