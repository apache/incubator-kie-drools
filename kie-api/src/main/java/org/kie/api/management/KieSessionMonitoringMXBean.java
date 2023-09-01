package org.kie.api.management;

/**
 * An MBean interface for {@link org.kie.api.runtime.KieSession} monitoring
 */
public interface KieSessionMonitoringMXBean extends GenericKieSessionMonitoringMXBean {
    /**        
     * @return the total fact count current loaded into the session      
     */       
    long getTotalFactCount();
}