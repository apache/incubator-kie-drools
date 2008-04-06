package org.drools.workflow.instance.context;

import org.drools.process.core.Context;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.context.AbstractContextInstance;
import org.drools.process.instance.impl.ContextInstanceFactory;
import org.drools.workflow.instance.NodeInstanceContainer;

public class WorkflowReuseContextInstanceFactory implements ContextInstanceFactory {
    
    public final Class<? extends ContextInstance> cls;
    
    public WorkflowReuseContextInstanceFactory(Class<? extends ContextInstance> cls){
        this.cls = cls;
    }

	public ContextInstance getContextInstance(Context context, ContextInstanceContainer contextInstanceContainer) {    	
        ContextInstance result = contextInstanceContainer.getContextInstance( context.getType(), context.getId() );
        if (result != null) {
            return result;
        }
        try {
            AbstractContextInstance contextInstance = (AbstractContextInstance) cls.newInstance();
            contextInstance.setContextId(context.getId());
            contextInstance.setContextInstanceContainer(contextInstanceContainer);
            NodeInstanceContainer nodeInstanceContainer = null;
            if (contextInstanceContainer instanceof NodeInstanceContainer) {
                nodeInstanceContainer = (NodeInstanceContainer) contextInstanceContainer;
            } else if (contextInstanceContainer instanceof ContextInstance) {
                ContextInstanceContainer parent = ((ContextInstance) contextInstanceContainer).getContextInstanceContainer();
                while (parent != null) {
                    if (parent instanceof NodeInstanceContainer) {
                        nodeInstanceContainer = (NodeInstanceContainer) parent;
                    } else if (contextInstanceContainer instanceof ContextInstance) {
                        parent = ((ContextInstance) contextInstanceContainer).getContextInstanceContainer();
                    } else {
                        parent = null;
                    }
                }
            }
            ((WorkflowContextInstance) contextInstance).setNodeInstanceContainer(nodeInstanceContainer);
            return contextInstance;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate context '"
                + this.cls.getName() + "': " + e.getMessage());
        }
	}

}
