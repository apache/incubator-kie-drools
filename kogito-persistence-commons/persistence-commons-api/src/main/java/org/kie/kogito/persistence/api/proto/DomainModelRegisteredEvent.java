/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.persistence.api.proto;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DomainModelRegisteredEvent that = (DomainModelRegisteredEvent) o;
        return Objects.equals(processId, that.processId) &&
                Objects.equals(domainDescriptor, that.domainDescriptor) &&
                Objects.equals(additionalTypes, that.additionalTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId, domainDescriptor, additionalTypes);
    }
}
