package org.drools.marshalling.impl;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.swimlane.SwimlaneContextInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Marshaller class for RuleFlowProcessInstances
 * 
 * @author mfossati
 */

public class RuleFlowProcessInstanceMarshaller extends
		AbstractProcessInstanceMarshaller {

	public static RuleFlowProcessInstanceMarshaller INSTANCE = new RuleFlowProcessInstanceMarshaller();

	private RuleFlowProcessInstanceMarshaller() {
	}

	public ProcessInstance readProcessInstance(MarshallerReaderContext context)
			throws IOException {
		ObjectInputStream stream = context.stream;
		InternalRuleBase ruleBase = context.ruleBase;
		InternalWorkingMemory wm = context.wm;

		RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
		processInstance.setId(stream.readLong());
		String processId = stream.readUTF();
		processInstance.setProcessId(processId);
		if (ruleBase != null) {
			processInstance.setProcess(ruleBase.getProcess(processId));
		}
		processInstance.setState(stream.readInt());
		long nodeInstanceCounter = stream.readLong();
		processInstance.setWorkingMemory(wm);

		int nbVariables = stream.readInt();
		if (nbVariables > 0) {
			VariableScopeInstance variableScopeInstance = (VariableScopeInstance) processInstance
					.getContextInstance(VariableScope.VARIABLE_SCOPE);
			for (int i = 0; i < nbVariables; i++) {
				String name = stream.readUTF();
				try {
					Object value = stream.readObject();
					variableScopeInstance.setVariable(name, value);
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException(
							"Could not reload variable " + name);
				}
			}
		}

		int nbSwimlanes = stream.readInt();
		if (nbSwimlanes > 0) {
			SwimlaneContextInstance swimlaneContextInstance = (SwimlaneContextInstance) processInstance
					.getContextInstance(SwimlaneContext.SWIMLANE_SCOPE);
			for (int i = 0; i < nbSwimlanes; i++) {
				String name = stream.readUTF();
				String value = stream.readUTF();
				swimlaneContextInstance.setActorId(name, value);
			}
		}

		while (stream.readShort() == PersisterEnums.NODE_INSTANCE) {
			readNodeInstance(context, processInstance, processInstance);
		}

		processInstance.internalSetNodeInstanceCounter(nodeInstanceCounter);
		if (wm != null) {
			processInstance.reconnect();
		}
		return processInstance;
	}
}
