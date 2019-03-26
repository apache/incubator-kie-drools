/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.core.transformation;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.kie.api.runtime.process.DataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of <code>DataTransformer</code> that is based on standard
 * Java scripting capabilities (javax.script).
 * By default it uses Rhino scripting engine for JavaScript evaluation. But supports 
 * all scripting engine that are compliant with JSR 223. It's just a matter of
 * placing them on classpath so Java itself can discover it and then new instance
 * of this class will be registered for that engine. <br/>
 * Allows to pass custom properties to the engine via property file that should be 
 * placed on root of the classpath named 'FQCN of the script engine factory'.properties
 * <br/>
 * When reading the properties file transformer recognizes three types of data:
 * <ul>
 * 	<li>boolean - when value is either true or false string</li>
 * 	<li>integer - when value is a number (matches \d+ regex)</li>
 * 	<li>string - default type</li>
 * </ul> 
 * return value of the expression is either:
 * <ul>
 * 	<li>value returned from scriptEngine.eval if not null</li>
 * 	<li>result of the output produced by the script engine - will be used only when eval returns null</li>
 * </ul>
 */
public class JavaScriptingDataTransformer implements DataTransformer {
	
	private static final Logger logger = LoggerFactory.getLogger(JavaScriptingDataTransformer.class);

	private ScriptEngineFactory factory;
	private ScriptEngine scriptEngine;
	private Map<String, Object> engineProperties = new HashMap<String, Object>();
	
	public JavaScriptingDataTransformer(ScriptEngineFactory factory) {
		this.factory = factory;
		this.scriptEngine = this.factory.getScriptEngine();
		registerAttributes();
	}
	
	@Override
	public Object transform(Object expression, Map<String, Object> parameters) {
		
		return evaluateExpression(expression, parameters);
	}

	@Override
	public Object compile(String expression, Map<String, Object> parameters) {
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
			Object result = null;
			StringWriter writer = new StringWriter();			
			
			ScriptContext context = new SimpleScriptContext();
			for (Map.Entry<String, Object> property : engineProperties.entrySet()) {
				context.setAttribute(property.getKey(), property.getValue(), ScriptContext.ENGINE_SCOPE);
			}
			Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.putAll(parameters);
			context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
			context.setWriter(writer);
			if (expression instanceof CompiledScript) {
				logger.debug("About to evaluate compiled expression {} with bindings {} on engine", expression, parameters, scriptEngine);
				result = ((CompiledScript) expression).eval(context);
			} else {
				logger.debug("About to evaluate expression {} with bindings {} on engine", expression, parameters, scriptEngine);
				result = scriptEngine.eval(expression.toString(), context);
			}
			if (result == null) {
				result = writer.toString();
			}
			return result;
		} catch (ScriptException e) {
			throw new RuntimeException("Error when evaluating script", e);
		}
	}
	
	protected void registerAttributes() {
		try {
			InputStream propsIn = this.getClass().getResourceAsStream("/"+this.factory.getClass().getName()+".properties");
			if (propsIn != null) {
				Properties props = new Properties();
				props.load(propsIn);
				for (String propertyName : props.stringPropertyNames()) {
					Object objectValue = resolveValue(props.getProperty(propertyName));
					if (objectValue != null) {
						engineProperties.put(propertyName, objectValue);
					}
				}
			}
		} catch (IOException e) {
			logger.error("Error while loading script engine properties", e);
		}
	}
	
	private Object resolveValue(String value) {
		if (value == null) {
			return null;
		}
		
		if (value.toLowerCase().matches("true|false")) {
			return Boolean.parseBoolean(value);
		} else if (value.matches("\\d+")) {
			return Integer.parseInt(value);
		}
		
		return value;
	}
}
