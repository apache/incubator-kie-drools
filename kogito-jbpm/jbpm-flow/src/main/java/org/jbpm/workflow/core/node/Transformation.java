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

package org.jbpm.workflow.core.node;

import java.io.Serializable;

public class Transformation implements Serializable {

	private static final long serialVersionUID = 1641905060375832661L;

	private String source;
	private String language;
	private String expression;
	private Object compiledExpression;

	public Transformation(String lang, String expression) {
		this.language = lang;
		this.expression = expression;
	}
	
	public Transformation(String lang, String expression, String source) {
		this.language = lang;
		this.expression = expression;
		this.source = source;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
		
	public Object getCompiledExpression() {
		return compiledExpression;
	}

	public void setCompiledExpression(Object compliedExpression) {
		this.compiledExpression = compliedExpression;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
}
