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

package org.jbpm.process.builder.dialect.java;

import java.util.Iterator;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.spi.Wireable;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ProcessErrorHandler;
import org.jbpm.process.builder.ProcessInvokerErrorHandler;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.kie.api.definition.process.Process;

public class JavaProcessDialect implements ProcessDialect {

	private static final ActionBuilder actionBuilder = new JavaActionBuilder();
	private static final ProcessClassBuilder processClassBuilder = new JavaProcessClassBuilder();
	private static final ReturnValueEvaluatorBuilder returnValueBuilder = new JavaReturnValueEvaluatorBuilder();
	
	public void addProcess(final ProcessBuildContext context) {
		JavaDialect javaDialect = (JavaDialect) context.getDialectRegistry().getDialect("java");
		String processClass = processClassBuilder.buildRule(context);
		if (processClass == null) {
			// nothing to compile.
			return;
		}

		final Process process = context.getProcess();
		final ProcessDescr processDescr = context.getProcessDescr();

		// The compilation result is for the entire rule, so difficult to
		// associate with any descr
		javaDialect.addClassCompileTask(
		        context.getPkg().getName() + "." + processDescr.getClassName(), 
		        processDescr, 
		        processClass,
				null, 
				new ProcessErrorHandler(processDescr, process, "Process Compilation error"));

		JavaDialectRuntimeData data = (JavaDialectRuntimeData) context.getPkg()
			.getDialectRuntimeRegistry().getDialectData(javaDialect.getId());

		for (final Iterator it = context.getInvokers().keySet().iterator(); it
				.hasNext();) {
			final String className = (String) it.next();

			// Check if an invoker - Action has been associated
			// If so we add it to the PackageCompilationData as it will get
			// wired up on compilation
			final Object invoker = context.getInvokerLookup(className);
			if (invoker != null && invoker instanceof Wireable) {
				data.putInvoker(className, (Wireable) invoker);
			}
			final String text = (String) context.getInvokers().get(className);

			final BaseDescr descr = (BaseDescr) context.getDescrLookup(className);
			javaDialect.addClassCompileTask(className, descr, text, null,
					new ProcessInvokerErrorHandler(processDescr, process,
							"Unable to generate action invoker."));

		}

		// setup the line mappins for this rule
		// TODO @TODO must setup mappings
		// final String name = this.pkg.getName() + "." + StringUtils.ucFirst(
		// ruleDescr.getClassName() );
		// final LineMappings mapping = new LineMappings( name );
		// mapping.setStartLine( ruleDescr.getConsequenceLine() );
		// mapping.setOffset( ruleDescr.getConsequenceOffset() );
		//
		// context.getPkg().getPackageCompilationData().getLineMappings().put(
		// name,
		// mapping );

	}

	public ActionBuilder getActionBuilder() {
		return actionBuilder;
	}

	public ProcessClassBuilder getProcessClassBuilder() {
		return processClassBuilder;
	}

	public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
		return returnValueBuilder;
	}

	public AssignmentBuilder getAssignmentBuilder() {
		throw new UnsupportedOperationException("Java assignments not supported");
	}

}
