package org.kie.dmn.core.compiler.execmodelbased;

import java.lang.reflect.Field;
import java.util.Map;

import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DecisionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecModelDTableModel extends DTableModel {

    static final Logger logger = LoggerFactory.getLogger(ExecModelDTableModel.class);
    private final String className;
    private final Class<?> clazz;

    public ExecModelDTableModel(DMNFEELHelper feel, DMNModelImpl model, String dtName, String tableName, DecisionTable dt, Class<?> feelExpressionClass) {
        super(feel, model, dtName, tableName, dt);
        className = getGeneratedClassName(ExecModelDMNEvaluatorCompiler.GeneratorsEnum.FEEL_EXPRESSION);
        clazz = feelExpressionClass;
    }

    @Override
    protected void initRows(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        // read init rows from here
        super.initRows(feelctx, compilationCache);
    }

    @Override
    protected void initInputClauses(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        try {
            for (int i = 0; i < columns.size(); i++) {
                DColumnModel column = columns.get(i);
                Field inputClauseField = clazz.getField("inputClause" + i + "_INSTANCE");
                column.compiledInputClause = (CompiledFEELExpression) inputClauseField.get(clazz);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
