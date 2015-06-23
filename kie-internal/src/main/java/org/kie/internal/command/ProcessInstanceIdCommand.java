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

package org.kie.internal.command;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.command.Command;

public abstract class ProcessInstanceIdCommand<T> implements Command<T> {

    private static final long serialVersionUID = -7958811586021197629L;
    
    @XmlElement(name="process-instance-id")
    @XmlSchemaType(name="long")
    protected Long processInstanceId;
    
    public void setProcessInstanceId(Long procInstId) { 
       this.processInstanceId = procInstId;
    }
    
    public Long getProcessInstanceId() { 
       return processInstanceId; 
    }
    
}
