/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.common;

import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.time.TimerService;
import org.kie.internal.runtime.KnowledgeRuntime;

public interface InternalKnowledgeRuntime extends KnowledgeRuntime {

    TimerService getTimerService();

    void startOperation();

    void endOperation();

    void executeQueuedActions();

    void queueWorkingMemoryAction(WorkingMemoryAction action);

    InternalProcessRuntime getProcessRuntime();

    void setId(Long id);

    void setEndOperationListener(EndOperationListener listener);

    long getLastIdleTimestamp();

}
