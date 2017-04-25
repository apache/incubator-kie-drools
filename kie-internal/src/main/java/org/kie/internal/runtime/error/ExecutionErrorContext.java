/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.error;

import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.task.model.Task;

public class ExecutionErrorContext {

    private Throwable cause;
    
    private NodeInstance lastExecutedNode;
    
    private Task lastExecutedTask;
    
    public ExecutionErrorContext(Throwable cause, NodeInstance lastExecutedNode, Task lastExecutedTask) {
        super();
        this.cause = cause;
        this.lastExecutedNode = lastExecutedNode;
        this.lastExecutedTask = lastExecutedTask;
    }

    public Throwable getCause() {
        return cause;
    }
    
    public NodeInstance getLastExecutedNode() {
        return lastExecutedNode;
    }
    
    public Task getLastExecutedTask() {
        return lastExecutedTask;
    }

    @Override
    public String toString() {
        return "ExecutionErrorContext [cause=" + cause + ", lastExecutedNode=" + lastExecutedNode 
                + ", lastExecutedTask=" + lastExecutedTask + "]";
    }
    
}
