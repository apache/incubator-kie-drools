/*
 * Copyright 2015 JBoss Inc
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

import org.drools.core.command.CommandService;
import org.drools.core.process.instance.WorkItemManagerFactory;
import org.drools.core.time.TimerService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.conf.QueryListenerOption;
import org.kie.api.runtime.conf.TimedRuleExecutionFilter;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.conf.ForceEagerActivationFilter;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Properties;

import static org.drools.core.util.ClassUtils.areNullSafeEquals;

public class SessionConfigurationDelegate extends SessionConfiguration {
    private final SessionConfiguration delegate = SessionConfiguration.getDefaultInstance();

    private ClockType clockType;
    private BeliefSystemType beliefSystemType;
    private Boolean keepReference;
    private ForceEagerActivationFilter forceEagerActivationFilter;
    private TimedRuleExecutionFilter timedRuleExecutionFilter;
    private QueryListenerOption queryListener;
    private TimerJobFactoryType timerJobFactoryType;

    public SessionConfigurationDelegate() { }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( clockType );
        out.writeObject( beliefSystemType );
        out.writeObject( keepReference );
        out.writeObject( queryListener );
        out.writeObject( timerJobFactoryType );
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        clockType = (ClockType) in.readObject();
        beliefSystemType = (BeliefSystemType) in.readObject();
        keepReference = (Boolean) in.readObject();
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

    @Override
    public SessionConfigurationImpl addDefaultProperties(Properties properties) {
        SessionConfigurationImpl conf = cloneSessionConfiguration();
        conf.addDefaultProperties( properties );
        return conf;
    }

    private SessionConfigurationImpl cloneSessionConfiguration() {
        SessionConfigurationImpl conf = new SessionConfigurationImpl();
        conf.setClockType( getClockType() );
        conf.setBeliefSystemType( getBeliefSystemType() );
        conf.setKeepReference( isKeepReference() );
        conf.setForceEagerActivationFilter( getForceEagerActivationFilter() );
        conf.setTimedRuleExecutionFilter( getTimedRuleExecutionFilter() );
        conf.setQueryListenerOption( getQueryListenerOption() );
        conf.setTimerJobFactoryType( getTimerJobFactoryType() );
        return conf;
    }

    @Override
    public void setKeepReference( boolean keepReference ) {
        if (isKeepReference() != keepReference) {
            this.keepReference = keepReference;
        }
    }

    @Override
    public boolean isKeepReference() {
        return keepReference != null ? keepReference : delegate.isKeepReference();
    }

    @Override
    public void setForceEagerActivationFilter( ForceEagerActivationFilter forceEagerActivationFilter ) {
        if (!areNullSafeEquals( getForceEagerActivationFilter(), forceEagerActivationFilter ) ) {
            this.forceEagerActivationFilter = forceEagerActivationFilter;
        }
    }

    @Override
    public ForceEagerActivationFilter getForceEagerActivationFilter() {
        return forceEagerActivationFilter != null ? forceEagerActivationFilter : delegate.getForceEagerActivationFilter();
    }

    @Override
    public void setTimedRuleExecutionFilter( TimedRuleExecutionFilter timedRuleExecutionFilter ) {
        if (!areNullSafeEquals( getTimedRuleExecutionFilter(), timedRuleExecutionFilter )) {
            this.timedRuleExecutionFilter = timedRuleExecutionFilter;
        }
    }

    @Override
    public TimedRuleExecutionFilter getTimedRuleExecutionFilter() {
        return timedRuleExecutionFilter != null ? timedRuleExecutionFilter : delegate.getTimedRuleExecutionFilter();
    }

    @Override
    public BeliefSystemType getBeliefSystemType() {
        return beliefSystemType != null ? beliefSystemType : delegate.getBeliefSystemType();
    }

    @Override
    public void setBeliefSystemType( BeliefSystemType beliefSystemType ) {
        if (getBeliefSystemType() != beliefSystemType) {
            this.beliefSystemType = beliefSystemType;
        }
    }

    @Override
    public ClockType getClockType() {
        return clockType != null ? clockType : delegate.getClockType();
    }

    @Override
    public void setClockType( ClockType clockType ) {
        if (getClockType() != clockType) {
            this.clockType = clockType;
        }
    }

    @Override
    public TimerJobFactoryType getTimerJobFactoryType() {
        return timerJobFactoryType != null ? timerJobFactoryType : delegate.getTimerJobFactoryType();
    }

    @Override
    public void setTimerJobFactoryType( TimerJobFactoryType timerJobFactoryType ) {
        if (getTimerJobFactoryType() != timerJobFactoryType) {
            this.timerJobFactoryType = timerJobFactoryType;
        }
    }


    @Override
    public QueryListenerOption getQueryListenerOption() {
        return queryListener != null ? queryListener : delegate.getQueryListenerOption();
    }

    @Override
    public void setQueryListenerOption( QueryListenerOption queryListener ) {
        if (getQueryListenerOption() != queryListener) {
            this.queryListener = queryListener;
        }
    }

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers() {
        return delegate.getWorkItemHandlers();
    }

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers( Map<String, Object> params ) {
        return delegate.getWorkItemHandlers(params);
    }

    @Override
    public WorkItemManagerFactory getWorkItemManagerFactory() {
        return delegate.getWorkItemManagerFactory();
    }

    @Override
    public String getProcessInstanceManagerFactory() {
        return delegate.getProcessInstanceManagerFactory();
    }

    @Override
    public String getSignalManagerFactory() {
        return delegate.getSignalManagerFactory();
    }

    @Override
    public CommandService getCommandService( KnowledgeBase kbase, Environment environment ) {
        return delegate.getCommandService( kbase, environment );
    }

    @Override
    public TimerService newTimerService() {
        return delegate.newTimerService();
    }
}
