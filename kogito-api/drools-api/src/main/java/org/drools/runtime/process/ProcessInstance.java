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

package org.drools.runtime.process;

public interface ProcessInstance
    extends
    EventListener {

    int STATE_PENDING   = 0;
    int STATE_ACTIVE    = 1;
    int STATE_COMPLETED = 2;
    int STATE_ABORTED   = 3;
    int STATE_SUSPENDED = 4;

    String getProcessId();

    long getId();

    String getProcessName();

    int getState();
    

    void signalEvent(String type, 
                     Object event);
}
