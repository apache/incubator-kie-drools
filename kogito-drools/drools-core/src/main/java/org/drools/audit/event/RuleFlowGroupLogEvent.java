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
 * A ruleflow-group event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the working memory.
 * It contains the ruleflow group name and its size.
 */
public class RuleFlowGroupLogEvent extends LogEvent {

    private String groupName;
    private int size;

    /**
     * Create a new ruleflow group log event.
     * 
     * @param type The type of event.  This can only be RULEFLOW_GROUP_ACTIVATED or RULEFLOW_GROUP_DEACTIVATED.
     * @param groupName The name of the ruleflow group
     * @param size The size of the ruleflow group
     */
    public RuleFlowGroupLogEvent(final int type,
                                 final String groupName,
                                 final int size) {
        super( type );
        this.groupName = groupName;
        this.size = size;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        groupName    = (String)in.readObject();
        size    = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(groupName);
        out.writeInt(size);
    }

    public String getGroupName() {
        return this.groupName;
    }

    public int getSize() {
        return this.size;
    }

    public String toString() {

        String msg = null;
        switch ( this.getType() ) {
            case BEFORE_RULEFLOW_GROUP_ACTIVATED :
                msg = "BEFORE RULEFLOW GROUP ACTIVATED";
                break;
            case AFTER_RULEFLOW_GROUP_ACTIVATED :
                msg = "AFTER RULEFLOW GROUP ACTIVATED";
                break;
            case BEFORE_RULEFLOW_GROUP_DEACTIVATED :
                msg = "BEFORE RULEFLOW GROUP DEACTIVATED";
                break;
            case AFTER_RULEFLOW_GROUP_DEACTIVATED :
                msg = "AFTER RULEFLOW GROUP DEACTIVATED";
                break;
        }
        return msg + " group:" + this.groupName + "[size=" + this.size + "]";
    }
}
