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
package org.kie.kogito.addons.quarkus.knative.eventing.deployment;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(prefix = "org.kie.kogito.addons.knative", name = "eventing", phase = ConfigPhase.BUILD_TIME)
public class EventingConfiguration {

    /**
     * Name of the default Knative Broker deployed in the target Knative namespace.
     * <p>
     * This broker is used as the reference to create the Knative Triggers responsible
     * to delegate the events that this Kogito service will consume.
     */
    @ConfigItem(defaultValue = "default")
    String broker;

    /**
     * Whether the extension should generate a default Knative Broker in memory to sink and dispatch the messages.
     * <p>
     * Turn this property to `false` in case you already have a broker installed in your namespace rather than the default one.
     * Note that you can use `org.kie.kogito.addons.knative.eventing.sink.*` to configure your custom Sink.
     * If not defined, this auto generated Broker will work as the Sink.
     */
    @ConfigItem(defaultValue = "true")
    Boolean autoGenerateBroker;

    /**
     * Whether to generate the Knative [KogitoSource](https://github.com/knative-sandbox/eventing-kogito) instead of a Knative SinkBinding.
     * <p>
     * KogitoSource is the custom SinkBinding created specifically to bound with the Kogito ecosystem such as Data Index,
     * Jobs Service, and so on.
     * <p>
     * For minimal configuration and setup, you can leave this property set to `false` (default).
     */
    @ConfigItem(defaultValue = "false")
    Boolean generateKogitoSource;

    SinkConfiguration sink;
}
