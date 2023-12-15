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
package org.kie.kogito.persistence.mongodb.mock;

import java.util.Map;

import org.kie.kogito.persistence.mongodb.index.ProcessIndexEvent;
import org.mockito.Mockito;

import io.quarkus.test.Mock;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Mock
@ApplicationScoped
public class MockProcessIndexEventListener {

    Map<String, String> mockProcessTypeMapper = mock(Map.class);

    public void reset() {
        Mockito.reset(mockProcessTypeMapper);
    }

    public void onIndexCreateOrUpdateEvent(@Observes ProcessIndexEvent event) {
        mockProcessTypeMapper.put(event.getProcessDescriptor().getProcessId(), event.getProcessDescriptor().getProcessType());
    }

    public void assertFire(String processId, String processType) {
        verify(mockProcessTypeMapper, times(1)).put(eq(processId), eq(processType));
    }
}
