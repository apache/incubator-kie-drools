package org.drools.process.instance.impl.factory;

import org.drools.process.core.Context;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.context.AbstractContextInstance;
import org.drools.process.instance.impl.ContextInstanceFactory;

public class ReuseContextInstanceFactory implements ContextInstanceFactory {
    
    public final Class<? extends ContextInstance> cls;
    
    public ReuseContextInstanceFactory(Class<? extends ContextInstance> cls){
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
            return contextInstance;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate context '"
                + this.cls.getName() + "': " + e.getMessage());
        }
	}

}
