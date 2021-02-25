/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.services.event;

import java.util.Objects;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.event.AbstractDataEvent;

/**
 * Small utility class to format DataEvents attributes from a given {@link ProcessInstance}
 */
public final class DataEventAttrBuilder {

    private DataEventAttrBuilder() {

    }

    public static String toSource(final ProcessInstance process) {
        Objects.requireNonNull(process);
        return String.format(AbstractDataEvent.SOURCE_FORMAT, process.getProcessId().toLowerCase());
    }

    public static String toSource(final String processId) {
        Objects.requireNonNull(processId);
        return String.format(AbstractDataEvent.SOURCE_FORMAT, processId.toLowerCase());
    }

    public static String toType(final String channelName, final ProcessInstance process) {
        Objects.requireNonNull(process);
        Objects.requireNonNull(channelName);
        return String.format(AbstractDataEvent.TYPE_FORMAT, process.getProcessId().toLowerCase(), channelName.toLowerCase());
    }

    public static String toType(final String channelName, final String processId) {
        Objects.requireNonNull(processId);
        Objects.requireNonNull(channelName);
        return String.format(AbstractDataEvent.TYPE_FORMAT, processId.toLowerCase(), channelName.toLowerCase());
    }

}
