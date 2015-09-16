package org.jbpm.kie.services.impl.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="parent")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parent {

    @XmlElement
    public Child child;

}
