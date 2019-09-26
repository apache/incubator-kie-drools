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
import org.kie.dmn.core.compiler.generators.FeelExpressionSourceGenerator;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.model.api.DecisionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.core.compiler.generators.FeelExpressionSourceGenerator.INPUT_CLAUSE_NAMESPACE;
import static org.kie.dmn.core.compiler.generators.FeelExpressionSourceGenerator.getOutputName;
import static org.kie.dmn.core.compiler.generators.FeelExpressionSourceGenerator.instanceName;

public class ExecModelDTableModel extends DTableModel {

    private static final Logger logger = LoggerFactory.getLogger(ExecModelDTableModel.class);
    private final Class<?> clazz;

    public ExecModelDTableModel(DMNFEELHelper feel, DMNModelImpl model, String dtName, String tableName, DecisionTable dt, Class<?> feelExpressionClass) {
        super(feel, model, dtName, tableName, dt);
        clazz = feelExpressionClass;
    }

    @Override
    protected void initRows(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        logger.debug("Reading " + rows.size() + " rows from class loader");;
        CompiledFEELExpression[][] array = (CompiledFEELExpression[][]) readFieldWithRuntimeCheck( FeelExpressionSourceGenerator.FEEL_EXPRESSION_ARRAY_NAME);
        for (int i = 0; i < rows.size(); i++) {
            DRowModel row = rows.get(i);
            row.compiledOutputs = Arrays.asList(array[i]);
        }
    }

    @Override
    protected void initInputClauses(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        logger.debug("Reading " + columns.size() + " columns from class loader");
        iterateOverInputClauses((column, index) -> column.compiledInputClause = (CompiledFEELExpression) readFieldWithRuntimeCheck(instanceName(INPUT_CLAUSE_NAMESPACE + index)));
    }

    @Override
    protected void initOutputClauses(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        logger.debug("Reading " + outputs.size() + " outputs from class loader");
        iterateOverOutputClauses((output, defaultValue) -> output.compiledDefault = (CompiledFEELExpression) readFieldWithRuntimeCheck(instanceName(getOutputName(defaultValue))));
    }

    private Object readFieldWithRuntimeCheck(String fieldName) {
        try {
            Field field = clazz.getField(fieldName);
            return field.get(clazz);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
