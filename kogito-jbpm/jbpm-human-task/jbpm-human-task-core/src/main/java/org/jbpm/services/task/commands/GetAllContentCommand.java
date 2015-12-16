/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.Content;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-all-content-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetAllContentCommand extends TaskCommand<List<Content>> {

	private static final long serialVersionUID = 5911387213149078240L;
	
	public GetAllContentCommand() {
	}
	
	public GetAllContentCommand(Long taskId) {
		this.taskId = taskId;
    }

	public List<Content> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskContentService().getAllContentByTaskId(taskId);
    }

}
