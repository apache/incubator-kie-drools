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
            Field inputClauseField = clazz.getField("FEEL_EXPRESSION_ARRAY");
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
                Field inputClauseField = clazz.getField(FeelExpressionSourceGenerator.INPUT_CLAUSE_NAMESPACE + i + "_INSTANCE");
                column.compiledInputClause = (CompiledFEELExpression) inputClauseField.get(clazz);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
