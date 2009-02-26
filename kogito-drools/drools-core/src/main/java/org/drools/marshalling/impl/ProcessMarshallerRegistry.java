package org.drools.marshalling.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;

/**
* Registry for Process/ProcessMarshaller
*/
public class ProcessMarshallerRegistry {

	public static ProcessMarshallerRegistry INSTANCE = new ProcessMarshallerRegistry();

	private Map<String , ProcessInstanceMarshaller> registry;

	private ProcessMarshallerRegistry() {
		 this.registry = new HashMap<String, ProcessInstanceMarshaller >();

	        // default logic that used to be in OutPutMarshaller:
	        register( RuleFlowProcess.RULEFLOW_TYPE,
	                  RuleFlowProcessInstanceMarshaller.INSTANCE );
	}

	public void register(String cls,
			ProcessInstanceMarshaller marchaller) {
		this.registry.put(cls, marchaller);
	}
	
	@SuppressWarnings("unchecked")
	public ProcessInstanceMarshaller getMarshaller(String type) {
		return this.registry.get(type);
	}
	

}
