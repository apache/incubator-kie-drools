package org.drools.agent.impl;

import java.util.Properties;

import org.drools.PropertiesConfiguration;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.core.util.StringUtils;

/**
 * drools.agent.scanResources = <true|false>
 * drools.agent.scanDirectories = <true|false>
 * drools.agent.newInstance = <true|false>
 * drools.agent.monitorChangeSetEvents = <true|false>
 *
 */
public class KnowledgeAgentConfigurationImpl
    implements
    KnowledgeAgentConfiguration {

    private boolean scanResources          = true;
    private boolean scanDirectories        = true;
    private boolean monitorChangeSetEvents = true;
    private boolean newInstance            = true;

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

        if ( name.equals( "drools.agent.monitorChangeSetEvents" ) ) {
            setMonitorChangeSetEvents( StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value ) );
        } else if ( name.equals( "drools.agent.scanDirectories" ) ) {
            boolean bool = StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value );
            setScanDirectories( bool );
            if ( bool ) {
                setScanResources( true );
                setMonitorChangeSetEvents( true );
            }
        } else if ( name.equals( "drools.agent.scanResources" ) ) {
            boolean bool = StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value );
            setScanResources( bool );
            if ( bool ) {
                setMonitorChangeSetEvents( true );
            }
        } else if ( name.equals( "drools.agent.newInstance" ) ) {
            setNewInstance( StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value ) );
        }
    }

    public String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        }

        if ( name.equals( "drools.agent.scanResources" ) ) {
            return Boolean.toString( this.scanResources );
        } else if ( name.equals( "drools.agent.scanDirectories" ) ) {
            return Boolean.toString( this.scanDirectories );
        } else if ( name.equals( "drools.agent.monitorChangeSetEvents" ) ) {
            return Boolean.toString( this.monitorChangeSetEvents );
        } else if ( name.equals( "drools.agent.newInstance" ) ) {
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

    public boolean isScanDirectories() {
        return scanDirectories;
    }

    public void setScanDirectories(boolean scanDirectories) {
        this.scanDirectories = scanDirectories;
    }

    public boolean isMonitorChangeSetEvents() {
        return monitorChangeSetEvents;
    }

    public void setMonitorChangeSetEvents(boolean monitorChangeSetEvents) {
        this.monitorChangeSetEvents = monitorChangeSetEvents;
    }

    public boolean isNewInstance() {
        return newInstance;
    }

    public void setNewInstance(boolean newInstance) {
        this.newInstance = newInstance;
    }
}
