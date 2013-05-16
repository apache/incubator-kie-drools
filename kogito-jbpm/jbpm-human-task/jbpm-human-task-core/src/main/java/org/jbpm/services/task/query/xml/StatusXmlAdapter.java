package org.jbpm.services.task.query.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kie.api.task.model.Status;

public class StatusXmlAdapter extends XmlAdapter<String, Status> {

    @Override
    public String marshal(Status v) throws Exception {
        return v.name();
    }

    @Override
    public Status unmarshal(String v) throws Exception {
        return Status.valueOf(Status.class, v);
    }

}
