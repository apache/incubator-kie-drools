/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates. 
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

package org.kie.kogito.index.event;

import java.util.List;

import org.kie.kogito.index.domain.DomainDescriptor;

public class DomainModelRegisteredEvent {

    private String processId;
    private DomainDescriptor domainDescriptor;
    private List<DomainDescriptor> additionalTypes;

    public DomainModelRegisteredEvent(String processId, DomainDescriptor domainDescriptor, List<DomainDescriptor> additionalTypes) {
        this.processId = processId;
        this.domainDescriptor = domainDescriptor;
        this.additionalTypes = additionalTypes;
    }

    public String getProcessId() {
        return processId;
    }

    public List<DomainDescriptor> getAdditionalTypes() {
        return additionalTypes;
    }

    public DomainDescriptor getDomainDescriptor() {
        return domainDescriptor;
    }

    @Override
    public String toString() {
        return "DomainModelRegisteredEvent{" +
                "processId='" + processId + '\'' +
                ", domainDescriptor=" + domainDescriptor +
                ", additionalTypes=" + additionalTypes +
                '}';
    }
}
