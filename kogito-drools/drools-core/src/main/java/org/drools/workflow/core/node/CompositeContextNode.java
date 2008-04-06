package org.drools.workflow.core.node;

import java.util.List;

import org.drools.process.core.Context;
import org.drools.process.core.ContextContainer;
import org.drools.process.core.impl.ContextContainerImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class CompositeContextNode extends CompositeNode implements ContextContainer {

    private static final long serialVersionUID = 400L;
    
    private ContextContainer contextContainer = new ContextContainerImpl();

    public List<Context> getContexts(String contextType) {
        return this.contextContainer.getContexts(contextType);
    }
    
    public void addContext(Context context) {
        this.contextContainer.addContext(context);
    }
    
    public Context getContext(String contextType, long id) {
        return this.contextContainer.getContext(contextType, id);
    }

    public void setDefaultContext(Context context) {
        this.contextContainer.setDefaultContext(context);
    }
    
    public Context getDefaultContext(String contextType) {
        return this.contextContainer.getDefaultContext(contextType);
    }

    public Context resolveContext(String contextId, Object param) {
        Context context = getDefaultContext(contextId);
        context = context.resolveContext(param);
        if (context != null) {
            return context;
        }
        return super.resolveContext(contextId, param);
    }

}
