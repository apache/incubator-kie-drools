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
package org.kie.kogito.event.cloudevents.extension;

import java.util.Optional;
import java.util.function.Consumer;

import io.cloudevents.CloudEventExtensions;

public class KogitoExtensionUtils {

    private KogitoExtensionUtils() {
    }

    public static void readStringExtension(CloudEventExtensions extensions, String key, Consumer<String> consumer) {
        Optional.ofNullable(extensions.getExtension(key))
                // there seems to be a bug in the cloudevents sdk so that, when a extension attributes is null,
                // it returns a "null" String instead of a real null object
                .filter(obj -> !("null".equals(obj)))
                .map(Object::toString)
                .ifPresent(consumer);
    }

    public static void readBooleanExtension(CloudEventExtensions extensions, String key, Consumer<Boolean> consumer) {
        Optional.ofNullable(extensions.getExtension(key))
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .ifPresent(consumer);
    }

}
