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

package org.drools.process.instance;

import org.drools.Agenda;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.process.Process;
import org.drools.runtime.process.EventListener;
import org.drools.WorkingMemory;

/**
 * A process instance is the representation of a process during its execution.
 * It contains all the runtime status information about the running process.
 * A process can have multiple instances.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface ProcessInstance extends org.drools.runtime.process.ProcessInstance, ContextInstanceContainer, ContextableInstance {

    void setId(long id);

    void setProcess(Process process);

    Process getProcess();   

    void setState(int state);
    
    void setWorkingMemory(InternalWorkingMemory workingMemory);
    
    WorkingMemory getWorkingMemory();

    Agenda getAgenda();
    
    void start();
}
