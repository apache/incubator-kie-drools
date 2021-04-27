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
package org.kie.kogito.addon.cloudevents.quarkus.http;

import io.cloudevents.CloudEvent;

public class CloudEventInvalidDataException extends Exception {

    private static final String MESSAGE_FORMAT = "CloudEvent '%s' data is not a valid JSON.";
    private final CloudEvent event;

    public CloudEventInvalidDataException(final CloudEvent event, final Throwable cause) {
        super(String.format(MESSAGE_FORMAT, event.getType()), cause);
        this.event = event;
    }

    public CloudEvent getEvent() {
        return event;
    }

}
