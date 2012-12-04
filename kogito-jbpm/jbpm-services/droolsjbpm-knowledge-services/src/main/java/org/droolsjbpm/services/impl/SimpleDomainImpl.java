/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.droolsjbpm.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.droolsjbpm.services.api.Domain;
import org.kie.commons.java.nio.file.Path;

/**
 *
 * @author salaboy
 */
public class SimpleDomainImpl implements Domain{
    private String name;
     // Asset Name / Assets Definition Path 
    private Map<String, String> assetsDefs = new HashMap<String, String>();
    
    // Ksession Name / List of assets Paths
    private Map<String, List<Path>> ksessionAssets = new HashMap<String, List<Path>>();
    
  
    
    public SimpleDomainImpl() {
    }

    public SimpleDomainImpl(String name) {
        this.name = name;
        
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public Map<String, String> getAssetsDefs() {
        return assetsDefs;
    }

    public void setAssetsDefs(Map<String, String> assetsDefs) {
        this.assetsDefs = assetsDefs;
    }
    
    public void addAsset(String name, String path){
        this.assetsDefs.put(name, path);
    }

    
    public Map<String, List<Path>> getKsessionAssets() {
        return ksessionAssets;
    }

    public void setKsessionAssets(Map<String, List<Path>> ksessionAssets) {
        this.ksessionAssets = ksessionAssets;
    }
    
    public void addKsessionAsset(String ksession, Path path){
        if(this.ksessionAssets.get(ksession) == null){
            this.ksessionAssets.put(ksession, new ArrayList<Path>());
        }
        this.ksessionAssets.get(ksession).add(path);
    }

  
    
    
    
 
}
