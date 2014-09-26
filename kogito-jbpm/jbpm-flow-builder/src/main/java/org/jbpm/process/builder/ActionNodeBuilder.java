package org.jbpm.process.builder;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.DataTransformer;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.Transformation;

public class ActionNodeBuilder extends ExtendedNodeBuilder {

    public void build(Process process,
                      ProcessDescr processDescr,
                      ProcessBuildContext context,
                      Node node) {
    	super.build(process, processDescr, context, node);
        ActionNode actionNode = ( ActionNode ) node;
        DroolsConsequenceAction action = (DroolsConsequenceAction) actionNode.getAction();
        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( action.getConsequence() );   
        ProcessDialect dialect = ProcessDialectRegistry.getDialect( action.getDialect() );            
        dialect.getActionBuilder().build( context, action, actionDescr, (NodeImpl) node );
        
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
