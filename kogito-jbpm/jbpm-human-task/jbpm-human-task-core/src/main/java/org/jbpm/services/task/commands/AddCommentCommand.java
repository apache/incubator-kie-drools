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
package org.jbpm.services.task.commands;

import org.jbpm.services.task.impl.model.xml.JaxbComment;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalComment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;


@XmlRootElement(name="add-comment-command")
@XmlAccessorType(XmlAccessType.NONE)
public class AddCommentCommand extends UserGroupCallbackTaskCommand<Long> {

	private static final long serialVersionUID = -1295175858745522756L;
	
	@XmlElement
    private JaxbComment jaxbComment;

	@XmlTransient
	private Comment comment;
    
    public AddCommentCommand() {
    }

    public AddCommentCommand(Long taskId, Comment comment) {
    	this.taskId = taskId;
    	setComment(comment);
    }

    public AddCommentCommand(Long taskId, String userId, String text) {
        this.taskId = taskId;
        JaxbComment jaxbComment = new JaxbComment(userId, new Date(), text);
        setComment(jaxbComment);
    }
    
    public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
		if (comment instanceof JaxbComment) {
        	this.jaxbComment = (JaxbComment) comment;
        } else {
        	this.jaxbComment = new JaxbComment(comment);
        }
	}
    
    public JaxbComment getJaxbComment() {
		return jaxbComment;
	}

	public void setJaxbComment(JaxbComment jaxbComment) {
		this.jaxbComment = jaxbComment;
	}

    public Long execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        Comment cmdComent = comment;
        if (cmdComent == null) {
        	cmdComent = jaxbComment;
        }
       
        InternalComment commentImpl = (InternalComment) TaskModelProvider.getFactory().newComment();
        commentImpl.setAddedAt(cmdComent.getAddedAt());
        User cmdAddedBy = cmdComent.getAddedBy();
        User addedBy = TaskModelProvider.getFactory().newUser(cmdAddedBy.getId());
        commentImpl.setAddedBy(addedBy);
        commentImpl.setText(cmdComent.getText());
        
        doCallbackOperationForComment(commentImpl, context);
        
        return context.getTaskCommentService().addComment(taskId, commentImpl);
    	 
    }
}
