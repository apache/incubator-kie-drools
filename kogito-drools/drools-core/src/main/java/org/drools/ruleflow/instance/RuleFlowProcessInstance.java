package org.drools.ruleflow.instance;

import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.instance.impl.WorkflowProcessInstanceImpl;

public class RuleFlowProcessInstance extends WorkflowProcessInstanceImpl {

    private static final long serialVersionUID = 400L;
    
    public RuleFlowProcess getRuleFlowProcess() {
        return (RuleFlowProcess) getProcess();
    }

    public void internalStart() {
        getNodeInstance( getRuleFlowProcess().getStart() ).trigger( null, null );
    }

}
