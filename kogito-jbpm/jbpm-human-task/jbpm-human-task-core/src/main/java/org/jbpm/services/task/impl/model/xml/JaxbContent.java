package org.jbpm.services.task.impl.model.xml;

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

import org.jbpm.services.task.impl.model.xml.adapter.StringObjectMapXmlAdapter;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.model.Content;

@XmlRootElement(name="content")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbContent implements Content {

    @XmlElement
    private Long id;

    @XmlElement(name="class-name")
    private String className = null;
    
    @XmlElement
    @XmlSchemaType(name="base64Binary")
    private byte[] content = null;
    
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
        if( content == null ) { 
            return; 
        }
        this.id = content.getId();
        Object realContentObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        this.className = realContentObject.getClass().getName();
        boolean serialize = true;
        if( realContentObject instanceof Map<?, ?> ) { 
            Map<?,?> contentMap = (Map<?,?>) realContentObject;
            if( ! contentMap.isEmpty() ) { 
                if( contentMap.keySet().iterator().next() instanceof String ) { 
                    serialize = false;
                    this.contentMap = (Map<String, Object>) contentMap;
                }
            }
        }
        if( serialize ) { 
            this.content = StringObjectMapXmlAdapter.serializeObject(realContentObject, "Content(" + this.id + ").content" );
        }
    }
    
    @Override
    public byte[] getContent() {
        byte [] realContent = null;
        if( this.content != null ) { 
            Object contentObject = StringObjectMapXmlAdapter.deserializeObject(this.content, this.className, 
                    "Content(" + this.id + ").content" );
            realContent = ContentMarshallerHelper.marshallContent(contentObject, null);
        } else if( this.contentMap != null ) { 
            realContent = ContentMarshallerHelper.marshallContent(this.contentMap, null);
        }
        return realContent;
    }
    
    public byte[] getSerializedContent() { 
        return this.content;
    }

    public Map<String, Object> getContentMap() { 
        return this.contentMap;
    }

    @Override
    public long getId() {
        return this.id;
    } 
    
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        String methodName = (new Throwable()).getStackTrace()[0].getMethodName();
        throw new UnsupportedOperationException(methodName + " is not supported on the JAXB " + Content.class.getSimpleName()
                + " implementation.");
    }

    public void writeExternal(ObjectOutput arg0) throws IOException {
        String methodName = (new Throwable()).getStackTrace()[0].getMethodName();
        throw new UnsupportedOperationException(methodName + " is not supported on the JAXB " + Content.class.getSimpleName()
                + " implementation.");
    }
}
