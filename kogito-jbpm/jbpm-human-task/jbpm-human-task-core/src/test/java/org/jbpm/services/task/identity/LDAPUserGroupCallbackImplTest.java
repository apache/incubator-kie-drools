package org.jbpm.services.task.identity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.naming.Context;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.kie.api.task.UserGroupCallback;

public class LDAPUserGroupCallbackImplTest extends LDAPBaseTest {

    @Test
    public void testUserExists() {
        Properties properties = new Properties();
        properties.setProperty(Context.PROVIDER_URL, "ldap://localhost:10389");
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
        properties.setProperty(Context.PROVIDER_URL, "ldap://localhost:10389");
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
        properties.setProperty(Context.PROVIDER_URL, "ldap://localhost:10389");
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
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(true);

        boolean userExists = ldapUserGroupCallback.existsUser("john");
        assertTrue(userExists);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationErrorUserExists() {
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(new Properties());

        boolean userExists = ldapUserGroupCallback.existsUser("john");
        assertTrue(userExists);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationErrorGroupExists() {
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(new Properties());

        boolean userExists = ldapUserGroupCallback.existsGroup("john");
        assertTrue(userExists);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationErrorUserGroup() {
        Properties properties = new Properties();

        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(properties);

        List<String> userGroups = ldapUserGroupCallback.getGroupsForUser("john", null, null);
        assertEquals(1, userGroups.size());
    }

    @Test
    public void testCreateCallbackFromProperties() {
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(true);

        assertNotNull(ldapUserGroupCallback);
    }

    @Test
    public void testCreateCallbackFromCustomProperties() {
        System.setProperty("jbpm.usergroup.callback.properties", "/jbpm.usergroup.callback.properties");
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(true);

        assertNotNull(ldapUserGroupCallback);
        System.clearProperty("jbpm.usergroup.callback.properties");
    }

}
