package org.drools.core.management;

import javax.management.ObjectName;

import org.drools.core.common.InternalWorkingMemoryActions;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.management.KieSessionMonitoringMXBean;

public class KieSessionMonitoringImpl extends GenericKieSessionMonitoringImpl implements KieSessionMonitoringMXBean {
    
    private ObjectName name;

    public KieSessionMonitoringImpl(String containerId, String kbaseId, String ksessionName) {
        super(containerId, kbaseId, ksessionName);
        
        this.name = DroolsManagementAgent.createObjectNameBy(containerId, kbaseId, KieSessionType.STATEFUL, ksessionName);
    }
    
    @Override
    public ObjectName getName() {
        return this.name;
    }

    @Override
    public long getTotalSessions() {
        return ksessions.size();
    }

    @Override
    public long getTotalFactCount() {
        long result = 0;
        for (KieRuntimeEventManager s : ksessions) {
            result += ((InternalWorkingMemoryActions) s).getFactCount();
        }
        return result;
    }
}
