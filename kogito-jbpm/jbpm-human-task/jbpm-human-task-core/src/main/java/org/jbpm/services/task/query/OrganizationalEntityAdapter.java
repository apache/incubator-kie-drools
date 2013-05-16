package org.jbpm.services.task.query;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;

public class OrganizationalEntityAdapter extends XmlAdapter<String, OrganizationalEntity> {

    @Override
    public OrganizationalEntity unmarshal(String v) throws Exception {
        if( v.matches("^g:.*") ) {
            String id = v.substring(1);
            return new GroupImpl(id);
        } else if( v.matches("u:.*") )  { 
            String id = v.substring(1);
            return new UserImpl(id);
        }
        throw new IllegalArgumentException("Unknown string format for " + OrganizationalEntity.class.getSimpleName() + ": '" + v + "'" );
    }

    @Override
    public String marshal(OrganizationalEntity v) throws Exception {
        if( v instanceof User ) { 
            return "u:" + v.getId();
        } else if( v instanceof Group ) { 
            return "g:" + v.getId();
        } 
        throw new IllegalArgumentException("Unknown " + OrganizationalEntity.class.getSimpleName() + " implementation instance: " + v.getClass().getSimpleName() );
    }

}
