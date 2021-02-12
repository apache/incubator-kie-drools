/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.taskassigning.index.service.client.impl;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClient;
import org.kie.kogito.taskassigning.index.service.client.graphql.GraphQLServiceClient;
import org.kie.kogito.taskassigning.index.service.client.graphql.OrderBy;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceOrderBy;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstancesQueryBuilder;

import static org.kie.kogito.taskassigning.index.service.client.graphql.ArgumentFactory.newDateGreaterThan;
import static org.kie.kogito.taskassigning.index.service.client.graphql.ArgumentFactory.newStringIn;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.STARTED;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.STATE;

/**
 * DataIndexServiceClient implementation based on current GraphQLServiceClient implementation.
 * This implementation might be changed in favor of the data-index-client Quarkus implementation to be provided
 * by Kogito.
 */
public class DataIndexServiceClientImpl implements DataIndexServiceClient {

    private GraphQLServiceClient queryService;

    public DataIndexServiceClientImpl(GraphQLServiceClient queryService) {
        this.queryService = queryService;
    }

    @Override
    public List<UserTaskInstance> findTasks(List<String> stateIn, ZonedDateTime startedAfter, String orderBy, boolean asc, int offset, int limit) {
        UserTaskInstancesQueryBuilder queryBuilder = UserTaskInstancesQueryBuilder.newBuilder();
        queryBuilder.fields(UserTaskInstance.Field.values());
        if (stateIn != null) {
            queryBuilder.where(STATE, newStringIn(stateIn));
        }
        if (startedAfter != null) {
            queryBuilder.where(STARTED, newDateGreaterThan(startedAfter));
        }
        queryBuilder.pagination(offset, limit);
        if (orderBy != null && !orderBy.isEmpty()) {
            queryBuilder.orderBy(UserTaskInstanceOrderBy.Field.valueOf(orderBy), asc ? OrderBy.ASC : OrderBy.DESC);
        }
        String query = queryBuilder.build();
        UserTaskInstance[] userTaskInstances = queryService.executeQuery(UserTaskInstancesQueryBuilder.QUERY_NAME, query, UserTaskInstance[].class);
        return Arrays.asList(userTaskInstances);
    }

    @Override
    public void close() throws IOException {
        queryService.close();
    }
}

