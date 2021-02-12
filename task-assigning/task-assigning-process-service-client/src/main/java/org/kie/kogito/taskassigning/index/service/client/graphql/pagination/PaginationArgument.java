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
package org.kie.kogito.taskassigning.index.service.client.graphql.pagination;

import com.fasterxml.jackson.databind.JsonNode;
import org.kie.kogito.taskassigning.index.service.client.graphql.Argument;

import static org.kie.kogito.taskassigning.util.JsonUtils.OBJECT_MAPPER;

public class PaginationArgument implements Argument {

    public static final String TYPE_ID = "Pagination";

    private int offset;
    private int limit;

    public PaginationArgument(int offset, int limit) {
        this.limit = limit;
        this.offset = offset;
    }

    @Override
    public JsonNode asJson() {
        return OBJECT_MAPPER.createObjectNode()
                .put("offset", getOffset())
                .put("limit", getLimit());
    }

    @Override
    public String getTypeId() {
        return TYPE_ID;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
