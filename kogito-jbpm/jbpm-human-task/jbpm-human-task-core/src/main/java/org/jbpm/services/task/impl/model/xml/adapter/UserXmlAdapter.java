package org.jbpm.services.task.impl.model.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jbpm.services.task.impl.model.UserImpl;
import org.kie.api.task.model.User;

public class UserXmlAdapter extends XmlAdapter<String, User> {

    @Override
    public User unmarshal(String v) throws Exception {
            return new UserImpl(v);
    }

    @Override
    public String marshal(User v) throws Exception {
        return v.getId();
    }

}
