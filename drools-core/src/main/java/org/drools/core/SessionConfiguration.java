/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.core.base.CoreComponentsBuilder;
import org.drools.core.process.WorkItemManagerFactory;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.util.ConfFileUtils;
import org.drools.util.StringUtils;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.AccumulateNullPropagationOption;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.DirectFiringOption;
import org.kie.api.runtime.conf.KeepReferenceOption;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.QueryListenerOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.api.runtime.conf.ThreadSafeOption;
import org.kie.api.runtime.conf.TimedRuleExecutionFilter;
import org.kie.api.runtime.conf.TimedRuleExecutionOption;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.conf.WorkItemHandlerOption;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.conf.ForceEagerActivationFilter;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.kie.internal.utils.ChainedProperties;

public class SessionConfiguration implements KieSessionConfiguration, Externalizable {

    private ChainedProperties chainedProperties;

    private volatile boolean immutable;

    private boolean keepReference;

    private boolean directFiring;

    private boolean threadSafe;

    private boolean accumulateNullPropagation;

    private ForceEagerActivationFilter forceEagerActivationFilter;
    private TimedRuleExecutionFilter timedRuleExecutionFilter;

    private ClockType clockType;

    private BeliefSystemType beliefSystemType;

    private QueryListenerOption queryListener;

    private Map<String, WorkItemHandler> workItemHandlers;
    private WorkItemManagerFactory workItemManagerFactory;
    private ExecutableRunner runner;

    private transient ClassLoader classLoader;

