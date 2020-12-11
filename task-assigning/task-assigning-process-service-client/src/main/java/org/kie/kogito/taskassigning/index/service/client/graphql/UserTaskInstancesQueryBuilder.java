/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kie.kogito.taskassigning.index.service.client.graphql.pagination.PaginationArgument;

import static org.kie.kogito.taskassigning.index.service.client.graphql.ArgumentFactory.newPagination;

public class UserTaskInstancesQueryBuilder extends AbstractQueryBuilder<UserTaskInstancesQueryBuilder> {

    public static final String QUERY_NAME = "UserTaskInstances";
    private static final String WHERE_VARIABLE = "where";
    private static final String ORDER_BY_VARIABLE = "orderBy";
    private static final String PAGINATION_VARIABLE = "pagination";
    private static final String FIELD_DELIMITER = " ";

    private List<UserTaskInstance.Field> fields = new ArrayList<>();
    private UserTaskInstanceArgument whereArgument;
    private UserTaskInstanceOrderBy orderBy;
    private PaginationArgument paginationArgument;

    private UserTaskInstancesQueryBuilder() {
    }

    public static UserTaskInstancesQueryBuilder newBuilder() {
        return new UserTaskInstancesQueryBuilder();
    }

    @Override
    protected String getRequest() {
        return fields.stream().map(UserTaskInstance.Field::getName).collect(Collectors.joining(FIELD_DELIMITER));
    }

    public UserTaskInstancesQueryBuilder fields(UserTaskInstance.Field... fields) {
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }

    public UserTaskInstancesQueryBuilder where(UserTaskInstanceArgument.Field field, Argument argument) {
        if (whereArgument == null) {
            whereArgument = new UserTaskInstanceArgument();
        }
        whereArgument.add(field.getName(), argument);
        return this;
    }

    public UserTaskInstancesQueryBuilder orderBy(UserTaskInstanceOrderBy.Field field, OrderBy orderBy) {
        if (this.orderBy == null) {
            this.orderBy = new UserTaskInstanceOrderBy();
        }
        this.orderBy.add(field.getName(), orderBy);
        return this;
    }

    public UserTaskInstancesQueryBuilder pagination(int offset, int limit) {
        this.paginationArgument = newPagination(offset, limit);
        return this;
    }

    public String build() {
        return buildAsJson().toString();
    }

    @Override
    protected ObjectNode buildAsJson() {
        super.queryName(QUERY_NAME);
        if (whereArgument != null) {
            withVariable(WHERE_VARIABLE, whereArgument);
        }
        if (orderBy != null) {
            withVariable(ORDER_BY_VARIABLE, orderBy);
        }
        if (paginationArgument != null) {
            withVariable(PAGINATION_VARIABLE, paginationArgument);
        }
        return super.buildAsJson();
    }
}
