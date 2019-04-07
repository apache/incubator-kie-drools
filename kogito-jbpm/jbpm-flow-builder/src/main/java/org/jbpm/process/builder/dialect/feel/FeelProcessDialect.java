/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.builder.dialect.feel;

import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.ProcessDialect;

public class FeelProcessDialect implements ProcessDialect {

	private static final ReturnValueEvaluatorBuilder returnValueEvaluatorBuilder = new FeelReturnValueEvaluatorBuilder();
	
	public void addProcess(final ProcessBuildContext context) {
        
	}

	public ActionBuilder getActionBuilder() {
	    throw new UnsupportedOperationException("FEEL actions not supported");
	}

	public ProcessClassBuilder getProcessClassBuilder() {
        throw new UnsupportedOperationException(
            "FeelProcessDialect.getProcessClassBuilder is not supported" );
	}

	public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
	    return returnValueEvaluatorBuilder;
	}

	public AssignmentBuilder getAssignmentBuilder() {
		throw new UnsupportedOperationException("FEEL assignments not supported");
	}

}
