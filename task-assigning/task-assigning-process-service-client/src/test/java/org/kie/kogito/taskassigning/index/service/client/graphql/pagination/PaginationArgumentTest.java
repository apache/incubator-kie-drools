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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationArgumentTest {

    private static final int OFFSET = 1;
    private static final int LIMIT = 2;
    private PaginationArgument pagination = new PaginationArgument(OFFSET, LIMIT);

    @Test
    void asJson() {
        assertThat(pagination.asJson()).hasToString("{\"offset\":" + OFFSET + ",\"limit\":" + LIMIT + "}");
    }

    @Test
    void getTypeId() {
        assertThat(pagination.getTypeId()).isEqualTo(PaginationArgument.TYPE_ID);
    }

    @Test
    void getOffset() {
        assertThat(pagination.getOffset()).isEqualTo(OFFSET);
    }

    @Test
    void getLimit() {
        assertThat(pagination.getLimit()).isEqualTo(LIMIT);
    }
}
