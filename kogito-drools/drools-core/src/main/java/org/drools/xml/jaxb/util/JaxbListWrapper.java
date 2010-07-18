package org.drools.xml.jaxb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="list")
public class JaxbListWrapper<T>  {
    
    Object[] elements;
    
    public JaxbListWrapper() {
        
    }
    
    public JaxbListWrapper(Object[] elements) {
        this.elements = elements;
    }
    
	@XmlElement(name="element")
	public Object[] getElements() {
		return elements;
	}
	
	public void setElements(Object[] elements) {
	    this.elements = elements;
	}
}
