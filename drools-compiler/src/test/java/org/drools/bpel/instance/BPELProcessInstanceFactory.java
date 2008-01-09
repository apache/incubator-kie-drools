package org.drools.bpel.instance;

import java.io.Serializable;

import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceFactory;

public class BPELProcessInstanceFactory implements ProcessInstanceFactory, Serializable {

    private static final long serialVersionUID = 400L;

    public ProcessInstance createProcessInstance() {
        return new BPELProcessInstance();
    }
    
    

}
