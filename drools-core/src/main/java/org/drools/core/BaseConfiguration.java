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
import org.kie.api.PropertiesConfiguration;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.MultiValueOption;
import org.kie.api.conf.Option;
import org.kie.api.conf.OptionsConfiguration;
import org.kie.api.conf.SingleValueOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.InternalPropertiesConfiguration;
import org.kie.internal.utils.ChainedProperties;


public abstract class BaseConfiguration<T extends Option, S extends SingleValueOption, M extends MultiValueOption> implements InternalPropertiesConfiguration, PropertiesConfiguration, Externalizable {

    protected CompositeConfiguration<T, S, M> compConfig;

    private volatile boolean               immutable;

    public BaseConfiguration(CompositeConfiguration<T, S, M> compConfig) {
        this.compConfig = compConfig;
        this.immutable = false;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(compConfig);
        out.writeBoolean(immutable);
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        compConfig = (CompositeConfiguration<T, S, M>) in.readObject();
        immutable = in.readBoolean();
    }

    public final boolean setProperty(String name, String value) {
        name = name.trim();
        if ( StringUtils.isEmpty(name) ) {
            return false;
        }

        boolean set = setInternalProperty(name, value);

        if (!set) {
            set = compConfig.setProperty(this, name, value);
        }

        return set;
    }

    public final String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        }

        String value = getInternalProperty(name);

        if (value == null) {
            value = compConfig.getProperty(this, name);
        }

        return value;
    }

    /**
     * Makes the configuration object immutable. Once it becomes immutable,
     * there is no way to make it mutable again.
     * This is done to keep consistency.
     */
    public final void makeImmutable() {
        this.immutable = true;
    }

    /**
     * Returns true if this configuration object is immutable or false otherwise.
     */
    public final boolean isImmutable() {
        return this.immutable;
    }

    protected final void checkCanChange() {
        if ( this.immutable ) {
            throw new UnsupportedOperationException( "Can't set a property after configuration becomes immutable" );
        }
    }

    public final ClassLoader getClassLoader() {
        return compConfig.getClassLoader();
    }

    public final void setClassLoader(ClassLoader classLoader) {
        compConfig.setClassLoader(classLoader);
    }

    public final String getPropertyValue( String name, String defaultValue ) {
        return compConfig.getProperties().getProperty( name, defaultValue );
    }

    public final ChainedProperties getProperties() {
        return compConfig.getProperties();
    }

    public final ChainedProperties getChainedProperties() {
        return compConfig.getProperties();
    }

    public final <X extends OptionsConfiguration<T, S, M>> X as(ConfigurationKey<X> key) {
        return compConfig.as(key);
    }
}