    private TimerJobFactoryType timerJobFactoryType;

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( chainedProperties );
        out.writeBoolean(immutable);
        out.writeBoolean( keepReference );
        out.writeObject(clockType);
        out.writeObject( queryListener );
        out.writeObject( timerJobFactoryType );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        chainedProperties = (ChainedProperties) in.readObject();
        immutable = in.readBoolean();
        keepReference = in.readBoolean();
        clockType = (ClockType) in.readObject();
        queryListener = (QueryListenerOption) in.readObject();
        try {
            timerJobFactoryType = (TimerJobFactoryType) in.readObject();
        } catch (java.io.InvalidObjectException e) {
            // workaround for old typo in TimerJobFactoryType
            if (e.getMessage().contains( "DEFUALT" )) {
                timerJobFactoryType = TimerJobFactoryType.DEFAULT;
            } else {
                throw e;
            }
        }
    }
    public SessionConfiguration() {
        init( null, null, null );
    }

    /**
     * Creates a new session configuration using the provided properties
     * as configuration options.
     */
    public SessionConfiguration( Properties properties ) {
        init( properties, null, null );
    }

    public SessionConfiguration( Properties properties, ClassLoader classLoader ) {
        init( properties, classLoader, null );
    }

    public SessionConfiguration( Properties properties, ClassLoader classLoader, ChainedProperties chainedProperties ) {
        init( properties, classLoader, chainedProperties );
    }

    private void init(Properties properties, ClassLoader classLoader, ChainedProperties chainedProperties) {
        this.classLoader = classLoader instanceof ProjectClassLoader ? classLoader : ProjectClassLoader.getClassLoader(classLoader, getClass());

        this.immutable = false;

        this.chainedProperties = chainedProperties != null ? chainedProperties : ChainedProperties.getChainedProperties( this.classLoader );

        if ( properties != null ) {
            this.chainedProperties = this.chainedProperties.clone();
            this.chainedProperties.addProperties( properties );
        }

        setKeepReference(Boolean.parseBoolean( getPropertyValue( KeepReferenceOption.PROPERTY_NAME, "true" ) ));

        setDirectFiring(Boolean.parseBoolean( getPropertyValue( DirectFiringOption.PROPERTY_NAME, "false" ) ));

        setThreadSafe(Boolean.parseBoolean( getPropertyValue( ThreadSafeOption.PROPERTY_NAME, "true" ) ));

        setAccumulateNullPropagation(Boolean.parseBoolean( getPropertyValue( AccumulateNullPropagationOption.PROPERTY_NAME, "false" ) ));

        setForceEagerActivationFilter(ForceEagerActivationOption.resolve( getPropertyValue( ForceEagerActivationOption.PROPERTY_NAME, "false" ) ).getFilter());

        setTimedRuleExecutionFilter(TimedRuleExecutionOption.resolve( getPropertyValue( TimedRuleExecutionOption.PROPERTY_NAME, "false" ) ).getFilter());

        setBeliefSystemType( BeliefSystemType.resolveBeliefSystemType( getPropertyValue( BeliefSystemTypeOption.PROPERTY_NAME, BeliefSystemType.SIMPLE.getId() ) ) );

        setClockType( ClockType.resolveClockType( getPropertyValue( ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.getId() ) ) );

        setQueryListenerOption( QueryListenerOption.determineQueryListenerClassOption( getPropertyValue( QueryListenerOption.PROPERTY_NAME, QueryListenerOption.STANDARD.getAsString() ) ) );

        setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType( getPropertyValue( TimerJobFactoryOption.PROPERTY_NAME, TimerJobFactoryType.THREAD_SAFE_TRACKABLE.getId() ) ));
    }

    public SessionConfiguration addDefaultProperties(Properties properties) {
        Properties defaultProperties = new Properties();
        for ( Map.Entry<Object, Object> prop : properties.entrySet() ) {
            if ( chainedProperties.getProperty( (String) prop.getKey(), null) == null ) {
                defaultProperties.put( prop.getKey(), prop.getValue() );
            }
        }

        this.chainedProperties.addProperties(defaultProperties);
        return this;
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

    private void setKeepReference(boolean keepReference) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.keepReference = keepReference;
    }

    public boolean isKeepReference() {
        return this.keepReference;
    }

    private void setDirectFiring(boolean directFiring) {
        this.directFiring = directFiring;
    }

    public boolean isDirectFiring() {
        return this.directFiring;
    }

    private void setThreadSafe(boolean threadSafe) {
        this.threadSafe = threadSafe;
    }

    public boolean isThreadSafe() {
        return this.threadSafe;
    }

    private void setAccumulateNullPropagation(boolean accumulateNullPropagation) {
        this.accumulateNullPropagation = accumulateNullPropagation;
    }

    public boolean isAccumulateNullPropagation() {
        return this.accumulateNullPropagation;
    }

    private void setForceEagerActivationFilter(ForceEagerActivationFilter forceEagerActivationFilter) {
        this.forceEagerActivationFilter = forceEagerActivationFilter;
    }

    public ForceEagerActivationFilter getForceEagerActivationFilter() {
        return this.forceEagerActivationFilter;
    }

    private void setTimedRuleExecutionFilter(TimedRuleExecutionFilter timedRuleExecutionFilter) {
        this.timedRuleExecutionFilter = timedRuleExecutionFilter;
    }

    public TimedRuleExecutionFilter getTimedRuleExecutionFilter() {
        return this.timedRuleExecutionFilter;
    }

    public BeliefSystemType getBeliefSystemType() {
        return this.beliefSystemType;
    }

    private void setBeliefSystemType(BeliefSystemType beliefSystemType) {
        this.beliefSystemType = beliefSystemType;
    }

    public ClockType getClockType() {
        return clockType;
    }

    private void setClockType(ClockType clockType) {
        this.clockType = clockType;
    }

    public TimerJobFactoryType getTimerJobFactoryType() {
        return timerJobFactoryType;
    }

    private void setTimerJobFactoryType(TimerJobFactoryType timerJobFactoryType) {
        this.timerJobFactoryType = timerJobFactoryType;
    }

    public Map<String, WorkItemHandler> getWorkItemHandlers() {
        if ( this.workItemHandlers == null ) {
            initWorkItemHandlers(new HashMap<>());
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
        this.workItemHandlers = new HashMap<>();

        // split on each space
        String locations[] = getPropertyValue( "drools.workItemHandlers", "" ).split( "\\s" );

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
        String content = ConfFileUtils.URLContentsToString( ConfFileUtils.getURL( location, null, RuleBaseConfiguration.class ) );
        Map<String, WorkItemHandler> workItemHandlers = (Map<String, WorkItemHandler>) CoreComponentsBuilder.get().getMVELExecutor().eval( content, params );
        this.workItemHandlers.putAll( workItemHandlers );
    }

    public WorkItemManagerFactory getWorkItemManagerFactory() {
        if ( this.workItemManagerFactory == null ) {
            initWorkItemManagerFactory();
        }
        return this.workItemManagerFactory;
    }

    public void setWorkItemManagerFactory(WorkItemManagerFactory workItemManagerFactory) {
        this.workItemManagerFactory = workItemManagerFactory;
    }

    @SuppressWarnings("unchecked")
    private void initWorkItemManagerFactory() {
        String className = getPropertyValue( "drools.workItemManagerFactory", "org.drools.core.process.impl.DefaultWorkItemManagerFactory" );
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
        return getPropertyValue( "drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory" );
    }

    public String getSignalManagerFactory() {
        return getPropertyValue( "drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory" );
    }

    public ExecutableRunner getRunner( KieBase kbase, Environment environment ) {
        if ( this.runner == null ) {
            initCommandService( kbase,
                    environment );
        }
        return this.runner;
    }

    @SuppressWarnings("unchecked")
    private void initCommandService(KieBase kbase, Environment environment) {
        String className = getPropertyValue( "drools.commandService", null );
        if ( className == null ) {
            return;
        }

        Class<ExecutableRunner> clazz = null;
        try {
            clazz = (Class<ExecutableRunner>) this.classLoader.loadClass( className );
        } catch ( ClassNotFoundException e ) {
        }

        if ( clazz != null ) {
            try {
                this.runner = clazz.getConstructor( KieBase.class,
                        KieSessionConfiguration.class,
                        Environment.class ).newInstance( kbase,
                        this,
                        environment );
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Unable to instantiate command service '" + className + "'", e );
            }
        } else {
            throw new IllegalArgumentException( "Command service '" + className + "' not found" );
        }
    }

    public String getPropertyValue( String name, String defaultValue ) {
        return this.chainedProperties.getProperty( name, defaultValue );
    }

    public QueryListenerOption getQueryListenerOption() {
        return this.queryListener;
    }

    private void setQueryListenerOption( QueryListenerOption queryListener ) {
        this.queryListener = queryListener;
    }

    public final boolean hasForceEagerActivationFilter() {
        try {
            return getForceEagerActivationFilter().accept(null);
        } catch (Exception e) {
            return true;
        }
    }

    public final TimerJobFactoryManager getTimerJobFactoryManager() {
        return getTimerJobFactoryType().createInstance();
    }

    public final <T extends KieSessionOption> void setOption(T option) {
        checkCanChange(); // throws an exception if a change isn't possible;
        if ( option instanceof ClockTypeOption ) {
            setClockType( ClockType.resolveClockType( ((ClockTypeOption) option).getClockType() ) );
        } else if ( option instanceof TimerJobFactoryOption ) {
            setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType(((TimerJobFactoryOption) option).getTimerJobType()));
        } else if ( option instanceof KeepReferenceOption ) {
            setKeepReference(((KeepReferenceOption) option).isKeepReference());
        } else if ( option instanceof DirectFiringOption ) {
            setDirectFiring(((DirectFiringOption) option).isDirectFiring());
        } else if ( option instanceof ThreadSafeOption ) {
            setThreadSafe(((ThreadSafeOption) option).isThreadSafe());
        } else if ( option instanceof AccumulateNullPropagationOption ) {
            setAccumulateNullPropagation(((AccumulateNullPropagationOption) option).isAccumulateNullPropagation());
        } else if ( option instanceof ForceEagerActivationOption ) {
            setForceEagerActivationFilter(((ForceEagerActivationOption) option).getFilter());
        } else if ( option instanceof TimedRuleExecutionOption ) {
            setTimedRuleExecutionFilter(((TimedRuleExecutionOption) option).getFilter());
        } else if ( option instanceof WorkItemHandlerOption ) {
            getWorkItemHandlers().put(((WorkItemHandlerOption) option).getName(),
                                      ((WorkItemHandlerOption) option).getHandler() );
        } else if ( option instanceof QueryListenerOption ) {
            setQueryListenerOption( (QueryListenerOption) option );
        } else if ( option instanceof BeliefSystemTypeOption ) {
            setBeliefSystemType( ((BeliefSystemType.resolveBeliefSystemType( ((BeliefSystemTypeOption) option).getBeliefSystemType() ))) );
        }
    }

    @SuppressWarnings("unchecked")
    public final <T extends SingleValueKieSessionOption> T getOption(Class<T> option) {
        if ( ClockTypeOption.class.equals( option ) ) {
            return (T) ClockTypeOption.get( getClockType().toExternalForm() );
        } else if ( KeepReferenceOption.class.equals( option ) ) {
            return (T) (isKeepReference() ? KeepReferenceOption.YES : KeepReferenceOption.NO);
        } else if ( DirectFiringOption.class.equals( option ) ) {
            return (T) (isDirectFiring() ? DirectFiringOption.YES : DirectFiringOption.NO);
        } else if ( ThreadSafeOption.class.equals( option ) ) {
            return (T) (isThreadSafe() ? ThreadSafeOption.YES : ThreadSafeOption.NO);
        } else if ( AccumulateNullPropagationOption.class.equals( option ) ) {
            return (T) (isAccumulateNullPropagation() ? AccumulateNullPropagationOption.YES : AccumulateNullPropagationOption.NO);
        } else if ( TimerJobFactoryOption.class.equals( option ) ) {
            return (T) TimerJobFactoryOption.get( getTimerJobFactoryType().toExternalForm() );
        } else if ( QueryListenerOption.class.equals( option ) ) {
            return (T) getQueryListenerOption();
        } else if ( BeliefSystemTypeOption.class.equals( option ) ) {
            return (T) BeliefSystemTypeOption.get( this.getBeliefSystemType().getId() );
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public final <T extends MultiValueKieSessionOption> T getOption(Class<T> option, String key) {
        if ( WorkItemHandlerOption.class.equals( option ) ) {
            return (T) WorkItemHandlerOption.get( key, getWorkItemHandlers().get( key ) );
        }
        return null;
    }

    public final void setProperty(String name, String value) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return;
        }

        checkCanChange(); // throws an exception if a change isn't possible;
        if ( name.equals( KeepReferenceOption.PROPERTY_NAME ) ) {
            setKeepReference( StringUtils.isEmpty( value ) || Boolean.parseBoolean( value ) );
        }else if ( name.equals( DirectFiringOption.PROPERTY_NAME ) ) {
            setDirectFiring(!StringUtils.isEmpty(value) && Boolean.parseBoolean(value));
        }else if ( name.equals( ThreadSafeOption.PROPERTY_NAME ) ) {
            setThreadSafe( StringUtils.isEmpty( value ) || Boolean.parseBoolean( value ) );
        } else if ( name.equals( AccumulateNullPropagationOption.PROPERTY_NAME ) ) {
            setAccumulateNullPropagation( !StringUtils.isEmpty( value ) && Boolean.parseBoolean( value ) );
        } else if ( name.equals( ForceEagerActivationOption.PROPERTY_NAME ) ) {
            setForceEagerActivationFilter(ForceEagerActivationOption.resolve(StringUtils.isEmpty(value) ? "false" : value).getFilter());
        } else if ( name.equals( TimedRuleExecutionOption.PROPERTY_NAME ) ) {
            setTimedRuleExecutionFilter(TimedRuleExecutionOption.resolve(StringUtils.isEmpty(value) ? "false" : value).getFilter());
        } else if ( name.equals( ClockTypeOption.PROPERTY_NAME ) ) {
            setClockType(ClockType.resolveClockType(StringUtils.isEmpty(value) ? "realtime" : value));
        } else if ( name.equals( TimerJobFactoryOption.PROPERTY_NAME ) ) {
            setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType(StringUtils.isEmpty(value) ? "default" : value));
        } else if ( name.equals( QueryListenerOption.PROPERTY_NAME ) ) {
            String property = StringUtils.isEmpty(value) ? QueryListenerOption.STANDARD.getAsString() : value;
            setQueryListenerOption( QueryListenerOption.determineQueryListenerClassOption( property ) );
        } else if ( name.equals( BeliefSystemTypeOption.PROPERTY_NAME ) ) {
            setBeliefSystemType(StringUtils.isEmpty(value) ? BeliefSystemType.SIMPLE : BeliefSystemType.resolveBeliefSystemType(value));
        }
    }

    public final String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        }

        if ( name.equals( KeepReferenceOption.PROPERTY_NAME ) ) {
            return Boolean.toString( isKeepReference() );
        }else if ( name.equals( DirectFiringOption.PROPERTY_NAME ) ) {
            return Boolean.toString(isDirectFiring());
        }else if ( name.equals( ThreadSafeOption.PROPERTY_NAME ) ) {
            return Boolean.toString(isThreadSafe());
        } else if ( name.equals( AccumulateNullPropagationOption.PROPERTY_NAME ) ) {
            return Boolean.toString(isAccumulateNullPropagation());
        } else if ( name.equals( ClockTypeOption.PROPERTY_NAME ) ) {
            return getClockType().toExternalForm();
        } else if ( name.equals( TimerJobFactoryOption.PROPERTY_NAME ) ) {
            return getTimerJobFactoryType().toExternalForm();
        } else if ( name.equals( QueryListenerOption.PROPERTY_NAME ) ) {
            return getQueryListenerOption().getAsString();
        } else if ( name.equals( BeliefSystemTypeOption.PROPERTY_NAME ) ) {
            return getBeliefSystemType().getId();
        }
        return null;
    }
}
