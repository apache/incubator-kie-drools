/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.droolsjbpm.services.api;

import java.util.List;
import java.util.Map;
import org.kie.commons.java.nio.file.Path;

/**
 *
 * @author salaboy
 */
public interface Domain {
    
    void setName(String name);
    
    String getName();

    Map<String, String> getAssetsDefs();

    void setAssetsDefs(Map<String, String> assetsDefs);
    
    void addAsset(String name, String path);

  
    Map<String, List<Path>> getKsessionAssets();

    void setKsessionAssets(Map<String, List<Path>> ksessionAssets);
    
    void addKsessionAsset(String ksession, Path path);

    void addProcessToKsession(String ksessionName, String processId, String bpmn2Content);
    
    Map<String, String> getAllProcesses();
    
    Map<String, String> getProcessesBySession(String kSessionName);
    
    String getProcessDefinitionBPMN2(String ksessionName, String processId);
    
}
