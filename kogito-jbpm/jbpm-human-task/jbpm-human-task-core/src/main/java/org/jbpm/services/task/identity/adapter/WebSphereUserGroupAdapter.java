package org.jbpm.services.task.identity.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebSphereUserGroupAdapter implements UserGroupAdapter {


    private static final Logger logger = LoggerFactory.getLogger(WebSphereUserGroupAdapter.class);
    private Object registry;

    public WebSphereUserGroupAdapter() {
        try {
            this.registry = InitialContext.doLookup("UserRegistry");
        } catch (NamingException e) {
            logger.warn("Unable to look up UserRegistry in JNDI under key 'UserRegistry', disabling websphere adapter");
        }
    }

    @SuppressWarnings("rawtypes")
	@Override
    public List<String> getGroupsForUser(String userId) {
        List<String> roles = new ArrayList<String>();
        if (registry == null) {
            return roles;
        }
        try {
            Method method = registry.getClass().getMethod("getGroupsForUser", new Class[]{String.class});
            List rolesIn = (List) method.invoke(registry, new Object[]{userId});
            if (rolesIn != null) {
                for (Object o : rolesIn) {
                    roles.add(  o.toString() );
                }
            }
        } catch (Exception e) {
            logger.error("Unable to get roles from registry due to {}", e.getMessage(), e);
        }

        return roles;
    }
}