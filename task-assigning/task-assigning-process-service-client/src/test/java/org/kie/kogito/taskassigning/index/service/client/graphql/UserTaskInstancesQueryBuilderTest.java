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
package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.util.Iterator;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringArgument;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.TestUtil.parseZonedDateTime;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.DESCRIPTION;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.NAME;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.STARTED;

class UserTaskInstancesQueryBuilderTest {

    private static final StringArgument NAME_ARGUMENT = ArgumentFactory.newStringEqual("nameValue");
    private static final StringArgument DESCRIPTION_ARGUMENT = ArgumentFactory.newStringEqual("descriptionValue");
    private static final DateArgument STARTED_ARGUMENT = ArgumentFactory.newDateEqual(parseZonedDateTime("2020-12-02T07:54:56.883Z"));
    private static final int OFFSET = 1;
    private static final int LIMIT = 2;

    private static final String QUERY_HEADER_WHERE = "query UserTaskInstances($where: UserTaskInstanceArgument){UserTaskInstances(where: $where)";
    private static final String QUERY_HEADER_WHERE_ORDER_BY = "query UserTaskInstances($where: UserTaskInstanceArgument, $orderBy: UserTaskInstanceOrderBy){UserTaskInstances(where: $where, orderBy: $orderBy)";
    private static final String QUERY_HEADER_WHERE_ORDER_BY_PAGINATION = "query UserTaskInstances($where: UserTaskInstanceArgument, $orderBy: UserTaskInstanceOrderBy, $pagination: Pagination){UserTaskInstances(where: $where, orderBy: $orderBy, pagination: $pagination)";

    private static final String QUERY_NODE_NAME = "query";
    private static final String WHERE_NODE_NAME = "where";
    private static final String ORDER_BY_NODE_NAME = "orderBy";
    private static final String VARIABLES_NODE_NAME = "variables";
    private static final String PAGINATION_NODE_NAME = "pagination";
    private static final String OFFSET_NODE_NAME = "offset";
    private static final String LIMIT_NODE_NAME = "limit";

    @Test
    void buildAsJsonWithWhere() {
        ObjectNode queryRoot = UserTaskInstancesQueryBuilder.newBuilder()
                .fields(UserTaskInstance.Field.NAME, UserTaskInstance.Field.DESCRIPTION, UserTaskInstance.Field.STARTED)
                .where(NAME, NAME_ARGUMENT)
                .where(DESCRIPTION, DESCRIPTION_ARGUMENT)
                .where(STARTED, STARTED_ARGUMENT)
                .buildAsJson();

        assertQueryHeader(queryRoot, QUERY_HEADER_WHERE);
        assertRequestedFields(queryRoot, NAME, DESCRIPTION, STARTED);

        assertVariable(queryRoot,
                       WHERE_NODE_NAME,
                       new NodeDef(NAME.getName(), NAME_ARGUMENT),
                       new NodeDef(DESCRIPTION.getName(), DESCRIPTION_ARGUMENT),
                       new NodeDef(STARTED.getName(), STARTED_ARGUMENT));
    }

    @Test
    void buildAsJsonWithWhereAndOrderBy() {
        ObjectNode queryRoot = UserTaskInstancesQueryBuilder.newBuilder()
                .fields(UserTaskInstance.Field.NAME, UserTaskInstance.Field.DESCRIPTION, UserTaskInstance.Field.STARTED)
                .where(NAME, NAME_ARGUMENT)
                .where(DESCRIPTION, DESCRIPTION_ARGUMENT)
                .where(STARTED, STARTED_ARGUMENT)
                .orderBy(UserTaskInstanceOrderBy.Field.STARTED, OrderBy.ASC)
                .buildAsJson();

        assertQueryHeader(queryRoot, QUERY_HEADER_WHERE_ORDER_BY);
        assertRequestedFields(queryRoot, NAME, DESCRIPTION, STARTED);

        assertVariable(queryRoot,
                       WHERE_NODE_NAME,
                       new NodeDef(NAME.getName(), NAME_ARGUMENT),
                       new NodeDef(DESCRIPTION.getName(), DESCRIPTION_ARGUMENT),
                       new NodeDef(STARTED.getName(), STARTED_ARGUMENT));

        assertVariable(queryRoot, ORDER_BY_NODE_NAME,
                       new NodeDef(STARTED.getName(), OrderBy.ASC));
    }

