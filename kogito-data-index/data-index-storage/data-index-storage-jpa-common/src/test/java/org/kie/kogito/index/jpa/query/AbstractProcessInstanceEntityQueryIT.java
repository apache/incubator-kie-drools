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
package org.kie.kogito.index.jpa.query;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.index.jpa.storage.ProcessInstanceEntityStorage;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.index.test.query.AbstractProcessInstanceQueryIT;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.model.ProcessInstanceState.COMPLETED;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.in;

public abstract class AbstractProcessInstanceEntityQueryIT extends AbstractProcessInstanceQueryIT {

    @Inject
    ProcessInstanceEntityStorage storage;

    @Override
    public ProcessInstanceEntityStorage getStorage() {
        return storage;
    }

    @Test
    void testCount() {
        ProcessInstanceStateDataEvent processInstanceEvent = TestUtils.createProcessInstanceEvent(UUID.randomUUID().toString(), "counting", null, null, COMPLETED.ordinal());
        storage.indexState(processInstanceEvent);
        assertThat(storage.query().count()).isNotZero();
        assertThat(storage.query().filter(List.of(in("state", List.of(34)))).count()).isZero();
    }
}
