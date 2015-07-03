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

package org.drools.core;

import org.drools.core.command.CommandService;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.util.ConfFileUtils;
import org.drools.core.util.MVELSafeHelper;
import org.drools.core.util.StringUtils;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.KeepReferenceOption;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.QueryListenerOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.api.runtime.conf.TimedRuleExectionOption;
import org.kie.api.runtime.conf.TimedRuleExecutionFilter;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.conf.WorkItemHandlerOption;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.conf.ForceEagerActivationFilter;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.kie.internal.utils.ChainedProperties;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
    KieSessionConfiguration,
    Externalizable {
    private static final long              serialVersionUID = 510l;

    private ChainedProperties              chainedProperties;

    private volatile boolean               immutable;

    private boolean                        keepReference;

    private ForceEagerActivationFilter     forceEagerActivationFilter;
    private TimedRuleExecutionFilter       timedRuleExecutionFilter;

    private ClockType                      clockType;
    
    private BeliefSystemType               beliefSystemType;

    private QueryListenerOption            queryListener;

    private Map<String, WorkItemHandler>   workItemHandlers;
    private WorkItemManagerFactory         workItemManagerFactory;
    private CommandService                 commandService;

    private transient ClassLoader          classLoader;
    
    private TimerJobFactoryType              timerJobFactoryType;

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( chainedProperties );
        out.writeBoolean(immutable);
        out.writeBoolean( keepReference );
        out.writeObject(clockType);
        out.writeObject( queryListener );
        out.writeObject( timerJobFactoryType );
    }

    private static class DefaultSessionConfiguration {
        private static final SessionConfiguration defaultInstance = new SessionConfiguration();
    }
    
    public static SessionConfiguration getDefaultInstance() {
        return DefaultSessionConfiguration.defaultInstance;
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        chainedProperties = (ChainedProperties) in.readObject();
        immutable = in.readBoolean();
        keepReference = in.readBoolean();
        clockType = (ClockType) in.readObject();
        queryListener = (QueryListenerOption) in.readObject();
        timerJobFactoryType = (TimerJobFactoryType) in.readObject();
    }

    /**
     * Creates a new session configuration with default configuration options.
     */
    public SessionConfiguration() {
        init(null, null);
    }

    /**
     * Creates a new session configuration using the provided properties
     * as configuration options. 
     */
    public SessionConfiguration(Properties properties) {
        init( properties, null );
    }

    public SessionConfiguration(ClassLoader classLoader) {
        init(null, classLoader);
    }

    public SessionConfiguration(Properties properties, ClassLoader classLoader) {
        init( properties, classLoader );
    }

    private void init(Properties properties, ClassLoader classLoader) {
        this.classLoader = ProjectClassLoader.getClassLoader(classLoader == null ? null : classLoader, getClass(), false);

        this.immutable = false;
        this.chainedProperties = new ChainedProperties( "session.conf",
                                                        this.classLoader );

        if ( properties != null ) {
            this.chainedProperties.addProperties( properties );
        }

        setKeepReference(Boolean.valueOf(this.chainedProperties.getProperty(KeepReferenceOption.PROPERTY_NAME, "true")));

        setForceEagerActivationFilter(ForceEagerActivationOption.resolve(this.chainedProperties.getProperty(ForceEagerActivationOption.PROPERTY_NAME,
                                                                                                            "false")).getFilter());

        setTimedRuleExecutionFilter(TimedRuleExectionOption.resolve(this.chainedProperties.getProperty(TimedRuleExectionOption.PROPERTY_NAME,
                                                                                                       "false")).getFilter());

        setBeliefSystemType( BeliefSystemType.resolveBeliefSystemType( this.chainedProperties.getProperty( BeliefSystemTypeOption.PROPERTY_NAME,
                                                                                                           BeliefSystemType.SIMPLE.getId())) );

        setClockType( ClockType.resolveClockType( this.chainedProperties.getProperty( ClockTypeOption.PROPERTY_NAME,
                                                                                      ClockType.REALTIME_CLOCK.getId() ) ) );

        setQueryListenerClass( this.chainedProperties.getProperty( QueryListenerOption.PROPERTY_NAME,
                                                                   QueryListenerOption.STANDARD.getAsString() ) );

        setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType(this.chainedProperties.getProperty(TimerJobFactoryOption.PROPERTY_NAME,
                                                                                                                 TimerJobFactoryType.TRACKABLE.getId())));
    }

    public void addDefaultProperties(Properties properties) {
        Properties defaultProperties = new Properties();
        for ( Map.Entry<Object, Object> prop : properties.entrySet() ) {
            if ( chainedProperties.getProperty( (String) prop.getKey(), null) == null ) {
                defaultProperties.put( prop.getKey(), prop.getValue() );
            }
        }

        this.chainedProperties.addProperties(defaultProperties);
    }

    public void setProperty(String name,
                            String value) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return;
        }

        if ( name.equals( KeepReferenceOption.PROPERTY_NAME ) ) {
            setKeepReference(StringUtils.isEmpty(value) || Boolean.parseBoolean(value));
        } else if ( name.equals( ForceEagerActivationOption.PROPERTY_NAME ) ) {
            setForceEagerActivationFilter(ForceEagerActivationOption.resolve(StringUtils.isEmpty(value) ? "false" : value).getFilter());
        } else if ( name.equals( TimedRuleExectionOption.PROPERTY_NAME ) ) {
            setTimedRuleExecutionFilter(TimedRuleExectionOption.resolve(StringUtils.isEmpty(value) ? "false" : value).getFilter());
        } else if ( name.equals( ClockTypeOption.PROPERTY_NAME ) ) {
            setClockType(ClockType.resolveClockType(StringUtils.isEmpty(value) ? "realtime" : value));
        } else if ( name.equals( TimerJobFactoryOption.PROPERTY_NAME ) ) {
            setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType(StringUtils.isEmpty(value) ? "default" : value));
        } else if ( name.equals( QueryListenerOption.PROPERTY_NAME ) ) {
            setQueryListenerClass(StringUtils.isEmpty(value) ? QueryListenerOption.STANDARD.getAsString() : value);
        } else if ( name.equals( BeliefSystemTypeOption.PROPERTY_NAME ) ) {
            setBeliefSystemType(StringUtils.isEmpty(value) ? BeliefSystemType.SIMPLE : BeliefSystemType.resolveBeliefSystemType(value));
        }
    }

    public String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        }

        if ( name.equals( KeepReferenceOption.PROPERTY_NAME ) ) {
            return Boolean.toString(this.keepReference);
        } else if ( name.equals( ClockTypeOption.PROPERTY_NAME ) ) {
            return this.clockType.toExternalForm();
        } else if ( name.equals( TimerJobFactoryOption.PROPERTY_NAME ) ) {
            return this.timerJobFactoryType.toExternalForm();
        } else if ( name.equals( QueryListenerOption.PROPERTY_NAME ) ) {
            return this.queryListener.getAsString();
        } else if ( name.equals( BeliefSystemTypeOption.PROPERTY_NAME ) ) {
            return this.getBeliefSystemType().getId();
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

    public void setForceEagerActivationFilter(ForceEagerActivationFilter forceEagerActivationFilter) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.forceEagerActivationFilter = forceEagerActivationFilter;
    }

    public ForceEagerActivationFilter getForceEagerActivationFilter() {
        return this.forceEagerActivationFilter;
    }

    public boolean hasForceEagerActivationFilter() {
        try {
            return forceEagerActivationFilter.accept(null);
        } catch (Exception e) {
            return true;
        }
    }

    public void setTimedRuleExecutionFilter(TimedRuleExecutionFilter timedRuleExecutionFilter) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.timedRuleExecutionFilter = timedRuleExecutionFilter;
    }

    public TimedRuleExecutionFilter getTimedRuleExecutionFilter() {
        return this.timedRuleExecutionFilter;
    }

    public BeliefSystemType getBeliefSystemType() {
        return this.beliefSystemType;
    }
    
    public void setBeliefSystemType(BeliefSystemType beliefSystemType) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.beliefSystemType = beliefSystemType;
    }

    public ClockType getClockType() {
        return clockType;
    }

    public void setClockType(ClockType clockType) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.clockType = clockType;
    }

    public TimerJobFactoryManager getTimerJobFactoryManager() {
        return getTimerJobFactoryType().createInstance();
    }

    public TimerJobFactoryType getTimerJobFactoryType() {
        return timerJobFactoryType;
    }

    private void setTimerJobFactoryType(TimerJobFactoryType timerJobFactoryType) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.timerJobFactoryType = timerJobFactoryType;
    }

    @SuppressWarnings("unchecked")
    private void setQueryListenerClass(String property) {
        checkCanChange();
        this.queryListener = QueryListenerOption.determineQueryListenerClassOption( property );
    }

    private void setQueryListenerClass(QueryListenerOption option) {
        checkCanChange();
        this.queryListener = option;
    }

    public Map<String, WorkItemHandler> getWorkItemHandlers() {

        if ( this.workItemHandlers == null ) {
            initWorkItemHandlers(new HashMap<String, Object>());
        }
        return this.workItemHandlers;

    }
    
    public Map<String, WorkItemHandler> getWorkItemHandlers(Map<String, Object> params) {
        
        if ( this.workItemHandlers == null ) {
            initWorkItemHandlers(params);
        }
        return this.workItemHandlers;
    }
    

    private void initWorkItemHandlers(Map<String, Object> params) {
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
                loadWorkItemHandlers( factoryLocation, params );
            }
        }
        
    }

    @SuppressWarnings("unchecked")
    private void loadWorkItemHandlers(String location, Map<String, Object> params) {
        String content = ConfFileUtils.URLContentsToString( ConfFileUtils.getURL( location,
                                                                                  null,
                                                                                  RuleBaseConfiguration.class ) );
        Map<String, WorkItemHandler> workItemHandlers = (Map<String, WorkItemHandler>) MVELSafeHelper.getEvaluator().eval( content,
                                                                                                  params );
        this.workItemHandlers.putAll( workItemHandlers );
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
                                                               "org.drools.core.process.instance.impl.DefaultWorkItemManagerFactory" );
        Class<WorkItemManagerFactory> clazz = null;
        try {
            clazz = (Class<WorkItemManagerFactory>) this.classLoader.loadClass( className );
        } catch ( ClassNotFoundException e ) {
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

    public String getProcessInstanceManagerFactory() {
        return this.chainedProperties.getProperty( "drools.processInstanceManagerFactory",
                                                   "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory" );
    }

    public String getSignalManagerFactory() {
        return this.chainedProperties.getProperty( "drools.processSignalManagerFactory",
                                                   "org.jbpm.process.instance.event.DefaultSignalManagerFactory" );
    }

    public CommandService getCommandService(KnowledgeBase kbase,
                                            Environment environment) {
        if ( this.commandService == null ) {
            initCommandService( kbase,
                                environment );
        }
        return this.commandService;
    }

    @SuppressWarnings("unchecked")
    private void initCommandService(KnowledgeBase kbase,
                                    Environment environment) {
        String className = this.chainedProperties.getProperty( "drools.commandService",
                                                               null );
        if ( className == null ) {
            return;
        }

        Class<CommandService> clazz = null;
        try {
            clazz = (Class<CommandService>) this.classLoader.loadClass( className );
        } catch ( ClassNotFoundException e ) {
        }

        if ( clazz != null ) {
            try {
                this.commandService = clazz.getConstructor( KnowledgeBase.class,
                                                            KieSessionConfiguration.class,
                                                            Environment.class ).newInstance( kbase,
                                                                                             this,
                                                                                             environment );
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Unable to instantiate command service '" + className + "'",
                                                    e );
            }
        } else {
            throw new IllegalArgumentException( "Command service '" + className + "' not found" );
        }
    }

    public TimerService newTimerService() {
        String className = this.chainedProperties.getProperty(
                                                               "drools.timerService",
                                                               "org.drools.core.time.impl.JDKTimerService" );
        if ( className == null ) {
            return null;
        }

        Class<TimerService> clazz = null;
        try {
            clazz = (Class<TimerService>) this.classLoader.loadClass( className );
        } catch ( ClassNotFoundException e ) {
        }

        if ( clazz != null ) {
            try {
                return clazz.newInstance();
            } catch ( Exception e ) {
                
                throw new IllegalArgumentException(
                                                    "Unable to instantiate timer service '" + className
                                                            + "'",
                                                    e );
            }
        } else {
            try {
                return (TimerService) MVELSafeHelper.getEvaluator().eval(className);
            } catch (Exception e) {
                throw new IllegalArgumentException( "Timer service '" + className
                                                + "' not found", e );
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleValueKieSessionOption> T getOption(Class<T> option) {
        if ( ClockTypeOption.class.equals( option ) ) {
            return (T) ClockTypeOption.get( getClockType().toExternalForm() );
        } else if ( KeepReferenceOption.class.equals( option ) ) {
            return (T) (this.keepReference ? KeepReferenceOption.YES : KeepReferenceOption.NO);
        } else if ( TimerJobFactoryOption.class.equals( option ) ) {
            return (T) TimerJobFactoryOption.get( getTimerJobFactoryType().toExternalForm() );
        } else if ( QueryListenerOption.class.equals( option ) ) {
            return (T) this.queryListener;
        } else if ( BeliefSystemTypeOption.class.equals( option ) ) {
            return (T) BeliefSystemTypeOption.get( this.getBeliefSystemType().getId() );
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends MultiValueKieSessionOption> T getOption(Class<T> option,
                                                                    String key) {
        if ( WorkItemHandlerOption.class.equals( option ) ) {
            return (T) WorkItemHandlerOption.get( key,
                                                  getWorkItemHandlers().get( key ) );
        }
        return null;
    }

    public <T extends KieSessionOption> void setOption(T option) {
        if ( option instanceof ClockTypeOption ) {
            setClockType( ClockType.resolveClockType( ((ClockTypeOption) option).getClockType() ) );
        } else if ( option instanceof TimerJobFactoryOption ) {
            setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType(((TimerJobFactoryOption) option).getTimerJobType()));
        } else if ( option instanceof KeepReferenceOption ) {
            setKeepReference(((KeepReferenceOption) option).isKeepReference());
        } else if ( option instanceof ForceEagerActivationOption ) {
            setForceEagerActivationFilter(((ForceEagerActivationOption) option).getFilter());
        } else if ( option instanceof TimedRuleExectionOption ) {
            setTimedRuleExecutionFilter(((TimedRuleExectionOption) option).getFilter());
        } else if ( option instanceof WorkItemHandlerOption ) {
            getWorkItemHandlers().put(((WorkItemHandlerOption) option).getName(),
                                      ((WorkItemHandlerOption) option).getHandler() );
        } else if ( option instanceof QueryListenerOption ) {
            this.queryListener = (QueryListenerOption) option;
        } else if ( option instanceof BeliefSystemTypeOption ) {
            this.beliefSystemType = ((BeliefSystemType.resolveBeliefSystemType( ((BeliefSystemTypeOption) option).getBeliefSystemType() )));
        }
    }

    public QueryListenerOption getQueryListenerOption() {
        return this.queryListener;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionConfiguration that = (SessionConfiguration) o;

        return keepReference == that.keepReference &&
               beliefSystemType == that.beliefSystemType &&
               clockType == that.clockType &&
               timerJobFactoryType == that.timerJobFactoryType;
    }

    @Override
    public int hashCode() {
        if (this == getDefaultInstance()) {
            return 0;
        }
        int result = (keepReference ? 1 : 0);
        result = 31 * result + clockType.hashCode();
        result = 31 * result + beliefSystemType.hashCode();
        result = 31 * result + timerJobFactoryType.hashCode();
        return result;
    }
}
