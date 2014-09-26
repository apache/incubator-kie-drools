package org.jbpm.process.builder;

import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.lang.descr.ProcessDescr;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.Transformation;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.DataTransformer;

public class StartNodeBuilder extends ExtendedNodeBuilder {

	@Override
	public void build(Process process, ProcessDescr processDescr, ProcessBuildContext context, Node node) {
		super.build(process, processDescr, context, node);
		
		Transformation transformation = (Transformation) node.getMetaData().get("Transformation");
		if (transformation != null) {
			WorkflowProcess wfProcess = (WorkflowProcess) process;
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("imports", wfProcess.getImports());
			parameters.put("classloader", context.getConfiguration().getClassLoader());
			
			DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
			transformation.setCompiledExpression(transformer.compile(transformation.getExpression(), parameters));
		}
	}

}
