package org.jbpm.services.task.query.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kie.internal.task.api.model.SubTasksStrategy;

public class SubTasksStrategyXmlAdapter extends XmlAdapter<String, SubTasksStrategy> {

    @Override
    public String marshal(SubTasksStrategy v) throws Exception {
        return v.name();
    }

    @Override
    public SubTasksStrategy unmarshal(String v) throws Exception {
        return SubTasksStrategy.valueOf(SubTasksStrategy.class, v);
    }

}
