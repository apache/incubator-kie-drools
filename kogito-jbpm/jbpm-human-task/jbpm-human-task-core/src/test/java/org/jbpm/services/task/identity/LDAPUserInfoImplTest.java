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

import java.util.Iterator;
import java.util.Properties;
import javax.naming.Context;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.jbpm.services.task.utils.LdapSearcher.SearchScope;
import org.junit.Test;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;

import static org.jbpm.services.task.utils.LdapSearcher.SearchScope.*;

public class LDAPUserInfoImplTest extends LDAPBaseTest {

    private static final User JOHN = TaskModelProvider.getFactory().newUser("john");
    private static final User JOHN_DN = TaskModelProvider.getFactory().newUser("uid=john,ou=People,dc=jbpm,dc=org");
    private static final User MARY = TaskModelProvider.getFactory().newUser("mary");
    private static final User MARY_DN = TaskModelProvider.getFactory().newUser("uid=mary,ou=People,dc=jbpm,dc=org");
    private static final User PETER = TaskModelProvider.getFactory().newUser("peter");
    private static final User MIKE = TaskModelProvider.getFactory().newUser("mike");

    private static final Group MANAGER = TaskModelProvider.getFactory().newGroup("manager");
    private static final Group MANAGER_DN = TaskModelProvider.getFactory().newGroup("cn=manager,ou=Roles,dc=jbpm,dc=org");
    private static final Group USER = TaskModelProvider.getFactory().newGroup("user");
    private static final Group USER_DN = TaskModelProvider.getFactory().newGroup("cn=user,ou=Roles,dc=jbpm,dc=org");
    private static final Group ANALYST = TaskModelProvider.getFactory().newGroup("analyst");
    private static final Group DEVELOPER = TaskModelProvider.getFactory().newGroup("developer");
    
    private static final String JOHN_EMAIL = "johndoe@jbpm.org";

    private Properties createUserInfoProperties() {
        Properties properties = new Properties();
        properties.setProperty(Context.PROVIDER_URL, SERVER_URL);
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, "ou=People,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, "ou=Roles,dc=jbpm,dc=org");
        properties.setProperty(LDAPUserInfoImpl.USER_FILTER, "(uid={0})");
        properties.setProperty(LDAPUserInfoImpl.ROLE_FILTER, "(cn={0})");
        properties.setProperty(LDAPUserInfoImpl.EMAIL_FILTER, "(mail={0})");
        return properties;
    }

    private Properties createUserInfoProperties(SearchScope searchScope) {
        Properties properties = createUserInfoProperties();
        properties.setProperty(LDAPUserInfoImpl.SEARCH_SCOPE, searchScope.name());
        return properties;
    }

