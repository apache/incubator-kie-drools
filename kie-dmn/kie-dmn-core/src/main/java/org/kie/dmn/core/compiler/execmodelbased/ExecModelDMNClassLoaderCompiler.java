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

import java.util.Optional;

import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNEvaluatorCompiler;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.DecisionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.compiler.generators.GeneratorsUtil.getDecisionTableName;

public class ExecModelDMNClassLoaderCompiler extends DMNEvaluatorCompiler {

    private DMNRuleClassFile dmnRuleClassFile;

    static final Logger logger = LoggerFactory.getLogger(ExecModelDMNEvaluatorCompiler.class);


    public ExecModelDMNClassLoaderCompiler(DMNCompilerImpl compiler, DMNRuleClassFile dmnRuleClassFile) {
        super(compiler);
        this.dmnRuleClassFile = dmnRuleClassFile;
    }

    @Override
    protected DMNExpressionEvaluator compileDecisionTable(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String dtName, DecisionTable dt) {
        String decisionName = getDecisionTableName(dtName, dt);
        // This is used just to get the compiled class name, but it only needs the namespace and the table name. exec model DTableModel is used instead
        DTableModel dTableModel = new DTableModel(ctx.getFeelHelper(), model, dtName, decisionName, dt);
        String evaluatorClassName = dTableModel.getGeneratedClassName(ExecModelDMNEvaluatorCompiler.GeneratorsEnum.EVALUATOR.getType());
        Optional<String> generatedClass = dmnRuleClassFile.getCompiledClass(evaluatorClassName);

        return generatedClass.map(gc -> {
            try {
                Class<?> evaluatorClass = getRootClassLoader().loadClass(gc);
                AbstractModelEvaluator evaluatorInstance = (AbstractModelEvaluator) evaluatorClass.newInstance();

                String feelExpressionClassName = dTableModel.getGeneratedClassName(ExecModelDMNEvaluatorCompiler.GeneratorsEnum.FEEL_EXPRESSION.getType());
                Class<?> feelExpressionClass = getRootClassLoader().loadClass(feelExpressionClassName);
                DTableModel execModelDTableModel = new ExecModelDTableModel(ctx.getFeelHelper(), model, dtName, decisionName, dt, feelExpressionClass);

                evaluatorInstance.initParameters(ctx, execModelDTableModel, node);

                logger.debug("Read compiled evaluator from class loader: " + evaluatorClassName);
                return evaluatorInstance;
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                throw new RuntimeException("Cannot instantiate class" + e);
            }
        }).orElseThrow(() -> new RuntimeException("No evaluator class found in file: " + dmnRuleClassFile));
    }
}
