package org.drools.ruleflow.instance;

import java.io.Serializable;

import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceFactory;

public class RuleFlowProcessInstanceFactory implements ProcessInstanceFactory, Serializable {

    private static final long serialVersionUID = 400L;

    public ProcessInstance createProcessInstance() {
        return new RuleFlowProcessInstance();
    }
    
    

}
