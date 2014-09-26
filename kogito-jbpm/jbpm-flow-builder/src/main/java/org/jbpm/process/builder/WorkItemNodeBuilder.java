package org.jbpm.process.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.DataTransformer;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.Transformation;
import org.jbpm.workflow.core.node.WorkItemNode;

public class WorkItemNodeBuilder extends EventBasedNodeBuilder {

	public void build(Process process, ProcessDescr processDescr,
			ProcessBuildContext context, Node node) {
		super.build(process, processDescr, context, node);
		WorkflowProcess wfProcess = (WorkflowProcess) process;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("imports", wfProcess.getImports());
		parameters.put("classloader", context.getConfiguration().getClassLoader());
		
		for (DataAssociation dataAssociation: ((WorkItemNode) node).getInAssociations()) {
			Transformation transformation = dataAssociation.getTransformation();
			if (transformation != null) {
				
				
				DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
				transformation.setCompiledExpression(transformer.compile(transformation.getExpression(), parameters));
				
			}
			
			List<Assignment> assignments = dataAssociation.getAssignments();
			if (assignments != null) {
				for (Assignment assignment: assignments) {
					ProcessDialect dialect = ProcessDialectRegistry.getDialect( assignment.getDialect() );            
			    	dialect.getAssignmentBuilder().build( 
		    			context, assignment, 
		    			dataAssociation.getSources().get(0), 
		    			dataAssociation.getTarget(), 
		    			((WorkItemNode) node), true);
				}
			}
		}
		for (DataAssociation dataAssociation: ((WorkItemNode) node).getOutAssociations()) {
			Transformation transformation = dataAssociation.getTransformation();
			if (transformation != null) {
				
				DataTransformer transformer = DataTransformerRegistry.get().find(transformation.getLanguage());
				transformation.setCompiledExpression(transformer.compile(transformation.getExpression(), parameters));
				
			}
			List<Assignment> assignments = dataAssociation.getAssignments();
			if (assignments != null) {
				for (Assignment assignment: assignments) {
					ProcessDialect dialect = ProcessDialectRegistry.getDialect( assignment.getDialect() );            
			    	dialect.getAssignmentBuilder().build(
		    			context, assignment, 
		    			dataAssociation.getSources().get(0), 
		    			dataAssociation.getTarget(),
		    			((WorkItemNode) node), false);
				}
			}
		}
	}

}
