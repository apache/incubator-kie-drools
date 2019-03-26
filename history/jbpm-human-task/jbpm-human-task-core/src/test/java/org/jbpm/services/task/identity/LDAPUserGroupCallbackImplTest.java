/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.identity;

import java.util.List;
import java.util.Properties;
import javax.naming.Context;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.jbpm.services.task.utils.LdapSearcher.SearchScope;
import org.junit.After;
import org.junit.Test;
import org.kie.api.task.UserGroupCallback;

public class LDAPUserGroupCallbackImplTest extends LDAPBaseTest {

    @After
    public void clearSystemProperties() {
        System.clearProperty("jbpm.usergroup.callback.properties");
    }

    @Test
    public void testUserExists() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.CUSTOM);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        boolean userExists = ldapUserGroupCallback.existsUser("john");
        Assertions.assertThat(userExists).isTrue();
    }

    @Test
    public void testGroupExists() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.CUSTOM);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        boolean groupExists = ldapUserGroupCallback.existsGroup("manager");
        Assertions.assertThat(groupExists).isTrue();
    }

    @Test
    public void testGroupsForUser() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.CUSTOM);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        List<String> userGroups = ldapUserGroupCallback.getGroupsForUser("john");
        Assertions.assertThat(userGroups).hasSize(1);
    }

    @Test
    public void testUserExistsDefaultProperties() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.DEFAULT);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        boolean userExists = ldapUserGroupCallback.existsUser("john");
        Assertions.assertThat(userExists).isTrue();
    }

    @Test
    public void testGroupExistsDefaultProperties() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.DEFAULT);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        boolean groupExists = ldapUserGroupCallback.existsGroup("manager");
        Assertions.assertThat(groupExists).isTrue();
    }

    @Test
    public void testGroupsForUserDefaultProperties() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.DEFAULT);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        List<String> userGroups = ldapUserGroupCallback.getGroupsForUser("john");
        Assertions.assertThat(userGroups).hasSize(1);
    }

    @Test
    public void testUserExistsSystemProperties() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.SYSTEM);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        boolean userExists = ldapUserGroupCallback.existsUser("john");
        Assertions.assertThat(userExists).isTrue();
    }

    @Test
    public void testGroupExistsSystemProperties() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.SYSTEM);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        boolean groupExists = ldapUserGroupCallback.existsGroup("manager");
        Assertions.assertThat(groupExists).isTrue();
    }

    @Test
    public void testGroupsForUserSystemProperties() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.SYSTEM);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        List<String> userGroups = ldapUserGroupCallback.getGroupsForUser("john");
        Assertions.assertThat(userGroups).hasSize(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCallbackFromNullProperties() {
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCallbackWithoutRequiredProperties() {
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(new Properties());
    }

    @Test
    public void testUsersObjectScopePeopleContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.OBJECT_SCOPE,
                "ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, false, false, false, false);
    }

    @Test
    public void testUsersObjectScopeJohnContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.OBJECT_SCOPE,
                "uid=john,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, true, false, false, false);
    }

    @Test
    public void testUsersOneLevelScopeBaseDnContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.ONELEVEL_SCOPE,
                "dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, false, false, false, false);
    }

    @Test
    public void testUsersOneLevelScopePeopleContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.ONELEVEL_SCOPE,
                "ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, true, true, false, false);
    }

    @Test
    public void testUsersOneLevelScopeJohnContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.ONELEVEL_SCOPE,
                "uid=john,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, false, false, false, false);
    }

    @Test
    public void testUsersOneLevelScopeEngContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.ONELEVEL_SCOPE,
                "ou=ENG,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, false, false, true, false);
    }

    @Test
    public void testUsersSubtreeScopeBaseDnContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.SUBTREE_SCOPE,
                "dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, true, true, true, true);
    }

    @Test
    public void testUsersSubtreeScopePeopleContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.SUBTREE_SCOPE,
                "ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, true, true, true, true);
    }

    @Test
    public void testUsersSubtreeScopeJohnContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.SUBTREE_SCOPE,
                "uid=john,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, true, false, false, false);
    }

    @Test
    public void testUsersSubtreeScopeEngContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithUserCtx(SearchScope.SUBTREE_SCOPE,
                "ou=ENG,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserGroupCallback, false, false, true, true);
    }

    @Test
    public void testGroupsObjectScopeRolesContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.OBJECT_SCOPE,
                "ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, false, false, false, false);
    }

    @Test
    public void testGroupsObjectScopeManagerContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.OBJECT_SCOPE,
                "cn=manager,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, true, false, false, false);
    }

    @Test
    public void testGroupsOneLevelScopeBaseDnContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.ONELEVEL_SCOPE,
                "dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, false, false, false, false);
    }

    @Test
    public void testGroupsOneLevelScopeRolesContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.ONELEVEL_SCOPE,
                "ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, true, true, false, false);
    }

    @Test
    public void testGroupsOneLevelScopeManagerContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.ONELEVEL_SCOPE,
                "cn=manager,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, false, false, false, false);
    }

    @Test
    public void testGroupsOneLevelScopeEngContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.ONELEVEL_SCOPE,
                "ou=ENG,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, false, false, true, false);
    }

    @Test
    public void testGroupsSubtreeScopeBaseDnContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.SUBTREE_SCOPE,
                "dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, true, true, true, true);
    }

    @Test
    public void testGroupsSubtreeScopeRolesContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.SUBTREE_SCOPE,
                "ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, true, true, true, true);
    }

    @Test
    public void testGroupsSubtreeScopeManagerContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.SUBTREE_SCOPE,
                "cn=manager,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, true, false, false, false);
    }

    @Test
    public void testGroupsSubtreeScopeEngContext() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallbackWithRoleCtx(SearchScope.SUBTREE_SCOPE,
                "ou=ENG,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserGroupCallback, false, false, true, true);
    }

    @Test
    public void testDefaultScope() {
        Properties properties = createUserGroupCallbackProperties();
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(properties);

        assertUsers(ldapUserGroupCallback, true, true, false, false);
        assertGroups(ldapUserGroupCallback, true, true, false, false);
    }

    @Test
    public void testInvalidScope() {
        Properties properties = createUserGroupCallbackProperties();
        properties.setProperty(LDAPUserGroupCallbackImpl.SEARCH_SCOPE, "abc");
        UserGroupCallback ldapUserGroupCallback = new LDAPUserGroupCallbackImpl(properties);

        assertUsers(ldapUserGroupCallback, true, true, false, false);
        assertGroups(ldapUserGroupCallback, true, true, false, false);
    }

    @Test
    public void testUserExistsWithCommaInDN() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.CUSTOM);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        boolean userExists = ldapUserGroupCallback.existsUser("john,jr");
        Assertions.assertThat(userExists).isTrue();
    }

    @Test
    public void testGroupExistsWithCommaInDN() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.CUSTOM);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        boolean groupExists = ldapUserGroupCallback.existsGroup("manager,eng");
        Assertions.assertThat(groupExists).isTrue();
    }   
    
    @Test
    public void testGroupsForUserWithCommaInDN() {
        UserGroupCallback ldapUserGroupCallback = createLdapUserGroupCallback(Configuration.CUSTOM);
        Assertions.assertThat(ldapUserGroupCallback).isNotNull();

        List<String> userGroups = ldapUserGroupCallback.getGroupsForUser("john,jr");
        Assertions.assertThat(userGroups).hasSize(1).allMatch(s -> s.equals("manager,eng"));
    }
    
    private Properties createUserGroupCallbackProperties() {
        Properties properties = new Properties();
        properties.setProperty(Context.PROVIDER_URL, SERVER_URL);
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_ROLES_FILTER, "(member={0})");
        return properties;
    }

    private Properties createUserGroupCallbackProperties(SearchScope searchScope) {
        Properties properties = createUserGroupCallbackProperties();
        properties.setProperty(LDAPUserGroupCallbackImpl.SEARCH_SCOPE, searchScope.name());
        return properties;
    }

    private UserGroupCallback createLdapUserGroupCallback(Configuration config) {
        switch (config) {
            case CUSTOM:
                return new LDAPUserGroupCallbackImpl(createUserGroupCallbackProperties());
            case SYSTEM:
                System.setProperty("jbpm.usergroup.callback.properties", "/jbpm.usergroup.callback.properties");
            case DEFAULT:
                return new LDAPUserGroupCallbackImpl(true);
            default:
                throw new IllegalArgumentException("unknown config type");
        }
    }

    private UserGroupCallback createLdapUserGroupCallbackWithUserCtx(SearchScope searchScope, String userCtx) {
        Properties properties = createUserGroupCallbackProperties(searchScope);
        properties.setProperty(LDAPUserGroupCallbackImpl.USER_CTX, userCtx);
        return new LDAPUserGroupCallbackImpl(properties);
    }

    private UserGroupCallback createLdapUserGroupCallbackWithRoleCtx(SearchScope searchScope, String roleCtx) {
        Properties properties = createUserGroupCallbackProperties(searchScope);
        properties.setProperty(LDAPUserGroupCallbackImpl.ROLE_CTX, roleCtx);
        return new LDAPUserGroupCallbackImpl(properties);
    }

    private void assertUsers(UserGroupCallback userGroupCallback, boolean john, boolean mary, boolean peter,
                             boolean mike) {
        Assertions.assertThat(userGroupCallback).isNotNull();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(userGroupCallback.existsUser("john")).as("john").isEqualTo(john);
        assertions.assertThat(userGroupCallback.existsUser("mary")).as("mary").isEqualTo(mary);
        assertions.assertThat(userGroupCallback.existsUser("peter")).as("peter").isEqualTo(peter);
        assertions.assertThat(userGroupCallback.existsUser("mike")).as("mike").isEqualTo(mike);
        assertions.assertAll();
    }

    private void assertGroups(UserGroupCallback userGroupCallback, boolean manager, boolean user, boolean analyst,
                              boolean developer) {
        Assertions.assertThat(userGroupCallback).isNotNull();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(userGroupCallback.existsGroup("manager")).as("manager").isEqualTo(manager);
        assertions.assertThat(userGroupCallback.existsGroup("user")).as("user").isEqualTo(user);
        assertions.assertThat(userGroupCallback.existsGroup("analyst")).as("analyst").isEqualTo(analyst);
        assertions.assertThat(userGroupCallback.existsGroup("developer")).as("developer").isEqualTo(developer);
        assertions.assertAll();
    }

}
