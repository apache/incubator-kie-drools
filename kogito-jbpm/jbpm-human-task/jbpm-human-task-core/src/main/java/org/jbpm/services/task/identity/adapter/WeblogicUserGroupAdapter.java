package org.jbpm.services.task.identity.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WeblogicUserGroupAdapter implements UserGroupAdapter {


    private static final Logger logger = LoggerFactory.getLogger(WeblogicUserGroupAdapter.class);
    private Class<?> webLogicSecurity;

    public WeblogicUserGroupAdapter() {
    	try {
            this.webLogicSecurity = Class.forName("weblogic.security.Security");
        } catch ( Exception e ) {
            logger.warn( "Unable to find weblogic.security.Security, disabling weblogic adapter" );
        }
    }


	@Override
    public List<String> getGroupsForUser(String userId) {
        List<String> roles = new ArrayList<String>();
        if (webLogicSecurity == null || userId == null || userId.isEmpty()) {
            return roles;
        }
        try {
        	Method method = webLogicSecurity.getMethod("getCurrentSubject", new Class[]{});
            Subject wlsSubject = (Subject) method.invoke( null, new Object[]{ } );
            if ( wlsSubject != null ) {
                for ( java.security.Principal p : wlsSubject.getPrincipals() ) {
                    if (p.getClass().getName().indexOf("WLSGroup") != -1) {
                    	roles.add(  p.getName() );
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Unable to get roles for user {} from subject due to {}", userId, e.getMessage(), e);
        }

        return roles;
    }
}