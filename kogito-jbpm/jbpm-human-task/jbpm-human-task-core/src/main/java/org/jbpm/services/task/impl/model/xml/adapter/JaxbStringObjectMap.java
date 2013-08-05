package org.jbpm.services.task.impl.model.xml.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbStringObjectMap {

    @XmlElement(name="entry")
    public List<JaxbStringObjectMapEntry> entries = new ArrayList<JaxbStringObjectMapEntry>();
    
    public void addEntry(JaxbStringObjectMapEntry newEntry) { 
       this.entries.add(newEntry);
    }
    
}
