/*
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

package org.drools.event;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.ProcessInstance;

public class ProcessCompletedEventImpl extends ProcessEvent implements ProcessCompletedEvent {

    private static final long serialVersionUID = 510l;

    public ProcessCompletedEventImpl(final ProcessInstance instance, KnowledgeRuntime kruntime) {
        super( instance, kruntime );
    }

    public String toString() {
        return "==>[ProcessCompleted(name=" + getProcessInstance().getProcessName() + "; id=" + getProcessInstance().getProcessId() + ")]";
    }
}
