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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.FaultData;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement(name="fault-data")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class JaxbFaultData implements FaultData {

    @XmlElement
    private AccessType accessType; 

    @XmlElement
    @XmlSchemaType(name="string")
    private String type;
   
    @XmlElement
    @XmlSchemaType(name="base64Binary")
    private byte[] content = null;
    
    private Object contentObject;
    
    @XmlElement(name="fault-name")
    @XmlSchemaType(name="string")
    private String faultName;
   
    public JaxbFaultData() { 
        // JAXB constructor
    }
    
    public JaxbFaultData(FaultData faultData) { 
        this.accessType = faultData.getAccessType();
        this.content = faultData.getContent();
        this.faultName = faultData.getFaultName();
        this.type = faultData.getType();
    }
    
    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType( AccessType accessType ) {
        this.accessType = accessType;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public void setContent( byte[] content ) {
        this.content = content;
    }

    @Override
    public String getFaultName() {
        return faultName;
    }

    @Override
    public void setFaultName( String faultName ) {
        this.faultName = faultName;
    }
    
    @Override
    public Object getContentObject() {
    	return contentObject;
    }
    
    @Override
    public void setContentObject(Object object) {
    	this.contentObject = object;
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        unsupported(FaultData.class);
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        unsupported(FaultData.class);
    }

}
