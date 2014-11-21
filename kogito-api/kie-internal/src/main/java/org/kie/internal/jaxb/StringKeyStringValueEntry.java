package org.kie.internal.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class StringKeyStringValueEntry {

	@XmlElement(name="key")
    private String key;

    @XmlElement(name="value")
    private String value;

    public StringKeyStringValueEntry() {
        // default
    }

    public StringKeyStringValueEntry(String key, String value) { 
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
