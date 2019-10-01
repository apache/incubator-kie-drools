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

package org.kie.kogito.index.graphql;

import java.util.Collection;

import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.kie.kogito.index.query.QueryService;

public class DomainModelDataFetcher implements DataFetcher<Collection<ObjectNode>> {

    private QueryService queryService;
    private String processId;

    public DomainModelDataFetcher(QueryService queryService, String processId) {
        this.queryService = queryService;
        this.processId = processId;
    }

    @Override
    public Collection<ObjectNode> get(DataFetchingEnvironment env) {
        String query = env.getArgument("query");
        return queryService.queryDomain(processId, query);
    }
}
