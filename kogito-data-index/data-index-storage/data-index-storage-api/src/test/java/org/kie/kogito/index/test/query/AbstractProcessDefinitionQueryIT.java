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
package org.kie.kogito.index.test.query;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.test.QueryTestBase;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.SortDirection;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.contains;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAll;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAny;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.in;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.isNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.notNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;

public abstract class AbstractProcessDefinitionQueryIT extends QueryTestBase<ProcessDefinitionKey, ProcessDefinition> {

    public abstract Storage<ProcessDefinitionKey, ProcessDefinition> getStorage();

    @Test
    void testProcessDefinitionQuery() {
        String processId = "travels";
        ProcessDefinition pdv1 = TestUtils.createProcessDefinition(processId, "1.0", Set.of("admin", "kogito"));
        Storage<ProcessDefinitionKey, ProcessDefinition> storage = getStorage();
        ProcessDefinitionKey pdv1Key = new ProcessDefinitionKey(pdv1.getId(), pdv1.getVersion());
        storage.put(pdv1Key, pdv1);
        ProcessDefinition pdv2 = TestUtils.createProcessDefinition(processId, "2.0", Set.of("kogito"));
        ProcessDefinitionKey pdv2Key = new ProcessDefinitionKey(pdv2.getId(), pdv2.getVersion());
        storage.put(pdv2Key, pdv2);

        queryAndAssert(assertWithKey(), storage, singletonList(isNull("type")), null, null, null, pdv1Key, pdv2Key);
        queryAndAssert(assertWithKey(), storage, singletonList(notNull("version")), null, null, null, pdv1Key, pdv2Key);
        queryAndAssert(assertWithKey(), storage, singletonList(equalTo("version", pdv1.getVersion())), null, null, null, pdv1Key);
        queryAndAssert(assertWithKey(), storage, singletonList(contains("roles", "admin")), null, null, null, pdv1Key);
        queryAndAssert(assertWithKey(), storage, singletonList(containsAny("roles", asList("admin", "kogito"))), null, null, null, pdv1Key, pdv2Key);
        queryAndAssert(assertWithKey(), storage, singletonList(containsAll("roles", asList("admin", "kogito"))), null, null, null, pdv1Key);
        queryAndAssert(assertWithKey(), storage, asList(in("id", asList(pdv1.getId(), pdv2.getId())),
                in("version", asList(pdv1.getVersion(), pdv2.getVersion()))),
                singletonList(orderBy("version", SortDirection.ASC)), 1, 1, pdv2Key);
        queryAndAssert(assertWithKey(), storage, null, singletonList(orderBy("version", SortDirection.DESC)), null,
                null, pdv2Key, pdv1Key);
    }

    public static <V> BiConsumer<List<V>, ProcessDefinitionKey[]> assertWithKey() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting("id", "version").map(Tuple::toArray)
                .map(objs -> new ProcessDefinitionKey((String) objs[0], (String) objs[1])).containsExactly(ids);
    }

}
