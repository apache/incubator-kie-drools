package org.jbpm.services.task.impl.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jbpm.services.task.impl.model.xml.adapter.UserXmlAdapter;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.User;

@XmlRootElement(name="attachment")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbComment extends AbstractJaxbTaskObject<Comment> implements Comment {

    @XmlElement
    @XmlSchemaType(name = "long")
    private Long id;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String text;
    
    @XmlElement(name="added-by")
    @XmlJavaTypeAdapter(value=UserXmlAdapter.class)
    private User addedBy;
    
    @XmlElement(name="added-at")
    @XmlSchemaType(name = "dateTime")
    private Date addedAt;

    public JaxbComment() { 
        super(Comment.class);
    }
    
    public JaxbComment(Comment comment) { 
        super(comment, Comment.class);
    }
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public User getAddedBy() {
        return addedBy;
    }

    @Override
    public Date getAddedAt() {
        return addedAt;
    } 
}
