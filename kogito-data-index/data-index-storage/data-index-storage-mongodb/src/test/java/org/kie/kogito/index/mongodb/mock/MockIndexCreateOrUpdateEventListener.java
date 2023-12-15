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
package org.kie.kogito.index.mongodb.mock;

import java.util.List;

import org.kie.kogito.persistence.mongodb.index.IndexCreateOrUpdateEvent;
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
public class MockIndexCreateOrUpdateEventListener {

    List<String> collections = mock(List.class);

    List<String> indexes = mock(List.class);

    public void reset() {
        Mockito.reset(collections, indexes);
    }

    public void onIndexCreateOrUpdateEvent(@Observes IndexCreateOrUpdateEvent event) {
        this.collections.add(event.getCollection());
        this.indexes.add(event.getIndex());
    }

    public void assertFire(String collection, String index) {
        verify(collections, times(1)).add(eq(collection));
        verify(indexes, times(1)).add(eq(index));
    }
}
