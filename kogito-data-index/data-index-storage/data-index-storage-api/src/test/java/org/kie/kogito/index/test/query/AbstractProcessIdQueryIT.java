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

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.test.QueryTestBase;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.SortDirection;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.kie.kogito.index.test.QueryTestUtils.assertWithString;
import static org.kie.kogito.index.test.QueryTestUtils.assertWithStringInOrder;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.and;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.contains;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAll;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.containsAny;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.in;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.like;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.notNull;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.or;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;

public abstract class AbstractProcessIdQueryIT extends QueryTestBase<String, String> {

    @Test
    void testProcessIdQuery() {
        String processId = "travels";
        String subProcessId = "travels_sub";
        String type1 = "org.acme.travels.travels";
        String type2 = "org.acme.travels";
        Storage<String, String> storage = getStorage();
        storage.put(processId, type1);
        storage.put(subProcessId, type2);

        queryAndAssert(assertWithString(), storage, singletonList(in("processId", asList(processId, subProcessId))), null, null, null, type1, type2);
        queryAndAssert(assertWithString(), storage, singletonList(equalTo("processId", processId)), null, null, null, type1);
        queryAndAssert(assertWithString(), storage, singletonList(notNull("processId")), null, null, null, type1, type2);
        queryAndAssert(assertWithString(), storage, singletonList(contains("fullTypeName", type1)), null, null, null, type1);
        queryAndAssert(assertWithString(), storage, singletonList(containsAny("fullTypeName", asList(type1, type2))), null, null, null, type1, type2);
        queryAndAssert(assertWithString(), storage, singletonList(containsAll("processId", asList(processId, subProcessId))), null, null, null);
        queryAndAssert(assertWithString(), storage, singletonList(like("processId", "*_sub")), null, null, null, type2);
        queryAndAssert(assertWithString(), storage, singletonList(and(asList(equalTo("processId", processId), equalTo("fullTypeName", type1)))), null, null, null, type1);
        queryAndAssert(assertWithString(), storage, singletonList(or(asList(equalTo("processId", processId), equalTo("fullTypeName", type2)))), null, null, null, type1, type2);
        queryAndAssert(assertWithString(), storage, asList(equalTo("processId", processId), equalTo("fullTypeName", type2)), null, null, null);

        queryAndAssert(assertWithStringInOrder(), storage, singletonList(in("processId", asList(processId, subProcessId))), singletonList(orderBy("fullTypeName", SortDirection.DESC)), 1, 1, type2);
        queryAndAssert(assertWithStringInOrder(), storage, null, singletonList(orderBy("fullTypeName", SortDirection.DESC)), 1, 1, type2);
        queryAndAssert(assertWithStringInOrder(), storage, null, asList(orderBy("fullTypeName", SortDirection.DESC), orderBy("processId", SortDirection.DESC)), null, null, type1, type2);
        queryAndAssert(assertWithStringInOrder(), storage, null, null, 1, 1, type2);
    }
}
