package org.jbpm.process.audit.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;

@XmlRootElement(name="variable-instance-log")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbVariableInstanceLog extends AbstractJaxbHistoryObject<VariableInstanceLog> {

    @XmlAttribute
    @XmlSchemaType(name="long")
    private Long id;
    
    @XmlElement(name="process-instance-id")
    @XmlSchemaType(name="long")
    private Long processInstanceId;
    
    @XmlElement(name="process-id")
    @XmlSchemaType(name="string")
    private String processId;
    
    @XmlElement
    @XmlSchemaType(name = "dateTime")
    private Date date;
    
    @XmlElement(name="variable-instance-id")
    @XmlSchemaType(name = "string")
    private String variableInstanceId;
    
    @XmlElement(name="variable-id")
    @XmlSchemaType(name = "string")
    private String variableId;    
    
    @XmlElement
    @XmlSchemaType(name = "string")
    private String value;
    
    @XmlElement
    @XmlSchemaType(name = "string")
    private String oldValue;    
    
    @XmlElement(name="external-id")
    @XmlSchemaType(name = "string")
    private String externalId;
    
    public JaxbVariableInstanceLog() { 
        super(VariableInstanceLog.class);
    }
    
    public JaxbVariableInstanceLog(VariableInstanceLog origLog) { 
       super(origLog, VariableInstanceLog.class);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVariableInstanceId() {
        return variableInstanceId;
    }

    public void setVariableInstanceId(String variableInstanceId) {
        this.variableInstanceId = variableInstanceId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}
