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

package org.drools.audit.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the working memory.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen </a>
 */
public class LogEvent { // implements Externalizable { This breaks XStream serialization !

	public static final int INSERTED                            = 1;
    public static final int UPDATED                             = 2;
    public static final int RETRACTED                           = 3;

    public static final int ACTIVATION_CREATED                  = 4;
    public static final int ACTIVATION_CANCELLED                = 5;
    public static final int BEFORE_ACTIVATION_FIRE              = 6;
    public static final int AFTER_ACTIVATION_FIRE               = 7;

    public static final int BEFORE_RULEFLOW_CREATED             = 8;
    public static final int AFTER_RULEFLOW_CREATED              = 9;
    public static final int BEFORE_RULEFLOW_COMPLETED           = 10;
    public static final int AFTER_RULEFLOW_COMPLETED            = 11;
    public static final int BEFORE_RULEFLOW_GROUP_ACTIVATED     = 12;
    public static final int AFTER_RULEFLOW_GROUP_ACTIVATED      = 13;
    public static final int BEFORE_RULEFLOW_GROUP_DEACTIVATED   = 14;
    public static final int AFTER_RULEFLOW_GROUP_DEACTIVATED    = 15;

    public static final int BEFORE_PACKAGE_ADDED                = 16;
    public static final int AFTER_PACKAGE_ADDED                 = 17;
    public static final int BEFORE_PACKAGE_REMOVED              = 18;
    public static final int AFTER_PACKAGE_REMOVED               = 19;
    public static final int BEFORE_RULE_ADDED                   = 20;
    public static final int AFTER_RULE_ADDED                    = 21;
    public static final int BEFORE_RULE_REMOVED                 = 22;
    public static final int AFTER_RULE_REMOVED                  = 23;

    public static final int BEFORE_RULEFLOW_NODE_TRIGGERED      = 24;
    public static final int AFTER_RULEFLOW_NODE_TRIGGERED       = 25;
    public static final int BEFORE_RULEFLOW_NODE_EXITED         = 26;
    public static final int AFTER_RULEFLOW_NODE_EXITED          = 27;
    
    public static final int BEFORE_TASK_INSTANCE_CREATED        = 28;
    public static final int AFTER_TASK_INSTANCE_CREATED         = 29;
    public static final int BEFORE_TASK_INSTANCE_COMPLETED      = 30;
    public static final int AFTER_TASK_INSTANCE_COMPLETED       = 31;
    
    public static final int BEFORE_VARIABLE_INSTANCE_CHANGED    = 32;
    public static final int AFTER_VARIABLE_INSTANCE_CHANGED     = 33;
    
    private int             type;

    public LogEvent() {
    }

    /**
     * Creates a new log event.
     * 
     * @param type The type of the log event.
     */
    public LogEvent(final int type) {
        this.type = type;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type    = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(type);
    }
    
    /**
     * Returns the type of the log event as defined in this class.
     * 
     * @return The type of the log event.
     */
    public int getType() {
        return this.type;
    }

}
