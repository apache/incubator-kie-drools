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

package org.jbpm.task.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.EventKeys;
import org.jbpm.eventmessaging.EventTriggerTransport;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.event.entity.TaskClaimedEvent;
import org.jbpm.task.event.entity.TaskCompletedEvent;
import org.jbpm.task.event.entity.TaskCreatedEvent;
import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.event.entity.TaskFailedEvent;
import org.jbpm.task.event.entity.TaskForwardedEvent;
import org.jbpm.task.event.entity.TaskReleasedEvent;
import org.jbpm.task.event.entity.TaskSkippedEvent;
import org.jbpm.task.event.entity.TaskStartedEvent;
import org.jbpm.task.event.entity.TaskStoppedEvent;
import org.jbpm.task.event.entity.TaskUserEvent;

public class MessagingTaskEventListener implements TaskEventListener {
	
    private EventKeys keys;
    
    public MessagingTaskEventListener(EventKeys keys) {
        this.keys = keys;
    }

    // --------------------
    // Private help methods
    // --------------------
    
    private List<EventTriggerTransport> getTargets(EventKey key, EventKey generalKey ) { 
        List<EventTriggerTransport> targets = keys.getTargets( key );
        if ( targets == null ){
            targets = keys.getTargets( generalKey );
            if (targets == null) {
                return null;
            } else { 
                targets = new ArrayList<EventTriggerTransport>(targets);
            }
        } else {
            targets = new ArrayList<EventTriggerTransport>(targets);
           
            // additional targets
            List<EventTriggerTransport> additionalTargets = keys.getTargets( generalKey );
            if (additionalTargets != null) {
                targets.addAll(additionalTargets);
            }
        }
        return targets;
    }

    private void triggerPayload(TaskEvent event, List<EventTriggerTransport> targets) { 
        Payload payload = new EventPayload( event );
        for ( Iterator<EventTriggerTransport> it = targets.iterator(); it.hasNext(); ) {
            EventTriggerTransport target = it.next();
            target.trigger( payload );
            if ( target.isRemove() ) {
                it.remove();
            }
        }
    }
    
    private void handleEvent(Class<? extends TaskEvent> taskEventClass, TaskUserEvent event) {
        EventKey key = new TaskEventKey(taskEventClass, event.getTaskId() );
        EventKey generalKey = new TaskEventKey(taskEventClass, -1);
        List<EventTriggerTransport> targets = getTargets(key, generalKey);

        if( targets == null ) { 
            return;
        }
        
        triggerPayload(event, targets);
        
        // Remove key if necessary
        if ( targets.isEmpty() ) {
            keys.removeKey( key );
        }
    }
    
    // ----------------
    // Listener methods
    // ----------------
    
    public void taskClaimed(TaskUserEvent event) {        
       handleEvent(TaskClaimedEvent.class, event);
    }

    public void taskCompleted(TaskUserEvent event) {
       handleEvent(TaskCompletedEvent.class, event);
    }

	public void taskFailed(TaskUserEvent event) {
       handleEvent(TaskFailedEvent.class, event);
	}

	public void taskSkipped(TaskUserEvent event) {
       handleEvent(TaskSkippedEvent.class, event);
	}

    public void taskCreated(TaskUserEvent event) {
       handleEvent(TaskCreatedEvent.class, event);
    }

    public void taskStarted(TaskUserEvent event) {
       handleEvent(TaskStartedEvent.class, event);
    }

    public void taskStopped(TaskUserEvent event) {
       handleEvent(TaskStoppedEvent.class, event);
    }

    public void taskReleased(TaskUserEvent event) {
       handleEvent(TaskReleasedEvent.class, event);
    }

    public void taskForwarded(TaskUserEvent event) {
       handleEvent(TaskForwardedEvent.class, event);
    }

}
