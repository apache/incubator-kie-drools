package org.drools.agent.impl;

import java.util.Properties;

import org.drools.PropertiesConfiguration;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.util.StringUtils;

/**
 * drools.agent.scanResources = <true|false>
 * drools.agent.newInstance = <true|false>
 *
 */
public class KnowledgeAgentConfigurationImpl
    implements
    KnowledgeAgentConfiguration {

    private boolean scanResources;
    private boolean newInstance;

    public KnowledgeAgentConfigurationImpl() {

    }

    public KnowledgeAgentConfigurationImpl(Properties properties) {
        for ( Object key : properties.keySet() ) {
            setProperty( (String) key,
                         (String) properties.get( key ) );
        }
    }

    public void setProperty(String name,
                            String value) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return;
        }

        if ( name.equals( "drools.agent.scanResources" ) ) {
            setScanResources(  StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value ) );
        } else if ( name.equals( "drools.agent.newInstance" ) ) {
            setNewInstance( StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value ) );
        }
    }

    public String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        }

        if ( name.equals( "drools.agent.scanResources " ) ) {
            return Boolean.toString( this.scanResources );
        } else if ( name.equals( "drools.agent.newInstance " ) ) {
            return Boolean.toString( this.newInstance );
        }
        
        return null;
    }

    public boolean isScanResources() {
        return scanResources;
    }

    public void setScanResources(boolean scanResources) {
        this.scanResources = scanResources;
    }

    public boolean isNewInstance() {
        return newInstance;
    }

    public void setNewInstance(boolean newInstance) {
        this.newInstance = newInstance;
    }
}
