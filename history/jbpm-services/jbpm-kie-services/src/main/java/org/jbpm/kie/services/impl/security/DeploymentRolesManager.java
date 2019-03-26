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

package org.jbpm.kie.services.impl.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kie.internal.identity.IdentityProvider;


public class DeploymentRolesManager {
    
    private static final int MAX_CACHE_ENTRIES = Integer.parseInt(System.getProperty("org.jbpm.service.cache.size", "100"));

    protected Map<String, List<String>> deploymentsRoles = new HashMap<String, List<String>>();
    
    protected Map<String, List<String>> userDeploymentIdsCache = new LinkedHashMap<String, List<String>>() {
        private static final long serialVersionUID = -2324394641773215253L;
        
        protected boolean removeEldestEntry(Map.Entry<String, List<String>> eldest) {
            return size() > MAX_CACHE_ENTRIES;
        }
    };
    
    public void addRolesForDeployment(String deploymentId, List<String> roles) {
        deploymentsRoles.put(deploymentId, roles);
        userDeploymentIdsCache.clear();
    }
    
    public void removeRolesForDeployment(String deploymentId) {
        deploymentsRoles.remove(deploymentId);
        userDeploymentIdsCache.clear();
    }
    
    public List<String> getDeploymentsForUser(IdentityProvider identityProvider) {
        String identityName = null;
        List<String> roles = null;
        try {
            identityName = identityProvider.getName();
            roles = identityProvider.getRoles();
        } catch (Exception e) {
            // in case there is no way to collect either name of roles of the requesting used return empty list
            return new ArrayList<String>();
        }
        List<String> usersDeploymentIds = userDeploymentIdsCache.get(identityName);
        if (usersDeploymentIds != null) {
            return usersDeploymentIds;
        }
        
        usersDeploymentIds = new ArrayList<String>();
        userDeploymentIdsCache.put(identityName, usersDeploymentIds);
        boolean isSecured = false;
        for (Map.Entry<String, List<String>> entry : deploymentsRoles.entrySet()){
            if (entry.getValue().isEmpty() || CollectionUtils.containsAny(roles, entry.getValue())) {
                usersDeploymentIds.add(entry.getKey());
            }
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                isSecured = true;
            }
        }
        
        if (isSecured && usersDeploymentIds.isEmpty()) {
            usersDeploymentIds.add("deployments-are-secured");
        }
        
        return usersDeploymentIds;
    }
}
