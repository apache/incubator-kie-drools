package org.jbpm.process.audit.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;

@XmlRootElement(name="node-instance-log")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbNodeInstanceLog extends AbstractJaxbHistoryObject<NodeInstanceLog> {

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
    
    @XmlElement
    @XmlSchemaType(name = "int")
    private Integer type;
 
    @XmlElement(name="node-instance-id")
    @XmlSchemaType(name = "string")
    private String nodeInstanceId;
    
    @XmlElement(name="node-id")
    @XmlSchemaType(name = "string")
    private String nodeId;    
    
    @XmlElement(name="node-name")
    @XmlSchemaType(name = "string")
    private String nodeName;
    
    @XmlElement(name="node-type")
    @XmlSchemaType(name = "string")
    private String nodeType;    
    
    @XmlElement(name="work-item-id")
    @XmlSchemaType(name = "long")
    private long workItemId;
    
    @XmlElement
    @XmlSchemaType(name = "string")
    private String connection;
    
    @XmlElement(name="external-id")
    @XmlSchemaType(name = "string")
    private String externalId;
     
    public JaxbNodeInstanceLog() { 
        super(NodeInstanceLog.class);
    }
    
    public JaxbNodeInstanceLog(NodeInstanceLog origLog) { 
       super(origLog, NodeInstanceLog.class);
    }

    public Long getId() {
        return id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public Date getDate() {
        return date;
    }

    public Integer getType() {
        return type;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public String getConnection() {
        return connection;
    }

    public String getExternalId() {
        return externalId;
    }
    
}
