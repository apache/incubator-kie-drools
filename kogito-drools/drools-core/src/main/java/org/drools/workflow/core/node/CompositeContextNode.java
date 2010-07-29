/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workflow.core.node;

import java.util.List;

import org.drools.process.core.Context;
import org.drools.process.core.ContextContainer;
import org.drools.process.core.context.AbstractContext;
import org.drools.process.core.impl.ContextContainerImpl;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class CompositeContextNode extends CompositeNode implements ContextContainer {

    private static final long serialVersionUID = 510l;
    
    private ContextContainer contextContainer = new ContextContainerImpl();

    public List<Context> getContexts(String contextType) {
        return this.contextContainer.getContexts(contextType);
    }
    
    public void addContext(Context context) {
        this.contextContainer.addContext(context);
        ((AbstractContext) context).setContextContainer(this);
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
        if (context != null) {
	        context = context.resolveContext(param);
	        if (context != null) {
	            return context;
	        }
        }
        return super.resolveContext(contextId, param);
    }

}
