package org.kie.dmn.core.compiler.execmodelbased;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DecisionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecModelDTableModel extends DTableModel {

    private final ClassLoader rootClassLoader;
    private final DMNRuleClassFile dmnRuleClassFile;

    static final Logger logger = LoggerFactory.getLogger(ExecModelDTableModel.class);


    public ExecModelDTableModel(DMNFEELHelper feel, DMNModelImpl model, String dtName, String tableName, DecisionTable dt, ClassLoader rootClassLoader, DMNRuleClassFile dmnRuleClassFile) {
        super(feel, model, dtName, tableName, dt);
        this.rootClassLoader = rootClassLoader;
        this.dmnRuleClassFile = dmnRuleClassFile;
    }

    @Override
    protected void initRows(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        // read init rows from here
        super.initRows(feelctx, compilationCache);
    }

    @Override
    protected void initInputClauses(CompilerContext feelctx, Map<String, CompiledFEELExpression> compilationCache) {
        // read init input clauses from here
        final String className = getGeneratedClassName(ExecModelDMNEvaluatorCompiler.GeneratorsEnum.FEEL_EXPRESSION);

        try {
            Class<?> clazz = rootClassLoader.loadClass(className);

            logger.debug("Read compiled input clause from class loader: " + className);

            int index = 1;

            for (DColumnModel column : columns) {
                Field feel_expression_input_clauses = clazz.getField("inputClause" + index + "_INSTANCE");
                column.compiledInputClause = (CompiledFEELExpression) feel_expression_input_clauses.get(clazz);
                index++;
            }

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected CompiledFEELExpression compileFeelExpression(DMNElement element, DMNFEELHelper feel, CompilerContext feelctx, Msg.Message msg, Map<String, CompiledFEELExpression> compilationCache, String expr, int index) {
        // load from class loader
        final String className = getGeneratedClassName(ExecModelDMNEvaluatorCompiler.GeneratorsEnum.FEEL_EXPRESSION);
        Optional<String> generatedClass = dmnRuleClassFile.getCompiledClass(className);

        return generatedClass.map(gc -> {
            try {
                Class<?> clazz = rootClassLoader.loadClass(gc);

                logger.debug("Read compiled Feel Expression from class loader: " + className);

                CompiledFEELExpression expression1 = new CompiledFEELExpression() {
                    @Override
                    public Object apply(EvaluationContext evaluationContext) {
                        return null;
                    }
                };

                return expression1;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }).orElseThrow(() -> new RuntimeException("Cannot instantiate evaluator"));


    }
}
