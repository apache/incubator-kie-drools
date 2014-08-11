/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jbpm.services.task.impl.model.xml.JaxbAttachment;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Content;
import org.kie.internal.command.Context;


@XmlRootElement(name="add-attachment-command")
@XmlAccessorType(XmlAccessType.NONE)
public class AddAttachmentCommand extends UserGroupCallbackTaskCommand<Long> {

	private static final long serialVersionUID = -1295175842745522756L;
	
	@XmlElement
    private JaxbAttachment jaxbAttachment;

	@XmlTransient
	private Attachment attachment;
	
	@XmlElement
    private JaxbContent jaxbContent;

	@XmlTransient
	private Content content;
    
    public AddAttachmentCommand() {
    }

    public AddAttachmentCommand(Long taskId, Attachment attachment, Content content) {
    	this.taskId = taskId;
    	setAttachment(attachment);
        setContent(content);
    }


    public Long execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        Attachment attachmentImpl = attachment;
        if (attachmentImpl == null) {
        	attachmentImpl = jaxbAttachment;
    	}
        
        Content contentImpl = content;
        if (contentImpl == null) {
        	contentImpl = jaxbContent;
        }
        
        doCallbackOperationForAttachment(attachmentImpl, context);
        
        return context.getTaskAttachmentService().addAttachment(taskId, attachmentImpl, contentImpl);
    	 
    }

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
		if (attachment instanceof JaxbAttachment) {
        	this.jaxbAttachment = (JaxbAttachment) attachment;
        } else {
        	this.jaxbAttachment = new JaxbAttachment(attachment);
        }
	}
    
    public JaxbAttachment getJaxbAttachment() {
		return jaxbAttachment;
	}

	public void setJaxbAttachment(JaxbAttachment jaxbAttachment) {
		this.jaxbAttachment = jaxbAttachment;
	}

	public JaxbContent getJaxbContent() {
		return jaxbContent;
	}

	public void setJaxbContent(JaxbContent jaxbContent) {
		this.jaxbContent = jaxbContent;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
		if (content instanceof JaxbContent) {
        	this.jaxbContent = (JaxbContent) content;
        } else {
        	this.jaxbContent = new JaxbContent(content);
        }
	}

	public Attachment getAttachment() {
		return attachment;
	}
}
