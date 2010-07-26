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

package org.drools.process.instance.impl.factory;

import org.drools.process.core.Context;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.AbstractContextInstance;
import org.drools.process.instance.impl.ContextInstanceFactory;

public class ReuseContextInstanceFactory implements ContextInstanceFactory {
    
    public final Class<? extends ContextInstance> cls;
    
    public ReuseContextInstanceFactory(Class<? extends ContextInstance> cls){
        this.cls = cls;
    }

	public ContextInstance getContextInstance(Context context, ContextInstanceContainer contextInstanceContainer, ProcessInstance processInstance) {    	
        ContextInstance result = contextInstanceContainer.getContextInstance( context.getType(), context.getId() );
        if (result != null) {
            return result;
        }
        try {
            AbstractContextInstance contextInstance = (AbstractContextInstance) cls.newInstance();
            contextInstance.setContextId(context.getId());
            contextInstance.setContextInstanceContainer(contextInstanceContainer);
            contextInstance.setProcessInstance(processInstance);
            contextInstanceContainer.addContextInstance(context.getType(), contextInstance);
            return contextInstance;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to instantiate context '"
                + this.cls.getName() + "': " + e.getMessage());
        }
	}

}
