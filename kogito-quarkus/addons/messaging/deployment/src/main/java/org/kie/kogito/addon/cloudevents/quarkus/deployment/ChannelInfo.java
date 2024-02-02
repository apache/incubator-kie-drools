/*
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
package org.kie.kogito.addon.cloudevents.quarkus.deployment;

import java.util.Collection;
import java.util.Optional;

public class ChannelInfo {

    private final String channelName;
    private final String className;
    private final Collection<String> triggers;

    private final boolean isInput;
    private final boolean isDefault;

    private final Optional<CloudEventMode> cloudEventMode;

    private final Optional<String> marshaller;
    private final Optional<OnOverflowInfo> onOverflow;

    protected ChannelInfo(String channelName, Collection<String> triggers, String className, boolean isInput, boolean isDefault, Optional<String> marshaller, Optional<OnOverflowInfo> onOverflow,
            Optional<CloudEventMode> cloudEventMode) {
        this.className = className;
        this.channelName = channelName;
        this.isInput = isInput;
        this.isDefault = isDefault;
        this.triggers = triggers;
        this.marshaller = marshaller;
        this.onOverflow = onOverflow;
        this.cloudEventMode = cloudEventMode;
    }

    public Collection<String> getTriggers() {
        return triggers;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getClassName() {
        return className;
    }

    public boolean isInput() {
        return isInput;
    }

    @Override
    public int hashCode() {
        return channelName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ChannelInfo))
            return false;
        return channelName.equals(((ChannelInfo) obj).getChannelName());
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isInputDefault() {
        return isInput && isDefault;
    }

    public boolean isOutputDefault() {
        return !isInput && isDefault;
    }

    public Optional<String> getMarshaller() {
        return marshaller;
    }

    public Optional<OnOverflowInfo> getOnOverflow() {
        return onOverflow;
    }

    public Optional<CloudEventMode> getCloudEventMode() {
        return cloudEventMode;
    }

    @Override
    public String toString() {
        return "ChannelInfo [channelName=" + channelName + ", className=" + className + ", triggers=" + triggers
                + ", isInput=" + isInput + ", isDefault=" + isDefault + ", cloudEventMode=" + cloudEventMode
                + ", marshaller=" + marshaller + ", onOverflow=" + onOverflow + "]";
    }
}
