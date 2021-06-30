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
package org.kie.kogito.addons.springboot.events.deprecated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @deprecated The library kogito-event-driven-decisions-springboot-addon is deprecated and will be removed in a future release! Please use kogito-addons-springboot-events-decisions instead
 */
@Deprecated
@Component
@Order(0)
public class DeprecatedSpringBootEvents implements ApplicationListener<ApplicationReadyEvent> {

    public static final Logger LOGGER = LoggerFactory.getLogger(DeprecatedSpringBootEvents.class);

    public DeprecatedSpringBootEvents() {
        LOGGER.warn("The library kogito-event-driven-decisions-springboot-addon is deprecated and will be removed in a future release! Please use kogito-addons-springboot-events-decisions instead");
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        LOGGER.warn("The library kogito-event-driven-decisions-springboot-addon is deprecated and will be removed in a future release! Please use kogito-addons-springboot-events-decisions instead");
    }
}
