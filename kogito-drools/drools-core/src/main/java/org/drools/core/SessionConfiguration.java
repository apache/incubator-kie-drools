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

import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.TimerJobFactoryManager;
import org.drools.core.util.StringUtils;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.KeepReferenceOption;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.QueryListenerOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.api.runtime.conf.TimedRuleExecutionOption;
import org.kie.api.runtime.conf.TimedRuleExecutionFilter;
import org.kie.api.runtime.conf.TimerJobFactoryOption;
import org.kie.api.runtime.conf.WorkItemHandlerOption;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.conf.ForceEagerActivationFilter;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;

import java.io.Externalizable;
import java.util.Map;
import java.util.Properties;

public abstract class SessionConfiguration implements KieSessionConfiguration, Externalizable {

    private static class DefaultSessionConfiguration {
        private static final SessionConfigurationImpl defaultInstance = new SessionConfigurationImpl();
    }

    public static SessionConfiguration getDefaultInstance() {
        return DefaultSessionConfiguration.defaultInstance;
    }

    public static SessionConfiguration newInstance() {
        return new SessionConfigurationDelegate();
    }

    public static SessionConfiguration newInstance(Properties properties) {
        return new SessionConfigurationImpl(properties);
    }

    public abstract void setKeepReference(boolean keepReference);
    public abstract boolean isKeepReference();

    public abstract void setForceEagerActivationFilter(ForceEagerActivationFilter forceEagerActivationFilter);
    public abstract ForceEagerActivationFilter getForceEagerActivationFilter();

    public final boolean hasForceEagerActivationFilter() {
        try {
            return getForceEagerActivationFilter().accept(null);
        } catch (Exception e) {
            return true;
        }
    }

    public abstract SessionConfiguration addDefaultProperties(Properties properties);

    public abstract void setTimedRuleExecutionFilter(TimedRuleExecutionFilter timedRuleExecutionFilter);
    public abstract TimedRuleExecutionFilter getTimedRuleExecutionFilter();

    public abstract BeliefSystemType getBeliefSystemType();
    public abstract void setBeliefSystemType(BeliefSystemType beliefSystemType);

    public abstract ClockType getClockType();
    public abstract void setClockType(ClockType clockType);

    public abstract TimerJobFactoryType getTimerJobFactoryType();
    public abstract void setTimerJobFactoryType(TimerJobFactoryType timerJobFactoryType);

    public final TimerJobFactoryManager getTimerJobFactoryManager() {
        return getTimerJobFactoryType().createInstance();
    }

    public abstract Map<String, WorkItemHandler> getWorkItemHandlers();
    public abstract Map<String, WorkItemHandler> getWorkItemHandlers(Map<String, Object> params);
    public abstract WorkItemManagerFactory getWorkItemManagerFactory();

    public abstract String getProcessInstanceManagerFactory();

    public abstract String getSignalManagerFactory();

    public abstract ExecutableRunner getRunner( KnowledgeBase kbase, Environment environment );

    public abstract TimerService newTimerService();

    public abstract QueryListenerOption getQueryListenerOption();
    public abstract void setQueryListenerOption( QueryListenerOption queryListener );

    public final <T extends KieSessionOption> void setOption(T option) {
        if ( option instanceof ClockTypeOption ) {
            setClockType( ClockType.resolveClockType( ((ClockTypeOption) option).getClockType() ) );
        } else if ( option instanceof TimerJobFactoryOption ) {
            setTimerJobFactoryType(TimerJobFactoryType.resolveTimerJobFactoryType(((TimerJobFactoryOption) option).getTimerJobType()));
        } else if ( option instanceof KeepReferenceOption ) {
            setKeepReference(((KeepReferenceOption) option).isKeepReference());
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
    public final <T extends MultiValueKieSessionOption> T getOption(Class<T> option,
                                                              String key) {
        if ( WorkItemHandlerOption.class.equals( option ) ) {
            return (T) WorkItemHandlerOption.get( key,
                                                  getWorkItemHandlers().get( key ) );
        }
        return null;
    }

    public final void setProperty(String name,
                            String value) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return;
        }

        if ( name.equals( KeepReferenceOption.PROPERTY_NAME ) ) {
            setKeepReference(StringUtils.isEmpty(value) || Boolean.parseBoolean(value));
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
            return Boolean.toString(isKeepReference());
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionConfiguration that = (SessionConfiguration) o;

        return isKeepReference() == that.isKeepReference() &&
               getBeliefSystemType() == that.getBeliefSystemType() &&
               getClockType() == that.getClockType() &&
               getTimerJobFactoryType() == that.getTimerJobFactoryType();
    }

    @Override
    public final int hashCode() {
        if (this == getDefaultInstance()) {
            return 0;
        }
        int result = (isKeepReference() ? 1 : 0);
        result = 31 * result + getClockType().hashCode();
        result = 31 * result + getBeliefSystemType().hashCode();
        result = 31 * result + getTimerJobFactoryType().hashCode();
        return result;
    }
}
