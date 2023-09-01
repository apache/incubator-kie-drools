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
package org.kie.internal.conf;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.kie.api.PropertiesConfiguration;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.MultiValueOption;
import org.kie.api.conf.Option;
import org.kie.api.conf.OptionKey;
import org.kie.api.conf.OptionsConfiguration;
import org.kie.api.conf.SingleValueOption;
import org.kie.internal.utils.ChainedProperties;

public class CompositeConfiguration<T extends Option, S extends SingleValueOption, M extends MultiValueOption>
        implements OptionsConfiguration<T, S, M>, Externalizable {

    private ClassLoader classLoader;

    private ChainedProperties chainedProperties;

    private Map<String, OptionsConfiguration<T, S, M>> configurations = new HashMap<>();

    public CompositeConfiguration(ChainedProperties chainedProperties, ClassLoader classloader, ConfigurationFactory<T, S, M>... factories) {
        setClassLoader(classloader);

        this.chainedProperties = chainedProperties;

        for (ConfigurationFactory<T, S, M> f  : factories) {
            configurations.put(f.type(), f.create(this, classloader, chainedProperties));
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // avoid serializing user defined system properties
        chainedProperties.filterDroolsPropertiesForSerialization();
        out.writeObject(chainedProperties);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        chainedProperties = (ChainedProperties) in.readObject();
    }

    @Override
    public void makeImmutable() {
        configurations.values().stream().forEach(conf -> conf.makeImmutable());
    }

    @Override
    public <C extends T> void setOption(C option) {
        OptionsConfiguration delegate = configurations.get(option.type());
        if (delegate==null) {
            throw new RuntimeException("Configuration for type " + option.type() + " does not exist");
        }
        delegate.setOption(option);
    }

    @Override
    public <C extends S> C getOption(OptionKey<C> optionKey) {
        OptionsConfiguration<T, S, M> delegate = configurations.get(optionKey.type());
        if (delegate==null) {
            throw new RuntimeException("Configuration for type " + optionKey.type() + " does not exist");
        }
        return delegate.getOption(optionKey);
    }

    @Override
    public <C extends M> C getOption(OptionKey<C> optionKey, String subKey) {
        OptionsConfiguration<T, S, M> delegate = configurations.get(optionKey.type());
        if (delegate==null) {
            throw new RuntimeException("Configuration for type " + optionKey.type() + " does not exist");
        }
        return delegate.getOption(optionKey, subKey);
    }

    @Override
    public <C extends M> Set<String> getOptionSubKeys(OptionKey<C> optionKey) {
        OptionsConfiguration<T, S, M> delegate = configurations.get(optionKey.type());
        if (delegate==null) {
            throw new RuntimeException("Configuration for type " + optionKey.type() + " does not exist");
        }
        return delegate.getOptionSubKeys(optionKey);
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ChainedProperties getProperties() {
        return chainedProperties;
    }

    public <X extends OptionsConfiguration<T, S, M>> X as(ConfigurationKey<X> configuration) {
        return (X) configurations.get(configuration.type());
    }


    @Override
    public boolean setProperty(String name, String value) {
        return setProperty(null, name, value);
    }

    @Override
    public String getProperty(String name) {
        return getProperty(null, name);
    }

    public boolean setProperty(PropertiesConfiguration exclude, String name, String value) {
        for(PropertiesConfiguration c : configurations.values()) {
            if (c != exclude) {
                if (((InternalPropertiesConfiguration)c).setInternalProperty(name, value)) {
                    return true;
                }
            }
        }

       // if the property was not intercepted above, just add it to the chained properties.
        Properties additionalProperty = new Properties();
        additionalProperty.setProperty(name, value);
        chainedProperties.addProperties(additionalProperty);

        return false;
    }

    public String getProperty(PropertiesConfiguration exclude, String name) {
        for (PropertiesConfiguration c : configurations.values()) {
            if (c != exclude) {
                String value = ((InternalPropertiesConfiguration)c).getInternalProperty(name);
                if (value != null) {
                    return value;
                }
            }
        }

        return null;
    }
}
