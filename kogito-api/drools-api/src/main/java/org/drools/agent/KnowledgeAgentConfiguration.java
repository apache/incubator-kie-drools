package org.drools.agent;

import org.drools.PropertiesConfiguration;

/**
 * drools.agent.scanResources = <true|false>
 * drools.agent.scanDirectories = <true|false>
 * drools.agent.newInstance = <true|false> // currently this is hard coded to true
 * drools.agent.monitorChangeSetEvents = <true|false>
 *
 * @see org.drools.agent.KnowledgeAgent
 * @see org.drools.agent.KnowledgeAgentConfiguration 
 */
public interface KnowledgeAgentConfiguration extends PropertiesConfiguration {

}
