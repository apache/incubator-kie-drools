package org.drools.commands.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is actually a wrapper for the following collections:
 * - list
 * - set
 * - map
 */

@XmlRootElement(name="list")
@XmlAccessorType(XmlAccessType.NONE)
public class JaxbListWrapper  {

    public static enum JaxbWrapperType {
        LIST, SET, MAP, ARRAY;
    }

    // set to null for backwards compatibility
    @XmlElement
    private JaxbWrapperType type = null;

    @XmlAttribute
    private String componentType = null;

    @XmlElement(name="element")
    private Object[] elements;

    public JaxbListWrapper() {
        // JAXB constructor
    }

    public JaxbListWrapper(Object[] elements) {
        this.elements = elements;
    }

    public JaxbListWrapper(Object[] elements, JaxbWrapperType type) {
        this.elements = elements;
        this.type = type;
    }

    public Object[] getElements() {
        return elements;
    }

    public void setElements(Object[] elements) {
        this.elements = elements;
    }

    public JaxbWrapperType getType() {
        return type;
    }

    public void setType( JaxbWrapperType type ) {
        this.type = type;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType( String componentType ) {
        this.componentType = componentType;
    }
}
