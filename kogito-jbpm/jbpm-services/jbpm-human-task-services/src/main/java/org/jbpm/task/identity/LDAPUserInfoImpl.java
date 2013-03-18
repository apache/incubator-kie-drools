/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task.identity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.jbpm.task.impl.model.GroupImpl;
import org.jbpm.task.impl.model.UserImpl;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.Group;
import org.kie.internal.task.api.model.OrganizationalEntity;
import org.kie.internal.task.api.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alternative
@ApplicationScoped
public class LDAPUserInfoImpl implements UserInfo {
    
    private static final Logger logger = LoggerFactory.getLogger(LDAPUserInfoImpl.class);
    
    protected static final String DEFAULT_PROPERTIES_NAME = "/jbpm.user.info.properties";
    
    public static final String BIND_USER = "ldap.bind.user";
    public static final String BIND_PWD = "ldap.bind.pwd";
    
    public static final String USER_CTX = "ldap.user.ctx";
    public static final String ROLE_CTX = "ldap.role.ctx";
    
    public static final String USER_FILTER = "ldap.user.filter";
    public static final String ROLE_FILTER = "ldap.role.filter";
    public static final String ROLE_MEMBERS_FILTER = "ldap.role.members.filter";
    
    public static final String EMAIL_ATTR_ID = "ldap.email.attr.id";
    public static final String NAME_ATTR_ID = "ldap.name.attr.id";
    public static final String LANG_ATTR_ID = "ldap.lang.attr.id";
    public static final String MEMBER_ATTR_ID = "ldap.member.attr.id";
    public static final String USER_ATTR_ID = "ldap.user.attr.id";
    public static final String ROLE_ATTR_ID = "ldap.role.attr.id";
    
    public static final String IS_ENTITY_ID_DN = "ldap.entity.id.dn";
    
    protected static final String[] requiredProperties = {USER_CTX, ROLE_CTX, USER_FILTER, ROLE_FILTER};

    
    private Properties config;
    
    
    public LDAPUserInfoImpl() {
        String propertiesLocation = System.getProperty("jbpm.user.info.properties");
        
        if (propertiesLocation == null) {
            propertiesLocation = DEFAULT_PROPERTIES_NAME;
        }
        logger.debug("Callback properties will be loaded from " + propertiesLocation);
        InputStream in = this.getClass().getResourceAsStream(propertiesLocation);
        if (in != null) {
            config = new Properties();
            try {
                config.load(in);
            } catch (IOException e) {
                e.printStackTrace();
                config = null;
            }
        }
        
        validate();
    }
    
    public LDAPUserInfoImpl(Properties config) {
        this.config = config;
        validate();
    }

    public String getDisplayName(OrganizationalEntity entity) {
        String context = null;
        String filter = null;
        String attrId = null;
        if (entity instanceof UserImpl) {
            context = this.config.getProperty(USER_CTX);
            filter = this.config.getProperty(USER_FILTER);
            attrId = this.config.getProperty(NAME_ATTR_ID, "displayName");
        } else if (entity instanceof GroupImpl) {
            context = this.config.getProperty(ROLE_CTX);
            filter = this.config.getProperty(ROLE_FILTER);
            attrId = this.config.getProperty(NAME_ATTR_ID, "displayName");
        } else {
            throw new IllegalArgumentException("Unknown organizational entity " + entity);
        }
        String result = searchLdap(context, filter, attrId, entity);
        
        return result;
    }

    public Iterator<OrganizationalEntity> getMembersForGroup(Group group) {
        InitialLdapContext ctx = null;
        List<OrganizationalEntity> memebers = new ArrayList<OrganizationalEntity>();
        try {
            ctx = buildInitialLdapContext();
            
            String roleContext = this.config.getProperty(ROLE_CTX);
            String roleFilter = this.config.getProperty(ROLE_MEMBERS_FILTER, this.config.getProperty(ROLE_FILTER));
            String roleAttrId = this.config.getProperty(MEMBER_ATTR_ID, "member");
            
            roleFilter = roleFilter.replaceAll("\\{0\\}", group.getId());
            
            SearchControls constraints = new SearchControls();
            
            NamingEnumeration<SearchResult> result = ctx.search(roleContext, roleFilter, constraints);
            while (result.hasMore()) {
                SearchResult sr = result.next();
                Attribute member = sr.getAttributes().get(roleAttrId);
                for (int i = 0; i < member.size(); i++) {
                    memebers.add(new UserImpl(member.get(i).toString()));
                }
                
            }
            result.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
            
        }
        return memebers.iterator();
    }

    public boolean hasEmail(Group group) {
        InitialLdapContext ctx = null;
        boolean exists = false;
        try {
            ctx = buildInitialLdapContext();
            
            String roleContext = this.config.getProperty(ROLE_CTX);
            String roleFilter = this.config.getProperty(ROLE_FILTER);
            String roleAttrId = this.config.getProperty(EMAIL_ATTR_ID, "mail");
            
            roleFilter = roleFilter.replaceAll("\\{0\\}", group.getId());
            
            SearchControls constraints = new SearchControls();
            
            NamingEnumeration<SearchResult> result = ctx.search(roleContext, roleFilter, constraints);
            if (result.hasMore()) {
                SearchResult sr = result.next();
                Attribute ldapGroupEmail = sr.getAttributes().get(roleAttrId);
                
                if (ldapGroupEmail != null && ldapGroupEmail.get() != null) {
                    exists = true;
                }
                
            }
            result.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
            
        }
        return exists;
    }

