/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.jsr223;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNEvaluatorCompiler;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.jsr223.JSR223DTExpressionEvaluator.JSR223Rule;
import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.UnaryTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSR223EvaluatorCompiler extends DMNEvaluatorCompiler {
    private static final Logger LOG = LoggerFactory.getLogger( JSR223EvaluatorCompiler.class );
    private final ScriptEngineManager SEMANAGER;
    
    public JSR223EvaluatorCompiler(DMNCompilerImpl compiler) { // TODO for composition, need DMNEvaluatorCompiler not to rely on self-def methods but go again via compilerConfig defined compiler.
        super(compiler);
        SEMANAGER = new ScriptEngineManager();
        LOG.debug("ScriptEngineFactories:");
        for (ScriptEngineFactory factory : SEMANAGER.getEngineFactories()) {
            printScriptEngineFactoryInfo(factory);
        }
    }

    @Override
    public DMNExpressionEvaluator compileExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, Expression expression) {
        if (expression instanceof LiteralExpression) {
            return compileLiteralExpr(ctx, model, node, exprName, (LiteralExpression) expression);
        } else if ( expression instanceof DecisionTable) {
            return compileDecisionTable(ctx, model, node, exprName, (DecisionTable) expression);
        } else {
            return super.compileExpression(ctx, model, node, exprName, expression);
        }
    }
    
    protected DMNExpressionEvaluator compileDecisionTable(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String dtName, DecisionTable dt) {
        if (model.getDefinitions().getExpressionLanguage().equals(model.getDefinitions().getURIFEEL())) {
            return super.compileDecisionTable(ctx, model, node, dtName, dt);
        }
        LOG.debug("exprLanguage {}", model.getDefinitions().getExpressionLanguage());
        List<JSR223LiteralExpressionEvaluator> ins = new ArrayList<>();
        for (InputClause input : dt.getInput()) {
            LiteralExpression inExpr = input.getInputExpression();
            normalizeLiteralExpressionInTable(model, inExpr);
            JSR223LiteralExpressionEvaluator inLiteralExpr = (JSR223LiteralExpressionEvaluator) compileLiteralExpr(ctx, model, node, dtName, inExpr);
            ins.add(inLiteralExpr);
        }
        if (dt.getOutput().size() != 1) {
            throw new UnsupportedOperationException("In JSR223 context, the DecisionTable must have only 1 output; for composite, use natural idiom of the expression language, eg: `{\"a\":1, \"b\":2}` etc.");
        }
        List<JSR223Rule> rules = new ArrayList<>();
        for (DecisionRule rule : dt.getRule()) {
            List<JSR223ScriptEngineEvaluator> ruleTests = new ArrayList<>();
            for (UnaryTests ie : rule.getInputEntry()) {
                normalizeUnaryTestsInTable(model, ie);
                JSR223ScriptEngineEvaluator ruleTest = compileUnaryTests(ctx, model, node, dtName, ie);
                ruleTests.add(ruleTest);
            }
            if (rule.getOutputEntry().size() != 1) {
                throw new IllegalStateException("inconsistent with OutputClause size.");
            }
            LiteralExpression outExpr = rule.getOutputEntry().get(0);
            normalizeLiteralExpressionInTable(model, outExpr);
            JSR223LiteralExpressionEvaluator outLiteralExpr = (JSR223LiteralExpressionEvaluator) compileLiteralExpr(ctx, model, node, dtName, outExpr);
            rules.add(new JSR223Rule(ruleTests, outLiteralExpr));
        }
        return new JSR223DTExpressionEvaluator(node, dt, ins, rules);
    }
    
    /**
     * internal ONLY implementation
     */
    private JSR223ScriptEngineEvaluator compileUnaryTests(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, UnaryTests expression) {
        final ScriptEngine efEngine = getScriptEngine(expression.getExpressionLanguage());
        return new JSR223ScriptEngineEvaluator(efEngine, expression.getText());
    }
    
    private void normalizeUnaryTestsInTable(DMNModelImpl model, UnaryTests ut) {
        if (!Optional.ofNullable(ut.getExpressionLanguage()).orElse("").isEmpty()) {
            throw new UnsupportedOperationException("In JSR223 context, the DecisionTable must be consistent in the expressionLanguage of its constituents elements (inputClause, outputClause, Rule)");
        }
        ut.setExpressionLanguage(model.getDefinitions().getExpressionLanguage());
    }

    private void normalizeLiteralExpressionInTable(DMNModelImpl model, LiteralExpression lExpr) {
        if (!Optional.ofNullable(lExpr.getExpressionLanguage()).orElse("").isEmpty()) {
            throw new UnsupportedOperationException("In JSR223 context, the DecisionTable must be consistent in the expressionLanguage of its constituents elements (inputClause, outputClause, Rule)");
        }
        lExpr.setExpressionLanguage(model.getDefinitions().getExpressionLanguage());
    }

    protected DMNExpressionEvaluator compileLiteralExpr(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, LiteralExpression expression) {
        String exprLanguage = Optional.ofNullable(expression.getExpressionLanguage()).orElse(model.getDefinitions().getExpressionLanguage());
        if (!exprLanguage.equals(model.getDefinitions().getURIFEEL())) {
            LOG.debug("exprLanguage {}", exprLanguage);
            final ScriptEngine efEngine = getScriptEngine(exprLanguage);
            // TODO ensure pick only from explicit Requirements?
            final JSR223ScriptEngineEvaluator eval = new JSR223ScriptEngineEvaluator(efEngine, expression.getText());
            return new JSR223LiteralExpressionEvaluator(eval);
        } else {
            return super.compileExpression(ctx, model, node, exprName, expression);
        }
    }
    
    private ScriptEngine getScriptEngine(String exprLanguage) {
        ScriptEngine engine = SEMANAGER.getEngineByName(exprLanguage);
        // if (expression.getExpressionLanguage().equalsIgnoreCase("javascript")) { // TODO force ES6?
        //     NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        //     engine = factory.getScriptEngine("--language=es6");
        // }
        if (engine == null) {
            throw new IllegalStateException("was unable to locate scripting engine: "+exprLanguage);
        }
        LOG.debug("Selected ScriptEngine: {}", engine.getFactory().getEngineName());
        return engine;
    }

    private void printScriptEngineFactoryInfo(ScriptEngineFactory factory) {
        String engName = factory.getEngineName();
        String engVersion = factory.getEngineVersion();
        String langName = factory.getLanguageName();
        String langVersion = factory.getLanguageVersion();
        LOG.debug("Script Engine: {} {} {} {}", engName, engVersion, langName, langVersion);
        List<String> engNames = factory.getNames();
        for(String name : engNames) {
            LOG.debug("\tEngine Alias: {}", name);
        }
    }
}