/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import org.kie.api.runtime.conf.TimedRuleExecutionOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.InternalPropertiesConfiguration;
import org.kie.internal.runtime.conf.ForceEagerActivationFilter;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;

public class RuleSessionConfiguration extends BaseConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> implements KieSessionConfiguration, InternalPropertiesConfiguration, Externalizable {

    public static final ConfigurationKey<RuleSessionConfiguration> KEY = new ConfigurationKey<>("Rule");

    private static final long              serialVersionUID = 510l;

    private ForceEagerActivationFilter     forceEagerActivationFilter;

    private BeliefSystemType               beliefSystemType;

    private AccumulateNullPropagationOption accumulateNullPropagation;
    
    private DirectFiringOption directFiringOption;
    
    private QueryListenerOption queryListenerOption;
    
    private ThreadSafeOption threadSafeOption;
    
    private TimedRuleExecutionOption timedRuleExecutionOption;

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject( queryListenerOption );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        queryListenerOption = (QueryListenerOption) in.readObject();
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
        setAccumulateNullPropagation(AccumulateNullPropagationOption.resolve(getPropertyValue(AccumulateNullPropagationOption.PROPERTY_NAME, "false")));

        setBeliefSystemType(BeliefSystemType.resolveBeliefSystemType( getPropertyValue( BeliefSystemTypeOption.PROPERTY_NAME, BeliefSystemType.SIMPLE.getId() ) ) );

    	setDirectFiringOption(DirectFiringOption.resolve(getPropertyValue(DirectFiringOption.PROPERTY_NAME, "false")));

        setForceEagerActivationFilter(ForceEagerActivationOption.resolve( getPropertyValue( ForceEagerActivationOption.PROPERTY_NAME, "false" ) ).getFilter());

        setQueryListenerOption(QueryListenerOption.determineQueryListenerClassOption( getPropertyValue( QueryListenerOption.PROPERTY_NAME, QueryListenerOption.STANDARD.getAsString() ) ) );

        setThreadSafeOption(ThreadSafeOption.resolve(getPropertyValue(ThreadSafeOption.PROPERTY_NAME, "true")));

