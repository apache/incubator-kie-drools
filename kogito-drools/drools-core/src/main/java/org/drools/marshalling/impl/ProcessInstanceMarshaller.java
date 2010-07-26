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
import java.io.ObjectInputStream;

import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.ProcessInstance;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * A ProcessInstanceMarshaller must contain all the write/read logic for nodes
 * of a specific ProcessInstance. It colaborates with OutputMarshaller and
 * InputMarshaller, that delegates in a ProcessInstanceMarshaller to stream in/out runtime
 * information.
 * 
 * @see OutPutMarshaller
 * @see InputMarshaller
 * @see ProcessMarshallerRegistry
 * 
 * @author mfossati, salaboy
 */

public interface ProcessInstanceMarshaller {

	public void writeProcessInstance(MarshallerWriteContext context,
			ProcessInstance processInstance) throws IOException;
    public void writeProcessInstance(MarshallerWriteContext context,
			ProcessInstance processInstance, boolean includeVariables) throws IOException;

	public void writeNodeInstance(MarshallerWriteContext context,
			NodeInstance nodeInstance) throws IOException;

	public ProcessInstance readProcessInstance(MarshallerReaderContext context)
			throws IOException;
    public ProcessInstance readProcessInstance(MarshallerReaderContext context,
            boolean includeVariables) throws IOException;

	public NodeInstance readNodeInstance(MarshallerReaderContext context,
			NodeInstanceContainer nodeInstanceContainer,
			WorkflowProcessInstance processInstance) throws IOException;
}
