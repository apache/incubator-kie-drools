package org.jbpm.kie.services.impl.bpmn2;

import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

@Singleton
public class ProcessDescriptionRepository {

    private ConcurrentHashMap<String, ProcessDescRepoHelper> processRepoHelper = new ConcurrentHashMap<String, ProcessDescRepoHelper>();
    
    public ProcessDescRepoHelper getProcessDesc(String processId) {
        return this.processRepoHelper.get(processId);
    }
    
    public void addProcessDescription(String processId, ProcessDescRepoHelper repoHelper) {
        this.processRepoHelper.put(processId, repoHelper);
    }
    
    public ProcessDescRepoHelper removeProcessDescription(String processId) {
        return this.processRepoHelper.remove(processId);
    }
    
}
