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
import java.util.stream.Collectors;

import javax.naming.directory.SearchResult;

import org.kie.api.task.UserGroupCallback;

/**
 * LDAP integration for Task Service to collect user and role/group information.
 * <p>
 * Following is a list of all supported properties:
 * <ul>
 * <li>ldap.bind.user (optional if LDAP server accepts anonymous access)</li>
 * <li>ldap.bind.pwd (optional if LDAP server accepts anonymous access</li>
 * <li>ldap.user.ctx (mandatory)</li>
 * <li>ldap.role.ctx (mandatory)</li>
 * <li>ldap.user.roles.ctx (optional, if not given ldap.role.ctx will be used)</li>
 * <li>ldap.user.filter (mandatory)</li>
 * <li>ldap.role.filter (mandatory)</li>
 * <li>ldap.user.roles.filter (mandatory)</li>
 * <li>ldap.user.attr.id (optional, if not given 'uid' will be used)</li>
 * <li>ldap.roles.attr.id (optional, if not given 'cn' will be used)</li>
 * <li>ldap.user.id.dn (optional, is user id a DN, instructs the callback to query for user DN before searching for roles, default false)</li>
 * <li>ldap.search.scope (optional, if not given 'ONELEVEL_SCOPE' will be used) possible values are: OBJECT_SCOPE, ONELEVEL_SCOPE, SUBTREE_SCOPE</li>
 * <li>ldap.name.escape (optional, instructs to escape - illegal character in user/group name before the query - currently escapes only comma) by default is set to true</li>
 * <li>java.naming.factory.initial</li>
 * <li>java.naming.security.authentication</li>
 * <li>java.naming.security.protocol</li>
 * <li>java.naming.provider.url</li>
 * <li></li>
 * </ul>
 * </p>
 */
public class LDAPUserGroupCallbackImpl extends AbstractLDAPUserGroupInfo implements UserGroupCallback {

    private static final String DEFAULT_PROPERTIES_NAME = "jbpm.usergroup.callback";

    public static final String USER_CTX = "ldap.user.ctx";
    public static final String ROLE_CTX = "ldap.role.ctx";
    public static final String USER_ROLES_CTX = "ldap.user.roles.ctx";
    public static final String USER_FILTER = "ldap.user.filter";
    public static final String ROLE_FILTER = "ldap.role.filter";
    public static final String USER_ROLES_FILTER = "ldap.user.roles.filter";
    public static final String USER_ATTR_ID = "ldap.user.attr.id";
    public static final String ROLE_ATTR_ID = "ldap.roles.attr.id";
    public static final String IS_USER_ID_DN = "ldap.user.id.dn";
    public static final String SEARCH_SCOPE = "ldap.search.scope";
    public static final String LDAP_NAME_ESCAPE = "ldap.name.escape";

    private static final String[] REQUIRED_PROPERTIES = {USER_CTX, ROLE_CTX, USER_FILTER, ROLE_FILTER, USER_ROLES_FILTER};

    private static final String DEFAULT_USER_ID_DN = "false";

    /**
     * Constructor needs to have at least one (unused) parameter in order to prevent CDI from automatic deployment.
     * Configuration properties are loaded from a file specified by jbpm.usergroup.callback system property or
     * classpath:/jbpm.usergroup.callback.properties file.
     * @param activate ignored
     */
    public LDAPUserGroupCallbackImpl(boolean activate) {
        super(REQUIRED_PROPERTIES, DEFAULT_PROPERTIES_NAME);
    }

    /**
     * @param config LDAP configuration properties
     */
    public LDAPUserGroupCallbackImpl(Properties config) {
        super(REQUIRED_PROPERTIES, config);
    }

    @Override
    public boolean existsUser(String userId) {
        String context = getConfigProperty(USER_CTX);
        String filter = getConfigProperty(USER_FILTER);
        String attributeId = getConfigProperty(USER_ATTR_ID, DEFAULT_USER_ATTR_ID);

        return existsEntity(userId, context, filter, attributeId);
    }

    @Override
    public boolean existsGroup(String groupId) {
        String context = getConfigProperty(ROLE_CTX);
        String filter = getConfigProperty(ROLE_FILTER);
        String attributeId = getConfigProperty(ROLE_ATTR_ID, DEFAULT_ROLE_ATTR_ID);

        return existsEntity(groupId, context, filter, attributeId);
    }

    private boolean existsEntity(String entityId, String context, String filter, String attributeId) {
        entityId = escapeIllegalChars(entityId);
        String ldapEntityId = ldapSearcher.search(context, filter, entityId).getSingleAttributeResult(attributeId);
        return entityId.equals(ldapEntityId);
    }

    @Override
    public List<String> getGroupsForUser(String userId) {
        String roleContext = getConfigProperty(USER_ROLES_CTX, getConfigProperty(ROLE_CTX));
        String roleFilter = getConfigProperty(USER_ROLES_FILTER);
        String roleAttributeId = getConfigProperty(ROLE_ATTR_ID, DEFAULT_ROLE_ATTR_ID);

        String userDn = userId;
        if (!isUserIdDn()) {
            String userContext = getConfigProperty(USER_CTX);
            String userFilter = getConfigProperty(USER_FILTER);

            SearchResult searchResult = ldapSearcher.search(userContext, userFilter, userId).getSingleSearchResult();
            userDn = searchResult.getNameInNamespace();
        }

        List<String> result = ldapSearcher.search(roleContext, roleFilter, userDn).getAttributeResults(roleAttributeId);
        
        return result.stream().map(r-> unescapeIllegalChars(r)).collect(Collectors.toList());
    }

    private boolean isUserIdDn() {
        return Boolean.parseBoolean(getConfigProperty(IS_USER_ID_DN, DEFAULT_USER_ID_DN));
    }
    
    private boolean escapeOn() {
        return Boolean.parseBoolean(getConfigProperty(LDAP_NAME_ESCAPE, "true"));
    }
    
    protected String escapeIllegalChars(String entityId) {
        if (!escapeOn()) {
            return entityId;
        }
        return entityId.replace(",", "\\,");
    }
    
    protected String unescapeIllegalChars(String entityId) {
        if (!escapeOn()) {
            return entityId;
        }
        return entityId.replace("\\,", ",");
    }

}
