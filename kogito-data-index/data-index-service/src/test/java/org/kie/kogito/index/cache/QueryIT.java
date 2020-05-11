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

package org.kie.kogito.index.cache;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.InfinispanServerTestResource;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.query.AttributeFilter;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.TestUtils.getProcessInstance;
import static org.kie.kogito.index.model.ProcessInstanceState.ACTIVE;
import static org.kie.kogito.index.model.ProcessInstanceState.COMPLETED;
import static org.kie.kogito.index.query.QueryFilterFactory.*;

@QuarkusTest
@QuarkusTestResource(InfinispanServerTestResource.class)
public class QueryIT {

    @Inject
    CacheService cacheService;

    @BeforeEach
    public void setup() {
        cacheService.getProcessInstancesCache().clear();
    }

    @Test
    public void testProcessInstanceQueries() {
        String processId = "travels";
        String processInstanceId = UUID.randomUUID().toString();
        String subProcessId = processId + "_sub";
        String subProcessInstanceId = UUID.randomUUID().toString();
        ProcessInstance processInstance = getProcessInstance(processId, processInstanceId, ACTIVE.ordinal(), null, null);
        cacheService.getProcessInstancesCache().put(processInstanceId, processInstance);
        cacheService.getProcessInstancesCache().put(subProcessInstanceId, getProcessInstance(subProcessId, subProcessInstanceId, COMPLETED.ordinal(), processInstanceId, processId));

        queryAndAssert(in("state", asList(ACTIVE.ordinal(), COMPLETED.ordinal())), processInstanceId, subProcessInstanceId);
        queryAndAssert(equalTo("state", ACTIVE.ordinal()), processInstanceId);
        queryAndAssert(greaterThan("state", ACTIVE.ordinal()), subProcessInstanceId);
        queryAndAssert(greaterThanEqual("state", ACTIVE.ordinal()), processInstanceId, subProcessInstanceId);
        queryAndAssert(lessThan("state", COMPLETED.ordinal()), processInstanceId);
        queryAndAssert(lessThanEqual("state", COMPLETED.ordinal()), processInstanceId, subProcessInstanceId);
        queryAndAssert(between("state", ACTIVE.ordinal(), COMPLETED.ordinal()), processInstanceId, subProcessInstanceId);
        queryAndAssert(isNull("rootProcessInstanceId"), processInstanceId);
        queryAndAssert(notNull("rootProcessInstanceId"), subProcessInstanceId);
        queryAndAssert(in("id", asList(processInstanceId, subProcessInstanceId)), processInstanceId, subProcessInstanceId);
        queryAndAssert(equalTo("rootProcessInstanceId", processInstanceId), subProcessInstanceId);
        queryAndAssert(in("processId", asList(processId, subProcessId)), processInstanceId, subProcessInstanceId);
        queryAndAssert(equalTo("processId", subProcessId), subProcessInstanceId);
        queryAndAssert(contains("roles", "admin"), processInstanceId, subProcessInstanceId);
        queryAndAssert(containsAny("roles", asList("admin", "kogito")), processInstanceId, subProcessInstanceId);
        queryAndAssert(containsAll("roles", asList("admin", "kogito")));
        queryAndAssert(isNull("roles"));
        queryAndAssert(isNull("end"), processInstanceId);
        queryAndAssert(lessThan("start", new Date().getTime()), processInstanceId, subProcessInstanceId);
        queryAndAssert(lessThanEqual("start", new Date().getTime()), processInstanceId, subProcessInstanceId);
        queryAndAssert(greaterThan("start", new Date().getTime()));
        queryAndAssert(greaterThanEqual("start", new Date().getTime()));
        queryAndAssert(equalTo("start", processInstance.getStart().toInstant().toEpochMilli()), processInstanceId);
    }

    private void queryAndAssert(AttributeFilter filter, String... ids) {
        List<ProcessInstance> instances = cacheService.getProcessInstancesCache().query().filter(singletonList(filter)).execute();
        assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting("id").containsExactlyInAnyOrder(ids);
    }
}
