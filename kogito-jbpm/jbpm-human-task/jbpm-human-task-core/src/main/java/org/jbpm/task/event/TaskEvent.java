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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.EventObject;

public abstract class TaskEvent extends EventObject implements Externalizable {
	
    private static final String dummySource = "";
    private long taskId;
    
    public TaskEvent() {
        super( dummySource );
    }
    
    public TaskEvent(long taskId) {
        super( taskId );
        this.taskId = taskId;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong( taskId );
    }  
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        taskId = in.readLong();
        source = taskId;
    }
    
    public long getTaskId() {
        return taskId;
    }
        
}
