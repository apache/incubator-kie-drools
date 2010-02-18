/*
 * Copyright 2005 JBoss Inc
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

package org.drools;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.command.CommandService;
import org.drools.core.util.ConfFileUtils;
import org.drools.core.util.StringUtils;
import org.drools.process.instance.ProcessInstanceManagerFactory;
import org.drools.process.instance.WorkItemManagerFactory;
import org.drools.process.instance.event.SignalManagerFactory;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.conf.KnowledgeSessionOption;
import org.drools.runtime.conf.MultiValueKnowledgeSessionOption;
import org.drools.runtime.conf.SingleValueKnowledgeSessionOption;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.util.ChainedProperties;
import org.drools.util.ClassLoaderUtil;
import org.mvel2.MVEL;

/**
 * SessionConfiguration
 *
 * A class to store Session related configuration. It must be used at session instantiation time
 * or not used at all.
 * This class will automatically load default values from system properties, so if you want to set
 * a default configuration value for all your new sessions, you can simply set the property as
 * a System property.
 *
 * After the Session is created, it makes the configuration immutable and there is no way to make it
 * mutable again. This is to avoid inconsistent behavior inside session.
 *
 * NOTE: This API is under review and may change in the future.
 * 
 * 
 * drools.keepReference = <true|false>
 * drools.clockType = <pseudo|realtime|heartbeat|implicit>
 */
