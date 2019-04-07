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

import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Content;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;


@XmlRootElement(name="add-content-command")
@XmlAccessorType(XmlAccessType.NONE)
public class AddContentCommand extends TaskCommand<Long> {

	private static final long serialVersionUID = -1295175858745522756L;

	@XmlElement
    private JaxbContent jaxbContent;

	@XmlTransient
	private Content content;
	
    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="parameter")
    private Map<String, Object> params;
    
    public AddContentCommand() {
    }

    public AddContentCommand(Long taskId, Content content) {
    	this.taskId = taskId;
    	setContent(content);
    }

    public AddContentCommand(Long taskId, Map<String, Object> params) {
    	this.taskId = taskId;
    	this.params = params;
    }

    public Long execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        if (params != null) {
        	return context.getTaskContentService().addOutputContent(taskId, params);
        } else {        
	        Content comentImpl = content;
	        if (comentImpl == null) {
	        	comentImpl = jaxbContent;
	    	}
	        
	        return context.getTaskContentService().setDocumentContent(taskId, comentImpl);
        }
    }

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}
    
    public JaxbContent getJaxbContent() {
		return jaxbContent;
	}

	public void setJaxbContent(JaxbContent jaxbContent) {
		this.jaxbContent = jaxbContent;
	}
}
