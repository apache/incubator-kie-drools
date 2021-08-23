/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.compiler.alphanetbased;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;

public class TableCells {

    private final int numRows;
    private final int numColumns;

    TableCell[][] cells;

    public TableCells(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        cells = new TableCell[numRows][numColumns];
    }

    public void add(TableCell unitTestField) {
        unitTestField.addToCells(cells);
    }

    public Map<String, String> createUnaryTestClasses() {
        Map<String, String> allUnaryTests = new HashMap<>();
        // I'm pretty sure we can abstract this iteration to avoid copying it
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
            for (int columnIndex = 0; columnIndex < numColumns; columnIndex++) {
                cells[rowIndex][columnIndex].addUnaryTestClass(allUnaryTests);
            }
        }
        return allUnaryTests;
    }

    private CompilationUnit getAlphaClassTemplate() {
        InputStream resourceAsStream = this.getClass()
                .getResourceAsStream("/org/kie/dmn/core/alphasupport/AlphaNodeCreationTemplate.java");
        return StaticJavaParser.parse(resourceAsStream);
    }

    public void addAlphaNetworkNode(BlockStmt alphaNetworkStatements, GeneratedSources generatedSources) {

        // I'm pretty sure we can abstract this iteration to avoid copying it
        for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {

            CompilationUnit alphaNetworkCreationCU = getAlphaClassTemplate();
            String methodName = String.format("AlphaNodeCreation%s", rowIndex);

            ClassOrInterfaceDeclaration alphaNodeCreationClass = alphaNetworkCreationCU.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(RuntimeException::new);
            alphaNodeCreationClass.removeComment();

            replaceSimpleNameWith(alphaNodeCreationClass, "AlphaNodeCreationTemplate", methodName);

            ConstructorDeclaration constructorDeclaration = alphaNodeCreationClass.findFirst(ConstructorDeclaration.class).orElseThrow(RuntimeException::new);

            MethodDeclaration testMethodDefinitionTemplate = alphaNodeCreationClass.findFirst(MethodDeclaration.class, md -> md.getNameAsString().equals("testRxCx"))
                    .orElseThrow(() -> new RuntimeException("Cannot find test method template"));
            testMethodDefinitionTemplate.remove();

            for (int columnIndex = 0; columnIndex < numColumns; columnIndex++) {
                TableCell tableCell = cells[rowIndex][columnIndex];
                tableCell.addNodeCreation(constructorDeclaration.getBody(), alphaNodeCreationClass, testMethodDefinitionTemplate);
            }

            String classNameWithPackage = TableCell.PACKAGE + "." + methodName;
            generatedSources.addNewSourceClass(classNameWithPackage, alphaNetworkCreationCU.toString());

            String newAlphaNetworkClass = String.format(
                    "new %s(ctx)", classNameWithPackage
            );


            alphaNetworkStatements.addStatement(StaticJavaParser.parseExpression(newAlphaNetworkClass));

        }
    }
}