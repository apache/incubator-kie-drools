/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.lifecycle.listeners;

import org.jbpm.task.Task;
import org.jbpm.task.annotations.Internal;

/**
 *

 */
@Internal
public class InternalDeadlinesEventListener implements DeadlinesEventListener{

    public void afterTaskAddedEvent(Task ti) {
        
    }

    public void afterTaskStartedEvent(Task ti) {
       
    }

    public void afterTaskSkippedEvent(Task ti) {
        
    }

    public void afterTaskStoppedEvent(Task ti) {
        
    }

    public void afterTaskCompletedEvent(Task ti) {
        
    }

    public void afterTaskFailedEvent(Task ti) {
        
    }

    public void afterTaskExitedEvent(Task ti) {
        
    }
    
}
