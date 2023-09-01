package org.kie.api.management;

/**
 * An MBean interface for {@link org.kie.api.runtime.StatelessKieSession} monitoring
 */
public interface StatelessKieSessionMonitoringMXBean extends GenericKieSessionMonitoringMXBean {

    long getTotalObjectsInserted();
    long getTotalObjectsDeleted();
}
