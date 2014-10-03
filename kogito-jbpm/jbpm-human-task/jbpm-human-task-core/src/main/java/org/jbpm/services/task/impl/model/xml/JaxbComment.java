package org.jbpm.services.task.impl.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.User;

@XmlRootElement(name="comment")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class JaxbComment extends AbstractJaxbTaskObject<Comment> implements Comment {

    @XmlElement
    @XmlSchemaType(name = "long")
    private Long id;

    @XmlElement
    @XmlSchemaType(name = "string")
    private String text;
    
    @XmlElement(name="added-by")
    @XmlSchemaType(name = "string")
    private String addedBy;
    
    @XmlElement(name="added-at")
    @XmlSchemaType(name = "dateTime")
    private Date addedAt;

    public JaxbComment() { 
        super(Comment.class);
    }
    
    public JaxbComment(Comment comment) { 
        super(comment, Comment.class);
        if( comment != null ) { 
            User adder = comment.getAddedBy();
            if( adder != null ) { 
                this.addedBy = adder.getId();
            }
        }
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
        return new GetterUser(this.addedBy);
    }

    @Override
    public Date getAddedAt() {
        return addedAt;
    } 
}
