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
package org.kie.kogito.index.postgresql.query;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.jpa.query.AbstractProcessDefinitionEntityQueryIT;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import static java.util.Collections.singletonList;
import static org.kie.kogito.index.json.JsonUtils.jsonFilter;
import static org.kie.kogito.index.test.QueryTestUtils.assertNoKey;
import static org.kie.kogito.index.test.QueryTestUtils.assertWithKey;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.contains;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;

@QuarkusTest
@QuarkusTestResource(PostgreSqlQuarkusTestResource.class)
class ProcessDefinitionEntityQueryIT extends AbstractProcessDefinitionEntityQueryIT {

    @Test
    void testMetadata() {
        final String processId = "persons";
        final String version = "1.0";
        ProcessDefinitionKey key = new ProcessDefinitionKey(processId, version);
        ProcessDefinition definitionEvent = TestUtils.createProcessDefinition(processId, version, Set.of());
        definitionEvent.setMetadata(Map.of("name", "Javierito", "hobbies", List.of("community", "first")));
        Storage<ProcessDefinitionKey, ProcessDefinition> storage = getStorage();
        storage.put(key, definitionEvent);
        queryAndAssert(assertWithKey(), storage, singletonList(jsonFilter(equalTo("metadata.name", "Javierito"))), null, null, null, key);
        queryAndAssert(assertNoKey(), storage, singletonList(jsonFilter(equalTo("metadata.name", "Fulanito"))), null, null, null, key);
        queryAndAssert(assertWithKey(), storage, singletonList(jsonFilter(contains("metadata.hobbies", "community"))), null, null, null, key);
        queryAndAssert(assertNoKey(), storage, singletonList(jsonFilter(contains("metadata.hobbies", "commercial"))), null, null, null, key);
    }
}
