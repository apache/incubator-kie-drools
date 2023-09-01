package org.kie.internal.jaxb;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class StringKeyObjectValueEntry {

    @XmlAttribute
    private String key;

    @XmlAttribute(name="class-name")
    private String className;

    @XmlValue
    @XmlSchemaType(name = "base64Binary")
    private byte[] value;

    public StringKeyObjectValueEntry() {
        // default
    }

    public StringKeyObjectValueEntry(Entry<String, Object> entry) {
       this.key = entry.getKey();
       Object object = entry.getValue();
       if( object != null ) {
           this.value = StringKeyObjectValueMapXmlAdapter.serializeObject(object, key);
           this.className = object.getClass().getName();
       }
    }

    public StringKeyObjectValueEntry(String key, String className, byte [] bytes) {
        this.key = key;
        this.className = className;
        this.value = bytes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public byte[] getBytes() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

}
