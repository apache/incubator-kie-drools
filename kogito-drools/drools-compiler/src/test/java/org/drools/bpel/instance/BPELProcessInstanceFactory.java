package org.drools.bpel.instance;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceFactory;

public class BPELProcessInstanceFactory implements ProcessInstanceFactory, Externalizable {

    private static final long serialVersionUID = 400L;

    public ProcessInstance createProcessInstance() {
        return new BPELProcessInstance();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }


}