        setTimedRuleExecutionFilter(TimedRuleExecutionOption.resolve( getPropertyValue( TimedRuleExecutionOption.PROPERTY_NAME, "false" ) ));
    }

    private void setDirectFiringOption(DirectFiringOption directFiringOption) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.directFiringOption = directFiringOption;
    }

    private boolean isDirectFiring() {
        return directFiringOption.isDirectFiring();
    }

    private void setAccumulateNullPropagation(AccumulateNullPropagationOption accumulateNullPropagation) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.accumulateNullPropagation = accumulateNullPropagation;
    }

    private void setQueryListenerOption( QueryListenerOption queryListener ) {
        checkCanChange();
        this.queryListenerOption = queryListener;
    }

    private void setTimedRuleExecutionFilter(TimedRuleExecutionOption timedRuleExecutionOption) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.timedRuleExecutionOption = timedRuleExecutionOption;
    }
    
    private void setForceEagerActivationFilter(ForceEagerActivationFilter forceEagerActivationFilter) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.forceEagerActivationFilter = forceEagerActivationFilter;
    }

    public ForceEagerActivationFilter getForceEagerActivationFilter() {
        return this.forceEagerActivationFilter;
    }

    public BeliefSystemType getBeliefSystemType() {
        return this.beliefSystemType;
    }

    public void setBeliefSystemType(BeliefSystemType beliefSystemType) {
        checkCanChange(); // throws an exception if a change isn't possible;
        this.beliefSystemType = beliefSystemType;
    }


    public final <T extends KieSessionOption> void setOption(T option) {
        switch (option.propertyName()) {
	        case AccumulateNullPropagationOption.PROPERTY_NAME: {
	            setAccumulateNullPropagation(((AccumulateNullPropagationOption) option));
	            break;
	        }
            case BeliefSystemTypeOption.PROPERTY_NAME: {
                setBeliefSystemType(((BeliefSystemType.resolveBeliefSystemType(((BeliefSystemTypeOption) option).getBeliefSystemType()))));
                break;
            }
            case DirectFiringOption.PROPERTY_NAME: {
                setDirectFiringOption(((DirectFiringOption) option));
                break;
            }
            case ForceEagerActivationOption.PROPERTY_NAME: {
                setForceEagerActivationFilter(((ForceEagerActivationOption) option).getFilter());
                break;
            }
            case QueryListenerOption.PROPERTY_NAME: {
                setQueryListenerOption((QueryListenerOption) option);
                break;
            }
            case ThreadSafeOption.PROPERTY_NAME: {
                setThreadSafeOption(((ThreadSafeOption) option));
                break;
            }
            case TimedRuleExecutionOption.PROPERTY_NAME: {
                setTimedRuleExecutionFilter(((TimedRuleExecutionOption) option));
                break;
            }
            default:
                compConfig.setOption(option);
        }
    }

    private void setThreadSafeOption(ThreadSafeOption threadSafeOption) {
    	checkCanChange();
		this.threadSafeOption = threadSafeOption;
		
	}

	@SuppressWarnings("unchecked")
    public final <T extends SingleValueKieSessionOption> T getOption(OptionKey<T> option) {
        switch (option.name()) {
	        case AccumulateNullPropagationOption.PROPERTY_NAME: {
	            return (T) accumulateNullPropagation;
	        }
            case BeliefSystemTypeOption.PROPERTY_NAME: {
                return (T) BeliefSystemTypeOption.get( this.getBeliefSystemType().getId() );
            }
            case DirectFiringOption.PROPERTY_NAME: {
                return (T) directFiringOption;
            }
            case QueryListenerOption.PROPERTY_NAME: {
                return (T) queryListenerOption;
            }
            case ThreadSafeOption.PROPERTY_NAME: {
                return (T) threadSafeOption;
            }
            case TimedRuleExecutionOption.PROPERTY_NAME: {
                return (T) timedRuleExecutionOption;
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
            	setDirectFiringOption(DirectFiringOption.resolve(value));
                break;
            }
            case ThreadSafeOption.PROPERTY_NAME: {
                setThreadSafeOption(ThreadSafeOption.resolve(value));
                break;
            }
            case AccumulateNullPropagationOption.PROPERTY_NAME: {
                setAccumulateNullPropagation(AccumulateNullPropagationOption.resolve(value));
                break;
            }
            case ForceEagerActivationOption.PROPERTY_NAME: {
                setForceEagerActivationFilter(ForceEagerActivationOption.resolve(StringUtils.isEmpty(value) ? "false" : value).getFilter());
                break;
            }
            case TimedRuleExecutionOption.PROPERTY_NAME: {
                setTimedRuleExecutionFilter(TimedRuleExecutionOption.resolve(StringUtils.isEmpty(value) ? "false" : value));
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
                return Boolean.toString(directFiringOption.isDirectFiring());
            } case ThreadSafeOption.PROPERTY_NAME: {
                return Boolean.toString(threadSafeOption.isThreadSafe());
            } case AccumulateNullPropagationOption.PROPERTY_NAME: {
                return Boolean.toString(accumulateNullPropagation.isAccumulateNullPropagation());
            } case QueryListenerOption.PROPERTY_NAME: {
                return this.queryListenerOption.getAsString();
            } case BeliefSystemTypeOption.PROPERTY_NAME: {
                return getBeliefSystemType().getId();
            }
        }

        return null;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleSessionConfiguration that = (RuleSessionConfiguration) o;

        return getBeliefSystemType() == that.getBeliefSystemType();
    }

    @Override
    public final int hashCode() {
        return getBeliefSystemType().hashCode();
    }
}