    public String getEmailForEntity(OrganizationalEntity entity) {
        String context = null;
        String filter = null;
        String attrId = null;
        if (entity instanceof UserImpl) {
            context = this.config.getProperty(USER_CTX);
            filter = this.config.getProperty(USER_FILTER);
            attrId = this.config.getProperty(EMAIL_ATTR_ID, "mail");
        } else if (entity instanceof GroupImpl) {
            context = this.config.getProperty(ROLE_CTX);
            filter = this.config.getProperty(ROLE_FILTER);
            attrId = this.config.getProperty(EMAIL_ATTR_ID, "mail");
        } else {
            throw new IllegalArgumentException("Unknown organizational entity " + entity);
        }
        String result = searchLdap(context, filter, attrId, entity);
        
        return result;
    }

    public String getLanguageForEntity(OrganizationalEntity entity) {
        String context = null;
        String filter = null;
        String attrId = null;
        if (entity instanceof User) {
            context = this.config.getProperty(USER_CTX);
            filter = this.config.getProperty(USER_FILTER);
            attrId = this.config.getProperty(LANG_ATTR_ID, "locale");
        } else if (entity instanceof Group) {
            context = this.config.getProperty(ROLE_CTX);
            filter = this.config.getProperty(ROLE_FILTER);
            attrId = this.config.getProperty(LANG_ATTR_ID, "locale");
        } else {
            throw new IllegalArgumentException("Unknown organizational entity " + entity);
        }
        String result = searchLdap(context, filter, attrId, entity);
        if (result == null) {
            // defaults to en-UK
            result = "en-UK";
        }
        return result;
    }
    
    protected void validate() {
        if (this.config == null) {
            throw new IllegalArgumentException("No configuration found for LDAPUserInfoImpl, aborting...");
        }
        StringBuffer missingRequiredProps = new StringBuffer();
        for (String requiredProp : requiredProperties) {
            if (!this.config.containsKey(requiredProp)) {
                if (missingRequiredProps.length() > 0) {
                    missingRequiredProps.append(", ");
                }
                missingRequiredProps.append(requiredProp);
            }
        }
        
        if (missingRequiredProps.length() > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Validation failed due to missing required properties: " + missingRequiredProps.toString());
            }
            throw new IllegalArgumentException("Missing required properties to configure LDAPUserInfoImpl: " + missingRequiredProps.toString());
        }
    }
    
    protected InitialLdapContext buildInitialLdapContext() throws NamingException {

        // Set defaults for key values if they are missing
        String factoryName = this.config.getProperty(Context.INITIAL_CONTEXT_FACTORY);

        if (factoryName == null)  {

            factoryName = "com.sun.jndi.ldap.LdapCtxFactory";
            this.config.setProperty(Context.INITIAL_CONTEXT_FACTORY, factoryName);
        }

        String authType = this.config.getProperty(Context.SECURITY_AUTHENTICATION);

        if (authType == null) {

            this.config.setProperty(Context.SECURITY_AUTHENTICATION, "simple");
        }

        String protocol = this.config.getProperty(Context.SECURITY_PROTOCOL);

        String providerURL = (String) this.config.getProperty(Context.PROVIDER_URL);
        if (providerURL == null) {

            providerURL = "ldap://localhost:"+ ((protocol != null && protocol.equals("ssl")) ? "636" : "389");
            this.config.setProperty(Context.PROVIDER_URL, providerURL);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Using following InitialLdapContext properties:");
            logger.debug("Factory " + this.config.getProperty(Context.INITIAL_CONTEXT_FACTORY));
            logger.debug("Authentication " + this.config.getProperty(Context.SECURITY_AUTHENTICATION));
            logger.debug("Protocol " +  this.config.getProperty(Context.SECURITY_PROTOCOL));
            logger.debug("Provider URL " +  this.config.getProperty(Context.PROVIDER_URL));
        }
        
        return new InitialLdapContext(this.config, null);
    }
    
    protected String searchLdap(String context, String filter, String attrId, OrganizationalEntity entity) {
        InitialLdapContext ctx = null;
        String result = null;
        try {
            ctx = buildInitialLdapContext();
            String entityId =  entity.getId();
            if (Boolean.parseBoolean(this.config.getProperty(IS_ENTITY_ID_DN, "false"))) {
                entityId = extractUserId(entityId, entity);
            }
            filter = filter.replaceAll("\\{0\\}",entityId);
            
            SearchControls constraints = new SearchControls();
            
            NamingEnumeration<SearchResult> ldapResult = ctx.search(context, filter, constraints);
            if (ldapResult.hasMore()) {
                SearchResult sr = ldapResult.next();
                Attribute entry = sr.getAttributes().get(attrId);
                if (entry != null) {
                    result = (String) entry.get();
                }
                
            }
            ldapResult.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
            
        }
        return result;
    }
    
    protected String extractUserId(String userDN, OrganizationalEntity entity) {
        String[] attributes = userDN.split(",");
        
        if (attributes.length == 1) {
            return userDN;
        }
        String entityAttrId = null;
        if (entity instanceof UserImpl) {
            entityAttrId = this.config.getProperty(USER_ATTR_ID, "uid");
        } else if (entity instanceof GroupImpl) {
            entityAttrId = this.config.getProperty(ROLE_ATTR_ID, "cn");
        }
        if (attributes != null) {
            for (String attribute : attributes) {
                String[] keyValue = attribute.split("=");
                
                if (keyValue[0].equalsIgnoreCase(entityAttrId)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

}
