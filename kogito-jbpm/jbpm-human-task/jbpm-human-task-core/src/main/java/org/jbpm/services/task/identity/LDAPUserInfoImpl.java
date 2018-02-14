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
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.jbpm.services.task.utils.LdapSearcher;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;

public class LDAPUserInfoImpl extends AbstractLDAPUserGroupInfo implements UserInfo {

    private static final String DEFAULT_PROPERTIES_NAME = "jbpm.user.info";

    public static final String USER_CTX = "ldap.user.ctx";
    public static final String ROLE_CTX = "ldap.role.ctx";

    public static final String USER_FILTER = "ldap.user.filter";
    public static final String ROLE_FILTER = "ldap.role.filter";
    public static final String ROLE_MEMBERS_FILTER = "ldap.role.members.filter";
    public static final String EMAIL_FILTER = "ldap.email.filter";

    public static final String EMAIL_ATTR_ID = "ldap.email.attr.id";
    public static final String NAME_ATTR_ID = "ldap.name.attr.id";
    public static final String LANG_ATTR_ID = "ldap.lang.attr.id";
    public static final String MEMBER_ATTR_ID = "ldap.member.attr.id";
    public static final String USER_ATTR_ID = "ldap.user.attr.id";
    public static final String ROLE_ATTR_ID = "ldap.role.attr.id";

    public static final String IS_ENTITY_ID_DN = "ldap.entity.id.dn";
    public static final String SEARCH_SCOPE = "ldap.search.scope";

    private static final String[] REQUIRED_PROPERTIES = {USER_CTX, ROLE_CTX, USER_FILTER, ROLE_FILTER};

    private static final String DEFAULT_EMAIL_ATTR_ID = "mail";
    private static final String DEFAULT_ENTITY_ID_DN = "false";
    private static final String DEFAULT_LANG_ATTR_ID = "locale";
    private static final String DEFAULT_MEMBER_ATTR_ID = "member";
    private static final String DEFAULT_NAME_ATTR_ID = "displayName";

    private static final String DEFAULT_LOCALE = "en-UK";

    /**
     * Constructor needs to have at least one (unused) parameter in order to prevent CDI from automatic deployment.
     * Configuration properties are loaded from a file specified by jbpm.user.info system property or
     * classpath:/jbpm.user.info.properties file.
     * @param activate ignored
     */
    public LDAPUserInfoImpl(boolean activate) {
        super(REQUIRED_PROPERTIES, DEFAULT_PROPERTIES_NAME);
    }

    /**
     * @param config LDAP configuration properties
     */
    public LDAPUserInfoImpl(Properties config) {
        super(REQUIRED_PROPERTIES, config);
    }

    @Override
    public String getDisplayName(OrganizationalEntity entity) {
        return getAttributeValueForEntity(entity, NAME_ATTR_ID, DEFAULT_NAME_ATTR_ID);
    }

    @Override
    public Iterator<OrganizationalEntity> getMembersForGroup(Group group) {
        String roleContext = getConfigProperty(ROLE_CTX);
        String roleFilter = getConfigProperty(ROLE_MEMBERS_FILTER, getConfigProperty(ROLE_FILTER));
        String roleAttrId = getConfigProperty(MEMBER_ATTR_ID, DEFAULT_MEMBER_ATTR_ID);

        String entityId = extractEntityId(group);

        List<String> memberIds = ldapSearcher.search(roleContext, roleFilter, entityId).getAttributeResults(roleAttrId);
        return memberIds.stream()
                .filter(memberId -> memberId != null)
                .map(memberId -> (OrganizationalEntity) TaskModelProvider.getFactory().newUser(memberId))
                .collect(Collectors.toList())
                .iterator();
    }

    @Override
    public boolean hasEmail(Group group) {
        return getEmailForEntity(group) != null;
    }

    @Override
    public String getEmailForEntity(OrganizationalEntity entity) {
        return getAttributeValueForEntity(entity, EMAIL_ATTR_ID, DEFAULT_EMAIL_ATTR_ID);
    }

    @Override
    public String getLanguageForEntity(OrganizationalEntity entity) {
        String result = getAttributeValueForEntity(entity, LANG_ATTR_ID, DEFAULT_LANG_ATTR_ID);
        return result == null ? DEFAULT_LOCALE : result;
    }

    private String getAttributeValueForEntity(OrganizationalEntity entity, String attributeName, String defaultValue) {
        String context = getConfigPropertyByEntity(entity, USER_CTX, ROLE_CTX);
        String filter = getConfigPropertyByEntity(entity, USER_FILTER, ROLE_FILTER);
        String attrId = getConfigProperty(attributeName, defaultValue);

        String entityId = extractEntityId(entity);

        return ldapSearcher.search(context, filter, entityId).getSingleAttributeResult(attrId);
    }

    private String getConfigPropertyByEntity(OrganizationalEntity entity, String userKey, String roleKey) {
        if (entity instanceof User) {
            return getConfigProperty(userKey);
        } else if (entity instanceof Group) {
            return getConfigProperty(roleKey);
        } else {
            throw new IllegalArgumentException("Unknown organizational entity: " + entity);
        }
    }

    private String extractEntityId(OrganizationalEntity entity) {
        if (!isEntityIdDn()) {
            return entity.getId();
        }

       

        String entityAttrId = null;
        if (entity instanceof User) {
            entityAttrId = getConfigProperty(USER_ATTR_ID, DEFAULT_USER_ATTR_ID);
        } else if (entity instanceof Group) {
            entityAttrId = getConfigProperty(ROLE_ATTR_ID, DEFAULT_ROLE_ATTR_ID);
        }
        
        return extractAttribute(entity.getId(), entityAttrId);
    }
    
    private String extractAttribute(String entityId, String entityAttrId) {
        String entityDN = entityId;
        String[] attributes = entityDN.split(",");

        if (attributes.length == 1) {
            return entityDN;
        }

        for (String attribute : attributes) {
            String[] keyValue = attribute.split("=");

            if (keyValue[0].equalsIgnoreCase(entityAttrId)) {
                return keyValue[1];
            }
        }

        throw new RuntimeException("Cannot parse '" + entityAttrId + "' attribute from entity DN '" + entityDN + "'");
    }

    private boolean isEntityIdDn() {
        return Boolean.parseBoolean(getConfigProperty(IS_ENTITY_ID_DN, DEFAULT_ENTITY_ID_DN));
    }

    @Override
    public String getEntityForEmail(String email) {        
        String context = getConfigProperty(USER_CTX);
        String filter = getConfigProperty(EMAIL_FILTER);
        String attributeId = getConfigProperty(USER_ATTR_ID, DEFAULT_USER_ATTR_ID);
        LdapSearcher searcher = ldapSearcher.search(context, filter, email);

        if (searcher.getSearchResults().isEmpty()) {
            return null;
        }
        
        if (isEntityIdDn()) {
            return searcher.getSingleSearchResult().getNameInNamespace();
        }
        
        return searcher.getSingleAttributeResult(attributeId);
    }

}
