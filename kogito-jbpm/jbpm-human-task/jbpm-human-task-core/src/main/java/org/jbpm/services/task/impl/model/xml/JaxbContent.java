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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.jbpm.services.task.impl.model.xml.adapter.StringObjectMapXmlAdapter;
import org.kie.api.task.model.Content;

@XmlRootElement(name="content")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbContent implements Content {

    @XmlElement
    private Long id;

    @XmlElement
    @XmlSchemaType(name="base64Binary")
    private byte[] serializedContent = null;
    
    @XmlElement(name="content-map")
    @XmlJavaTypeAdapter(StringObjectMapXmlAdapter.class)
    private Map<String, Object> contentMap = null;
    
    public JaxbContent() { 
        // default
    }
    
    public JaxbContent(Content content) { 
        initialize(content);
    }
    
    public void initialize(Content content) { 
        if( content == null || content.getId() == -1) { 
            return; 
        }
        this.id = content.getId();
        this.serializedContent = content.getContent();
    }
    
    @Override
    @JsonIgnore
    public byte[] getContent() {
        return serializedContent;
    }
   
    public byte[] getSerializedContent() { 
        return this.serializedContent;
    }

    public void setSerializedContent(byte [] content) { 
        this.serializedContent = content;
    }

    public Map<String, Object> getContentMap() { 
        return this.contentMap;
    }

    public void setContentMap(Map<String, Object> map) { 
        this.contentMap = map;
    }

    @Override
    public long getId() {
        return this.id;
    } 
    
    public void setId(Long id) {
        this.id = id; 
    } 
    
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        unsupported(Content.class);
    }

    public void writeExternal(ObjectOutput arg0) throws IOException {
        unsupported(Content.class);
    }
}
