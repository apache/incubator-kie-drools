/*
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

package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;

import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.ProtobufMessages;

public interface WorkingMemoryAction
    extends
    Externalizable {
    public static final short WorkingMemoryReteAssertAction  = 1;
    public static final short DeactivateCallback             = 2;
    public static final short PropagateAction                = 3;
    public static final short LogicalRetractCallback         = 4;
    public static final short WorkingMemoryReteExpireAction  = 5;
    public static final short SignalProcessInstanceAction    = 6;
    public static final short SignalAction                   = 7;
    public static final short WorkingMemoryBehahviourRetract = 8;

    public void execute(InternalWorkingMemory workingMemory);

    public void execute(InternalKnowledgeRuntime kruntime);

    public void write(MarshallerWriteContext context) throws IOException;    
    
    public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) throws IOException;
}
