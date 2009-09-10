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
