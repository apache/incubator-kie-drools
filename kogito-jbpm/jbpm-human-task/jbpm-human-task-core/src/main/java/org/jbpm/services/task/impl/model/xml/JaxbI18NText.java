package org.jbpm.services.task.impl.model.xml;

import static org.jbpm.services.task.impl.model.xml.AbstractJaxbTaskObject.unsupported;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.User;

@XmlRootElement(name="i18n-text")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbI18NText implements I18NText {

    @XmlElement
    @XmlSchemaType(name="long")
    private Long id;
 
    @XmlElement
    @XmlSchemaType(name="string")
    private String text;
 
    @XmlElement
    @XmlSchemaType(name="string")
    private String language;
 
    public JaxbI18NText() { 
       // JAXB Default 
    }
    
    public JaxbI18NText(I18NText text) { 
       this.id = text.getId();
       this.language = text.getLanguage();
       this.text = text.getText();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String lang) {
        this.language = lang;
    }

    public static List<JaxbI18NText> convertListFromInterfaceToJaxbImpl(List<I18NText> textList) { 
        List<JaxbI18NText> jaxbTextList = new ArrayList<JaxbI18NText>(textList.size());
        if( textList != null ) { 
            for( I18NText text : textList ) { 
                if( text instanceof JaxbI18NText ) { 
                    jaxbTextList.add((JaxbI18NText) text);
                } else { 
                    jaxbTextList.add(new JaxbI18NText(text));
                }
            }
        }
        return jaxbTextList;
    }
    
    public static List<I18NText> convertListFromJaxbImplToInterface(List<JaxbI18NText> jaxbList) { 
        List<I18NText> list = new ArrayList<I18NText>(jaxbList.size());
        if( jaxbList != null ) { 
            for( JaxbI18NText jaxb : jaxbList ) { 
                list.add(jaxb.createImplInstance());
            }
        }
        return list;
    }
    
    private I18NText createImplInstance() { 
       return new GetterI18NText(this.id, this.language, this.text);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        unsupported(I18NText.class);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        unsupported(I18NText.class);
    }
    
    
    /**
     * I was forced to do this because we put the interfaces to our *ENTITIES* in the *PUBLIC* API. 
     */
    static class GetterI18NText implements I18NText {
    
        private final Long id;
        private final String lang;
        private final String text;
        
        public GetterI18NText(Long id, String lang, String text) { 
            this.id = id;
            this.lang = lang;
            this.text = text;
        }
        
        @Override
        public Long getId() { return this.id; }
    
        @Override
        public String getLanguage() { return this.lang; }

        @Override
        public String getText() { return this.text; }

        public void writeExternal(ObjectOutput out) throws IOException { unsupported(User.class); }
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException { unsupported(User.class); } 
    }
}
