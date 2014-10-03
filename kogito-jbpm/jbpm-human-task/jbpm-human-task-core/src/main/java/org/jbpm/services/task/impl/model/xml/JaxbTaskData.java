package org.jbpm.services.task.impl.model.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;

@XmlRootElement(name = "task-data")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ JaxbComment.class, JaxbAttachment.class })
@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class JaxbTaskData extends AbstractJaxbTaskObject<TaskData> implements TaskData {

    @XmlElement
    private Status status;

    @XmlElement
    private Status previousStatus;

    @XmlElement(name = "actual-owner")
    private String actualOwner;

    @XmlElement(name = "created-by")
    private String createdBy;

    @XmlElement(name = "created-on")
    @XmlSchemaType(name = "dateTime")
    private Date createdOn;

    @XmlElement(name = "activation-time")
    @XmlSchemaType(name = "dateTime")
    private Date activationTime;

    @XmlElement(name = "expiration-time")
    @XmlSchemaType(name = "dateTime")
    private Date expirationTime;

    @XmlElement
    @XmlSchemaType(name = "boolean")
    private Boolean skipable;

    @XmlElement(name = "work-item-id")
    @XmlSchemaType(name = "long")
    private Long workItemId;

    @XmlElement(name = "process-instance-id")
    @XmlSchemaType(name = "long")
    private Long processInstanceId;

    @XmlElement(name = "document-type")
    @XmlSchemaType(name = "string")
    private String documentType;

    @XmlElement(name = "document-content-id")
    @XmlSchemaType(name = "long")
    private Long documentContentId;

    @XmlElement(name = "output-type")
    @XmlSchemaType(name = "string")
    private String outputType;

    @XmlElement(name = "output-content-id")
    @XmlSchemaType(name = "long")
    private Long outputContentId;

    @XmlElement(name = "fault-name")
    @XmlSchemaType(name = "string")
    private String faultName;

    @XmlElement(name = "fault-type")
    @XmlSchemaType(name = "string")
    private String faultType;

    @XmlElement(name = "fault-content-id")
    @XmlSchemaType(name = "long")
    private Long faultContentId;

    @XmlElement(name = "parent-id")
    @XmlSchemaType(name = "long")
    private Long parentId;

    @XmlElement(name = "process-id")
    @XmlSchemaType(name = "string")
    private String processId;

    @XmlElement(name = "process-session-id")
    @XmlSchemaType(name = "int")
    private Integer processSessionId;

    @XmlElement
    private List<JaxbComment> comments;

    @XmlElement
    private List<JaxbAttachment> attachments;
    
    @XmlElement(name = "deployment-id")
    @XmlSchemaType(name = "string")
    private String deploymentId;

    public JaxbTaskData() {
        super(TaskData.class);
    }

    public JaxbTaskData(TaskData taskData) {
        super(TaskData.class);
       
        this.activationTime = taskData.getActivationTime();
        User actualOwnerUser = taskData.getActualOwner();
        if( actualOwnerUser != null ) { 
            this.actualOwner = actualOwnerUser.getId();
        }
        if( taskData.getComments() != null ) { 
            List<JaxbComment> commentList = new ArrayList<JaxbComment>();
            for (Object comment : taskData.getComments() ) {
                commentList.add(new JaxbComment((Comment) comment));
            }
            this.comments = commentList;
        }
        User createdByUser = taskData.getCreatedBy();
        if( createdByUser != null ) { 
            this.createdBy = createdByUser.getId();
        }
        this.createdOn = taskData.getCreatedOn();
        this.deploymentId = taskData.getDeploymentId();
        this.documentContentId = taskData.getDocumentContentId();
        this.documentType = taskData.getDocumentType();
        this.expirationTime = taskData.getExpirationTime();
        this.faultContentId = taskData.getFaultContentId();
        this.faultName = taskData.getFaultName();
        this.faultType = taskData.getFaultType();
        this.outputContentId = taskData.getOutputContentId();
        this.outputType = taskData.getOutputType();
        this.parentId = taskData.getParentId();
        this.previousStatus = taskData.getPreviousStatus();
        this.processId = taskData.getProcessId();
        this.processInstanceId = taskData.getProcessInstanceId();
        this.processSessionId = taskData.getProcessSessionId();
        this.status = taskData.getStatus();
        this.skipable = taskData.isSkipable();
        this.workItemId = taskData.getWorkItemId();
        
        List<JaxbAttachment> attachList = new ArrayList<JaxbAttachment>();
        for (Object attach : taskData.getAttachments() ) { 
            attachList.add(new JaxbAttachment((Attachment) attach));
        }
        this.attachments = attachList;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Status getPreviousStatus() {
        return previousStatus;
    }

    @Override
    public User getActualOwner() {
        return new GetterUser(actualOwner);
    }

    public String getActualOwnerId() {
        return actualOwner;
    }

    public void setActualOwnerId(String actualOwnerId) {
        this.actualOwner = actualOwnerId;
    }

    @Override
    public User getCreatedBy() {
        return new GetterUser(createdBy);
    }

    public String getCreatedById() {
        return createdBy;
    }

    public void setCreatedById(String createdById) {
        this.createdBy = createdById;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public Date getActivationTime() {
        return activationTime;
    }

    @Override
    public Date getExpirationTime() {
        return expirationTime;
    }

    @Override
    public boolean isSkipable() {
        return skipable;
    }

    @Override
    public long getWorkItemId() {
        return workItemId;
    }

    @Override
    public long getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    @Override
    public int getProcessSessionId() {
        return processSessionId;
    }

    @Override
    public String getDocumentType() {
        return documentType;
    }

    @Override
    public long getDocumentContentId() {
        return documentContentId;
    }

    @Override
    public String getOutputType() {
        return outputType;
    }

    @Override
    public long getOutputContentId() {
        return outputContentId;
    }

    @Override
    public String getFaultName() {
        return faultName;
    }

    @Override
    public String getFaultType() {
        return faultType;
    }

    @Override
    public long getFaultContentId() {
        return faultContentId;
    }

    @Override
    public List<Comment> getComments() {
        List<Comment> commentList = new ArrayList<Comment>();
        if (comments != null) {
            for (JaxbComment jaxbComment : comments) {
                commentList.add(jaxbComment);
            }
        }
        return Collections.unmodifiableList(commentList);
    }

    public List<JaxbComment> getJaxbComments() {
        return comments;
    }

    public void setJaxbComments(List<JaxbComment> jaxbComments) {
        this.comments = jaxbComments;
    }

    @Override
    public List<Attachment> getAttachments() {
        List<Attachment> attachmentList = new ArrayList<Attachment>();
        if (attachments != null) {
            for (JaxbAttachment jaxbAttachment : attachments) {
                attachmentList.add(jaxbAttachment);
            }
        }
        return Collections.unmodifiableList(attachmentList);
    }

    public List<JaxbAttachment> getJaxbAttachments() {
        return attachments;
    }

    public void setJaxbAttachments(List<JaxbAttachment> jaxbAttachments) {
        this.attachments = jaxbAttachments;
    }

    @Override
    public long getParentId() {
        return parentId;
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPreviousStatus(Status previousStatus) {
        this.previousStatus = previousStatus;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public void setActivationTime(Date activationTime) {
        this.activationTime = activationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setSkipable(Boolean skipable) {
        this.skipable = skipable;
    }

    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setDocumentContentId(Long documentContentId) {
        this.documentContentId = documentContentId;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public void setOutputContentId(Long outputContentId) {
        this.outputContentId = outputContentId;
    }

    public void setFaultName(String faultName) {
        this.faultName = faultName;
    }

    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    public void setFaultContentId(Long faultContentId) {
        this.faultContentId = faultContentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setProcessSessionId(Integer processSessionId) {
        this.processSessionId = processSessionId;
    }

    public void setComments(List<JaxbComment> comments) {
        this.comments = comments;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

}
