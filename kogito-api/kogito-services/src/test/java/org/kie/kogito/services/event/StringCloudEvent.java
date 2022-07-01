/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;

import org.kie.kogito.event.process.ProcessDataEvent;

public class StringCloudEvent extends ProcessDataEvent<String> {

    public StringCloudEvent() {
    }

    public StringCloudEvent(String dummyEvent, String type) {
        this(dummyEvent, type, null);
    }

    public StringCloudEvent(String dummyEvent, String type, String source) {
        this(dummyEvent, type, source, null);
    }

    public StringCloudEvent(String dummyEvent, String type, String source, String referenceId) {
        super(type, Optional.ofNullable(source).orElse(StringCloudEvent.class.getSimpleName()), dummyEvent, "1", "1", "1", "1", "1", "1", "1", null, referenceId);
    }
}
