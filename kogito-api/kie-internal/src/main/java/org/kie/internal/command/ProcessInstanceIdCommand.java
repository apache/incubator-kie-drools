package org.kie.internal.command;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.command.Command;

public abstract class ProcessInstanceIdCommand<T> implements Command<T> {

    private static final long serialVersionUID = -7958811586021197629L;
    
    @XmlElement(name="process-instance-id")
    @XmlSchemaType(name="long")
    protected Long processInstanceId;
    
    public void setProcessInstanceId(Long procInstId) { 
       this.processInstanceId = procInstId;
    }
    
    public Long getProcessInstanceId() { 
       return processInstanceId; 
    }
    
}
