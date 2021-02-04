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
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ArrayType;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.InputClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseType;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;

public class DMNAlphaNetworkCompiler {

    private static final Logger logger = LoggerFactory.getLogger(DMNAlphaNetworkCompiler.class);

    private CompilationUnit template;
    private ClassOrInterfaceDeclaration dmnAlphaNetworkClass;


    public DMNAlphaNetworkCompiler() {
    }

    public GeneratedSources generateSourceCode(DecisionTable decisionTable, TableCells tableCells, String decisionTableName, GeneratedSources allGeneratedSources) {

        String escapedDecisionTableName = String.format("DMNAlphaNetwork_%s", CodegenStringUtil.escapeIdentifier(decisionTableName));

        initTemplate();
        setDMNAlphaNetworkClassName(escapedDecisionTableName);
        initPropertyNames(decisionTable.getInput());

        BlockStmt alphaNetworkStatements = new BlockStmt();
        tableCells.addAlphaNetworkNode(alphaNetworkStatements, allGeneratedSources);

        BlockStmt alphaNetworkBlock = dmnAlphaNetworkClass
                .findFirst(BlockStmt.class, block -> blockHasComment(block, " Alpha network creation statements"))
                .orElseThrow(RuntimeException::new);

        alphaNetworkBlock.replace(alphaNetworkStatements);

        String alphaNetworkClassWithPackage = String.format("org.kie.dmn.core.alphasupport.%s", escapedDecisionTableName);
        allGeneratedSources.addNewAlphaNetworkClass(alphaNetworkClassWithPackage, template.toString());

        allGeneratedSources.logGeneratedClasses();

        return allGeneratedSources;
    }

    private static boolean blockHasComment(BlockStmt block, String comment) {
        return block.getComment().filter(c -> comment.equals(c.getContent()))
                .isPresent();
    }

    private void initTemplate() {
        template = getMethodTemplate();
        dmnAlphaNetworkClass = template.getClassByName("DMNAlphaNetworkTemplate")
                .orElseThrow(() -> new RuntimeException("Cannot find class"));
        dmnAlphaNetworkClass.removeComment();
    }

    private void setDMNAlphaNetworkClassName(String escapedDecisionTableName) {
        replaceSimpleNameWith(dmnAlphaNetworkClass, "DMNAlphaNetworkTemplate", escapedDecisionTableName);
    }

    private void initPropertyNames(List<InputClause> input) {

        NodeList<Expression> propertyNamesArray = input.stream()
                .map(inputClause -> inputClause.getInputExpression().getText())
                .map(StringLiteralExpr::new)
                .collect(Collectors.toCollection(NodeList::new));

        ArrayCreationExpr array = new ArrayCreationExpr()
                .setElementType(new ArrayType(parseType(String.class.getCanonicalName())))
                .setInitializer(new ArrayInitializerExpr(propertyNamesArray));

        template.findAll(StringLiteralExpr.class, n -> n.asString().equals("PROPERTY_NAMES"))
                .forEach(r -> r.replace(array));
    }

    private CompilationUnit getMethodTemplate() {
        InputStream resourceAsStream = this.getClass()
                .getResourceAsStream("/org/kie/dmn/core/alphasupport/DMNAlphaNetworkTemplate.java");
        return StaticJavaParser.parse(resourceAsStream);
    }
}