    @Test
    void buildAsJsonWithWhereAndOrderByAndPagination() {
        ObjectNode queryRoot = UserTaskInstancesQueryBuilder.newBuilder()
                .fields(UserTaskInstance.Field.NAME, UserTaskInstance.Field.DESCRIPTION, UserTaskInstance.Field.STARTED)
                .where(NAME, NAME_ARGUMENT)
                .where(DESCRIPTION, DESCRIPTION_ARGUMENT)
                .where(STARTED, STARTED_ARGUMENT)
                .orderBy(UserTaskInstanceOrderBy.Field.STARTED, OrderBy.ASC)
                .pagination(OFFSET, LIMIT)
                .buildAsJson();

        assertQueryHeader(queryRoot, QUERY_HEADER_WHERE_ORDER_BY_PAGINATION);
        assertRequestedFields(queryRoot, NAME, DESCRIPTION, STARTED);

        assertVariable(queryRoot,
                       WHERE_NODE_NAME,
                       new NodeDef(NAME.getName(), NAME_ARGUMENT),
                       new NodeDef(DESCRIPTION.getName(), DESCRIPTION_ARGUMENT),
                       new NodeDef(STARTED.getName(), STARTED_ARGUMENT));

        assertVariable(queryRoot, ORDER_BY_NODE_NAME,
                       new NodeDef(STARTED.getName(), OrderBy.ASC));

        assertVariable(queryRoot,
                       PAGINATION_NODE_NAME,
                       new NodeDef(OFFSET_NODE_NAME, IntNode.valueOf(OFFSET)),
                       new NodeDef(LIMIT_NODE_NAME, IntNode.valueOf(LIMIT)));
    }

    private void assertQueryHeader(ObjectNode queryRootNode, String queryHeader) {
        JsonNode queryNode = queryRootNode.get(QUERY_NODE_NAME);
        assertThat(queryNode).isNotNull();
        assertThat(queryNode.asText()).startsWith(queryHeader);
    }

    private void assertRequestedFields(ObjectNode queryRootNode, UserTaskInstanceArgument.Field... fields) {
        JsonNode queryNode = queryRootNode.get(QUERY_NODE_NAME);
        assertThat(queryNode).isNotNull();
        StringBuilder builder = new StringBuilder();
        Stream.of(fields).forEach(field -> {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(field.getName());
        });
        builder.insert(0, "{");
        builder.append("}");
        assertThat(queryNode.asText()).containsOnlyOnce(builder);
    }

    private void assertVariable(ObjectNode queryNode, String variableName, NodeDef... variableDef) {
        ObjectNode variablesNode = (ObjectNode) queryNode.get(VARIABLES_NODE_NAME);
        assertThat(variablesNode).isNotNull();
        ObjectNode variableNode = (ObjectNode) variablesNode.get(variableName);
        assertThat(variableNode).isNotNull();
        assertNodeDefs(variableNode, variableDef);
    }

    private static void assertNodeDefs(ObjectNode objectNode, NodeDef... defs) {
        assertThat(objectNode)
                .isNotNull()
                .hasSize(defs.length);

        Iterator<String> fieldNames = objectNode.fieldNames();
        int i = 0;
        String fieldName;
        JsonNode defNode;
        while (fieldNames.hasNext()) {
            fieldName = fieldNames.next();
            assertThat(fieldName).isEqualTo(defs[i].getName());
            defNode = objectNode.get(fieldName);
            assertThat(defNode).isEqualTo(defs[i].getJsonNode());
            i++;
        }
    }

    private static class NodeDef {

        private String name;
        private JsonNode jsonNode;

        public NodeDef(String name, Argument argument) {
            this.name = name;
            this.jsonNode = argument.asJson();
        }

        public NodeDef(String name, JsonNode jsonNode) {
            this.name = name;
            this.jsonNode = jsonNode;
        }

        public String getName() {
            return name;
        }

        public JsonNode getJsonNode() {
            return jsonNode;
        }
    }
}
