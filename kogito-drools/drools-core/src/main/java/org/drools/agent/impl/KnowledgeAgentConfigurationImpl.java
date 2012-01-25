/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.agent.impl;

import java.util.Properties;

import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.conf.MonitorChangesetEventsOption;
import org.drools.agent.conf.NewInstanceOption;
import org.drools.agent.conf.ScanDirectoriesOption;
import org.drools.agent.conf.ScanResourcesOption;
import org.drools.agent.conf.UseKnowledgeBaseClassloaderOption;
import org.drools.agent.conf.ValidationTimeoutOption;
import org.drools.core.util.StringUtils;
import org.drools.util.ChainedProperties;
import org.drools.util.ClassLoaderUtil;
import org.drools.util.CompositeClassLoader;

/**
 * drools.agent.scanResources = <true|false>
 * drools.agent.scanDirectories = <true|false>
 * drools.agent.newInstance = <true|false>
 * drools.agent.monitorChangeSetEvents = <true|false>
 */
public class KnowledgeAgentConfigurationImpl
    implements
    KnowledgeAgentConfiguration {


    private static final KnowledgeAgentConfiguration defaultConf = new KnowledgeAgentConfigurationImpl();

    public static KnowledgeAgentConfiguration getDefaultInstance() {
        return defaultConf;
    }


    private ChainedProperties       chainedProperties;
    private CompositeClassLoader    classLoader;
    private boolean                 immutable              = false;
    private boolean                 classLoaderCache       = true;


    private boolean scanResources          = true;
    private boolean scanDirectories        = true;
    private boolean monitorChangeSetEvents = true;
    private boolean newInstance            = true;
    private boolean useKBaseClassLoaderForCompiling = false;
    private int     validationTimeout      = 0;



    public KnowledgeAgentConfigurationImpl(Properties properties) {
        init( properties,
              null );
    }

    public KnowledgeAgentConfigurationImpl() {
        init( null,
              null );
    }

    /**
     * A constructor that sets the parent classloader to be used
     * while dealing with this knowledge agent
     *
     * @param classLoaders
     */
    public KnowledgeAgentConfigurationImpl(ClassLoader... classLoaders) {
        init( null,
              classLoaders );
    }

    /**
     * A constructor that sets the classloader to be used as the parent classloader
     * of this knowledge agent classloaders, and the properties to be used
     * as base configuration options
     *
     * @param classLoaders
     * @param properties
     */
    public KnowledgeAgentConfigurationImpl(Properties properties,
                                           ClassLoader... classLoaders) {
        init( properties,
              classLoaders );
    }



    private void init(Properties properties,
                      ClassLoader... classLoaders) {
        this.immutable = false;

        setClassLoader( classLoaders );

        this.chainedProperties = new ChainedProperties( "knowledgeagent.conf",
                                                        this.classLoader,
                                                        true );

        if ( properties != null ) {
            this.chainedProperties.addProperties( properties );
        }

        setProperty( MonitorChangesetEventsOption.PROPERTY_NAME,
                     this.chainedProperties.getProperty( MonitorChangesetEventsOption.PROPERTY_NAME,
                                                         "true" ) );

        setProperty( ScanDirectoriesOption.PROPERTY_NAME,
                     this.chainedProperties.getProperty( ScanDirectoriesOption.PROPERTY_NAME,
                                                         "true" ) );

        setProperty( ScanResourcesOption.PROPERTY_NAME,
                     this.chainedProperties.getProperty( ScanResourcesOption.PROPERTY_NAME,
                                                         "true" ) );

        setProperty( NewInstanceOption.PROPERTY_NAME,
                     this.chainedProperties.getProperty( NewInstanceOption.PROPERTY_NAME,
                                                         "true" ) );

        setProperty( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME,
                     this.chainedProperties.getProperty( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME,
                                                         "false" ) );

        setProperty( ValidationTimeoutOption.PROPERTY_NAME,
                     this.chainedProperties.getProperty( ValidationTimeoutOption.PROPERTY_NAME,
                                                         "0" ) );

    }



    public void setProperty(String name,
                            String value) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return;
        }

        if ( name.equals( MonitorChangesetEventsOption.PROPERTY_NAME ) ) {
            setMonitorChangeSetEvents( StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value ) );
        } else if ( name.equals( ScanDirectoriesOption.PROPERTY_NAME ) ) {
            boolean bool = StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value );
            setScanDirectories( bool );
            if ( bool ) {
                setScanResources( true );
                setMonitorChangeSetEvents( true );
            }
        } else if ( name.equals( ScanResourcesOption.PROPERTY_NAME ) ) {
            boolean bool = StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value );
            setScanResources( bool );
            if ( bool ) {
                setMonitorChangeSetEvents( true );
            }
        } else if ( name.equals( NewInstanceOption.PROPERTY_NAME ) ) {
            setNewInstance( StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value ) );
        } else if ( name.equals( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME ) ) {
            setUseKBaseClassLoaderForCompiling( StringUtils.isEmpty( value ) ? false : Boolean.parseBoolean( value ) );
        } else if ( name.equals( ValidationTimeoutOption.PROPERTY_NAME ) ) {
            try {
                int millisec =  StringUtils.isEmpty( value ) ? 0 : Integer.parseInt( value );
                setValidationTimeout( millisec );
            } catch ( NumberFormatException nfe ) {
                setValidationTimeout( 0 );
            }
        }
    }

    public String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        }

        if ( name.equals( ScanResourcesOption.PROPERTY_NAME ) ) {
            return Boolean.toString( this.scanResources );
        } else if ( name.equals( ScanDirectoriesOption.PROPERTY_NAME ) ) {
            return Boolean.toString( this.scanDirectories );
        } else if ( name.equals( MonitorChangesetEventsOption.PROPERTY_NAME ) ) {
            return Boolean.toString( this.monitorChangeSetEvents );
        } else if ( name.equals( NewInstanceOption.PROPERTY_NAME ) ) {
            return Boolean.toString( this.newInstance );
        } else if ( name.equals( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME ) ) {
            return Boolean.toString( this.useKBaseClassLoaderForCompiling );
        } else if ( name.equals( ValidationTimeoutOption.PROPERTY_NAME ) ) {
            return Integer.toString( this.validationTimeout );
        }

        return null;
    }


    /**
     * Makes the configuration object immutable. Once it becomes immutable,
     * there is no way to make it mutable again.
     * This is done to keep consistency.
     */
    public void makeImmutable() {
        this.immutable = true;
    }

    /**
     * Returns true if this configuration object is immutable or false otherwise.
     * @return
     */
    public boolean isImmutable() {
        return this.immutable;
    }

    private void checkCanChange() {
        if ( this.immutable ) {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }


    public CompositeClassLoader getClassLoader() {
        return this.classLoader.clone();
    }

    public void setClassLoader(ClassLoader... classLoaders) {
        this.classLoader = ClassLoaderUtil.getClassLoader(classLoaders,
                getClass(),
                isClassLoaderCacheEnabled());
    }

    public boolean isClassLoaderCacheEnabled() {
        return classLoaderCache;
    }

    public void setClassLoaderCacheEnabled(boolean classLoaderCacheEnabled) {
        this.classLoaderCache = classLoaderCacheEnabled;
        this.classLoader.setCachingEnabled( this.classLoaderCache );
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

    public boolean isUseKBaseClassLoaderForCompiling() {
        return this.useKBaseClassLoaderForCompiling;
    }

    public void setUseKBaseClassLoaderForCompiling(boolean useKBaseClassLoaderForCompiling) {
        this.useKBaseClassLoaderForCompiling = useKBaseClassLoaderForCompiling;
    }

    public int getValidationTimeout() {
        return validationTimeout;
    }

    public void setValidationTimeout(int validationTimeout) {
        this.validationTimeout = validationTimeout;
    }

}
