package org.jbpm.kie.test.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class House implements Building {

    public Integer doors;

    public void setDoors(Integer doors) {
        this.doors = doors;
    }

    @Override
    public Integer getDoors() {
        return doors;
    }

}
