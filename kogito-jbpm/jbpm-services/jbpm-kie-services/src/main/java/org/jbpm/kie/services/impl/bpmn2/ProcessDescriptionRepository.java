package org.jbpm.kie.services.impl.bpmn2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

@Singleton
public class ProcessDescriptionRepository {

    private Map<String, ProcessDescRepoHelper> processRepoHelper = new ConcurrentHashMap<String, ProcessDescRepoHelper>();
    
    private Map<String, String> globalItemDefinitions = new ConcurrentHashMap<String, String>();
    
    public ProcessDescRepoHelper getProcessDesc(String processId) {
        return this.processRepoHelper.get(processId);
    }

    public Map<String, String> getGlobalItemDefinitions() {
        return globalItemDefinitions;
    }
    
    
    public void addProcessDescription(String processId, ProcessDescRepoHelper repoHelper) {
        this.processRepoHelper.put(processId, repoHelper);
    }
    
    public ProcessDescRepoHelper removeProcessDescription(String processId) {
        return this.processRepoHelper.remove(processId);
    }
    
}
