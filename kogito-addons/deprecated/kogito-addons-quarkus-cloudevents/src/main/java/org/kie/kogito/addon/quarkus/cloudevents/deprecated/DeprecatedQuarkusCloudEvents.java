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
package org.kie.kogito.addon.quarkus.cloudevents.deprecated;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated The library kogito-addons-quarkus-cloudevents is deprecated and will be removed in a future release!. The new name for this addon is kogito-addons-quarkus-messaging
 */
@ApplicationScoped
@Deprecated
public class DeprecatedQuarkusCloudEvents {

    public static final Logger LOGGER = LoggerFactory.getLogger(DeprecatedQuarkusCloudEvents.class);

    public DeprecatedQuarkusCloudEvents() {
        LOGGER.warn(
                "@deprecated The library kogito-addons-quarkus-cloudevents is deprecated and will be removed in a future release!. The new name for this addon is kogito-addons-quarkus-messaging");
    }

}
