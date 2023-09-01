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
package org.kie.dmn.core.compiler.alphanetbased;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.model.api.BuiltinAggregator;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.HitPolicy;
import org.kie.dmn.model.api.InputClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseType;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.blockHasComment;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.parseJavaClassTemplateFromResources;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceStringLiteralExprWith;

public class DMNAlphaNetworkCompiler {

    private static final Logger logger = LoggerFactory.getLogger(DMNAlphaNetworkCompiler.class);

    private CompilationUnit template;
    private ClassOrInterfaceDeclaration dmnAlphaNetworkClass;

    public DMNAlphaNetworkCompiler() {
    }

    public GeneratedSources generateSourceCode(DecisionTable decisionTable,
                                               TableCells tableCells,
                                               String decisionTableName,
                                               GeneratedSources allGeneratedSources) {

        String escapedDecisionTableName = String.format("DMNAlphaNetwork_%s", CodegenStringUtil.escapeIdentifier(decisionTableName));

        initTemplate();
        setDMNAlphaNetworkClassName(escapedDecisionTableName);

        initPropertyNames(decisionTable.getInput());
        initHitPolicy(decisionTable.getHitPolicy(), decisionTable.getAggregation());

        generateValidationStatements(tableCells, allGeneratedSources);

        String alphaNetworkClassWithPackage = String.format("org.kie.dmn.core.alphasupport.%s", escapedDecisionTableName);
        allGeneratedSources.addNewAlphaNetworkClass(alphaNetworkClassWithPackage, template.toString());

        allGeneratedSources.logGeneratedClasses();

        return allGeneratedSources;
    }

    private void generateValidationStatements(TableCells tableCells, GeneratedSources allGeneratedSources) {

        BlockStmt validationBlock = dmnAlphaNetworkClass
                .findFirst(BlockStmt.class, block -> blockHasComment(block, "Validation Column"))
                .orElseThrow(RuntimeException::new);

        tableCells.addColumnValidationStatements(validationBlock, allGeneratedSources);
        validationBlock.remove();
    }

    private void initHitPolicy(HitPolicy hitPolicy, BuiltinAggregator aggregation) {
        String hitPolicyName = String.format("%s %s",
                                             hitPolicy.value(),
                                             aggregation != null ? aggregation.value() : "").trim();

        replaceStringLiteralExprWith(dmnAlphaNetworkClass, "HIT_POLICY_NAME", hitPolicyName);
    }

    private void initTemplate() {
        template = parseJavaClassTemplateFromResources(this.getClass(),
                                                       "/org/kie/dmn/core/alphasupport/DMNAlphaNetworkTemplate.java");
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
                .setElementType(parseType(String.class.getCanonicalName()))
                .setInitializer(new ArrayInitializerExpr(propertyNamesArray));

        template.findAll(StringLiteralExpr.class, n -> n.asString().equals("PROPERTY_NAMES"))
                .forEach(r -> r.replace(array));
    }
}
