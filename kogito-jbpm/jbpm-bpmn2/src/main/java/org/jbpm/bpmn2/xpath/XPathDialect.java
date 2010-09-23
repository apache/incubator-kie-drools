/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.bpmn2.xpath;

import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.ProcessDialect;

public class XPathDialect implements ProcessDialect {

    public static final String ID = "XPath";
    
	private static final XPathReturnValueEvaluatorBuilder RETURN_VALUE_EVALUATOR_BUILDER = new XPathReturnValueEvaluatorBuilder();
	
	public String getId() {
		return ID;
	}

	public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
		return RETURN_VALUE_EVALUATOR_BUILDER;
	}

	public void addProcess(ProcessBuildContext context) {
		
	}

	public ActionBuilder getActionBuilder() {
		throw new UnsupportedOperationException("XPath does not support actoins");
	}

	public ProcessClassBuilder getProcessClassBuilder() {
		throw new UnsupportedOperationException("XPath does not support class builder");
	}

}
