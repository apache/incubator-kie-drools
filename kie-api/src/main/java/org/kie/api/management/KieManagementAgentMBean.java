package org.kie.api.management;

/**
 * A tree root level class for all the drools management
 * MBeans published to an MBean agent
 */
public interface KieManagementAgentMBean {

    public long getKieBaseCount();

    public long getSessionCount();

}
