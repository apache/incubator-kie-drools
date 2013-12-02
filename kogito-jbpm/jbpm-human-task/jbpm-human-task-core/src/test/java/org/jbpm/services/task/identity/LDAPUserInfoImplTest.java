package org.jbpm.services.task.identity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;

//Ignore it as it relies on external LDAP server
@Ignore
public class LDAPUserInfoImplTest {

    @Test
    public void testGetEmailForUserEntity() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("john");
        String email = ldapUserInfo.getEmailForEntity(user);
        assertNotNull(email);
        assertEquals("john@jbpm.org", email);
    }
    
    @Test
    public void testGetEmailForGroupEntity() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserInfoImpl.EMAIL_ATTR_ID, "ou");
        
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        Group group = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) group).setId("manager");
        String email = ldapUserInfo.getEmailForEntity(group);
        assertNotNull(email);
        assertEquals("managers@jbpm.org", email);
    }
    
    @Test
    public void testGetEmailForUserEntityAsDN() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserInfoImpl.IS_ENTITY_ID_DN, "true");
        
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("uid=john,ou=People,dc=jbpm,dc=org");
        String email = ldapUserInfo.getEmailForEntity(user);
        assertNotNull(email);
        assertEquals("john@jbpm.org", email);
    }
    
    @Test
    public void testGetEmailForGroupEntityAsDN() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserInfoImpl.IS_ENTITY_ID_DN, "true");
        properties.setProperty(LDAPUserInfoImpl.EMAIL_ATTR_ID, "ou");
        
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        Group group = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) group).setId("cn=manager,ou=Roles,dc=jbpm,dc=org");
        String email = ldapUserInfo.getEmailForEntity(group);
        assertNotNull(email);
        assertEquals("managers@jbpm.org", email);
    }
    
    @Test
    public void testGetGroupMembers() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserInfoImpl.MEMBER_ATTR_ID, "member");
        
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        
        Group group = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) group).setId("manager");
        Iterator<OrganizationalEntity> members = ldapUserInfo.getMembersForGroup(group);
        assertNotNull(members);
        List<OrganizationalEntity> orgMembers = new ArrayList<OrganizationalEntity>();
        
        while (members.hasNext()) {
            OrganizationalEntity organizationalEntity = (OrganizationalEntity) members
                    .next();
            orgMembers.add(organizationalEntity);
        }
        assertEquals(4, orgMembers.size());
        
    }
    
    @Test
    public void testGetNameForUserEntity() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("john");
        String email = ldapUserInfo.getDisplayName(user);
        assertNotNull(email);
        assertEquals("John Doe", email);
    }
    
    @Test
    public void testGetNameForGroupEntity() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserInfoImpl.NAME_ATTR_ID, "description");
        
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        Group group = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) group).setId("manager");
        String email = ldapUserInfo.getDisplayName(group);
        assertNotNull(email);
        assertEquals("Manager group", email);
    }
    
    @Test
    public void testGetLangForUserEntity() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("john");
        String email = ldapUserInfo.getLanguageForEntity(user);
        assertNotNull(email);
        assertEquals("en-UK", email);
    }
    
    @Test
    public void testGetLangForGroupEntity() {
        Properties properties = new Properties();
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        Group group = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) group).setId("manager");
        String email = ldapUserInfo.getLanguageForEntity(group);
        assertNotNull(email);
        assertEquals("en-UK", email);
    }
}
