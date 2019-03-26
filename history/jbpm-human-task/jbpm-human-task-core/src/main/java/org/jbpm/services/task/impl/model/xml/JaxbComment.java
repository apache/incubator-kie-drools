/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.impl.model.xml;

import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.unsupported;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.jbpm.services.task.impl.model.xml.InternalJaxbWrapper.GetterUser;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.User;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement(name="comment")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class JaxbComment implements Comment {

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
        // JAXB Constructor
    }
    
    public JaxbComment(Comment comment) { 
        initialize(comment);
    }
   
    public JaxbComment(String userId, Date commentDate, String commentText) { 
       this.addedBy = userId; 
       this.addedAt = commentDate; 
       this.text = commentText; 
    }
    
    protected void initialize(Comment comment) { 
        if( comment != null ) { 
            this.id = comment.getId();
            this.text = comment.getText();
            User addedByUser = comment.getAddedBy();
            if( addedByUser != null ) { 
                this.addedBy = addedByUser.getId();
            }
            this.addedAt = comment.getAddedAt();
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

    public void setAddedAt(Date commentDate) { 
        this.addedAt = commentDate;
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
