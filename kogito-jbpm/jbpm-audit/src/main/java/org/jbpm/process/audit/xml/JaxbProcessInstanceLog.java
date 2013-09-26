package org.jbpm.process.audit.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.jbpm.process.audit.ProcessInstanceLog;

@XmlRootElement(name="process-instance-log")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbProcessInstanceLog extends AbstractJaxbHistoryObject<ProcessInstanceLog> {

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
    private Date start;
    
    @XmlElement
    @XmlSchemaType(name = "dateTime")
    private Date end;
    
    @XmlElement(nillable=true)
    @XmlSchemaType(name = "int")
    private Integer status;
 
    @XmlElement(nillable=true, name="parent-process-instance-id")
    @XmlSchemaType(name = "long")
    private Long parentProcessInstanceId;
    
    @XmlElement(nillable=true)
    @XmlSchemaType(name = "string")
    private String outcome;    
    
    @XmlElement
    @XmlSchemaType(name = "long")
    private Long duration;
    
    @XmlElement
    @XmlSchemaType(name = "string")
    private String identity;    
    
    @XmlElement(name="process-version")
    @XmlSchemaType(name = "string")
    private String processVersion;
    
    @XmlElement(name="process-name")
    @XmlSchemaType(name = "string")
    private String processName;
    
    @XmlElement(name="external-id")
    @XmlSchemaType(name = "string")
    private String externalId;
    
    public JaxbProcessInstanceLog() { 
        super(ProcessInstanceLog.class);
    }
    
    public JaxbProcessInstanceLog(ProcessInstanceLog processInstanceLog) { 
       super(processInstanceLog, ProcessInstanceLog.class);
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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(Long parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}
