package org.drools.agent;

import org.drools.PropertiesConfiguration;

/**
 * <p>
 * drools.agent.scanResources = &lt;true|false&gt;<br/>
 * drools.agent.scanDirectories = &lt;true|false&gt;<br/>
 * drools.agent.newInstance = &lt;true|false&gt; // currently this is hard coded to true<br/>
 * drools.agent.monitorChangeSetEvents = &lt;true|false&gt;<br/>
 * </p>
 * @see org.drools.agent.KnowledgeAgent
 * @see org.drools.agent.KnowledgeAgentConfiguration 
 */
public interface KnowledgeAgentConfiguration
    extends
    PropertiesConfiguration {

    public boolean isScanResources();

    public boolean isScanDirectories();

    public boolean isMonitorChangeSetEvents();

    public boolean isNewInstance();

    public boolean isUseKBaseClassLoaderForCompiling();


}
