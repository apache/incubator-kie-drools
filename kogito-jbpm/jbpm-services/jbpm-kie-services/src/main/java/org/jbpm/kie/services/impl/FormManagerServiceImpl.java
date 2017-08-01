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
package org.jbpm.kie.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FormManagerServiceImpl implements FormManagerService {
    private Map<String, Map<String, String>> formsRegistry = new HashMap<String, Map<String, String>>();


    @Override
    public void registerForm(String deploymentId, String key, String formContent) {
        if(formsRegistry.get(deploymentId) == null){
            formsRegistry.put(deploymentId, new HashMap<String, String>());
        }
        formsRegistry.get(deploymentId).put(key, formContent);
    }

    @Override
    public void unRegisterForms( String deploymentId ) {
        formsRegistry.remove( deploymentId );
    }

    @Override
    public Map<String, String> getAllFormsByDeployment(String deploymentId) {
        return formsRegistry.get(deploymentId);
    }

    @Override
    public Set<String> getAllDeployments() {
        return formsRegistry.keySet();
    }

    @Override
    public Map<String, String> getAllForms() {
        Map<String, String> allForms = new HashMap<String, String>();
        for(Map<String, String> formsByDep : formsRegistry.values()){
            allForms.putAll(formsByDep);
        }
        return allForms;
    }

    @Override
    public String getFormByKey(String deploymentId, String key) {
        if(deploymentId != null && formsRegistry != null && formsRegistry.containsKey(deploymentId)){
            return formsRegistry.get(deploymentId).get(key);
        }
        return null;
    }
    
    
    
}
