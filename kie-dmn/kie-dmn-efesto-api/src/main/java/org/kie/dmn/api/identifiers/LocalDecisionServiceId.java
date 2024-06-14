/**
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
package org.kie.dmn.api.identifiers;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class LocalDecisionServiceId extends LocalUriId implements LocalId {
    public static final String PREFIX = "services";

    private final Id decisionId;
    private final String serviceId;

    public LocalDecisionServiceId(Id decisionId, String serviceId) {
        super(decisionId.toLocalId().asLocalUri().append(PREFIX).append(serviceId));
        LocalId localDecisionId = decisionId.toLocalId();
        if (!localDecisionId.toLocalId().asLocalUri().startsWith(LocalDecisionId.PREFIX)) {
            throw new IllegalArgumentException("Not a valid decision path"); // fixme use typed exception
        }
        this.decisionId = decisionId;
        this.serviceId = serviceId;
    }

    public Id decisionId() {
        return decisionId;
    }

    public String serviceId() {
        return serviceId;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

}
