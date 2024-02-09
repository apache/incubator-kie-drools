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
import java.util.Set;

import org.drools.util.StringUtils;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.OptionKey;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.AccumulateNullPropagationOption;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.DirectFiringOption;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.QueryListenerOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.api.runtime.conf.ThreadSafeOption;
import org.kie.api.runtime.conf.TimedRuleExecutionFilter;
import org.kie.api.runtime.conf.TimedRuleExecutionOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.InternalPropertiesConfiguration;
import org.kie.internal.runtime.conf.ForceEagerActivationFilter;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;

public class RuleSessionConfiguration extends BaseConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> implements KieSessionConfiguration, InternalPropertiesConfiguration, Externalizable {

    public static final ConfigurationKey<RuleSessionConfiguration> KEY = new ConfigurationKey<>("Rule");

    private static final long              serialVersionUID = 510l;

    private boolean                        directFiring;

    private boolean                        threadSafe;

    private boolean                        accumulateNullPropagation;

    private ForceEagerActivationFilter     forceEagerActivationFilter;
    private TimedRuleExecutionFilter       timedRuleExecutionFilter;

    private BeliefSystemType               beliefSystemType;

    private QueryListenerOption            queryListener;

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject( queryListener );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        queryListener = (QueryListenerOption) in.readObject();
    }

    public final boolean hasForceEagerActivationFilter() {
        try {
            return getForceEagerActivationFilter().accept(null);
        } catch (Exception e) {
            return true;
        }
    }

    public RuleSessionConfiguration(CompositeConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> compConfig) {
        super(compConfig);
        init();
    }

    private void init() {
        setDirectFiring(Boolean.parseBoolean(getPropertyValue(DirectFiringOption.PROPERTY_NAME, "false")));

        setThreadSafe(Boolean.parseBoolean(getPropertyValue(ThreadSafeOption.PROPERTY_NAME, "true")));

        setAccumulateNullPropagation(Boolean.parseBoolean(getPropertyValue(AccumulateNullPropagationOption.PROPERTY_NAME, "false")));

        setForceEagerActivationFilter(ForceEagerActivationOption.resolve( getPropertyValue( ForceEagerActivationOption.PROPERTY_NAME, "false" ) ).getFilter());

        setTimedRuleExecutionFilter(TimedRuleExecutionOption.resolve( getPropertyValue( TimedRuleExecutionOption.PROPERTY_NAME, "false" ) ).getFilter());

        setBeliefSystemType( BeliefSystemType.resolveBeliefSystemType( getPropertyValue( BeliefSystemTypeOption.PROPERTY_NAME, BeliefSystemType.SIMPLE.getId() ) ) );

        setQueryListenerOption( QueryListenerOption.determineQueryListenerClassOption( getPropertyValue( QueryListenerOption.PROPERTY_NAME, QueryListenerOption.STANDARD.getAsString() ) ) );
    }

    public void setDirectFiring(boolean directFiring) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.directFiring = directFiring;
    }

    public boolean isDirectFiring() {
        return this.directFiring;
    }

    public void setThreadSafe(boolean threadSafe) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.threadSafe = threadSafe;
    }

    public boolean isThreadSafe() {
        return this.threadSafe;
    }

    public void setAccumulateNullPropagation(boolean accumulateNullPropagation) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.accumulateNullPropagation = accumulateNullPropagation;
    }

    public boolean isAccumulateNullPropagation() {
        return this.accumulateNullPropagation;
    }

    public void setForceEagerActivationFilter(ForceEagerActivationFilter forceEagerActivationFilter) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.forceEagerActivationFilter = forceEagerActivationFilter;
    }

    public ForceEagerActivationFilter getForceEagerActivationFilter() {
        return this.forceEagerActivationFilter;
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

    public QueryListenerOption getQueryListenerOption() {
        return this.queryListener;
    }

    public void setQueryListenerOption( QueryListenerOption queryListener ) {
        checkCanChange();
        this.queryListener = queryListener;
    }


    public final <T extends KieSessionOption> void setOption(T option) {
        switch (option.propertyName()) {
            case DirectFiringOption.PROPERTY_NAME: {
                setDirectFiring(((DirectFiringOption) option).isDirectFiring());
                break;
            }
            case ThreadSafeOption.PROPERTY_NAME: {
                setThreadSafe(((ThreadSafeOption) option).isThreadSafe());
                break;
            }
            case AccumulateNullPropagationOption.PROPERTY_NAME: {
                setAccumulateNullPropagation(((AccumulateNullPropagationOption) option).isAccumulateNullPropagation());
                break;
            }
            case ForceEagerActivationOption.PROPERTY_NAME: {
                setForceEagerActivationFilter(((ForceEagerActivationOption) option).getFilter());
                break;
            }
            case TimedRuleExecutionOption.PROPERTY_NAME: {
                setTimedRuleExecutionFilter(((TimedRuleExecutionOption) option).getFilter());
                break;
            }
            case QueryListenerOption.PROPERTY_NAME: {
                setQueryListenerOption((QueryListenerOption) option);
                break;
            }
            case BeliefSystemTypeOption.PROPERTY_NAME: {
                setBeliefSystemType(((BeliefSystemType.resolveBeliefSystemType(((BeliefSystemTypeOption) option).getBeliefSystemType()))));
                break;
            }
            default:
                compConfig.setOption(option);
        }
    }

    @SuppressWarnings("unchecked")
    public final <T extends SingleValueKieSessionOption> T getOption(OptionKey<T> option) {
        switch (option.name()) {
            case DirectFiringOption.PROPERTY_NAME: {
                return (T) (isDirectFiring() ? DirectFiringOption.YES : DirectFiringOption.NO);
            }
            case ThreadSafeOption.PROPERTY_NAME: {
                return (T) (isThreadSafe() ? ThreadSafeOption.YES : ThreadSafeOption.NO);
            }
            case AccumulateNullPropagationOption.PROPERTY_NAME: {
                return (T) (isAccumulateNullPropagation() ? AccumulateNullPropagationOption.YES : AccumulateNullPropagationOption.NO);
            }
            case QueryListenerOption.PROPERTY_NAME: {
                return (T) getQueryListenerOption();
            }
            case BeliefSystemTypeOption.PROPERTY_NAME: {
                return (T) BeliefSystemTypeOption.get( this.getBeliefSystemType().getId() );
            }
            default:
                return compConfig.getOption(option);
        }
    }

    public final <T extends MultiValueKieSessionOption> T getOption(OptionKey<T> option, String subKey) {
        return compConfig.getOption(option, subKey);
    }

    @Override
    public <C extends MultiValueKieSessionOption> Set<String> getOptionSubKeys(OptionKey<C> optionKey) {
        return compConfig.getOptionSubKeys(optionKey);
    }

    @Override
    public boolean setInternalProperty(String name, String value) {
        switch(name) {
            case DirectFiringOption.PROPERTY_NAME: {
                setDirectFiring(!StringUtils.isEmpty(value) && Boolean.parseBoolean(value));
                break;
            }
            case ThreadSafeOption.PROPERTY_NAME: {
                setThreadSafe(StringUtils.isEmpty(value) || Boolean.parseBoolean(value));
                break;
            }
            case AccumulateNullPropagationOption.PROPERTY_NAME: {
                setAccumulateNullPropagation(!StringUtils.isEmpty(value) && Boolean.parseBoolean(value));
                break;
            }
            case ForceEagerActivationOption.PROPERTY_NAME: {
                setForceEagerActivationFilter(ForceEagerActivationOption.resolve(StringUtils.isEmpty(value) ? "false" : value).getFilter());
                break;
            }
            case TimedRuleExecutionOption.PROPERTY_NAME: {
                setTimedRuleExecutionFilter(TimedRuleExecutionOption.resolve(StringUtils.isEmpty(value) ? "false" : value).getFilter());
                break;
            }
            case QueryListenerOption.PROPERTY_NAME: {
                String property = StringUtils.isEmpty(value) ? QueryListenerOption.STANDARD.getAsString() : value;
                setQueryListenerOption(QueryListenerOption.determineQueryListenerClassOption(property));
                break;
            }
            case BeliefSystemTypeOption.PROPERTY_NAME: {
                setBeliefSystemType(StringUtils.isEmpty(value) ? BeliefSystemType.SIMPLE : BeliefSystemType.resolveBeliefSystemType(value));
                break;
            } default: {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getInternalProperty(String name) {
        switch(name) {
            case DirectFiringOption.PROPERTY_NAME: {
                return Boolean.toString(isDirectFiring());
            } case ThreadSafeOption.PROPERTY_NAME: {
                return Boolean.toString(isThreadSafe());
            } case AccumulateNullPropagationOption.PROPERTY_NAME: {
                return Boolean.toString(isAccumulateNullPropagation());
            } case QueryListenerOption.PROPERTY_NAME: {
                return getQueryListenerOption().getAsString();
            } case BeliefSystemTypeOption.PROPERTY_NAME: {
                return getBeliefSystemType().getId();
            }
        }

        return null;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RuleSessionConfiguration that = (RuleSessionConfiguration) o;

        return getBeliefSystemType() == that.getBeliefSystemType();
    }

    @Override
    public final int hashCode() {
        return getBeliefSystemType().hashCode();
    }
}
