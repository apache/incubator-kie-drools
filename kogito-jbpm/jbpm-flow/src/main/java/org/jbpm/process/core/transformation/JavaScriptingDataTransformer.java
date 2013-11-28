/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.process.core.transformation;

import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import org.kie.api.runtime.process.DataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of <code>DataTransformer</code> that is based on standard
 * Java scripting capabilities (javax.script).
 * By default it uses Rhino scripting engine for JavaScript evaluation. But supports 
 * all scripting engine that are compliant with JSR 223. It's just a matter of
 * placing them on classpath so Java itself can discover it and then new instance
 * of this class will be registered for that engine. 
 *
 */
public class JavaScriptingDataTransformer implements DataTransformer {
	
	private static final Logger logger = LoggerFactory.getLogger(JavaScriptingDataTransformer.class);

	private ScriptEngineFactory factory;
	private ScriptEngine scriptEngine;
	
	public JavaScriptingDataTransformer(ScriptEngineFactory factory) {
		this.factory = factory;
		this.scriptEngine = this.factory.getScriptEngine();
	}
	
	@Override
	public Object transform(Object expression, Map<String, Object> parameters) {
		
		return evaluateExpression(expression, parameters);
	}

	@Override
	public Object compile(String expression) {
		if (scriptEngine instanceof Compilable) {
			logger.debug("Compiling expression {} with engine {}", expression, scriptEngine);
			try {
				return ((Compilable) scriptEngine).compile(expression);
			} catch (ScriptException e) {
				throw new RuntimeException("Error when compiling script", e);
			}
		}
		logger.debug("Compilation not supported on engine {}", scriptEngine);
		return expression;
	}

	
	protected Object evaluateExpression(Object expression, Map<String, Object> parameters) {
		try {
			
			Bindings bindings = scriptEngine.createBindings();
			bindings.putAll(parameters);
			if (expression instanceof CompiledScript) {
				logger.debug("About to evaluate compiled expression {} with bindings {} on engine", expression, parameters, scriptEngine);
				return ((CompiledScript) expression).eval(bindings);
			}
			logger.debug("About to evaluate expression {} with bindings {} on engine", expression, parameters, scriptEngine);
			return scriptEngine.eval(expression.toString(), bindings);
		} catch (ScriptException e) {
			throw new RuntimeException("Error when evaluating script", e);
		}
	}
}
