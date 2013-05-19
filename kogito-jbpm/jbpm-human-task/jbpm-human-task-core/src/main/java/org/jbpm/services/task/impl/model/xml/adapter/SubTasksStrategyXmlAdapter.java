package org.jbpm.services.task.impl.model.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kie.internal.task.api.model.SubTasksStrategy;

public class SubTasksStrategyXmlAdapter extends XmlAdapter<String, SubTasksStrategy> {

    @Override
    public String marshal(SubTasksStrategy v) throws Exception {
        if( v != null ) { 
        return v.name();
        }
        return null;
    }

    @Override
    public SubTasksStrategy unmarshal(String v) throws Exception {
        if( v != null ) { 
            return SubTasksStrategy.valueOf(SubTasksStrategy.class, v);
        } 
        return null;
    }

}
