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
package org.kie.kogito.event.process;

import org.kie.kogito.event.AbstractDataEvent;

public class ProcessDataEvent<T> extends AbstractDataEvent<T> {

    public ProcessDataEvent() {
    }

    public ProcessDataEvent(T body) {
        setData(body);
    }

    public ProcessDataEvent(String type,
            String source,
            T body,
            String kogitoProcessInstanceId,
            String kogitoProcessInstanceVersion,
            String kogitoParentProcessInstanceId,
            String kogitoRootProcessInstanceId,
            String kogitoProcessId,
            String kogitoRootProcessId,
            String kogitoProcessInstanceState,
            String kogitoAddons,
            String kogitoProcessType,
            String kogitoReferenceId) {
        super(type,
                source,
                body,
                kogitoProcessInstanceId,
                kogitoRootProcessInstanceId,
                kogitoProcessId,
                kogitoRootProcessId,
                kogitoAddons);
        this.kogitoProcessInstanceVersion = kogitoProcessInstanceVersion;
        this.kogitoParentProcessInstanceId = kogitoParentProcessInstanceId;
        this.kogitoProcessInstanceState = kogitoProcessInstanceState;
        this.kogitoReferenceId = kogitoReferenceId;
        this.kogitoProcessType = kogitoProcessType;
    }

}
