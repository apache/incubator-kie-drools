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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jbpm.services.task.impl.model.xml.adapter.StatusXmlAdapter;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskData;
import org.kie.api.task.model.User;

@XmlRootElement(name = "task-data")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ JaxbComment.class, JaxbAttachment.class })
public class JaxbTaskData extends AbstractJaxbTaskObject<TaskData> implements TaskData {

    @XmlElement
    @XmlJavaTypeAdapter(value = StatusXmlAdapter.class)
    private Status status;

    @XmlElement(name = "previous-status")
    @XmlJavaTypeAdapter(value = StatusXmlAdapter.class)
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
    @XmlSchemaType(name = "String")
    private String documentType;

    @XmlElement(name = "document-content-id")
    @XmlSchemaType(name = "long")
    private Long documentContentId;

    @XmlElement(name = "output-type")
    @XmlSchemaType(name = "String")
    private String outputType;

    @XmlElement(name = "output-content-id")
    @XmlSchemaType(name = "long")
    private Long outputContentId;

    @XmlElement(name = "fault-name")
    @XmlSchemaType(name = "String")
    private String faultName;

    @XmlElement(name = "fault-type")
    @XmlSchemaType(name = "String")
    private String faultType;

    @XmlElement(name = "fault-content-id")
    @XmlSchemaType(name = "long")
    private Long faultContentId;

    @XmlElement(name = "parent-id")
    @XmlSchemaType(name = "long")
    private Long parentId;

    @XmlElement(name = "process-id")
    @XmlSchemaType(name = "String")
    private String processId;

    @XmlElement(name = "process-session-id")
    @XmlSchemaType(name = "int")
    private Integer processSessionId;

    @XmlElement(name = "comment")
    private List<JaxbComment> comments;

    @XmlElement(name = "attachment")
    private List<JaxbAttachment> attachments;
    
    @XmlElement(name = "deployment-id")
    @XmlSchemaType(name = "String")
    private String deploymentId;

    public JaxbTaskData() {
        super(TaskData.class);
    }

    public JaxbTaskData(TaskData taskData) {
        super(taskData, TaskData.class);
        User createdByUser = taskData.getCreatedBy();
        if( createdByUser != null ) { 
            this.createdBy = createdByUser.getId();
        }
        User actualOwnerUser = taskData.getActualOwner();
        if( actualOwnerUser != null ) { 
            this.actualOwner = actualOwnerUser.getId();
        }
        List<JaxbComment> commentList = new ArrayList<JaxbComment>();
        for (Object comment : taskData.getComments() ) {
            commentList.add(new JaxbComment((Comment) comment));
        }
        this.comments = commentList;
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

    @Override
    public User getCreatedBy() {
        return new GetterUser(createdBy);
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

    @Override
    public long getParentId() {
        return parentId;
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

}
