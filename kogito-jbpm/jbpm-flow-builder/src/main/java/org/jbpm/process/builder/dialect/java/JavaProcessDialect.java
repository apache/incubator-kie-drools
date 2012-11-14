package org.jbpm.process.builder.dialect.java;

import java.util.Iterator;

import org.kie.definition.process.Process;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ProcessErrorHandler;
import org.jbpm.process.builder.ProcessInvokerErrorHandler;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.ProcessDialect;

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
		javaDialect.addClassCompileTask(context.getPkg().getName() + "."
				+ processDescr.getClassName(), processDescr, processClass,
				null, new ProcessErrorHandler(processDescr, process,
						"Process Compilation error"));

		JavaDialectRuntimeData data = (JavaDialectRuntimeData) context.getPkg()
			.getDialectRuntimeRegistry().getDialectData(javaDialect.getId());

		for (final Iterator it = context.getInvokers().keySet().iterator(); it
				.hasNext();) {
			final String className = (String) it.next();

			// Check if an invoker - Action has been associated
			// If so we add it to the PackageCompilationData as it will get
			// wired up on compilation
			final Object invoker = context.getInvokerLookups().get(className);
			if (invoker != null) {
				data.putInvoker(className, invoker);
			}
			final String text = (String) context.getInvokers().get(className);

			final BaseDescr descr = (BaseDescr) context.getDescrLookups().get(
					className);
			javaDialect.addClassCompileTask(className, descr, text, null,
					new ProcessInvokerErrorHandler(processDescr, process,
							"Unable to generate action invoker."));

		}

		// setup the line mappins for this rule
		// @TODO must setup mappings
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
		throw new UnsupportedOperationException(
			"Java assignments not supported");
	}

}
