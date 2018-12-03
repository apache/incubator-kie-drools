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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.model.api.DecisionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.compiler.execmodelbased.FeelExpressionSourceGenerator.INPUT_CLAUSE_NAMESPACE;
import static org.kie.dmn.core.compiler.execmodelbased.FeelExpressionSourceGenerator.getOutputName;
import static org.kie.dmn.core.compiler.execmodelbased.FeelExpressionSourceGenerator.instanceName;

public class ExecModelDTableModel extends DTableModel {

    static final Logger logger = LoggerFactory.getLogger(ExecModelDTableModel.class);
    private final Class<?> clazz;

    public ExecModelDTableModel(DMNFEELHelper feel, DMNModelImpl model, String dtName, String tableName, DecisionTable dt, Class<?> feelExpressionClass) {
        super(feel, model, dtName, tableName, dt);
        clazz = feelExpressionClass;
    }

    @Override
    protected void initRows(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        logger.info("Reading " + rows.size() + " rows from class loader");
        try {
            Field inputClauseField = clazz.getField(FeelExpressionSourceGenerator.FEEL_EXPRESSION_ARRAY_NAME);
            CompiledFEELExpression[][] array = (CompiledFEELExpression[][]) inputClauseField.get(clazz);
            for (int i = 0; i < rows.size(); i++) {
                DRowModel row = rows.get(i);
                row.compiledOutputs = Arrays.asList(array[i]);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void initInputClauses(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        logger.info("Reading " + columns.size() + " columns from class loader");
        try {
            for (int i = 0; i < columns.size(); i++) {
                DColumnModel column = columns.get(i);
                Field inputClauseField = clazz.getField( instanceName(INPUT_CLAUSE_NAMESPACE + i));
                column.compiledInputClause = (CompiledFEELExpression) inputClauseField.get(clazz);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void initOutputClauses(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        logger.info("Reading " + outputs.size() + " outputs from class loader");
        for (DOutputModel output : outputs) {
            output.outputValues = getOutputValuesTests( output );
            String defaultValue = output.outputClause.getDefaultOutputEntry() != null ? output.outputClause.getDefaultOutputEntry().getText() : null;
            if (defaultValue != null && !defaultValue.isEmpty()) {
                try {
                    Field outputClauseField = clazz.getField(instanceName(getOutputName(defaultValue)));
                    output.compiledDefault = (CompiledFEELExpression) outputClauseField.get(clazz);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
