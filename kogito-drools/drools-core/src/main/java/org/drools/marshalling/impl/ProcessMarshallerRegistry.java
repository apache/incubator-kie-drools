package org.drools.marshalling.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleflow.core.RuleFlowProcess;

/**
 * Registry for Process/ProcessMarshaller
 */
public class ProcessMarshallerRegistry {

	public static ProcessMarshallerRegistry INSTANCE = new ProcessMarshallerRegistry();

	private Map<String, ProcessInstanceMarshaller> registry;

	private ProcessMarshallerRegistry() {
		this.registry = new HashMap<String, ProcessInstanceMarshaller>();
		register(RuleFlowProcess.RULEFLOW_TYPE,
                 RuleFlowProcessInstanceMarshaller.INSTANCE);
	}

	public void register(String type, ProcessInstanceMarshaller marchaller) {
		this.registry.put(type, marchaller);
	}
	
	public ProcessInstanceMarshaller getMarshaller(String type) {
		return this.registry.get(type);
	}

}
