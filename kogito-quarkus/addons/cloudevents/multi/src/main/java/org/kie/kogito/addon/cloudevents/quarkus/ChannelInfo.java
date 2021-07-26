/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.addon.cloudevents.quarkus;

import java.util.Objects;

public class ChannelInfo {

    private String beanName;
    private String channelName;

    public ChannelInfo(String beanName, String channelName) {
        this.beanName = beanName;
        this.channelName = channelName;

    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, channelName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ChannelInfo))
            return false;
        ChannelInfo other = (ChannelInfo) obj;
        return Objects.equals(beanName, other.beanName) && Objects.equals(channelName, other.channelName);
    }

    @Override
    public String toString() {
        return "ChannelInfo [beanName=" + beanName + ", channelName=" + channelName + "]";
    }
}
