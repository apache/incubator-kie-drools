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
package org.kie.kogito.addons.quarkus.knative.eventing.deployment;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
@ConfigMapping(prefix = "org.kie.kogito.addons.knative.eventing")
public interface EventingConfiguration {

    /**
     * Name of the default Knative Broker deployed in the target Knative namespace.
     * <p>
     * This broker is used as the reference to create the Knative Triggers responsible
     * to delegate the events that this Kogito service will consume.
     */
    @WithDefault("default")
    String broker();

    /**
     * Whether the extension should generate a default Knative Broker in memory to sink and dispatch the messages.
     * <p>
     * Turn this property to `false` in case you already have a broker installed in your namespace rather than the default one.
     * Note that you can use `org.kie.kogito.addons.knative.eventing.sink.*` to configure your custom Sink.
     * If not defined, this auto generated Broker will work as the Sink.
     */
    @WithDefault("true")
    Boolean autoGenerateBroker();

    @WithName("sink")
    SinkConfiguration sinkConfig();
}