    private void testGetDisplayName(OrganizationalEntity entity, String expectedName, boolean customAttribute) {
        Properties properties = createUserInfoProperties();
        if (customAttribute) {
            properties.setProperty(LDAPUserInfoImpl.NAME_ATTR_ID, "name");
        }
        if (entity.getId().startsWith("uid=") || entity.getId().startsWith("cn=")) {
            properties.setProperty(LDAPUserInfoImpl.IS_ENTITY_ID_DN, "true");
        }
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);
        String name = ldapUserInfo.getDisplayName(entity);
        Assertions.assertThat(name).isNotNull();
        Assertions.assertThat(name).isEqualTo(expectedName);
    }

    @Test
    public void testGetDisplayNameForUserByDefaultAttribute() {
        testGetDisplayName(JOHN, "John Doe", false);
    }

    @Test
    public void testGetDisplayNameForUserDnByDefaultAttribute() {
        testGetDisplayName(JOHN_DN, "John Doe", false);
    }

    @Test
    public void testGetDisplayNameForUserByCustomAttribute() {
        testGetDisplayName(MARY, "Mary Snow", true);
    }

    @Test
    public void testGetDisplayNameForUserDnByCustomAttribute() {
        testGetDisplayName(MARY_DN, "Mary Snow", true);
    }

    @Test
    public void testGetDisplayNameForGroupByDefaultAttribute() {
        testGetDisplayName(MANAGER, "jBPM manager", false);
    }

    @Test
    public void testGetDisplayNameForGroupDnByDefaultAttribute() {
        testGetDisplayName(MANAGER_DN, "jBPM manager", false);
    }

    @Test
    public void testGetDisplayNameForGroupByCustomAttribute() {
        testGetDisplayName(USER, "jBPM user", true);
    }

    @Test
    public void testGetDisplayNameForGroupDnByCustomAttribute() {
        testGetDisplayName(USER_DN, "jBPM user", true);
    }

    private void testGetMembersForGroup(boolean emptyGroup, boolean customAttribute, boolean distinguishedName) {
        Properties properties = createUserInfoProperties();
        if (customAttribute) {
            properties.setProperty(LDAPUserInfoImpl.MEMBER_ATTR_ID, "representative");
        }
        if (distinguishedName) {
            properties.setProperty(LDAPUserInfoImpl.IS_ENTITY_ID_DN, "true");
        }
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);

        Group group;
        if (distinguishedName) {
            group = emptyGroup ? USER_DN : MANAGER_DN;
        } else {
            group = emptyGroup ? USER : MANAGER;
        }
        Iterator<OrganizationalEntity> iterator = ldapUserInfo.getMembersForGroup(group);

        if (emptyGroup) {
            Assertions.assertThat(iterator.hasNext()).isFalse();
            return;
        }

        Assertions.assertThat(iterator.hasNext()).isTrue();
        User user = (User) iterator.next();
        if (customAttribute) {
            Assertions.assertThat(user.getId()).isEqualTo(MARY_DN.getId());
        } else {
            Assertions.assertThat(user.getId()).isEqualTo(JOHN_DN.getId());
        }
        Assertions.assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void testGetMembersForGroupByDefaultAttribute() {
        testGetMembersForGroup(false, false, false);
    }


    @Test
    public void testGetMembersForGroupDnByDefaultAttribute() {
        testGetMembersForGroup(false, false, true);
    }

    @Test
    public void testGetMembersForGroupByCustomAttribute() {
        testGetMembersForGroup(false, true, false);
    }

    @Test
    public void testGetMembersForGroupDnByCustomAttribute() {
        testGetMembersForGroup(false, true, true);
    }

    @Test
    public void testGetMembersForEmptyGroupByDefaultAttribute() {
        testGetMembersForGroup(true, false, false);
    }

    @Test
    public void testGetMembersForEmptyGroupByCustomAttribute() {
        testGetMembersForGroup(true, true, false);
    }

    private void testHasEmail(Group group, boolean hasEmail, boolean customAttribute) {
        Properties properties = createUserInfoProperties();
        if (customAttribute) {
            properties.setProperty(LDAPUserInfoImpl.EMAIL_ATTR_ID, "email");
        }
        if (group.getId().startsWith("cn=")) {
            properties.setProperty(LDAPUserInfoImpl.IS_ENTITY_ID_DN, "true");
        }
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);

        Assertions.assertThat(ldapUserInfo.hasEmail(group)).isEqualTo(hasEmail);
    }

    @Test
    public void testHasExistingEmailByDefaultAttribute() {
        testHasEmail(MANAGER, true, false);
    }


    @Test
    public void testHasExistingEmailDnByDefaultAttribute() {
        testHasEmail(MANAGER_DN, true, false);
    }

    @Test
    public void testHasExistingEmailByCustomAttribute() {
        testHasEmail(USER, true, true);
    }


    @Test
    public void testHasExistingEmailDnByCustomAttribute() {
        testHasEmail(USER_DN, true, true);
    }

    @Test
    public void testHasNonExistingEmailByDefaultAttribute() {
        testHasEmail(USER, false, false);
    }

    @Test
    public void testHasNonExistingEmailByCustomAttribute() {
        testHasEmail(MANAGER, false, true);
    }

    private void testGetEmailForEntity(OrganizationalEntity entity, String email, boolean customAttribute) {
        Properties properties = createUserInfoProperties();
        if (customAttribute) {
            properties.setProperty(LDAPUserInfoImpl.EMAIL_ATTR_ID, "email");
        }
        if (entity.getId().startsWith("uid=") || entity.getId().startsWith("cn=")) {
            properties.setProperty(LDAPUserInfoImpl.IS_ENTITY_ID_DN, "true");
        }
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);

        Assertions.assertThat(ldapUserInfo.getEmailForEntity(entity)).isEqualTo(email);
    }

    @Test
    public void testGetExistingEmailForUserByDefaultAttribute() {
        testGetEmailForEntity(JOHN, "johndoe@jbpm.org", false);
    }

    @Test
    public void testGetExistingEmailForUserDnByDefaultAttribute() {
        testGetEmailForEntity(JOHN_DN, "johndoe@jbpm.org", false);
    }

    @Test
    public void testGetExistingEmailForUserByCustomAttribute() {
        testGetEmailForEntity(MARY, "marysnow@jbpm.org", true);
    }

    @Test
    public void testGetExistingEmailForUserDnByCustomAttribute() {
        testGetEmailForEntity(MARY_DN, "marysnow@jbpm.org", true);
    }

    @Test
    public void testGetNonExistingEmailForUserByDefaultAttribute() {
        testGetEmailForEntity(MARY, null, false);
    }

    @Test
    public void testGetNonExistingEmailForUserByCustomAttribute() {
        testGetEmailForEntity(JOHN, null, true);
    }

    @Test
    public void testGetExistingEmailForGroupByDefaultAttribute() {
        testGetEmailForEntity(MANAGER, "manager@jbpm.org", false);
    }

    @Test
    public void testGetExistingEmailForGroupDnByDefaultAttribute() {
        testGetEmailForEntity(MANAGER_DN, "manager@jbpm.org", false);
    }

    @Test
    public void testGetExistingEmailForGroupByCustomAttribute() {
        testGetEmailForEntity(USER, "user@jbpm.org", true);
    }

    @Test
    public void testGetExistingEmailForGroupDnByCustomAttribute() {
        testGetEmailForEntity(USER_DN, "user@jbpm.org", true);
    }

    @Test
    public void testGetNonExistingEmailForGroupByDefaultAttribute() {
        testGetEmailForEntity(USER, null, false);
    }

    @Test
    public void testGetNonExistingEmailForGroupByCustomAttribute() {
        testGetEmailForEntity(MANAGER, null, true);
    }

    private void testGetLanguageForEntity(OrganizationalEntity entity, String language, boolean customAttribute) {
        Properties properties = createUserInfoProperties();
        if (customAttribute) {
            properties.setProperty(LDAPUserInfoImpl.LANG_ATTR_ID, "language");
        }
        if (entity.getId().startsWith("uid=") || entity.getId().startsWith("cn=")) {
            properties.setProperty(LDAPUserInfoImpl.IS_ENTITY_ID_DN, "true");
        }
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);

        Assertions.assertThat(ldapUserInfo.getLanguageForEntity(entity)).isEqualTo(language);
    }

    @Test
    public void testGetLanguageForUserByDefaultAttribute() {
        testGetLanguageForEntity(JOHN, "en-US", false);
    }

    @Test
    public void testGetLanguageForUserDnByDefaultAttribute() {
        testGetLanguageForEntity(JOHN_DN, "en-US", false);
    }

    @Test
    public void testGetLanguageForUserByCustomAttribute() {
        testGetLanguageForEntity(MARY, "fr-FR", true);
    }

    @Test
    public void testGetLanguageForUserDnByCustomAttribute() {
        testGetLanguageForEntity(MARY_DN, "fr-FR", true);
    }

    @Test
    public void testGetDefaultLanguageForUserByDefaultAttribute() {
        testGetLanguageForEntity(MARY, "en-UK", false);
    }

    @Test
    public void testGetDefaultLanguageForUserByCustomAttribute() {
        testGetLanguageForEntity(JOHN, "en-UK", true);
    }

    @Test
    public void testGetLanguageForGroupByDefaultAttribute() {
        testGetLanguageForEntity(MANAGER, "en-US", false);
    }

    @Test
    public void testGetLanguageForGroupDnByDefaultAttribute() {
        testGetLanguageForEntity(MANAGER_DN, "en-US", false);
    }

    @Test
    public void testGetLanguageForGroupByCustomAttribute() {
        testGetLanguageForEntity(USER, "fr-FR", true);
    }

    @Test
    public void testGetLanguageForGroupDnByCustomAttribute() {
        testGetLanguageForEntity(USER_DN, "fr-FR", true);
    }

    @Test
    public void testGetDefaultLanguageForGroupByDefaultAttribute() {
        testGetLanguageForEntity(USER, "en-UK", false);
    }

    @Test
    public void testGetDefaultLanguageForGroupByCustomAttribute() {
        testGetLanguageForEntity(MANAGER, "en-UK", true);
    }
    
    private void testGetEntityForEmail(String email, String expected, boolean useDN) {
        Properties properties = createUserInfoProperties();
        if (useDN) {
            properties.setProperty(LDAPUserInfoImpl.IS_ENTITY_ID_DN, "true");
        }
        UserInfo ldapUserInfo = new LDAPUserInfoImpl(properties);

        Assertions.assertThat(ldapUserInfo.getEntityForEmail(email)).isEqualTo(expected);
    }
    
    @Test
    public void testGetEntityForEmail() {
        testGetEntityForEmail(JOHN_EMAIL, JOHN.getId(), false);
    }
    
    @Test
    public void testGetEntityForEmailAsDN() {
        
        testGetEntityForEmail(JOHN_EMAIL, JOHN_DN.getId(), true);
    }

    private UserInfo createLdapUserInfoUid(Properties properties) {
        properties.setProperty(LDAPUserInfoImpl.NAME_ATTR_ID, "uid");
        return new LDAPUserInfoImpl(properties);
    }

    private UserInfo createLdapUserInfoWithUserCtx(SearchScope searchScope, String userCtx) {
        Properties properties = createUserInfoProperties(searchScope);
        properties.setProperty(LDAPUserInfoImpl.USER_CTX, userCtx);
        return createLdapUserInfoUid(properties);
    }

    private UserInfo createLdapUserInfoCn(Properties properties) {
        properties.setProperty(LDAPUserInfoImpl.NAME_ATTR_ID, "cn");
        return new LDAPUserInfoImpl(properties);
    }

    private UserInfo createLdapUserInfoWithGroupCtx(SearchScope searchScope, String groupCtx) {
        Properties properties = createUserInfoProperties(searchScope);
        properties.setProperty(LDAPUserInfoImpl.ROLE_CTX, groupCtx);
        return createLdapUserInfoCn(properties);
    }

    private void assertUsers(UserInfo userInfo, boolean john, boolean mary, boolean peter, boolean mike) {
        Assertions.assertThat(userInfo).isNotNull();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(userInfo.getDisplayName(JOHN)).as(JOHN.getId()).isEqualTo(john ? JOHN.getId() : null);
        assertions.assertThat(userInfo.getDisplayName(MARY)).as(MARY.getId()).isEqualTo(mary ? MARY.getId() : null);
        assertions.assertThat(userInfo.getDisplayName(PETER)).as(PETER.getId()).isEqualTo(peter ? PETER.getId() : null);
        assertions.assertThat(userInfo.getDisplayName(MIKE)).as(MIKE.getId()).isEqualTo(mike ? MIKE.getId() : null);
        assertions.assertAll();
    }

    private void assertGroups(UserInfo userInfo, boolean manager, boolean user, boolean analyst, boolean developer) {
        Assertions.assertThat(userInfo).isNotNull();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(userInfo.getDisplayName(MANAGER)).as(MANAGER.getId())
                .isEqualTo(manager ? MANAGER.getId() : null);
        assertions.assertThat(userInfo.getDisplayName(USER)).as(USER.getId())
                .isEqualTo(user ? USER.getId() : null);
        assertions.assertThat(userInfo.getDisplayName(ANALYST)).as(ANALYST.getId())
                .isEqualTo(analyst ? ANALYST.getId() : null);
        assertions.assertThat(userInfo.getDisplayName(DEVELOPER)).as(DEVELOPER.getId())
                .isEqualTo(developer ? DEVELOPER.getId() : null);
        assertions.assertAll();
    }

    @Test
    public void testUsersObjectScopeBaseDnContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(OBJECT_SCOPE, "dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, false, false, false, false);
    }

    @Test
    public void testUsersObjectScopePeopleContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(OBJECT_SCOPE, "ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, false, false, false, false);
    }

    @Test
    public void testUsersObjectScopeJohnContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(OBJECT_SCOPE, "uid=john,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, true, false, false, false);
    }

    @Test
    public void testUsersOneLevelScopeBaseDnContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(ONELEVEL_SCOPE, "dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, false, false, false, false);
    }

    @Test
    public void testUsersOneLevelScopePeopleContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(ONELEVEL_SCOPE, "ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, true, true, false, false);
    }

    @Test
    public void testUsersOneLevelScopeJohnContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(ONELEVEL_SCOPE, "uid=john,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, false, false, false, false);
    }

    @Test
    public void testUsersOneLevelScopeEngContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(ONELEVEL_SCOPE, "ou=ENG,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, false, false, true, false);
    }

    @Test
    public void testUsersSubtreeScopeBaseDnContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(SUBTREE_SCOPE, "dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, true, true, true, true);
    }

    @Test
    public void testUsersSubtreeScopePeopleContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(SUBTREE_SCOPE, "ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, true, true, true, true);
    }

    @Test
    public void testUsersSubtreeScopeJohnContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(SUBTREE_SCOPE, "uid=john,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, true, false, false, false);
    }

    @Test
    public void testUsersSubtreeScopeEngContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithUserCtx(SUBTREE_SCOPE, "ou=ENG,ou=People,dc=jbpm,dc=org");
        assertUsers(ldapUserInfo, false, false, true, true);
    }

    @Test
    public void testGroupsObjectScopeBaseDnContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(OBJECT_SCOPE, "dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, false, false, false, false);
    }

    @Test
    public void testGroupsObjectScopeRolesContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(OBJECT_SCOPE, "ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, false, false, false, false);
    }

    @Test
    public void testGroupsObjectScopeManagerContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(OBJECT_SCOPE, "cn=manager,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, true, false, false, false);
    }

    @Test
    public void testGroupsOneLevelScopeBaseDnContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(ONELEVEL_SCOPE, "dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, false, false, false, false);
    }

    @Test
    public void testGroupsOneLevelScopeRolesContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(ONELEVEL_SCOPE, "ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, true, true, false, false);
    }

    @Test
    public void testGroupsOneLevelScopeManagerContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(ONELEVEL_SCOPE, "cn=manager,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, false, false, false, false);
    }

    @Test
    public void testGroupsOneLevelScopeEngContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(ONELEVEL_SCOPE, "ou=ENG,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, false, false, true, false);
    }

    @Test
    public void testGroupsSubtreeScopeBaseDnContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(SUBTREE_SCOPE, "dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, true, true, true, true);
    }

    @Test
    public void testGroupsSubtreeScopeRolesContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(SUBTREE_SCOPE, "ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, true, true, true, true);
    }

    @Test
    public void testGroupsSubtreeScopeManagerContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(SUBTREE_SCOPE, "cn=manager,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, true, false, false, false);
    }

    @Test
    public void testGroupsSubtreeScopeEngContext() {
        UserInfo ldapUserInfo = createLdapUserInfoWithGroupCtx(SUBTREE_SCOPE, "ou=ENG,ou=Roles,dc=jbpm,dc=org");
        assertGroups(ldapUserInfo, false, false, true, true);
    }

    @Test
    public void testUsersDefaultScope() {
        UserInfo ldapUserInfo = createLdapUserInfoUid(createUserInfoProperties());
        assertUsers(ldapUserInfo, true, true, false, false);
    }

    @Test
    public void testGroupsDefaultScope() {
        UserInfo ldapUserInfo = createLdapUserInfoCn(createUserInfoProperties());
        assertGroups(ldapUserInfo, true, true, false, false);
    }

    @Test
    public void testUsersInvalidScope() {
        Properties properties = createUserInfoProperties();
        properties.setProperty(LDAPUserInfoImpl.SEARCH_SCOPE, "xyz");
        UserInfo ldapUserInfo = createLdapUserInfoUid(properties);

        assertUsers(ldapUserInfo, true, true, false, false);
    }

    @Test
    public void testGroupsInvalidScope() {
        Properties properties = createUserInfoProperties();
        properties.setProperty(LDAPUserInfoImpl.SEARCH_SCOPE, "xyz");
        UserInfo ldapUserInfo = createLdapUserInfoCn(properties);

        assertGroups(ldapUserInfo, true, true, false, false);
    }

}
