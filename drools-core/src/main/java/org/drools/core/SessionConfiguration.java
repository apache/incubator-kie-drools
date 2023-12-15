/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.drools.core.time.TimerService;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.util.StringUtils;
import org.kie.api.KieBase;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.OptionKey;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.KeepReferenceOption;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.internal.conf.CompositeConfiguration;

public class SessionConfiguration extends BaseConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> implements KieSessionConfiguration, Externalizable {

    private static final long              serialVersionUID = 510l;

    public static final ConfigurationKey<SessionConfiguration> KEY = new ConfigurationKey<>("Base");

    private boolean                        keepReference;

    private ClockType                      clockType;

    private TimerJobFactoryType            timerJobFactoryType;

    private PersistedSessionOption persistedSessionOption;

    private ExecutableRunner runner;

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeBoolean( keepReference );
        out.writeObject(clockType);
        out.writeObject( timerJobFactoryType );
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        keepReference = in.readBoolean();
        clockType = (ClockType) in.readObject();
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


    public SessionConfiguration( CompositeConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> compConfig) {
        super(compConfig);
        init();
    }

    private void init() {
        setKeepReference(Boolean.parseBoolean(getPropertyValue(KeepReferenceOption.PROPERTY_NAME, "true")));

        setClockType( ClockType.resolveClockType( getPropertyValue( ClockTypeOption.PROPERTY_NAME, ClockType.REALTIME_CLOCK.getId() ) ) );


        setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType( getPropertyValue( TimerJobFactoryOption.PROPERTY_NAME, TimerJobFactoryType.THREAD_SAFE_TRACKABLE.getId() ) ));
    }


    public final <T extends KieSessionOption> void setOption(T option) {
        switch (option.propertyName()) {
            case ClockTypeOption.PROPERTY_NAME: {
                setClockType(ClockType.resolveClockType(((ClockTypeOption) option).getClockType()));
                break;
            }
            case TimerJobFactoryOption.PROPERTY_NAME: {
                setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType(((TimerJobFactoryOption) option).getTimerJobType()));
                break;
            }
            case KeepReferenceOption.PROPERTY_NAME: {
                setKeepReference(((KeepReferenceOption)option).isKeepReference());
                break;
            }
            case PersistedSessionOption.PROPERTY_NAME: {
                setPersistedSessionOption( (PersistedSessionOption) option );
                break;
            }
            default:
                compConfig.setOption(option);
        }
    }

    @SuppressWarnings("unchecked")
    public final <T extends SingleValueKieSessionOption> T getOption(OptionKey<T> option) {
        switch (option.name()) {
            case ClockTypeOption.PROPERTY_NAME: {
                return (T) ClockTypeOption.get( getClockType().toExternalForm() );
            }
            case TimerJobFactoryOption.PROPERTY_NAME: {
                return (T) TimerJobFactoryOption.get( getTimerJobFactoryType().toExternalForm() );
            }
            case KeepReferenceOption.PROPERTY_NAME: {
                return (T) (isKeepReference() ? KeepReferenceOption.YES : KeepReferenceOption.NO);
            }
            case PersistedSessionOption.PROPERTY_NAME: {
                return (T) getPersistedSessionOption();
            }
            default:
                return compConfig.getOption(option);
        }
    }

    @SuppressWarnings("unchecked")
    public final <T extends MultiValueKieSessionOption> T getOption(OptionKey<T> option,
                                                                    String subKey) {

        return compConfig.getOption(option, subKey);
    }

    @Override public <C extends MultiValueKieSessionOption> Set<String> getOptionSubKeys(OptionKey<C> optionKey) {
        return compConfig.getOptionSubKeys(optionKey);
    }

    public final boolean setInternalProperty(String name, String value) {
        switch(name) {
            case ClockTypeOption.PROPERTY_NAME: {
                setClockType(ClockType.resolveClockType(StringUtils.isEmpty(value) ? "realtime" : value));
                break;
            }
            case TimerJobFactoryOption.PROPERTY_NAME: {
                setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType(StringUtils.isEmpty(value) ? "default" : value));
                break;
            } default : {
                return false;
            }
        }

        return true;
    }

    public final String getInternalProperty(String name) {
        switch(name) {
            case ClockTypeOption.PROPERTY_NAME: {
                return getClockType().toExternalForm();
            }
            case TimerJobFactoryOption.PROPERTY_NAME: {
                return getTimerJobFactoryType().toExternalForm();
            }
        }
        return null;
    }

    public SessionConfiguration addDefaultProperties(Properties properties) {
        Properties defaultProperties = new Properties();
        for ( Map.Entry<Object, Object> prop : properties.entrySet() ) {
            if ( getProperties().getProperty( (String) prop.getKey(), null) == null ) {
                defaultProperties.put( prop.getKey(), prop.getValue() );
            }
        }

        getProperties().addProperties(defaultProperties);
        return this;
    }

    public void setKeepReference(boolean keepReference) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.keepReference = keepReference;
    }

    public boolean isKeepReference() {
        return this.keepReference;
    }

    public boolean hasPersistedSessionOption() {
        return this.persistedSessionOption != null;
    }

    public PersistedSessionOption getPersistedSessionOption() {
        return this.persistedSessionOption;
    }

    private void setPersistedSessionOption(PersistedSessionOption persistedSessionOption){
        this.persistedSessionOption = persistedSessionOption;
    }

    public ClockType getClockType() {
        return clockType;
    }

    public void setClockType(ClockType clockType) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.clockType = clockType;
    }

    public TimerJobFactoryType getTimerJobFactoryType() {
        return timerJobFactoryType;
    }

    public void setTimerJobFactoryType(TimerJobFactoryType timerJobFactoryType) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.timerJobFactoryType = timerJobFactoryType;
    }

    public final TimerJobFactoryManager getTimerJobFactoryManager() {
        return getTimerJobFactoryType().createInstance();
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
            clazz = (Class<ExecutableRunner>) getClassLoader().loadClass( className );
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

    public TimerService createTimerService() {
        TimerService service = getClockType().createInstance();
        service.setTimerJobFactoryManager(getTimerJobFactoryManager());
        return service;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SessionConfiguration that = (SessionConfiguration) o;


        return getClockType() == that.getClockType() &&
                getTimerJobFactoryType() == that.getTimerJobFactoryType();
    }

    @Override
    public final int hashCode() {
        int result = getClockType().hashCode();
        result = 31 * result + getTimerJobFactoryType().hashCode();
        return result;
    }
}
