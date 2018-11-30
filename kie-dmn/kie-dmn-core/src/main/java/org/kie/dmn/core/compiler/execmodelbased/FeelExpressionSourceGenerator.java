/*
 * Copyright 2005 JBoss Inc
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

package org.kie.dmn.core.compiler.execmodelbased;

import java.util.ArrayList;
import java.util.List;

import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeelExpressionSourceGenerator implements ExecModelDMNEvaluatorCompiler.SourceGenerator {

    private static final Logger logger = LoggerFactory.getLogger(FeelExpressionSourceGenerator.class);

    static final String INPUT_CLAUSE_NAMESPACE = "InputClause";

    private Class<?> COMPILED_FEEL_EXPRESSION_TYPE = CompiledFEELExpression.class;

    private JavaParserSourceGenerator sourceGenerator;

    public String generate(DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel) {
        String pkgName = dTableModel.getNamespace();
        String dTableName = dTableModel.getTableName();

        sourceGenerator = new JavaParserSourceGenerator(dTableName, ExecModelDMNEvaluatorCompiler.GeneratorsEnum.FEEL_EXPRESSION.type, pkgName);
        sourceGenerator.addImports(org.kie.dmn.feel.codegen.feel11.CompiledCustomFEELFunction.class,
                                   org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression.class,
                                   org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings.class,
                                   EvaluationContext.class,
                                   CompiledFEELExpression.class);

        generateInitRows(ctx, dTableModel, dTableName);
        generateInputClauses(ctx, dTableModel);

        String source = sourceGenerator.getSource();
        if (logger.isDebugEnabled()) {
            logger.debug(dTableName + ":\n" + source);
        }
        return source;
    }

    private void generateInitRows(DMNCompilerContext ctx, DTableModel dTableModel, String className) {

        ClassOrInterfaceDeclaration[][] rows = dTableModel.generateRows(ctx.toCompilerContext());
        List<List<String>> arrayInitializer = new ArrayList<>();

        for (int i = 0; i < rows.length; i++) {
            ClassOrInterfaceDeclaration[] columns = rows[i];

            List<String> arrayInitializerInner = new ArrayList<>();
            for (int j = 0; j < columns.length; j++) {
                String testClass = className + "r" + i + "c" + j + "expression";
                String node = instanceName(testClass);

                sourceGenerator.addField(testClass, COMPILED_FEEL_EXPRESSION_TYPE, node);
                sourceGenerator.addInnerClassWithName(columns[j], testClass);

                arrayInitializerInner.add(node);
            }

            arrayInitializer.add(new ArrayList<>(arrayInitializerInner));
        }

        sourceGenerator.addTwoDimensionalArray(arrayInitializer, "FEEL_EXPRESSION_ARRAY", COMPILED_FEEL_EXPRESSION_TYPE);
    }

    private void generateInputClauses(DMNCompilerContext ctx, DTableModel dTableModel) {

        List<ClassOrInterfaceDeclaration> inputClauses = dTableModel.generateInputClauses(ctx.toCompilerContext());

        for (int i = 0; i < inputClauses.size(); i++) {
            String testClass = INPUT_CLAUSE_NAMESPACE + i;

            sourceGenerator.addField(testClass, COMPILED_FEEL_EXPRESSION_TYPE, instanceName(testClass));
            sourceGenerator.addInnerClassWithName(inputClauses.get(i), testClass);
        }
    }

    private String instanceName(String testClass) {
        return testClass + "_INSTANCE";
    }
}