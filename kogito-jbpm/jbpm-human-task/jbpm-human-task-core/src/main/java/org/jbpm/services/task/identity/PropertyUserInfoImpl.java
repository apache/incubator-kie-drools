/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.services.task.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;

public class PropertyUserInfoImpl extends AbstractUserGroupInfo implements UserInfo {
    
    protected Map<String, Map<String, Object>> registry = new HashMap<String, Map<String,Object>>();

    //no no-arg constructor to prevent cdi from auto deploy
    public PropertyUserInfoImpl(boolean activate) {
        try {
        	String propertiesLocation = System.getProperty("jbpm.user.info.properties");
	        Properties registryProps = readProperties(propertiesLocation, "classpath:/userinfo.properties");
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
    public PropertyUserInfoImpl(Properties registryProps) {
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
                    	User user = TaskModelProvider.getFactory().newUser();
                        ((InternalOrganizationalEntity) user).setId(member);
                        membersList.add(user);
                    }
                    entityInfo.put("members", membersList);
                }
                registry.put(propertyKey, entityInfo);
                
            }
        }
    }
}