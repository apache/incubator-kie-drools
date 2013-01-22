package org.jbpm.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.persistence.map.MapBasedPersistenceContext;
import org.jbpm.persistence.correlation.CorrelationKeyInfo;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.kie.process.CorrelationKey;
import org.kie.runtime.process.ProcessInstance;

public class MapBasedProcessPersistenceContext extends MapBasedPersistenceContext
    implements
    ProcessPersistenceContext,
    NonTransactionalProcessPersistentSession{
    
    private ProcessStorage storage;
    private Map<Long, ProcessInstanceInfo> processes;
    private Map<CorrelationKeyInfo, ProcessInstanceInfo> processInstancesByBusinessKey;

    public MapBasedProcessPersistenceContext(ProcessStorage storage) {
        super( storage );
        this.storage = storage;
        this.processes = new HashMap<Long, ProcessInstanceInfo>();
        this.processInstancesByBusinessKey = new HashMap<CorrelationKeyInfo, ProcessInstanceInfo>();
    }

    public void persist(ProcessInstanceInfo processInstanceInfo) {
        if( processInstanceInfo.getId() == null ) {
            processInstanceInfo.setId( storage.getNextProcessInstanceId() );
        }
        processes.put( processInstanceInfo.getId(), processInstanceInfo );
    }

    public ProcessInstanceInfo findProcessInstanceInfo(Long processId) {
        ProcessInstanceInfo processInstanceInfo = processes.get( processId );
        if( processInstanceInfo == null){
            processInstanceInfo = storage.findProcessInstanceInfo( processId );
        }
        return processInstanceInfo;
    }

    public List<ProcessInstanceInfo> getStoredProcessInstances() {
        return Collections.unmodifiableList( new ArrayList<ProcessInstanceInfo>(processes.values()));
    }

    @Override
    public void close() {
        super.close();
        clearStoredProcessInstances();
    }

    public void remove(ProcessInstanceInfo processInstanceInfo) {
        storage.removeProcessInstanceInfo( processInstanceInfo.getId() );
        
    }

    public List<Long> getProcessInstancesWaitingForEvent(String type) {
        return storage.getProcessInstancesWaitingForEvent( type );
    }

    public void clearStoredProcessInstances() {
        processes.clear();
    }

    @Override
    public void persist(CorrelationKeyInfo correlationKeyInfo) {
        ProcessInstanceInfo piInfo = this.processes.get(correlationKeyInfo.getProcessInstanceId());
        if (piInfo != null) {
            this.processInstancesByBusinessKey.put(correlationKeyInfo, piInfo);
        }
    }

    @Override
    public Long getProcessInstanceByCorrelationKey(CorrelationKey correlationKey) {
        ProcessInstanceInfo piInfo = this.processInstancesByBusinessKey.get(correlationKey);
        return piInfo.getId();
    }
}
