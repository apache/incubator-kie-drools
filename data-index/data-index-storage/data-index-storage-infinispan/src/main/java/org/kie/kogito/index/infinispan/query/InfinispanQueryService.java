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

package org.kie.kogito.index.infinispan.query;

import java.io.StringReader;
import java.util.Collection;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.FilterConditionContextQueryBuilder;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryBuilder;
import org.infinispan.query.dsl.QueryFactory;
import org.kie.kogito.index.infinispan.cache.InfinispanCacheManager;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.query.ProcessInstanceFilter;
import org.kie.kogito.index.query.QueryService;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@ApplicationScoped
public class InfinispanQueryService implements QueryService {

    @Inject
    InfinispanCacheManager manager;

    @Override
    public Collection<JsonObject> queryDomain(String domain, String query) {
        if (query == null) {
            return manager.getDomainModelCache(domain).values();
        } else {
            QueryFactory qf = Search.getQueryFactory((RemoteCache) manager.getDomainModelCache(domain));
            Query q = qf.create(query);
            return q.<String>list().stream().map(json -> Json.createReader(new StringReader(json)).readObject()).collect(toList());
        }
    }

    @Override
    public Collection<JsonObject> queryProcessInstances(ProcessInstanceFilter filter) {
        QueryFactory qf = Search.getQueryFactory((RemoteCache) manager.getProcessInstancesCache());
        QueryBuilder qb = qf.from(ProcessInstance.class);
        if (filter != null) {
            FilterConditionContextQueryBuilder filterBuilder = null;
            if (filter.getState() != null && filter.getState().isEmpty() == false) {
                filterBuilder = qb.having("state").in(filter.getState());
            }
            if (filter.getProcessId() != null && filter.getProcessId().isEmpty() == false) {
                Set<String> processIds = filter.getProcessId().stream().map(String::toLowerCase).collect(toSet());
                if (filterBuilder == null) {
                    filterBuilder = qb.having("processId").in(processIds);
                } else {
                    filterBuilder.and().having("processId").in(processIds);
                }
            }
            if (filter.getId() != null && filter.getId().isEmpty() == false) {
                if (filterBuilder == null) {
                    qb.having("id").in(filter.getId());
                } else {
                    filterBuilder.and().having("id").in(filter.getId());
                }
            }
            if (filter.getLimit() != null) {
                qb.maxResults(filter.getLimit());
            }
            if (filter.getOffset() != null) {
                qb.startOffset(filter.getOffset());
            }
        }
        return qb.build().list();
    }
}
