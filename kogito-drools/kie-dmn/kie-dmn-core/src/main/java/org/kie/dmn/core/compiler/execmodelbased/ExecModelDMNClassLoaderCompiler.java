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

public class ExecModelDMNClassLoaderCompiler extends DMNEvaluatorCompiler {

    private DMNRuleClassFile dmnRuleClassFile;

    static final Logger logger = LoggerFactory.getLogger(ExecModelDMNEvaluatorCompiler.class);


    public ExecModelDMNClassLoaderCompiler(DMNCompilerImpl compiler, DMNRuleClassFile dmnRuleClassFile) {
        super(compiler);
        this.dmnRuleClassFile = dmnRuleClassFile;
    }

    @Override
    protected DMNExpressionEvaluator compileDecisionTable(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String dtName, DecisionTable decisionTable) {
        DTableModel dTableModel = new DTableModel(ctx.getFeelHelper(), model, node.getName(), node.getName(), decisionTable);
        String className = dTableModel.getGeneratedClassName(ExecModelDMNEvaluatorCompiler.GeneratorsEnum.EVALUATOR);
        Optional<String> generatedClass = dmnRuleClassFile.getCompiledClass(className);

        return generatedClass.map(gc -> {
            try {
                Class<?> clazz = getRootClassLoader().loadClass(gc);
                AbstractModelEvaluator evaluatorInstance = (AbstractModelEvaluator) clazz.newInstance();
                evaluatorInstance.initParameters(ctx, dTableModel, node);

                logger.debug("Read compiled evaluator from class loader: " + className);
                return evaluatorInstance;
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).orElseThrow(() -> new RuntimeException("Cannot instantiate evaluator"));
    }
}
