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

package org.drools.audit.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A ruleflow event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the working memory.
 * It contains the process name and id.
 */
public class RuleFlowLogEvent extends LogEvent {

    private String processId;
    private String processName;
    private long processInstanceId;

    /**
     * Create a new ruleflow log event.
     * 
     * @param type The type of event.  This can only be RULEFLOW_CREATED,
     *        RULEFLOW_COMPLETED, RULEFLOW_NODE_START or RULEFLOW_NODE_END.
     * @param processId The id of the process
     * @param processName The name of the process
     */
    public RuleFlowLogEvent(final int type,
                            final String processId,
                            final String processName,
                            final long processInstanceId) {
        super( type );
        this.processId = processId;
        this.processName = processName;
        this.processInstanceId = processInstanceId;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        processId    = (String)in.readObject();
        processName    = (String)in.readObject();
        processInstanceId = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(processId);
        out.writeObject(processName);
        out.writeLong(processInstanceId);
    }

    public String getProcessId() {
        return this.processId;
    }

    public String getProcessName() {
        return this.processName;
    }
    
    public long getProcessInstanceId() {
        return this.processInstanceId;
    }

    public String toString() {
        String msg = null;
        switch ( this.getType() ) {
            case BEFORE_RULEFLOW_CREATED :
                msg = "BEFORE RULEFLOW STARTED";
                break;
            case AFTER_RULEFLOW_CREATED :
                msg = "AFTER RULEFLOW STARTED";
                break;
            case BEFORE_RULEFLOW_COMPLETED :
                msg = "BEFORE RULEFLOW COMPLETED";
                break;
            case AFTER_RULEFLOW_COMPLETED :
                msg = "AFTER RULEFLOW COMPLETED";
                break;
        }
        return msg + " process:" + this.processName + "[id=" + this.processId + "]";
    }
}
