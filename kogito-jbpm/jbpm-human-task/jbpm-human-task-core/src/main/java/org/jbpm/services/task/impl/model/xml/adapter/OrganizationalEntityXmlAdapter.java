package org.jbpm.services.task.impl.model.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;

public class OrganizationalEntityXmlAdapter extends XmlAdapter<String, OrganizationalEntity> {

    private static final String USER_PREFIX = "u:";
    private static final String GROUP_PREFIX = "g:";
    
    @Override
    public OrganizationalEntity unmarshal(String v) throws Exception {
        if( v.startsWith(USER_PREFIX) ) { 
            return new UserImpl(v.substring(2, v.length()));
        } else if( v.startsWith(GROUP_PREFIX) ) { 
           return new GroupImpl(v.substring(2, v.length()));
        } else { 
            throw new IllegalStateException("Unknown prefix '" + v.substring(0,2) + "' (" + v + ")" );
        }
    }

    @Override
    public String marshal(OrganizationalEntity v) throws Exception {
        StringBuffer out = new StringBuffer();
        if( v instanceof User ) { 
            out.append(USER_PREFIX);
        } else if( v instanceof Group ) { 
            out.append(GROUP_PREFIX);
        } else { 
            throw new IllegalStateException("Unknown " + OrganizationalEntity.class.getSimpleName() + " type : " + v.getClass().getName() );
        }
        return out.append(v.getId()).toString();
    }

}
