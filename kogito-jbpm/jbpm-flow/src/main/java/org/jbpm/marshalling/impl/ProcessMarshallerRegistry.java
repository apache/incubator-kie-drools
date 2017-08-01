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

package org.jbpm.marshalling.impl;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.ruleflow.core.RuleFlowProcess;

/**
 * Registry for Process/ProcessMarshaller
 */
public class ProcessMarshallerRegistry {

	public static ProcessMarshallerRegistry INSTANCE = new ProcessMarshallerRegistry();

	private Map<String, ProcessInstanceMarshaller> registry;

	private ProcessMarshallerRegistry() {
		this.registry = new HashMap<String, ProcessInstanceMarshaller>();
		register(RuleFlowProcess.RULEFLOW_TYPE,
                 ProtobufRuleFlowProcessInstanceMarshaller.INSTANCE);
	}

	public void register(String type, ProcessInstanceMarshaller marchaller) {
		this.registry.put(type, marchaller);
	}
	
	public ProcessInstanceMarshaller getMarshaller(String type) {
		return this.registry.get(type);
	}

}
