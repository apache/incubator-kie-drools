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

import org.kie.api.runtime.Context;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.task.api.UserInfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="get-user-info-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetUserInfoCommand extends TaskCommand<UserInfo> {
	
	private static final long serialVersionUID = -5174631969130504959L;

	public GetUserInfoCommand() {
	}

	public UserInfo execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
    	return (UserInfo) context.get(EnvironmentName.TASK_USER_INFO);
       
    }
}
