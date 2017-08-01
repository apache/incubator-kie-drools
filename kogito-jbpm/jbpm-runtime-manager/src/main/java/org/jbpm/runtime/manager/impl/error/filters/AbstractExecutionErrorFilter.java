/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl.error.filters;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.runtime.error.ExecutionErrorContext;
import org.kie.internal.runtime.error.ExecutionErrorFilter;


public abstract class AbstractExecutionErrorFilter implements ExecutionErrorFilter {

    protected String getStackTrace(Throwable cause) {
        StringWriter writer = new StringWriter();
        PrintWriter p = new PrintWriter(writer);
        cause.printStackTrace(p);
        return writer.toString();
    }
    
    protected String nodeName(NodeInstance nodeInstance) {
        try {
            return nodeInstance.getNodeName();
        } catch (IllegalStateException e) {
            NodeInstanceLog nodeInstanceLog = (NodeInstanceLog) ((NodeInstanceImpl) nodeInstance).getMetaData("NodeInstanceLog");
            if (nodeInstanceLog != null) {
                return nodeInstanceLog.getNodeName();
            }
            
            return "";
        }
    }
    
    protected Long getInitActivityId(ExecutionErrorContext context) {
        if (context.getFirstExecutedNode() == null) {
            return null;
        }
        
        return context.getFirstExecutedNode().getId();
    }

}
