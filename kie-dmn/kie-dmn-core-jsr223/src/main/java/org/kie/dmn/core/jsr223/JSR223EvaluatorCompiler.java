package org.kie.dmn.core.jsr223;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.api.EvaluatorResult.ResultType;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.EvaluatorResultImpl;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNEvaluatorCompiler;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.LiteralExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSR223EvaluatorCompiler extends DMNEvaluatorCompiler {
    private static final Logger LOG = LoggerFactory.getLogger( DMNEvaluatorCompiler.class );

    private final ObjectMapper MAPPER = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .build()
        //.configure(JsonWriteFeature.QUOTE_FIELD_NAMES.mappedFeature(), true)
        ;
    
    public JSR223EvaluatorCompiler(DMNCompilerImpl compiler) { // TODO for composition, need DMNEvaluatorCompiler not to rely on self-def methods but go again via compilerConfig defined compiler.
        super(compiler);
    }

    @Override
    public DMNExpressionEvaluator compileExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, Expression expression) {
        if (expression instanceof LiteralExpression) {
            return compileLiteralExpression(ctx, model, node, exprName, (LiteralExpression) expression);
        } else {
            return super.compileExpression(ctx, model, node, exprName, expression);
        }
    }

    private static double doubleValueExact(BigDecimal original) {
        double result = original.doubleValue();
        if (!(Double.isNaN(result) || Double.isInfinite(result))) {
            if (new BigDecimal(String.valueOf(result)).compareTo(original) == 0) {
                return result;
            }
        }
        throw new ArithmeticException(String.format("Conversion of %s incurred in loss of precision from BigDecimal", original));
    }

    /**
     * TODO PROVISIONAL, as this does not support non-latin characters, and without accents.
     */
    public static String escapeIdentifierForBinding(String original) {
        StringBuilder sb = new StringBuilder(original.length());
        Iterable<Integer> iterable = original.codePoints()::iterator;
        int i = 0;
        for (Integer cp : iterable) {
            if (i == 0) {
                if (cp >= '0' && cp <= '9') {
                    sb.append("_");
                }
            }
            if (cp >= '0' && cp <= '9') {
                sb.append((char) (int) cp);
            } else if (cp >= 'a' && cp <= 'z') {
                sb.append((char) (int) cp);
            } else if (cp >= 'A' && cp <= 'Z') {
                sb.append((char) (int) cp);
            } else {
                sb.append("_");
            }
            i++;
        }
        return sb.toString();
    }

    private DMNExpressionEvaluator compileLiteralExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, LiteralExpression expression) {
        String exprLanguage = Optional.ofNullable(expression.getExpressionLanguage()).orElse(""); // TODO check also default expression language in root <definitions>.
        LOG.info("exprLanguage {}", exprLanguage);
        if (exprLanguage.equals("")) {
            return super.compileExpression(ctx, model, node, exprName, expression);
        }
        if (!exprLanguage.contains("FEEL")) { // TODO use proper FEEL namespace checks
            final ScriptEngine efEngine = locateScriptEngine(exprLanguage);
            return new DMNExpressionEvaluator() {
                @Override
                public EvaluatorResult evaluate(DMNRuntimeEventManager dmrem, DMNResult dmnr) {
                    ScriptContext newContext = new SimpleScriptContext();
                    newContext.setBindings(efEngine.createBindings(), ScriptContext.ENGINE_SCOPE);
                    Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
                    DMNResultImpl result = (DMNResultImpl) dmnr;
                    for (Entry<String, Object> kv : result.getContext().getAll().entrySet()) { // TODO ensure pick only from explicit Requirements
                        String key = escapeIdentifierForBinding(kv.getKey());
                        Object value = kv.getValue();
                        if (value instanceof BigDecimal) {
                            value = doubleValueExact((BigDecimal) value);
                        }
                        if (value instanceof FEELFunction) {
                            LOG.trace("SKIP binding {} of {}", key, value);
                        } else {
                            LOG.info("Setting binding {} to {}", key, value);
                            engineScope.put(key, value);
                        }
                    }
                    Object evaluatorResult = null;
                    ResultType resultType = ResultType.SUCCESS;
                    try {
                        Object scriptResult = efEngine.eval(expression.getText(), newContext);
                        LOG.info("Script result: {}", scriptResult);
                        evaluatorResult = EvalHelper.coerceNumber(scriptResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                        resultType = ResultType.FAILURE;
                    }
                    return new EvaluatorResultImpl(evaluatorResult, resultType);
                }
            };
        } else {
            return super.compileExpression(ctx, model, node, exprName, expression);
        }
    }
    
    private ScriptEngine locateScriptEngine(String exprLanguage) {
        ScriptEngineManager manager = new ScriptEngineManager();
        LOG.info("ScriptEngineFactories:");
        for (ScriptEngineFactory factory : manager.getEngineFactories()) {
            printScriptEngineFactoryInfo(factory);
        }
        ScriptEngine engine = manager.getEngineByName(exprLanguage);
        // if (expression.getExpressionLanguage().equalsIgnoreCase("javascript")) { // TODO force ES6?
        //     NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        //     engine = factory.getScriptEngine("--language=es6");
        // }
        if (engine == null) {
            throw new IllegalStateException("was unable to locate scripting engine: "+exprLanguage);
        }
        LOG.info("Selected ScriptEngine: ");
        printScriptEngineFactoryInfo(engine.getFactory());
        return engine;
    }

    private void printScriptEngineFactoryInfo(ScriptEngineFactory factory) {
        String engName = factory.getEngineName();
        String engVersion = factory.getEngineVersion();
        String langName = factory.getLanguageName();
        String langVersion = factory.getLanguageVersion();
        LOG.info("Script Engine: {} {} {} {}", engName, engVersion, langName, langVersion);
        List<String> engNames = factory.getNames();
        for(String name : engNames) {
            LOG.info("\tEngine Alias: {}", name);
        }
    }
}