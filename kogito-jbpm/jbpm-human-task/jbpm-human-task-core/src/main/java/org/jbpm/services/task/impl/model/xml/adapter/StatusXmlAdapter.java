package org.jbpm.services.task.impl.model.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kie.api.task.model.Status;

public class StatusXmlAdapter extends XmlAdapter<String, Status> {

    @Override
    public String marshal(Status v) throws Exception {
        if( v != null ) { 
            return v.name();
        } 
        return null;
    }

    @Override
    public Status unmarshal(String v) throws Exception {
        if( v != null ) { 
            return Status.valueOf(Status.class, v);
        } 
        return null;
    }

}
