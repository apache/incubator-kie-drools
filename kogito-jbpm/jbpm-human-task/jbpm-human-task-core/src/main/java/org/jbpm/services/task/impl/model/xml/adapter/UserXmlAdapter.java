package org.jbpm.services.task.impl.model.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;

public class UserXmlAdapter extends XmlAdapter<String, User> {

    @Override
    public User unmarshal(String v) throws Exception {
    	User user = TaskModelProvider.getFactory().newUser();
    	((InternalOrganizationalEntity) user).setId(v);
        return user;
    }

    @Override
    public String marshal(User v) throws Exception {
        return v.getId();
    }

}
