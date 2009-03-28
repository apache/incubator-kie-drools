package org.drools.marshalling.impl;

import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;

/**
 * Marshaller class for RuleFlowProcessInstances
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 * @author mfossati
 */

public class RuleFlowProcessInstanceMarshaller extends
		AbstractProcessInstanceMarshaller {

	public static RuleFlowProcessInstanceMarshaller INSTANCE = new RuleFlowProcessInstanceMarshaller();

	private RuleFlowProcessInstanceMarshaller() {
	}

	protected WorkflowProcessInstanceImpl createProcessInstance() {
		return new RuleFlowProcessInstance();
	}

}
