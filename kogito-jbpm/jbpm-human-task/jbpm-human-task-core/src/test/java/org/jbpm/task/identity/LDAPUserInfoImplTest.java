package org.jbpm.task.identity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;
import org.junit.Ignore;
import org.junit.Test;

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
        
        String email = ldapUserInfo.getEmailForEntity(new User("john"));
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
        
        String email = ldapUserInfo.getEmailForEntity(new Group("manager"));
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
        
        String email = ldapUserInfo.getEmailForEntity(new User("uid=john,ou=People,dc=jbpm,dc=org"));
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
        
        String email = ldapUserInfo.getEmailForEntity(new Group("cn=manager,ou=Roles,dc=jbpm,dc=org"));
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
        
        Iterator<OrganizationalEntity> members = ldapUserInfo.getMembersForGroup(new Group("manager"));
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
        
        String email = ldapUserInfo.getDisplayName(new User("john"));
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
        
        String email = ldapUserInfo.getDisplayName(new Group("manager"));
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
        
        String email = ldapUserInfo.getLanguageForEntity(new User("john"));
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
        
        String email = ldapUserInfo.getLanguageForEntity(new Group("manager"));
        assertNotNull(email);
        assertEquals("en-UK", email);
    }
}