public class SessionConfiguration
    implements
    KnowledgeSessionConfiguration,
    Externalizable {
    private static final long serialVersionUID = 500L;

    private ChainedProperties chainedProperties;

    private volatile boolean  immutable;

    private boolean           keepReference;

    private ClockType         clockType;

    private Map<String, WorkItemHandler>   workItemHandlers;
    private ProcessInstanceManagerFactory  processInstanceManagerFactory;
    private SignalManagerFactory           processSignalManagerFactory;
    private WorkItemManagerFactory         workItemManagerFactory;
    private CommandService                 commandService; 

    private transient ClassLoader          classLoader;
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( chainedProperties );
        out.writeBoolean( immutable );
        out.writeBoolean( keepReference );
        out.writeObject( clockType );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        chainedProperties = (ChainedProperties) in.readObject();
        immutable = in.readBoolean();
        keepReference = in.readBoolean();
        clockType = (ClockType) in.readObject();
    }

    /**
     * Creates a new session configuration using the provided properties
     * as configuration options. 
     *
     * @param properties
     */
    public SessionConfiguration(Properties properties) {
        init( null, properties );
    }

    /**
     * Creates a new session configuration with default configuration options.
     */
    public SessionConfiguration() {
        init( null, null );
    }
    
    public SessionConfiguration(ClassLoader classLoader) {
    	init ( classLoader, null );
    }

    private void init(ClassLoader classLoader, Properties properties) {
    	this.classLoader =  ClassLoaderUtil.getClassLoader( classLoader, getClass() );
    	
        this.immutable = false;
        this.chainedProperties = new ChainedProperties( "session.conf",  this.classLoader );

        if ( properties != null ) {
            this.chainedProperties.addProperties( properties );
        }

        setKeepReference( Boolean.valueOf( this.chainedProperties.getProperty( "drools.keepReference",
                                                                               "true" ) ).booleanValue() );

        setClockType( ClockType.resolveClockType( this.chainedProperties.getProperty( "drools.clockType",
                                                                                      "realtime" ) ) );
    }
    
    public void addProperties(Properties properties) {
        if ( properties != null ) {
            this.chainedProperties.addProperties( properties );
        }        
    }
    
    public void setProperty(String name,
                            String value) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return;
        }
        
        if ( name.equals( "drools.keepReference" ) ) {
            setKeepReference(  StringUtils.isEmpty( value ) ? true : Boolean.parseBoolean( value ) );
        } else if ( name.equals( "drools.clockType" ) ) {
            setClockType( ClockType.resolveClockType(  StringUtils.isEmpty( value ) ? "realtime" : value ) );
        }
    }   
    
    public String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        }
        
        if ( name.equals( "drools.keepReference" ) ) {
            return Boolean.toString( this.keepReference );
        } else if ( name.equals( "drools.clockType" ) ) {
            return this.clockType.toExternalForm();
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

    public void setKeepReference(boolean keepReference) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.keepReference = keepReference;
    }

    public boolean isKeepReference() {
        return this.keepReference;
    }

    public ClockType getClockType() {
        return clockType;
    }

    public void setClockType(ClockType clockType) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.clockType = clockType;
    }

    public Map<String, WorkItemHandler> getWorkItemHandlers() {
        if ( this.workItemHandlers == null ) {
            initWorkItemHandlers();
        }
        return this.workItemHandlers;

    }

    private void initWorkItemHandlers() {
        this.workItemHandlers = new HashMap<String, WorkItemHandler>();

        // split on each space
        String locations[] = this.chainedProperties.getProperty( "drools.workItemHandlers",
                                                                 "" ).split( "\\s" );

        // load each SemanticModule
        for ( String factoryLocation : locations ) {
            // trim leading/trailing spaces and quotes
            factoryLocation = factoryLocation.trim();
            if ( factoryLocation.startsWith( "\"" ) ) {
                factoryLocation = factoryLocation.substring( 1 );
            }
            if ( factoryLocation.endsWith( "\"" ) ) {
                factoryLocation = factoryLocation.substring( 0,
                                                             factoryLocation.length() - 1 );
            }
            if ( !factoryLocation.equals( "" ) ) {
                loadWorkItemHandlers( factoryLocation );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadWorkItemHandlers(String location) {
        String content = ConfFileUtils.URLContentsToString( ConfFileUtils.getURL( location,
                                                                                  null,
                                                                                  RuleBaseConfiguration.class ) );
        Map<String, WorkItemHandler> workItemHandlers = (Map<String, WorkItemHandler>) MVEL.eval( content,
                                                                                                  new HashMap() );
        this.workItemHandlers.putAll( workItemHandlers );
    }

    public ProcessInstanceManagerFactory getProcessInstanceManagerFactory() {
        if ( this.processInstanceManagerFactory == null ) {
            initProcessInstanceManagerFactory();
        }
        return this.processInstanceManagerFactory;
    }

    @SuppressWarnings("unchecked")
    private void initProcessInstanceManagerFactory() {
        String className = this.chainedProperties.getProperty( "drools.processInstanceManagerFactory",
                                                               "org.drools.process.instance.impl.DefaultProcessInstanceManagerFactory" );
        Class<ProcessInstanceManagerFactory> clazz = null;
        try {
            clazz = (Class<ProcessInstanceManagerFactory>) Thread.currentThread().getContextClassLoader().loadClass( className );
        } catch ( ClassNotFoundException e ) {
        }

        if ( clazz == null ) {
            try {
                clazz = (Class<ProcessInstanceManagerFactory>) SessionConfiguration.class.getClassLoader().loadClass( className );
            } catch ( ClassNotFoundException e ) {
            }
        }

        if ( clazz != null ) {
            try {
                this.processInstanceManagerFactory = clazz.newInstance();
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Unable to instantiate process instance manager factory '" + className + "'" );
            }
        } else {
            throw new IllegalArgumentException( "Process instance manager factory '" + className + "' not found" );
        }
    }

    public SignalManagerFactory getSignalManagerFactory() {
        if ( this.processSignalManagerFactory == null ) {
            initSignalManagerFactory();
        }
        return this.processSignalManagerFactory;
    }

    @SuppressWarnings("unchecked")
    private void initSignalManagerFactory() {
        String className = this.chainedProperties.getProperty( "drools.processSignalManagerFactory",
                                                               "org.drools.process.instance.event.DefaultSignalManagerFactory" );
        Class<SignalManagerFactory> clazz = null;
        try {
            clazz = (Class<SignalManagerFactory>) Thread.currentThread().getContextClassLoader().loadClass( className );
        } catch ( ClassNotFoundException e ) {
        }

        if ( clazz == null ) {
            try {
                clazz = (Class<SignalManagerFactory>) SessionConfiguration.class.getClassLoader().loadClass( className );
            } catch ( ClassNotFoundException e ) {
            }
        }

        if ( clazz != null ) {
            try {
                this.processSignalManagerFactory = clazz.newInstance();
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Unable to instantiate signal manager factory '" + className + "'" );
            }
        } else {
            throw new IllegalArgumentException( "Signal manager factory '" + className + "' not found" );
        }
    }

    public WorkItemManagerFactory getWorkItemManagerFactory() {
        if ( this.workItemManagerFactory == null ) {
            initWorkItemManagerFactory();
        }
        return this.workItemManagerFactory;
    }

    @SuppressWarnings("unchecked")
    private void initWorkItemManagerFactory() {
        String className = this.chainedProperties.getProperty( "drools.workItemManagerFactory",
                                                               "org.drools.process.instance.impl.DefaultWorkItemManagerFactory" );
        Class<WorkItemManagerFactory> clazz = null;
        try {
            clazz = (Class<WorkItemManagerFactory>) Thread.currentThread().getContextClassLoader().loadClass( className );
        } catch ( ClassNotFoundException e ) {
        }

        if ( clazz == null ) {
            try {
                clazz = (Class<WorkItemManagerFactory>) SessionConfiguration.class.getClassLoader().loadClass( className );
            } catch ( ClassNotFoundException e ) {
            }
        }

        if ( clazz != null ) {
            try {
                this.workItemManagerFactory = clazz.newInstance();
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Unable to instantiate work item manager factory '" + className + "'",
                                                    e );
            }
        } else {
            throw new IllegalArgumentException( "Work item manager factory '" + className + "' not found" );
        }
    }
    
    public CommandService getCommandService(KnowledgeBase kbase, Environment environment) {
        if ( this.commandService == null ) {
            initCommandService(kbase, environment);
        }
        return this.commandService;
    }

    @SuppressWarnings("unchecked")
    private void initCommandService(KnowledgeBase kbase, Environment environment) {
        String className = this.chainedProperties.getProperty( "drools.commandService", null );
        if (className == null) {
        	return;
        }
        
        Class<CommandService> clazz = null;
        try {
            clazz = (Class<CommandService>) Thread.currentThread().getContextClassLoader().loadClass( className );
        } catch ( ClassNotFoundException e ) {
        }

        if ( clazz == null ) {
            try {
                clazz = (Class<CommandService>) SessionConfiguration.class.getClassLoader().loadClass( className );
            } catch ( ClassNotFoundException e ) {
            }
        }

        if ( clazz != null ) {
            try {
                this.commandService = clazz.getConstructor(KnowledgeBase.class, KnowledgeSessionConfiguration.class, Environment.class).newInstance(kbase, this, environment);
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Unable to instantiate command service '" + className + "'",
                                                    e );
            }
        } else {
            throw new IllegalArgumentException( "Command service '" + className + "' not found" );
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleValueKnowledgeSessionOption> T getOption(Class<T> option) {
        if ( ClockTypeOption.class.equals( option ) ) {
            return (T) ClockTypeOption.get( getClockType().toExternalForm() );
        }
        return null;
    }

    public <T extends MultiValueKnowledgeSessionOption> T getOption(Class<T> option,
                                                                    String key) {
        return null;
    }

    public <T extends KnowledgeSessionOption> void setOption(T option) {
        if ( option instanceof ClockTypeOption ) {
            setClockType( ClockType.resolveClockType( ((ClockTypeOption) option ).getClockType() ) );
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
