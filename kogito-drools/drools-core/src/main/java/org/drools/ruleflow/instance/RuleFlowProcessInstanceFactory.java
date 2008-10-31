package org.drools.ruleflow.instance;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.process.instance.InternalProcessInstance;
import org.drools.process.instance.ProcessInstanceFactory;

public class RuleFlowProcessInstanceFactory implements ProcessInstanceFactory, Externalizable {

    private static final long serialVersionUID = 400L;

    public InternalProcessInstance createProcessInstance() {
        return new RuleFlowProcessInstance();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }


}
