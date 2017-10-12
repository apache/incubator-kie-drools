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
import org.kie.api.runtime.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "add-content-from-user-command")
@XmlAccessorType(XmlAccessType.NONE)
public class AddContentFromUserCommand extends UserGroupCallbackTaskCommand<Long> {

    private static final long serialVersionUID = -1295175858745522756L;

    @XmlElement(name="document-content-bytes")
    @XmlSchemaType(name = "base64Binary")
    private byte[] documentContentBytes = null;


    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="output-content-map")
    private Map<String, Object> outputContentMap = null;

    public AddContentFromUserCommand() {
        // default JAXB constructor
    }
    
    public AddContentFromUserCommand(long taskId, String userId) {
       setTaskId(taskId); 
       setUserId(userId);
    }

    public byte[] getDocumentContentBytes() {
        return documentContentBytes;
    }

    public void setDocumentContentBytes( byte[] documentContentBytes ) {
        this.documentContentBytes = documentContentBytes;
    }

    public Map<String, Object> getOutputContentMap() {
        if( this.outputContentMap == null ) { 
            this.outputContentMap = new HashMap<String, Object>();
        }
        return outputContentMap;
    }

    public void setOutputContentMap( Map<String, Object> outputContentMap ) {
        this.outputContentMap = outputContentMap;
    }

    public Long execute( Context cntxt ) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context, true);
        groupIds = doUserGroupCallbackOperation(userId, null, context);
        context.set("local:groups", groupIds);

        if( outputContentMap != null ) { 
            return context.getTaskInstanceService().addOutputContentFromUser(taskId, userId, outputContentMap);
        } else if( documentContentBytes != null ) {
            // TODO!
            // return context.getTaskInstanceService().setDocumentContentFromUser(taskId, userId, documentContentBytes);
        }
        return -1l;
    }

}
