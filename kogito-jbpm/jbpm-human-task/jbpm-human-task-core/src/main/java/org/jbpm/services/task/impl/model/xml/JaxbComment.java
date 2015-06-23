/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.services.task.impl.model.xml;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jbpm.services.task.impl.model.xml.InternalJaxbWrapper.GetterUser;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.User;

@XmlType(name="comment")
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

    public String getAddedById() {
        return this.addedBy;
    }

    @Override
    public Date getAddedAt() {
        return addedAt;
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        unsupported(Comment.class);
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        unsupported(Comment.class);
    } 
}
