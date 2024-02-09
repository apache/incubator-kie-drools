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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.base.base.CoreComponentsBuilder;
import org.drools.core.process.WorkItemManagerFactory;
import org.drools.core.util.ConfFileUtils;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.OptionKey;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.api.runtime.conf.WorkItemHandlerOption;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.conf.CompositeConfiguration;

public class FlowSessionConfiguration extends BaseConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> implements KieSessionConfiguration, Externalizable {

    private static final long              serialVersionUID = 510l;

    private Map<String, WorkItemHandler>   workItemHandlers;
    private WorkItemManagerFactory         workItemManagerFactory;

    public static final ConfigurationKey<FlowSessionConfiguration> KEY = new ConfigurationKey<>("Flow");

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
    }

    public FlowSessionConfiguration(CompositeConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> compConfig) {
        super(compConfig);
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
        String content = ConfFileUtils.URLContentsToString(ConfFileUtils.getURL(location,
                                                                                null,
                                                                                RuleBaseConfiguration.class));
        Map<String, WorkItemHandler> workItemHandlers = (Map<String, WorkItemHandler>) CoreComponentsBuilder.get().getMVELExecutor().eval(content, params);
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
            clazz = (Class<WorkItemManagerFactory>) getClassLoader().loadClass( className );
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


    public final <T extends KieSessionOption> void setOption(T option) {
        switch (option.propertyName()) {
            case WorkItemHandlerOption.PROPERTY_NAME: {
                getWorkItemHandlers().put(((WorkItemHandlerOption) option).getName(),
                                          ((WorkItemHandlerOption) option).getHandler());
                break;
            }
            default:
                compConfig.setOption(option);
        }
    }

    @SuppressWarnings("unchecked")
    public final <T extends SingleValueKieSessionOption> T getOption(OptionKey<T> option) {
        return compConfig.getOption(option);
    }

    @SuppressWarnings("unchecked")
    public final <T extends MultiValueKieSessionOption> T getOption(OptionKey<T> option,
                                                                    String subKey) {
        switch(option.name()) {
            case WorkItemHandlerOption.PROPERTY_NAME: {
                return (T) WorkItemHandlerOption.get(subKey,
                                                     getWorkItemHandlers().get(subKey));
            }
            default:
                return compConfig.getOption(option, subKey);
        }
    }

    @Override public <C extends MultiValueKieSessionOption> Set<String> getOptionSubKeys(OptionKey<C> optionKey) {
        return getWorkItemHandlers().keySet();
    }

    public final boolean setInternalProperty(String name,
                                  String value) {
        return false;
    }

    public final String getInternalProperty(String name) {
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

        FlowSessionConfiguration that = (FlowSessionConfiguration) o;

        return getWorkItemHandlers().equals(that.getWorkItemHandlers());
    }

    @Override
    public final int hashCode() {
        int result = getWorkItemHandlers().hashCode();
        return result;
    }
}
