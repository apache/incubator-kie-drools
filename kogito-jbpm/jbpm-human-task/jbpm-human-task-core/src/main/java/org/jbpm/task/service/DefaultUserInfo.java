package org.jbpm.task.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;

public class DefaultUserInfo implements UserInfo {
    
    protected Map<String, Map<String, Object>> registry = new HashMap<String, Map<String,Object>>();

    public DefaultUserInfo() {
        try {
        Properties registryProps = new Properties();
        registryProps.load(this.getClass().getResourceAsStream("/userinfo.properties"));
        buildRegistry(registryProps);
        } catch (Exception e) {
            throw new IllegalStateException("Problem loading userinfo properties", e);
        }
    }
    
    /**
     * Constructs default UserInfo implementation to provide required information to the escalation handler.
     * following is the string for every organizational entity
     * entityId=email:locale:displayname:[member,member]
     * members are optional and should be given for group entities
     * @param registryProps
     */
    public DefaultUserInfo(Properties registryProps) {
        buildRegistry(registryProps);
    }
    
    
    public String getDisplayName(OrganizationalEntity entity) {
        Map<String, Object> entityInfo = registry.get(entity.getId());
        
        if (entityInfo != null) {
            return (String) entityInfo.get("name");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Iterator<OrganizationalEntity> getMembersForGroup(Group group) {
        Map<String, Object> entityInfo = registry.get(group.getId());
        
        if (entityInfo != null) {
            return  ((List<OrganizationalEntity>) entityInfo.get("members")).iterator();
        }
        return null;
    }

    public boolean hasEmail(Group group) {
        Map<String, Object> entityInfo = registry.get(group.getId());
        
        if (entityInfo != null) {
            return entityInfo.containsKey("email");
        }
        return false;
    }

    public String getEmailForEntity(OrganizationalEntity entity) {
        Map<String, Object> entityInfo = registry.get(entity.getId());
        
        if (entityInfo != null) {
            return (String) entityInfo.get("email");
        }
        throw new IllegalStateException("No EMail address found for " + entity.getId());
    }

    public String getLanguageForEntity(OrganizationalEntity entity) {
        Map<String, Object> entityInfo = registry.get(entity.getId());
        
        if (entityInfo != null) {
            return (String) entityInfo.get("locale");
        }
        return null;
    }

    protected void buildRegistry(Properties registryProps) {
        
        if (registryProps != null) {
            Iterator<Object> propertyKeys = registryProps.keySet().iterator();
            while (propertyKeys.hasNext()) {
                String propertyKey = (String) propertyKeys.next();
                
                // following is the string for every organizational entity
                // email:locale:displayname:[member,member]
                // members are optional and should be given for group entities
                
                String propertyValue = registryProps.getProperty(propertyKey);
                String[] elems = propertyValue.split(":");
                
                Map<String, Object> entityInfo = new HashMap<String, Object>();
                entityInfo.put("email", elems[0]);
                entityInfo.put("locale", elems[1]);
                entityInfo.put("name", elems[2]);
                
                if (elems.length == 4 && elems[3] != null) {
                    String memberList = elems[3];
                    if (memberList.startsWith("[")) {
                        memberList = memberList.substring(1);
                    }
                    
                    if (memberList.endsWith("]")) {
                        memberList = memberList.substring(0, memberList.length()-1);
                    }
                    String[] members = memberList.split(",");
                    
                    List<OrganizationalEntity> membersList = new ArrayList<OrganizationalEntity>();
                    for (String member : members) {
                        membersList.add(new User(member));
                    }
                    entityInfo.put("members", membersList);
                }
                registry.put(propertyKey, entityInfo);
                
            }
        }
    }
}