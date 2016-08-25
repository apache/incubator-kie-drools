package org.drools.core.management;

import org.drools.core.common.InternalWorkingMemoryActions;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.management.KieSessionMonitoringMBean;

public class KieSessionMonitoringImpl extends GenericKieSessionMonitoringImpl implements KieSessionMonitoringMBean {
    
    public KieSessionMonitoringImpl(String containerId, String kbaseId, String ksessionName) {
        super(containerId, kbaseId, ksessionName);
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
