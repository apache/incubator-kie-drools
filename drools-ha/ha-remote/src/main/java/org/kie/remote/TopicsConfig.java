/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.remote;

import java.util.Optional;

public class TopicsConfig {

    private String eventsTopicName;
    private String kieSessionInfosTopicName;

    public static TopicsConfig getDefaultTopicsConfig(){
        return anTopicsConfig().
                withKieSessionInfosTopicName(Optional.ofNullable(System.getenv(CommonConfig.DEFAULT_KIE_SESSION_INFOS_TOPIC)).orElse(CommonConfig.DEFAULT_KIE_SESSION_INFOS_TOPIC)).
                withEventsTopicName(Optional.ofNullable(System.getenv(CommonConfig.DEFAULT_EVENTS_TOPIC)).orElse(CommonConfig.DEFAULT_EVENTS_TOPIC)).build();
    }

    private TopicsConfig() { }

    public static TopicsConfig anTopicsConfig() { return new TopicsConfig(); }


    public TopicsConfig withEventsTopicName(String eventsTopicName) {
        this.eventsTopicName = eventsTopicName;
        return this;
    }

    public TopicsConfig withKieSessionInfosTopicName(String kieSessionInfosTopicName) {
        this.kieSessionInfosTopicName = kieSessionInfosTopicName;
        return this;
    }

    public TopicsConfig build() {
        TopicsConfig topicsConfig = new TopicsConfig();
        topicsConfig.eventsTopicName = this.eventsTopicName;
        topicsConfig.kieSessionInfosTopicName = this.kieSessionInfosTopicName;
        return topicsConfig;
    }

    public String getEventsTopicName() { return eventsTopicName; }

    public String getKieSessionInfosTopicName() { return kieSessionInfosTopicName; }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TopicsConfig{");
        sb.append(", eventsTopicName='").append(eventsTopicName).append('\'');
        sb.append(", kieSessionInfosTopicName='").append(kieSessionInfosTopicName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
