package org.kie.dmn.core.jsr223;

import java.util.List;
import java.util.Optional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNEvaluatorCompiler;
import org.kie.dmn.core.impl.DMNModelImpl;
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

    private DMNExpressionEvaluator compileLiteralExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, LiteralExpression expression) {
        String exprLanguage = Optional.ofNullable(expression.getExpressionLanguage()).orElse(""); // TODO check also default expression language in root <definitions>.
        LOG.info("exprLanguage {}", exprLanguage);
        if (exprLanguage.equals("")) {
            return super.compileExpression(ctx, model, node, exprName, expression);
        }
        if (!exprLanguage.contains("FEEL")) { // TODO use proper FEEL namespace checks
            final ScriptEngine efEngine = locateScriptEngine(exprLanguage);
            // TODO ensure pick only from explicit Requirements?
            final JSR223ScriptEngineEvaluator eval = new JSR223ScriptEngineEvaluator(efEngine, expression.getText());
            return new JSR223LiteralExpressionEvaluator(eval);
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