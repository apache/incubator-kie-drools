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

/**
 *
 */
package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;

import org.drools.marshalling.impl.MarshallerWriteContext;

public interface WorkingMemoryAction extends Externalizable {
    public static final int WorkingMemoryReteAssertAction = 1;
    public static final int DeactivateCallback = 2;
    public static final int PropagateAction = 3;
    public static final int LogicalRetractCallback = 4;
    public static final int WorkingMemoryReteExpireAction = 5;
    public static final int SignalProcessInstanceAction = 6;
    public static final int SignalAction = 7;
    
    
    public void execute(InternalWorkingMemory workingMemory);
    
    public void write(MarshallerWriteContext context) throws IOException;
}