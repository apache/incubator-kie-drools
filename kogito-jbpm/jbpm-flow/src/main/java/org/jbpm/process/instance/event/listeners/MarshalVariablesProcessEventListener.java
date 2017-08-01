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

package org.jbpm.process.instance.event.listeners;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.drools.core.marshalling.impl.ProcessMarshallerWriteContext;
import org.drools.core.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process event listener to be used with plugable variable strategies to make sure that upon process instance completion
 * process variables will be persisted in back end store. This is important as by default this was not required 
 * because process instance (that contains all variables) was removed from db any way and thus there was no need to trigger marshaling.
 * In case of external data store (e.g. data base over JPA or CMIS) this must be invoked otherwise data in external 
 * system might not be up to date with processing outcome from process instance.
 *
 */
public class MarshalVariablesProcessEventListener extends DefaultProcessEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MarshalVariablesProcessEventListener.class);

	public void afterProcessCompleted(ProcessCompletedEvent event) {
		ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) event.getKieRuntime().getEnvironment().get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
		
		VariableScopeInstance variableScope = 
        (VariableScopeInstance) ((WorkflowProcessInstance)event.getProcessInstance()).getContextInstance(VariableScope.VARIABLE_SCOPE);

		Map<String, Object> variables = variableScope.getVariables();
		
		for (Map.Entry<String, Object> variable : variables.entrySet()) {
		    logger.debug("Searching for applicable strategy to handle variable name '{}' value '{}'", variable.getKey(), variable.getValue());
			for (ObjectMarshallingStrategy strategy : strategies) {
				// skip default strategy as it requires context and anyway will not make any effect as variables
			    // are removed together with process instance
			    if (strategy instanceof SerializablePlaceholderResolverStrategy) {
				    continue;
				}
			    if (strategy.accept(variable.getValue())) {
			        logger.debug("Strategy of type {} found to handle variable '{}'", strategy, variable.getKey());
					try {
					    ProcessMarshallerWriteContext context = new ProcessMarshallerWriteContext(new ByteArrayOutputStream(), null, null, null, null, event.getKieRuntime().getEnvironment());
					    context.setProcessInstanceId(event.getProcessInstance().getId());
			            context.setState(ProcessMarshallerWriteContext.STATE_COMPLETED);
			            
						strategy.marshal(null, context, variable.getValue());
						logger.debug("Variable '{}' successfully persisted by strategy {}", variable.getKey(), strategy);
						break;
					} catch (Exception e) {
						logger.warn("Errer while storing process variable {} due to {}", variable.getKey(), e.getMessage());
						logger.debug("Variable marshal error:", e);
					}
				}
			}
		}
	}


}
