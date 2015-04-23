/**
 * Copyright 2005 JBoss Inc
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

package org.jbpm.workflow.instance.node;

import java.io.Serializable;
import java.util.UUID;

import org.jbpm.process.core.async.AsyncSignalEventCommand;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime counterpart of an event node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class AsyncEventNodeInstance extends EventNodeInstance {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AsyncEventNodeInstance.class);
    
    private String eventType = UUID.randomUUID().toString();
    
    private EventListener listener = new AsyncExternalEventListener();

    public void internalTrigger(final NodeInstance from, String type) {

        super.internalTrigger(from, type);
    	
    	ExecutorService executorService = (ExecutorService) getProcessInstance().getKnowledgeRuntime().getEnvironment().get("ExecutorService");
    	if (executorService != null) {
    	    RuntimeManager runtimeManager = ((RuntimeManager)getProcessInstance().getKnowledgeRuntime().getEnvironment().get("RuntimeManager"));
    	    
    	    CommandContext ctx = new CommandContext();
            ctx.setData("DeploymentId", runtimeManager.getIdentifier());
            ctx.setData("ProcessInstanceId", getProcessInstance().getId());
            ctx.setData("Signal", getEventType());
            ctx.setData("Event", null);
            
            executorService.scheduleRequest(AsyncSignalEventCommand.class.getName(), ctx);
    	} else {
    	    logger.warn("No async executor service found continuing as sync operation...");
    	    // if there is no executor service available move as sync node
    	    triggerCompleted();
    	}
    }

    @Override
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
 
    @Override
    protected EventListener getEventListener() {
        return this.listener;
    }

    private class AsyncExternalEventListener implements EventListener, Serializable {
        private static final long serialVersionUID = 5L;
        public String[] getEventTypes() {
            return null;
        }
        public void signalEvent(String type,
                Object event) {
            triggerCompleted();
        }       
    }
}
