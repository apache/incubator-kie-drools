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

import org.drools.util.StringUtils;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.MBeansOption;
import org.kie.api.conf.MultiValueKieBaseOption;
import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueKieBaseOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RuleBaseConfiguration
 *
 * A class to store RuleBase related configuration. It must be used at rule base instantiation time
 * or not used at all.
 * This class will automatically load default values from system properties, so if you want to set
 * a default configuration value for all your new rule bases, you can simply set the property as
 * a System property.
 *
 * After RuleBase is created, it makes the configuration immutable and there is no way to make it
 * mutable again. This is to avoid inconsistent behavior inside rulebase.
 *
 * NOTE: This API is under review and may change in the future.
 */

/**
 * Available configuration options:
 * <pre>
 * drools.maintainTms = &lt;true|false&gt;
 * drools.sequential = &lt;true|false&gt;
 * drools.sequential.agenda = &lt;sequential|dynamic&gt;
 * drools.removeIdentities = &lt;true|false&gt;
 * drools.shareAlphaNodes  = &lt;true|false&gt;
 * drools.shareBetaNodes = &lt;true|false&gt;
 * drools.alphaNodeHashingThreshold = &lt;1...n&gt;
 * drools.alphaNodeRangeIndexThreshold = &lt;1...n&gt;
 * drools.betaNodeRangeIndexEnabled = &lt;true|false&gt;
 * drools.sessionPool = &lt;1...n&gt;
 * drools.compositeKeyDepth = &lt;1..3&gt;
 * drools.indexLeftBetaMemory = &lt;true/false&gt;
 * drools.indexRightBetaMemory = &lt;true/false&gt;
 * drools.equalityBehavior = &lt;identity|equality&gt;
 * drools.conflictResolver = &lt;qualified class name&gt;
 * drools.consequenceExceptionHandler = &lt;qualified class name&gt;
 * drools.ruleBaseUpdateHandler = &lt;qualified class name&gt;
 * drools.sessionClock = &lt;qualified class name&gt;
 * drools.mbeans = &lt;enabled|disabled&gt;
 * drools.classLoaderCacheEnabled = &lt;true|false&gt;
 * drools.declarativeAgendaEnabled =  &lt;true|false&gt;
 * drools.permgenThreshold = &lt;1...n&gt;
 * drools.jittingThreshold = &lt;1...n&gt;
 * </pre>
 */
public class KieBaseConfigurationImpl extends BaseConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>
    implements
    KieBaseConfiguration,
    Externalizable {
    private static final long serialVersionUID = 510l;

    protected static final transient Logger logger = LoggerFactory.getLogger(KieBaseConfigurationImpl.class);

    public static final ConfigurationKey<KieBaseConfigurationImpl> KEY = new ConfigurationKey<>("Base");

    private boolean mutabilityEnabled;

    // this property activates MBean monitoring and management
    private boolean mbeansEnabled;

    /**
     * A constructor that sets the classloader to be used as the parent classloader
     * of this rule base classloader, and the properties to be used
     * as base configuration options
     *
     * @param compConfig
     */
    public KieBaseConfigurationImpl(CompositeConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> compConfig) {
        super(compConfig);
        init();
    }

    private void init() {
        setMBeansEnabled( MBeansOption.isEnabled( getPropertyValue( MBeansOption.PROPERTY_NAME, "disabled" ) ) );

        setMutabilityEnabled( KieBaseMutabilityOption.determineMutability(
                getPropertyValue( KieBaseMutabilityOption.PROPERTY_NAME, "ALLOWED" )) == KieBaseMutabilityOption.ALLOWED );
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeBoolean(mutabilityEnabled);
    }

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
        mutabilityEnabled = in.readBoolean();
    }
    public boolean setInternalProperty(String name, String value) {
        switch(name) {
            case MBeansOption.PROPERTY_NAME: {
                setMBeansEnabled( MBeansOption.isEnabled(value));
                break;
            }
            case KieBaseMutabilityOption.PROPERTY_NAME: {
                setMutabilityEnabled( StringUtils.isEmpty( value ) ? true : KieBaseMutabilityOption.determineMutability(value) == KieBaseMutabilityOption.ALLOWED );
                break;
            } default: {
                return false;
            }
        }

        return true;
    }

    public String getInternalProperty(String name) {
        switch(name) {
            case MBeansOption.PROPERTY_NAME: {
                return isMBeansEnabled() ? "enabled" : "disabled";
            }
            case KieBaseMutabilityOption.PROPERTY_NAME: {
                return isMutabilityEnabled() ? "ALLOWED" : "DISABLED";
            }
        }

        return null;
    }

    /**
     * Defines if the RuleBase should expose management and monitoring MBeans
     *
     * @param mbeansEnabled true for multi-thread or
     *                     false for single-thread. Default is false.
     */
    public void setMBeansEnabled(boolean mbeansEnabled) {
        checkCanChange();
        this.mbeansEnabled = mbeansEnabled;
    }

    /**
     * Returns true if the management and monitoring through MBeans is active 
     *
     * @return
     */
    public boolean isMBeansEnabled() {
        return this.mbeansEnabled;
    }

    public void setMutabilityEnabled( boolean mutabilityEnabled ) {
        this.mutabilityEnabled = mutabilityEnabled;
    }

    public boolean isMutabilityEnabled() {
        return mutabilityEnabled;
    }


    @SuppressWarnings("unchecked")
    public <T extends SingleValueKieBaseOption> T getOption(OptionKey<T> option) {
        switch(option.name()) {
            case MBeansOption.PROPERTY_NAME: {
                return (T) (this.isMBeansEnabled() ? MBeansOption.ENABLED : MBeansOption.DISABLED);
            }
            case KieBaseMutabilityOption.PROPERTY_NAME: {
                return (T) (this.isMutabilityEnabled() ? KieBaseMutabilityOption.ALLOWED : KieBaseMutabilityOption.DISABLED);
            }
            default:
                return compConfig.getOption(option);
        }
    }

    public void setOption(KieBaseOption option) {
        switch(option.getPropertyName()) {
            case MBeansOption.PROPERTY_NAME: {
                setMBeansEnabled( ( (MBeansOption) option ).isEnabled());
                break;
            }
            case KieBaseMutabilityOption.PROPERTY_NAME: {
                setMutabilityEnabled(option == KieBaseMutabilityOption.ALLOWED);
                break;
            }
            default:
                compConfig.setOption(option);
        }
    }

    @Override public <C extends MultiValueKieBaseOption> C getOption(OptionKey<C> optionKey, String subKey) {
        return compConfig.getOption(optionKey, subKey);
    }
}
