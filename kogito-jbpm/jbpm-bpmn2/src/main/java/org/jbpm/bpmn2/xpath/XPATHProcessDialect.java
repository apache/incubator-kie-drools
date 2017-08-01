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

package org.jbpm.bpmn2.xpath;

import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.ProcessDialect;

/**
 * Please make sure to use the getter methods when referring to the static final fields, 
 * because this class is extended in other modules (jbpm-kie-services). 
 */
public class XPATHProcessDialect implements ProcessDialect {

	public static final String ID = "XPath";
	private static final ActionBuilder actionBuilder = new XPATHActionBuilder();
	private static final ReturnValueEvaluatorBuilder returnValueBuilder = new XPATHReturnValueEvaluatorBuilder();
	private static final AssignmentBuilder assignmentBuilder = new XPATHAssignmentBuilder();
	
	public void addProcess(final ProcessBuildContext context) {
        // @TODO setup line mappings
	}

	public ActionBuilder getActionBuilder() {
		return actionBuilder;
	}

	public ProcessClassBuilder getProcessClassBuilder() {
        throw new UnsupportedOperationException( "MVELProcessDialect.getProcessClassBuilder is not supported" );
	}

	public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
		return returnValueBuilder;
	}

	public AssignmentBuilder getAssignmentBuilder() {
		return assignmentBuilder;
	}

}
