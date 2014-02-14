package org.jbpm.services.task.impl.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.User;

@XmlRootElement(name="attachment")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbAttachment extends AbstractJaxbTaskObject<Attachment> implements Attachment {

    @XmlElement
    @XmlSchemaType(name = "long")
    private Long id;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String name;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String contentType;

    @XmlElement
    @XmlSchemaType(name = "dateTime")
    private Date attachedAt;

    @XmlElement(name="attached-by")
    @XmlSchemaType(name = "string")
    private String attachedBy;

    @XmlElement
    @XmlSchemaType(name = "size")
    private Integer size;

    @XmlElement(name="attachment-content-id")
    @XmlSchemaType(name = "long")
    private Long attachmentContentId;

    public JaxbAttachment() { 
        super(Attachment.class);
    }
    
    public JaxbAttachment(Attachment attachment) { 
        super(attachment, Attachment.class);
        User attacher = attachment.getAttachedBy();
        if( attacher != null ) { 
            this.attachedBy = attacher.getId();
        }
    }
        
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Date getAttachedAt() {
        return attachedAt;
    }

    @Override
    public User getAttachedBy() {
        return new GetterUser(this.attachedBy);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public long getAttachmentContentId() {
        return attachmentContentId;
    }

}
