package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.debug.DebugTools;

public class MVELConsequence implements Consequence, MVELCompileable,
		Externalizable {
	private static final long serialVersionUID = 400L;

	private MVELCompilationUnit unit;
	private String id;

	private Serializable expr;
	private DroolsMVELFactory prototype;

	public MVELConsequence() {
	}

	public MVELConsequence(final MVELCompilationUnit unit, final String id) {
		this.unit = unit;
		this.id = id;
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		unit = (MVELCompilationUnit) in.readObject();
		id = in.readUTF();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(unit);
		out.writeUTF(id);
	}

	public void compile(ClassLoader classLoader) {
		expr = unit.getCompiledExpression(classLoader);
		prototype = unit.getFactory();
	}

	public void evaluate(final KnowledgeHelper knowledgeHelper,
			final WorkingMemory workingMemory) throws Exception {
		DroolsMVELFactory factory = (DroolsMVELFactory) this.prototype.clone();

		factory.setContext(knowledgeHelper.getTuple(), knowledgeHelper, null,
				workingMemory, null);

		// do we have any functions for this namespace?
		Package pkg = workingMemory.getRuleBase().getPackage("MAIN");
		if (pkg != null) {
			MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg
					.getDialectRuntimeRegistry().getDialectData(this.id);
			factory.setNextFactory(data.getFunctionFactory());
		}

		CompiledExpression compexpr = (CompiledExpression) this.expr;

		// Receive breakpoints from debugger
		MVELDebugHandler.prepare();

		pkg = knowledgeHelper.getWorkingMemory().getRuleBase().getPackage(
				knowledgeHelper.getRule().getPackage());

		ClassLoader tempClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				((InternalRuleBase) workingMemory.getRuleBase())
						.getRootClassLoader());

		try {
	        if (MVELDebugHandler.isDebugMode()) {
	            if (MVELDebugHandler.verbose) {
	                System.out.println("Executing expression " + compexpr.getSourceName());
	                System.out.println(DebugTools.decompile(compexpr));
	            }
	            MVEL.executeDebugger(compexpr, null, factory);
	        } else {
	            MVEL.executeExpression(compexpr, null, factory);
	        }
		} finally {
	        Thread.currentThread().setContextClassLoader(tempClassLoader);
		}
	}

	public Serializable getCompExpr() {
		return expr;
	}

}
