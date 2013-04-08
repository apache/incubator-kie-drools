/*
 * Copyright 2013 JBoss Inc
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
package org.kie.internal.runtime.manager;

import java.util.List;
import java.util.Map;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.process.WorkItemHandler;

public interface RegisterableItemsFactory {

    Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime);
    
    List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime);
    
    List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime);
    
    List<WorkingMemoryEventListener> getWorkingMemoryEventListeners(RuntimeEngine runtime);
}
