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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.util.ConfFileUtils;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.MultiValueKieBaseOption;
import org.kie.api.conf.OptionKey;
import org.kie.api.conf.SingleValueKieBaseOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FlowBaseConfiguration
 *
 * A class to store FlowBase related configuration. It must be used at flow base instantiation time
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
 * <pre>
 * </pre>
 */
public class FlowBaseConfiguration extends BaseConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>
    implements
    KieBaseConfiguration,
    Externalizable {

    public static final ConfigurationKey<FlowBaseConfiguration> KEY = new ConfigurationKey<>("Rule");

    private static final long serialVersionUID = 510l;

    protected static final Logger logger = LoggerFactory.getLogger(FlowBaseConfiguration.class);

    private List<Map<String, Object>> workDefinitions;


    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
    }


    /**
     * A constructor that sets the classloader to be used as the parent classloader
     * of this rule base classloaders, and the properties to be used
     * as base configuration options
     *
     */
    public FlowBaseConfiguration(CompositeConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> compConfig) {
        super(compConfig);
        initWorkDefinitions();
    }

    public List<Map<String, Object>> getWorkDefinitions() {
        if ( this.workDefinitions == null ) {
            initWorkDefinitions();
        }
        return this.workDefinitions;

    }

    private void initWorkDefinitions() {
        this.workDefinitions = new ArrayList<Map<String, Object>>();

        // split on each space
        String locations[] = getPropertyValue( "drools.workDefinitions",
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
                loadWorkItems( factoryLocation );
            }
        }
    }

    private void loadWorkItems(String location) {
        String content = ConfFileUtils.URLContentsToString( ConfFileUtils.getURL( location,
                                                                                  null,
                                                                                  FlowBaseConfiguration.class));
        try {
//            this.workDefinitions.addAll(
//                (List<Map<String, Object>>) MVELExecutor.get().eval( content, new HashMap() ) );
        } catch ( Throwable t ) {
            logger.error("Error occurred while loading work definitions " + location
                    + "\nContinuing without reading these work definitions", t);
            throw new RuntimeException( "Could not parse work definitions " + location + ": " + t.getMessage() );
        }
    }


    @Override
    public boolean setInternalProperty(String name, String value) {
        return false;
    }

    @Override
    public String getInternalProperty(String name) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleValueKieBaseOption> T getOption(OptionKey<T> option) {
        return compConfig.getOption(option);
    }

    public void setOption(KieBaseOption option) {
    }

}
