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

package org.drools.marshalling.impl;

import java.io.IOException;

import org.drools.common.WorkingMemoryAction;
import org.drools.common.RuleFlowGroupImpl.DeactivateCallback;
import org.drools.common.TruthMaintenanceSystem.LogicalRetractCallback;
import org.drools.reteoo.PropagationQueuingNode.PropagateAction;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteExpireAction;

public class PersisterHelper {
    public static WorkingMemoryAction readWorkingMemoryAction(MarshallerReaderContext context) throws IOException, ClassNotFoundException {
        int type = context.readInt();
        switch(type) {
            case WorkingMemoryAction.WorkingMemoryReteAssertAction : {
                return new WorkingMemoryReteAssertAction(context);
            }
            case WorkingMemoryAction.DeactivateCallback : {
                return new DeactivateCallback(context);
            }
            case WorkingMemoryAction.PropagateAction : {
                return new PropagateAction(context);
            }
            case WorkingMemoryAction.LogicalRetractCallback : {
                return new LogicalRetractCallback(context);
            }
            case WorkingMemoryAction.WorkingMemoryReteExpireAction : {
                return new WorkingMemoryReteExpireAction(context);
            }
        }    
        return null;
    }
    
    public void write(MarshallerWriteContext context) throws IOException {
        
    }
}
