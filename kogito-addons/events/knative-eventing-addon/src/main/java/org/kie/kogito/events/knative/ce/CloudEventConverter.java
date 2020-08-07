/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.events.knative.ce;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;

/**
 * Simple utility class to convert from CloudEvents objects to a Json String.
 * Wraps invocation to the CE SDK, so we can safely change the inner implementation without impacting callers.
 */
public final class CloudEventConverter {

    private static final EventFormat format = EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE);

    private CloudEventConverter() {
    }

    public static String toJson(final CloudEvent cloudEvent) {
        return new String(format.serialize(cloudEvent));
    }

    public static CloudEvent toCloudEvent(final byte[] object) {
        return format.deserialize(object);
    }
}
