package org.jbpm.task.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
//Ignore it as it relies on external LDAP server
@Ignore
public class LDAPUserGroupCallbackImplTest {

    @Test
    public void testUserExists() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_FILTER, "(member={0})");
        
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(properties);
        
        boolean userExists = ldapUserGroupCallback.existsUser("john");
        assertTrue(userExists);
    }
    
    @Test
    public void testGroupExists() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_FILTER, "(member={0})");
        
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(properties);
        
        boolean userExists = ldapUserGroupCallback.existsGroup("manager");
        assertTrue(userExists);
    }
    
    @Test
    public void testUserGroup() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_FILTER, "(member={0})");
        
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(properties);
        
        List<String> userGroups = ldapUserGroupCallback.getGroupsForUser("john", null, null);
        assertEquals(3, userGroups.size());
    }
    
    @Test
    public void testDefaultPropsUserExists() {
        
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl();
        
        boolean userExists = ldapUserGroupCallback.existsUser("john");
        assertTrue(userExists);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testValidationErrorUserExists() {
        
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(new Properties());
        
        boolean userExists = ldapUserGroupCallback.existsUser("john");
        assertTrue(userExists);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testValidationErrorGroupExists() {
        
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(new Properties());
        
        boolean userExists = ldapUserGroupCallback.existsGroup("john");
        assertTrue(userExists);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testValidationErrorUserGroup() {
        Properties properties = new Properties();
        
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(properties);
        
        List<String> userGroups = ldapUserGroupCallback.getGroupsForUser("john", null, null);
        assertEquals(1, userGroups.size());
    }
}
