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

public class MessagingTaskEventListener implements TaskEventListener {
	
    private EventKeys keys;
    
    public MessagingTaskEventListener(EventKeys keys) {
        this.keys = keys;
    }
    
    public void taskClaimed(TaskClaimedEvent event) {        
        EventKey key = new TaskEventKey(TaskClaimedEvent.class, event.getTaskId() );
        List<EventTriggerTransport> targets = null;
        if(keys.getTargets( key ) == null){
            return;
        }else{
            targets = new ArrayList<EventTriggerTransport>(keys.getTargets( key )); 
        }
        Payload payload = new EventPayload( event );
        for ( Iterator<EventTriggerTransport> it = targets.iterator(); it.hasNext(); ) {
            EventTriggerTransport target = it.next();
            target.trigger( payload );
            if ( target.isRemove() ) {
                it.remove();
            }
        }
        if ( targets.isEmpty() ) {
            keys.removeKey( key );
        }
    }

    public void taskCompleted(TaskCompletedEvent event) {
        EventKey key = new TaskEventKey(TaskCompletedEvent.class, event.getTaskId() );
        List<EventTriggerTransport> targets = null;
        if ( keys.getTargets( key ) == null ){
        	key = new TaskEventKey(TaskCompletedEvent.class, -1);
        	if(keys.getTargets( key ) == null){
            	return;
            } else {
            	targets = new ArrayList<EventTriggerTransport>(keys.getTargets( key ));
            }
        } else {
        	targets = new ArrayList<EventTriggerTransport>(keys.getTargets( key ));
                key = new TaskEventKey(TaskCompletedEvent.class, -1);
                if(keys.getTargets( key ) != null){
                    List<EventTriggerTransport> additionalTargets = new ArrayList<EventTriggerTransport>(keys.getTargets( key ));
                    if (additionalTargets != null) {
                            targets.addAll(additionalTargets);
                    }
                }
        }
        Payload payload = new EventPayload( event );
        for ( Iterator<EventTriggerTransport> it = targets.iterator(); it.hasNext(); ) {
            EventTriggerTransport target = it.next();
            target.trigger( payload );
            if ( target.isRemove() ) {
                it.remove();
            }
        }
        if ( targets.isEmpty() ) {
            keys.removeKey( key );
        }   
    }

    public void taskFailed(TaskFailedEvent event) {
        EventKey key = new TaskEventKey(TaskFailedEvent.class, event.getTaskId() );
        List<EventTriggerTransport> targets = null;
        if ( keys.getTargets( key ) == null ){
        	key = new TaskEventKey(TaskFailedEvent.class, -1);
        	if(keys.getTargets( key ) == null){
            	return;
            } else {
            	targets = new ArrayList<EventTriggerTransport>(keys.getTargets( key ));
            }
        } else {
                targets = new ArrayList<EventTriggerTransport>(keys.getTargets( key ));
        	key = new TaskEventKey(TaskFailedEvent.class, -1);
                if(keys.getTargets( key ) != null){
                    List<EventTriggerTransport> additionalTargets = new ArrayList<EventTriggerTransport>(keys.getTargets( key ));
                    if (additionalTargets != null) {
                            targets.addAll(additionalTargets);
                    }
                }
        }
        Payload payload = new EventPayload( event );
        for ( Iterator<EventTriggerTransport> it = targets.iterator(); it.hasNext(); ) {
            EventTriggerTransport target = it.next();
            target.trigger( payload );
            if ( target.isRemove() ) {
                it.remove();
            }
        }
        if ( targets.isEmpty() ) {
            keys.removeKey( key );
        }
	}

    public void taskSkipped(TaskSkippedEvent event) {
        EventKey key = new TaskEventKey(TaskSkippedEvent.class, event.getTaskId() );
        List<EventTriggerTransport> targets = null;
        if ( keys.getTargets( key ) == null ){
        	key = new TaskEventKey(TaskSkippedEvent.class, -1);
        	if(keys.getTargets( key ) == null){
            	return;
            } else {
            	targets = new ArrayList<EventTriggerTransport>(keys.getTargets( key ));
            }
        } else {
                targets = new ArrayList<EventTriggerTransport>(keys.getTargets( key ));
        	key = new TaskEventKey(TaskSkippedEvent.class, -1);
                if(keys.getTargets( key ) != null){
                    List<EventTriggerTransport> additionalTargets = new ArrayList<EventTriggerTransport>(keys.getTargets( key ));
                    if (additionalTargets != null) {
                            targets.addAll(additionalTargets);
                    }
                }
        }
        Payload payload = new EventPayload( event );
        for ( Iterator<EventTriggerTransport> it = targets.iterator(); it.hasNext(); ) {
            EventTriggerTransport target = it.next();
            target.trigger( payload );
            if ( target.isRemove() ) {
                it.remove();
            }
        }
        if ( targets.isEmpty() ) {
            keys.removeKey( key );
        }
	}

}
