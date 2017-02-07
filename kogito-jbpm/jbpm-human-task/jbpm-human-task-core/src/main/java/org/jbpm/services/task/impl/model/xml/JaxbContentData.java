package org.jbpm.services.task.impl.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.ContentData;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement(name="content-data")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class JaxbContentData extends AbstractJaxbTaskObject<ContentData> implements ContentData {

    @XmlElement
    private AccessType accessType; 

    @XmlElement
    @XmlSchemaType(name="string")
    private String type;
   
    @XmlElement
    @XmlSchemaType(name="base64Binary")
    private byte[] content = null;
    
    public JaxbContentData() {
        super(ContentData.class);
    }

    public JaxbContentData(ContentData contentData) {
        super(contentData, ContentData.class);
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

}
