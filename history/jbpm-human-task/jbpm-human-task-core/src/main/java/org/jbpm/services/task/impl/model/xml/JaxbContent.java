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
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.model.Content;
import org.kie.internal.jaxb.StringKeyObjectValueMapXmlAdapter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement(name="content")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class JaxbContent implements Content {

    @XmlElement
    private Long id;

    @XmlElement
    @XmlSchemaType(name="base64Binary")
    private byte[] content = null;
    
    @XmlJavaTypeAdapter(StringKeyObjectValueMapXmlAdapter.class)
    private Map<String, Object> contentMap = null;
    
    public JaxbContent() { 
        // default
    }
    
    public JaxbContent(Content content) { 
        initialize(content);
    }
    
    @SuppressWarnings("unchecked")
    public void initialize(Content content) {
        if( content == null || content.getId() == -1) { 
            return; 
        }
        this.id = content.getId();
        this.content = content.getContent();
        if( content instanceof JaxbContent ) { 
            this.contentMap = ((JaxbContent) content).getContentMap();
        } else { 
            try {
                Object unmarshalledContent = ContentMarshallerHelper.unmarshall(content.getContent(), null);
                if( unmarshalledContent != null && unmarshalledContent instanceof Map ) { 
                    contentMap = (Map<String, Object>) unmarshalledContent;
                }
            } catch (Exception e) {
                // don't fail in case of unmarshalling problem as it might be content not handled via jaxb 
                // Ļe.g. custom classes, non map based etc
            }
        }
    }
    
    @Override
    public byte[] getContent() {
        return content;
    }
   
    public byte[] getSerializedContent() { 
        return this.content;
    }

    public void setSerializedContent(byte [] content) { 
        this.content = content;
    }

    public Map<String, Object> getContentMap() { 
        return this.contentMap;
    }

    public void setContentMap(Map<String, Object> map) { 
        this.contentMap = map;
    }

    @Override
    public Long getId() {
        return this.id;
    } 
    
    public void setId(Long id) {
        this.id = id; 
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        unsupported(Content.class);
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        unsupported(Content.class);
    } 
    
}
